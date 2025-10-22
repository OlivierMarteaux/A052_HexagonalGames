package com.openclassrooms.hexagonal.games.ui.screen.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.oliviermarteaux.localShared.ui.showToastFlag
import com.oliviermarteaux.localShared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log,
) {
    val email: String = checkNotNull(savedStateHandle["email"])
    var incorrectPassword: Boolean by mutableStateOf(false)
        private set
    var password: String by mutableStateOf("")
        private set
    fun onPasswordChange(newPassword: String) { password = newPassword }
    fun showIncorrectPasswordToast() = viewModelScope.launch { showToastFlag{ incorrectPassword = it } }
    fun signIn(password: String, onSignIn: () -> Unit) = viewModelScope.launch {
        userRepository.signIn(email, password).fold(
            onSuccess = { onSignIn() },
            onFailure = { error ->
                when (error) {
                    is FirebaseAuthInvalidCredentialsException,
                    is IllegalArgumentException -> {
                        log.e("PasswordViewModel: signIn: Invalid credentials: ${ error.message ?: "" }")
                        showIncorrectPasswordToast()
                    }
                    is FirebaseNetworkException -> showNetworkErrorToast()
                    else -> {
                        log.e("PasswordViewModel: Unknown error: ${error.message}", error)
                        showUnknownErrorToast()
                    }
                }
            }
        )
    }
}