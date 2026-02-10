package com.rio.rostry.domain.integrity

import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ClutchEntity
import com.rio.rostry.data.database.entity.ProductEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataIntegrityManager - Transactional wrappers for critical multi-entity operations.
 *
 * Ensures atomicity when:
 * - Recording a hatch (create chicks + update clutch + link offspring)
 * - Transferring ownership (lock records + update seller + log audit)
 * - Recording mortality (update product status + create record + update dashboard)
 * - Completing a breeding cycle (finalize clutch + update pair stats)
 */
@Singleton
class DataIntegrityManager @Inject constructor(
    private val db: AppDatabase
) {
    /**
     * Atomically record a completed hatch:
     * 1. Creates ProductEntity entries for each chick
     * 2. Updates the ClutchEntity with hatch results
     * 3. Links offspring IDs to the clutch
     * 4. Updates the breeding pair statistics
     */
    suspend fun recordHatch(
        clutch: ClutchEntity,
        chicks: List<ProductEntity>,
        chicksHatched: Int,
        deadInShell: Int
    ) {
        db.runInTransaction {
            kotlinx.coroutines.runBlocking {
                // 1. Insert all chick records
                val chickIds = mutableListOf<String>()
                chicks.forEach { chick ->
                    db.productDao().insertProduct(chick)
                    chickIds.add(chick.productId)
                }

                // 2. Update clutch with results
                val offspringJson = com.google.gson.Gson().toJson(chickIds)
                val updatedClutch = clutch.copy(
                    chicksHatched = chicksHatched,
                    deadInShell = deadInShell,
                    actualHatchEndDate = System.currentTimeMillis(),
                    status = "COMPLETE",
                    offspringIdsJson = offspringJson,
                    fertilityRate = clutch.calculateFertilityRate(),
                    hatchabilityOfFertile = clutch.calculateHatchabilityOfFertile(),
                    hatchabilityOfSet = clutch.calculateHatchabilityOfSet(),
                    updatedAt = System.currentTimeMillis()
                )
                db.clutchDao().update(updatedClutch)

                // 3. Update breeding pair stats if linked
                clutch.breedingPairId?.let { pairId ->
                    val pair = db.breedingPairDao().getById(pairId)
                    pair?.let {
                        val updatedPair = it.copy(
                            hatchedEggs = (it.hatchedEggs ?: 0) + chicksHatched,
                            hatchSuccessRate = updatedClutch.hatchabilityOfSet ?: it.hatchSuccessRate,
                            updatedAt = System.currentTimeMillis()
                        )
                        db.breedingPairDao().upsert(updatedPair)
                    }
                }
            }
        }
    }

    /**
     * Atomically transfer bird ownership:
     * 1. Lock the bird's records (prevent future edits by old owner)
     * 2. Update seller ID
     * 3. Preserve lineage chain
     */
    suspend fun transferOwnership(
        productId: String,
        newOwnerId: String,
        transferId: String
    ) {
        db.runInTransaction {
            kotlinx.coroutines.runBlocking {
                val now = System.currentTimeMillis()

                // 1. Lock records
                db.productDao().lockRecords(productId, now, now)

                // 2. Update ownership
                db.productDao().updateSellerIdAndTouch(productId, newOwnerId, now)

                // 3. Increment edit count for audit trail
                db.productDao().incrementEditCount(productId, "SYSTEM_TRANSFER", now)
            }
        }
    }

    /**
     * Atomically record bird mortality:
     * 1. Update bird lifecycle status to DECEASED
     * 2. Create mortality record
     * 3. Update dashboard cache
     */
    suspend fun recordMortality(
        productId: String,
        farmerId: String,
        causeCategory: String,
        circumstances: String?,
        quantity: Int = 1
    ) {
        db.runInTransaction {
            kotlinx.coroutines.runBlocking {
                val now = System.currentTimeMillis()

                // 1. Update product status
                db.productDao().updateLifecycleStatus(productId, "DECEASED", now)

                // 2. Create mortality record
                val mortalityRecord = com.rio.rostry.data.database.entity.MortalityRecordEntity(
                    deathId = java.util.UUID.randomUUID().toString(),
                    productId = productId,
                    farmerId = farmerId,
                    causeCategory = causeCategory,
                    circumstances = circumstances,
                    quantity = quantity,
                    occurredAt = now,
                    updatedAt = now
                )
                db.mortalityRecordDao().insert(mortalityRecord)
            }
        }
    }

    /**
     * Atomically complete a breeding cycle:
     * 1. Finalize clutch status
     * 2. Update breeding pair availability
     */
    suspend fun completeBreedingCycle(
        clutchId: String,
        pairId: String?
    ) {
        db.runInTransaction {
            kotlinx.coroutines.runBlocking {
                val now = System.currentTimeMillis()

                // 1. Finalize clutch
                val clutch = db.clutchDao().findById(clutchId)
                clutch?.let {
                    db.clutchDao().update(it.copy(
                        status = "COMPLETE",
                        updatedAt = now
                    ))
                }

                // 2. Update breeding pair
                pairId?.let { id ->
                    val pair = db.breedingPairDao().getById(id)
                    pair?.let {
                        db.breedingPairDao().upsert(it.copy(
                            status = "AVAILABLE",
                            updatedAt = now
                        ))
                    }
                }
            }
        }
    }

    /**
     * Validate referential integrity across core tables.
     * Returns a list of issues found.
     */
    suspend fun validateIntegrity(): List<IntegrityIssue> {
        val issues = mutableListOf<IntegrityIssue>()

        // Check for orphaned products (seller doesn't exist)
        val orphaned = db.productDao().getProductsWithMissingSellers()
        orphaned.forEach {
            issues.add(IntegrityIssue(
                type = IssueType.ORPHANED_PRODUCT,
                entityId = it.productId,
                message = "Product '${it.name}' references non-existent seller '${it.sellerId}'"
            ))
        }

        // Check for missing bird codes
        val missingCodes = db.productDao().getProductsWithMissingBirdCodes()
        missingCodes.forEach {
            issues.add(IntegrityIssue(
                type = IssueType.MISSING_BIRD_CODE,
                entityId = it.productId,
                message = "Product '${it.name}' is missing birdCode or colorTag"
            ))
        }

        return issues
    }

    data class IntegrityIssue(
        val type: IssueType,
        val entityId: String,
        val message: String
    )

    enum class IssueType {
        ORPHANED_PRODUCT,
        MISSING_BIRD_CODE,
        BROKEN_LINEAGE,
        DUPLICATE_RECORD,
        STALE_DATA
    }
}
