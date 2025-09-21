package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.CoinLedgerDao
import com.rio.rostry.data.database.entity.CoinLedgerEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface CoinRepository {
    fun observeBalance(userId: String): Flow<Int>
    suspend fun getBalance(userId: String): Int
    suspend fun purchaseCoins(userId: String, coins: Int, paymentRef: String? = null): Resource<Unit>
    suspend fun useCoins(userId: String, coins: Int, refId: String?, notes: String? = null): Resource<Unit>
    suspend fun refundCoins(userId: String, coins: Int, refId: String?, notes: String? = null): Resource<Unit>
}

@Singleton
class CoinRepositoryImpl @Inject constructor(
    private val ledgerDao: CoinLedgerDao
) : CoinRepository {

    private val pricePerCoinInr = 5.0

    override fun observeBalance(userId: String): Flow<Int> = ledgerDao.observeUserLedger(userId).map { entries ->
        entries.sumOf { it.coins }
    }

    override suspend fun getBalance(userId: String): Int = ledgerDao.userCoinBalance(userId)

    override suspend fun purchaseCoins(userId: String, coins: Int, paymentRef: String?): Resource<Unit> = try {
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
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Coin purchase failed")
    }

    override suspend fun useCoins(userId: String, coins: Int, refId: String?, notes: String?): Resource<Unit> = try {
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
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Coin usage failed")
    }

    override suspend fun refundCoins(userId: String, coins: Int, refId: String?, notes: String?): Resource<Unit> = try {
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
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Coin refund failed")
    }
}
