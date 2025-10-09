package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser

interface UserApi {
    suspend fun checkEmail(email: String): Boolean
    suspend fun createAccount(newUser: NewUser): FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun signOut(): FirebaseUser?
    suspend fun deleteAccount(): FirebaseUser?
}