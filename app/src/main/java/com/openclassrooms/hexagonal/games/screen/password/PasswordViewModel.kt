package com.openclassrooms.hexagonal.games.screen.password

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class PasswordViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

//    private val auth = FirebaseAuth.getInstance()
//    var errorMessage: String? by mutableStateOf(null)
//        private set
    var password: String by mutableStateOf("")
        private set
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun signIn(email: String, password: String, onSignIn: () -> Unit) = viewModelScope.launch {
        userRepository.signIn(email, password).fold(
            onSuccess = {
                Log.d("OM_TAG", "PasswordViewModel: signIn: Login successful")
                onSignIn()
            },
            onFailure = { e ->
                Log.d("OM_TAG", "PasswordViewModel: signIn: Login failed: ${e.localizedMessage}")
            }
        )
    }

//    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                val result = auth.signInWithEmailAndPassword(email, password).await()
//                val uid = result.user?.uid
//                Log.d("OM_TAG", "PasswordViewModel:signIn: uid: $uid")
//                if (uid != null) {
//                    onSuccess()
//                } else {
//                    errorMessage = "Login failed: no user id"
//                    Log.d("OM_TAG", "PasswordViewModel: signIn: Login failed: no user id")
//                }
//            } catch (e: Exception) {
//                errorMessage = e.localizedMessage
//                Log.d("OM_TAG", "PasswordViewModel: signIn: Login failed: ${e.localizedMessage}")
//            }
//        }
//    }
}