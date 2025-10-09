package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser

interface UserApi {
    fun getCurrentUser(): FirebaseUser?
    fun signOut(): FirebaseUser?
    suspend fun deleteAccount(): FirebaseUser?
    suspend fun createAccount(newUser: NewUser): FirebaseUser?
    suspend fun checkEmail(email: String): Boolean
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}