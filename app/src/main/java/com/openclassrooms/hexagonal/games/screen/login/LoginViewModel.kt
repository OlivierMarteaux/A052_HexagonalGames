package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var newUser: NewUser by mutableStateOf(NewUser())
        private set

    var emailExist: Boolean? by mutableStateOf(null)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    fun onEmailChange(newEmail: String) {
        newUser = newUser.copy(email = newEmail)
    }

    fun onFirstNameChange(newFirstName: String) {
        newUser = newUser.copy(firstname = newFirstName)
    }

    fun onLastNameChange(newLastName: String) {
        newUser = newUser.copy(lastname = newLastName)
    }

    fun onPasswordChange(newPassword: String) {
        newUser = newUser.copy(password = newPassword)
    }

//    fun checkEmail(email: String) {
//        val cleanEmail = email.trim().lowercase()
//        auth.fetchSignInMethodsForEmail(cleanEmail)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val result = task.result?.signInMethods
//                    emailExist = result != null
//                    Log.d("OM_TAG", "LoginViewModel: checkEmail: emailExist =  $emailExist")
//                } else {
//                    emailExist = false
//                    Log.d("OM_TAG", "checkEmail failed", task.exception)
//                }
//            }
//    }

    fun checkEmailInFirestore(email: String/*, onResult: (Boolean) -> Unit*/) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot ->
                emailExist = !snapshot.isEmpty
                Log.d("OM_TAG", "LoginViewModel: checkEmailInFirestore: emailExist =  $emailExist")
//                onResult(!snapshot.isEmpty)
            }
            .addOnFailureListener {
//                onResult(false)
                emailExist = false
                Log.d("OM_TAG","LoginViewModel: checkEmailInFirestore: emailExist =  $emailExist")
                Log.d("OM_TAG", "checkEmailInFirestore failed", it)
            }
    }

    /**
     * Create a new user with Firebase Authentication
     */
//    fun createAccount(newUser: NewUser) {
//        viewModelScope.launch {
//            try {
//                with(newUser) {
//                    auth.createUserWithEmailAndPassword(email, password)
//                        .addOnSuccessListener {
//                            Log.d("OM_TAG", "LoginViewModel: User created: ${it.user?.email}")
//                        }.await()
//                }
//            } catch (e: Exception) {
//                errorMessage = e.localizedMessage
//                Log.e("OM_TAG", "LoginViewModel: createAccount failed", e)
//            }
//        }
//    }

    fun createAccount(
        newUser: NewUser,
        onAccountCreated: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                with(newUser) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val user = authResult.user
                            val uid = user?.uid
                            if (uid != null) {
                                // 1) Update FirebaseUser profile (displayName)
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName("$firstname $lastname")
                                    .build()

                                user.updateProfile(profileUpdates)
                                    .addOnSuccessListener {
                                        Log.d("OM_TAG", "LoginViewModel: CreateAccount: Firestore: FirebaseUser profile updated with displayName")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("OM_TAG", "LoginViewModel: CreateAccount: Firestore: Failed to update FirebaseUser profile", e)
                                    }

                                val db = FirebaseFirestore.getInstance()
                                val userData = mapOf(
                                    "id" to uid,
                                    "firstname" to firstname,
                                    "lastname" to lastname,
                                    "email" to email
                                )
                                db.collection("users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "OM_TAG",
                                            "LoginViewModel: CreateAccount: Firestore: User profile created for $uid"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "OM_TAG",
                                            "LoginViewModel: CreateAccount: Firestore: Failed to create user profile",
                                            e
                                        )
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            errorMessage = e.localizedMessage
                            Log.e(
                                "OM_TAG",
                                "LoginViewModel: CreateAccount: createAccount failed",
                                e
                            )
                        }
                        .await()
                    onAccountCreated()
                    Log.d("OM_TAG", "LoginViewModel: CreateAccount: onAccountCreated called")
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.e("OM_TAG", "LoginViewModel: CreateAccount: createAccount exception", e)
            }
        }
    }

    init {
//        // Check if a user is already signed in
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        firebaseUser?.let { fbUser ->
//            currentUser = fbUser.toUser()
//        }
    }

    // Extension to map FirebaseUser -> User
//    private fun FirebaseUser.toUser(): User {
//        return User(
//            id = uid,
//            firstname = displayName?.substringBefore(" ") ?: "",
//            lastname = displayName?.substringAfter(" ") ?: "",
//            email = email ?: "",
//        )
//    }
//
//    fun onLoginSuccess(firebaseUser: FirebaseUser?) {
//        firebaseUser?.let {
//            currentUser = it.toUser()
//        }
//    }
//
//    fun logout() {
//        FirebaseAuth.getInstance().signOut()
//        currentUser = null
//    }
}