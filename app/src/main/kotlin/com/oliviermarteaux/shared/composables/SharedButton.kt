package com.oliviermarteaux.shared.composables

//import android.R.attr.enabled
//import android.R.attr.onClick
//import android.R.attr.text
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonColors
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.ButtonElevation
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.text.style.TextAlign
//
//@Composable
//fun SharedButton(
//    text: String = "",
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    shape: Shape = ButtonDefaults.shape,
//    colors: ButtonColors = ButtonDefaults.buttonColors(),
//    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
//    border: BorderStroke? = null,
//    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
//    interactionSource: MutableInteractionSource? = null,
//    textAlign: TextAlign = TextAlign.Center,
//    //_ last parameter = Lambda function
//    onClick: () -> Unit
//){
//    Button(
//        onClick = onClick,
//        modifier = modifier,
//        enabled = enabled,
//        shape = shape,
//        colors = colors,
//        elevation = elevation,
//        border = border,
//        contentPadding = contentPadding,
//        interactionSource = interactionSource,
//    ) {
//        Text(
//            text = text,
//            textAlign = textAlign,
//            style = MaterialTheme.typography.labelMedium
//        )
//    }
//}