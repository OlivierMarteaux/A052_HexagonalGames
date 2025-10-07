package com.oliviermarteaux.shared.composables

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.oliviermarteaux.shared.utils.copyImageToInternalStorage

/**
 * Composable returning a launcher for picking images using the system photo picker.
 *
 * This launcher supports single image selection and invokes [onImagePicked] with the picked URI.
 *
 * @param onImagePicked Callback invoked with the URI of the picked image, or null if none selected.
 * @return A [ManagedActivityResultLauncher] for launching the photo picker.
 */
@Composable
fun sharedImagePicker(
    onImagePicked: (Uri) -> Unit
): ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?> {

    // Registers a photo picker activity launcher in single-select mode.
    val context = LocalContext.current
    val pickMedia =
        rememberLauncherForActivityResult(PickVisualMedia()) { pickedUri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            pickedUri?.let { uri ->
                Log.d("OM_TAG", "VitesseImagePicker: picked URI: $uri")
                val persistentUri = copyImageToInternalStorage(context, uri)
                persistentUri?.let { onImagePicked(it) }
                Log.d("OM_TAG", "VitesseImagePicker: Persistent URI: $persistentUri")
            } ?: Log.d("OM_TAG", "VitesseImagePicker: No media selected")
        }
    return pickMedia
}