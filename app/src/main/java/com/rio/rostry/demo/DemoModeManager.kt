package com.rio.rostry.demo

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoModeManager @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    enum class Environment { LIVE, DEMO }

    data class FeatureFlags(
        val payments: Boolean = true,
        val social: Boolean = true,
        val marketplace: Boolean = true,
        val transfers: Boolean = true,
        val monitoring: Boolean = true,
        val analytics: Boolean = true,
        val location: Boolean = true,
        val offline: Boolean = true,
    )

    private val prefs by lazy { appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    private val _enabled = MutableStateFlow(prefs.getBoolean(KEY_ENABLED, false))
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _flags = MutableStateFlow(
        FeatureFlags(
            payments = prefs.getBoolean(KEY_FLAG_PAYMENTS, true),
            social = prefs.getBoolean(KEY_FLAG_SOCIAL, true),
            marketplace = prefs.getBoolean(KEY_FLAG_MARKETPLACE, true),
            transfers = prefs.getBoolean(KEY_FLAG_TRANSFERS, true),
            monitoring = prefs.getBoolean(KEY_FLAG_MONITORING, true),
            analytics = prefs.getBoolean(KEY_FLAG_ANALYTICS, true),
            location = prefs.getBoolean(KEY_FLAG_LOCATION, true),
            offline = prefs.getBoolean(KEY_FLAG_OFFLINE, true),
        )
    )
    val flags: StateFlow<FeatureFlags> = _flags.asStateFlow()

    private val _environment = MutableStateFlow(
        when (prefs.getString(KEY_ENV, Environment.LIVE.name)) {
            Environment.DEMO.name -> Environment.DEMO
            else -> Environment.LIVE
        }
    )
    val environment: StateFlow<Environment> = _environment.asStateFlow()

    fun isEnabled(): Boolean = _enabled.value

    fun setEnabled(value: Boolean) {
        _enabled.value = value
        prefs.edit().putBoolean(KEY_ENABLED, value).apply()
    }

    fun updateFlags(update: FeatureFlags.() -> FeatureFlags) {
        _flags.value = _flags.value.update().also { f ->
            prefs.edit()
                .putBoolean(KEY_FLAG_PAYMENTS, f.payments)
                .putBoolean(KEY_FLAG_SOCIAL, f.social)
                .putBoolean(KEY_FLAG_MARKETPLACE, f.marketplace)
                .putBoolean(KEY_FLAG_TRANSFERS, f.transfers)
                .putBoolean(KEY_FLAG_MONITORING, f.monitoring)
                .putBoolean(KEY_FLAG_ANALYTICS, f.analytics)
                .putBoolean(KEY_FLAG_LOCATION, f.location)
                .putBoolean(KEY_FLAG_OFFLINE, f.offline)
                .apply()
        }
    }

    fun resetFlags() {
        _flags.value = FeatureFlags()
        prefs.edit()
            .putBoolean(KEY_FLAG_PAYMENTS, true)
            .putBoolean(KEY_FLAG_SOCIAL, true)
            .putBoolean(KEY_FLAG_MARKETPLACE, true)
            .putBoolean(KEY_FLAG_TRANSFERS, true)
            .putBoolean(KEY_FLAG_MONITORING, true)
            .putBoolean(KEY_FLAG_ANALYTICS, true)
            .putBoolean(KEY_FLAG_LOCATION, true)
            .putBoolean(KEY_FLAG_OFFLINE, true)
            .apply()
    }

    fun resetAll() {
        _enabled.value = false
        _flags.value = FeatureFlags()
        _environment.value = Environment.LIVE
        prefs.edit().clear().apply()
    }

    fun setEnvironment(env: Environment) {
        _environment.value = env
        prefs.edit().putString(KEY_ENV, env.name).apply()
        // When switching to DEMO, ensure enabled is true for convenience (can be toggled off by tester)
        if (env == Environment.DEMO && !_enabled.value) {
            setEnabled(true)
        }
    }

    companion object {
        private const val PREFS_NAME = "demo_mode_prefs"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_ENV = "environment"
        private const val KEY_FLAG_PAYMENTS = "flag_payments"
        private const val KEY_FLAG_SOCIAL = "flag_social"
        private const val KEY_FLAG_MARKETPLACE = "flag_marketplace"
        private const val KEY_FLAG_TRANSFERS = "flag_transfers"
        private const val KEY_FLAG_MONITORING = "flag_monitoring"
        private const val KEY_FLAG_ANALYTICS = "flag_analytics"
        private const val KEY_FLAG_LOCATION = "flag_location"
        private const val KEY_FLAG_OFFLINE = "flag_offline"
    }
}
