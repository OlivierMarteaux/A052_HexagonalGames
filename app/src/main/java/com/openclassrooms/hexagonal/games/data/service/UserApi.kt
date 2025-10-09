package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser

interface UserApi {
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
    fun deleteAccount()
    suspend fun createAccount(newUser: NewUser)
    suspend fun checkEmail(email: String): Boolean
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}