package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.ClutchEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ClutchEntity operations.
 * 
 * Provides comprehensive queries for breeding/hatching workflow:
 * - Clutch lifecycle management
 * - Fertility and hatchability tracking
 * - Breeding pair performance analytics
 */
@Dao
interface ClutchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(clutch: ClutchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clutches: List<ClutchEntity>)

    @Update
    suspend fun update(clutch: ClutchEntity)

    @Query("DELETE FROM clutches WHERE clutchId = :clutchId")
    suspend fun delete(clutchId: String)

    @Query("SELECT * FROM clutches WHERE clutchId = :clutchId")
    suspend fun findById(clutchId: String): ClutchEntity?

    @Query("SELECT * FROM clutches WHERE clutchId = :clutchId")
    fun observeById(clutchId: String): Flow<ClutchEntity?>

    // By farmer
    @Query("SELECT * FROM clutches WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun observeByFarmer(farmerId: String): Flow<List<ClutchEntity>>

    @Query("SELECT * FROM clutches WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    suspend fun getByFarmer(farmerId: String): List<ClutchEntity>

    // By breeding pair
    @Query("SELECT * FROM clutches WHERE breedingPairId = :pairId ORDER BY createdAt DESC")
    fun observeByBreedingPair(pairId: String): Flow<List<ClutchEntity>>

    @Query("SELECT * FROM clutches WHERE breedingPairId = :pairId ORDER BY createdAt DESC")
    suspend fun getByBreedingPair(pairId: String): List<ClutchEntity>

    // By status
    @Query("SELECT * FROM clutches WHERE farmerId = :farmerId AND status = :status ORDER BY createdAt DESC")
    fun observeByStatus(farmerId: String, status: String): Flow<List<ClutchEntity>>

    // Active clutches (in progress)
    @Query("""
        SELECT * FROM clutches 
        WHERE farmerId = :farmerId 
        AND status IN ('COLLECTING', 'SET', 'INCUBATING', 'HATCHING') 
        ORDER BY expectedHatchDate ASC
    """)
    fun observeActive(farmerId: String): Flow<List<ClutchEntity>>

    // Upcoming hatches (within next N days)
    @Query("""
        SELECT * FROM clutches 
        WHERE farmerId = :farmerId 
        AND status IN ('SET', 'INCUBATING') 
        AND expectedHatchDate IS NOT NULL 
        AND expectedHatchDate BETWEEN :now AND :futureDate 
        ORDER BY expectedHatchDate ASC
    """)
    fun observeUpcomingHatches(farmerId: String, now: Long, futureDate: Long): Flow<List<ClutchEntity>>

    // Completed clutches for analytics
    @Query("""
        SELECT * FROM clutches 
        WHERE farmerId = :farmerId 
        AND status = 'COMPLETE' 
        ORDER BY actualHatchEndDate DESC 
        LIMIT :limit
    """)
    suspend fun getRecentCompleted(farmerId: String, limit: Int = 20): List<ClutchEntity>

    // Breeding pair performance
    @Query("""
        SELECT AVG(hatchabilityOfFertile) 
        FROM clutches 
        WHERE breedingPairId = :pairId 
        AND status = 'COMPLETE' 
        AND hatchabilityOfFertile IS NOT NULL
    """)
    suspend fun getAverageHatchabilityForPair(pairId: String): Double?

    @Query("""
        SELECT AVG(fertilityRate) 
        FROM clutches 
        WHERE breedingPairId = :pairId 
        AND status = 'COMPLETE' 
        AND fertilityRate IS NOT NULL
    """)
    suspend fun getAverageFertilityForPair(pairId: String): Double?

    @Query("SELECT SUM(chicksHatched) FROM clutches WHERE breedingPairId = :pairId AND status = 'COMPLETE'")
    suspend fun getTotalChicksForPair(pairId: String): Int?

    @Query("SELECT COUNT(*) FROM clutches WHERE breedingPairId = :pairId")
    suspend fun getClutchCountForPair(pairId: String): Int

    // Farm-wide stats
    @Query("SELECT SUM(chicksHatched) FROM clutches WHERE farmerId = :farmerId AND actualHatchEndDate >= :since")
    suspend fun getTotalChicksSince(farmerId: String, since: Long): Int?

    @Query("SELECT AVG(hatchabilityOfSet) FROM clutches WHERE farmerId = :farmerId AND status = 'COMPLETE' AND hatchabilityOfSet IS NOT NULL")
    suspend fun getAverageHatchability(farmerId: String): Double?

    // Count by status
    @Query("SELECT COUNT(*) FROM clutches WHERE farmerId = :farmerId AND status = :status")
    suspend fun countByStatus(farmerId: String, status: String): Int
}
