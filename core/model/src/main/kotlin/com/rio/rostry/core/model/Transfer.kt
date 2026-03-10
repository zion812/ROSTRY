package com.rio.rostry.core.model

/**
 * Domain model representing an asset transfer between users.
 * 
 * Represents the ownership transfer of farm assets with verification and tracking.
 */
data class Transfer(
    val transferId: String,
    val productId: String?,
    val fromUserId: String?,
    val toUserId: String?,
    val orderId: String?,
    val amount: Double,
    val currency: String,
    val type: String,
    val status: String,
    val transferCode: String?,
    val transferCodeExpiresAt: Long?,
    val transferType: String?,
    val lineageSnapshotJson: String?,
    val healthSnapshotJson: String?,
    val sellerPhotoUrl: String?,
    val buyerPhotoUrl: String?,
    val gpsLat: Double?,
    val gpsLng: Double?,
    val timeoutAt: Long?,
    val conditionsJson: String?,
    val transactionReference: String?,
    val notes: String?,
    val initiatedAt: Long,
    val completedAt: Long?,
    val updatedAt: Long,
    val lastModifiedAt: Long,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false
)

/**
 * Transfer analytics data for reporting and insights.
 */
data class TransferAnalytics(
    val totalTransfers: Int,
    val completedTransfers: Int,
    val pendingTransfers: Int,
    val cancelledTransfers: Int,
    val totalValue: Double,
    val averageTransferValue: Double,
    val period: String,
    val transfersByStatus: Map<String, Int>,
    val transfersByType: Map<String, Int>
)
