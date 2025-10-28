package com.openclassrooms.hexagonal.games.ui.screen.password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.composables.IconScaffold
import com.oliviermarteaux.shared.composables.IconSource
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedPassword
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.openclassrooms.hexagonal.games.R

/**
 * A screen for entering a password to sign in.
 *
 * @param modifier The modifier to apply to this screen.
 * @param navigateToHomeScreen A function to call to navigate to the home screen.
 * @param navigateToPasswordResetScreen A function to call to navigate to the password reset screen.
 * @param onBackClick A function to call when the back button is clicked.
 * @param passwordViewModel The view model for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    navigateToPasswordResetScreen: (String) -> Unit,
    onBackClick: () -> Unit = {},
    passwordViewModel: PasswordViewModel = hiltViewModel()
){
    SharedScaffold(
        modifier = modifier,
        title = stringResource(R.string.sign_in),
        onBackClick = onBackClick
    ){ contentPadding ->
        Box {
            with (passwordViewModel) {
                PasswordBody(
                    email = email,
                    password = password,
                    modifier = modifier
                        .padding(contentPadding)
                        .padding(horizontal = SharedPadding.xl)
                        .fillMaxSize(),
                    onPasswordChange = ::onPasswordChange,
                    navigateToHomeScreen = navigateToHomeScreen,
                    navigateToPasswordResetScreen = navigateToPasswordResetScreen,
                    signIn = ::signIn
                )
                if(unknownError) SharedToast(text = stringResource(R.string.application_error_unknown))
                if(networkError) SharedToast(
                    text = stringResource(R.string.application_error_network),
                    bottomPadding = 120
                )
                if(incorrectPassword)SharedToast(
                    text = stringResource(R.string.password_screen_error_incorrect_password),
                    bottomPadding = 160
                )
            }
        }
    }
}

/**
 * A composable for the body of the password screen.
 *
 * @param email The user's email address.
 * @param password The user's password.
 * @param modifier The modifier to apply to this composable.
 * @param onPasswordChange A function to call when the password changes.
 * @param navigateToHomeScreen A function to call to navigate to the home screen.
 * @param navigateToPasswordResetScreen A function to call to navigate to the password reset screen.
 * @param signIn A function to call to sign in the user.
 */
@Composable
private fun PasswordBody(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onPasswordChange: (String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToPasswordResetScreen: (String) -> Unit,
    signIn: (String, () -> Unit) -> Unit
) {
    IconScaffold(
        icon = IconSource.PainterIcon(painterResource(R.drawable.hexagonal_games_logo)),
        modifier = modifier
    ){
        Text(
            text = stringResource(R.string.password_label, email),
            textAlign = TextAlign.Center,
        )
        SharedOutlinedPassword(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = stringResource(R.string.password),
            imeAction = ImeAction.Done,
            modifier = Modifier.fillMaxWidth()
        )
        SharedButton(text = stringResource(R.string.forgot_password)){ navigateToPasswordResetScreen(email) }
        SharedButton(text = stringResource(R.string.sign_in)){ signIn(password) { navigateToHomeScreen() } }
    }
}