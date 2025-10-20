package com.openclassrooms.hexagonal.games.ui.screen.account

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log
) {
    fun deleteAccount(onDeleteAccount: () -> Unit) {
        viewModelScope.launch {
            userRepository.deleteAccount().fold(
                onSuccess = {
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
                onSignOut()
            },
            onFailure = {
                showUnknownErrorToast()
            }
        )
    }
}