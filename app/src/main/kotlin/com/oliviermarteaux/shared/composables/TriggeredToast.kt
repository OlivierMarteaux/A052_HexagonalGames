package com.oliviermarteaux.shared.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TriggeredToast (
    text: String,
    modifier: Modifier = Modifier,
    durationMillis: Long = 3000,
    bottomPadding: Int = 80,
    trigger: Boolean
){
    if (trigger){
        SharedToast(
            text = text,
            durationMillis = durationMillis,
            bottomPadding = bottomPadding,
            modifier = modifier,
        )
    }
}