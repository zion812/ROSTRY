package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(tableName = "disputes")
data class DisputeEntity(
    @PrimaryKey val disputeId: String = "",
    val transferId: String = "",
    val reporterId: String = "", // User who reported
    val reportedUserId: String = "", // The other party
    val reason: String = "",
    val description: String = "",
    val evidenceUrls: List<String> = emptyList(), // Photos/Videos
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
