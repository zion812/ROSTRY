package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.BreedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {
    @Query("SELECT * FROM breeds")
    fun getAllBreeds(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE culinaryProfile = :profile")
    fun getBreedsByCulinaryProfile(profile: String): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE farmingDifficulty = :difficulty")
    fun getBreedsByDifficulty(difficulty: String): Flow<List<BreedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<BreedEntity>)
    
    @Query("SELECT * FROM breeds WHERE breedId = :breedId")
    suspend fun getBreedById(breedId: String): BreedEntity?
}
