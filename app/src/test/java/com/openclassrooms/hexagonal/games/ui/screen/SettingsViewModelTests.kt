package com.openclassrooms.hexagonal.games.ui.screen

import com.oliviermarteaux.shared.utils.Logger
import com.oliviermarteaux.shared.utils.NoOpLogger
import com.oliviermarteaux.shared.datastore.NotificationPreferencesRepository
import com.oliviermarteaux.shared.test.assertFlagSwitching
import com.openclassrooms.hexagonal.games.MainDispatcherRule
import com.openclassrooms.hexagonal.games.ui.screen.settings.SettingsViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // handles Dispatchers.Main for tests
    private val notificationPreferencesRepository: NotificationPreferencesRepository = mockk()
    private val log: Logger = NoOpLogger
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        settingsViewModel = SettingsViewModel(
            notificationPreferencesRepository = notificationPreferencesRepository,
            log = log
        )
    }

    //_ -------------------------------------------------
    // showNotifStateToast
    // -------------------------------------------------
    @Test
    fun showNotifStateToast_onCall_ToastFlagToggles() = runTest {
        // Given
        assertFalse(settingsViewModel.notifStateToast)
        // When
        settingsViewModel.showNotifStateToast()
        // Then
        assertFlagSwitching { settingsViewModel.notifStateToast }
    }

    //_ ------------------------------------------------------------------------
    // toggleNotifications enables
    // ------------------------------------------------------------------------
    @Test
    fun toggleNotifications_Enable_NotifStateEnabledAndSaved() = runTest {
        // Given
        coEvery { notificationPreferencesRepository.saveNotificationPreference(true) } just Runs
        // When
        settingsViewModel.toggleNotifications(true)
        advanceUntilIdle()
        // Then
        coVerify { notificationPreferencesRepository.saveNotificationPreference(true) }
        assertEquals(true, settingsViewModel.notifState)
    }

    //_ ------------------------------------------------------------------------
    // toggleNotifications disables
    // ------------------------------------------------------------------------
    @Test
    fun toggleNotifications_Disable_NotifStateDisabledAndSaved() = runTest {
        // Given
        coEvery { notificationPreferencesRepository.saveNotificationPreference(false) } just Runs
        // When
        settingsViewModel.toggleNotifications(false)
        advanceUntilIdle()
        // Then
        coVerify { notificationPreferencesRepository.saveNotificationPreference(false) }
        assertEquals(false, settingsViewModel.notifState)
    }
    //_ ------------------------------------------------------------------------
    // showNotifPermissionAlertDialog
    // ------------------------------------------------------------------------
    @Test
    fun showNotifPermissionAlertDialog_SetTrue_AlertDialogFlagTrue() {
        // When
        settingsViewModel.showNotifPermissionAlertDialog(true)
        // Then
        assertTrue(settingsViewModel.notifPermissionAlertDialog)
    }

    @Test
    fun showNotifPermissionAlertDialog_SetFalse_AlertDialogFlagFalse() {
        // When
        settingsViewModel.showNotifPermissionAlertDialog(false)
        // Then
        assertFalse(settingsViewModel.notifPermissionAlertDialog)
    }

    //_ ------------------------------------------------------------------------
    // initial state
    // ------------------------------------------------------------------------
    @Test
    fun settingsViewModel_InitialState_HasDefaults() {
        // Then
        assertFalse(settingsViewModel.notifPermissionAlertDialog)
        assertFalse(settingsViewModel.notifStateToast)
        assertEquals(false, settingsViewModel.notifState)
    }
}