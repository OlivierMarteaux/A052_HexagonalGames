package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import androidx.lifecycle.ViewModel
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

//    var currentUser: User? by mutableStateOf(getCurrentUser())
//        private set
//
//    fun getCurrentUser(): User? = userRepository.getCurrentUser()

    fun signOut(onSignOut: () -> Unit = {}) {
        userRepository.signOut()
        onSignOut()
        Log.d("OM_TAG", "AccountViewModel: signOut(): onSignOut() called")
    }
    fun deleteAccount(onDeleteAccount: () -> Unit = {}) {
        userRepository.deleteAccount()
        onDeleteAccount()
        Log.d("OM_TAG", "AccountViewModel: deleteAccount(): onDeleteAccount() called")
    }

//    init {
////        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        val currentUser = userRepository.getCurrentUser()
//        currentUser?.let{ user = it.toUser() }
//        Log.d("OM_TAG", "AccountViewModel: init(): current user = $user")
//    }

//    fun signOut(onSignOut: () -> Unit = {}){
//        FirebaseAuth.getInstance().signOut()
//        user = null
//        Log.d("OM_TAG", "AccountViewModel: signOut(): current user = $user")
//        onSignOut()
//        Log.d("OM_TAG", "AccountViewModel: signOut(): onSignOut() called")
//    }
//
//    fun deleteAccount(onDeleteAccount: () -> Unit = {}) {
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            val uid = user.uid
//            val db = FirebaseFirestore.getInstance()
//
//            // 1) Delete Firestore entry
//            db.collection("users").document(uid)
//                .delete()
//                .addOnSuccessListener {
//                    Log.d("OM_TAG", "AccountViewModel: disconnect(): Firestore user $uid deleted")
//
//                    // 2) Delete Firebase Auth account
//                    user.delete()
//                        .addOnSuccessListener {
//                            Log.d("OM_TAG", "AccountViewModel: disconnect(): Auth user deleted")
//                            signOut()
//                        }
//                        .addOnFailureListener { e ->
//                            Log.e("OM_TAG", "AccountViewModel: disconnect(): Failed to delete Auth user", e)
//                        }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("OM_TAG", "AccountViewModel: disconnect(): Failed to delete Firestore user", e)
//                }
//        } else {
//            Log.w("OM_TAG", "AccountViewModel: disconnect(): No user logged in to delete")
//        }
//        onDeleteAccount()
//    }
}