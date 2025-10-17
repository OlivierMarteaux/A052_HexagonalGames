package com.openclassrooms.hexagonal.games.screen.password

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @param: ApplicationContext private val context: Context
): AuthUserViewModel(userRepository, context) {

    var incorrectPassword: Boolean by mutableStateOf(false)
        private set
    var password: String by mutableStateOf("")
        private set
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }
    fun showIncorrectPasswordToast(){
        viewModelScope.launch {
            incorrectPassword = true
            delay(2000)
            incorrectPassword = false
        }
    }
    fun signIn(email: String, password: String, onSignIn: () -> Unit) = viewModelScope.launch {
        userRepository.signIn(email, password).fold(
            onSuccess = { onSignIn() },
            onFailure = { error ->
                when (error) {
                    is FirebaseAuthInvalidCredentialsException,
                    is IllegalArgumentException -> {
                        Log.e("OM_TAG", "PasswordViewModel: signIn: Invalid credentials: ${ error.message ?: "" }")
                        showIncorrectPasswordToast()
                    }
                    else -> {
                        Log.e("OM_TAG", "PasswordViewModel: Unknown error: ${error.message}")
                        showUnknownErrorToast()
                    }
                }
            }
        )
    }
}