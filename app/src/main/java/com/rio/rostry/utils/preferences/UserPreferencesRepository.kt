package com.rio.rostry.utils.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val PUSH_NOTIFICATIONS_KEY = booleanPreferencesKey("push_notifications")
    private val WEEKLY_DIGEST_KEY = booleanPreferencesKey("weekly_digest")
    private val LOCATION_OFFERS_KEY = booleanPreferencesKey("location_offers")

    fun getPreferences(): Flow<Map<String, Boolean>> = dataStore.data.map { prefs ->
        mapOf(
            PreferenceKeys.NOTIFICATIONS to (prefs[PUSH_NOTIFICATIONS_KEY] ?: true),
            PreferenceKeys.WEEKLY_DIGEST to (prefs[WEEKLY_DIGEST_KEY] ?: true),
            PreferenceKeys.LOCATION_OFFERS to (prefs[LOCATION_OFFERS_KEY] ?: false)
        )
    }

    suspend fun savePreference(key: String, enabled: Boolean): Boolean {
        return try {
            val internalKey = when (key) {
                PreferenceKeys.NOTIFICATIONS -> PUSH_NOTIFICATIONS_KEY
                PreferenceKeys.WEEKLY_DIGEST -> WEEKLY_DIGEST_KEY
                PreferenceKeys.LOCATION_OFFERS -> LOCATION_OFFERS_KEY
                else -> throw IllegalArgumentException("Unknown preference key: $key")
            }
            dataStore.edit { it[internalKey] = enabled }
            true
        } catch (e: Exception) {
            Log.e("UserPreferencesRepository", "Error saving preference $key", e)
            false
        }
    }

    suspend fun getPreference(key: String): Boolean {
        return try {
            val internalKey = when (key) {
                PreferenceKeys.NOTIFICATIONS -> PUSH_NOTIFICATIONS_KEY
                PreferenceKeys.WEEKLY_DIGEST -> WEEKLY_DIGEST_KEY
                PreferenceKeys.LOCATION_OFFERS -> LOCATION_OFFERS_KEY
                else -> throw IllegalArgumentException("Unknown preference key: $key")
            }
            val default = when (key) {
                PreferenceKeys.NOTIFICATIONS, PreferenceKeys.WEEKLY_DIGEST -> true
                PreferenceKeys.LOCATION_OFFERS -> false
                else -> false
            }
            dataStore.data.first()[internalKey] ?: default
        } catch (e: Exception) {
            Log.e("UserPreferencesRepository", "Error getting preference $key", e)
            when (key) {
                PreferenceKeys.NOTIFICATIONS, PreferenceKeys.WEEKLY_DIGEST -> true
                PreferenceKeys.LOCATION_OFFERS -> false
                else -> false
            }
        }
    }

    @Deprecated("Use getPreference instead")
    fun isFarmNotificationEnabled(type: String): Boolean = true
}
