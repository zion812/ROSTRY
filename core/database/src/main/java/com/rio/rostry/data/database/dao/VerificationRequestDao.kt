package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.VerificationRequestEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for verification request operations.
 * 
 * Provides CRUD operations for managing verification requests locally,
 * including queries for draft recovery and admin dashboard views.
 */
@Dao
interface VerificationRequestDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: VerificationRequestEntity)
    
    @Update
    suspend fun update(request: VerificationRequestEntity)
    
    @Query("SELECT * FROM verification_requests WHERE requestId = :requestId")
    suspend fun getById(requestId: String): VerificationRequestEntity?
    
    @Query("SELECT * FROM verification_requests WHERE requestId = :requestId")
    fun observeById(requestId: String): Flow<VerificationRequestEntity?>
    
    @Query("SELECT * FROM verification_requests WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestByUserId(userId: String): VerificationRequestEntity?
    
    @Query("SELECT * FROM verification_requests WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun observeLatestByUserId(userId: String): Flow<VerificationRequestEntity?>
    
    @Query("SELECT * FROM verification_requests WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAllByUserId(userId: String): Flow<List<VerificationRequestEntity>>
    
    @Query("SELECT * FROM verification_requests WHERE status = :status ORDER BY submittedAt ASC")
    fun observeByStatus(status: String): Flow<List<VerificationRequestEntity>>
    
    /** Get all pending requests for admin dashboard */
    @Query("SELECT * FROM verification_requests WHERE status = 'PENDING' ORDER BY submittedAt ASC")
    fun observePendingRequests(): Flow<List<VerificationRequestEntity>>
    
    /** Get drafts that need retry (failed uploads) */
    @Query("SELECT * FROM verification_requests WHERE userId = :userId AND status = 'DRAFT'")
    suspend fun getDraftsByUserId(userId: String): List<VerificationRequestEntity>
    
    /** Delete old drafts for user (cleanup before creating new request) */
    @Query("DELETE FROM verification_requests WHERE userId = :userId AND status = 'DRAFT'")
    suspend fun deleteDraftsByUserId(userId: String)
    
    /** Update status only */
    @Query("UPDATE verification_requests SET status = :status, updatedAt = :updatedAt WHERE requestId = :requestId")
    suspend fun updateStatus(requestId: String, status: String, updatedAt: Long = System.currentTimeMillis())
    
    /** Update with rejection */
    @Query("UPDATE verification_requests SET status = 'REJECTED', rejectionReason = :reason, reviewedAt = :reviewedAt, updatedAt = :updatedAt WHERE requestId = :requestId")
    suspend fun reject(
        requestId: String, 
        reason: String, 
        reviewedAt: Long = System.currentTimeMillis(),
        updatedAt: Long = System.currentTimeMillis()
    )
    
    /** Update to approved */
    @Query("UPDATE verification_requests SET status = 'APPROVED', reviewedAt = :reviewedAt, updatedAt = :updatedAt WHERE requestId = :requestId")
    suspend fun approve(
        requestId: String, 
        reviewedAt: Long = System.currentTimeMillis(),
        updatedAt: Long = System.currentTimeMillis()
    )
    
    /** Update upload URLs */
    @Query("UPDATE verification_requests SET govtIdUrl = :govtIdUrl, updatedAt = :updatedAt WHERE requestId = :requestId")
    suspend fun updateGovtIdUrl(requestId: String, govtIdUrl: String, updatedAt: Long = System.currentTimeMillis())
    
    @Query("UPDATE verification_requests SET farmPhotoUrl = :farmPhotoUrl, updatedAt = :updatedAt WHERE requestId = :requestId")
    suspend fun updateFarmPhotoUrl(requestId: String, farmPhotoUrl: String, updatedAt: Long = System.currentTimeMillis())
    
    /** Mark as submitted to Firestore */
    @Query("UPDATE verification_requests SET status = 'PENDING', submittedAt = :submittedAt, updatedAt = :updatedAt WHERE requestId = :requestId")
    suspend fun markSubmitted(requestId: String, submittedAt: Long = System.currentTimeMillis(), updatedAt: Long = System.currentTimeMillis())
}
