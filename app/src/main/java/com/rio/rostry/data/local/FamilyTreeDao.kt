package com.rio.rostry.data.local

import androidx.room.*
import com.rio.rostry.data.model.FamilyTree
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyTreeDao {
    @Query("SELECT * FROM family_tree ORDER BY generation ASC")
    fun getAllFamilyTrees(): Flow<List<FamilyTree>>

    @Query("SELECT * FROM family_tree WHERE parentId = :parentId")
    fun getChildrenByParentId(parentId: String): Flow<List<FamilyTree>>

    @Query("SELECT * FROM family_tree WHERE childId = :childId")
    fun getParentsByChildId(childId: String): Flow<List<FamilyTree>>

    @Query("SELECT * FROM family_tree WHERE id = :id")
    suspend fun getFamilyTreeById(id: String): FamilyTree?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyTree(familyTree: FamilyTree)

    @Update
    suspend fun updateFamilyTree(familyTree: FamilyTree)

    @Delete
    suspend fun deleteFamilyTree(familyTree: FamilyTree)

    // Temporarily removing purge method
    // @Query("DELETE FROM family_tree WHERE isDeleted = 1 AND deletedAt < :beforeTimestamp")
    // suspend fun purgeDeletedFamilyTrees(beforeTimestamp: Long)
}