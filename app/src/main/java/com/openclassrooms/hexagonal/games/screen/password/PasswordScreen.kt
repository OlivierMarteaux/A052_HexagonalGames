package com.openclassrooms.hexagonal.games.screen.password

import android.R.attr.text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedPassword
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    email: String,
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    navigateToPasswordResetScreen: (String) -> Unit,
    onBackClick: () -> Unit = {},
    passwordViewModel: PasswordViewModel = hiltViewModel()
){
    val incorrectPassword: Boolean = passwordViewModel.incorrectPassword
    val unknownError: Boolean = passwordViewModel.unknownError

    HexagonalGamesScaffold(
        modifier = modifier,
        title = stringResource(R.string.sign_in)
    ){ contentPadding ->
        Box {
            PasswordBody(
                email = email,
                password = passwordViewModel.password,
                modifier = modifier.padding(contentPadding),
                onPasswordChange = passwordViewModel::onPasswordChange,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToPasswordResetScreen = navigateToPasswordResetScreen,
                signIn = passwordViewModel::signIn
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
    signIn: (String, String, () -> Unit) -> Unit
) {
    Column (modifier = modifier){
        Text(text = stringResource(R.string.password_label, email))
        SharedOutlinedPassword(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = stringResource(R.string.password),
            imeAction = ImeAction.Done,
        )
        SharedButton(text = stringResource(R.string.forgot_password))
            { navigateToPasswordResetScreen(email) }

        SharedButton(text = stringResource(R.string.sign_in))
            { signIn(email, password, navigateToHomeScreen) }
    }
}