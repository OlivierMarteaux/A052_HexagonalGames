package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeComment
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakePost
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakePosts
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PostRepositoryTest {

    private lateinit var postApi: PostApi
    private lateinit var repository: PostRepository

    @Before
    fun setUp() {
        postApi = mockk()
        // Stub BEFORE repository creation
        coEvery { postApi.getPostsOrderByCreationDateDesc() } returns flowOf(Result.success(fakePosts))
        repository = PostRepository(postApi)
    }

    @Test
    fun posts_WhenCalled_DelegatesToApi() = runTest {
        // When
        repository.posts.collect{}
        // Then
        coVerify(exactly = 1) { postApi.getPostsOrderByCreationDateDesc() }
    }

    @Test
    fun addPost_WhenCalled_DelegatesToApi() = runTest {
        // Given
        val expectedResult = Result.success(Unit)
        coEvery { postApi.addPost(fakePost) } returns expectedResult
        // When
        repository.addPost(fakePost)
        // Then
        coVerify(exactly = 1) { postApi.addPost(fakePost) }
    }

    @Test
    fun addComment_WhenCalled_DelegatesToApi() = runTest {
        // Given
        val postId = fakePost.id
        val comment = fakeComment
        val expectedResult = Result.success(Unit)
        coEvery { postApi.addComment(postId, comment) } returns expectedResult
        // When
        repository.addComment(postId, comment)
        // Then
        coVerify(exactly = 1) { postApi.addComment(postId, comment) }
    }
}

