package com.openclassrooms.hexagonal.games.screen.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.updateValue
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context
) : AuthUserViewModel(userRepository, context) {
    var accountCreationError: Boolean by mutableStateOf(false)
        private set
    var newUser: NewUser by mutableStateOf(NewUser())
        private set
    var emailExist: Boolean? by mutableStateOf(null)
        private set
//    fun onEmailChange(newEmail: String) {
//        newUser = newUser.copy(email = newEmail)
//    }
//    fun onFirstNameChange(newFirstName: String) {
//        newUser = newUser.copy(firstname = newFirstName)
//    }
//    fun onLastNameChange(newLastName: String) {
//        newUser = newUser.copy(lastname = newLastName)
//    }
//    fun onPasswordChange(newPassword: String) {
//        newUser = newUser.copy(password = newPassword)
//    }
    fun onEmailChange(newEmail: String) = updateUser { it.copy(email = newEmail) }
    fun onFirstNameChange(newFirstName: String) = updateUser { it.copy(firstname = newFirstName) }
    fun onLastNameChange(newLastName: String) = updateUser { it.copy(lastname = newLastName) }
    fun onPasswordChange(newPassword: String) = updateUser { it.copy(password = newPassword) }

    fun checkEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            emailExist = userRepository.checkEmail(email).fold(
                onSuccess = { withContext(Dispatchers.Main) { it }},
                onFailure = {
                    withContext(Dispatchers.Main){
                        showUnknownErrorToast()
                        null
                    }
                }
            )
        }
    }
    fun createAccount(newUser: NewUser, onAccountCreated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.createAccount(newUser).fold(
                onSuccess = { withContext(Dispatchers.Main) { onAccountCreated() } },
                onFailure = { withContext(Dispatchers.Main) { showAccountCreationErrorToast() } }
            )
        }
    }
    private fun showAccountCreationErrorToast(){
        viewModelScope.launch{
            accountCreationError = true
            delay(TOAST_DURATION)
            accountCreationError = false
        }
    }
    private fun updateUser(update: (NewUser) -> NewUser) {
        newUser = update(newUser)
    }
}