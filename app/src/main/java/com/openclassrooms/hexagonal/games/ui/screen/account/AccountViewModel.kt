package com.openclassrooms.hexagonal.games.ui.screen.account

import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Account screen.
 *
 * @param userRepository The repository for managing user data.
 * @param log The logger.
 * @param isOnlineFlow A flow that emits the current internet connection status.
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log
) {
    /**
     * Deletes the user's account.
     *
     * @param onDeleteAccount A callback to invoke when the account is deleted successfully.
     */
    fun deleteAccount(onDeleteAccount: () -> Unit) {
        viewModelScope.launch {
            userRepository.deleteAccount().fold(
                onSuccess = { onDeleteAccount() },
                onFailure = { showUnknownErrorToast() }
            )
        }
    }
    /**
     * Signs out the user.
     *
     * @param onSignOut A callback to invoke when the user is signed out successfully.
     */
    fun signOut(onSignOut: () -> Unit = {}) {
        userRepository.signOut().fold(
            onSuccess = { onSignOut() },
            onFailure = { showUnknownErrorToast() }
        )
    }
}