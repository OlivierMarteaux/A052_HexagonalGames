package com.openclassrooms.hexagonal.games.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.localShared.extensions.isHardEnough
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedPassword
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.oliviermarteaux.shared.extensions.isValidEmail
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToPasswordScreen: (String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    onBackClick: () -> Unit = {},
    loginViewModel: LoginViewModel = hiltViewModel()
){
    val newUser: NewUser = loginViewModel.newUser
    val emailExist: Boolean? = loginViewModel.emailExist
    val isOnline: Boolean = loginViewModel.isOnline
    val networkError: Boolean = loginViewModel.networkError
    val unknownError: Boolean = loginViewModel.unknownError
    val accountCreationError: Boolean = loginViewModel.accountCreationError

    HexagonalGamesScaffold(
        modifier = modifier,
        title = stringResource(R.string.login_screen_label)
    ) { contentPadding ->
        Box {
            LoginBody(
                newUser = newUser,
                emailExist = emailExist,
                isOnline = isOnline,
                modifier = modifier.padding(contentPadding),
                onEmailChange = loginViewModel::onEmailChange,
                onFirstNameChange = loginViewModel::onFirstNameChange,
                onLastNameChange = loginViewModel::onLastNameChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                createAccount = loginViewModel::createAccount,
                checkEmail = loginViewModel::checkEmail,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToPasswordScreen = navigateToPasswordScreen,
                showNetworkErrorToast = loginViewModel::showNetworkErrorToast,
            )
            TriggeredToast(
                trigger = unknownError,
                text = stringResource(R.string.application_error_unknown),
            )
            TriggeredToast(
                trigger = networkError,
                text = stringResource(R.string.application_error_network),
                bottomPadding = 120
            )
            TriggeredToast(
                trigger = accountCreationError,
                text = stringResource(R.string.login_screen_error_account),
                bottomPadding = 120
            )
        }
    }
}

@Composable
private fun LoginBody(
    newUser: NewUser,
    emailExist: Boolean?,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    createAccount: (NewUser, () -> Unit) -> Unit,
    checkEmail: (String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToPasswordScreen: (String) -> Unit,
    showNetworkErrorToast: () -> Unit,
){
    Column(modifier = modifier){
        Column {
            SharedOutlinedTextField(
                value = newUser.email,
                onValueChange = { onEmailChange(it) },
                label = stringResource(R.string.email),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                isError = !newUser.email.run {isValidEmail() && isNotEmpty()},
                errorText = when {
                    newUser.email.isEmpty() -> stringResource(R.string.login_screen_email_error_empty)
                    !newUser.email.isValidEmail() -> stringResource(R.string.login_screen_email_error_format)
                    else -> {"null"}
                }
            )
            when {
                emailExist == null -> {
                    SharedButton(
                        onClick = { if (isOnline) checkEmail(newUser.email) else showNetworkErrorToast() },
                        text = stringResource(R.string.next)
                    )
                }
                emailExist -> { navigateToPasswordScreen(newUser.email) }
                !emailExist -> {
                    Column {
                        SharedOutlinedTextField(
                            value = newUser.firstname,
                            onValueChange = { onFirstNameChange(it) },
                            label = stringResource(R.string.first_name),
                            isError = newUser.firstname.isEmpty(),
                            errorText = stringResource(R.string.login_screen_first_name_error_empty)
                        )
                        SharedOutlinedTextField(
                            value = newUser.lastname,
                            onValueChange = { onLastNameChange(it) },
                            label = stringResource(R.string.last_name),
                            isError = newUser.lastname.isEmpty(),
                            errorText = stringResource(R.string.login_screen_last_name_error_empty)
                        )
                        SharedOutlinedPassword(
                            value = newUser.password,
                            onValueChange = { onPasswordChange(it) },
                            label = stringResource(R.string.new_password),
                            isError = !newUser.password.isHardEnough(6),
                            errorText = stringResource(R.string.login_screen_password_error_strength)
                        )
                        SharedButton(
                            onClick = { createAccount(newUser){navigateToHomeScreen()} },
                            text = stringResource(R.string.create_account)
                        )
                    }
                }
            }
        }
    }
}