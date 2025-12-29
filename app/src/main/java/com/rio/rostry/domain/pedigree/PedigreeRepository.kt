package com.rio.rostry.domain.pedigree

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PedigreeRepository - Builds and manages recursive pedigree trees for Enthusiast users.
 * 
 * Key Features:
 * - Recursive 3-generation tree building (Parents → Grandparents → Great-Grandparents)
 * - Guest parent support for birds from external sources
 * - Pedigree completeness scoring for market value assessment
 * 
 * Uses ProductEntity.parentMaleId and ProductEntity.parentFemaleId for lineage tracking.
 */
interface PedigreeRepository {
    /**
     * Build a complete pedigree tree for a bird up to specified depth.
     * @param birdId The root bird's product ID
     * @param maxDepth Maximum generations to traverse (default 3)
     * @return PedigreeTree with recursive sire/dam branches
     */
    suspend fun getFullPedigree(birdId: String, maxDepth: Int = 3): Resource<PedigreeTree>
    
    /**
     * Link parents to a bird.
     * @param birdId The child bird's ID
     * @param sireId Father's product ID (optional)
     * @param damId Mother's product ID (optional)
     */
    suspend fun linkParents(birdId: String, sireId: String?, damId: String?): Resource<Unit>
    
    /**
     * Get all potential parents for selection (same owner, opposite gender where applicable).
     * @param ownerId Owner's user ID
     * @param excludeId Bird ID to exclude from results (the child itself)
     * @param gender Filter by gender ("male" for sire selection, "female" for dam selection)
     */
    suspend fun getPotentialParents(ownerId: String, excludeId: String, gender: String?): Resource<List<ProductEntity>>
    
    /**
     * Calculate pedigree completeness score for a bird.
     * @return Completeness level (NONE, ONE_GEN, TWO_GEN, THREE_GEN)
     */
    suspend fun getPedigreeCompleteness(birdId: String): Resource<PedigreeCompleteness>
    
    /**
     * Get all offspring (descendants) of a bird.
     * @param parentId The parent bird's ID
     * @return List of direct children
     */
    suspend fun getOffspring(parentId: String): Resource<List<ProductEntity>>
}

@Singleton
class PedigreeRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : PedigreeRepository {
    
    override suspend fun getFullPedigree(birdId: String, maxDepth: Int): Resource<PedigreeTree> {
        return withContext(Dispatchers.IO) {
            try {
                val rootBird = productDao.findById(birdId)
                    ?: return@withContext Resource.Error("Bird not found")
                
                val tree = buildPedigreeTree(rootBird, 0, maxDepth)
                Resource.Success(tree)
            } catch (e: Exception) {
                Timber.e(e, "Failed to build pedigree for $birdId")
                Resource.Error(e.message ?: "Failed to build pedigree")
            }
        }
    }
    
    /**
     * Recursively build the pedigree tree by traversing parentMaleId and parentFemaleId.
     */
    private suspend fun buildPedigreeTree(
        bird: ProductEntity,
        currentDepth: Int,
        maxDepth: Int
    ): PedigreeTree {
        // Base case: max depth reached
        if (currentDepth >= maxDepth) {
            return PedigreeTree(
                bird = PedigreeBird.fromProduct(bird),
                sire = null,
                dam = null,
                generation = currentDepth
            )
        }
        
        // Recursively fetch sire (father) tree
        val sireTree = bird.parentMaleId?.let { sireId ->
            productDao.findById(sireId)?.let { sire ->
                buildPedigreeTree(sire, currentDepth + 1, maxDepth)
            }
        }
        
        // Recursively fetch dam (mother) tree
        val damTree = bird.parentFemaleId?.let { damId ->
            productDao.findById(damId)?.let { dam ->
                buildPedigreeTree(dam, currentDepth + 1, maxDepth)
            }
        }
        
        return PedigreeTree(
            bird = PedigreeBird.fromProduct(bird),
            sire = sireTree,
            dam = damTree,
            generation = currentDepth
        )
    }
    
    override suspend fun linkParents(birdId: String, sireId: String?, damId: String?): Resource<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val bird = productDao.findById(birdId)
                    ?: return@withContext Resource.Error("Bird not found")
                
                // Validate sire is male if provided
                sireId?.let { id ->
                    val sire = productDao.findById(id)
                    if (sire != null && sire.gender?.lowercase() == "female") {
                        return@withContext Resource.Error("Sire must be male")
                    }
                }
                
                // Validate dam is female if provided
                damId?.let { id ->
                    val dam = productDao.findById(id)
                    if (dam != null && dam.gender?.lowercase() == "male") {
                        return@withContext Resource.Error("Dam must be female")
                    }
                }
                
                // Update bird with parent IDs
                val updated = bird.copy(
                    parentMaleId = sireId ?: bird.parentMaleId,
                    parentFemaleId = damId ?: bird.parentFemaleId,
                    updatedAt = System.currentTimeMillis(),
                    dirty = true
                )
                productDao.upsert(updated)
                
                Timber.d("Linked parents for $birdId: sire=$sireId, dam=$damId")
                Resource.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Failed to link parents for $birdId")
                Resource.Error(e.message ?: "Failed to link parents")
            }
        }
    }
    
    override suspend fun getPotentialParents(
        ownerId: String,
        excludeId: String,
        gender: String?
    ): Resource<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                // Get all birds owned by this user that are not batches
                val allBirds = productDao.getActiveWithBirth().filter { bird ->
                    bird.sellerId == ownerId &&
                    bird.productId != excludeId &&
                    bird.isBatch != true &&
                    !bird.isDeleted
                }
                
                // Filter by gender if specified
                val filtered = if (gender != null) {
                    allBirds.filter { it.gender?.lowercase() == gender.lowercase() }
                } else {
                    allBirds
                }
                
                Resource.Success(filtered)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get potential parents")
                Resource.Error(e.message ?: "Failed to get potential parents")
            }
        }
    }
    
    override suspend fun getPedigreeCompleteness(birdId: String): Resource<PedigreeCompleteness> {
        return when (val result = getFullPedigree(birdId, 3)) {
            is Resource.Success -> Resource.Success(result.data!!.completeness)
            is Resource.Error -> Resource.Error(result.message ?: "Failed to get completeness")
            is Resource.Loading -> Resource.Loading()
        }
    }
    
    override suspend fun getOffspring(parentId: String): Resource<List<ProductEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                // Find all birds where parentMaleId or parentFemaleId matches
                val offspring = productDao.getActiveWithBirth().filter { bird ->
                    bird.parentMaleId == parentId || bird.parentFemaleId == parentId
                }
                Resource.Success(offspring)
            } catch (e: Exception) {
                Timber.e(e, "Failed to get offspring for $parentId")
                Resource.Error(e.message ?: "Failed to get offspring")
            }
        }
    }
}
