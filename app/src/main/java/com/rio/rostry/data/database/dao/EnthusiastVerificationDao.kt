package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.EnthusiastVerificationEntity
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EnthusiastVerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: EnthusiastVerificationEntity)

    @Update
    suspend fun updateVerification(verification: EnthusiastVerificationEntity)

    @Query("SELECT * FROM enthusiast_verifications WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    fun getLatestVerificationForUser(userId: String): Flow<EnthusiastVerificationEntity?>

    @Query("SELECT * FROM enthusiast_verifications WHERE verificationId = :verificationId")
    suspend fun getVerificationById(verificationId: String): EnthusiastVerificationEntity?

    @Query("SELECT * FROM enthusiast_verifications WHERE userId = :userId")
    suspend fun getAllVerificationsForUser(userId: String): List<EnthusiastVerificationEntity>

    @Query("SELECT * FROM enthusiast_verifications WHERE status = :status ORDER BY submittedAt ASC")
    fun getVerificationsByStatus(status: VerificationStatus): Flow<List<EnthusiastVerificationEntity>>
    
    @Query("SELECT * FROM enthusiast_verifications WHERE status = 'PENDING' ORDER BY submittedAt ASC")
    fun getPendingVerifications(): Flow<List<EnthusiastVerificationEntity>>

    @Query("UPDATE enthusiast_verifications SET status = :status, reviewedAt = :reviewedAt, reviewedBy = :reviewedBy, rejectionReason = :reason, updatedAt = :updatedAt WHERE verificationId = :verificationId")
    suspend fun updateVerificationStatus(
        verificationId: String,
        status: VerificationStatus,
        reviewedAt: Long,
        reviewedBy: String,
        reason: String?,
        updatedAt: Long
    )
}
