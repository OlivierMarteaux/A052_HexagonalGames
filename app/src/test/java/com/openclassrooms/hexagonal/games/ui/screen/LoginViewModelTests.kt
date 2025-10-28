package com.openclassrooms.hexagonal.games.ui.screen

import com.oliviermarteaux.shared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.shared.utils.Logger
import com.oliviermarteaux.shared.utils.NoOpLogger
import com.oliviermarteaux.shared.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeNewUser
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeUser
import com.openclassrooms.hexagonal.games.ui.screen.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var loginViewModel: LoginViewModel
    private val userRepository: UserRepository = mockk()
    private val log: Logger = NoOpLogger
    private val isOnlineFlow = MutableStateFlow(true)

    val dispatchers = CoroutineDispatcherProvider(
        io = mainDispatcherRule.testDispatcher,
        main = mainDispatcherRule.testDispatcher
    )

    @Before
    fun setUp() {
        every {userRepository.userAuthState} returns emptyFlow()
        loginViewModel = LoginViewModel(
            userRepository = userRepository,
            log = log,
            isOnlineFlow = isOnlineFlow,
            dispatchers = dispatchers
        )
    }

    //_ onChange functions -----------------------------------------------
    @Test
    fun onEmailChange_WhenNewEmailProvided_UpdatesNewUserEmail() {
        // Given
        val newEmail = "new@email.com"
        // When
        loginViewModel.onEmailChange(newEmail)
        // Then
        val expected = newEmail
        val actual = loginViewModel.newUser.email
        assertEquals(expected,actual)
    }

    @Test
    fun onFirstNameChange_WhenNewFirstNameProvided_UpdatesNewUserFirstName() {
        // Given
        val newFirstName = "Alice"
        // When
        loginViewModel.onFirstNameChange(newFirstName)
        // Then
        val expected = newFirstName
        val actual = loginViewModel.newUser.firstname
        assertEquals(expected,actual)
    }

    @Test
    fun onLastNameChange_WhenNewLastNameProvided_UpdatesNewUserLastName() {
        // Given
        val newLastName = "Smith"
        // When
        loginViewModel.onLastNameChange(newLastName)
        // Then
        val expected = newLastName
        val actual = loginViewModel.newUser.lastname
        assertEquals(expected,actual)
    }

    @Test
    fun onPasswordChange_WhenNewPasswordProvided_UpdatesNewUserPassword() {
        // Given
        val newPassword = "supersecret"
        // When
        loginViewModel.onPasswordChange(newPassword)
        // Then
        val expected = newPassword
        val actual = loginViewModel.newUser.password
        assertEquals(expected,actual)
    }

    //_ checkEmail -------------------------------------------------------
    @Test
    fun checkEmail_onSuccess_UpdatesEmailExistTrue() = runTest {
        // Given
        coEvery { userRepository.checkEmail(any()) } returns Result.success(true)
        // When
        loginViewModel.checkEmail(fakeUser.email)
        advanceUntilIdle()
        // Then
        assertTrue(loginViewModel.emailExist?:false)
//        advanceUntilIdle() // <- waits for all launched coroutines to finish
    }

    @Test
    fun checkEmail_onFailure_CallsShowUnknownErrorToastAndEmailExistNull() = runTest {
        // Given
        coEvery { userRepository.checkEmail(any()) } returns Result.failure(Exception("Network error"))
        // When
        loginViewModel.checkEmail(fakeUser.email)
        // Then
        advanceTimeBy(50)
        assertNull(loginViewModel.emailExist)
        assertTrue(loginViewModel.unknownError)
        advanceUntilIdle() // <- waits for all launched coroutines to finish
        assertFalse(loginViewModel.unknownError)
    }

    //_ createAccount ----------------------------------------------------
    @Test
    fun createAccount_onSuccess_CallsOnAccountCreated() = runTest {
        // Given
        coEvery { userRepository.createAccount(any()) } returns Result.success(fakeUser)
        var onAccountCreated = false
        // When
        loginViewModel.createAccount(fakeNewUser){onAccountCreated = true}
        advanceUntilIdle()
        // Then
        assertTrue(onAccountCreated)
//        advanceUntilIdle() // <- waits for all launched coroutines to finish
    }

    @Test
    fun createAccount_onFailure_ShowsAccountCreationErrorToast() = runTest {
        // Given
        coEvery { userRepository.createAccount(any()) } returns Result.failure(Exception("Server error"))
        // When
        loginViewModel.createAccount(fakeNewUser) {}
        // Then
        advanceTimeBy(50)
        assertTrue(loginViewModel.accountCreationError)
        advanceUntilIdle() // <- waits for all launched coroutines to finish
        assertFalse(loginViewModel.accountCreationError)
    }

    //_ showAccountCreationErrorToast ------------------------------------
    @Test
    fun showAccountCreationErrorToast_onCall_SetsErrorTrueThenFalse() = runTest {
        // Given
        val toastDuration = TOAST_DURATION
        // When
        loginViewModel.showAccountCreationErrorToast()
        advanceTimeBy(50)
        // Then (during toast)
        assertTrue(loginViewModel.accountCreationError)
        // When (after toast delay)
        advanceUntilIdle()
        // Then (toast gone)
        assertFalse(loginViewModel.accountCreationError)
//        advanceUntilIdle() // <- waits for all launched coroutines to finish
    }
}
