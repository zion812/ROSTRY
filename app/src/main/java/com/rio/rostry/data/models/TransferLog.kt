package com.rio.rostry.data.models

data class TransferLog(
    val transferId: String,
    val fowlId: String,
    val giverId: String,
    val receiverId: String,
    val timestamp: Long,
    val status: TransferStatus,
    val proofUrls: List<String>, // URLs to proof documents/images
    val createdAt: Long,
    val updatedAt: Long
)

enum class TransferStatus {
    PENDING,
    VERIFIED,
    REJECTED
}