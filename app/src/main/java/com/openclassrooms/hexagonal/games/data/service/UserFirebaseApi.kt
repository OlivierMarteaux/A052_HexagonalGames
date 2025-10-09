package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

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
}