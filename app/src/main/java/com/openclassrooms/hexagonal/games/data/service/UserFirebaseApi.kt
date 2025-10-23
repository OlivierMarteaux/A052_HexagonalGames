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

class UserFirebaseApi: UserApi {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user: FirebaseUser? = firebaseAuth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override val userAuthState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

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

    private suspend fun updateFirebaseUserProfile(newUser: NewUser, firebaseUser: FirebaseUser) =
        try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(with(newUser) { "$firstname $lastname" })
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: updateFirebaseUserProfile exception: ${e.message}")
        }

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

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        firebaseAuth.sendPasswordResetEmail(email).await()
        Log.d("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
        Unit
    }.onFailure { e ->
        Log.e("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset failed: ${e.message}")
    }

    override fun signOut() : Result<User?> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        Log.d("OM_TAG", "UserFirebaseApi: signOut(): Signing out")
        firebaseAuth.signOut()
        null
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: signOut(): Failed to sign out: ${e.message}")
    }

    override suspend fun deleteAccount(): Result<User?> = runCatching {
//        throw IllegalStateException("Forced exception for testing")
        deleteFireStoreUserEntry()
        deleteAuthUser()
        signOut()
        null
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: deleteAccount(): Failed to delete account: ${e.message}")
    }

    private suspend fun deleteAuthUser() = runCatching {
        Log.d("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Deleting auth user")
        user?.delete()?.await()
    }.onFailure { e ->
        Log.e("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Failed to delete auth user: ${e.message}")
    }

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