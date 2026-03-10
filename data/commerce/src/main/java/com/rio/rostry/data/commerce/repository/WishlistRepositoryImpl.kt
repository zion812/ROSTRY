package com.rio.rostry.data.commerce.repository

import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.Wishlist
import com.rio.rostry.data.commerce.mapper.toWishlist
import com.rio.rostry.data.database.dao.WishlistDao
import com.rio.rostry.data.database.entity.WishlistEntity
import com.rio.rostry.domain.commerce.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WishlistRepository for wishlist operations.
 */
@Singleton
class WishlistRepositoryImpl @Inject constructor(
    private val dao: WishlistDao
) : WishlistRepository {

    override fun observe(userId: String): Flow<List<Wishlist>> {
        return dao.observe(userId).map { list -> list.map { it.toWishlist() } }
    }

    override suspend fun add(userId: String, productId: String): Result<Unit> {
        return try {
            dao.upsert(WishlistEntity(userId = userId, productId = productId))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun remove(userId: String, productId: String): Result<Unit> {
        return try {
            dao.remove(userId, productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

