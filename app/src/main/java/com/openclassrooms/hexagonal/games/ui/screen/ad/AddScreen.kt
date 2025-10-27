package com.openclassrooms.hexagonal.games.ui.screen.ad

import android.R.attr.enabled
import android.R.attr.onClick
import android.R.attr.singleLine
import android.R.attr.text
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oliviermarteaux.localShared.ui.UiState
import com.oliviermarteaux.localShared.ui.theme.SharedPadding
import com.oliviermarteaux.shared.composables.CenteredCircularProgressIndicator
import com.oliviermarteaux.localShared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.SharedToast
import com.oliviermarteaux.shared.composables.sharedImagePicker
import com.oliviermarteaux.shared.ui.theme.SharedShapes
import com.openclassrooms.hexagonal.games.R

/**
 * A screen for adding a new post.
 *
 * @param modifier The modifier to apply to this screen.
 * @param viewModel The view model for this screen.
 * @param navigateBack A function to call to navigate back to the previous screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
  modifier: Modifier = Modifier,
  viewModel: AddViewModel = hiltViewModel(),
  navigateBack: () -> Unit,
) {
  SharedScaffold(
    modifier = modifier,
    title =  stringResource(R.string.add_fragment_label),
    onBackClick = navigateBack
  ) { contentPadding ->
    with(viewModel) {
      val post by post.collectAsStateWithLifecycle()
      val errors by errors.collectAsStateWithLifecycle()

      Box {
        if (addPostUiState is UiState.Loading) CenteredCircularProgressIndicator()
        CreatePost(
          modifier = Modifier
            .padding(contentPadding)
            .padding(SharedPadding.xl)
            .fillMaxSize(),
          errors = errors,
          title = post.title,
          onTitleChanged = { onAction(FormEvent.TitleChanged(it)) },
          description = post.description ?: "",
          onDescriptionChanged = { onAction(FormEvent.DescriptionChanged(it)) },
          onSaveClick = { addPost(navigateBack) },
          onPhotoChanged = { onAction(FormEvent.photoChanged(it)) },
          photoUrl = post.photoUrl
        )
        if(unknownError) SharedToast(text = stringResource(R.string.application_error_unknown))
        if(networkError) SharedToast(
          text = stringResource(R.string.application_error_network),
          bottomPadding = 120
        )
      }
    }
  }
}

/**
 * A composable for creating a post.
 *
 * @param modifier The modifier to apply to this composable.
 * @param title The title of the post.
 * @param onTitleChanged A function to call when the title changes.
 * @param description The description of the post.
 * @param onDescriptionChanged A function to call when the description changes.
 * @param onSaveClick A function to call when the save button is clicked.
 * @param errors A list of form errors.
 * @param photoUrl The URL of the photo.
 * @param onPhotoChanged A function to call when the photo changes.
 */
@Composable
private fun CreatePost(
  modifier: Modifier = Modifier,
  title: String,
  onTitleChanged: (String) -> Unit,
  description: String,
  onDescriptionChanged: (String) -> Unit,
  onSaveClick: () -> Unit,
  errors: List<FormError>?,
  photoUrl: String?,
  onPhotoChanged: (String) -> Unit
) {
  val scrollState = rememberScrollState()

  // Get the ImagePicker launcher
  val imagePickerLauncher = sharedImagePicker { onPhotoChanged(it.toString()) }

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    SharedOutlinedTextField(
      modifier = Modifier.fillMaxWidth(),
      value = title,
      isError = errors?.contains(FormError.TitleError) ?: false,
      onValueChange = { onTitleChanged(it) },
      label = stringResource(id = R.string.hint_title),
      keyboardType = KeyboardType.Text,
      errorText = errors?.find { it is FormError.TitleError }
        ?.let { stringResource(id = it.messageRes) },
      bottomPadding = SharedPadding.xl
    )
    SharedOutlinedTextField(
      modifier = Modifier.fillMaxWidth(),
      value = description,
      isError = photoUrl?.let { false } ?: errors?.contains(FormError.DescriptionError) ?: false,
      onValueChange = { onDescriptionChanged(it) },
      label = stringResource(id = R.string.hint_description),
      keyboardType = KeyboardType.Text,
      errorText = photoUrl ?: errors?.find { it is FormError.DescriptionError }
        ?.let { stringResource(id = it.messageRes) },
      bottomPadding = SharedPadding.xl
    )
    //_ IMAGE PICKER -------------------------------------
    SharedAsyncImage(
      photoUri = photoUrl,
      imageModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(ratio = 4 / 3f),
      isError = photoUrl?.let { false } ?: errors?.contains(FormError.DescriptionError) ?: false,
      errorText = stringResource(R.string.invalid_photo),
      bottomPadding = SharedPadding.xl
    )
    SharedButton(text = stringResource(R.string.select_a_photo)) {
      imagePickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }
    val photoError:Boolean = photoUrl?.let { false } ?: errors?.contains(FormError.DescriptionError) ?: false
    SharedButton(
      text = stringResource(R.string.action_save),
      enabled = !photoError && title.isNotBlank(),
      onClick = onSaveClick
    )
  }
}