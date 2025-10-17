package com.openclassrooms.hexagonal.games.screen.reset

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context
): AuthUserViewModel(userRepository, context) {

    private val initialEmail: String = checkNotNull(savedStateHandle["email"])

    var alertDialog by mutableStateOf(false)
    private set

    var email: String by mutableStateOf(initialEmail)
        private set
    fun onEmailChange(newEmail: String) {
        email = newEmail
    }
    fun sendPasswordResetEmail(email:String) =
        viewModelScope.launch {
            userRepository.sendPasswordResetEmail(email).fold(
                onSuccess = {
                    alertDialog = true
                    Log.d("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
                },
                onFailure = { e ->
                    showUnknownErrorToast()
                    Log.e("OM_TAG", "ResetViewModel: sendPasswordResetEmail($email): Password reset failed", e)
                }
            )
        }
}