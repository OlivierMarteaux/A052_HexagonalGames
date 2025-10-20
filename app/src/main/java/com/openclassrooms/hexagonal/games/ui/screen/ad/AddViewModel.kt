package com.openclassrooms.hexagonal.games.ui.screen.ad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
class AddViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {
  
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

  var unknownError: Boolean by mutableStateOf(false)
    private set

  
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
    //TODO : retrieve the current user
    //_ 2 parallel coroutines:
    //_ coroutine 1: add the post to the repository
    viewModelScope.launch(Dispatchers.IO) {
      postRepository.addPost(
        _post.value.copy(
          author = User("1", "Gerry", "Ariella", "ariella.gerry@gmail.com")
        )
      ).fold(
        onSuccess = { withContext(Dispatchers.Main) { onResult() } },
        onFailure = { unknownError = true }
      )
    }
    //_ coroutine 2: Max delay before coroutine cancellation
    // (network timeout or unknown error)
    viewModelScope.launch(Dispatchers.Main) {
      delay(3000)
      onResult()
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
