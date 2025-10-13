package com.openclassrooms.hexagonal.games

import android.app.Application
import android.content.Context
import android.util.Log
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.oliviermarteaux.shared.firebase.fcm.subscribeToFcmNotificationTopic
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
            //_ initialize firebase
            FirebaseApp.initializeApp(this)
            Log.d("OM_TAG", "HexagonalGamesApplication: onCreate(): FirebaseApp initialized")

            //_ Firebase authentification: sign out user at app start
            FirebaseAuth.getInstance().signOut()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            Log.d("OM_TAG", "HexagonalGamesApplication: onCreate(): FirebaseAuth signed out")
            Log.i("OM_TAG", "HexagonalGamesApplication: onCreate(): firebaseUser = $firebaseUser")

            //_ Firebase cloud messaging: create notif channel and subscribe topic
            //_ not needed if only one default channel as it is created by MyFirebaseMessaging class
//            createDeviceNotificationChannel(
//                notifManager = getSystemService(NotificationManager::class.java)
//            )
            //_ Firebase cloud messaging: subscribe to Firebase topic (Mandatory to receive notifs)
            subscribeToFcmNotificationTopic()

            //_ manage application exceptions
        } catch (e: Exception) {
            Log.e("OM_TAG", "HexagonalGamesApplication: onCreate(): FirebaseApp initialization failed", e)
        }
    }
}