package com.rio.rostry.domain.repository

import com.rio.rostry.domain.model.FamilyTree
import kotlinx.coroutines.flow.Flow

interface FamilyTreeRepository {
    fun getAllFamilyTrees(): Flow<List<FamilyTree>>
    fun getChildrenByParentId(parentId: String): Flow<List<FamilyTree>>
    fun getParentsByChildId(childId: String): Flow<List<FamilyTree>>
    suspend fun getFamilyTreeById(id: String): FamilyTree?
    suspend fun insertFamilyTree(familyTree: FamilyTree)
    suspend fun updateFamilyTree(familyTree: FamilyTree)
    suspend fun deleteFamilyTree(id: String)
}