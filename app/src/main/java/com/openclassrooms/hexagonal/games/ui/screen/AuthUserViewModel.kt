package com.openclassrooms.hexagonal.games.ui.screen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.AndroidLogger
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class AuthUserViewModel(
    private val userRepository: UserRepository,
    private val log: Logger,
    /*_ isOnlineFlow injected instead of calling checkInternetConnection() to make class easier
        to test. */
    private val isOnlineFlow: Flow<Boolean>,
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

    fun checkUserState(
        onUserLogged: () -> Unit,
        onNoUserLogged: () -> Unit
    ) {
        currentUser?.let {
            log.v("AuthUserViewModel: onAuthUserClick: currentUser = ${currentUser?.email}")
            onUserLogged()
        }?: run {
            log.v("AuthUserViewModel: onAuthUserClick: no user logged")
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
            isOnlineFlow.collect{
                isOnline = it
                log.v("AuthUserViewModel: checkOnlineState(): online state is $it")
                if (!isOnline) showNetworkErrorToast()
            }
        }
    }
    private fun observeUserState() {
        viewModelScope.launch {
            userRepository.userAuthState.collect { user ->
                currentUser = user?.toUser()
                log.v("AuthUserViewModel: observeUserState(): current user is ${currentUser?.email?:"not connected"}")
//                currentUser?:showAuthErrorToast()
            }
        }
    }
    init {
        observeUserState()
        observeOnlineState()
    }
}