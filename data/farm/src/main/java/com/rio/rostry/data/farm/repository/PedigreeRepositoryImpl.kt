package com.rio.rostry.data.farm.repository

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.farm.repository.PedigreeRepository
import com.rio.rostry.domain.farm.model.PedigreeTree
import com.rio.rostry.domain.farm.model.PedigreeBird
import com.rio.rostry.domain.farm.model.PedigreeCompleteness
import com.rio.rostry.core.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PedigreeRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : PedigreeRepository {

    override suspend fun getFullPedigree(birdId: String, maxDepth: Int): Result<PedigreeTree> {
        return withContext(Dispatchers.IO) {
            try {
                val rootBird = productDao.findById(birdId)
                    ?: return@withContext Result.Error(Exception("Bird not found"))
                val tree = buildPedigreeTree(rootBird, 0, maxDepth)
                Result.Success(tree)
            } catch (e: Exception) {
                Timber.e(e, "Failed to build pedigree for $birdId")
                Result.Error(e)
            }
        }
    }

    private suspend fun buildPedigreeTree(bird: ProductEntity, currentDepth: Int, maxDepth: Int): PedigreeTree {
        if (currentDepth >= maxDepth) {
            return PedigreeTree(bird = PedigreeBird.fromProduct(bird), sire = null, dam = null, generation = currentDepth)
        }
        val sireTree = bird.parentMaleId?.let { sireId ->
            productDao.findById(sireId)?.let { sire -> buildPedigreeTree(sire, currentDepth + 1, maxDepth) }
        }
        val damTree = bird.parentFemaleId?.let { damId ->
            productDao.findById(damId)?.let { dam -> buildPedigreeTree(dam, currentDepth + 1, maxDepth) }
        }
        return PedigreeTree(bird = PedigreeBird.fromProduct(bird), sire = sireTree, dam = damTree, generation = currentDepth)
    }

    override suspend fun linkParents(birdId: String, sireId: String?, damId: String?): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val bird = productDao.findById(birdId)
                    ?: return@withContext Result.Error(Exception("Bird not found"))
                sireId?.let { id ->
                    val sire = productDao.findById(id)
                    if (sire != null && sire.gender?.lowercase() == "female")
                        return@withContext Result.Error(Exception("Sire must be male"))
                }
                damId?.let { id ->
                    val dam = productDao.findById(id)
                    if (dam != null && dam.gender?.lowercase() == "male")
                        return@withContext Result.Error(Exception("Dam must be female"))
                }
                val updated = bird.copy(
                    parentMaleId = sireId ?: bird.parentMaleId,
                    parentFemaleId = damId ?: bird.parentFemaleId,
                    updatedAt = System.currentTimeMillis(), dirty = true
                )
                productDao.upsert(updated)
                Result.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Failed to link parents for $birdId")
                Result.Error(e)
            }
        }
    }

    override suspend fun getPotentialParents(ownerId: String, excludeId: String, gender: String?): Result<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val filtered = productDao.getPotentialParents(ownerId = ownerId, excludeId = excludeId, gender = gender)
                Result.Success(filtered)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get potential parents")
                Result.Error(e)
            }
        }
    }

    override suspend fun getPedigreeCompleteness(birdId: String): Result<PedigreeCompleteness> {
        return when (val result = getFullPedigree(birdId, 3)) {
            is Result.Success -> Result.Success(result.data.completeness)
            is Result.Error -> Result.Error(result.exception)
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun getOffspring(parentId: String): Result<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val offspring = productDao.getOffspring(parentId)
                Result.Success(offspring)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get offspring for $parentId")
                Result.Error(e)
            }
        }
    }
}
