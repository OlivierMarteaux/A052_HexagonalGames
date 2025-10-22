package com.openclassrooms.hexagonal.games.ui.screen.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.localShared.utils.Logger
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.ui.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val postId: String = checkNotNull(savedStateHandle["post_id"])

    var post: Post by mutableStateOf(Post())
        private set

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