package com.openclassrooms.hexagonal.games.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

abstract class AuthUserViewModel(private val userRepository: UserRepository) : ViewModel() {

    var currentUser: User? by mutableStateOf(null)
        protected set
    private fun observeUserState() {
        viewModelScope.launch {
            userRepository.userAuthState.collect { user ->
                currentUser = user?.toUser()
                Log.v("OM_TAG", "AuthUserViewModel: observeUserState(): current user is ${currentUser?.email?:"not connected"}")
            }
        }
    }
    init {
        observeUserState()
//        Log.v("OM_TAG", "AuthUserViewModel: init(): current user is ${currentUser?:"not connected"}")
    }
}