package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.ProductVerificationDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVerificationDraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: ProductVerificationDraftEntity)

    @Update
    suspend fun updateDraft(draft: ProductVerificationDraftEntity)

    @Query("SELECT * FROM product_verification_drafts WHERE draftId = :draftId")
    suspend fun getDraft(draftId: String): ProductVerificationDraftEntity?

    @Query("SELECT * FROM product_verification_drafts WHERE productId = :productId AND status = :status")
    suspend fun getDraftsForProduct(productId: String, status: String): List<ProductVerificationDraftEntity>

    @Query("SELECT * FROM product_verification_drafts WHERE productId = :productId")
    suspend fun getAllDraftsForProduct(productId: String): List<ProductVerificationDraftEntity>

    @Query("SELECT * FROM product_verification_drafts WHERE verifierId = :verifierId AND status = :status")
    suspend fun getDraftsForVerifier(verifierId: String, status: String): List<ProductVerificationDraftEntity>

    @Query("UPDATE product_verification_drafts SET status = :status, mergedAt = :mergedAt, mergedInto = :mergedInto WHERE draftId IN (:draftIds)")
    suspend fun markDraftsAsMerged(draftIds: List<String>, status: String, mergedAt: Long, mergedInto: String)

    @Query("DELETE FROM product_verification_drafts WHERE draftId = :draftId")
    suspend fun deleteDraft(draftId: String)

    @Query("SELECT * FROM product_verification_drafts WHERE productId = :productId")
    fun observeDraftsForProduct(productId: String): Flow<List<ProductVerificationDraftEntity>>
}
