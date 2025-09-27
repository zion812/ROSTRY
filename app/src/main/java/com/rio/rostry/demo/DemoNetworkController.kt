package com.rio.rostry.demo

import com.rio.rostry.utils.network.NetworkQualityMonitor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoNetworkController @Inject constructor(
    private val network: NetworkQualityMonitor,
    private val demoMode: DemoModeManager
) {
    fun goOffline() {
        if (demoMode.isEnabled()) {
            network.forceOnlineState(false)
            network.forceQuality(NetworkQualityMonitor.Quality.OFFLINE)
        }
    }

    fun goOnlineWifi() {
        if (demoMode.isEnabled()) {
            network.forceOnlineState(true)
            network.forceQuality(NetworkQualityMonitor.Quality.WIFI)
        }
    }

    fun simulateQuality(quality: NetworkQualityMonitor.Quality) {
        if (demoMode.isEnabled()) {
            network.forceOnlineState(quality != NetworkQualityMonitor.Quality.OFFLINE)
            network.forceQuality(quality)
        }
    }

    fun clearOverrides() {
        network.forceOnlineState(null)
        network.forceQuality(null)
    }
}
