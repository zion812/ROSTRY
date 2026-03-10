package com.rio.rostry.core.model

/**
 * Domain model for a disease zone.
 */
data class DiseaseZone(
    val zoneId: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val reason: String,
    val severity: ZoneSeverity,
    val isActive: Boolean,
    val createdAt: Long
)

/**
 * Severity level for a disease zone.
 */
enum class ZoneSeverity {
    WARNING,
    QUARANTINE,
    LOCKDOWN
}

/**
 * Biosecurity status for a location.
 */
sealed class BiosecurityStatus {
    object Safe : BiosecurityStatus()
    data class Warning(val zones: List<DiseaseZone>) : BiosecurityStatus()
    data class Blocked(val zones: List<DiseaseZone>) : BiosecurityStatus()
}
