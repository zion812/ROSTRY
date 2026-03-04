package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the digital_twins table — unified bird identity.
 */
@Dao
interface DigitalTwinDao {

    // ==================== INSERTS / UPDATES ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(twin: DigitalTwinEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(twins: List<DigitalTwinEntity>)

    @Update
    suspend fun update(twin: DigitalTwinEntity)

    // ==================== SINGLE QUERIES ====================

    /** Get Digital Twin by twinId */
    @Query("SELECT * FROM digital_twins WHERE twinId = :twinId AND isDeleted = 0")
    suspend fun getById(twinId: String): DigitalTwinEntity?

    /** Get Digital Twin by linked bird (ProductEntity) ID */
    @Query("SELECT * FROM digital_twins WHERE birdId = :birdId AND isDeleted = 0")
    suspend fun getByBirdId(birdId: String): DigitalTwinEntity?

    /** Get Digital Twin by linked bird ID (reactive) */
    @Query("SELECT * FROM digital_twins WHERE birdId = :birdId AND isDeleted = 0")
    fun observeByBirdId(birdId: String): Flow<DigitalTwinEntity?>

    /** Get Digital Twin by registry ID */
    @Query("SELECT * FROM digital_twins WHERE registryId = :registryId AND isDeleted = 0")
    suspend fun getByRegistryId(registryId: String): DigitalTwinEntity?

    // ==================== COLLECTION QUERIES ====================

    /** Get ALL active (non-deleted) twins — used by WorkManager for batch evolution */
    @Query("SELECT * FROM digital_twins WHERE isDeleted = 0 ORDER BY updatedAt ASC")
    suspend fun getAllActive(): List<DigitalTwinEntity>

