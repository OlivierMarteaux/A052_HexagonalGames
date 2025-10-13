package com.openclassrooms.hexagonal.games.screen.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
  var notifPermissionAlertDialog: Boolean by mutableStateOf(false)
    private set

  var notifStateToast: Boolean by mutableStateOf(false)
    private set

  fun showNotifStateToast() {
    viewModelScope.launch {
      notifStateToast = true
      delay(3000)
      notifStateToast = false
    }
  }

  fun showNotifPermissionAlertDialog(value: Boolean) {
    notifPermissionAlertDialog = value
  }
  fun toggleNotifications(isNotifEnabled: Boolean) {
    viewModelScope.launch {
      userPreferencesRepository.saveNotificationPreference(isNotifEnabled)
      Log.d("OM_TAG", "SettingsViewModel: toggleNotifications(): ${userPreferencesRepository.isNotifEnabled}")
    }
  }
}