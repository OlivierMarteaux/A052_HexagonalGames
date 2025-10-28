package com.openclassrooms.hexagonal.games.ui.screen

import androidx.navigation.NamedNavArgument

/**
 * A sealed class that represents the different screens in the application.
 *
 * @property route The route for the screen.
 * @property navArguments The navigation arguments for the screen.
 */
sealed class Screen(
  val route: String,
  val navArguments: List<NamedNavArgument> = emptyList()
) {
  /**
   * The splash screen.
   */
  data object Splash : Screen("splash")
  /**
   * The home feed screen.
   */
  data object Homefeed : Screen("homefeed")
  /**
   * The detail screen.
   */
  data object Detail : Screen("detail/{post_id}")

  /**
   * The comment screen.
   */
  data object Comment : Screen("comment/{post_id}")
  /**
   * The add post screen.
   */
  data object AddPost : Screen("addPost")
  /**
   * The settings screen.
   */
  data object Settings : Screen("settings")
  /**
   * The account screen.
   */
  data object Account : Screen("account")
  /**
   * The login screen.
   */
  data object Login : Screen("login")
  /**
   * The password screen.
   */
  data object Password : Screen("password/{email}")
  /**
   * The reset password screen.
   */
  data object Reset : Screen("reset/{email}")
}