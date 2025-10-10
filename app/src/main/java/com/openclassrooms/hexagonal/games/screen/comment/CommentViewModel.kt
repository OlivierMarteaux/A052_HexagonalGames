package com.openclassrooms.hexagonal.games.screen.comment

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val postId: String = checkNotNull(savedStateHandle["post_id"])
    var commentContent: String by mutableStateOf("")
    private set

    fun onCommentChange(newComment: String) {
        commentContent = newComment
    }

    fun addComment(onResult: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val author = userRepository.currentUser
                Log.d("OM_TAG", "CommentViewModel: addComment: author = currentUser = $author")

                val comment = Comment(
                    author = author?: User(),
                    content = commentContent,
                    timestamp = System.currentTimeMillis()
                )

                postRepository.addComment(postId, comment)
                Log.d("OM_TAG", "CommentViewModel: addComment: success")
            } catch (e: Exception) {
                Log.e("OM_TAG", "CommentViewModel: addComment: failed", e)
            } finally {
                withContext(Dispatchers.Main) {
                    onResult()
                }
            }
        }
    }
}