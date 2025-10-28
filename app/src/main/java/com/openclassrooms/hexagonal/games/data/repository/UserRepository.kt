package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.data.service.UserApi
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user data.
 *
 * @property userApi The API for interacting with user data.
 */
@Singleton
class UserRepository @Inject constructor(private val userApi: UserApi) {

    /**
     * A flow that emits the current authentication state of the user.
     * Emits a [FirebaseUser] if a user is signed in, or `null` otherwise.
     */
    val userAuthState: Flow<FirebaseUser?> = userApi.userAuthState
    /**
     * Checks if an email address is already registered.
     *
     * @param email The email address to check.
     * @return A [Result] indicating whether the email exists. `Result.success(true)` if it exists, `Result.success(false)` otherwise.
     */
    suspend fun checkEmail(email: String): Result<Boolean> = userApi.checkEmail(email)
    /**
     * Creates a new user account.
     *
     * @param newUser The details of the new user.
     * @return A [Result] containing the created [User] on success, or an error.
     */
    suspend fun createAccount(newUser: NewUser): Result<User?> = userApi.createAccount(newUser)
    /**
     * Signs in a user with their email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] containing the signed-in [User] on success, or an error.
     */
    suspend fun signIn(email: String, password: String): Result<User?> =
        userApi.signIn(email, password)
    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email The email address to send the reset link to.
     * @return A [Result] indicating success or failure.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        userApi.sendPasswordResetEmail(email)
    /**
     * Signs out the current user.
     *
     * @return A [Result] containing the signed-out [User] on success, or an error.
     */
    fun signOut(): Result<User?> = userApi.signOut()
    /**
     * Deletes the current user's account.
     *
     * @return A [Result] containing the deleted [User] on success, or an error.
     */
    suspend fun deleteAccount(): Result<User?> = userApi.deleteAccount()
}