package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.auth.FirebaseUser

interface UserApi {
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
    fun deleteAccount()
}