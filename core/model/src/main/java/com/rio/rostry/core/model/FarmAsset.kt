package com.rio.rostry.core.model

/**
 * Farm asset model representing livestock or other farm assets.
 */
data class FarmAsset(
    val id: String,
    val farmerId: String,
    val assetType: AssetType = AssetType.BIRD,
    val birthDate: Long? = null,
    val breed: String? = null,
    val gender: Gender? = null,
    val healthStatus: HealthStatus = HealthStatus.HEALTHY,
    val location: String? = null,
    val biologicalData: Map<String, Any> = emptyMap(),
    val lifecycleStage: String = "ADULT",
    val parentMaleId: String? = null,
    val parentFemaleId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AssetType {
    BIRD,
    BATCH,
    EQUIPMENT,
    BUILDING
}

enum class Gender {
    MALE,
    FEMALE,
    UNKNOWN
}

enum class HealthStatus {
    HEALTHY,
    SICK,
    QUARANTINED,
    DECEASED
}
