package com.openclassrooms.hexagonal.games.data.service

import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User

interface UserApi {
    suspend fun checkEmail(email: String): Boolean
    suspend fun createAccount(newUser: NewUser): User?
    suspend fun signIn(email: String, password: String): User?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun signOut(): User?
    suspend fun deleteAccount(): User?
}