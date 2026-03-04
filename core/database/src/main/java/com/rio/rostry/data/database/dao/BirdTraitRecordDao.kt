package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.BirdTraitRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for BirdTraitRecordEntity operations.
 *
 * Provides queries for:
 * - Per-bird trait recording and retrieval
 * - Trait aggregation across generations (for BVI calculation)
 * - Growth tracking over time (weight @ age milestones)
 * - Breed standard comparison data
 */
@Dao
interface BirdTraitRecordDao {

    // ===== CRUD =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BirdTraitRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<BirdTraitRecordEntity>)

    @Update
    suspend fun update(record: BirdTraitRecordEntity)

    @Query("DELETE FROM bird_trait_records WHERE recordId = :recordId")
    suspend fun delete(recordId: String)

    @Query("SELECT * FROM bird_trait_records WHERE recordId = :recordId")
    suspend fun findById(recordId: String): BirdTraitRecordEntity?

    // ===== Per-Bird Queries =====

    /** All trait records for a bird, newest first */
    @Query("SELECT * FROM bird_trait_records WHERE productId = :productId ORDER BY recordedAt DESC")
    fun observeByBird(productId: String): Flow<List<BirdTraitRecordEntity>>

    @Query("SELECT * FROM bird_trait_records WHERE productId = :productId ORDER BY recordedAt DESC")
    suspend fun getByBird(productId: String): List<BirdTraitRecordEntity>

    /** Traits filtered by category for a bird */
    @Query("""
        SELECT * FROM bird_trait_records 
        WHERE productId = :productId AND traitCategory = :category 
        ORDER BY recordedAt DESC
    """)
    fun observeByBirdAndCategory(productId: String, category: String): Flow<List<BirdTraitRecordEntity>>

    /** Get latest value of a specific trait for a bird */
    @Query("""
        SELECT * FROM bird_trait_records 
        WHERE productId = :productId AND traitName = :traitName 
        ORDER BY recordedAt DESC LIMIT 1
    """)
    suspend fun getLatestTrait(productId: String, traitName: String): BirdTraitRecordEntity?

    /** Get trait history for a specific trait (for growth charts) */
    @Query("""
        SELECT * FROM bird_trait_records 
        WHERE productId = :productId AND traitName = :traitName 
        ORDER BY ageWeeks ASC, recordedAt ASC
    """)
    fun observeTraitHistory(productId: String, traitName: String): Flow<List<BirdTraitRecordEntity>>

    @Query("""
        SELECT * FROM bird_trait_records 
        WHERE productId = :productId AND traitName = :traitName 
        ORDER BY ageWeeks ASC, recordedAt ASC
    """)
    suspend fun getTraitHistory(productId: String, traitName: String): List<BirdTraitRecordEntity>

    // ===== Owner-Level Queries =====

    /** All records for an owner */
    @Query("SELECT * FROM bird_trait_records WHERE ownerId = :ownerId ORDER BY recordedAt DESC")
    fun observeByOwner(ownerId: String): Flow<List<BirdTraitRecordEntity>>

    /** Count trait records per bird for an owner (to gauge data completeness) */
    @Query("""
        SELECT productId, COUNT(*) as cnt 
        FROM bird_trait_records 
        WHERE ownerId = :ownerId 
        GROUP BY productId
    """)
    suspend fun getTraitCountPerBird(ownerId: String): List<TraitCountResult>

    // ===== Aggregation Queries (for BVI and Analytics) =====

    /** Average numeric value for a trait across multiple birds */
    @Query("""
        SELECT AVG(numericValue) FROM bird_trait_records 
        WHERE productId IN (:productIds) 
        AND traitName = :traitName 
        AND numericValue IS NOT NULL
    """)
    suspend fun getAverageTraitValue(productIds: List<String>, traitName: String): Double?

    /** Average numeric value for a trait at a specific age milestone */
    @Query("""
        SELECT AVG(numericValue) FROM bird_trait_records 
        WHERE productId IN (:productIds) 
        AND traitName = :traitName 
        AND ageWeeks = :ageWeeks
        AND numericValue IS NOT NULL
    """)
    suspend fun getAverageTraitAtAge(productIds: List<String>, traitName: String, ageWeeks: Int): Double?

    /** Get distinct trait names recorded for a bird */
    @Query("""
        SELECT DISTINCT traitName FROM bird_trait_records 
        WHERE productId = :productId
    """)
    suspend fun getRecordedTraitNames(productId: String): List<String>

    /** Count of birds with trait records for an owner */
    @Query("""
        SELECT COUNT(DISTINCT productId) FROM bird_trait_records 
        WHERE ownerId = :ownerId
    """)
    suspend fun countBirdsWithTraits(ownerId: String): Int

    /** Count distinct traits recorded for a single bird */
    @Query("SELECT COUNT(DISTINCT traitName) FROM bird_trait_records WHERE productId = :productId")
    suspend fun getTraitCount(productId: String): Int

    /** Total trait records for an owner */
    @Query("SELECT COUNT(*) FROM bird_trait_records WHERE ownerId = :ownerId")
    suspend fun countTotalRecords(ownerId: String): Int
}

/** Result class for trait count per bird */
data class TraitCountResult(
    val productId: String,
    val cnt: Int
)
