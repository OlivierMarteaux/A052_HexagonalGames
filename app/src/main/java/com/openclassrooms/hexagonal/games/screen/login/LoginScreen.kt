package com.openclassrooms.hexagonal.games.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.ui.HexagonalGamesScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToPasswordScreen: (String) -> Unit,
    onBackClick: () -> Unit = {},
    loginViewModel: LoginViewModel = hiltViewModel(),
    ){
        val newUser = loginViewModel.newUser
        val emailExist = loginViewModel.emailExist
        if (emailExist == true) navigateToPasswordScreen(newUser.email)

        HexagonalGamesScaffold(
            modifier = modifier,
            title = stringResource(R.string.login_screen_label)
        ) { contentPadding ->
            LoginBody(
                newUser = newUser,
                emailExist = emailExist,
                modifier = modifier.padding(contentPadding),
                onEmailChange = loginViewModel::onEmailChange,
                onFirstNameChange = loginViewModel::onFirstNameChange,
                onLastNameChange = loginViewModel::onLastNameChange,
                onPasswordChange = loginViewModel::onPasswordChange,
                createAccount = loginViewModel::createAccount,
                checkEmailInFirestore = loginViewModel::checkEmailInFirestore
            )
        }
}

@Composable
private fun LoginBody(
    newUser: NewUser,
    emailExist: Boolean?,
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    createAccount: (NewUser) -> Unit,
    checkEmailInFirestore: (String) -> Unit,
){
    Column(modifier = modifier){
        Column {
            SharedOutlinedTextField(
                value = newUser.email,
                onValueChange = { onEmailChange(it) },
                label = stringResource(R.string.email),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            )
            AnimatedVisibility(emailExist == null) {
                SharedButton(
                    onClick = {
                        checkEmailInFirestore(newUser.email)
                    },
                    text = stringResource(R.string.next)
                )
            }
        }
        AnimatedVisibility(emailExist == false){
            Column {
                SharedOutlinedTextField(
                    value = newUser.firstname,
                    onValueChange = { onFirstNameChange(it) },
                    label = stringResource(R.string.first_name),
                )
                SharedOutlinedTextField(
                    value = newUser.lastname,
                    onValueChange = { onLastNameChange(it) },
                    label = stringResource(R.string.last_name),
                )
                SharedOutlinedTextField(
                    value = newUser.password,
                    onValueChange = { onPasswordChange(it) },
                    label = stringResource(R.string.new_password),
                    keyboardType = KeyboardType.Password,
                )
                SharedButton(
                    onClick = { createAccount(newUser) },
                    text = stringResource(R.string.create_account)
                )
            }
        }
    }
}