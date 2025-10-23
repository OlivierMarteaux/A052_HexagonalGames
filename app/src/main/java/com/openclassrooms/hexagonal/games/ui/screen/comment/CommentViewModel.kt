package com.openclassrooms.hexagonal.games.ui.screen.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.CoroutineDispatcherProvider
import com.oliviermarteaux.localShared.utils.Logger
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
    private val postId: String = checkNotNull(savedStateHandle["post_id"])
    var commentContent: String by mutableStateOf("")
        private set
    fun onCommentChange(newComment: String) {
        commentContent = newComment
    }
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