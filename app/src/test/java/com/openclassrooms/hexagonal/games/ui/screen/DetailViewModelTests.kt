package com.openclassrooms.hexagonal.games.ui.screen

import androidx.lifecycle.SavedStateHandle
import com.oliviermarteaux.shared.utils.Logger
import com.oliviermarteaux.shared.utils.NoOpLogger
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakePost
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakePosts
import com.openclassrooms.hexagonal.games.ui.screen.detail.DetailViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val log: Logger = NoOpLogger
    private val isOnlineFlow: Flow<Boolean> = flowOf(true)
    private lateinit var postRepository: PostRepository
    private lateinit var userRepository: UserRepository
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private val fakePostId = fakePost.id

    // Helper to expose post flow
    private val postFlow = MutableSharedFlow<Result<List<Post>>>()

    @Before
    fun setup() {
        postRepository = mockk()
        userRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf("post_id" to fakePostId))
        every { userRepository.userAuthState } returns emptyFlow()
        every { postRepository.posts } returns postFlow
        detailViewModel = DetailViewModel(
            savedStateHandle = savedStateHandle,
            postRepository = postRepository,
            userRepository = userRepository,
            isOnlineFlow = isOnlineFlow,
            log = log
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getPost_onSuccess_returnTargetPost() = runTest {
        // Given
        val expectedPost = fakePost
        // When
        postFlow.emit(Result.success(fakePosts))
        advanceUntilIdle()
        // Then
        val actualPost = detailViewModel.post
        assertEquals(expectedPost, actualPost)
    }
}