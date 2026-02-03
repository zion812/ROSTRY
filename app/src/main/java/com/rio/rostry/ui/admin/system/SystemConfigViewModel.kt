package com.rio.rostry.ui.admin.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.SystemConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemConfigViewModel @Inject constructor(
    private val repository: SystemConfigRepository
) : ViewModel() {

    data class ConfigItem(val key: String, val name: String, val sortOrder: Int, val description: String, val value: String, val isBoolean: Boolean = false, val booleanValue: Boolean = false)

    private val _configItems = MutableStateFlow<List<ConfigItem>>(emptyList())
    val configItems = _configItems.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            combine(
                listOf(
                    repository.getString("upload_max_size", "10 MB"),
                    repository.getString("session_timeout", "30 minutes"),
                    repository.getString("password_policy", "Strong (8+ chars)"),
                    repository.getBoolean("notif_email_enabled", true),
                    repository.getBoolean("notif_sms_enabled", true),
                    repository.getBoolean("maintenance_mode", false),
                    repository.getString("api_rate_limit", "100 req/min"),
                    repository.getString("cache_ttl", "5 minutes")
                )
            ) { args ->
                val upload = args[0] as String
                val session = args[1] as String
                val pass = args[2] as String
                val email = args[3] as Boolean
                val sms = args[4] as Boolean
                val maintenance = args[5] as Boolean
                val api = args[6] as String
                val cache = args[7] as String

                listOf(
                    ConfigItem("upload_max_size", "Max Upload Size", 1, "Maximum file size for uploads", upload),
                    ConfigItem("session_timeout", "Session Timeout", 2, "Auto-logout after inactivity", session),
                    ConfigItem("password_policy", "Password Policy", 3, "Password requirements", pass),
                    ConfigItem("notif_email_enabled", "Email Notifications", 4, "System email notifications", if(email) "Enabled" else "Disabled", true, email),
                    ConfigItem("notif_sms_enabled", "SMS Alerts", 5, "Critical SMS alerts", if(sms) "Enabled" else "Disabled", true, sms),
                    ConfigItem("maintenance_mode", "Maintenance Mode", 6, "Put system in maintenance", if(maintenance) "Enabled" else "Disabled", true, maintenance),
                    ConfigItem("api_rate_limit", "API Rate Limit", 7, "Rate limiting per user", api),
                    ConfigItem("cache_ttl", "Cache TTL", 8, "Default cache expiration", cache)
                ).sortedBy { it.sortOrder }
            }.collect {
                _configItems.value = it
            }
        }
    }

    fun updateBoolean(key: String, value: Boolean) {
        viewModelScope.launch {
            repository.setBoolean(key, value)
        }
    }
}
