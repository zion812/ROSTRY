package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyTreeDao {
    @Upsert
    suspend fun upsertAll(items: List<FamilyTreeEntity>)

    @Upsert
    suspend fun upsert(item: FamilyTreeEntity)

    @Query("SELECT * FROM family_tree WHERE treeId = :treeId LIMIT 1")
    suspend fun findById(treeId: String): FamilyTreeEntity?

    @Query("SELECT * FROM family_tree WHERE productId = :productId AND isDeleted = 0")
    fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>>

    @Query("SELECT * FROM family_tree WHERE assetId = :assetId AND isDeleted = 0")
    suspend fun getParentsForAsset(assetId: String): List<FamilyTreeEntity>

    @Query("SELECT COUNT(*) FROM family_tree WHERE assetId = :assetId")
    suspend fun hasDocumentation(assetId: String): Boolean
    
    // Fallback: Check if asset exists in farm_assets
    @Query("SELECT COUNT(*) FROM farm_assets WHERE assetId = :assetId AND isDeleted = 0")
    suspend fun assetExists(assetId: String): Boolean

    @Query("SELECT * FROM family_tree WHERE updatedAt > :since ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getUpdatedSince(since: Long, limit: Int = 500): List<FamilyTreeEntity>

    @Query("DELETE FROM family_tree WHERE isDeleted = 1")
    suspend fun purgeDeleted()
}
