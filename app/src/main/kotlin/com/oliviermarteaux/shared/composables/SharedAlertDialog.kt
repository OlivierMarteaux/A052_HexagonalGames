package com.oliviermarteaux.shared.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun SharedAlertDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    confirmText: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    dismissText: String = "",
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        titleContentColor = titleContentColor,
        text = { Text(text) },
        textContentColor = textContentColor,
        confirmButton = { TextButton(onClick = onConfirm) {Text(confirmText)} },
        containerColor = containerColor,
        dismissButton = { TextButton(onClick = onDismiss) {Text(dismissText)} },
        modifier = modifier,
        icon = icon,
        iconContentColor = iconContentColor,
        shape = shape,
        tonalElevation = tonalElevation,
        properties = properties
    )
}