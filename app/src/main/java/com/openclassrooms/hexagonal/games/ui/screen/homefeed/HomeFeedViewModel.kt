package com.openclassrooms.hexagonal.games.ui.screen.homefeed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.shared.ui.UiState
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing data and events related to the HomeFeed.
 * This ViewModel retrieves posts from the PostRepository and exposes them as a Flow<List<Post>>,
 * allowing UI components to observe and react to changes in the posts data.
 */
@HiltViewModel
class HomeFeedViewModel @Inject constructor(
  private val postRepository: PostRepository,
  private val userRepository: UserRepository,
  private val log: Logger,
  private val isOnlineFlow: Flow<Boolean>
) : AuthUserViewModel(
  userRepository = userRepository,
  isOnlineFlow = isOnlineFlow,
  log = log,
) {
  var homeFeedUiState: UiState<Post> by mutableStateOf(UiState.Loading)
    private set

  fun loadPosts() {
    viewModelScope.launch {
      homeFeedUiState = UiState.Loading
//      delay(3000) // simulate network delay for Loading state evidence
      postRepository.posts.collect { result ->
        result
          .onSuccess { posts ->
            homeFeedUiState =
              if (posts.isEmpty()) UiState.Empty
              else UiState.Success(posts)
          }
          .onFailure { e ->
            homeFeedUiState = UiState.Error(e)
          }
      }
    }
  }

  init {
//    throw RuntimeException("Test Crash") // Force a crash
    // Fetch posts from the repository
    log.d("HomeFeedViewModel: init")
    loadPosts()
  }
}