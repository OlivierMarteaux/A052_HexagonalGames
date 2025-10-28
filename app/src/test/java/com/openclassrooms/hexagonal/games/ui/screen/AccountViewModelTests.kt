package com.openclassrooms.hexagonal.games.ui.screen

import com.oliviermarteaux.shared.utils.Logger
import com.oliviermarteaux.shared.utils.NoOpLogger
import com.oliviermarteaux.shared.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeUser
import com.openclassrooms.hexagonal.games.ui.screen.account.AccountViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var accountViewModel: AccountViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var fakeOnlineFlow: Flow<Boolean>
    private val log: Logger = NoOpLogger // <-- use NoOpLogger

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        userRepository = mockk()
        fakeOnlineFlow = flowOf(true)
        every { userRepository.userAuthState } returns flowOf(null)
        accountViewModel = AccountViewModel(
            userRepository = userRepository,
            log = log,
            isOnlineFlow = fakeOnlineFlow
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAccount_onSuccess_triggerOnDeleteAction() = runTest {
        // Given
        coEvery { userRepository.deleteAccount() } returns Result.success(fakeUser)
        var onDeleteAccountCalled  = false
        // When
        accountViewModel.deleteAccount {onDeleteAccountCalled = true}
        // Then
        advanceUntilIdle()
        coVerify(exactly = 1) { userRepository.deleteAccount() }
        assertTrue(onDeleteAccountCalled)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAccount_onFailure_ShowUnknownErrorToast() = runTest {
        // Given
        coEvery { userRepository.deleteAccount() } returns Result.failure(Exception("fail"))
        // When
        accountViewModel.deleteAccount {}
        // Then
        // advance until unknownError=true is set
        advanceTimeBy(50)
        assertTrue(accountViewModel.unknownError)
        // advance until reset to unknownError=false
        advanceTimeBy(TOAST_DURATION)
        assertFalse(accountViewModel.unknownError)
    }
}