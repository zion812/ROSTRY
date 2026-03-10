package com.rio.rostry.data.commerce.repository

import com.rio.rostry.domain.commerce.repository.TransactionRepository
import com.rio.rostry.core.model.Transaction
import com.rio.rostry.core.common.Result
import com.rio.rostry.data.commerce.mapper.toTransaction
import com.rio.rostry.data.database.dao.TransactionDao
import com.rio.rostry.data.database.entity.TransactionEntity
import com.rio.rostry.core.common.session.CurrentUserProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TransactionRepository.
 * 
 * Handles financial transactions for orders including payment tracking,
 * revenue reporting, and transaction history.
 */
@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val currentUserProvider: CurrentUserProvider
) : TransactionRepository {

    private val userId: String
        get() = currentUserProvider.userIdOrNull() ?: ""

    override fun streamTransactionsByUser(userId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUser(userId).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    override fun streamTransactionsByOrder(orderId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByOrder(orderId).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    override fun streamAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
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
    ): Result<Unit> {
        return try {
            val uid = userId
            if (uid.isEmpty()) {
                return Result.Error(Exception("User not authenticated"))
            }
            
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
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to record transaction for order: $orderId")
            Result.Error(e)
        }
    }
}
