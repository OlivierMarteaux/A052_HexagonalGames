package com.openclassrooms.hexagonal.games.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var newUser: NewUser by mutableStateOf(NewUser())
        private set
//    var currentUser: User? by mutableStateOf(null)
//        private set

    var emailExist: Boolean? by mutableStateOf(null)
        private set

    var errorMessage: String? by mutableStateOf(null)
        private set

    fun onEmailChange(newEmail: String) {
        newUser = newUser.copy( email = newEmail)
    }

    fun onFirstNameChange(newFirstName: String) {
        newUser = newUser.copy( firstname = newFirstName)
    }

    fun onLastNameChange(newLastName: String) {
        newUser = newUser.copy( lastname = newLastName)
    }

    fun onPasswordChange(newPassword: String) {
        newUser = newUser.copy( password = newPassword)
    }

    fun checkEmail(email: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.signInMethods
                    emailExist = !result.isNullOrEmpty()
                } else {
                    emailExist = false
                }
            }
    }

    /**
     * Create a new user with Firebase Authentication
     */
    fun createAccount(newUser: NewUser) {
        viewModelScope.launch {
            try {
                with (newUser) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
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