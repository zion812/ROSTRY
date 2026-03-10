package com.rio.rostry.core.model

/**
 * Domain model representing a batch operation on multiple farm assets.
 */
data class AssetBatchOperation(
    val operationId: String,
    val farmerId: String,
    val operationType: String,
    val selectionCriteria: String,
    val operationData: String,
    val status: String,
    val totalItems: Int,
    val processedItems: Int = 0,
    val successfulItems: Int = 0,
    val failedItems: Int = 0,
    val errorLog: String? = null,
    val canRollback: Boolean = false,
    val rollbackData: String? = null,
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val estimatedDuration: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
