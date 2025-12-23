package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.FarmVerificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmVerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerification(verification: FarmVerificationEntity)

    @Update
    suspend fun updateVerification(verification: FarmVerificationEntity)

    @Query("SELECT * FROM farm_verifications WHERE farmerId = :farmerId ORDER BY createdAt DESC LIMIT 1")
    fun getLatestVerificationForFarmer(farmerId: String): Flow<FarmVerificationEntity?>

    @Query("SELECT * FROM farm_verifications WHERE verificationId = :verificationId")
    suspend fun getVerificationById(verificationId: String): FarmVerificationEntity?

    @Query("SELECT * FROM farm_verifications WHERE farmerId = :farmerId")
    suspend fun getAllVerificationsForFarmer(farmerId: String): List<FarmVerificationEntity>
}
