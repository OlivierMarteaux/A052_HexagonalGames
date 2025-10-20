package com.openclassrooms.hexagonal.games.data.service

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.toList
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.*
import org.mockito.Mockito.mockStatic

//@OptIn(ExperimentalCoroutinesApi::class)
//class PostFirebaseApiTest {
//
//    private lateinit var postApi: PostFirebaseApi
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var storage: FirebaseStorage
//    private lateinit var postsCollection: CollectionReference
//    private lateinit var storageRef: StorageReference
//    private lateinit var imageRef: StorageReference
//
//    private val testDispatcher = StandardTestDispatcher()
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//
//        // mockito mock creation
//        firestore = mock()
//        storage = mock()
//        postsCollection = mock()
//        storageRef = mock()
//        imageRef = mock()
//
//        // mockito stubbing methods
//        whenever(firestore.collection("posts")).thenReturn(postsCollection)
//        whenever(storage.reference).thenReturn(storageRef)
//        whenever(storageRef.child(any())).thenReturn(imageRef)
//
//        postApi = PostFirebaseApi(firestore, storage)
//        // Inject mocks manually via reflection if needed for isolated test
//        val postsField = PostFirebaseApi::class.java.getDeclaredField("postsCollection")
//        postsField.isAccessible = true
//        postsField.set(postApi, postsCollection)
//
//        val storageField = PostFirebaseApi::class.java.getDeclaredField("storage")
//        storageField.isAccessible = true
//        storageField.set(postApi, storage)
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
//    // region addPost()
//
//    @Test
//    fun addPost_WhenImageUploadSucceeds_AddsPostSuccessfully() = runTest {
//        val post = Post(id = "1", title = "Hello", photoUrl = "localUri")
//
//        whenever(imageRef.putFile(any())).thenReturn(mock())
//        whenever(imageRef.downloadUrl).thenReturn(Tasks.forResult("http://test.com/image.jpg".toUri()))
//        whenever(postsCollection.add(any())).thenReturn(Tasks.forResult(mock()))
//
//        val result = postApi.addPost(post)
//
//        assertTrue(result.isSuccess)
//        verify(postsCollection).add(check { post: Post ->
//            assertEquals("http://test.com/image.jpg", post.photoUrl)
//        })
//    }

//    @Test
//    fun `addPost should return failure when image upload fails`() = runTest {
//        val post = Post(id = "1", title = "Error", photoUrl = "localUri")
//
//        whenever(imageRef.putFile(any())).thenReturn(Tasks.forException(Exception("Upload failed")))
//
//        val result = postApi.addPost(post)
//
//        assertTrue(result.isFailure)
//        assertEquals("Upload failed", result.exceptionOrNull()?.message)
//    }
//
//    @Test
//    fun `addPost should work with empty image URL`() = runTest {
//        val post = Post(id = "1", title = "No image", photoUrl = "")
//
//        whenever(postsCollection.add(any())).thenReturn(Tasks.forResult(mock()))
//
//        val result = postApi.addPost(post)
//
//        assertTrue(result.isSuccess)
//        verify(postsCollection).add(any())
//    }
//
//    // endregion
//
//    // region addComment()
//
//    @Test
//    fun `addComment should update Firestore successfully`() = runTest {
//        val postId = "123"
//        val comment = Comment()
//
//        val documentRef: DocumentReference = mock()
//        whenever(postsCollection.document(postId)).thenReturn(documentRef)
//        whenever(documentRef.update(eq("comments"), any())).thenReturn(Tasks.forResult(null))
//
//        val result = postApi.addComment(postId, comment)
//
//        assertTrue(result.isSuccess)
//        verify(documentRef).update(eq("comments"), any())
//    }
//
//    @Test
//    fun `addComment should return failure when Firestore update fails`() = runTest {
//        val postId = "123"
//        val comment = Comment()
//
//        val documentRef: DocumentReference = mock()
//        whenever(postsCollection.document(postId)).thenReturn(documentRef)
//        whenever(documentRef.update(eq("comments"), any()))
//            .thenReturn(Tasks.forException(Exception("Permission denied")))
//
//        val result = postApi.addComment(postId, comment)
//
//        assertTrue(result.isFailure)
//        assertEquals("Permission denied", result.exceptionOrNull()?.message)
//    }
//
//    // endregion
//
//    // region getPostsOrderByCreationDateDesc()
//
//    @Test
//    fun `getPostsOrderByCreationDateDesc should emit posts list on success`() = runTest {
//        val snapshot = mock<QuerySnapshot>()
//        val document = mock<DocumentSnapshot>()
//        whenever(document.toObject(Post::class.java)).thenReturn(Post(id = "1", title = "Post"))
//        whenever(snapshot.documents).thenReturn(listOf(document))
//
//        val query = mock<Query>()
//        whenever(postsCollection.orderBy(any<String>(), any())).thenReturn(query)
//
//        val slot = argumentCaptor<EventListener<QuerySnapshot>>()
//        whenever(query.addSnapshotListener(slot.capture())).thenReturn(mock())
//
//        val results = mutableListOf<Result<List<Post>>>()
//        val job = launch {
//            postApi.getPostsOrderByCreationDateDesc().toList(results)
//        }
//
//        slot.firstValue.onEvent(snapshot, null)
//
//        assertTrue(results.first().isSuccess)
//        assertEquals(1, results.first().getOrNull()?.size)
//        job.cancel()
//    }
//
//    @Test
//    fun `getPostsOrderByCreationDateDesc should emit failure on Firestore error`() = runTest {
//        val query = mock<Query>()
//        whenever(postsCollection.orderBy(any<String>(), any())).thenReturn(query)
//        val slot = argumentCaptor<EventListener<QuerySnapshot>>()
//        whenever(query.addSnapshotListener(slot.capture())).thenReturn(mock())
//
//        val results = mutableListOf<Result<List<Post>>>()
//        val job = launch {
//            postApi.getPostsOrderByCreationDateDesc().toList(results)
//        }
//
//        slot.firstValue.onEvent(null,
//            FirebaseFirestoreException("Test error", FirebaseFirestoreException.Code.ABORTED)
//        )
//
//        assertTrue(results.first().isFailure)
//        job.cancel()
//    }

    // endregion
//}