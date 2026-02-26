package com.rio.rostry.domain.manager

import android.util.Log
import com.rio.rostry.data.database.dao.ConfigurationCacheDao
import com.rio.rostry.data.database.entity.ConfigurationCacheEntity
import com.rio.rostry.domain.config.AppConfiguration
import com.rio.rostry.domain.config.ConfigurationDefaults
import com.rio.rostry.domain.config.ConfigurationManager
import com.rio.rostry.domain.config.ValidationResult
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "RemoteConfigMgr"

/**
 * Configuration manager backed by Firebase Remote Config, with local caching
 * and secure defaults. Priority: Remote → Cache → Defaults.
 */
@Singleton
class RemoteConfigurationManager @Inject constructor(
    private val configCacheDao: ConfigurationCacheDao,
    private val gson: Gson
) : ConfigurationManager {

    private val _configFlow = MutableStateFlow(ConfigurationDefaults.DEFAULT_CONFIGURATION)
    private var lastRefreshTime: Long = 0L

    companion object {
        private const val REFRESH_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
        private const val CONFIG_KEY = "app_configuration"
    }

    override suspend fun load(): Result<AppConfiguration> {
        return try {
            // 1. Try remote
            val remote = fetchFromRemote()
            if (remote != null) {
                val validation = validate(remote)
                if (validation is ValidationResult.Valid) {
                    cacheConfiguration(remote)
                    _configFlow.value = remote
                    lastRefreshTime = System.currentTimeMillis()
                    return Result.success(remote)
                } else {
                    Log.w(TAG, "Remote config invalid: ${(validation as ValidationResult.Invalid).errors}")
                }
            }

            // 2. Try cache
            val cached = loadFromCache()
            if (cached != null) {
                Log.d(TAG, "Using cached configuration")
                _configFlow.value = cached
                return Result.success(cached)
            }

            // 3. Use defaults
            Log.d(TAG, "Using default configuration")
            _configFlow.value = ConfigurationDefaults.DEFAULT_CONFIGURATION
            Result.success(ConfigurationDefaults.DEFAULT_CONFIGURATION)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load configuration, using defaults", e)
            _configFlow.value = ConfigurationDefaults.DEFAULT_CONFIGURATION
            Result.success(ConfigurationDefaults.DEFAULT_CONFIGURATION)
        }
    }

    override suspend fun refresh(): Result<Unit> {
        val now = System.currentTimeMillis()
        if (now - lastRefreshTime < REFRESH_INTERVAL_MS) {
            return Result.success(Unit) // Too soon to refresh
        }
        return try {
            load()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun get(): AppConfiguration = _configFlow.value

    override fun validate(config: AppConfiguration): ValidationResult {
        val configValidator = com.rio.rostry.domain.manager.ConfigurationValidator
        val managerResult = configValidator.validate(
            com.rio.rostry.domain.manager.AppConfiguration(
                thresholds = com.rio.rostry.domain.manager.ThresholdConfig(
                    storageQuotaMB = config.thresholds.storageQuotaMB,
                    maxBatchSize = config.thresholds.maxBatchSize,
                    circuitBreakerFailureRate = config.thresholds.circuitBreakerFailureRate,
                    hubCapacity = config.thresholds.hubCapacity,
                    deliveryRadiusKm = config.thresholds.deliveryRadiusKm
                ),
                timeouts = com.rio.rostry.domain.manager.TimeoutConfig(
                    networkRequestSeconds = config.timeouts.networkRequestSeconds,
                    circuitBreakerOpenSeconds = config.timeouts.circuitBreakerOpenSeconds,
                    retryDelaysSeconds = config.timeouts.retryDelaysSeconds
                ),
                security = com.rio.rostry.domain.manager.SecurityConfig(
                    adminIdentifiers = config.security.adminIdentifiers,
                    allowedFileTypes = config.security.allowedFileTypes
                )
            )
        )
        return when (managerResult) {
            is com.rio.rostry.domain.manager.ConfigValidationResult.Valid -> ValidationResult.Valid
            is com.rio.rostry.domain.manager.ConfigValidationResult.Invalid -> ValidationResult.Invalid(
                managerResult.errors.map { com.rio.rostry.domain.config.ValidationError("config", it, "VALIDATION") }
            )
        }
    }

    override fun observe(): Flow<AppConfiguration> = _configFlow.asStateFlow()

    private suspend fun fetchFromRemote(): AppConfiguration? {
        return try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(300) // 5 minutes
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings).await()
            remoteConfig.fetchAndActivate().await()

            val json = remoteConfig.getString(CONFIG_KEY)
            if (json.isNotBlank()) {
                gson.fromJson(json, AppConfiguration::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch remote configuration", e)
            null
        }
    }

    private suspend fun loadFromCache(): AppConfiguration? {
        return try {
            val entity = configCacheDao.get(CONFIG_KEY)
            if (entity != null) {
                gson.fromJson(entity.value, AppConfiguration::class.java)
            } else null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load cached configuration", e)
            null
        }
    }

    private suspend fun cacheConfiguration(config: AppConfiguration) {
        try {
            val entity = ConfigurationCacheEntity(
                key = CONFIG_KEY,
                value = gson.toJson(config),
                valueType = "AppConfiguration",
                lastUpdated = System.currentTimeMillis(),
                source = "REMOTE"
            )
            configCacheDao.upsert(entity)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to cache configuration", e)
        }
    }
}
