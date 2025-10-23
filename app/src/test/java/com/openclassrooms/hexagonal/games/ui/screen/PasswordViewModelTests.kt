package com.openclassrooms.hexagonal.games.ui.screen

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.localShared.utils.NoOpLogger
import com.oliviermarteaux.shared.test.assertFlagSwitching
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeUser
import com.openclassrooms.hexagonal.games.ui.screen.password.PasswordViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
@ExperimentalCoroutinesApi
class PasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val userRepository: UserRepository = mockk()
    private val log: Logger = NoOpLogger
    private val isOnlineFlow: Flow<Boolean> = flowOf(true)
    private lateinit var passwordViewModel: PasswordViewModel

    private val savedStateHandle = SavedStateHandle(mapOf("email" to fakeUser.email))

    @Before
    fun setUp() {
        every {userRepository.userAuthState} returns emptyFlow()
        passwordViewModel = PasswordViewModel(
            savedStateHandle = savedStateHandle,
            userRepository = userRepository,
            log = log,
            isOnlineFlow = isOnlineFlow
        )
    }

    //_ ------------------------------------------------------------------------
    // onPasswordChange
    // ------------------------------------------------------------------------
    @Test
    fun onPasswordChange_NewPassword_PasswordStateUpdated() = runTest {
        // Given
        val newPassword = "newPass123"
        // When
        passwordViewModel.onPasswordChange(newPassword)
        // Then
        assertEquals(newPassword, passwordViewModel.password)
    }

    //_ ------------------------------------------------------------------------
    // showIncorrectPasswordToast
    // ------------------------------------------------------------------------
    @Test
    fun showIncorrectPasswordToast_onCall_TogglesIncorrectPasswordFlag() = runTest {
        // Given
        assertFalse(passwordViewModel.incorrectPassword)
        // When
        passwordViewModel.showIncorrectPasswordToast()
        // Then
        assertFlagSwitching{passwordViewModel.incorrectPassword}
    }

    //_ ------------------------------------------------------------------------
    // signIn success
    // ------------------------------------------------------------------------
    @Test
    fun signIn_onSuccess_CallsOnSignIn() = runTest {
        // Given
        coEvery { userRepository.signIn(any(), any()) } returns Result.success(fakeUser)
        var onSignIn = false
        // When
        passwordViewModel.signIn("password"){onSignIn = true}
        // Then
        advanceUntilIdle()
        coVerify { userRepository.signIn(fakeUser.email, "password") }
        assertTrue(onSignIn)
    }

    //_ ------------------------------------------------------------------------
    // signIn invalid credentials
    // ------------------------------------------------------------------------
    @Test
    fun signIn_onInvalidCredentials_ShowsIncorrectPasswordToast() = runTest {
        // Given
        val mockException = mockk<FirebaseAuthInvalidCredentialsException>()
        coEvery {mockException.message} returns "ERROR_INVALID_CREDENTIALS"
        coEvery { userRepository.signIn(any(), any()) } returns Result.failure(mockException)
        // When
        passwordViewModel.signIn("wrongPass", onSignIn = {})
        // Then
        assertFlagSwitching{passwordViewModel.incorrectPassword}
    }

    //_ ------------------------------------------------------------------------
    // signIn empty credentials
    // ------------------------------------------------------------------------
    @Test
    fun signIn_emptyCredentials_ShowsIncorrectPasswordToast() = runTest {
        // Given
        coEvery { userRepository.signIn(any(), any()) } returns Result.failure(IllegalArgumentException("Invalid password"))
        // When
        passwordViewModel.signIn("wrongPass", onSignIn = {})
        // Then
        assertFlagSwitching{passwordViewModel.incorrectPassword}
    }

    //_ ------------------------------------------------------------------------
    // signIn unknown error
    // ------------------------------------------------------------------------
    @Test
    fun signIn_onUnknownError_CallsShowUnknownErrorToast() = runTest {
        // Given
        coEvery { userRepository.signIn(any(), any()) } returns Result.failure(Exception("Unknown error"))
        // When
        passwordViewModel.signIn("pass", onSignIn = {})
        // Then
        assertFlagSwitching{passwordViewModel.unknownError}
    }
}
