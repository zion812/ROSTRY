package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.AssetBatchOperationDao
import com.rio.rostry.data.database.entity.AssetBatchOperationEntity
import com.rio.rostry.domain.repository.AssetBatchOperationRepository
import javax.inject.Inject

class AssetBatchOperationRepositoryImpl @Inject constructor(
    private val dao: AssetBatchOperationDao
) : AssetBatchOperationRepository {

    override suspend fun createOperation(operation: AssetBatchOperationEntity) {
        dao.insert(operation)
    }

    override suspend fun updateOperation(operation: AssetBatchOperationEntity) {
        dao.update(operation)
    }

    override suspend fun getOperationById(operationId: String): AssetBatchOperationEntity? {
        return dao.getOperationById(operationId)
    }

    override suspend fun getOperationsByFarmer(farmerId: String): List<AssetBatchOperationEntity> {
        return dao.getOperationsByFarmer(farmerId)
    }
}
