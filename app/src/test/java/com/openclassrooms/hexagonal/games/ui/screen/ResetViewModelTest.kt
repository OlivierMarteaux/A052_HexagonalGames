package com.openclassrooms.hexagonal.games.ui.screen

import androidx.lifecycle.SavedStateHandle
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.localShared.utils.NoOpLogger
import com.oliviermarteaux.shared.test.assertFlagSwitching
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeUser
import com.openclassrooms.hexagonal.games.ui.screen.reset.ResetViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResetViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // handles Dispatchers.Main for tests
    private val userRepository: UserRepository = mockk()
    private val log: Logger = NoOpLogger
    private val isOnlineFlow: Flow<Boolean> = flowOf(true)
    private val savedStateHandle = SavedStateHandle(mapOf("email" to fakeUser.email))
    private lateinit var resetViewModel: ResetViewModel

    @Before
    fun setUp() {
        every {userRepository.userAuthState} returns emptyFlow()
        resetViewModel = ResetViewModel(
            savedStateHandle = savedStateHandle,
            userRepository = userRepository,
            log = log,
            isOnlineFlow = isOnlineFlow
        )
    }

    //_ ------------------------------------------------------------------------
    // onEmailChange
    // ------------------------------------------------------------------------
    @Test
    fun onEmailChange_NewEmail_EmailUpdated() = runTest {
        // Given
        val newEmail = fakeUser.email
        // When
        resetViewModel.onEmailChange(newEmail)
        // Then
        assertEquals(newEmail, resetViewModel.email)
    }

    //_ ------------------------------------------------------------------------
    // sendPasswordResetEmail success
    // ------------------------------------------------------------------------
    @Test
    fun sendPasswordResetEmail_Success_AlertDialogShown() = runTest {
        // Given
        coEvery { userRepository.sendPasswordResetEmail(any()) } returns Result.success(Unit)
        // When
        resetViewModel.sendPasswordResetEmail(fakeUser.email)
        advanceUntilIdle()
        // Then
        assertTrue(resetViewModel.alertDialog)
    }

    //_ ------------------------------------------------------------------------
    // sendPasswordResetEmail failure
    // ------------------------------------------------------------------------
    @Test
    fun sendPasswordResetEmail_Failure_ShowsUnknownErrorToast() = runTest {
        // Given
        coEvery { userRepository.sendPasswordResetEmail(any()) } returns Result.failure(Exception("Network error"))
        // When
        resetViewModel.sendPasswordResetEmail(fakeUser.email)
        // Then
        assertFlagSwitching { resetViewModel.unknownError }
    }

    //_ ------------------------------------------------------------------------
    // initial state
    // ------------------------------------------------------------------------
    @Test
    fun resetViewModel_InitialEmail_SetCorrectly() = runTest {
        // Then
        assertEquals(fakeUser.email, resetViewModel.email)
        assertFalse(resetViewModel.alertDialog)
    }
}
