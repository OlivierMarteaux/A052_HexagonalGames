package com.openclassrooms.hexagonal.games.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.ui.showToastFlag
import com.oliviermarteaux.localShared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * An abstract view model that provides authentication and user state management.
 *
 * @param userRepository The repository for managing user data.
 * @param log The logger.
 * @param isOnlineFlow A flow that emits the current internet connection status.
 */
abstract class AuthUserViewModel(
    private val userRepository: UserRepository,
    private val log: Logger,
    /*_ isOnlineFlow injected instead of calling checkInternetConnection() to make class easier
        to test. */
    private val isOnlineFlow: Flow<Boolean>,
) : ViewModel() {
    /**
     * The currently signed-in user.
     */
    var currentUser: User? by mutableStateOf(null)
        protected set
    /**
     * A boolean indicating if the device is online.
     */
    var isOnline: Boolean by mutableStateOf(true)
        private set
    /**
     * A boolean indicating if there is an authentication error.
     */
    var authError: Boolean by mutableStateOf(false)
        private set
    /**
     * A boolean indicating if there is a network error.
     */
    var networkError: Boolean by mutableStateOf(false)
        private set
    /**
     * A boolean indicating if there is an unknown error.
     */
    var unknownError: Boolean by mutableStateOf(false)
        private set

    /**
     * Checks the user's authentication state and invokes the appropriate callback.
     *
     * @param onUserLogged The callback to invoke if the user is logged in.
     * @param onNoUserLogged The callback to invoke if the user is not logged in.
     */
    fun checkUserState(
        onUserLogged: () -> Unit,
        onNoUserLogged: () -> Unit
    ) {
        currentUser?.let {
            log.v("AuthUserViewModel: onAuthUserClick: currentUser = ${currentUser?.email}")
            onUserLogged()
        }?: run {
            log.v("AuthUserViewModel: onAuthUserClick: no user logged")
            onNoUserLogged()
        }
    }
    /**
     * Shows a toast message for a network error.
     */
    fun showNetworkErrorToast() = viewModelScope.launch { showToastFlag{ networkError = it } }
    /**
     * Shows a toast message for an unknown error.
     */
    fun showUnknownErrorToast() = viewModelScope.launch { showToastFlag{ unknownError = it } }
    /**
     * Shows a toast message for an authentication error.
     */
    fun showAuthErrorToast() = viewModelScope.launch {showToastFlag{ authError = it }}

    /**
     * Observes the online state of the device.
     */
    fun observeOnlineState(){
        viewModelScope.launch {
            isOnlineFlow.collect{
                isOnline = it
                log.v("AuthUserViewModel: checkOnlineState(): online state is $it")
                if (!isOnline) showNetworkErrorToast()
            }
        }
    }
    /**
     * Observes the user's authentication state.
     */
    private fun observeUserState() {
        viewModelScope.launch {
            userRepository.userAuthState.collect { user ->
                currentUser = user?.toUser()
                log.v("AuthUserViewModel: observeUserState(): current user is ${currentUser?.email?:"not connected"}")
            }
        }
    }
    init {
        observeUserState()
        observeOnlineState()
    }
}