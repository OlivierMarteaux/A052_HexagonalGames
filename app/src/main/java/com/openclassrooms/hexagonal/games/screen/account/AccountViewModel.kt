package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.User


class AccountViewModel : ViewModel() {

    var user: User? by mutableStateOf(null)
        private set

    init {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let{ user = it.toUser() }
        Log.d("OM_TAG", "AccountViewModel: init(): current user = $user")
    }

    fun signOut(onSignOut: () -> Unit = {}){
        FirebaseAuth.getInstance().signOut()
        user = null
        Log.d("OM_TAG", "AccountViewModel: signOut(): current user = $user")
        onSignOut()
        Log.d("OM_TAG", "AccountViewModel: signOut(): onSignOut() called")
    }

    fun deleteAccount(onDeleteAccount: () -> Unit = {}) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
            val db = FirebaseFirestore.getInstance()

            // 1) Delete Firestore entry
            db.collection("users").document(uid)
                .delete()
                .addOnSuccessListener {
                    Log.d("OM_TAG", "AccountViewModel: disconnect(): Firestore user $uid deleted")

                    // 2) Delete Firebase Auth account
                    user.delete()
                        .addOnSuccessListener {
                            Log.d("OM_TAG", "AccountViewModel: disconnect(): Auth user deleted")
                            signOut()
                        }
                        .addOnFailureListener { e ->
                            Log.e("OM_TAG", "AccountViewModel: disconnect(): Failed to delete Auth user", e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("OM_TAG", "AccountViewModel: disconnect(): Failed to delete Firestore user", e)
                }
        } else {
            Log.w("OM_TAG", "AccountViewModel: disconnect(): No user logged in to delete")
        }
        onDeleteAccount()
    }
}