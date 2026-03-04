package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.CoinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinTransaction(coinTransaction: CoinEntity)

    @Query("SELECT * FROM coins WHERE coinTransactionId = :transactionId")
    fun getCoinTransactionById(transactionId: String): Flow<CoinEntity?>

    @Query("SELECT * FROM coins WHERE userId = :userId ORDER BY transactionDate DESC")
    fun getCoinTransactionsByUserId(userId: String): Flow<List<CoinEntity>>

    @Query("SELECT SUM(amount) FROM coins WHERE userId = :userId")
    fun getUserCoinBalance(userId: String): Flow<Double?>

    @Query("SELECT * FROM coins WHERE type = :type ORDER BY transactionDate DESC")
    fun getCoinTransactionsByType(type: String): Flow<List<CoinEntity>>

    @Query("SELECT * FROM coins WHERE relatedOrderId = :orderId ORDER BY transactionDate DESC")
    fun getCoinTransactionsByOrderId(orderId: String): Flow<List<CoinEntity>>

    @Query("DELETE FROM coins WHERE coinTransactionId = :transactionId")
    suspend fun deleteCoinTransactionById(transactionId: String)

    @Query("DELETE FROM coins WHERE userId = :userId")
    suspend fun deleteAllCoinTransactionsForUser(userId: String)

    @Query("DELETE FROM coins")
    suspend fun deleteAllCoinTransactions()
}
