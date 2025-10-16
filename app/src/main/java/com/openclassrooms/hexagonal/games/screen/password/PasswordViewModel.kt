package com.openclassrooms.hexagonal.games.screen.password

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
class PasswordViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    var password: String by mutableStateOf("")
        private set
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }
    fun signIn(email: String, password: String, onSignIn: () -> Unit) = viewModelScope.launch {
        userRepository.signIn(email, password).fold(
            onSuccess = { onSignIn() },
            onFailure = { e ->Log.e("OM_TAG", e.message?:"")}
        )
    }
}