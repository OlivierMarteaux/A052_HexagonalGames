package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import coil.util.CoilUtils.result
import com.openclassrooms.hexagonal.games.data.service.PostApi
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class provides a repository for accessing and managing Post data.
 * It utilizes dependency injection to retrieve a PostApi instance for interacting
 * with the data source. The class is marked as a Singleton using @Singleton annotation,
 * ensuring there's only one instance throughout the application.
 */
@Singleton
class PostRepository @Inject constructor(
  private val postApi: PostApi,
//  private val ioScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
  
  /**
   * Retrieves a Flow object containing a list of Posts ordered by creation date
   * in descending order.
   *
   * @return Flow containing a list of Posts.
   */
  val posts: Flow<List<Post>> = postApi.getPostsOrderByCreationDateDesc()
  
  /**
   * Adds a new Post to the data source using the injected PostApi.
   *
   * @param post The Post object to be added.
   */
  suspend fun addPost(post: Post?) {
      try {
        postApi.addPost(post!!)
        Log.d("OM_TAG", "PostRepository: addPost: success")
      } catch (e: Exception) {
        Log.e("OM_TAG", "PostRepository: addPost: failed with following error:", e)
      }
  }
}
