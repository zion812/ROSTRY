package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.WishlistDao
import com.rio.rostry.data.database.entity.WishlistEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface WishlistRepository {
    fun observe(userId: String): Flow<List<WishlistEntity>>
    suspend fun add(userId: String, productId: String): Resource<Unit>
    suspend fun remove(userId: String, productId: String): Resource<Unit>
}

@Singleton
class WishlistRepositoryImpl @Inject constructor(
    private val dao: WishlistDao
) : WishlistRepository {

    override fun observe(userId: String): Flow<List<WishlistEntity>> = dao.observe(userId)

    override suspend fun add(userId: String, productId: String): Resource<Unit> = try {
        dao.upsert(WishlistEntity(userId = userId, productId = productId))
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to add to wishlist")
    }

    override suspend fun remove(userId: String, productId: String): Resource<Unit> = try {
        dao.remove(userId, productId)
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to remove from wishlist")
    }
}
