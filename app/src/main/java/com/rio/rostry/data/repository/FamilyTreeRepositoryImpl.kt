package com.rio.rostry.data.repository

import com.rio.rostry.data.local.FamilyTreeDao
import com.rio.rostry.domain.model.FamilyTree as DomainFamilyTree
import com.rio.rostry.data.model.FamilyTree as DataFamilyTree
import com.rio.rostry.domain.repository.FamilyTreeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class FamilyTreeRepositoryImpl @Inject constructor(
    private val familyTreeDao: FamilyTreeDao
) : BaseRepository(), FamilyTreeRepository {

    override fun getAllFamilyTrees(): Flow<List<DomainFamilyTree>> {
        return familyTreeDao.getAllFamilyTrees().map { trees ->
            trees.map { it.toDomainModel() }
        }
    }

    override fun getChildrenByParentId(parentId: String): Flow<List<DomainFamilyTree>> {
        return familyTreeDao.getChildrenByParentId(parentId).map { trees ->
            trees.map { it.toDomainModel() }
        }
    }

    override fun getParentsByChildId(childId: String): Flow<List<DomainFamilyTree>> {
        return familyTreeDao.getParentsByChildId(childId).map { trees ->
            trees.map { it.toDomainModel() }
        }
    }

    override suspend fun getFamilyTreeById(id: String): DomainFamilyTree? {
        return familyTreeDao.getFamilyTreeById(id)?.toDomainModel()
    }

    override suspend fun insertFamilyTree(familyTree: DomainFamilyTree) {
        familyTreeDao.insertFamilyTree(familyTree.toDataModel())
    }

    override suspend fun updateFamilyTree(familyTree: DomainFamilyTree) {
        val updatedTree = familyTree.copy(updatedAt = Date())
        familyTreeDao.updateFamilyTree(updatedTree.toDataModel())
    }

    override suspend fun deleteFamilyTree(id: String) {
        familyTreeDao.getFamilyTreeById(id)?.let { familyTreeDao.deleteFamilyTree(it) }
    }

    private fun DataFamilyTree.toDomainModel(): DomainFamilyTree {
        return DomainFamilyTree(
            id = id,
            parentId = parentId,
            childId = childId,
            relationshipType = relationshipType,
            generation = generation,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainFamilyTree.toDataModel(): DataFamilyTree {
        return DataFamilyTree(
            id = id,
            parentId = parentId,
            childId = childId,
            relationshipType = relationshipType,
            generation = generation,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}