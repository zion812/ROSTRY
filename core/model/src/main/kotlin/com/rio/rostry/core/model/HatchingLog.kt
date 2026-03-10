package com.rio.rostry.core.model

/**
 * Domain model for hatching log entries.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class HatchingLog(
    val id: String,
    val batchId: String,
    val date: Long,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val notes: String? = null,
    val createdAt: Long
)
