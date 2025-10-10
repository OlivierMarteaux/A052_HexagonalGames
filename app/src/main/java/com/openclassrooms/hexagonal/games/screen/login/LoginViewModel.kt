package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import android.util.Log.e
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    var newUser: NewUser by mutableStateOf(NewUser())
        private set
    var emailExist: Boolean? by mutableStateOf(null)
        private set
    fun onEmailChange(newEmail: String) {
        newUser = newUser.copy(email = newEmail)
    }
    fun onFirstNameChange(newFirstName: String) {
        newUser = newUser.copy(firstname = newFirstName)
    }
    fun onLastNameChange(newLastName: String) {
        newUser = newUser.copy(lastname = newLastName)
    }
    fun onPasswordChange(newPassword: String) {
        newUser = newUser.copy(password = newPassword)
    }
    fun checkEmail(email: String) {
        viewModelScope.launch {
            emailExist = userRepository.checkEmail(email)
        }
    }
    fun createAccount(newUser: NewUser, onAccountCreated: () -> Unit) {
        viewModelScope.launch {
            userRepository.createAccount(newUser)
            onAccountCreated()
        }
    }
}