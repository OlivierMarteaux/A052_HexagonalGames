package com.openclassrooms.hexagonal.games.ui.screen

import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.localShared.utils.NoOpLogger
import com.oliviermarteaux.shared.ui.UiState
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakePost
import com.openclassrooms.hexagonal.games.ui.screen.homefeed.HomeFeedViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeFeedViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var homeFeedViewModel: HomeFeedViewModel
    private lateinit var postRepository: PostRepository
    private lateinit var userRepository: UserRepository
    private lateinit var fakeOnlineFlow: Flow<Boolean>
    private val log: Logger = NoOpLogger

    @Before
    fun setup() {
        postRepository = mockk()
        userRepository = mockk()
        fakeOnlineFlow = flowOf(true)

        every { userRepository.userAuthState } returns flowOf(null)
        coEvery { postRepository.posts } returns flowOf(Result.success(emptyList()))

        homeFeedViewModel = HomeFeedViewModel(
            postRepository = postRepository,
            userRepository = userRepository,
            log = log,
            isOnlineFlow = fakeOnlineFlow
        )
    }

    @Test
    fun loadPosts_WhenRepositoryReturnsPosts_SetsUiStateSuccess() = runTest {
        // Given
        coEvery { postRepository.posts } returns flowOf(Result.success(listOf(fakePost)))
        // When
        homeFeedViewModel.loadPosts()
        advanceUntilIdle() // make sure all coroutines finish
        // Then
        assertTrue(homeFeedViewModel.homeFeedUiState is UiState.Success<Post>)
        val posts: List<Post> = (homeFeedViewModel.homeFeedUiState as UiState.Success<Post>).data
        assertEquals(1, posts.size)
        assertEquals(fakePost.id, posts[0].id)
    }

    @Test
    fun loadPosts_WhenRepositoryReturnsEmpty_SetsUiStateEmpty() = runTest {
        // Given
        coEvery { postRepository.posts } returns flowOf(Result.success(emptyList()))
        // When
        homeFeedViewModel.loadPosts()
        advanceUntilIdle()
        // Then
        assertTrue(homeFeedViewModel.homeFeedUiState is UiState.Empty)
    }

    @Test
    fun loadPosts_WhenRepositoryFails_SetsUiStateError() = runTest {
        // Given
        val exception = Throwable("Network error")
        coEvery { postRepository.posts } returns flowOf(Result.failure(exception))
        // When
        homeFeedViewModel.loadPosts()
        advanceUntilIdle()
        // Then
        assertTrue(homeFeedViewModel.homeFeedUiState is UiState.Error)
        val error = (homeFeedViewModel.homeFeedUiState as UiState.Error).throwable
        assertEquals(exception, error)
    }
}