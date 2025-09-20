package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.PoultryTrait
import kotlinx.coroutines.flow.Flow

@Dao
interface PoultryTraitDao {
    @Query("SELECT * FROM poultry_traits WHERE poultryId = :poultryId")
    fun getTraitsByPoultryId(poultryId: String): Flow<List<PoultryTrait>>

    @Query("SELECT * FROM poultry_traits WHERE traitId = :traitId")
    fun getPoultryByTraitId(traitId: String): Flow<List<PoultryTrait>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoultryTrait(poultryTrait: PoultryTrait)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoultryTraits(poultryTraits: List<PoultryTrait>)

    @Query("DELETE FROM poultry_traits WHERE poultryId = :poultryId AND traitId = :traitId")
    suspend fun deletePoultryTrait(poultryId: String, traitId: String)

    @Query("DELETE FROM poultry_traits WHERE poultryId = :poultryId")
    suspend fun deleteAllTraitsForPoultry(poultryId: String)
}