package com.rio.rostry.domain.model

import java.util.Date

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // order, payment, system
    val isRead: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)