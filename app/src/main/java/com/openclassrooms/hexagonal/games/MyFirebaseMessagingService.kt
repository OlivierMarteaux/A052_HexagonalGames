package com.openclassrooms.hexagonal.games

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here
        Log.d("OM_TAG", "FCM: From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("OM_TAG", "FCM: Message Notification: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("OM_TAG", "FCM: Refreshed token: $token")

        // TODO: Send token to your backend if needed
    }
}