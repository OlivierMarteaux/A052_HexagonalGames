package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.shared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for the Comment screen.
 *
 * @param savedStateHandle The saved state handle for the view model.
 * @param postRepository The repository for managing posts.
 * @param userRepository The repository for managing user data.
 * @param log The logger.
 * @param isOnlineFlow A flow that emits the current internet connection status.
 * @param dispatchers The coroutine dispatcher provider.
 */
@HiltViewModel
class CommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val log: Logger,
    private val isOnlineFlow: Flow<Boolean>,
    private val dispatchers: CoroutineDispatcherProvider
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log,
) {
    /**
     * The ID of the post to add a comment to.
     */
    private val postId: String = checkNotNull(savedStateHandle["post_id"])
    /**
     * The content of the comment.
     */
    var commentContent: String by mutableStateOf("")
        private set
    /**
     * Updates the content of the comment.
     *
     * @param newComment The new content of the comment.
     */
    fun onCommentChange(newComment: String) {
        commentContent = newComment
    }
    /**
     * Adds a new comment to the post.
     *
     * @param onResult A callback to invoke when the comment is added successfully.
     */
    fun addComment(onResult: () -> Unit = {}) {
        val author = currentUser
        log.d("CommentViewModel: addComment: author = currentUser = $author")

        val comment = Comment(
            author = author?: User(),
            content = commentContent,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch(dispatchers.io) {
            postRepository.addComment(postId, comment).fold(
                onSuccess = { withContext(dispatchers.main) { onResult() } },
                onFailure = { withContext(dispatchers.main) { showUnknownErrorToast() } }
            )
        }
    }
}