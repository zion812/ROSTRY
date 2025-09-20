package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.BreedingRecordDao
import com.rio.rostry.data.local.PoultryDao
import com.rio.rostry.domain.model.FamilyTreeNode
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BuildFamilyTreeUseCase @Inject constructor(
    private val poultryDao: PoultryDao,
    private val breedingRecordDao: BreedingRecordDao
) {
    suspend operator fun invoke(rootPoultryId: String, maxDepth: Int = 3): FamilyTreeNode? {
        val rootPoultry = poultryDao.getPoultryById(rootPoultryId) ?: return null
        return buildNode(rootPoultry, maxDepth)
    }
    
    private suspend fun buildNode(poultry: com.rio.rostry.data.model.Poultry, remainingDepth: Int): FamilyTreeNode {
        if (remainingDepth <= 0) {
            return FamilyTreeNode(poultry = poultry)
        }
        
        // Get parents
        val parents = mutableListOf<com.rio.rostry.data.model.Poultry>()
        poultry.parentId1?.let { parentId ->
            poultryDao.getPoultryById(parentId)?.let { parents.add(it) }
        }
        poultry.parentId2?.let { parentId ->
            poultryDao.getPoultryById(parentId)?.let { parents.add(it) }
        }
        
        // Get children
        val children = poultryDao.getChildrenByParentId(poultry.id).first()
        
        // Get breeding records
        val breedingRecords = breedingRecordDao.getBreedingRecordsByParentId(poultry.id).first()
        
        // Build parent nodes
        val parentNodes = parents.map { parent ->
            buildNode(parent, remainingDepth - 1)
        }
        
        // Build child nodes
        val childNodes = children.map { child ->
            buildNode(child, remainingDepth - 1)
        }
        
        return FamilyTreeNode(
            poultry = poultry,
            parents = parentNodes,
            children = childNodes,
            breedingRecords = breedingRecords
        )
    }
}