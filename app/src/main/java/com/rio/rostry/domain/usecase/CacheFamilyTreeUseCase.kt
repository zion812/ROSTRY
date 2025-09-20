package com.rio.rostry.domain.usecase

import com.rio.rostry.data.local.PoultryFamilyTreeCache
import com.rio.rostry.domain.model.FamilyTreeNode
import javax.inject.Inject

class CacheFamilyTreeUseCase @Inject constructor(
    private val cache: PoultryFamilyTreeCache
) {
    operator fun invoke(rootPoultryId: String, familyTree: FamilyTreeNode) {
        cache.putFamilyTree(rootPoultryId, familyTree)
    }
}