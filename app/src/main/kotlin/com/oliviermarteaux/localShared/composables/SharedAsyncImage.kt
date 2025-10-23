package com.oliviermarteaux.localShared.composables

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.fallback
import coil3.request.placeholder
import com.oliviermarteaux.shared.composables.SupportingText
import com.oliviermarteaux.shared.compose.R

/**
 * Displays an image from a photo URI with placeholder and error handling.
 *
 * @param photoUri The URI string of the image to display. May be null.
 * @param modifier Optional [Modifier] for styling the image.
 */
@Composable
fun SharedAsyncImage(
    photoUri: String?,
    modifier: Modifier = Modifier,
    crossfade: Boolean =  true,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    placeholder: Int = R.drawable.placeholder,
    error: Int = R.drawable.placeholder,
    fallback: Int = R.drawable.placeholder,
    context: Context = LocalContext.current,
    alignment: Alignment = Alignment.Center,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    supportingText: String? = null,
    errorText: String? = null,
    isError: Boolean = false,
    bottomPadding: Dp = 0.dp
){
    val context = context
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(photoUri)
            .crossfade(crossfade)
            .placeholder(placeholder) // shown while loading
            .error(error)       // shown on error
            .fallback(fallback)    // shown if `photoUri` is null
            .build()
    )
    SupportingText(
        supportingText = supportingText,
        errorText = errorText,
        isError = isError,
        bottomPadding = bottomPadding
    ){
        Image(
            painter = imagePainter,
            contentScale = contentScale,
            modifier = modifier,
            contentDescription = contentDescription,
            alignment = alignment,
            alpha = alpha,
            colorFilter = colorFilter
        )
    }
}