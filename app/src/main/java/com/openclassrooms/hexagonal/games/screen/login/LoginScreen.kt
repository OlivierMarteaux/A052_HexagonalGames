package com.openclassrooms.hexagonal.games.screen.login

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.oliviermarteaux.shared.composables.SharedIcon
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.NewUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToPasswordScreen: () -> Unit = {},
    onBackClick: () -> Unit = {},
    loginViewModel: LoginViewModel = hiltViewModel()
    ){

    val newUser = loginViewModel.newUser
    val emailExist = loginViewModel.emailExist

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.login_screen_label))
                }
            )
        },
    ) { contentPadding ->
        LoginBody(
            newUser = newUser,
            emailExist = emailExist,
            modifier = modifier.padding(contentPadding),
            onEmailChange = loginViewModel::onEmailChange,
            onFirstNameChange = loginViewModel::onFirstNameChange,
            onLastNameChange = loginViewModel::onLastNameChange,
            onPasswordChange = loginViewModel::onPasswordChange,
            navigateToPasswordScreen = navigateToPasswordScreen,
            createAccount = loginViewModel::createAccount,
            checkEmail = loginViewModel::checkEmail
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
    navigateToPasswordScreen: () -> Unit = {},
    createAccount: (NewUser) -> Unit = {},
    checkEmail: (String) -> Unit = {}
){
    Column(modifier = modifier){
        SharedOutlinedTextField(
            value = newUser.email,
            onValueChange = { onEmailChange(it) },
            label = stringResource(R.string.email),
            keyboardType = KeyboardType.Email,
            keyboardActions = KeyboardActions(
                onNext = {
                    checkEmail(newUser.email)
                    if (emailExist == true) navigateToPasswordScreen() }
            )
        )
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
                    label = stringResource(R.string.password),
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

@Composable
fun SharedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    text: String = "",
){
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        Text(text)
    }
}

@Composable
fun SharedOutlinedTextField(
    /*text field params*/
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorText: String = "null",
    /* icon params */
    icon: ImageVector? = null,
    iconModifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
){
    Row(
        modifier = modifier.padding(bottom = 45.dp),
    ){
        icon?.let{ SharedIcon(
            icon = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = iconModifier.padding(top = 14.dp, end = 15.dp))}
            ?: Spacer(Modifier.size(39.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = {Text(label)},
            placeholder = { Text(label) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText?:{ if (isError) Text(errorText) },
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction,
                keyboardType = keyboardType
            ),
        )
    }
}