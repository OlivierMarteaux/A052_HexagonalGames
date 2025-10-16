package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi) {
//    var currentUser: User? by mutableStateOf(null)
//        private set

    val userAuthState: Flow<FirebaseUser?> = userApi.userAuthState

    private var _currentUser by mutableStateOf<User?>(null)
    var currentUser: User?
        get() {
            Log.i("OM_TAG", "UserRepository: get currentUser with value = $_currentUser")
            return _currentUser
        }
        private set(value) {
            _currentUser = value
            Log.i("OM_TAG", "UserRepository: set currentUser to value = $_currentUser")
        }
    suspend fun checkEmail(email: String): Result<Boolean> = userApi.checkEmail(email)
    suspend fun createAccount(newUser: NewUser) {
        currentUser = userApi.createAccount(newUser)
    }
    suspend fun signIn(email: String, password: String): Result<Unit> {
        currentUser = userApi.signIn(email, password)
        return currentUser?.let{ Result.success(Unit) }?:Result.failure(Exception("Sign in failed"))
    }
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        userApi.sendPasswordResetEmail(email)
    fun signOut() { currentUser = userApi.signOut() }
    suspend fun deleteAccount() { currentUser = userApi.deleteAccount() }
}