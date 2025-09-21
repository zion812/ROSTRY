package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.CoinLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinLedgerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: CoinLedgerEntity)

    @Query("SELECT COALESCE(SUM(coins), 0) FROM coin_ledger WHERE userId = :userId")
    suspend fun userCoinBalance(userId: String): Int

    @Query("SELECT * FROM coin_ledger WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserLedger(userId: String): Flow<List<CoinLedgerEntity>>
}
