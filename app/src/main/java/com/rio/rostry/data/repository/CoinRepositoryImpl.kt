package com.rio.rostry.data.repository

import com.rio.rostry.data.local.CoinDao
import com.rio.rostry.data.model.Coin as DataCoin
import com.rio.rostry.domain.model.Coin as DomainCoin
import com.rio.rostry.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val coinDao: CoinDao
) : CoinRepository {

    override fun getAllCoins(): Flow<List<DomainCoin>> {
        return coinDao.getAllCoins().map { coins ->
            coins.map { it.toDomainModel() }
        }
    }

    override suspend fun getCoinById(id: String): DomainCoin? {
        return coinDao.getCoinById(id)?.toDomainModel()
    }

    override suspend fun getCoinByUserId(userId: String): DomainCoin? {
        return coinDao.getCoinByUserId(userId)?.toDomainModel()
    }

    override suspend fun insertCoin(coin: DomainCoin) {
        coinDao.insertCoin(coin.toDataModel())
    }

    override suspend fun updateCoin(coin: DomainCoin) {
        coinDao.updateCoin(coin.toDataModel())
    }

    override suspend fun deleteCoin(coin: DomainCoin) {
        coinDao.deleteCoin(coin.toDataModel())
    }

    private fun DataCoin.toDomainModel(): DomainCoin {
        return DomainCoin(
            id = id,
            userId = userId,
            balance = balance,
            totalEarned = totalEarned,
            totalSpent = totalSpent,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainCoin.toDataModel(): DataCoin {
        return DataCoin(
            id = id,
            userId = userId,
            balance = balance,
            totalEarned = totalEarned,
            totalSpent = totalSpent,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}