package com.rio.rostry.core.model

data class Dispute(
    val id: String,
    val transferId: String,
    val reporterId: String,
    val reportedUserId: String,
    val reason: String,
    val description: String,
    val evidenceUrls: List<String> = emptyList(),
    val status: DisputeStatus = DisputeStatus.OPEN,
    val resolution: String? = null,
    val resolvedByAdminId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

enum class DisputeStatus {
    OPEN,
    UNDER_REVIEW,
    RESOLVED_REFUNDED,
    RESOLVED_DISMISSED,
    RESOLVED_WARNING_ISSUED
}
