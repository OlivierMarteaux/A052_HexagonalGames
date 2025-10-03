package com.openclassrooms.hexagonal.games.screen.reset

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResetViewModel : ViewModel() {

    var email: String by mutableStateOf("")
        private set
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    private val auth = FirebaseAuth.getInstance()
    var alertDialog by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                // ✅ Show success dialog
                alertDialog = true
                Log.d("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Log.e("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset failed", e)
            }
        }
    }
}