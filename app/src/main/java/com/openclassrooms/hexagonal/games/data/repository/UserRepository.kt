package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userApi: UserApi) {

    val userAuthState: Flow<FirebaseUser?> = userApi.userAuthState
    suspend fun checkEmail(email: String): Result<Boolean> = userApi.checkEmail(email)
    suspend fun createAccount(newUser: NewUser): Result<User?> = userApi.createAccount(newUser)
    suspend fun signIn(email: String, password: String): Result<User?> =
        userApi.signIn(email, password)
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        userApi.sendPasswordResetEmail(email)
    fun signOut(): Result<User?> = userApi.signOut()
    suspend fun deleteAccount(): Result<User?> = userApi.deleteAccount()
}