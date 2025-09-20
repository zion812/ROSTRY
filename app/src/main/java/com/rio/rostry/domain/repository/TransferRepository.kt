package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.Transfer
import kotlinx.coroutines.flow.Flow

interface TransferRepository {
    fun getAllTransfers(): Flow<List<Transfer>>
    suspend fun getTransferById(id: String): Transfer?
    fun getTransfersByUserId(userId: String): Flow<List<Transfer>>
    suspend fun insertTransfer(transfer: Transfer)
    suspend fun updateTransfer(transfer: Transfer)
    suspend fun deleteTransfer(transfer: Transfer)
}