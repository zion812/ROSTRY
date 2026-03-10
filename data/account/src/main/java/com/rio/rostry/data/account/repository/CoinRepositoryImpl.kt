package com.rio.rostry.data.account.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.data.database.dao.CoinLedgerDao
import com.rio.rostry.data.database.entity.CoinLedgerEntity
import com.rio.rostry.domain.account.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CoinRepository for managing virtual currency transactions.
 * 
 * Phase 2: Domain and Data Decoupling
 * Task 11.4.3 - Account Domain repository migration
 */
@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val ledgerDao: CoinLedgerDao
) : CoinRepository {

    private val pricePerCoinInr = 5.0

    override fun observeBalance(userId: String): Flow<Int> = 
        ledgerDao.observeUserLedger(userId).map { entries ->
            entries.sumOf { it.coins }
        }

    override suspend fun getBalance(userId: String): Int = 
        ledgerDao.userCoinBalance(userId)

    override suspend fun purchaseCoins(
        userId: String,
        coins: Int,
        paymentRef: String?
    ): Result<Unit> = try {
        require(coins > 0) { "Coins must be > 0" }
        val entry = CoinLedgerEntity(
            entryId = java.util.UUID.randomUUID().toString(),
            userId = userId,
            type = "PURCHASE",
            coins = coins,
            amountInInr = coins * pricePerCoinInr,
            refId = paymentRef,
            notes = "Coin purchase",
            createdAt = System.currentTimeMillis()
        )
        ledgerDao.insert(entry)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun useCoins(
        userId: String,
        coins: Int,
        refId: String?,
        notes: String?
    ): Result<Unit> = try {
        require(coins > 0) { "Coins must be > 0" }
        val bal = ledgerDao.userCoinBalance(userId)
        require(bal >= coins) { "Insufficient coin balance" }
        val entry = CoinLedgerEntity(
            entryId = java.util.UUID.randomUUID().toString(),
            userId = userId,
            type = "USE",
            coins = -coins,
            amountInInr = 0.0,
            refId = refId,
            notes = notes ?: "Coin usage",
            createdAt = System.currentTimeMillis()
        )
        ledgerDao.insert(entry)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun refundCoins(
        userId: String,
        coins: Int,
        refId: String?,
        notes: String?
    ): Result<Unit> = try {
        require(coins > 0) { "Coins must be > 0" }
        val entry = CoinLedgerEntity(
            entryId = java.util.UUID.randomUUID().toString(),
            userId = userId,
            type = "REFUND",
            coins = coins,
            amountInInr = 0.0,
            refId = refId,
            notes = notes ?: "Coin refund",
            createdAt = System.currentTimeMillis()
        )
        ledgerDao.insert(entry)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}

