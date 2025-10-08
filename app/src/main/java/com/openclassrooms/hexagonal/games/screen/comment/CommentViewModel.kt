package com.openclassrooms.hexagonal.games.screen.comment

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
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
    private val postRepository: PostRepository
): ViewModel() {

    private val postId: String = checkNotNull(savedStateHandle["post_id"])
    var commentContent: String by mutableStateOf("")
    private set

    fun onCommentChange(newComment: String) {
        commentContent = newComment
    }

//    fun addComment(onResult: () -> Unit) {
//        //TODO : retrieve the current user
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                postRepository.addComment(postId, comment)
//                Log.d("OM_TAG", "CommentViewModel: addComment: success")
//            } catch (e: Exception) {
//                Log.e("OM_TAG", "CommentViewModel: addComment: failed with following error:", e)
//            }
//            finally {
//                withContext(Dispatchers.Main) {onResult()}
//            }
//        }
//    }

    fun addComment(onResult: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val author = User(
                    id = currentUser?.uid ?: "",
                    firstname = currentUser?.displayName?.split(" ")?.firstOrNull() ?: "",
                    lastname = currentUser?.displayName?.split(" ")?.getOrNull(1) ?: "",
                    email = currentUser?.email ?: ""
                )

                val comment = Comment(
                    author = author,
                    content = commentContent
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