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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class PostFirebaseApi: PostApi {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val postsCollection = firestore.collection("posts")

    /**
     * Retrieves a flow of posts ordered by creation date in descending order.
     * @return A flow emitting a list of posts.
     */
    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> = callbackFlow {
        try {
            val listener = postsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    Log.d(
                        "OM_TAG",
                        "PostFirebaseApi: getPostsOrderByCreationDateDesc: $postsCollection"
                    )

                    val posts = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Post::class.java)?.copy(id = doc.id)
                    }.orEmpty()
                    trySend(posts)
                }

            awaitClose { listener.remove() }
        } catch (e: Exception) {
            Log.d("OM_TAG", "PostFirebaseApi: getPostsOrderByCreationDateDesc: failed", e)
            throw e
        }
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
        } catch (e: FirebaseFirestoreException) {
            Log.e("OM_TAG", "PostFirebaseApi: addPost: failed due to FirebaseFirestoreException: ${e.message}")
        } catch (e: Exception) {
            Log.e("OM_TAG", "PostFirebaseApi: addPost: failed due to Exception: ${e.message}")
            throw e
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