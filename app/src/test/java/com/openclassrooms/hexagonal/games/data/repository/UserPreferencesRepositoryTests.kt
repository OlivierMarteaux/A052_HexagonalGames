package com.openclassrooms.hexagonal.games.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException
import org.junit.Assert.*

class UserPreferencesRepositoryTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: UserPreferencesRepository

    private val IS_NOTIF_ENABLED = booleanPreferencesKey("is_notifications_enabled")

    @Before
    fun setUp() {
        dataStore = mockk()
        repository = UserPreferencesRepository(dataStore)
    }

    @Test
    fun saveNotificationPreference_WhenCalled_CallsDataStoreEdit() = runTest {
        // Given
        coEvery { dataStore.edit(any()) }

        // When
        repository.saveNotificationPreference(false)

        // Then
        coVerify(exactly = 1) { dataStore.edit(any()) }
    }

    @Test
    fun isNotifEnabled_WhenDataStoreReturnsTrue_EmitsTrue() = runTest {
        // Arrange
        val prefs = mockk<Preferences>()
        every { prefs[IS_NOTIF_ENABLED] } returns true
        every { dataStore.data } returns flowOf(prefs)

        // Act & Assert
        repository.isNotifEnabled.test {
            val value = awaitItem()
            assertTrue(value)
            awaitComplete()
        }
    }

    @Test
    fun isNotifEnabled_WhenDataStoreReturnsFalse_EmitsFalse() = runTest {
        // Arrange
        val prefs = mockk<Preferences>()
        every { prefs[IS_NOTIF_ENABLED] } returns false
        every { dataStore.data } returns flowOf(prefs)

        // Act & Assert
        repository.isNotifEnabled.test {
            val value = awaitItem()
            assertEquals(false, value)
            awaitComplete()
        }
    }

    @Test
    fun isNotifEnabled_WhenIOExceptionOccurs_EmitsDefaultTrue() = runTest {
        // Arrange
        every { dataStore.data } returns flow { throw IOException("Test IO") }

        // Act & Assert
        repository.isNotifEnabled.test {
            val value = awaitItem()
            assertEquals(true, value) // fallback default
            awaitComplete()
        }
    }
}
