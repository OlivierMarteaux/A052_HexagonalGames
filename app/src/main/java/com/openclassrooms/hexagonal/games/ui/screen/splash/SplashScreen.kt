package com.openclassrooms.hexagonal.games.ui.screen.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedImage
import com.openclassrooms.hexagonal.games.R

/**
 * A screen that is displayed when the application is launched.
 *
 * @param modifier The modifier to apply to this screen.
 * @param navigateToLoginScreen A function to call to navigate to the login screen.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToLoginScreen: () -> Unit,
) {
    Column(
        modifier = modifier
    ){
        SharedImage(
            painter = painterResource(id = R.drawable.hexagonal_games_logo),
        )
        SharedButton(
            onClick = navigateToLoginScreen,
            text = stringResource(R.string.sign_in_with_email)
        )
    }
}