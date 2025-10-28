package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * A Firebase implementation of the [UserApi] interface.
 */
class UserFirebaseApi: UserApi {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user: FirebaseUser? = firebaseAuth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * A flow that emits the current authentication state of the user.
     * Emits a [FirebaseUser] if a user is signed in, or `null` otherwise.
     */
    override val userAuthState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    /**
     * Checks if an email address is already registered.
     *
     * @param email The email address to check.
     * @return A [Result] indicating whether the email exists. `Result.success(true)` if it exists, `Result.success(false)` otherwise.
     */
    override suspend fun checkEmail(email: String) = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        var emailExist: Boolean
        val snapshot = firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
        emailExist = !snapshot.isEmpty
        Log.d("OM_TAG", "UserFirebaseApi: checkEmail: emailExist =  $emailExist")
        emailExist
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: checkEmail: exception: ${e.message}")
    }

    /**
     * Creates a new user account.
     *
     * @param newUser The details of the new user.
     * @return A [Result] containing the created [User] on success, or an error.
     */
    override suspend fun createAccount(newUser: NewUser) : Result<User?> = runCatching {
//            throw IllegalStateException("Forced exception for testing")
            Log.d("OM_TAG", "UserFirebaseApi: CreateAccount: newUser = $newUser")
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(newUser.email, newUser.password)
                .await() // ✅ this suspends until Firebase finishes

            // Do follow-up work AFTER user is created :
            val firebaseUser = authResult.user
            firebaseUser?.let { uid ->
                // 1) Update FirebaseUser profile (displayName)
                updateFirebaseUserProfile(newUser, firebaseUser)
                // 2) Add new user to Firestore
                addNewUserToFirestore(newUser, firebaseUser.uid)
            }
            firebaseUser?.toUser()
    }.onFailure{ e->
        Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: exception: ${e.message}")
    }

    /**
     * Adds a new user to the Firestore database.
     *
     * @param newUser The new user's data.
     * @param uid The user's unique ID.
     */
    private suspend fun addNewUserToFirestore(newUser: NewUser, uid: String) =
        try {
            val userData = mapOf(
                "id" to uid,
                "firstname" to newUser.firstname,
                "lastname" to newUser.lastname,
                "email" to newUser.email
            )
            firestore.collection("users").document(uid)
                .set(userData).await()
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: addNewUserToFirestore exception: ${e.message}")
        }

    /**
     * Updates the user's profile in Firebase Authentication.
     *
     * @param newUser The new user's data.
     * @param firebaseUser The Firebase user to update.
     */
    private suspend fun updateFirebaseUserProfile(newUser: NewUser, firebaseUser: FirebaseUser) =
        try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(with(newUser) { "$firstname $lastname" })
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: updateFirebaseUserProfile exception: ${e.message}")
        }

    /**
     * Signs in a user with their email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] containing the signed-in [User] on success, or an error.
     */
    override suspend fun signIn(email: String, password: String): Result<User?> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val token = task.result
            firestore.collection("users").document(firebaseUser?.uid ?:"").update("fcmToken", token)
        }
        Log.d("OM_TAG", "UserFirebaseApi:signIn: success")
        firebaseUser?.toUser()
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi:signIn: exception: ${e.message}")
    }

    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email The email address to send the reset link to.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        firebaseAuth.sendPasswordResetEmail(email).await()
        Log.d("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
        Unit
    }.onFailure { e ->
        Log.e("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset failed: ${e.message}")
    }

    /**
     * Signs out the current user.
     *
     * @return A [Result] containing the signed-out [User] on success, or an error.
     */
    override fun signOut() : Result<User?> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        Log.d("OM_TAG", "UserFirebaseApi: signOut(): Signing out")
        firebaseAuth.signOut()
        null
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: signOut(): Failed to sign out: ${e.message}")
    }

    /**
     * Deletes the current user's account.
     *
     * @return A [Result] containing the deleted [User] on success, or an error.
     */
    override suspend fun deleteAccount(): Result<User?> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        deleteFireStoreUserEntry()
        deleteAuthUser()
        signOut()
        null
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: deleteAccount(): Failed to delete account: ${e.message}")
    }

    /**
     * Deletes the authenticated user from Firebase Authentication.
     */
    private suspend fun deleteAuthUser() = runCatching {
        Log.d("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Deleting auth user")
        user?.delete()?.await()
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Failed to delete auth user: ${e.message}")
    }

    /**
     * Deletes the user's entry from the Firestore "users" collection.
     */
    private suspend fun deleteFireStoreUserEntry() = runCatching {
        val userUid = user?.uid
        userUid?.let {
            firestore.collection("users").document(userUid)
                .delete().await()
        }
        Log.d("OM_TAG", "UserFirebaseApi: deleteFireStoreUserEntry(): userUid = $userUid")
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: deleteFireStoreUserEntry(): Failed to delete user: ${e.message}")
    }
}
