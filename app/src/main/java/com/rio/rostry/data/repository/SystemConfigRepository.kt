package com.rio.rostry.data.repository

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

interface SystemConfigRepository {
    fun getBoolean(key: String, default: Boolean): Flow<Boolean>
    suspend fun setBoolean(key: String, value: Boolean)
    
    fun getString(key: String, default: String): Flow<String>
    suspend fun setString(key: String, value: String)
}

@Singleton
class SystemConfigRepositoryImpl @Inject constructor(
    @Named("system_config_prefs") private val prefs: SharedPreferences
) : SystemConfigRepository {

    override fun getBoolean(key: String, default: Boolean): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, k ->
            if (key == k) {
                trySend(sharedPreferences.getBoolean(key, default))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getBoolean(key, default))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getString(key: String, default: String): Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, k ->
            if (key == k) {
                trySend(sharedPreferences.getString(key, default) ?: default)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(key, default) ?: default)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}
