package com.openclassrooms.hexagonal.games.screen.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.screen.login.SharedButton
import com.openclassrooms.hexagonal.games.screen.login.SharedOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    email: String,
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit = {},
    navigateToPasswordResetScreen: () -> Unit = {},
    onBackClick: () -> Unit = {},
    passwordViewModel: PasswordViewModel = hiltViewModel()
){

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.sign_in))
                }
            )
        },
    ) { contentPadding ->
        PasswordBody(
            email = email,
            password = passwordViewModel.password,
            modifier = modifier.padding(contentPadding),
            onPasswordChange = passwordViewModel::onPasswordChange,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToPasswordResetScreen = navigateToPasswordResetScreen,
            signIn = passwordViewModel::signIn
        )
    }
}

@Composable
private fun PasswordBody(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onPasswordChange: (String) -> Unit,
    navigateToHomeScreen: () -> Unit = {},
    navigateToPasswordResetScreen: () -> Unit = {},
    signIn: (String, String, () -> Unit) -> Unit = { _, _, _ -> }
) {
    Column (modifier = modifier){
        SharedOutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it) },
            label = stringResource(R.string.password),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        )
        SharedButton(
            onClick = navigateToPasswordResetScreen,
            text = stringResource(R.string.forgot_password)
        )
        SharedButton(
            onClick = {
                signIn(email, password, navigateToHomeScreen)
            },
            text = stringResource(R.string.sign_in)
        )
    }
}