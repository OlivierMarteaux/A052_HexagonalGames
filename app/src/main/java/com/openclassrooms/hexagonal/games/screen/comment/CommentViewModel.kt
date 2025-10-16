package com.openclassrooms.hexagonal.games.screen.comment

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.checkInternetConnection
import com.oliviermarteaux.shared.utils.isOnline
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@HiltViewModel
class CommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
//): ViewModel() {
): AuthUserViewModel(userRepository) {

    private val postId: String = checkNotNull(savedStateHandle["post_id"])
    var commentContent: String by mutableStateOf("")
        private set
//    var isOnline: Boolean by mutableStateOf(false)
//        private set
//    val isOnline: Flow<Boolean> = checkInternetConnection(connectivityManager)
    var unknownError: Boolean by mutableStateOf(false)
        private set

    var noInternetToast: Boolean by mutableStateOf(false)
        private set

//    fun onInternetCheckClick(
////        isOnline: Boolean,               // ✅ add this parameter
//        isOnlineAction: () -> Unit,
//        isOfflineAction: () -> Unit
//    ) {
//        if (isOnline) {
//            isOnlineAction()
//        } else {
//            isOfflineAction()
//        }
//    }

//    fun checkInternetConnection(){
//        viewModelScope.launch {
//            checkInternetConnection(connectivityManager).collect {
//                isOnline = it
//            }
//        }
//    }

    fun showNoInternetToast(){
        viewModelScope.launch {
            noInternetToast = true
            delay(TOAST_DURATION)
            noInternetToast = false
        }
    }

    fun onCommentChange(newComment: String) {
        commentContent = newComment
    }

    fun showUnknownErrorToast() = viewModelScope.launch {
        unknownError = true
        delay(TOAST_DURATION)
        unknownError = false
    }

    fun addComment(onResult: () -> Unit = {}) {

        val author = currentUser
        Log.d("OM_TAG", "CommentViewModel: addComment: author = currentUser = $author")

        val comment = Comment(
            author = author?: User(),
            content = commentContent,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch(Dispatchers.IO) {
            postRepository.addComment(postId, comment).fold(
                onSuccess = { withContext(Dispatchers.Main) { onResult() } },
                onFailure = { withContext(Dispatchers.Main) { showUnknownErrorToast() } }
            )
        }
    }

//    init { checkInternetConnection() }
//    fun addComment(onResult: () -> Unit = {}) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val author = userRepository.currentUser
//                Log.d("OM_TAG", "CommentViewModel: addComment: author = currentUser = $author")
//
//                val comment = Comment(
//                    author = author?: User(),
//                    content = commentContent,
//                    timestamp = System.currentTimeMillis()
//                )
//
//                postRepository.addComment(postId, comment)
//                Log.d("OM_TAG", "CommentViewModel: addComment: success")
//            } catch (e: Exception) {
//                Log.e("OM_TAG", "CommentViewModel: addComment: failed", e)
//            } finally {
//                withContext(Dispatchers.Main) {
//                    onResult()
//                }
//            }
//        }
//    }
}