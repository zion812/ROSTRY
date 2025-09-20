package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getAllCoins(): Flow<List<Coin>>
    suspend fun getCoinById(id: String): Coin?
    suspend fun getCoinByUserId(userId: String): Coin?
    suspend fun insertCoin(coin: Coin)
    suspend fun updateCoin(coin: Coin)
    suspend fun deleteCoin(coin: Coin)
}