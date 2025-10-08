package com.openclassrooms.hexagonal.games

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import com.google.firebase.FirebaseApp
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
        FirebaseApp.initializeApp(this)
    }
}
