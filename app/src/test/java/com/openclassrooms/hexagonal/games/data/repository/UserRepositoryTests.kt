package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeNewUser
import com.openclassrooms.hexagonal.games.fake.FakeDataFactory.fakeUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserRepositoryTests {

    private lateinit var userApi: UserApi
    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        userApi = mockk()
        coEvery { userApi.userAuthState } returns mockk()
        userRepository = UserRepository(userApi)
    }

    @Test
    fun checkEmail_whenCalled_DelegatesToApi() = runTest {
        // Given
        coEvery { userApi.checkEmail(any()) } returns Result.success(true)
        // When
        userRepository.checkEmail(fakeUser.email)
        // Then
        coVerify { userApi.checkEmail(fakeUser.email) }
    }

    @Test
    fun createAccount_whenCalled_DelegatesToApi() = runTest {
        // Given
        coEvery { userApi.createAccount(any()) } returns Result.success(fakeUser)
        // When
        userRepository.createAccount(fakeNewUser)
        // Then
        coVerify { userApi.createAccount(fakeNewUser) }
    }

    @Test
    fun signIn_whenCalled_DelegatesToApi() = runTest {
        val email = "james.buchanan@examplepetstore.com"
        val password = "password"
        // Given
        coEvery { userApi.signIn(any(), any()) } returns Result.success(fakeUser)
        // When
        userRepository.signIn(email, password)
        // Then
        coVerify { userApi.signIn(email, password) }
    }

    @Test
    fun sendPasswordResetEmail_whenCalled_DelegatesToApi() = runTest {
        // Given
        coEvery { userApi.sendPasswordResetEmail(any()) } returns Result.success(Unit)
        // When
        userRepository.sendPasswordResetEmail(fakeUser.email)
        // Then
        coVerify { userApi.sendPasswordResetEmail(fakeUser.email) }
    }

    @Test
    fun signOut_whenCalled_DelegatesToApi() = runTest {
        // Given
        coEvery { userApi.signOut() } returns Result.success(fakeUser)
        // When
        userRepository.signOut()
        // Then
        coVerify { userApi.signOut() }
    }

    @Test
    fun deleteAccount_whenCalled_DelegatesToApi() = runTest {
        // Given
        coEvery { userApi.deleteAccount() } returns Result.success(fakeUser)
        // When
        userRepository.deleteAccount()
        // Then
        coVerify { userApi.deleteAccount() }
    }
}