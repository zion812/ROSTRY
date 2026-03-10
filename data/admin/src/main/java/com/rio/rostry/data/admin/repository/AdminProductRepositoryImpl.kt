package com.rio.rostry.data.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.core.model.Product
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.admin.repository.AdminProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Compile-safe admin product repository stub used during modular migration.
 */
@Singleton
class AdminProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminProductRepository {

    override suspend fun flagProduct(productId: String, reason: String): Result<Unit> = Result.Success(Unit)

    override suspend fun hideProduct(productId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun unhideProduct(productId: String): Result<Unit> = Result.Success(Unit)

    override fun getAllProductsAdmin(): Flow<Result<List<Product>>> = flowOf(Result.Success(emptyList()))

    override fun getFlaggedProducts(): Flow<Result<List<Product>>> = flowOf(Result.Success(emptyList()))

    override suspend fun clearFlag(productId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun unflagProduct(productId: String): Result<Unit> = clearFlag(productId)

    override suspend fun deleteProduct(productId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun restoreProduct(productId: String): Result<Unit> = Result.Success(Unit)
}
