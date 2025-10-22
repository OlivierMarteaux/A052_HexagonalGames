package com.openclassrooms.hexagonal.games.ui.screen.password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedPassword
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.openclassrooms.hexagonal.games.R

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
                    modifier = modifier.padding(contentPadding),
                    onPasswordChange = ::onPasswordChange,
                    navigateToHomeScreen = navigateToHomeScreen,
                    navigateToPasswordResetScreen = navigateToPasswordResetScreen,
                    signIn = ::signIn
                )
                TriggeredToast(
                    trigger = incorrectPassword,
                    text = stringResource(R.string.password_screen_error_incorrect_password)
                )
                TriggeredToast(
                    trigger = unknownError,
                    text = stringResource(R.string.application_error_unknown),
                    bottomPadding = 120
                )
                TriggeredToast(
                    trigger = networkError,
                    text = stringResource(R.string.application_error_network)
                )
            }
        }
    }
}

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
    Column (modifier = modifier){
        Text(text = stringResource(R.string.password_label, email))
        SharedOutlinedPassword(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = stringResource(R.string.password),
            imeAction = ImeAction.Done,
        )
        SharedButton(text = stringResource(R.string.forgot_password)) {
            navigateToPasswordResetScreen(email)
        }

        SharedButton(text = stringResource(R.string.sign_in)) {
            signIn(password) { navigateToHomeScreen() }
        }
    }
}