package com.openclassrooms.hexagonal.games.ui.screen.reset

import android.R.attr.text
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedAlertDialog
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedEmail
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.oliviermarteaux.shared.extensions.isValidEmail
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@Composable
fun ResetScreen(
//    email: String,
    modifier: Modifier = Modifier,
    navigateToLoginScreen: () -> Unit,
    onBackClick: () -> Unit = {},
    resetViewModel: ResetViewModel = hiltViewModel()
) {

    HexagonalGamesScaffold(
        modifier = modifier,
        title = "Reset Password"
    ) { contentPadding ->
        with (resetViewModel) {
            Box {
                ResetBody(
                    email = email,
                    modifier = modifier.padding(contentPadding),
                    onEmailChange = ::onEmailChange,
                    sendPasswordResetEmail = ::sendPasswordResetEmail,
                    alertDialog = alertDialog,
                    navigateToLoginScreen = navigateToLoginScreen
                )
                TriggeredToast(
                    trigger = unknownError,
                    text = stringResource(R.string.application_error_unknown)
                )
            }
        }
    }
}

@Composable
private fun ResetBody(
    email: String,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    sendPasswordResetEmail: (String) -> Unit,
    alertDialog: Boolean,
    navigateToLoginScreen: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(text = stringResource(R.string.reset_label))
//        SharedOutlinedTextField(
//            value = email,
//            onValueChange = { onEmailChange(it) },
//            label = stringResource(R.string.email),
//            keyboardType = KeyboardType.Email,
//            imeAction = ImeAction.Done,
//            isError = email.run {isValidEmail() && isNotEmpty()},
//            errorText = when {
//                email.isEmpty() -> stringResource(R.string.login_screen_email_error_empty)
//                email.isValidEmail() -> stringResource(R.string.login_screen_email_error_format)
//                else -> {"null"}
//            }
//        )
        SharedOutlinedEmail(
            value = email,
            onValueChange = { onEmailChange(it) },
            label = stringResource(R.string.email),
            imeAction = ImeAction.Done,
            errorText = when {
                email.isEmpty() -> stringResource(R.string.login_screen_email_error_empty)
                !email.isValidEmail() -> stringResource(R.string.login_screen_email_error_format)
                else -> {"null"}
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

