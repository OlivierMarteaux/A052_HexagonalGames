package com.openclassrooms.hexagonal.games.ui.screen

import androidx.lifecycle.SavedStateHandle
import com.oliviermarteaux.localShared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.localShared.utils.NoOpLogger
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakePosts
import com.openclassrooms.hexagonal.games.ui.screen.comment.CommentViewModel
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommentViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Dependencies
    private lateinit var postRepository: PostRepository
    private lateinit var userRepository: UserRepository
    private val log: Logger = NoOpLogger
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var isOnlineFlow: Flow<Boolean>
    // ViewModel under test
    private lateinit var commentViewModel: CommentViewModel

    private val fakePostId = FakeDataFactory.fakePost.id

    @Before
    fun setup() {
        postRepository = mockk()
        userRepository = mockk()
        isOnlineFlow = flowOf(true)
        savedStateHandle = SavedStateHandle(mapOf("post_id" to fakePostId))

        val dispatchers = CoroutineDispatcherProvider(
            io = mainDispatcherRule.testDispatcher,
            main = mainDispatcherRule.testDispatcher
        )

        // default for currentUser
        every { userRepository.userAuthState } returns flowOf(null)
        coEvery {postRepository.posts} returns flowOf(Result.success(fakePosts))

        commentViewModel = CommentViewModel(
            savedStateHandle = savedStateHandle,
            postRepository = postRepository,
            userRepository = userRepository,
            log = log,
            isOnlineFlow = isOnlineFlow,
            dispatchers = dispatchers
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    //_ ------------------------------------------------------
    // TESTS
    // ------------------------------------------------------

    @Test
    fun onCommentChange_UpdatesCommentContent() = runTest {
        commentViewModel.onCommentChange("Hello World")
        assertEquals("Hello World", commentViewModel.commentContent)
    }

    @Test
    fun addComment_onSuccess_CallsOnResult() = runTest {
        // Given
        coEvery { postRepository.addComment(any(), any()) } returns Result.success(Unit)
        var callbackCalled = false
        commentViewModel.onCommentChange("Test comment")
        // When
        commentViewModel.addComment { callbackCalled = true }
        advanceUntilIdle()
        // Then
        assertTrue(callbackCalled)
        assertFalse(commentViewModel.unknownError)
    }

    @Test
    fun addComment_onFailure_ShowsUnknownErrorToast() = runTest {
        // Given
        coEvery { postRepository.addComment(any(), any()) } returns Result.failure(Exception("fail"))
        commentViewModel.onCommentChange("Test comment")
        // When
        commentViewModel.addComment()
        advanceTimeBy(50)
        // Then
        assertTrue(commentViewModel.unknownError)
        advanceUntilIdle()
        assertFalse(commentViewModel.unknownError)
    }
}