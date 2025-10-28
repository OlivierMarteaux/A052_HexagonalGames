package com.oliviermarteaux.shared.composables

//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.LocalContentColor
//import androidx.compose.material3.LocalTextStyle
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldColors
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import com.oliviermarteaux.shared.extensions.isHardEnough
//
//
//@Composable
//fun SharedOutlinedPassword(
//    //_ text field params
//    value: String,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    readOnly: Boolean = false,
//    textStyle: TextStyle = LocalTextStyle.current,
//    label: String,
//    leadingIcon: @Composable (() -> Unit)? = null,
//    trailingIcon: @Composable (() -> Unit)? = null,
//    prefix: @Composable (() -> Unit)? = null,
//    suffix: @Composable (() -> Unit)? = null,
//    supportingText: String? = null,
//    isError: Boolean? = null,
//    visualTransformation: VisualTransformation = PasswordVisualTransformation(),
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    singleLine: Boolean = true,
//    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
//    minLines: Int = 1,
//    interactionSource: MutableInteractionSource? = null,
//    shape: Shape = OutlinedTextFieldDefaults.shape,
//    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
//    imeAction: ImeAction = ImeAction.Done,
//    keyboardType: KeyboardType = KeyboardType.Password,
//    minLength: Int = 6,
//    passwordSetting:Boolean = false,
//    //_ icon params
//    icon: ImageVector? = null,
//    iconModifier: Modifier = Modifier,
//    contentDescription: String? = null,
//    tint: Color = LocalContentColor.current,
//    bottomPadding: Dp = 0.dp,
//    errorText: String? = null,
//    onValueChange: (String) -> Unit,
//){
//    val defaultError = !value.run {isNotEmpty() && isHardEnough(minLength)}
//    val error = if (passwordSetting) isError?:defaultError else false
//
//    SupportingText(
//        supportingText = supportingText,
//        errorText = errorText,
//        isError = error,
//        bottomPadding = bottomPadding
//    ) {
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            modifier = modifier,
//            enabled = enabled,
//            readOnly = readOnly,
//            textStyle = textStyle,
//            label = { Text(label) },
//            placeholder = { Text(label) },
//            leadingIcon = leadingIcon,
//            trailingIcon = trailingIcon,
//            prefix = prefix,
//            suffix = suffix,
//            supportingText = null,
//            isError = error,
//            visualTransformation = visualTransformation,
//            keyboardActions = keyboardActions,
//            singleLine = singleLine,
//            maxLines = maxLines,
//            minLines = minLines,
//            interactionSource = interactionSource,
//            shape = shape,
//            colors = colors,
//            keyboardOptions = KeyboardOptions(
//                imeAction = imeAction,
//                keyboardType = keyboardType
//            )
//        )
//    }
//}