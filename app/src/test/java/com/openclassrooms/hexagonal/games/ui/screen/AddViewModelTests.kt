package com.openclassrooms.hexagonal.games.ui.screen

import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.ui.screen.ad.AddViewModel
import com.openclassrooms.hexagonal.games.ui.screen.ad.FormEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var addViewModel: AddViewModel
    private lateinit var postRepository: PostRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        postRepository = mockk()
        addViewModel = AddViewModel(postRepository)
    }

    // ----------------------------
    // onAction Tests
    // ----------------------------

    @Test
    fun onAction_TitleChanged_UpdatesPostTitle() = runTest {
        // Given
        val newTitle = "New Post Title"
        // When
        addViewModel.onAction(FormEvent.TitleChanged(newTitle))
        // Then
        assertEquals(newTitle, addViewModel.post.value.title)
    }

    @Test
    fun onAction_DescriptionChanged_UpdatesPostDescription() = runTest {
        // Given
        val newDescription = "Some description"
        // When
        addViewModel.onAction(FormEvent.DescriptionChanged(newDescription))
        // Then
        assertEquals(newDescription, addViewModel.post.value.description)
    }

    @Test
    fun onAction_photoChanged_UpdatesPostPhotoUrl() = runTest {
        // Given
        val newPhotoUrl = "http://example.com/photo.png"
        // When
        addViewModel.onAction(FormEvent.photoChanged(newPhotoUrl))
        // Then
        assertEquals(newPhotoUrl, addViewModel.post.value.photoUrl)
    }

    // ----------------------------
    // addPost Tests
    // ----------------------------

    @Test
    fun addPost_onSuccess_CallsOnResult() = runTest {
        // Given
        coEvery { postRepository.addPost(any()) } returns Result.success(Unit)
        var onResultCalled = false
        // When
        addViewModel.addPost { onResultCalled = true }
        // advance coroutines
        advanceUntilIdle()
        // Then
        assertTrue(onResultCalled)
        assertFalse(addViewModel.unknownError)
        coVerify { postRepository.addPost(any()) }
    }
}
