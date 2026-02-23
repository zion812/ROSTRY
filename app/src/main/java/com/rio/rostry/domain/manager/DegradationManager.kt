package com.rio.rostry.domain.manager

import kotlinx.coroutines.flow.StateFlow

enum class DegradedService {
    RECOMMENDATION,
    ANALYTICS,
    MEDIA_UPLOAD,
    NOTIFICATIONS,
    NETWORK
}

/**
 * Tracks the degradation state of various backend services to allow
 * the UI to display fallback states or warning banners.
 */
interface DegradationManager {
    val degradedServices: StateFlow<Set<DegradedService>>

    fun reportDegraded(service: DegradedService)
    fun reportRecovered(service: DegradedService)
    fun isDegraded(service: DegradedService): Boolean
}
