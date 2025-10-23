package com.openclassrooms.hexagonal.games.ui.screen.login

import android.R.attr.bottom
import android.R.attr.label
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.localShared.composables.SharedIcon
import com.oliviermarteaux.shared.composables.SharedOutlinedEmail
import com.oliviermarteaux.shared.composables.SharedOutlinedPassword
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.extensions.isValidEmail
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.ui.theme.SharedSize
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.NewUser
import com.openclassrooms.hexagonal.games.ui.screen.settings.IconScaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.common.math.LinearTransformation.vertical


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToPasswordScreen: (String) -> Unit,
    navigateToHomeScreen: () -> Unit,
    onBackClick: () -> Unit = {},
    loginViewModel: LoginViewModel = hiltViewModel()
){
    with (loginViewModel) {
        SharedScaffold(
            modifier = modifier,
            title = stringResource(R.string.login_screen_label),
            onBackClick = onBackClick,
        ) { contentPadding ->
            Box {
                LoginBody(
                    newUser = newUser,
                    emailExist = emailExist,
                    isOnline = isOnline,
                    modifier = modifier
                        .padding(contentPadding)
                        .padding(horizontal = SharedPadding.xl)
                        .fillMaxSize(),
                    onEmailChange = ::onEmailChange,
                    onFirstNameChange = ::onFirstNameChange,
                    onLastNameChange = ::onLastNameChange,
                    onPasswordChange = ::onPasswordChange,
                    createAccount = ::createAccount,
                    checkEmail = ::checkEmail,
                    navigateToHomeScreen = navigateToHomeScreen,
                    navigateToPasswordScreen = navigateToPasswordScreen,
                    showNetworkErrorToast = ::showNetworkErrorToast,
                    onEmailExist = ::onEmailExist,
                )
                if(unknownError) SharedToast(text = stringResource(R.string.application_error_unknown))
                if(networkError) SharedToast(
                    text = stringResource(R.string.application_error_network),
                    bottomPadding = 120
                )
                if(accountCreationError) SharedToast(
                    text = stringResource(R.string.login_screen_error_account),
                    bottomPadding = 160
                )
            }
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
    onEmailExist: (() -> Unit)-> Unit,
){
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly,
//        modifier = modifier
//    ){
//        SharedIcon(
//            modifier = Modifier.size(200.dp),
//            painter = painterResource(R.drawable.hexagonal_games_logo),
//        )
    var verticalArrangement: Arrangement.Vertical by remember { mutableStateOf(Arrangement.SpaceEvenly) }
    var scaffoldModifier: Modifier by remember { mutableStateOf(modifier) }

    IconScaffold(
        modifier = scaffoldModifier,
        verticalArrangement = verticalArrangement,
    ){
        SharedOutlinedEmail(
            value = newUser.email,
            onValueChange = { onEmailChange(it) },
            label = stringResource(R.string.email),
            imeAction = ImeAction.Done,
            modifier = Modifier.fillMaxWidth(),
            bottomPadding = SharedPadding.xxl,
            errorText = when {
                newUser.email.isEmpty() -> stringResource(R.string.login_screen_email_error_empty)
                !newUser.email.isValidEmail() -> stringResource(R.string.login_screen_email_error_format)
                else -> null
            }
        )
        when {
            emailExist == null -> {
                SharedButton(
                    onClick = { if (isOnline) checkEmail(newUser.email) else showNetworkErrorToast() },
                    text = stringResource(R.string.next),
                    enabled = newUser.email.run {isValidEmail() && isNotEmpty()}
                )
            }
            emailExist -> { onEmailExist{navigateToPasswordScreen(newUser.email) } }
            !emailExist -> {
                verticalArrangement = Arrangement.Top
                scaffoldModifier = modifier
                    .verticalScroll(rememberScrollState())
//                    .imePadding()
                val firstNameFocusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) { firstNameFocusRequester.requestFocus() }
                SharedOutlinedTextField(
                    value = newUser.firstname,
                    onValueChange = { onFirstNameChange(it) },
                    label = stringResource(R.string.first_name),
                    isError = newUser.firstname.isEmpty(),
                    errorText = stringResource(R.string.login_screen_first_name_error_empty),
                    bottomPadding = SharedPadding.xxl,
                    modifier = Modifier
                        .focusRequester(firstNameFocusRequester)
                        .fillMaxWidth()
                )
                SharedOutlinedTextField(
                    value = newUser.lastname,
                    onValueChange = { onLastNameChange(it) },
                    label = stringResource(R.string.last_name),
                    isError = newUser.lastname.isEmpty(),
                    errorText = stringResource(R.string.login_screen_last_name_error_empty),
                    bottomPadding = SharedPadding.xxl,
                    modifier = Modifier.fillMaxWidth()
                )
                SharedOutlinedPassword(
                    value = newUser.password,
                    onValueChange = { onPasswordChange(it) },
                    label = stringResource(R.string.new_password),
                    errorText = stringResource(R.string.login_screen_password_error_strength),
                    passwordSetting = true,
                    modifier = Modifier.fillMaxWidth(),
                    bottomPadding = SharedPadding.xxl
                )
                SharedButton(
                    onClick = { createAccount(newUser){navigateToHomeScreen()} },
                    text = stringResource(R.string.create_account),
                    modifier = Modifier.padding(vertical = SharedPadding.xxl)
                )
                Spacer(modifier = Modifier.size(300.dp))
            }
        }
    }
}