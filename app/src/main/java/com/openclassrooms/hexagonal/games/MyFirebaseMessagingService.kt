package com.openclassrooms.hexagonal.games

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassrooms.hexagonal.games.data.repository.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService(
) : FirebaseMessagingService() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var notificationManager: NotificationManager
    val channelId = "NewPostChannel"

    override fun onCreate() {
        super.onCreate()
        notificationManager = application.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //_ Handle FCM messages here
        Log.d("OM_TAG", "FCM: OnMessageReceived: From: ${remoteMessage.from}")

        //_ 🔹 Collect the latest value of the DataStore flow once (suspend)
        CoroutineScope(Dispatchers.IO).launch {
            val isNotifEnabled = userPreferencesRepository.isNotifEnabled.firstOrNull() ?: true

            if (!isNotifEnabled) {
                Log.d("OM_TAG", "FCM: Notifications disabled by user, skipping notification")
                return@launch
            }

            remoteMessage.notification?.let {
                Log.d("OM_TAG", "FCM: OnMessageReceived: Message Notification: ${it.body}")
                val title = it.title ?: "New Post"
                val body = it.body ?: ""
                showNotification(
                    notifTitle = title,
                    notifBody = body,
                    notifIcon = R.drawable.hexagonal_games_logo,
                    notifChannelId = channelId,
                    notifChannelTitle = "New posts channel",
                    notifDescription = "This channel notify users for all new posts"
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("OM_TAG", "FCM: OnNewToken: new token = $token")

        sendRegistrationTokenToServer(token)
    }

    private fun showNotification(
        notifChannelId: String = "DefaultChannelId",
        notifChannelTitle: String = "Default channel title",
        notifChannelImportance: Int = NotificationManager.IMPORTANCE_HIGH,
        notifTitle: String = "Default notification title",
        notifBody: String = "",
        notifIcon: Int,
        notifDescription: String = "Default notification description",
        ) {

        //_ create android device notification channel for application
        // (not necessarily the same as Firebase topic)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(notifChannelId, notifChannelTitle, notifChannelImportance)
                    .apply {
                description = notifDescription
                    }
            notificationManager.createNotificationChannel(channel)
        }

        //_ build the notification body
        val notification = NotificationCompat.Builder(this, notifChannelId)
            .setContentTitle(notifTitle)
            .setContentText(notifBody)
            .setSmallIcon(notifIcon)
            .setAutoCancel(true)
            .build()

        //_ display notification on device
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendRegistrationTokenToServer(token: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.w("OM_TAG", "sendRegistrationTokenToServer: No user logged in, cannot save FCM token.")
            return
        }

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(user.uid)

        userRef.update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("OM_TAG", "sendRegistrationTokenToServer: FCM token successfully updated for user ${user.uid}")
            }
            .addOnFailureListener { e ->
                Log.w("OM_TAG", "sendRegistrationTokenToServer: Error updating FCM token", e)
            }
    }
}