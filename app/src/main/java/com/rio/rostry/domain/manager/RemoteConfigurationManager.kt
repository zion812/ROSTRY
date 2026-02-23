package com.rio.rostry.domain.manager

import com.rio.rostry.data.database.dao.ConfigurationCacheDao
import com.rio.rostry.data.database.entity.ConfigurationCacheEntity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configuration manager backed by Firebase Remote Config, with local caching
 * and secure defaults. Priority: Remote → Cache → Defaults.
 */
@Singleton
class RemoteConfigurationManager @Inject constructor(
    private val configCacheDao: ConfigurationCacheDao,
    private val gson: Gson
) : ConfigurationManager {

    private val _configFlow = MutableStateFlow(ConfigurationDefaults.DEFAULT)
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
                if (validation is ConfigValidationResult.Valid) {
                    cacheConfiguration(remote)
                    _configFlow.value = remote
                    lastRefreshTime = System.currentTimeMillis()
                    return Result.success(remote)
                } else {
                    Timber.w("Remote config invalid: ${(validation as ConfigValidationResult.Invalid).errors}")
                }
            }

            // 2. Try cache
            val cached = loadFromCache()
            if (cached != null) {
                Timber.d("Using cached configuration")
                _configFlow.value = cached
                return Result.success(cached)
            }

            // 3. Use defaults
            Timber.d("Using default configuration")
            Result.success(ConfigurationDefaults.DEFAULT)
        } catch (e: Exception) {
            Timber.e(e, "Failed to load configuration, using defaults")
            Result.success(ConfigurationDefaults.DEFAULT)
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

    override fun validate(config: AppConfiguration): ConfigValidationResult {
        return ConfigurationValidator.validate(config)
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
            Timber.w(e, "Failed to fetch remote configuration")
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
            Timber.w(e, "Failed to load cached configuration")
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
            Timber.w(e, "Failed to cache configuration")
        }
    }
}
