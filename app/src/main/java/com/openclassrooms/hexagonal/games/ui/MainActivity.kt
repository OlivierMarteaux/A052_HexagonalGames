package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.oliviermarteaux.shared.composables.startup.RequestNotificationPermission
import com.oliviermarteaux.shared.firebase.getDeviceToken
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.account.AccountScreen
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.screen.login.LoginScreen
import com.openclassrooms.hexagonal.games.screen.password.PasswordScreen
import com.openclassrooms.hexagonal.games.screen.reset.ResetScreen
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.screen.splash.SplashScreen
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
    
    setContent {
      val navController = rememberNavController()
      
      HexagonalGamesTheme {
        RequestNotificationPermission()
        getDeviceToken()
        HexagonalGamesNavHost(navHostController = navController)
      }
    }
  }
}
@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
  NavHost(
    navController = navHostController,
    startDestination = Screen.Splash.route
  ) {
    /* SPLASH SCREEN ############################################################################*/
    composable(route = Screen.Splash.route) {
      Log.d("OM_TAG", "NavHost: splash screen displayed")
      SplashScreen(
        navigateToLoginScreen = {
          navHostController.navigate(Screen.Login.route)
        }
      )
    }
    /* LOGIN SCREEN #############################################################################*/
    composable(route = Screen.Login.route) {
      LoginScreen(
        onBackClick = { navHostController.navigateUp() },
        navigateToPasswordScreen = { email -> navHostController.navigate("password/$email") },
        navigateToHomeScreen = {
          Log.d("OM_TAG", "NavHost: navigating to home screen")
          navHostController.navigate(Screen.Homefeed.route)
        }
      )
    }
    /* PASSWORD SCREEN ##########################################################################*/
    composable(
      route = Screen.Password.route,
      arguments = listOf(
        navArgument("email") { type = NavType.StringType }
      )
    ) { backStackEntry ->
      val email = backStackEntry.arguments?.getString("email") ?: ""
      PasswordScreen(
        email = email,
        navigateToHomeScreen = {navHostController.navigate(Screen.Homefeed.route)},
        navigateToPasswordResetScreen = {email ->
          navHostController.navigate("reset/$email")
        }
      )
    }
    /* RESET SCREEN #############################################################################*/
    composable(
      route = Screen.Reset.route,
      arguments = listOf(
        navArgument("email") { type = NavType.StringType }
      )
    ) { backStackEntry ->
      val email = backStackEntry.arguments?.getString("email") ?: ""
      ResetScreen(
        email = email,
        navigateToLoginScreen = {navHostController.navigate(Screen.Login.route)},
      )
    }
    /* HOME SCREEN ##############################################################################*/
    composable(route = Screen.Homefeed.route) {
      Log.d("OM_TAG", "NavHost: home screen displayed")
      HomefeedScreen(
        onPostClick = {
          //TODO
        },
        onSettingsClick = {
          navHostController.navigate(Screen.Settings.route)
        },
        onAccountClick = {
          val connected = FirebaseAuth.getInstance().currentUser
          connected?.let{navHostController.navigate(Screen.Account.route)}?:
          navHostController.navigate(Screen.Login.route)
        },
        onFABClick = {
          navHostController.navigate(Screen.AddPost.route)
        }
      )
    }
    /* ACCOUNT SCREEN ###########################################################################*/
    composable(route = Screen.Account.route) {
      AccountScreen(
        navigateToSplashScreen = {
          navHostController.navigate(Screen.Splash.route) {
            popUpTo(navHostController.graph.startDestinationId) { //clears the stack back to the first screen.
              inclusive = true //removes even that first destination, so Splash becomes the new root.
            }
            launchSingleTop = true //avoids creating multiple Splash screens if user signs out multiple times.
          }
        },
      )
    }
    /* ADD POST SCREEN ##########################################################################*/
    composable(route = Screen.AddPost.route) {
      AddScreen(
        onBackClick = { navHostController.navigateUp() },
        onSaveClick = { navHostController.navigateUp() }
      )
    }
    /* SETTINGS SCREEN ##########################################################################*/
    composable(route = Screen.Settings.route) {
      SettingsScreen(
        onBackClick = { navHostController.navigateUp() }
      )
    }
  }
}
