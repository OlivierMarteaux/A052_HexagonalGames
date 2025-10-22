package com.openclassrooms.hexagonal.games.ui.screen.ad

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oliviermarteaux.localShared.ui.UiState
import com.oliviermarteaux.shared.composables.CenteredCircularProgressIndicator
import com.oliviermarteaux.shared.composables.SharedAsyncImage
import com.oliviermarteaux.shared.composables.SharedButton
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.TriggeredToast
import com.oliviermarteaux.shared.composables.sharedImagePicker
import com.openclassrooms.hexagonal.games.R

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

    val post by viewModel.post.collectAsStateWithLifecycle()
    val errors by viewModel.errors.collectAsStateWithLifecycle()
    val addPostUiState = viewModel.addPostUiState

    Box {
      if (addPostUiState is UiState.Loading) { CenteredCircularProgressIndicator() }
      CreatePost(
        modifier = Modifier.padding(contentPadding),
        errors = errors,
        title = post.title,
        onTitleChanged = { viewModel.onAction(FormEvent.TitleChanged(it)) },
        description = post.description ?: "",
        onDescriptionChanged = { viewModel.onAction(FormEvent.DescriptionChanged(it)) },
        onSaveClick = { viewModel.addPost(navigateBack) },
        onPhotoChanged = { viewModel.onAction(FormEvent.photoChanged(it)) },
        photoUrl = post.photoUrl
      )
      TriggeredToast(
        trigger = viewModel.unknownError,
        text = stringResource(R.string.application_error_unknown)
      )
      TriggeredToast(
        trigger = viewModel.networkError,
        text = stringResource(R.string.application_error_network),
        bottomPadding = 120
      )
    }
  }
}

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
    modifier = modifier
      .padding(16.dp)
      .fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Column(
      modifier = modifier
        .fillMaxSize()
        .weight(1f)
        .verticalScroll(scrollState)
    ) {
      OutlinedTextField(
        modifier = Modifier
          .padding(top = 16.dp)
          .fillMaxWidth(),
        value = title,
        isError = errors?.contains(FormError.TitleError)?:false,
        onValueChange = { onTitleChanged(it) },
        label = { Text(stringResource(id = R.string.hint_title)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true
      )
      errors?.find { it is FormError.TitleError }?.let { error ->
        Text(
          text = stringResource(id = error.messageRes),
          color = MaterialTheme.colorScheme.error,
        )
      }
      OutlinedTextField(
        modifier = Modifier
          .padding(top = 16.dp)
          .fillMaxWidth(),
        value = description,
        isError = photoUrl?.let{false}?:errors?.contains(FormError.DescriptionError)?:false,
        onValueChange = { onDescriptionChanged(it) },
        label = { Text(stringResource(id = R.string.hint_description)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
      )
      photoUrl?:errors?.find { it is FormError.DescriptionError }?.let { error ->
          Text(
            text = stringResource(id = error.messageRes),
            color = MaterialTheme.colorScheme.error,
          )
      }
    }
    //_ IMAGE PICKER -------------------------------------
    photoUrl?.let{ SharedAsyncImage(photoUri = photoUrl) }?:
    if(description.isBlank()){Text(stringResource(R.string.invalid_photo))} else {}
    SharedButton(text = stringResource(R.string.select_a_photo)) {
      imagePickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }
    Button(
      enabled = errors == emptyList<FormError>(),
      onClick = onSaveClick
    ) {
      Text(
        modifier = Modifier.padding(8.dp),
        text = stringResource(id = R.string.action_save)
      )
    }
  }
}