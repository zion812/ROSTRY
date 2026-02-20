package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset_batch_operations")
data class AssetBatchOperationEntity(
    @PrimaryKey val operationId: String,
    val farmerId: String,
    val operationType: String, // UPDATE, DELETE, MOVE, TREAT
    val selectionCriteria: String, // JSON criteria used to select assets
    val operationData: String, // JSON data for the operation
    val status: String, // PENDING, RUNNING, PAUSED, COMPLETED, FAILED, CANCELLED
    val totalItems: Int,
    val processedItems: Int = 0,
    val successfulItems: Int = 0,
    val failedItems: Int = 0,
    val errorLog: String?, // JSON array of errors
    val canRollback: Boolean = false,
    val rollbackData: String?, // JSON data needed for rollback
    val startedAt: Long?,
    val completedAt: Long?,
    val estimatedDuration: Long?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
