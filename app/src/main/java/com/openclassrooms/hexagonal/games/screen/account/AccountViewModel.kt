package com.openclassrooms.hexagonal.games.screen.account

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.android.gms.auth.api.Auth
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.AuthUserViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context
) : AuthUserViewModel(userRepository, context) {
//) : ViewModel() {
//    var currentUser: User? by mutableStateOf(null)
//        private set
    fun deleteAccount(onDeleteAccount: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.deleteAccount().fold(
                onSuccess = {
                    Log.d("OM_TAG", "AccountViewModel deleteAccount(): account deleted")
                    onDeleteAccount()
                },
                onFailure = {
                    showUnknownErrorToast()
                }
            )
        }
    }
    fun signOut(onSignOut: () -> Unit = {}) {
        userRepository.signOut().fold(
            onSuccess = {
                Log.d("OM_TAG", "AccountViewModel signOut(): user signed out")
                onSignOut()
            },
            onFailure = {
                showUnknownErrorToast()
            }
        )
    }
//    private fun observeUserState() {
//        viewModelScope.launch {
//            userRepository.userAuthState.collect { user ->
//                currentUser = user?.toUser()
//                Log.d("OM_TAG", "DetailViewModel observeUserState(): current user is $currentUser")
//            }
//        }
//    }
//    init { observeUserState() }
}