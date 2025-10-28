package com.openclassrooms.hexagonal.games.ui.screen.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Detail screen.
 *
 * @param savedStateHandle The saved state handle for the view model.
 * @param postRepository The repository for managing posts.
 * @param userRepository The repository for managing user data.
 * @param log The logger.
 * @param isOnlineFlow A flow that emits the current internet connection status.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log,
) {
    /**
     * The ID of the post to display.
     */
    private val postId: String = checkNotNull(savedStateHandle["post_id"])

    /**
     * The post to display.
     */
    var post: Post by mutableStateOf(Post())
        private set

    /**
     * Gets the post from the repository.
     */
    suspend fun getPost(){
        postRepository.posts.collect { result ->
            result.fold(
                onSuccess = { posts ->
                    post = posts.find { it.id == postId } !!
                    log.d("DetailViewModel getPost(): $post")
                },
                onFailure = { e ->
                    log.e("DetailViewModel: getPost(): error: $e.message")
                }
            )
        }
    }

    init { viewModelScope.launch { getPost() } }
}