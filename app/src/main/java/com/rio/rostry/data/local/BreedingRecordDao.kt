package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.BreedingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingRecordDao {
    @Query("SELECT * FROM breeding_records ORDER BY breedingDate DESC")
    fun getAllBreedingRecords(): Flow<List<BreedingRecord>>

    @Query("SELECT * FROM breeding_records WHERE id = :id")
    suspend fun getBreedingRecordById(id: String): BreedingRecord?

    @Query("SELECT * FROM breeding_records WHERE parentId1 = :parentId OR parentId2 = :parentId")
    fun getBreedingRecordsByParentId(parentId: String): Flow<List<BreedingRecord>>

    @Query("SELECT * FROM breeding_records WHERE parentId1 = :parentId1 AND parentId2 = :parentId2")
    fun getBreedingRecordsByParentPair(parentId1: String, parentId2: String): Flow<List<BreedingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreedingRecord(breedingRecord: BreedingRecord)

    @Update
    suspend fun updateBreedingRecord(breedingRecord: BreedingRecord)

    @Delete
    suspend fun deleteBreedingRecord(breedingRecord: BreedingRecord)
}