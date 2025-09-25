package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import com.rio.rostry.data.database.entity.MortalityRecordEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: GrowthRecordEntity)

    @Query("SELECT * FROM growth_records WHERE productId = :productId ORDER BY week ASC")
    fun observeForProduct(productId: String): Flow<List<GrowthRecordEntity>>

    @Query("SELECT * FROM growth_records WHERE productId = :productId AND week = :week LIMIT 1")
    suspend fun findByWeek(productId: String, week: Int): GrowthRecordEntity?

    @Query("SELECT COUNT(*) FROM growth_records WHERE createdAt BETWEEN :start AND :end")
    suspend fun countBetween(start: Long, end: Long): Int
}

@Dao
interface QuarantineRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: QuarantineRecordEntity)

    @Update
    suspend fun update(record: QuarantineRecordEntity)

    @Query("SELECT * FROM quarantine_records WHERE productId = :productId ORDER BY startedAt DESC")
    fun observeForProduct(productId: String): Flow<List<QuarantineRecordEntity>>

    @Query("SELECT * FROM quarantine_records WHERE status = :status ORDER BY startedAt DESC")
    fun observeByStatus(status: String): Flow<List<QuarantineRecordEntity>>
}

@Dao
interface MortalityRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: MortalityRecordEntity)

    @Query("SELECT * FROM mortality_records ORDER BY occurredAt DESC")
    fun observeAll(): Flow<List<MortalityRecordEntity>>

    @Query("SELECT * FROM mortality_records WHERE causeCategory = :category ORDER BY occurredAt DESC")
    suspend fun byCategory(category: String): List<MortalityRecordEntity>

    @Query("SELECT COUNT(*) FROM mortality_records WHERE occurredAt BETWEEN :start AND :end")
    suspend fun countBetween(start: Long, end: Long): Int
}

@Dao
interface VaccinationRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: VaccinationRecordEntity)

    @Query("SELECT * FROM vaccination_records WHERE productId = :productId ORDER BY scheduledAt ASC")
    fun observeForProduct(productId: String): Flow<List<VaccinationRecordEntity>>

    @Query("SELECT * FROM vaccination_records WHERE administeredAt IS NULL AND scheduledAt <= :byTime ORDER BY scheduledAt ASC")
    suspend fun dueReminders(byTime: Long): List<VaccinationRecordEntity>

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE administeredAt IS NOT NULL AND administeredAt BETWEEN :start AND :end")
    suspend fun countAdministeredBetween(start: Long, end: Long): Int
}

@Dao
interface HatchingBatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(batch: HatchingBatchEntity)

    @Query("SELECT * FROM hatching_batches ORDER BY startedAt DESC")
    fun observeBatches(): Flow<List<HatchingBatchEntity>>
}

@Dao
interface HatchingLogDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(log: HatchingLogEntity)

    @Query("SELECT * FROM hatching_logs WHERE batchId = :batchId ORDER BY createdAt ASC")
    fun observeForBatch(batchId: String): Flow<List<HatchingLogEntity>>
}
