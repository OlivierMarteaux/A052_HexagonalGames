package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.oliviermarteaux.shared.composables.startup.RequestNotificationPermission
import com.oliviermarteaux.shared.firebase.getDeviceToken
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.account.AccountScreen
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.comment.CommentScreen
import com.openclassrooms.hexagonal.games.screen.detail.DetailScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomeFeedScreen
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

      // Observe current backstack entry
      val currentBackStackEntry by navController.currentBackStackEntryAsState()
      LaunchedEffect(currentBackStackEntry) {
        currentBackStackEntry?.destination?.route?.let { route ->
          Log.i("OM_TAG", " ${route.uppercase()} SCREEN")
        }
      }
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
    startDestination = Screen.Homefeed.route
  ) {
    /* SPLASH SCREEN ############################################################################*/
    composable(route = Screen.Splash.route) {
      SplashScreen(navigateToLoginScreen = { navHostController.navigate(Screen.Login.route) })
    }
    /* LOGIN SCREEN #############################################################################*/
    composable(route = Screen.Login.route) {
      LoginScreen(
        onBackClick = { navHostController.navigateUp() },
        navigateToPasswordScreen = { email -> navHostController.navigate("password/$email") },
        navigateToHomeScreen = { navHostController.navigate(Screen.Homefeed.route) }
      )
    }
    /* PASSWORD SCREEN ##########################################################################*/
    composable(
      route = Screen.Password.route,
      arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) { backStackEntry ->
      val email = backStackEntry.arguments?.getString("email") ?: ""
      PasswordScreen(
        email = email,
        navigateToHomeScreen = { navHostController.navigate(Screen.Homefeed.route) },
        navigateToPasswordResetScreen = {email -> navHostController.navigate("reset/$email") }
      )
    }
    /* RESET SCREEN #############################################################################*/
    composable(
      route = Screen.Reset.route,
      arguments = listOf(navArgument("email") { type = NavType.StringType })
    ) { backStackEntry ->
      val email = backStackEntry.arguments?.getString("email") ?: ""
      ResetScreen(
        email = email,
        navigateToLoginScreen = { navHostController.navigate(Screen.Login.route) },
      )
    }
    /* HOME SCREEN ##############################################################################*/
    composable(route = Screen.Homefeed.route) {
      HomeFeedScreen(
        onPostClick = {post -> navHostController.navigate(Screen.Detail.route + "/${post.id}") },
        onSettingsClick = { navHostController.navigate(Screen.Settings.route) },
        navigateToLogin = { navHostController.navigate(Screen.Login.route) },
        navigateToAccount = { navHostController.navigate(Screen.Account.route) },
        navigateToAddPost = { navHostController.navigate(Screen.AddPost.route) }
      )
    }/* DETAIL SCREEN ###########################################################################*/
    composable(
      route = Screen.Detail.route + "/{post_id}",
      arguments = listOf(navArgument("post_id") { type = NavType.StringType })
    ){
      DetailScreen(
        onBackClick = { navHostController.navigateUp() },
        onFABClick = {post -> navHostController.navigate(Screen.Comment.route + "/${post.id}") }
      )
    }
    /* COMMENT SCREEN ###########################################################################*/
    composable(
      route = Screen.Comment.route + "/{post_id}",
      arguments = listOf(
        navArgument("post_id") { type = NavType.StringType }
      )
    ){
      CommentScreen(
        onBackClick = { navHostController.navigateUp() },
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
        onSaveClick = { navHostController.popBackStack() }
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
