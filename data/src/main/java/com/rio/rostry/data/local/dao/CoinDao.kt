package com.rio.rostry.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.local.entities.CoinTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tx: CoinTransactionEntity)

    @Query("SELECT * FROM coin_transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun streamByUser(userId: String): Flow<List<CoinTransactionEntity>>

    @Query("SELECT COALESCE(SUM(CASE WHEN type IN ('SPEND') THEN -coins ELSE coins END), 0) FROM coin_transactions WHERE userId = :userId")
    suspend fun getBalance(userId: String): Int

    @Query("SELECT * FROM coin_transactions WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(userId: String, limit: Int = 20): List<CoinTransactionEntity>
}
