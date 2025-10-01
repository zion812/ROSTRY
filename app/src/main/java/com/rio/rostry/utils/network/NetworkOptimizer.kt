package com.rio.rostry.utils.network

/**
 * Network efficiency helpers: request batching hints, adaptive quality toggles, and caching hints.
 * Pure scaffolding; wire into networking stack (Retrofit/OkHttp/Firestore) in app modules.
 */
object NetworkOptimizer {
    /** Determine if batching should be enabled given network quality assessment. */
    fun shouldBatchRequests(quality: NetworkQuality): Boolean = when (quality) {
        NetworkQuality.POOR -> true
        NetworkQuality.MODERATE -> true
        NetworkQuality.GOOD -> false
        NetworkQuality.EXCELLENT -> false
    }

    /** Suggest image/network quality to save bandwidth on poor networks. */
    fun suggestedQuality(quality: NetworkQuality): QualityProfile = when (quality) {
        NetworkQuality.POOR -> QualityProfile(lowBandwidth = true, imageQuality = 0.5f)
        NetworkQuality.MODERATE -> QualityProfile(lowBandwidth = true, imageQuality = 0.7f)
        NetworkQuality.GOOD -> QualityProfile(lowBandwidth = false, imageQuality = 0.85f)
        NetworkQuality.EXCELLENT -> QualityProfile(lowBandwidth = false, imageQuality = 1.0f)
    }

    /** Simple backoff strategy for retries (in milliseconds). */
    fun backoffForAttempt(attempt: Int, baseMs: Long = 500L, maxMs: Long = 8000L): Long =
        (baseMs * (1 shl attempt)).coerceAtMost(maxMs)
}

enum class NetworkQuality { POOR, MODERATE, GOOD, EXCELLENT }

data class QualityProfile(val lowBandwidth: Boolean, val imageQuality: Float)
