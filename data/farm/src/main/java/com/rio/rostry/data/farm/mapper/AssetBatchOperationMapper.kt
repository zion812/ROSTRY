package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.AssetBatchOperation
import com.rio.rostry.data.database.entity.AssetBatchOperationEntity

fun AssetBatchOperationEntity.toAssetBatchOperation(): AssetBatchOperation {
    return AssetBatchOperation(
        operationId = this.operationId,
        farmerId = this.farmerId,
        operationType = this.operationType,
        selectionCriteria = this.selectionCriteria,
        operationData = this.operationData,
        status = this.status,
        totalItems = this.totalItems,
        processedItems = this.processedItems,
        successfulItems = this.successfulItems,
        failedItems = this.failedItems,
        errorLog = this.errorLog,
        canRollback = this.canRollback,
        rollbackData = this.rollbackData,
        startedAt = this.startedAt,
        completedAt = this.completedAt,
        estimatedDuration = this.estimatedDuration,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun AssetBatchOperation.toEntity(): AssetBatchOperationEntity {
    return AssetBatchOperationEntity(
        operationId = this.operationId,
        farmerId = this.farmerId,
        operationType = this.operationType,
        selectionCriteria = this.selectionCriteria,
        operationData = this.operationData,
        status = this.status,
        totalItems = this.totalItems,
        processedItems = this.processedItems,
        successfulItems = this.successfulItems,
        failedItems = this.failedItems,
        errorLog = this.errorLog,
        canRollback = this.canRollback,
        rollbackData = this.rollbackData,
        startedAt = this.startedAt,
        completedAt = this.completedAt,
        estimatedDuration = this.estimatedDuration,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
