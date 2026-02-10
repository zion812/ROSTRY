package com.rio.rostry.domain.transfer

import com.google.gson.Gson
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.domain.trust.DocumentationScoreCalculator
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds documentation snapshots when birds are transferred between users.
 *
 * Captures text-based records (pedigree, health, growth, breeding) into JSON snapshots
 * stored on TransferEntity. Media (images/video) is excluded to keep transfer lightweight.
 *
 * Flow:
 * 1. Seller initiates transfer → buildDocumentationSnapshot() called
 * 2. Snapshot JSON fields populated on TransferEntity
 * 3. Buyer claims transfer → snapshot data available in their received transfer record
 * 4. Buyer can view the seller's documentation history for the acquired bird
 */
@Singleton
class TransferDocumentationService @Inject constructor(
    private val db: AppDatabase,
    private val documentationScoreCalculator: DocumentationScoreCalculator,
    private val gson: Gson
) {

    /**
     * Build a complete documentation snapshot for a bird at transfer time.
     *
     * @param productId The bird being transferred
     * @return A snapshot that can be serialized to TransferEntity JSON fields
     */
    suspend fun buildDocumentationSnapshot(productId: String): TransferDocumentationSnapshot {
        val product = db.productDao().findById(productId)

        // 1. Bird identity (text only, no media)
        val identity = product?.let {
            BirdIdentitySnapshot(
                productId = it.productId,
                name = it.name,
                birdCode = it.birdCode,
                breed = it.breed,
                gender = it.gender,
                color = it.color,
                colorTag = it.colorTag,
                birthDate = it.birthDate,
                ageWeeks = it.ageWeeks,
                weightGrams = it.weightGrams,
                heightCm = it.heightCm,
                stage = it.stage?.name,
                raisingPurpose = it.raisingPurpose,
                breedingStatus = it.breedingStatus,
                snapshotAt = System.currentTimeMillis()
            )
        }

        // 2. Lineage — parents and pedigree depth
        val lineage = buildLineageSnapshot(productId, product)

        // 3. Health — vaccination + health status
        val health = buildHealthSnapshot(product)

        // 4. Breeding history
        val breeding = buildBreedingSnapshot(productId, product?.sellerId)

        // 5. Documentation score at transfer time
        val docScore = product?.let {
            documentationScoreCalculator.calculate(
                product = it,
                hasVaccinationRecords = health.vaccinationCount > 0,
                hasFcrData = false,
                hasGrowthRecords = false,
                pedigreeDepth = lineage.pedigreeDepth,
                medicalRecordCount = 0,
                photoCount = it.imageUrls.size
            )
        }

        return TransferDocumentationSnapshot(
            identity = identity,
            lineage = lineage,
            health = health,
            breeding = breeding,
            documentationScoreAtTransfer = docScore?.overallScore ?: 0f,
            trustBadgeAtTransfer = docScore?.trustBadge?.name ?: "NONE",
            snapshotCreatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Serialize snapshot fields to JSON and set them on a TransferEntity.
     */
    fun applySnapshotToTransfer(
        transfer: TransferEntity,
        snapshot: TransferDocumentationSnapshot
    ): TransferEntity {
        return transfer.copy(
            lineageSnapshotJson = gson.toJson(snapshot.lineage),
            growthSnapshotJson = gson.toJson(
                mapOf(
                    "identity" to snapshot.identity,
                    "breeding" to snapshot.breeding,
                    "documentationScore" to snapshot.documentationScoreAtTransfer,
                    "trustBadge" to snapshot.trustBadgeAtTransfer
                )
            ),
            healthSnapshotJson = gson.toJson(snapshot.health)
        )
    }

    // ========== Private snapshot builders ==========

    private suspend fun buildLineageSnapshot(
        productId: String,
        product: com.rio.rostry.data.database.entity.ProductEntity?
    ): LineageSnapshot {
        val familyLinks = db.familyTreeDao().getForProduct(productId).firstOrNull() ?: emptyList()
        val parentIds = mutableListOf<String>()

        product?.parentMaleId?.let { parentIds.add(it) }
        product?.parentFemaleId?.let { parentIds.add(it) }

        // Get parent details (one level up)
        val parents = parentIds.mapNotNull { parentId ->
            db.productDao().findById(parentId)?.let { parent ->
                ParentSnapshot(
                    productId = parent.productId,
                    name = parent.name,
                    birdCode = parent.birdCode,
                    breed = parent.breed,
                    gender = parent.gender,
                    color = parent.color
                )
            }
        }

        // Determine pedigree depth (0, 1, or 2+)
        var pedigreeDepth = 0
        if (parents.isNotEmpty()) {
            pedigreeDepth = 1
            val hasGrandparent = parents.any { parent ->
                val gp = db.productDao().findById(parent.productId)
                gp?.parentMaleId != null || gp?.parentFemaleId != null
            }
            if (hasGrandparent) pedigreeDepth = 2
        }

        return LineageSnapshot(
            parentMaleId = product?.parentMaleId,
            parentFemaleId = product?.parentFemaleId,
            parents = parents,
            familyTreeLinkCount = familyLinks.size,
            pedigreeDepth = pedigreeDepth,
            parentIdsJson = product?.parentIdsJson
        )
    }

    private fun buildHealthSnapshot(
        product: com.rio.rostry.data.database.entity.ProductEntity?
    ): HealthSnapshot {
        val vaccinationCount = product?.vaccinationRecordsJson?.let {
            try {
                gson.fromJson(it, List::class.java)?.size ?: 0
            } catch (_: Exception) { 0 }
        } ?: 0

        return HealthSnapshot(
            currentHealthStatus = product?.healthStatus ?: "UNKNOWN",
            vaccinationRecordsJson = product?.vaccinationRecordsJson,
            vaccinationCount = vaccinationCount
        )
    }

    private suspend fun buildBreedingSnapshot(
        productId: String,
        sellerId: String?
    ): BreedingSnapshot {
        if (sellerId == null) return BreedingSnapshot(false, 0, 0)

        val asMale = try {
            db.breedingPairDao().countActiveByMale(sellerId, productId)
        } catch (_: Exception) { 0 }

        val asFemale = try {
            db.breedingPairDao().countActiveByFemale(sellerId, productId)
        } catch (_: Exception) { 0 }

        return BreedingSnapshot(
            isBreeder = asMale > 0 || asFemale > 0,
            activePairsAsMale = asMale,
            activePairsAsFemale = asFemale
        )
    }
}

// ========== Snapshot Data Classes (serialized to JSON, no media) ==========

data class TransferDocumentationSnapshot(
    val identity: BirdIdentitySnapshot?,
    val lineage: LineageSnapshot,
    val health: HealthSnapshot,
    val breeding: BreedingSnapshot,
    val documentationScoreAtTransfer: Float,
    val trustBadgeAtTransfer: String,
    val snapshotCreatedAt: Long
)

data class BirdIdentitySnapshot(
    val productId: String,
    val name: String,
    val birdCode: String?,
    val breed: String?,
    val gender: String?,
    val color: String?,
    val colorTag: String?,
    val birthDate: Long?,
    val ageWeeks: Int?,
    val weightGrams: Double?,
    val heightCm: Double?,
    val stage: String?,
    val raisingPurpose: String?,
    val breedingStatus: String?,
    val snapshotAt: Long
    // NO imageUrls — media excluded from transfer
)

data class LineageSnapshot(
    val parentMaleId: String?,
    val parentFemaleId: String?,
    val parents: List<ParentSnapshot>,
    val familyTreeLinkCount: Int,
    val pedigreeDepth: Int,
    val parentIdsJson: String?
)

data class ParentSnapshot(
    val productId: String,
    val name: String,
    val birdCode: String?,
    val breed: String?,
    val gender: String?,
    val color: String?
)

data class HealthSnapshot(
    val currentHealthStatus: String,
    val vaccinationRecordsJson: String?,
    val vaccinationCount: Int
)

data class BreedingSnapshot(
    val isBreeder: Boolean,
    val activePairsAsMale: Int,
    val activePairsAsFemale: Int
)
