package com.openclassrooms.hexagonal.games.screen.reset

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    var email: String by mutableStateOf("")
        private set
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }
    var alertDialog by mutableStateOf(false)
        private set
    fun sendPasswordResetEmail(email:String) =
        viewModelScope.launch {
            userRepository.sendPasswordResetEmail(email).fold(
                onSuccess = {
                    alertDialog = true
                    Log.d("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
                },
                onFailure = { e ->
                    Log.e("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset failed", e)
                }
            )
        }
}