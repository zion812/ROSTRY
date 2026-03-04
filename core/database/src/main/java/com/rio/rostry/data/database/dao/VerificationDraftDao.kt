package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.VerificationDraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationDraftDao {
    @Upsert
    suspend fun upsertDraft(draft: VerificationDraftEntity)

    @Query("SELECT * FROM verification_drafts WHERE userId = :userId")
    fun observeDraft(userId: String): Flow<VerificationDraftEntity?>

    @Query("SELECT * FROM verification_drafts WHERE userId = :userId")
    suspend fun getDraft(userId: String): VerificationDraftEntity?

    @Query("DELETE FROM verification_drafts WHERE userId = :userId")
    suspend fun deleteDraft(userId: String)

    @Query("SELECT * FROM verification_drafts WHERE lastSavedAt < :cutoffTime")
    suspend fun getOldDrafts(cutoffTime: Long): List<VerificationDraftEntity>
}
