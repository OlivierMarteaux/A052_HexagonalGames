package com.openclassrooms.hexagonal.games.screen.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository
) : ViewModel() {

    private val postId: String = checkNotNull(savedStateHandle["post_id"])

    var post: Post by mutableStateOf(Post())
        private set

    init { viewModelScope.launch { getPost() } }

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
}