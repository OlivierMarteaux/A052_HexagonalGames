package com.openclassrooms.hexagonal.games.screen.settings

import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.oliviermarteaux.shared.utils.checkNotificationPermission
import com.openclassrooms.hexagonal.games.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

  var notifState: String by mutableStateOf("disabled")
    private set

  fun showNotifStateToast() {
    viewModelScope.launch {
      notifStateToast = true
      delay(3000)
      notifStateToast = false
    }
  }

//  fun showNotifPermissionAlertDialog(value: Boolean) {
//    notifPermissionAlertDialog = value
//  }
  fun toggleNotifications(isNotifEnabled: Boolean) {
    viewModelScope.launch {
      userPreferencesRepository.saveNotificationPreference(isNotifEnabled)
      notifState = if (isNotifEnabled) "enabled" else "disabled"
      Log.d("OM_TAG", "SettingsViewModel: toggleNotifications(): ${userPreferencesRepository.isNotifEnabled}")
    }
  }

//  @OptIn(ExperimentalPermissionsApi::class)
//  fun checkNotifPermission(
//    notifPermissionState: PermissionState?,
//    onNotifPermissionGranted: () -> Unit
//  )
//  {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//      if (notifPermissionState?.status?.isGranted == false) {
//        notifPermissionState.launchPermissionRequest()
//      } else {
//        onNotifPermissionGranted()
//      }
//    } else { onNotifPermissionGranted() }
//  }
}