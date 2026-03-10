package com.rio.rostry.domain.farm.model

import java.time.Instant

/**
 * Domain model for farm asset.
 * 
 * Phase 2: Domain and Data Decoupling
 * Represents biological/physical assets in production (ADR-004 3-tier model).
 */
data class FarmAsset(
    val id: String,
    val farmerId: String,
    val assetType: AssetType,
    val birthDate: Instant?,
    val breed: String?,
    val gender: String?,
    val healthStatus: HealthStatus,
    val location: String?,
    val biologicalData: Map<String, Any>?,
    val lifecycleStage: LifecycleStage,
    val parentMaleId: String?,
    val parentFemaleId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * Type of farm asset.
 */
enum class AssetType {
    BIRD,
    BATCH,
    EQUIPMENT,
    FEED,
    MEDICATION
}

/**
 * Health status of a farm asset.
 */
enum class HealthStatus {
    HEALTHY,
    SICK,
    QUARANTINED,
    DECEASED,
    UNKNOWN
}

/**
 * Lifecycle stage of a farm asset.
 */
enum class LifecycleStage {
    CHICK,
    JUVENILE,
    ADULT,
    BREEDING,
    RETIRED,
    HARVESTED
}
