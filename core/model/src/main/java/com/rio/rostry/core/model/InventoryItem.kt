package com.rio.rostry.core.model

/**
 * Inventory item model representing harvested assets.
 */
data class InventoryItem(
    val id: String,
    val farmAssetId: String,
    val farmerId: String,
    val quantity: Double = 1.0,
    val unit: String = "piece",
    val harvestDate: Long? = null,
    val storageLocation: String? = null,
    val qualityGrade: QualityGrade = QualityGrade.STANDARD,
    val expiryDate: Long? = null,
    val availableQuantity: Double = 1.0,
    val reservedQuantity: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class QualityGrade {
    PREMIUM,
    STANDARD,
    ECONOMY
}
