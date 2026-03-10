package com.rio.rostry.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private object Keys {
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val LOW_DATA_MODE = booleanPreferencesKey("low_data_mode")
        val THEME = stringPreferencesKey("app_theme") // "system", "light", "dark"
        val LANGUAGE = stringPreferencesKey("app_language") // "en", "hi", "te"
    }

    data class SettingsState(
        val notificationsEnabled: Boolean = true,
        val lowDataMode: Boolean = false,
        val theme: String = "system",
        val language: String = "en"
    )

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            context.settingsDataStore.data.collect { prefs ->
                _state.value = SettingsState(
                    notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
                    lowDataMode = prefs[Keys.LOW_DATA_MODE] ?: false,
                    theme = prefs[Keys.THEME] ?: "system",
                    language = prefs[Keys.LANGUAGE] ?: "en"
                )
            }
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            context.settingsDataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
        }
    }

    fun setLowDataMode(enabled: Boolean) {
        viewModelScope.launch {
            context.settingsDataStore.edit { it[Keys.LOW_DATA_MODE] = enabled }
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            context.settingsDataStore.edit { it[Keys.THEME] = theme }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            context.settingsDataStore.edit { it[Keys.LANGUAGE] = language }
        }
    }
}
