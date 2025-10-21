package com.openclassrooms.hexagonal.games.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openclassrooms.hexagonal.games.ui.screen.Screen
import com.openclassrooms.hexagonal.games.ui.screen.account.AccountScreen
import com.openclassrooms.hexagonal.games.ui.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.ui.screen.comment.CommentScreen
import com.openclassrooms.hexagonal.games.ui.screen.detail.DetailScreen
import com.openclassrooms.hexagonal.games.ui.screen.homefeed.HomeFeedScreen
import com.openclassrooms.hexagonal.games.ui.screen.login.LoginScreen
import com.openclassrooms.hexagonal.games.ui.screen.password.PasswordScreen
import com.openclassrooms.hexagonal.games.ui.screen.reset.ResetScreen
import com.openclassrooms.hexagonal.games.ui.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.ui.screen.splash.SplashScreen

@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Homefeed.route
    ) {
        /*_ SPLASH SCREEN ############################################################################*/
        composable(route = Screen.Splash.route) {
            SplashScreen(navigateToLoginScreen = { navHostController.navigate(Screen.Login.route) })
        }
        /*_ LOGIN SCREEN #############################################################################*/
        composable(route = Screen.Login.route) {
            LoginScreen(
                onBackClick = { navHostController.navigateUp() },
                navigateToPasswordScreen = {
                    email -> navHostController.navigate("password/$email")
                                           },
                navigateToHomeScreen = { navHostController.navigate(Screen.Homefeed.route) }
            )
        }
        /*_ PASSWORD SCREEN ##########################################################################*/
        composable(
            route = Screen.Password.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            PasswordScreen(
                email = email,
                onBackClick = { navHostController.navigateUp() },
                navigateToHomeScreen = { navHostController.navigate(Screen.Homefeed.route){
                    popUpTo(0) { inclusive = true } // clear everything
                } },
                navigateToPasswordResetScreen = {email -> navHostController.navigate(Screen.Reset.route + "/${email}")  }
            )
        }
        /*_ RESET SCREEN #############################################################################*/
        composable(
            route = Screen.Reset.route+ "/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetScreen(
//        email = email,
                onBackClick = { navHostController.navigateUp() },
                navigateToLoginScreen = { navHostController.navigate(Screen.Login.route) },
            )
        }
        /*_ HOME SCREEN ##############################################################################*/
        composable(route = Screen.Homefeed.route) {
            HomeFeedScreen(
                onPostClick = {post -> navHostController.navigate(Screen.Detail.route + "/${post.id}") },
                onSettingsClick = { navHostController.navigate(Screen.Settings.route) },
                navigateToLogin = { navHostController.navigate(Screen.Login.route) },
                navigateToAccount = { navHostController.navigate(Screen.Account.route) },
                navigateToAddPost = { navHostController.navigate(Screen.AddPost.route) }
            )
        }/*_ DETAIL SCREEN ###########################################################################*/
        composable(
            route = Screen.Detail.route + "/{post_id}",
            arguments = listOf(navArgument("post_id") { type = NavType.StringType })
        ){
            DetailScreen(
                onBackClick = { navHostController.navigateUp() },
                navigateToCommentScreen = {post -> navHostController.navigate(Screen.Comment.route + "/${post.id}") }
            )
        }
        /*_ COMMENT SCREEN ###########################################################################*/
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
        /*_ ACCOUNT SCREEN ###########################################################################*/
        composable(route = Screen.Account.route) {
            AccountScreen(
                navigateBack = { navHostController.navigateUp() },
            )
        }
        /*_ ADD POST SCREEN ##########################################################################*/
        composable(route = Screen.AddPost.route) {
            AddScreen(
                onBackClick = { navHostController.navigateUp() },
                navigateToHomeScreen = { navHostController.navigateUp() }
            )
        }
        /*_ SETTINGS SCREEN ##########################################################################*/
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navHostController.navigateUp() }
            )
        }
    }
}