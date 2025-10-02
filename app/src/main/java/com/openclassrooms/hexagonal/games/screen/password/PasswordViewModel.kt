package com.openclassrooms.hexagonal.games.screen.password

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PasswordViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    var errorMessage: String? by mutableStateOf(null)
        private set
    var password: String by mutableStateOf("")
        private set
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                Log.d("OM_TAG", "PasswordViewModel:signIn: uid: $uid")
                if (uid != null) {
                    onSuccess()
                } else {
                    errorMessage = "Login failed: no user id"
                    Log.d("OM_TAG", "PasswordViewModel: signIn: Login failed: no user id")
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.d("OM_TAG", "PasswordViewModel: signIn: Login failed: ${e.localizedMessage}")
            }
        }
    }
}