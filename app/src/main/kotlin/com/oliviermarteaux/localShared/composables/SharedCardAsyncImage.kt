package com.oliviermarteaux.localShared.composables

//import android.content.Context
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CardElevation
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.DefaultAlpha
//import androidx.compose.ui.graphics.FilterQuality
//import androidx.compose.ui.graphics.Shape
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.painter.ColorPainter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//import coil3.compose.AsyncImagePainter
//import coil3.compose.AsyncImagePainter.State.Empty.painter
//import coil3.compose.rememberAsyncImagePainter
//import coil3.request.ImageRequest
//import coil3.request.crossfade
//import coil3.request.error
//import coil3.request.fallback
//import coil3.request.placeholder
//import coil3.size.Scale
//import com.oliviermarteaux.localShared.ui.theme.SharedPadding
//import com.oliviermarteaux.shared.composables.SupportingText
//import com.oliviermarteaux.shared.compose.R
//import com.oliviermarteaux.shared.ui.theme.SharedShapes
//
///**
// * Displays an image from a photo URI with placeholder and error handling.
// *
// * @param photoUri The URI string of the image to display. May be null.
// * @param modifier Optional [Modifier] for styling the image.
// */
//@Composable
//fun SharedAsyncImage(
//    photoUri: String?,
//    modifier: Modifier = Modifier,
//    crossfade: Boolean =  true,
//    contentScale: ContentScale = ContentScale.Crop,
//    contentDescription: String? = null,
//    placeholder: Int = R.drawable.placeholder,
//    error: Int = R.drawable.placeholder,
//    fallback: Int = R.drawable.placeholder,
//    context: Context = LocalContext.current,
//    alignment: Alignment = Alignment.Center,
//    alpha: Float = DefaultAlpha,
//    colorFilter: ColorFilter? = null,
//    supportingText: String? = null,
//    errorText: String? = null,
//    isError: Boolean = false,
//    bottomPadding: Dp = 0.dp,
//    elevation: CardElevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
//    imageModifier: Modifier = Modifier,
//    border: BorderStroke? = null,
//    shape: Shape = CardDefaults.elevatedShape,
//    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
//    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
//    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
//    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
//    clipToBounds: Boolean = false
//    ){
////    val context = context
////    val imagePainter = rememberAsyncImagePainter(
////        model = ImageRequest.Builder(context)
////            .data(photoUri)
////            .crossfade(crossfade)
////            .placeholder(placeholder) // shown while loading
////            .error(error)       // shown on error
////            .fallback(fallback)    // shown if `photoUri` is null
////            .build()
////    )
//    SupportingText(
//        supportingText = supportingText,
//        errorText = errorText,
//        isError = isError,
//        bottomPadding = bottomPadding
//    ){
//        Card(
//            shape = shape,
//            border = border,
//            modifier = modifier,
//            elevation = elevation
//        ) {
//            AsyncImage(
//                model = photoUri,
//                contentDescription = contentDescription,
//                modifier = imageModifier,
//                placeholder = painterResource(placeholder),
//                error = painterResource(error),
//                fallback = painterResource(fallback),
//                onLoading = onLoading,
//                onSuccess = onSuccess,
//                onError = onError,
//                alignment = alignment,
//                contentScale = contentScale,
//                alpha = alpha,
//                colorFilter = colorFilter,
//                filterQuality = filterQuality,
//                clipToBounds = clipToBounds
//            )
////            Image(
////                painter = imagePainter,
////                contentScale = contentScale,
////                modifier = imageModifier,
////                contentDescription = contentDescription,
////                alignment = alignment,
////                alpha = alpha,
////                colorFilter = colorFilter
////            )
//        }
//    }
//}