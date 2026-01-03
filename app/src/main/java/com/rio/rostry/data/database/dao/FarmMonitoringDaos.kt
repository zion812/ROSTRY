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

    @Query("SELECT * FROM growth_records WHERE productId = :productId ORDER BY week ASC")
    suspend fun getAllByProduct(productId: String): List<GrowthRecordEntity>

    @Query("SELECT * FROM growth_records WHERE productId = :productId AND week = :week LIMIT 1")
    suspend fun findByWeek(productId: String, week: Int): GrowthRecordEntity?

    @Query("SELECT COUNT(*) FROM growth_records WHERE createdAt BETWEEN :start AND :end")
    suspend fun countBetween(start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM growth_records WHERE farmerId = :farmerId AND createdAt BETWEEN :start AND :end")
    suspend fun countForFarmerBetween(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM growth_records WHERE farmerId = :farmerId AND createdAt BETWEEN :start AND :end")
    fun observeCountForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<Int>
    
    @Query("SELECT * FROM growth_records WHERE farmerId = :farmerId ORDER BY week ASC")
    suspend fun getAllByFarmer(farmerId: String): List<GrowthRecordEntity>

    @Query("SELECT * FROM growth_records WHERE dirty = 1")
    suspend fun getDirty(): List<GrowthRecordEntity>

    @Query("UPDATE growth_records SET dirty = 0, syncedAt = :syncedAt WHERE recordId IN (:recordIds)")
    suspend fun clearDirty(recordIds: List<String>, syncedAt: Long)

    @Query("SELECT COUNT(*) FROM growth_records WHERE farmerId = :farmerId AND createdAt BETWEEN :start AND :end")
    suspend fun countByFarmerInRange(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM growth_records WHERE farmerId = :farmerId")
    suspend fun getRecordCountForFarmer(farmerId: String): Int
}



@Dao
interface MortalityRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: MortalityRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: MortalityRecordEntity)

    @Query("SELECT * FROM mortality_records ORDER BY occurredAt DESC")
    fun observeAll(): Flow<List<MortalityRecordEntity>>

    @Query("SELECT * FROM mortality_records WHERE causeCategory = :category ORDER BY occurredAt DESC")
    suspend fun byCategory(category: String): List<MortalityRecordEntity>

    @Query("SELECT COUNT(*) FROM mortality_records WHERE occurredAt BETWEEN :start AND :end")
    suspend fun countBetween(start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM mortality_records WHERE farmerId = :farmerId AND occurredAt BETWEEN :start AND :end")
    suspend fun countForFarmerBetween(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM mortality_records WHERE farmerId = :farmerId AND occurredAt BETWEEN :start AND :end")
    fun observeCountForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<Int>

    @Query("SELECT * FROM mortality_records WHERE dirty = 1")
    suspend fun getDirty(): List<MortalityRecordEntity>

    @Query("UPDATE mortality_records SET dirty = 0, syncedAt = :syncedAt WHERE deathId IN (:deathIds)")
    suspend fun clearDirty(deathIds: List<String>, syncedAt: Long)

    @Query("SELECT COUNT(*) FROM mortality_records WHERE farmerId = :farmerId AND occurredAt BETWEEN :start AND :end")
    suspend fun countByFarmerInRange(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM mortality_records WHERE farmerId = :farmerId")
    suspend fun getRecordCountForFarmer(farmerId: String): Int
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

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NULL AND scheduledAt BETWEEN :start AND :end")
    suspend fun countDueForFarmer(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NULL AND scheduledAt < :now")
    suspend fun countOverdueForFarmer(farmerId: String, now: Long): Int

    @Query("SELECT * FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NULL AND scheduledAt < :now ORDER BY scheduledAt ASC")
    suspend fun getOverdueForFarmer(farmerId: String, now: Long): List<VaccinationRecordEntity>

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND scheduledAt BETWEEN :start AND :end")
    suspend fun countScheduledBetweenForFarmer(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NOT NULL AND administeredAt BETWEEN :start AND :end")
    suspend fun countAdministeredBetweenForFarmer(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NOT NULL AND administeredAt BETWEEN :start AND :end")
    fun observeAdministeredCountForFarmerBetween(farmerId: String, start: Long, end: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NULL AND scheduledAt BETWEEN :start AND :end")
    fun observeDueForFarmer(farmerId: String, start: Long, end: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NULL AND scheduledAt < :now")
    fun observeOverdueForFarmer(farmerId: String, now: Long): Flow<Int>

    @Query("SELECT * FROM vaccination_records WHERE farmerId = :farmerId ORDER BY scheduledAt DESC")
    fun observeByFarmer(farmerId: String): Flow<List<VaccinationRecordEntity>>

    @Query("SELECT * FROM vaccination_records WHERE dirty = 1")
    suspend fun getDirty(): List<VaccinationRecordEntity>

    @Query("UPDATE vaccination_records SET dirty = 0, syncedAt = :syncedAt WHERE vaccinationId IN (:vaccinationIds)")
    suspend fun clearDirty(vaccinationIds: List<String>, syncedAt: Long)

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId")
    suspend fun countByFarmer(farmerId: String): Int

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NOT NULL")
    suspend fun countCompletedByFarmer(farmerId: String): Int

    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId")
    suspend fun getRecordCountForFarmer(farmerId: String): Int

    /** Count pending (unadministered, not overdue) vaccinations for a farmer. */
    @Query("SELECT COUNT(*) FROM vaccination_records WHERE farmerId = :farmerId AND administeredAt IS NULL AND scheduledAt >= :now")
    suspend fun countPendingByFarmer(farmerId: String, now: Long): Int
    
    /** Get all vaccination records for a specific product. */
    @Query("SELECT * FROM vaccination_records WHERE productId = :productId ORDER BY scheduledAt DESC")
    suspend fun getRecordsByProduct(productId: String): List<VaccinationRecordEntity>
}

@Dao
interface HatchingBatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(batch: HatchingBatchEntity)

    @Query("SELECT * FROM hatching_batches ORDER BY startedAt DESC")
    fun observeBatches(): Flow<List<HatchingBatchEntity>>

    // Optimized streams (avoid in-memory filtering in repositories)
    @Query("SELECT * FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > :now ORDER BY expectedHatchAt ASC")
    fun observeActiveBatchesForFarmer(farmerId: String, now: Long): Flow<List<HatchingBatchEntity>>

    @Query("SELECT * FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt BETWEEN :start AND :end ORDER BY expectedHatchAt ASC")
    fun observeHatchingDue(farmerId: String, start: Long, end: Long): Flow<List<HatchingBatchEntity>>

    @Query("SELECT * FROM hatching_batches WHERE batchId = :batchId LIMIT 1")
    suspend fun getById(batchId: String): HatchingBatchEntity?

    @Query("SELECT COUNT(*) FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > :now")
    suspend fun countActiveForFarmer(farmerId: String, now: Long): Int

    @Query("SELECT COUNT(*) FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt BETWEEN :now AND :weekEnd")
    suspend fun countDueThisWeekForFarmer(farmerId: String, now: Long, weekEnd: Long): Int

    @Query("SELECT COUNT(*) FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > :now")
    fun observeActiveForFarmer(farmerId: String, now: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt BETWEEN :now AND :weekEnd")
    fun observeDueThisWeekForFarmer(farmerId: String, now: Long, weekEnd: Long): Flow<Int>

    @Query("SELECT * FROM hatching_batches WHERE farmerId = :farmerId AND status = 'ACTIVE' AND expectedHatchAt BETWEEN :start AND :end ORDER BY expectedHatchAt ASC")
    suspend fun getHatchingDueSoon(farmerId: String, start: Long, end: Long): List<HatchingBatchEntity>

    @Query("SELECT SUM(eggsCount) FROM hatching_batches WHERE farmerId = :farmerId AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > :now")
    fun observeTotalActiveEggs(farmerId: String, now: Long): Flow<Int?>

    // Fetch batches created from specific egg collections
    @Query("SELECT * FROM hatching_batches WHERE sourceCollectionId IN (:collectionIds)")
    suspend fun getBySourceCollectionIds(collectionIds: List<String>): List<HatchingBatchEntity>

    @Query("SELECT * FROM hatching_batches WHERE dirty = 1")
    suspend fun getDirty(): List<HatchingBatchEntity>

    @Query("UPDATE hatching_batches SET dirty = 0, syncedAt = :syncedAt WHERE batchId IN (:batchIds)")
    suspend fun clearDirty(batchIds: List<String>, syncedAt: Long)

    /** Count batches currently incubating (status = INCUBATING or ACTIVE with expectedHatchAt in future) */
    @Query("SELECT COUNT(*) FROM hatching_batches WHERE farmerId = :farmerId AND status IN ('INCUBATING', 'ACTIVE') AND expectedHatchAt > :now")
    suspend fun countIncubatingForFarmer(farmerId: String, now: Long = System.currentTimeMillis()): Int
}

@Dao
interface HatchingLogDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(log: HatchingLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: HatchingLogEntity)

    @Query("SELECT * FROM hatching_logs WHERE batchId = :batchId ORDER BY createdAt ASC")
    fun observeForBatch(batchId: String): Flow<List<HatchingLogEntity>>

    @Query("SELECT COUNT(*) FROM hatching_logs WHERE batchId = :batchId AND eventType = :type")
    suspend fun countByBatchAndType(batchId: String, type: String): Int

    @Query("SELECT COUNT(*) FROM hatching_logs WHERE farmerId = :farmerId AND eventType = 'HATCHED' AND createdAt BETWEEN :start AND :end")
    suspend fun countHatchedBetweenForFarmer(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM hatching_logs WHERE farmerId = :farmerId AND eventType = 'SET' AND createdAt BETWEEN :start AND :end")
    suspend fun countEggsSetBetweenForFarmer(farmerId: String, start: Long, end: Long): Int

    @Query("SELECT * FROM hatching_logs WHERE dirty = 1")
    suspend fun getDirty(): List<HatchingLogEntity>

    @Query("UPDATE hatching_logs SET dirty = 0, syncedAt = :syncedAt WHERE logId IN (:logIds)")
    suspend fun clearDirty(logIds: List<String>, syncedAt: Long)
}
