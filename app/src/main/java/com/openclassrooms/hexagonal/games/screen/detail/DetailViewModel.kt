package com.openclassrooms.hexagonal.games.screen.detail

import android.R.attr.duration
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.utils.isOnline
import com.oliviermarteaux.utils.TOAST_DURATION
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.data.repository.UserRepository
import com.openclassrooms.hexagonal.games.domain.mapper.toUser
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import com.openclassrooms.hexagonal.games.screen.AuthUserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context
) : AuthUserViewModel(userRepository, context) {
//) : ViewModel() {

//    private var _userDisconnected = MutableSharedFlow<Unit>()
//    val userDisconnected = _userDisconnected.asSharedFlow()
//    var userDisconnected: Boolean by mutableStateOf(false)

    private val postId: String = checkNotNull(savedStateHandle["post_id"])

//    var currentUser: User? by mutableStateOf(null)
//        private set
//    var notConnectedToast: Boolean by mutableStateOf(false)
//        private set
    var post: Post by mutableStateOf(Post())
        private set

    suspend fun getPost(){
        postRepository.posts.collect { result ->
            result.fold(
                onSuccess = { posts ->
                    post = posts.find { it.id == postId } !!
                    Log.d("OM_TAG", "DetailViewModel getPost(): $post")
                },
                onFailure = { e ->
                    Log.e("OM_TAG", "DetailViewModel: getPost(): error: $e.message")
                }
            )
        }
    }
//    fun showNotConnectedToast(duration: Long = TOAST_DURATION){
//        viewModelScope.launch {
//            notConnectedToast = true
//            Log.d("OM_TAG", "DetailViewModel: showNotConnectedToast = true")
//            delay(duration)
//            notConnectedToast = false
//            Log.d("OM_TAG", "DetailViewModel: showNotConnectedToast = false")
//        }
//    }
//    fun onAddCommentClick(
//        onUserLogged: () -> Unit,
//        onNoUserLogged: () -> Unit
//    ) {
////    val currentUser: User? = userRepository.currentUser
//        currentUser?.let {
//            Log.d("OM_TAG", "DetailViewModel: onAddCommentClick: currentUser = ${currentUser?.email}")
//            onUserLogged()
//        }?: run {
//            Log.d("OM_TAG", "DetailViewModel: onAddCommentClick: no user logged")
//            onNoUserLogged()
//        }
//    }
//    private fun observeUserState() {
//        viewModelScope.launch {
//            userRepository.userAuthState.collect { user ->
//                currentUser = user?.toUser()
//                Log.d("OM_TAG", "DetailViewModel observeUserState(): $currentUser")
//            }
//        }
//    }

    init {
        viewModelScope.launch { getPost() }
//        observeUserState()
        viewModelScope.launch {
            Log.d("OM_TAG", "DetailViewModel: init(): start 10s countdown")
            delay(10000)
            userRepository.signOut()
            Log.d("OM_TAG", "DetailViewModel: signOut()")
        }
    }
}