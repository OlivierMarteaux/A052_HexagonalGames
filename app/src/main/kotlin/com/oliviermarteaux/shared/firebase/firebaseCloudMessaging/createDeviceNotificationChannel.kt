package com.oliviermarteaux.shared.firebase.firebaseCloudMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

fun createDeviceNotificationChannel(
    notifId: String = "allUsers",
    notifName: String = "allUsers",
    notifImportance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    notifDesc: String = "allUsers",
    notifManager: NotificationManager
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val defaultChannel = NotificationChannel(
            notifId,
            notifName,
            notifImportance
        ).apply {
            description = notifDesc
        }
        notifManager.createNotificationChannel(defaultChannel)
    }
}