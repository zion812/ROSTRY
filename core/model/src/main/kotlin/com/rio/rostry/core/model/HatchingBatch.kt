package com.rio.rostry.core.model

/**
 * Domain model for hatching batches.
 * 
 * Phase 2: Domain and Data Decoupling
 * Requirement 4.1 - Domain models are framework-independent
 */
data class HatchingBatch(
    val id: String,
    val batchName: String,
    val startDate: Long,
    val expectedHatchDate: Long,
    val eggCount: Int,
    val status: String,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
