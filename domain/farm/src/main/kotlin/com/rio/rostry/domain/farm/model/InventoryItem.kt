package com.rio.rostry.domain.farm.model

import java.time.Instant

/**
 * Domain model for inventory item.
 * 
 * Phase 2: Domain and Data Decoupling
 * Represents allocatable stock derived from farm assets (ADR-004 3-tier model).
 */
data class InventoryItem(
    val id: String,
    val farmAssetId: String,
    val farmerId: String,
    val quantity: Int,
    val unit: String,
    val harvestDate: Instant?,
    val storageLocation: String?,
    val qualityGrade: QualityGrade,
    val expiryDate: Instant?,
    val availableQuantity: Int,
    val reservedQuantity: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * Quality grade of inventory item.
 */
enum class QualityGrade {
    PREMIUM,
    STANDARD,
    ECONOMY,
    UNGRADED
}
