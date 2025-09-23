package com.rio.rostry.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkQualityMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    enum class Quality { G2, G3, G4, G5, WIFI, OFFLINE }

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    @Volatile private var online = false
    @Volatile private var downKbps: Int = 0
    @Volatile private var isWifi = false

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { updateState() }
            override fun onLost(network: Network) { updateState() }
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) { updateState() }
        })
        updateState()
    }

    private fun updateState() {
        val nw = cm.activeNetwork ?: run { online = false; downKbps = 0; isWifi = false; return }
        val caps = cm.getNetworkCapabilities(nw)
        online = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        val down = caps?.linkDownstreamBandwidthKbps ?: 0
        downKbps = down
        isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    fun isOnline(): Boolean = online

    fun quality(): Quality {
        if (!online) return Quality.OFFLINE
        if (isWifi) return Quality.WIFI
        val d = downKbps
        return when {
            d < 100 -> Quality.G2
            d < 1000 -> Quality.G3
            d < 10_000 -> Quality.G4
            else -> Quality.G5
        }
    }
}
