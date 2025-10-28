package com.openclassrooms.hexagonal.games.ui.screen.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.ui.showToastFlag
import com.oliviermarteaux.shared.utils.Logger
import com.oliviermarteaux.shared.datastore.NotificationPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val notificationPreferencesRepository: NotificationPreferencesRepository,
  private val log: Logger
) : ViewModel() {
  /**
   * A boolean indicating if the notification permission alert dialog should be shown.
   */
  var notifPermissionAlertDialog: Boolean by mutableStateOf(false)
    private set
  /**
   * A boolean indicating if the notification state toast should be shown.
   */
  var notifStateToast: Boolean by mutableStateOf(false)
    private set
  /**
   * The current state of the notification preference.
   */
  var notifState: Boolean by mutableStateOf(false)
    private set
  /**
   * Shows a toast message with the current notification state.
   */
  fun showNotifStateToast() = viewModelScope.launch { showToastFlag { notifStateToast = it } }
  /**
   * Toggles the notification preference.
   *
   * @param isNotifEnabled Whether notifications should be enabled.
   */
  fun toggleNotifications(isNotifEnabled: Boolean) {
    viewModelScope.launch {
      notificationPreferencesRepository.saveNotificationPreference(isNotifEnabled)
      notifState = isNotifEnabled
      log.d("SettingsViewModel: toggleNotifications(): $isNotifEnabled")
    }
  }
  /**
   * Shows or hides the notification permission alert dialog.
   *
   * @param value Whether to show the dialog.
   */
  fun showNotifPermissionAlertDialog(value: Boolean) {
    log.d("SettingsViewModel: showNotifPermissionAlertDialog($value)")
    notifPermissionAlertDialog = value
  }
}