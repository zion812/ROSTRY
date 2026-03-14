package com.rio.rostry.data.admin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rio.rostry.core.model.Product
import com.rio.rostry.core.model.Result
import com.rio.rostry.domain.admin.repository.AdminProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real admin product repository implementation with Firestore queries.
 * Provides admin-level product management functionality.
 */
@Singleton
class AdminProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminProductRepository {

    private val productsCollection = firestore.collection("products")

    override suspend fun flagProduct(productId: String, reason: String): Result<Unit> = try {
        productsCollection.document(productId)
            .update(mapOf(
                "adminFlagged" to true,
                "flagReason" to reason,
                "flaggedAt" to System.currentTimeMillis()
            ))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun hideProduct(productId: String): Result<Unit> = try {
        productsCollection.document(productId)
            .update(mapOf(
                "status" to "HIDDEN",
                "hiddenAt" to System.currentTimeMillis()
            ))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun unhideProduct(productId: String): Result<Unit> = try {
        productsCollection.document(productId)
            .update(mapOf(
                "status" to "ACTIVE",
                "hiddenAt" to null
            ))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun getAllProductsAdmin(): Flow<Result<List<Product>>> = flow {
        try {
            val snapshot = productsCollection
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toProduct()
            }
            emit(Result.Success(products))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override fun getFlaggedProducts(): Flow<Result<List<Product>>> = flow {
        try {
            val snapshot = productsCollection
                .whereEqualTo("adminFlagged", true)
                .orderBy("flaggedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toProduct()
            }
            emit(Result.Success(products))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun clearFlag(productId: String): Result<Unit> = try {
        productsCollection.document(productId)
            .update(mapOf(
                "adminFlagged" to false,
                "flagReason" to null,
                "flaggedAt" to null
            ))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun unflagProduct(productId: String): Result<Unit> = clearFlag(productId)

    override suspend fun deleteProduct(productId: String): Result<Unit> = try {
        // Soft delete - mark as deleted
        productsCollection.document(productId)
            .update(mapOf(
                "isDeleted" to true,
                "deletedAt" to System.currentTimeMillis()
            ))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun restoreProduct(productId: String): Result<Unit> = try {
        productsCollection.document(productId)
            .update(mapOf(
                "isDeleted" to false,
                "deletedAt" to null,
                "status" to "ACTIVE"
            ))
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    /**
     * Convert Firestore document to Product domain model.
     */
    private fun com.google.firebase.firestore.DocumentSnapshot.toProduct(): Product? {
        return try {
            Product(
                id = id,
                name = getString("name") ?: "",
                sellerId = getString("sellerId") ?: "",
                category = getString("category") ?: "",
                price = getDouble("price") ?: 0.0,
                quantity = getDouble("quantity") ?: 1.0,
                unit = getString("unit") ?: "piece",
                currency = getString("currency") ?: "INR",
                description = getString("description"),
                imageUrls = get("imageUrls") as? List<String> ?: emptyList(),
                breed = getString("breed"),
                gender = getString("gender"),
                birthDate = getLong("birthDate"),
                ageWeeks = getLong("ageWeeks")?.toInt(),
                colorTag = getString("colorTag"),
                birdCode = getString("birdCode"),
                stage = getString("stage") ?: "ADULT",
                lifecycleStatus = getString("lifecycleStatus") ?: "ACTIVE",
                lastStageTransitionAt = getLong("lastStageTransitionAt"),
                latitude = getDouble("latitude"),
                longitude = getDouble("longitude"),
                location = getString("location"),
                familyTreeId = getString("familyTreeId"),
                parentMaleId = getString("parentMaleId"),
                parentFemaleId = getString("parentFemaleId"),
                isTraceable = getBoolean("isTraceable") ?: false,
                isVerified = getBoolean("isVerified") ?: false,
                verificationLevel = getString("verificationLevel"),
                qrCodeUrl = getString("qrCodeUrl"),
                metadata = get("metadata") as? Map<String, Any>,
                recordsLockedAt = getLong("recordsLockedAt"),
                autoLockAfterDays = getLong("autoLockAfterDays")?.toInt() ?: 30,
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
}