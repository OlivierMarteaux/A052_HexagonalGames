package com.openclassrooms.hexagonal.games.screen.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
  /*private val notificationManager: NotificationManager,*/
) : ViewModel() {

  /**
   * Enables notifications for the application.
   * TODO: Implement the logic to enable notifications, likely involving interactions with a notification manager.
   */
//  @RequiresApi(Build.VERSION_CODES.O)
  fun enableNotifications() {
//    val channel = notificationManager.getNotificationChannel("main")
//    channel?.let {
//      it.importance = NotificationManager.IMPORTANCE_DEFAULT
//      notificationManager.createNotificationChannel(it)
//    }
  }
  
  /**
   * Disables notifications for the application.
   * TODO: Implement the logic to disable notifications, likely involving interactions with a notification manager.
   */
//  @RequiresApi(Build.VERSION_CODES.O)
  fun disableNotifications() {
//    val channel = notificationManager.getNotificationChannel("main")
//    channel?.let {
//      it.importance = NotificationManager.IMPORTANCE_NONE
//      notificationManager.createNotificationChannel(it)
//    }
  }
}
