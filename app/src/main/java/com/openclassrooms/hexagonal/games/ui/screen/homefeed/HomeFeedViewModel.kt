package com.openclassrooms.hexagonal.games.ui.screen.homefeed

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.Logger
import com.oliviermarteaux.shared.ui.UiState
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
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
//  ViewModel() {

//  var currentUser: User? = userRepository.currentUser
//    private set
//  var currentUser: User? by mutableStateOf(null)
//    private set
  var homeFeedUiState: UiState<Post> by mutableStateOf(UiState.Loading)
    private set
  var showLogToast: Boolean by mutableStateOf(false)
    private set
//  var userDisconnected: Boolean by mutableStateOf(false)
//    private set

//  private fun observeUserState() {
//    viewModelScope.launch {
//      userRepository.userAuthState.collect { user ->
//        currentUser = user?.toUser()
//        Log.d("OM_TAG", "DetailViewModel observeUserState(): $currentUser")
//      }
//    }
//  }
//  fun onAuthUserClick(
//    onUserLogged: () -> Unit,
//    onNoUserLogged: () -> Unit
//    ) {
////    val currentUser: User? = userRepository.currentUser
//    currentUser?.let {
//      Log.d("OM_TAG", "HomeFeedViewModel: onAuthUserClick: currentUser = $currentUser")
//      onUserLogged()
//    }?: run {
//      Log.d("OM_TAG", "HomeFeedViewModel: onAuthUserClick: no user logged")
//      onNoUserLogged()
//    }
//  }
  fun showLogToast(duration: Long = TOAST_DURATION) {
    viewModelScope.launch {
      showLogToast = true
      Log.d("OM_TAG", "HomeFeedViewModel: showLogToast = true")
      delay(duration)
      showLogToast = false
      Log.d("OM_TAG", "HomeFeedViewModel: showLogToast = false")
    }
  }

  init {
    // Fetch posts from the repository
//    observeUserState()
    Log.d("OM_TAG", "HomeFeedViewModel: init")
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