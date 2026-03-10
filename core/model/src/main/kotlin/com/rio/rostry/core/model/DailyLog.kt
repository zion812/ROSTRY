package com.rio.rostry.core.model

/**
 * Domain model for a daily log entry.
 */
data class DailyLog(
    val logId: String,
    val productId: String,
    val farmerId: String,
    val logDate: Long,
    val weightGrams: Double? = null,
    val feedKg: Double? = null,
    val medication: List<String>? = null,
    val symptoms: List<String>? = null,
    val activityLevel: String? = null,
    val photoUrls: List<String>? = null,
    val notes: String? = null,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val createdAt: Long,
    val updatedAt: Long
)
