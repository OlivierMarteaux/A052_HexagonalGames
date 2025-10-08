package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class PostFirebaseApi: PostApi {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val postsCollection = firestore.collection("posts")

    override fun getPost(postId: String): Flow<Post> = callbackFlow {
        val listenerRegistration = postsCollection
            .whereEqualTo("id", postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val document = snapshot?.documents?.firstOrNull()
                val post = document?.toObject(Post::class.java)
                if (post != null) {
                    trySend(post).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                Log.d("OM_TAG", "PostFirebaseApi: getPostsOrderByCreationDateDesc: $postsCollection")

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }.orEmpty()
                Log.d("OM_TAG", "PostFirebaseApi: getPostsOrderByCreationDateDesc: $posts")

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addPost(post: Post) {
        try {
            val localPhotoUrl = post.photoUrl
            Log.d("OM_TAG", "PostFirebaseApi: addPost: localPhotoUrl = $localPhotoUrl")
            // Upload image if available
            val firebasePhotoUrl = if (!localPhotoUrl.isNullOrEmpty()) {
                try {
                    uploadImageToStorage(localPhotoUrl.toUri())
                } catch (e: CancellationException) {
                    Log.e("OM_TAG", "PostFirebaseApi: addPost: Upload was cancelled from uploadImageToStorage(), likely due to scope destruction ")
                    throw e // let coroutine cancel normally
                } catch (e: Exception) {
                    Log.e("OM_TAG", "PostFirebaseApi: addPost:  Failed to upload image from uploadImageToStorage()", e)
                    ""
                }
            } else ""
//        val firebasePhotoUrl = localPhotoUrl?.let{
//            val uri = uploadImageToStorage(it.toUri())
//            Log.d("OM_TAG", "PostFirebaseApi: addPost: uploadImageToStorage = $uri")
//            uri
//        }
            Log.d("OM_TAG", "PostFirebaseApi: addPost: firebasePhotoUrl = $firebasePhotoUrl")
//        val newPost = post.copy(photoUrl = firebasePhotoUrl)
            val newPost = mapOf(
                "id" to post.id,
                "title" to post.title,
                "description" to post.description,
                "photoUrl" to firebasePhotoUrl,
                "timestamp" to post.timestamp,
                "author" to mapOf(
                    "id" to post.author?.id,
                    "firstname" to post.author?.firstname,
                    "lastname" to post.author?.lastname,
                    "email" to post.author?.email
                ),
                "comments" to post.comments.map { comment ->
                    mapOf(
                        "author" to mapOf(
                            "id" to comment.author.id,
                            "firstname" to comment.author.firstname,
                            "lastname" to comment.author.lastname,
                        ),
                        "content" to comment.content
                    )
                }
            )
            // Add to Firestore
            postsCollection.add(newPost).await()
            Log.d("OM_TAG", "PostFirebaseApi: addPost: success")
        } catch (e: Exception) {
            Log.e("OM_TAG", "PostFirebaseApi: addPost: failed", e)
            throw e
        }
    }

    /**
     * Uploads an image to Firebase Storage and returns its download URL.
     */
    private suspend fun uploadImageToStorage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val imageRef = storage.reference.child("posts/${UUID.randomUUID()}.jpg")
        Log.d("OM_TAG", "PostFirebaseApi: uploadImageToStorage: uploading $imageUri")

        imageRef.putFile(imageUri).await() // uploads
        val downloadUrl = imageRef.downloadUrl.await().toString()
        Log.d("OM_TAG", "PostFirebaseApi: uploadImageToStorage: success, url = $downloadUrl")

        return@withContext downloadUrl
    }
}