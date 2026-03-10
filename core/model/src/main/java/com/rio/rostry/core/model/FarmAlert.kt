package com.rio.rostry.core.model

/**
 * Domain model for farm alerts and notifications.
 */
data class FarmAlert(
    val alertId: String = "",
    val farmerId: String = "",
    val alertType: String = "",
    val severity: String = "",
    val message: String = "",
    val actionRoute: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)
