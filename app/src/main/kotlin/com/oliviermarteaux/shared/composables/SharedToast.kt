package com.oliviermarteaux.shared.composables

import android.R.attr.text
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun SharedToast(
    text: String,
    modifier: Modifier = Modifier,
    durationMillis: Long = 5000,
    bottomPadding: Int = 80
) {
    var visible by remember { mutableStateOf(true) }

    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = modifier
                    .wrapContentWidth()
                    .animateContentSize()
                    .padding(8.dp),
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp
                )
            }
        }

        // Auto dismiss after durationMillis
        LaunchedEffect(Unit) {
            delay(durationMillis)
            visible = false
        }
    }
}