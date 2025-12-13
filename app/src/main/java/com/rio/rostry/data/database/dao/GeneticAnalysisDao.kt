package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.GeneticAnalysisEntity

// Placeholder entity for GeneticAnalysis if it doesn't exist, 
// or assume it exists if the DAO is requested.
// Since I can't see the entity, I'll assume standard methods.

@Dao
interface GeneticAnalysisDao {
    @Query("SELECT * FROM genetic_analysis WHERE (maleProductId = :maleId AND femaleProductId = :femaleId) OR (maleProductId = :femaleId AND femaleProductId = :maleId)")
    suspend fun findByPair(maleId: String, femaleId: String): GeneticAnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(analysis: GeneticAnalysisEntity)

    @Query("SELECT * FROM genetic_analysis WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun fetchUpdatedGeneticAnalyses(since: Long, limit: Int = 500): List<GeneticAnalysisEntity>

    @Query("SELECT * FROM genetic_analysis WHERE dirty = 1 LIMIT :limit")
    suspend fun getDirty(limit: Int = 100): List<GeneticAnalysisEntity>

    @Query("UPDATE genetic_analysis SET dirty = 0, syncedAt = :syncedAt WHERE analysisId = :analysisId")
    suspend fun clearDirty(analysisId: String, syncedAt: Long)

    @Upsert
    suspend fun upsert(analysis: GeneticAnalysisEntity)
}