package com.openclassrooms.hexagonal.games.ui.screen.reset

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.oliviermarteaux.shared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Reset Password screen.
 *
 * @param savedStateHandle The saved state handle for the view model.
 * @param userRepository The repository for managing user data.
 * @param log The logger.
 * @param isOnlineFlow A flow that emits the current internet connection status.
 */
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
    /**
     * The initial email address passed to the screen.
     */
    private val initialEmail: String = checkNotNull(savedStateHandle["email"])
    /**
     * A boolean indicating if the alert dialog should be shown.
     */
    var alertDialog by mutableStateOf(false)
        private set
    /**
     * The email address entered by the user.
     */
    var email: String by mutableStateOf(initialEmail)
        private set
    /**
     * Updates the email address.
     *
     * @param newEmail The new email address.
     */
    fun onEmailChange(newEmail: String) { email = newEmail }
    /**
     * Sends a password reset email to the specified email address.
     *
     * @param email The email address to send the reset link to.
     */
    fun sendPasswordResetEmail(email:String) = viewModelScope.launch {
        userRepository.sendPasswordResetEmail(email).fold(
            onSuccess = {
                alertDialog = true
                log.d("ResetViewModel: sendPasswordResetEmail($email): Password reset email sent")
            },
            onFailure = { error ->
                log.e("ResetViewModel: sendPasswordResetEmail($email): Password reset failed: ${error.message}")
                when (error) {
                    is FirebaseNetworkException -> showNetworkErrorToast()
                    else -> showUnknownErrorToast()
                }
            }
        )
    }
}