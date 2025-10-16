package com.openclassrooms.hexagonal.games.screen

import android.R.attr.onClick
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

abstract class AuthUserViewModel(
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    var currentUser: User? by mutableStateOf(null)
        protected set
    var isOnline: Boolean by mutableStateOf(true)
        private set
    var authError: Boolean by mutableStateOf(false)
        private set
    var networkError: Boolean by mutableStateOf(false)
        private set
    var unknownError: Boolean by mutableStateOf(false)
        private set

    fun onAuthUserClick(
        onUserLogged: () -> Unit,
        onNoUserLogged: () -> Unit
    ) {
        currentUser?.let {
            Log.v("OM_TAG", "AuthUserViewModel: onAuthUserClick: currentUser = ${currentUser?.email}")
            onUserLogged()
        }?: run {
            Log.v("OM_TAG", "AuthUserViewModel: onAuthUserClick: no user logged")
            onNoUserLogged()
        }
    }

    fun showNetworkErrorToast() = viewModelScope.launch {
        networkError = true
        delay(TOAST_DURATION)
        networkError = false
    }
    fun showUnknownErrorToast() = viewModelScope.launch {
        unknownError = true
        delay(TOAST_DURATION)
        unknownError = false
    }
    fun showAuthErrorToast(duration: Long = TOAST_DURATION){
        viewModelScope.launch {
            authError = true
            delay(duration)
            authError = false
        }
    }

    fun observeOnlineState(){
        viewModelScope.launch {
            checkInternetConnection(context).collect{
                isOnline = it
                Log.v("OM_TAG", "AuthUserViewModel: checkOnlineState(): online state is $it")
                if (!isOnline) showNetworkErrorToast()
            }
        }
    }
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
        observeOnlineState()
    }
}