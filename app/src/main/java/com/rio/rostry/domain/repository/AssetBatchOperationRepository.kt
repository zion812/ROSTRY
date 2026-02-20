package com.rio.rostry.domain.repository

import com.rio.rostry.data.database.entity.AssetBatchOperationEntity
import kotlinx.coroutines.flow.Flow

interface AssetBatchOperationRepository {
    suspend fun createOperation(operation: AssetBatchOperationEntity)
    suspend fun updateOperation(operation: AssetBatchOperationEntity)
    suspend fun getOperationById(operationId: String): AssetBatchOperationEntity?
    suspend fun getOperationsByFarmer(farmerId: String): List<AssetBatchOperationEntity>
}
