package com.openclassrooms.hexagonal.games.ui.screen

import androidx.navigation.NamedNavArgument

sealed class Screen(
  val route: String,
  val navArguments: List<NamedNavArgument> = emptyList()
) {
  data object Splash : Screen("splash")
  data object Homefeed : Screen("homefeed")
  data object Detail : Screen("detail/{post_id}")

  data object Comment : Screen("comment/{post_id}")
  data object AddPost : Screen("addPost")
  data object Settings : Screen("settings")
  data object Account : Screen("account")
  data object Login : Screen("login")
  data object Password : Screen("password/{email}")
  data object Reset : Screen("reset/{email}")
}