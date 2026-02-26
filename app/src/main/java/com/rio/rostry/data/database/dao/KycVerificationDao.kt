package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.KycVerificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KycVerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: KycVerificationEntity)

    @Update
    suspend fun updateVerification(verification: KycVerificationEntity)

    @Query("SELECT * FROM kyc_verifications WHERE verificationId = :verificationId")
    suspend fun getVerification(verificationId: String): KycVerificationEntity?

    @Query("SELECT * FROM kyc_verifications WHERE userId = :userId")
    suspend fun getVerificationByUserId(userId: String): KycVerificationEntity?

    @Query("SELECT * FROM kyc_verifications WHERE status = :status")
    suspend fun getVerificationsByStatus(status: String): List<KycVerificationEntity>

    @Query("SELECT * FROM kyc_verifications WHERE userId = :userId")
    fun observeVerificationByUserId(userId: String): Flow<KycVerificationEntity?>

    @Query("UPDATE kyc_verifications SET status = :status, reviewedAt = :reviewedAt, reviewedBy = :reviewedBy, rejectionReason = :rejectionReason WHERE verificationId = :verificationId")
    suspend fun updateStatus(
        verificationId: String,
        status: String,
        reviewedAt: Long,
        reviewedBy: String,
        rejectionReason: String?
    )

    @Query("DELETE FROM kyc_verifications WHERE verificationId = :verificationId")
    suspend fun deleteVerification(verificationId: String)
}
