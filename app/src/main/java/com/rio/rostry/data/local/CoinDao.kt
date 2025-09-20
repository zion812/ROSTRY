package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Coin
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins WHERE isDeleted = 0")
    fun getAllCoins(): Flow<List<Coin>>

    @Query("SELECT * FROM coins WHERE id = :id AND isDeleted = 0")
    suspend fun getCoinById(id: String): Coin?

    @Query("SELECT * FROM coins WHERE userId = :userId AND isDeleted = 0")
    suspend fun getCoinByUserId(userId: String): Coin?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: Coin)

    @Update
    suspend fun updateCoin(coin: Coin)

    @Query("UPDATE coins SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteCoin(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM coins WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    suspend fun purgeDeletedCoins(beforeTimestamp: Long)
}