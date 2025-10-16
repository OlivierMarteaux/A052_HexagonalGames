package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserApi {
    val userAuthState: Flow<FirebaseUser?>
    suspend fun checkEmail(email: String): Result<Boolean>
    suspend fun createAccount(newUser: NewUser): Result<User?>
    suspend fun signIn(email: String, password: String): User?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun signOut(): User?
    suspend fun deleteAccount(): User?
}