package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.GeneticTrait
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneticTraitDao {
    @Query("SELECT * FROM genetic_traits ORDER BY name ASC")
    fun getAllGeneticTraits(): Flow<List<GeneticTrait>>

    @Query("SELECT * FROM genetic_traits WHERE id = :id")
    suspend fun getGeneticTraitById(id: String): GeneticTrait?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneticTrait(geneticTrait: GeneticTrait)

    @Update
    suspend fun updateGeneticTrait(geneticTrait: GeneticTrait)

    @Delete
    suspend fun deleteGeneticTrait(geneticTrait: GeneticTrait)
}