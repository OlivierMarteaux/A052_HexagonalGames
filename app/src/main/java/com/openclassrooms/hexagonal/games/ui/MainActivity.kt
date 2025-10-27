package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.oliviermarteaux.shared.composables.startup.DismissKeyboardOnTapOutside
import com.oliviermarteaux.shared.composables.startup.RequestNotificationPermission
import com.oliviermarteaux.shared.firebase.fcm.getDeviceToken
import com.openclassrooms.hexagonal.games.ui.navigation.HexagonalGamesNavHost
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    //_ translucent status bar
    WindowCompat.setDecorFitsSystemWindows(window, false)
    
    setContent {
      val navController = rememberNavController()

      // Observe current backstack entry
      val currentBackStackEntry by navController.currentBackStackEntryAsState()
      LaunchedEffect(currentBackStackEntry) {
        currentBackStackEntry?.destination?.route?.let { route ->
          Log.i("OM_TAG", " ${route.uppercase()} SCREEN")
        }
      }
      HexagonalGamesTheme {
//        SetStatusBarColor(Color.Transparent)
        RequestNotificationPermission()
        getDeviceToken()
        DismissKeyboardOnTapOutside { HexagonalGamesNavHost(navHostController = navController) }
      }
    }
  }
}