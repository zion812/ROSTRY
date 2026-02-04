package com.rio.rostry.data.repository

import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.TransactionDao
import com.rio.rostry.data.database.entity.TransactionEntity
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val currentUserProvider: CurrentUserProvider
) : BaseRepository(), TransactionRepository {

    private val userId: String
        get() = currentUserProvider.userIdOrNull() ?: ""

    override fun streamTransactionsByUser(userId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByUser(userId)
    }

    override fun streamTransactionsByOrder(orderId: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByOrder(orderId)
    }

    override fun streamAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override fun streamTotalRevenue(): Flow<Double?> {
        return transactionDao.getTotalRevenue()
    }

    override suspend fun recordTransaction(
        orderId: String,
        amount: Double,
        status: String,
        paymentMethod: String,
        gatewayRef: String?,
        notes: String?
    ) {
        try {
            val uid = userId
            if (uid.isNotEmpty()) {
                val transaction = TransactionEntity(
                    transactionId = UUID.randomUUID().toString(),
                    orderId = orderId,
                    userId = uid,
                    amount = amount,
                    status = status,
                    paymentMethod = paymentMethod,
                    gatewayReference = gatewayRef,
                    notes = notes
                )
                transactionDao.insert(transaction)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to record transaction for order: $orderId")
        }
    }
}
