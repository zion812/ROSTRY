package com.rio.rostry.data.repository

import com.rio.rostry.data.local.TransferDao
import com.rio.rostry.data.model.Transfer as DataTransfer
import com.rio.rostry.domain.model.Transfer as DomainTransfer
import com.rio.rostry.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransferRepositoryImpl @Inject constructor(
    private val transferDao: TransferDao
) : TransferRepository {

    override fun getAllTransfers(): Flow<List<DomainTransfer>> {
        return transferDao.getAllTransfers().map { transfers ->
            transfers.map { it.toDomainModel() }
        }
    }

    override suspend fun getTransferById(id: String): DomainTransfer? {
        return transferDao.getTransferById(id)?.toDomainModel()
    }

    override fun getTransfersByUserId(userId: String): Flow<List<DomainTransfer>> {
        return transferDao.getTransfersByUserId(userId).map { transfers ->
            transfers.map { it.toDomainModel() }
        }
    }

    override suspend fun insertTransfer(transfer: DomainTransfer) {
        transferDao.insertTransfer(transfer.toDataModel())
    }

    override suspend fun updateTransfer(transfer: DomainTransfer) {
        transferDao.updateTransfer(transfer.toDataModel())
    }

    override suspend fun deleteTransfer(transfer: DomainTransfer) {
        transferDao.deleteTransfer(transfer.toDataModel())
    }

    private fun DataTransfer.toDomainModel(): DomainTransfer {
        return DomainTransfer(
            id = id,
            fromUserId = fromUserId,
            toUserId = toUserId,
            orderId = orderId,
            amount = amount,
            type = type,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainTransfer.toDataModel(): DataTransfer {
        return DataTransfer(
            id = id,
            fromUserId = fromUserId,
            toUserId = toUserId,
            orderId = orderId,
            amount = amount,
            type = type,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}