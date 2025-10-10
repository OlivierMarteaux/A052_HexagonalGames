package com.openclassrooms.hexagonal.games.screen.homefeed

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.ui.UiState
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
  private val userRepository: UserRepository
) :
  ViewModel() {

  var homeFeedUiState: UiState<Post> by mutableStateOf(UiState.Loading)
    private set

  var currentUser: User? = userRepository.currentUser
  var showLogToast: Boolean by mutableStateOf(false)
    private set

  fun onFabClick(onUserLogged: () -> Unit, onNoUserLogged: () -> Unit) {
    if (userRepository.currentUser != null) {
      onUserLogged()
    } else {
      onNoUserLogged()
    }
  }

  fun showLogToast(duration: Long = TOAST_DURATION) {
    viewModelScope.launch {
      showLogToast = true
      delay(duration)
      showLogToast = false
    }
  }

  fun onAccountClick(
    onUserLogged: () -> Unit,
    onNoUserLogged: () -> Unit
    ) {
    val currentUser: User? = userRepository.currentUser
    currentUser?.let {
      Log.d("OM_TAG", "HomeFeedViewModel: onAccountClick: currentUser = $currentUser")
      onUserLogged()
    }?: run {
      Log.d("OM_TAG", "HomeFeedViewModel: onAccountClick: no user logged")
      onNoUserLogged()
    }
  }
  
  init {
    // Fetch posts from the repository
    viewModelScope.launch {
      homeFeedUiState = UiState.Loading
//      delay(3000)
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
}