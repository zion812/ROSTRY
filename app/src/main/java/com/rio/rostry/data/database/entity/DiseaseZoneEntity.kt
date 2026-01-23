package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.Date

@Keep
@IgnoreExtraProperties
data class DiseaseZoneEntity(
    val zoneId: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Double = 0.0,
    val reason: String = "",
    val severity: ZoneSeverity = ZoneSeverity.WARNING,
    val isActive: Boolean = true,
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val expiresAt: Date? = null
)

enum class ZoneSeverity {
    WARNING,    // Informational only
    RESTRICTED, // Enhanced checks needed
    LOCKDOWN    // No movement allowed
}
