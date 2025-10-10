package com.openclassrooms.hexagonal.games

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here
        Log.d("OM_TAG", "FCM: From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("OM_TAG", "FCM: Message Notification: ${it.body}")
            val title = it.title ?: "New Post"
            val body = it.body ?: ""
            showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("OM_TAG", "FCM: Refreshed token: $token")

        // TODO: Send token to your backend if needed
    }

    private fun showNotification(title: String, body: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Default", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.hexagonal_games_logo)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}