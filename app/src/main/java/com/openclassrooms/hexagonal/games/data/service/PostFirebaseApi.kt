package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.UUID

class PostFirebaseApi: PostApi {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val postsCollection = firestore.collection("posts")

    /**
     * Retrieves a flow of posts ordered by creation date in descending order.
     * @return A flow emitting a list of posts.
     */
    override fun getPostsOrderByCreationDateDesc(): Flow<Result<List<Post>>> = callbackFlow {
        val listenerRegistration = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> {
                        Log.e("OM_TAG", "Firestore listener error: ${error.message}", error)
                        trySend(Result.failure(error))
                    }

                    snapshot != null -> {
                        val posts = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Post::class.java)?.copy(id = doc.id)
                        }
                        trySend(Result.success(posts))
                    }
                }
            }
        awaitClose { listenerRegistration.remove() }
    }.catch { e ->
        // catches coroutine/flow cancellation or unexpected exceptions
        Log.e("OM_TAG", "Flow exception: ${e.message}", e)
        emit(Result.failure(e))
    }

    /**
     * Adds a new post to the Firestore database.
     * @param post The post to be added.
     */
    override suspend fun addPost(post: Post) {
        try {
            val authState = FirebaseAuth.getInstance().currentUser?.displayName
            Log.d("OM_TAG", "PostFirebaseApi: addPost: authState = $authState")
            //info: Upload image to Firebase Storage if available
            val localPhotoUrl = post.photoUrl
            Log.d("OM_TAG", "PostFirebaseApi: addPost: localPhotoUrl = $localPhotoUrl")
            val firebasePhotoUrl = if (!localPhotoUrl.isNullOrEmpty()) {
                uploadImageToStorage(localPhotoUrl.toUri())
            } else ""
            Log.d("OM_TAG", "PostFirebaseApi: addPost: firebasePhotoUrl = $firebasePhotoUrl")

            //info: Add post to Firestore posts collection with updated image url
            val updatedPost = post.copy(photoUrl = firebasePhotoUrl)
            postsCollection.add(updatedPost).await()
            Log.d("OM_TAG", "PostFirebaseApi: addPost: success")
        } catch (e: Exception) {
            Log.e("OM_TAG", "PostFirebaseApi: addPost: failed due to Exception: ${e.cause}",e)
        }
    }

    override suspend fun addComment(postId: String, comment: Comment) {
        try {
            postsCollection.document(postId)
                .update("comments", FieldValue.arrayUnion(comment))
                .await()
            Log.d("OM_TAG", "PostFirebaseApi: addComment: success")
        } catch (e: Exception) {
            Log.e("OM_TAG", "PostFirebaseApi: addComment: failed: ${e.message}")
        }
    }

    /**
     * Uploads an image to Firebase Storage and returns its download URL.
     */
    private suspend fun uploadImageToStorage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val imageRef = storage.reference.child("posts/${UUID.randomUUID()}.jpg")
            Log.d("OM_TAG", "PostFirebaseApi: uploadImageToStorage: uploading $imageUri")
            imageRef.putFile(imageUri).await() // uploads
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Log.d("OM_TAG", "PostFirebaseApi: uploadImageToStorage: success, url = $downloadUrl")
            return@withContext downloadUrl
        } catch (e: Exception) {
            Log.e("OM_TAG", "PostFirebaseApi: uploadImageToStorage: failed", e)
            throw e
        }
    }
}