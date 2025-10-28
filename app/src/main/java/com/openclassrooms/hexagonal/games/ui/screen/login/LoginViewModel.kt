package com.openclassrooms.hexagonal.games.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.ui.showToastFlag
import com.oliviermarteaux.shared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.shared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for the Login screen.
 *
 * @param userRepository The repository for managing user data.
 * @param log The logger.
 * @param isOnlineFlow A flow that emits the current internet connection status.
 * @param dispatchers The coroutine dispatcher provider.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>,
    private val dispatchers: CoroutineDispatcherProvider,
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log,
) {
    /**
     * A boolean indicating if there was an error creating the account.
     */
    var accountCreationError: Boolean by mutableStateOf(false)
        private set
    /**
     * The new user object.
     */
    var newUser: NewUser by mutableStateOf(NewUser())
        private set
    /**
     * A boolean indicating if the email exists. `null` if the check has not been performed yet.
     */
    var emailExist: Boolean? by mutableStateOf(null)
        private set
    /**
     * Updates the email of the new user.
     *
     * @param newEmail The new email.
     */
    fun onEmailChange(newEmail: String) = updateUser { it.copy(email = newEmail) }
    /**
     * Updates the first name of the new user.
     *
     * @param newFirstName The new first name.
     */
    fun onFirstNameChange(newFirstName: String) = updateUser { it.copy(firstname = newFirstName) }
    /**
     * Updates the last name of the new user.
     *
     * @param newLastName The new last name.
     */
    fun onLastNameChange(newLastName: String) = updateUser { it.copy(lastname = newLastName) }
    /**
     * Updates the password of the new user.
     *
     * @param newPassword The new password.
     */
    fun onPasswordChange(newPassword: String) = updateUser { it.copy(password = newPassword) }
    /**
     * Resets the email exist check and invokes the provided callback.
     *
     * @param onResult The callback to invoke.
     */
    fun onEmailExist(onResult: () -> Unit){
        emailExist = null
        onResult()
    }

    /**
     * Checks if an email exists.
     *
     * @param email The email to check.
     */
    fun checkEmail(email: String) {
        viewModelScope.launch(dispatchers.io) {
            emailExist = userRepository.checkEmail(email).fold(
                onSuccess = { withContext(dispatchers.main) { it }},
                onFailure = {
                    withContext(dispatchers.main){
                        showUnknownErrorToast()
                        null
                    }
                }
            )
        }
    }
    /**
     * Creates a new account.
     *
     * @param newUser The new user to create.
     * @param onAccountCreated A callback to invoke when the account is created successfully.
     */
    fun createAccount(newUser: NewUser, onAccountCreated: () -> Unit) {
        viewModelScope.launch(dispatchers.io) {
            userRepository.createAccount(newUser).fold(
                onSuccess = { withContext(dispatchers.main) { onAccountCreated() } },
                onFailure = { withContext(dispatchers.main) { showAccountCreationErrorToast() } }
            )
        }
    }
    /**
     * Shows a toast message for an account creation error.
     */
    fun showAccountCreationErrorToast()= viewModelScope.launch {
        showToastFlag { accountCreationError = it }
    }
    /**
     * Updates the new user object.
     *
     * @param update A function to update the new user object.
     */
    private fun updateUser(update: (NewUser) -> NewUser) {
        newUser = update(newUser)
    }
}