    /** Get all twins for an owner */
    @Query("SELECT * FROM digital_twins WHERE ownerId = :ownerId AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getByOwner(ownerId: String): Flow<List<DigitalTwinEntity>>

    /** Get all twins for an owner (one-shot) */
    @Query("SELECT * FROM digital_twins WHERE ownerId = :ownerId AND isDeleted = 0 ORDER BY updatedAt DESC")
    suspend fun getByOwnerOnce(ownerId: String): List<DigitalTwinEntity>

    /** Get all twins of a specific breed/strain */
    @Query("SELECT * FROM digital_twins WHERE ownerId = :ownerId AND strainType = :strainType AND isDeleted = 0")
    fun getByStrain(ownerId: String, strainType: String): Flow<List<DigitalTwinEntity>>

    /** Get birds in a specific lifecycle stage */
    @Query("SELECT * FROM digital_twins WHERE ownerId = :ownerId AND lifecycleStage = :stage AND isDeleted = 0")
    fun getByLifecycleStage(ownerId: String, stage: String): Flow<List<DigitalTwinEntity>>

    /** Get birds eligible for breeding */
    @Query("""
        SELECT * FROM digital_twins 
        WHERE ownerId = :ownerId AND isDeleted = 0
        AND lifecycleStage IN ('ADULT_FIGHTER', 'BREEDER_PRIME')
        ORDER BY valuationScore DESC
    """)
    fun getBreedingEligible(ownerId: String): Flow<List<DigitalTwinEntity>>

    /** Get birds eligible for shows */
    @Query("""
        SELECT * FROM digital_twins 
        WHERE ownerId = :ownerId AND isDeleted = 0
        AND lifecycleStage IN ('PRE_ADULT', 'ADULT_FIGHTER', 'BREEDER_PRIME')
        AND currentHealthStatus = 'HEALTHY'
        ORDER BY valuationScore DESC
    """)
    fun getShowEligible(ownerId: String): Flow<List<DigitalTwinEntity>>

    // ==================== MARKET / VALUATION QUERIES ====================

    /** Get top-valued birds for an owner */
    @Query("""
        SELECT * FROM digital_twins 
        WHERE ownerId = :ownerId AND isDeleted = 0
        AND valuationScore IS NOT NULL
        ORDER BY valuationScore DESC 
        LIMIT :limit
    """)
    fun getTopValuedBirds(ownerId: String, limit: Int = 10): Flow<List<DigitalTwinEntity>>

    /** Get certified birds (REGISTERED, VERIFIED, CHAMPION) */
    @Query("""
        SELECT * FROM digital_twins 
        WHERE ownerId = :ownerId AND isDeleted = 0
        AND certificationLevel != 'NONE'
        ORDER BY 
            CASE certificationLevel 
                WHEN 'CHAMPION' THEN 0 
                WHEN 'VERIFIED' THEN 1 
                WHEN 'REGISTERED' THEN 2 
            END
    """)
    fun getCertifiedBirds(ownerId: String): Flow<List<DigitalTwinEntity>>

    // ==================== SCORE UPDATES ====================

    /** Update morphology score */
    @Query("UPDATE digital_twins SET morphologyScore = :score, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateMorphologyScore(twinId: String, score: Int, now: Long = System.currentTimeMillis())

    /** Update genetics score */
    @Query("UPDATE digital_twins SET geneticsScore = :score, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateGeneticsScore(twinId: String, score: Int, now: Long = System.currentTimeMillis())

    /** Update performance score */
    @Query("UPDATE digital_twins SET performanceScore = :score, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updatePerformanceScore(twinId: String, score: Int, now: Long = System.currentTimeMillis())

    /** Update health score */
    @Query("UPDATE digital_twins SET healthScore = :score, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateHealthScore(twinId: String, score: Int, now: Long = System.currentTimeMillis())

    /** Update valuation score (composite) */
    @Query("UPDATE digital_twins SET valuationScore = :score, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateValuationScore(twinId: String, score: Int, now: Long = System.currentTimeMillis())

    /** Update fight stats */
    @Query("""
        UPDATE digital_twins SET 
            totalFights = :totalFights, 
            fightWins = :wins, 
            updatedAt = :now 
        WHERE twinId = :twinId
    """)
    suspend fun updateFightStats(twinId: String, totalFights: Int, wins: Int, now: Long = System.currentTimeMillis())

    /** Update lifecycle stage */
    @Query("""
        UPDATE digital_twins SET 
            lifecycleStage = :stage, 
            ageDays = :ageDays, 
            updatedAt = :now 
        WHERE twinId = :twinId
    """)
    suspend fun updateLifecycleStage(twinId: String, stage: String, ageDays: Int, now: Long = System.currentTimeMillis())

    /** Update weight */
    @Query("UPDATE digital_twins SET weightKg = :weightKg, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateWeight(twinId: String, weightKg: Double, now: Long = System.currentTimeMillis())

    /** Update breeding status */
    @Query("UPDATE digital_twins SET breedingStatus = :status, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateBreedingStatus(twinId: String, status: String, now: Long = System.currentTimeMillis())

    /** Update certification level */
    @Query("UPDATE digital_twins SET certificationLevel = :level, verifiedStatus = :verified, updatedAt = :now WHERE twinId = :twinId")
    suspend fun updateCertification(twinId: String, level: String, verified: Boolean, now: Long = System.currentTimeMillis())

    // ==================== COUNTS / STATS ====================

    /** Count all birds for an owner */
    @Query("SELECT COUNT(*) FROM digital_twins WHERE ownerId = :ownerId AND isDeleted = 0")
    suspend fun countByOwner(ownerId: String): Int

    /** Count by lifecycle stage */
    @Query("SELECT COUNT(*) FROM digital_twins WHERE ownerId = :ownerId AND lifecycleStage = :stage AND isDeleted = 0")
    suspend fun countByStage(ownerId: String, stage: String): Int

    /** Average valuation score across all owner's birds */
    @Query("SELECT AVG(valuationScore) FROM digital_twins WHERE ownerId = :ownerId AND valuationScore IS NOT NULL AND isDeleted = 0")
    suspend fun averageValuationScore(ownerId: String): Double?

    // ==================== SYNC ====================

    @Query("SELECT * FROM digital_twins WHERE dirty = 1")
    suspend fun getDirtyRecords(): List<DigitalTwinEntity>

    @Query("UPDATE digital_twins SET dirty = 0, syncedAt = :syncedAt WHERE twinId = :twinId")
    suspend fun markSynced(twinId: String, syncedAt: Long = System.currentTimeMillis())

    // ==================== SOFT DELETE ====================

    @Query("UPDATE digital_twins SET isDeleted = 1, deletedAt = :now WHERE twinId = :twinId")
    suspend fun softDelete(twinId: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM digital_twins WHERE twinId = :twinId")
    suspend fun hardDelete(twinId: String)
}
