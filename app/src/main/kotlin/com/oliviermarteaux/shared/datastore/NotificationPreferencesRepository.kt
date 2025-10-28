package com.oliviermarteaux.shared.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.oliviermarteaux.shared.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for managing user preferences.
 *
 * @param dataStore The DataStore instance for storing preferences.
 */
@Singleton
class NotificationPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val log: Logger,
){
    /**
     * A companion object to hold the keys for the preferences.
     */
    private companion object {
        /**
         * The key for the notification preference.
         */
        val IS_NOTIF_ENABLED = booleanPreferencesKey("is_notifications_enabled")
    }

    /**
     * Saves the notification preference.
     *
     * @param isNotifEnabled Whether notifications are enabled.
     */
    suspend fun saveNotificationPreference(isNotifEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_NOTIF_ENABLED] = isNotifEnabled
        }
    }

    /**
     * A flow that emits the current state of the notification preference.
     * It defaults to `true` if the preference is not set.
     */
    val isNotifEnabled: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                log.e("UserPreferencesRepository: isNotifEnabled: Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map{ preferences ->
            preferences[IS_NOTIF_ENABLED] ?: true
        }
}