package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.Coin
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins")
    fun getAllCoins(): Flow<List<Coin>>

    @Query("SELECT * FROM coins WHERE id = :id")
    suspend fun getCoinById(id: String): Coin?

    @Query("SELECT * FROM coins WHERE userId = :userId")
    suspend fun getCoinByUserId(userId: String): Coin?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoin(coin: Coin)

    @Update
    suspend fun updateCoin(coin: Coin)

    @Delete
    suspend fun deleteCoin(coin: Coin)
}