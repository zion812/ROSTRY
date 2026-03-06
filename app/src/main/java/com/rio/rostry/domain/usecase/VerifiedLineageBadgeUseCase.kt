package com.rio.rostry.domain.usecase

import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.dao.FarmAssetDao
import javax.inject.Inject

class VerifiedLineageBadgeUseCase @Inject constructor(
    private val familyTreeDao: FamilyTreeDao,
    private val farmAssetDao: FarmAssetDao
) {
    /**
     * Checks if a bird has an unbroken chain of documentation up to 3 generations.
     * Returns true if all ancestors found in the tree exist as documented farm assets.
     */
    suspend operator fun invoke(assetId: String): Boolean {
        return checkLineageRecursively(assetId, currentDepth = 0, maxDepth = 2)
    }

    private suspend fun checkLineageRecursively(assetId: String, currentDepth: Int, maxDepth: Int): Boolean {
        if (currentDepth > maxDepth) return true

        val parents = familyTreeDao.getParentsForAsset(assetId)
        if (parents.isEmpty() && currentDepth == 0) {
            val asset = farmAssetDao.findById(assetId)
            return asset != null && (asset.origin == "PURCHASED" || asset.origin == "HATCHED_ON_FARM")
        }
        
        // At each level, check if the parents themselves exist and are documented
        for (parentRelation in parents) {
            val parentId = parentRelation.parentAssetId ?: return false // Parent ID missing in tree
            
            // Verify parent exists in our local or synced DB
            val parentExists = farmAssetDao.findById(parentId) != null
            if (!parentExists) return false

            // Recurse to check parent's own lineage
            if (!checkLineageRecursively(parentId, currentDepth + 1, maxDepth)) {
                return false
            }
        }
        return parents.isNotEmpty()
    }
}
