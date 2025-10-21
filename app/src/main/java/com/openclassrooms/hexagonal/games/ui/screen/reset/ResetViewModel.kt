package com.openclassrooms.hexagonal.games.ui.screen.reset

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log
) {
    private val initialEmail: String = checkNotNull(savedStateHandle["email"])
    var alertDialog by mutableStateOf(false)
        private set
    var email: String by mutableStateOf(initialEmail)
        private set
    fun onEmailChange(newEmail: String) { email = newEmail }
    fun sendPasswordResetEmail(email:String) = viewModelScope.launch {
        userRepository.sendPasswordResetEmail(email).fold(
            onSuccess = {
                alertDialog = true
                log.d("ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
            },
            onFailure = { e ->
                showUnknownErrorToast()
                log.e("ResetViewModel: sendPasswordResetEmail($email): Password reset failed", e)
            }
        )
    }
}