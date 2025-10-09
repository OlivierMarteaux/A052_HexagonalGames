package com.openclassrooms.hexagonal.games.data.service

import android.R.attr.password
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserFirebaseApi: UserApi {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user: FirebaseUser? = firebaseAuth.currentUser
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun getCurrentUser(): FirebaseUser? {
        try {
            Log.d("OM_TAG", "UserFirebaseApi: getCurrentUser(): User = $user")
            return user
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: getCurrentUser(): Failed to get current user", e)
            throw e
        }
    }

    override fun signOut() {
        try {
            Log.d("OM_TAG", "UserFirebaseApi: signOut(): Signing out")
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: signOut(): Failed to sign out", e)
            throw e
        }
    }

    override fun deleteAccount() {
        try {
            Log.d("OM_TAG", "UserFirebaseApi: deleteFireStoreUserEntry(): Deleting FireStore User Entry")
            deleteFireStoreUserEntry()
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: deleteFireStoreUserEntry(): Failed to delete FireStore User Entry", e)
            throw e
        }
        try {
            deleteAuthUser()
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Failed to delete Auth user", e)
            throw e
        }
    }

    private fun deleteAuthUser() {
        user!!.delete()
            .addOnSuccessListener {
                Log.d("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Auth user deleted")
                signOut()
            }
            .addOnFailureListener { e ->
                Log.e("OM_TAG", "UserFirebaseApi: deleteAuthUser(): Failed to delete Auth user", e)
            }
    }

    private fun deleteFireStoreUserEntry() {
        val userUid = user!!.uid
        firestore.collection("users").document(userUid)
            .delete()
            .addOnSuccessListener {
                Log.d("OM_TAG", "UserFirebaseApi: deleteFireStoreUserEntry(): Firestore user $userUid deleted")
            }
            .addOnFailureListener { e ->
                Log.e("OM_TAG", "UserFirebaseApi: deleteFireStoreUserEntry(): Failed to delete Firestore user", e)
        }
    }

    override suspend fun createAccount(newUser: NewUser) {
        try {
            with(newUser) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        user?.uid?.let{ uid ->
                            // 1) Update FirebaseUser profile (displayName)
                            updateFirebaseUserProfile(newUser)
                            // 2) Add new user to Firestore
                            addNewUserToFirestore(newUser, uid)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: createAccount failed", e)
                    }
                    .await()
            }
        } catch (e: Exception) {
            Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: createAccount exception", e)
        }
    }

    private fun addNewUserToFirestore(newUser: NewUser, uid: String) {
        val userData = mapOf(
            "id" to uid,
            "firstname" to newUser.firstname,
            "lastname" to newUser.lastname,
            "email" to newUser.email
        )
        firestore.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("OM_TAG", "UserFirebaseApi: addNewUserToFirestore: Firestore: User profile created for $uid")
            }
            .addOnFailureListener { e ->
                Log.e("OM_TAG", "UserFirebaseApi: addNewUserToFirestore: Firestore: Failed to create user profile", e)
            }
    }

    private fun updateFirebaseUserProfile(newUser: NewUser) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(with(newUser) { "$firstname $lastname" })
            .build()
        user?.let{
            it.updateProfile(profileUpdates)
                .addOnSuccessListener {
                    Log.d("OM_TAG", "UserFirebaseApi: CreateAccount: Firestore: FirebaseUser profile updated with displayName")
                }
                .addOnFailureListener { e ->
                    Log.e("OM_TAG", "UserFirebaseApi: CreateAccount: Firestore: Failed to update FirebaseUser profile", e)
                }
        }
    }

    override suspend fun checkEmail(email: String): Boolean {
        var emailExist = false
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                emailExist = !snapshot.isEmpty
                Log.d("OM_TAG", "UserFirebaseApi: checkEmail: emailExist =  $emailExist")
            }
            .addOnFailureListener {
                emailExist = false
                Log.d("OM_TAG","UserFirebaseApi: checkEmail: emailExist = false")
                Log.d("OM_TAG", "UserFirebaseApi: checkEmail failed", it)
            }.await()
        return emailExist
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.d("OM_TAG", "UserFirebaseApi:signIn: success")
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.d("OM_TAG", "UserFirebaseApi: signIn: failed: ${e.localizedMessage}")
            return Result.failure(e)
        }
    }
}