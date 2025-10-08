package com.openclassrooms.hexagonal.games.screen.detail

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository
) : ViewModel() {

    private val postId: String = checkNotNull(savedStateHandle["post_id"])

//    var post: Post by mutableStateOf(Post())
//        private set
    private val _post: MutableStateFlow<Post> = MutableStateFlow(Post())
    val post: StateFlow<Post>
        get() = _post

    fun getPost(postId:String) = postRepository.getPost(postId)

    init {
        viewModelScope.launch {
//            getPost(postId).collect {
//                post = it
//            }
            postRepository.posts.collect { posts ->
                _post.value = posts.find { it.id == postId } !!
                Log.d("OM_TAG", "DetailViewModel post collected: $post")
            }
        }
    }
}