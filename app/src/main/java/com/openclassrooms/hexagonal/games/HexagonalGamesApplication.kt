package com.openclassrooms.hexagonal.games

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

/**
 * The application class for the Hexagonal Games application.
 * This class serves as the entry point for the application and can be used for global application-level
 * initialization tasks such as dependency injection setup using Hilt.
 */
@HiltAndroidApp
class HexagonalGamesApplication : Application(), SingletonImageLoader.Factory{

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context = context)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        try {
//            createNotificationChannels()
            FirebaseApp.initializeApp(this)
            FirebaseAuth.getInstance().signOut()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "Subscribed to allUsers topic")
                    }
                }
            Log.d("OM_TAG", "HexagonalGamesApplication: onCreate(): FirebaseApp initialized")
            Log.d("OM_TAG", "HexagonalGamesApplication: onCreate(): FirebaseAuth signed out")
            Log.i("OM_TAG", "HexagonalGamesApplication: onCreate(): firebaseUser = $firebaseUser")
        } catch (e: Exception) {
            Log.e("OM_TAG", "HexagonalGamesApplication: onCreate(): FirebaseApp initialization failed", e)
        }
    }

//    private fun createNotificationChannels() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val defaultChannel = NotificationChannel(
//                "main",
//                "Main Notifications",
//                NotificationManager.IMPORTANCE_DEFAULT
//            ).apply {
//                description = "Main notifications"
//            }
//
//            val manager = getSystemService(NotificationManager::class.java)
//            manager.createNotificationChannel(defaultChannel)
//        }
//    }
}
