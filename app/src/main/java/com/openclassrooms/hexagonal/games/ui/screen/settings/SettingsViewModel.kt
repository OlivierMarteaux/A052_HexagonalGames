package com.openclassrooms.hexagonal.games.ui.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.Logger
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
  private val userPreferencesRepository: UserPreferencesRepository,
  private val log: Logger
) : ViewModel() {
  var notifPermissionAlertDialog: Boolean by mutableStateOf(false)
    private set
  var notifStateToast: Boolean by mutableStateOf(false)
    private set
  var notifState: String by mutableStateOf("disabled")
    private set
  fun showNotifStateToast() {
    log.d("SettingsViewModel: showNotifStateToast()")
    viewModelScope.launch {
      notifStateToast = true
      delay(3000)
      notifStateToast = false
    }
  }
  fun toggleNotifications(isNotifEnabled: Boolean) {
    viewModelScope.launch {
      userPreferencesRepository.saveNotificationPreference(isNotifEnabled)
      notifState = if (isNotifEnabled) "enabled" else "disabled"
      log.d("SettingsViewModel: toggleNotifications(): $isNotifEnabled")
    }
  }
  fun showNotifPermissionAlertDialog(value: Boolean) {
    log.d("SettingsViewModel: showNotifPermissionAlertDialog($value)")
    notifPermissionAlertDialog = value
  }
}