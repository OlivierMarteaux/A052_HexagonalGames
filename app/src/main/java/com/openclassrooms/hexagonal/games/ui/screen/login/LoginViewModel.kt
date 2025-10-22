package com.openclassrooms.hexagonal.games.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.ui.showToastFlag
import com.oliviermarteaux.localShared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    var accountCreationError: Boolean by mutableStateOf(false)
        private set
    var newUser: NewUser by mutableStateOf(NewUser())
        private set
    var emailExist: Boolean? by mutableStateOf(null)
        private set
    fun onEmailChange(newEmail: String) = updateUser { it.copy(email = newEmail) }
    fun onFirstNameChange(newFirstName: String) = updateUser { it.copy(firstname = newFirstName) }
    fun onLastNameChange(newLastName: String) = updateUser { it.copy(lastname = newLastName) }
    fun onPasswordChange(newPassword: String) = updateUser { it.copy(password = newPassword) }
    fun onEmailExist(onResult: () -> Unit){
        emailExist = null
        onResult()
    }

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
    fun createAccount(newUser: NewUser, onAccountCreated: () -> Unit) {
        viewModelScope.launch(dispatchers.io) {
            userRepository.createAccount(newUser).fold(
                onSuccess = { withContext(dispatchers.main) { onAccountCreated() } },
                onFailure = { withContext(dispatchers.main) { showAccountCreationErrorToast() } }
            )
        }
    }
    fun showAccountCreationErrorToast()= viewModelScope.launch {
        showToastFlag { accountCreationError = it }
    }
    private fun updateUser(update: (NewUser) -> NewUser) {
        newUser = update(newUser)
    }
}