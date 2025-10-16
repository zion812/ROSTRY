package com.rio.rostry.utils.network

import javax.inject.Inject
import javax.inject.Singleton
import com.rio.rostry.BuildConfig

@Singleton
class FeatureToggles @Inject constructor(
    private val network: NetworkQualityMonitor
) {
    enum class ImageQuality { LOW, MEDIUM, HIGH }

    fun imageQuality(): ImageQuality = when (network.quality()) {
        NetworkQualityMonitor.Quality.OFFLINE -> ImageQuality.LOW
        NetworkQualityMonitor.Quality.G2 -> ImageQuality.LOW
        NetworkQualityMonitor.Quality.G3 -> ImageQuality.MEDIUM
        NetworkQualityMonitor.Quality.G4, NetworkQualityMonitor.Quality.G5, NetworkQualityMonitor.Quality.WIFI -> ImageQuality.HIGH
    }

    fun autoplayVideos(): Boolean = when (network.quality()) {
        NetworkQualityMonitor.Quality.WIFI, NetworkQualityMonitor.Quality.G5 -> true
        else -> false
    }

    fun prefetchAllowed(): Boolean = when (network.quality()) {
        NetworkQualityMonitor.Quality.WIFI, NetworkQualityMonitor.Quality.G4, NetworkQualityMonitor.Quality.G5 -> true
        else -> false
    }

    // Feature gates (backed by build config for now; can be remote-config driven later)
    fun isDemoEnabled(): Boolean = BuildConfig.DEBUG
    fun isLiveBroadcastEnabled(): Boolean = BuildConfig.DEBUG

    // Routing-related feature toggles
    fun isGroupsEnabled(): Boolean = BuildConfig.DEBUG
    fun isEventsEnabled(): Boolean = true
    fun isExpertBookingEnabled(): Boolean = true
    fun isModerationEnabled(): Boolean = BuildConfig.DEBUG
    fun isLeaderboardEnabled(): Boolean = BuildConfig.DEBUG
    fun isLiveBroadcast(): Boolean = BuildConfig.DEBUG
}
