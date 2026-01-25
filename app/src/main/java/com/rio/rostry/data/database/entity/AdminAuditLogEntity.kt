package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = "admin_audit_logs",
    indices = [
        Index(value = ["adminId"]),
        Index(value = ["actionType"]),
        Index(value = ["timestamp"]),
        Index(value = ["targetId"])
    ]
)
data class AdminAuditLogEntity(
    @PrimaryKey val logId: String = "",
    val adminId: String = "",
    val adminName: String? = null,
    val actionType: String = "", // e.g. USER_SUSPEND, PROD_FLAG, ORDER_CANCEL
    val targetId: String? = null, // userId, productId, orderId
    val targetType: String? = null, // "USER", "PRODUCT", "ORDER"
    val details: String? = null, // JSON or plain text reason
    val timestamp: Long = System.currentTimeMillis()
)
