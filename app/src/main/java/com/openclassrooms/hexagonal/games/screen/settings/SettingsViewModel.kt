package com.openclassrooms.hexagonal.games.screen.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val notificationManager: NotificationManager,
  private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

  fun toggleNotifications(isNotifEnabled: Boolean) {
    viewModelScope.launch {
      userPreferencesRepository.saveNotificationPreference(isNotifEnabled)
      Log.d("OM_TAG", "SettingsViewModel: toggleNotifications(): ${userPreferencesRepository.isNotifEnabled}")
    }
  }

  /**
   * Enables notifications for the application.
   * TODO: Implement the logic to enable notifications, likely involving interactions with a notification manager.
   */
//  @RequiresApi(Build.VERSION_CODES.O)
//  fun enableNotifications() {
//    val channel = notificationManager.getNotificationChannel("NewPostChannel")
//    channel?.let {
//      userPreferencesRepository.setNotificationsEnabled(true)
////      it.importance = NotificationManager.IMPORTANCE_DEFAULT
////      notificationManager.createNotificationChannel(it)
////      Log.d("OM_TAG", "SettingsViewModel: enableNotifications(): Notification Channel: ${it.importance}")
//    }
//  }
  
  /**
   * Disables notifications for the application.
   * TODO: Implement the logic to disable notifications, likely involving interactions with a notification manager.
   */
//  @RequiresApi(Build.VERSION_CODES.O)
//  fun disableNotifications() {
//    val channel = notificationManager.getNotificationChannel("NewPostChannel")
//    channel?.let {
//      it.importance = NotificationManager.IMPORTANCE_NONE
//      notificationManager.createNotificationChannel(it)
//      Log.d("OM_TAG", "SettingsViewModel: disableNotifications(): Notification Channel: ${it.importance}")
//    }
//  }
}
