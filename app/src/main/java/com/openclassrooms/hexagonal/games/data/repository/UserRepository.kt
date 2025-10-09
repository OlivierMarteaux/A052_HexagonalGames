package com.openclassrooms.hexagonal.games.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi) {
    var currentUser: User? by mutableStateOf(null)
        private set
    suspend fun checkEmail(email: String) = userApi.checkEmail(email)
    suspend fun createAccount(newUser: NewUser) {
        currentUser = userApi.createAccount(newUser)?.toUser()
    }
    suspend fun signIn(email: String, password: String) {
        currentUser = userApi.signIn(email, password)?.toUser()
    }
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        userApi.sendPasswordResetEmail(email)
    fun signOut() { currentUser = userApi.signOut()?.toUser() }
    suspend fun deleteAccount() { currentUser = userApi.deleteAccount()?.toUser() }
}