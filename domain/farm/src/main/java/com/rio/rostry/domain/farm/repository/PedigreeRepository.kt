package com.rio.rostry.domain.farm.repository

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.core.common.Result
import com.rio.rostry.domain.farm.model.PedigreeTree
import com.rio.rostry.domain.farm.model.PedigreeCompleteness

/**
 * Domain interface for pedigree (lineage tree) operations.
 * Migrated from app module as part of Phase 1 repository migration.
 */
interface PedigreeRepository {
    suspend fun getFullPedigree(birdId: String, maxDepth: Int = 3): Result<PedigreeTree>
    suspend fun linkParents(birdId: String, sireId: String?, damId: String?): Result<Unit>
    suspend fun getPotentialParents(ownerId: String, excludeId: String, gender: String?): Result<List<ProductEntity>>
    suspend fun getPedigreeCompleteness(birdId: String): Result<PedigreeCompleteness>
    suspend fun getOffspring(parentId: String): Result<List<ProductEntity>>
}

