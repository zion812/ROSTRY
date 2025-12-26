package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.BreedingPairEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingPairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pair: BreedingPairEntity)

    @Query("SELECT * FROM breeding_pairs WHERE (maleProductId = :maleId AND femaleProductId = :femaleId) OR (maleProductId = :femaleId AND femaleProductId = :maleId)")
    suspend fun getByProducts(maleId: String, femaleId: String): List<BreedingPairEntity>

    @Query("SELECT * FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun getActivePairsForFarmer(farmerId: String): List<BreedingPairEntity>

    @Query("SELECT * FROM breeding_pairs WHERE farmerId = :farmerId")
    fun observePairsForFarmer(farmerId: String): Flow<List<BreedingPairEntity>>

    @Query("SELECT * FROM breeding_pairs WHERE pairId = :pairId")
    suspend fun getById(pairId: String): BreedingPairEntity?

    @Query("SELECT * FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE' ORDER BY pairedAt DESC")
    fun observeActive(farmerId: String): Flow<List<BreedingPairEntity>>

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE'")
    suspend fun countActive(farmerId: String): Int

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE' AND maleProductId = :maleProductId")
    suspend fun countActiveByMale(farmerId: String, maleProductId: String): Int

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId AND status = 'ACTIVE' AND femaleProductId = :femaleProductId")
    suspend fun countActiveByFemale(farmerId: String, femaleProductId: String): Int

    @Query("SELECT * FROM breeding_pairs WHERE dirty = 1")
    suspend fun getDirty(): List<BreedingPairEntity>

    @Query("UPDATE breeding_pairs SET dirty = 0, syncedAt = :syncedAt WHERE pairId IN (:pairIds)")
    suspend fun clearDirty(pairIds: List<String>, syncedAt: Long)

    @Query("SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = :farmerId")
    suspend fun getPairCountForFarmer(farmerId: String): Int
}
