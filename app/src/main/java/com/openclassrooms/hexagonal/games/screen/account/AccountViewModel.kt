package com.openclassrooms.hexagonal.games.screen.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    fun signOut(onSignOut: () -> Unit = {}) {
        userRepository.signOut()
        onSignOut()
        Log.d("OM_TAG", "AccountViewModel: signOut(): current user is now ${userRepository.currentUser}")
        Log.d("OM_TAG", "AccountViewModel: signOut(): onSignOut() called")
    }
    fun deleteAccount(onDeleteAccount: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.deleteAccount()
            onDeleteAccount()
            Log.d("OM_TAG", "AccountViewModel: deleteAccount(): onDeleteAccount() called")
        }
    }
}