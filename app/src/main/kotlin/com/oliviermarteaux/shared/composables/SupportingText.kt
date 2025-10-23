package com.oliviermarteaux.shared.composables

import android.R.attr.enabled
import android.R.attr.fontWeight
import android.R.attr.label
import android.R.attr.lineHeight
import android.R.attr.maxLines
import android.R.attr.minLines
import android.R.attr.singleLine
import android.R.attr.text
import android.R.attr.textStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SupportingText(
    supportingText: String? = null,
    errorText: String? = null,
    isError: Boolean = false,
    bottomPadding: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    Column {
        content()
        Box {
            if (supportingText != null || errorText != null) {
                Text(
                    text = if (isError) errorText ?: "" else supportingText ?: "",
                    color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                )
            }
            Spacer(Modifier.size(bottomPadding))
        }
    }
}