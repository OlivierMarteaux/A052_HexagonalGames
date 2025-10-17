package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val IS_NOTIF_ENABLED = booleanPreferencesKey("is_notifications_enabled")
    }
    suspend fun saveNotificationPreference(isNotifEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_NOTIF_ENABLED] = isNotifEnabled
        }
    }
    val isNotifEnabled: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e("TAG_OM", "UserPreferencesRepository: isNotifEnabled: Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map{ preferences ->
            preferences[IS_NOTIF_ENABLED] ?: true
        }
}