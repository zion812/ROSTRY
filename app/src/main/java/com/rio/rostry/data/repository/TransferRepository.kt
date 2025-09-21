package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.entity.TransferEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface TransferRepository {
    fun getById(transferId: String): Flow<TransferEntity?>
    fun getFromUser(userId: String): Flow<List<TransferEntity>>
    fun getToUser(userId: String): Flow<List<TransferEntity>>
    suspend fun upsert(transfer: TransferEntity)
    suspend fun softDelete(transferId: String)
}

@Singleton
class TransferRepositoryImpl @Inject constructor(
    private val dao: TransferDao
) : TransferRepository {

    override fun getById(transferId: String): Flow<TransferEntity?> = dao.getTransferById(transferId)

    override fun getFromUser(userId: String): Flow<List<TransferEntity>> = dao.getTransfersFromUser(userId)

    override fun getToUser(userId: String): Flow<List<TransferEntity>> = dao.getTransfersToUser(userId)

    override suspend fun upsert(transfer: TransferEntity) {
        val now = System.currentTimeMillis()
        dao.upsert(transfer.copy(updatedAt = now, lastModifiedAt = now, dirty = true))
    }

    override suspend fun softDelete(transferId: String) {
        val now = System.currentTimeMillis()
        dao.upsert(
            TransferEntity(
                transferId = transferId,
                fromUserId = null,
                toUserId = null,
                orderId = null,
                amount = 0.0,
                currency = "",
                type = "",
                status = "",
                transactionReference = null,
                notes = null,
                initiatedAt = now,
                completedAt = null,
                updatedAt = now,
                lastModifiedAt = now,
                isDeleted = true,
                deletedAt = now,
                dirty = true
            )
        )
    }
}
