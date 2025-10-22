package com.openclassrooms.hexagonal.games.ui.screen.ad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.ui.UiState
import com.oliviermarteaux.localShared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class AddViewModel @Inject constructor(
  private val postRepository: PostRepository,
  private val userRepository: UserRepository,
  private val isOnlineFlow: Flow<Boolean>,
  private val log: Logger
): AuthUserViewModel(
  userRepository = userRepository,
  isOnlineFlow = isOnlineFlow,
  log = log
) {
  var addPostUiState: UiState<Unit> by mutableStateOf(UiState.Idle)
    private set

  /**
   * Internal mutable state flow representing the current post being edited.
   */
  private var _post = MutableStateFlow(
    Post(
      id = UUID.randomUUID().toString(),
      title = "",
      description = "",
      photoUrl = null,
      timestamp = System.currentTimeMillis(),
      author = null
    )
  )
  /**
   * Public state flow representing the current post being edited.
   * This is immutable for consumers.
   */
  val post: StateFlow<Post>
    get() = _post
  /**
   * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
   */
  val errors: StateFlow<List<FormError>?>  = post.map {
    verifyPost()
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = null,
  )
  /**
   * Handles form events like title and description changes.
   *
   * @param formEvent The form event to be processed.
   */
  fun onAction(formEvent: FormEvent) {
    when (formEvent) {
      is FormEvent.DescriptionChanged -> {
        _post.value = _post.value.copy(
          description = formEvent.description
        )
      }

      is FormEvent.TitleChanged -> {
        _post.value = _post.value.copy(
          title = formEvent.title
        )
      }

      is FormEvent.photoChanged -> {
        _post.value = _post.value.copy(
          photoUrl = formEvent.photoUrl
        )
      }
    }
  }

  /**
   * Attempts to add the current post to the repository after setting the author.
   *
   * TODO: Implement logic to retrieve the current user.
   */
  fun addPost(onResult: () -> Unit) {
    addPostUiState = UiState.Loading
    if(!isOnline) {
      showNetworkErrorToast()
      addPostUiState = UiState.Idle // 🟢 reset state since we're not adding the post
      return
    }
    //_ add the post to the repository
    viewModelScope.launch(Dispatchers.IO) {
//      delay(3000) // simulate network delay for Loading state evidence
      postRepository.addPost(_post.value.copy(author = currentUser)).fold(
        onSuccess = { withContext(Dispatchers.Main) { onResult() } },
        onFailure = { showUnknownErrorToast() }
      )
      addPostUiState = UiState.Idle
    }
  }
  /**
   * Verifies mandatory fields of the post
   * and returns a corresponding FormError if so.
   *
   * @return A FormError.TitleError if title is empty, null otherwise.
   */
  private fun verifyPost(): List<FormError> {
    val errors = mutableListOf<FormError>()
    if (_post.value.title.isBlank()) {errors.add(FormError.TitleError)}
    if (_post.value.description.isNullOrBlank()) {errors.add(FormError.DescriptionError)}
    return errors
  }
}