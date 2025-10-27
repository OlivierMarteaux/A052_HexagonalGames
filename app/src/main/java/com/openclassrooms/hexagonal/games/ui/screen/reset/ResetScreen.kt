package com.openclassrooms.hexagonal.games.ui.screen.reset

import android.R.attr.contentDescription
import android.R.attr.text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedAlertDialog
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.localShared.composables.SharedIcon
import com.oliviermarteaux.shared.composables.SharedOutlinedEmail
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.extensions.isValidEmail
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.screen.settings.IconScaffold

/**
 * A screen for resetting the user's password.
 *
 * @param modifier The modifier to apply to this screen.
 * @param navigateToLoginScreen A function to call to navigate to the login screen.
 * @param onBackClick A function to call when the back button is clicked.
 * @param resetViewModel The view model for this screen.
 */
@Composable
fun ResetScreen(
    modifier: Modifier = Modifier,
    navigateToLoginScreen: () -> Unit,
    onBackClick: () -> Unit = {},
    resetViewModel: ResetViewModel = hiltViewModel()
) {

    SharedScaffold(
        modifier = modifier,
        title = stringResource(R.string.reset_screen_title),
        onBackClick = onBackClick
    ) { contentPadding ->
        with (resetViewModel) {
            Box {
                ResetBody(
                    email = email,
                    modifier = modifier
                        .padding(contentPadding)
                        .padding(horizontal = SharedPadding.xl)
                        .fillMaxSize()
                    ,
                    onEmailChange = ::onEmailChange,
                    sendPasswordResetEmail = ::sendPasswordResetEmail,
                    alertDialog = alertDialog,
                    navigateToLoginScreen = navigateToLoginScreen,
                )
                if(unknownError) SharedToast(text = stringResource(R.string.application_error_unknown))
                if(networkError) SharedToast(
                    text = stringResource(R.string.application_error_network),
                    bottomPadding = 120
                )
            }
        }
    }
}

/**
 * A composable for the body of the reset screen.
 *
 * @param email The user's email address.
 * @param modifier The modifier to apply to this composable.
 * @param onEmailChange A function to call when the email changes.
 * @param sendPasswordResetEmail A function to call to send a password reset email.
 * @param alertDialog A boolean indicating if the alert dialog should be shown.
 * @param navigateToLoginScreen A function to call to navigate to the login screen.
 */
@Composable
private fun ResetBody(
    email: String,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    sendPasswordResetEmail: (String) -> Unit,
    alertDialog: Boolean,
    navigateToLoginScreen: () -> Unit,
) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly,
//        modifier = modifier
//    ) {
//        SharedIcon(
//            modifier = Modifier.size(200.dp),
//            painter = painterResource(R.drawable.hexagonal_games_logo),
//        )
    IconScaffold(modifier = modifier){
        Text(
            text = stringResource(R.string.reset_label),
            textAlign = TextAlign.Center
        )
        SharedOutlinedEmail(
            value = email,
            onValueChange = { onEmailChange(it) },
            label = stringResource(R.string.email),
            imeAction = ImeAction.Done,
            modifier = Modifier.fillMaxWidth(),
            errorText = when {
                email.isEmpty() -> stringResource(R.string.login_screen_email_error_empty)
                !email.isValidEmail() -> stringResource(R.string.login_screen_email_error_format)
                else -> null
            }
        )
        SharedButton(text = stringResource(R.string.send)) { sendPasswordResetEmail(email) }
    }
    AnimatedVisibility(alertDialog) {
        SharedAlertDialog(
            title = stringResource(R.string.reset_alert_dialog_title),
            text = stringResource(R.string.reset_alert_dialog_text, email),
            onConfirm = navigateToLoginScreen,
            confirmText = stringResource(R.string.reset_alert_dialog_confirm_text)
        )
    }
}

