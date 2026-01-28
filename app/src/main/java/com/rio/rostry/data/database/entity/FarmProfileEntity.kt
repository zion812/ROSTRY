package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * FarmProfileEntity represents the public-facing farm profile.
 * This is the "Glass Box" - making farm operations transparent to build trust.
 * 
 * Synced to Firestore for public discovery by buyers.
 */
@Keep
@Entity(
    tableName = "farm_profiles",
    indices = [
        Index(value = ["isVerified"]),
        Index(value = ["province"]),
        Index(value = ["trustScore"])
    ]
)
data class FarmProfileEntity(
    @PrimaryKey val farmerId: String = "",
    
    // Identity
    val farmName: String = "",
    val farmBio: String? = null,
    val logoUrl: String? = null,
    val coverPhotoUrl: String? = null,
    
    // Location
    val locationName: String? = null,      // "Batangas, Philippines"
    val barangay: String? = null,
    val municipality: String? = null,
    val province: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Verification & Trust
    val isVerified: Boolean = false,
    val verifiedAt: Long? = null,
    val memberSince: Long = System.currentTimeMillis(),  // Account creation date
    val farmEstablished: Long? = null,      // When farm actually started
    
    // Trust Metrics (Pre-computed by LifecycleWorker)
    val trustScore: Int = 0,                // 0-100
    val totalBirdsSold: Int = 0,
    val totalOrdersCompleted: Int = 0,
    val avgResponseTimeMinutes: Int? = null,
    val vaccinationRate: Int? = null,       // 0-100%
    val returningBuyerRate: Int? = null,    // 0-100%
    
    // Badges (JSON array of earned badges)
    val badgesJson: String = "[]",          // ["VERIFIED", "FULLY_VACCINATED", "5_STAR_SELLER"]
    
    // Contact
    val whatsappNumber: String? = null,
    val isWhatsappEnabled: Boolean = true,
    val isCallEnabled: Boolean = true,
    
    // Privacy Settings
    val isPublic: Boolean = true,           // Global profile visibility
    val showLocation: Boolean = true,
    val showSalesHistory: Boolean = true,
    val showTimeline: Boolean = true,
    val shareVaccinationLogs: Boolean = true,
    val shareSanitationLogs: Boolean = true,
    val shareFeedLogs: Boolean = false,
    val shareWeightData: Boolean = true,
    val shareSalesActivity: Boolean = true,
    val shareMortalityData: Boolean = false,
    val shareExpenseData: Boolean = false,
    
    // Sync
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dirty: Boolean = true
) {
    // No-arg constructor for Firestore
    constructor() : this(farmerId = "")

    class StringListConverter {
        @androidx.room.TypeConverter
        fun fromStringList(value: List<String>?): String? {
            return value?.let { com.google.gson.Gson().toJson(it) }
        }

        @androidx.room.TypeConverter
        fun toStringList(value: String?): List<String>? {
            val listType = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
            return value?.let { com.google.gson.Gson().fromJson(it, listType) }
        }
    }
}
