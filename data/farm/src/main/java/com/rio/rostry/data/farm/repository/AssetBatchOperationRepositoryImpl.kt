package com.rio.rostry.data.farm.repository

import com.rio.rostry.domain.farm.repository.AssetBatchOperationRepository
import com.rio.rostry.core.model.AssetBatchOperation
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.farm.mapper.toAssetBatchOperation
import com.rio.rostry.data.farm.mapper.toEntity
import com.rio.rostry.data.database.dao.AssetBatchOperationDao
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetBatchOperationRepositoryImpl @Inject constructor(
    private val dao: AssetBatchOperationDao
) : AssetBatchOperationRepository {

    override suspend fun createOperation(operation: AssetBatchOperation): Result<Unit> {
        return try {
            dao.insert(operation.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create batch operation")
            Result.Error(e)
        }
    }

    override suspend fun updateOperation(operation: AssetBatchOperation): Result<Unit> {
        return try {
            dao.update(operation.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update batch operation")
            Result.Error(e)
        }
    }

    override suspend fun getOperationById(operationId: String): Result<AssetBatchOperation?> {
        return try {
            val entity = dao.getOperationById(operationId)
            Result.Success(entity?.toAssetBatchOperation())
        } catch (e: Exception) {
            Timber.e(e, "Failed to get batch operation: $operationId")
            Result.Error(e)
        }
    }

    override suspend fun getOperationsByFarmer(farmerId: String): Result<List<AssetBatchOperation>> {
        return try {
            val entities = dao.getOperationsByFarmer(farmerId)
            Result.Success(entities.map { it.toAssetBatchOperation() })
        } catch (e: Exception) {
            Timber.e(e, "Failed to get batch operations for farmer: $farmerId")
            Result.Error(e)
        }
    }
}
