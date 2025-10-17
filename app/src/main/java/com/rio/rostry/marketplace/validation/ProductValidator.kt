package com.rio.rostry.marketplace.validation

import com.rio.rostry.data.database.dao.DailyLogDao
import com.rio.rostry.data.database.dao.GrowthRecordDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.dao.VaccinationRecordDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.marketplace.model.AgeGroup
import com.rio.rostry.marketplace.model.ProductCategory
import com.rio.rostry.data.repository.TraceabilityRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

/**
 * Validates product listings for marketplace rules, including farm data freshness.
 */
@Singleton
class ProductValidator @Inject constructor(
    private val traceabilityRepository: TraceabilityRepository,
    private val vaccinationDao: VaccinationRecordDao,
    private val growthDao: GrowthRecordDao,
    private val dailyLogDao: DailyLogDao,
    private val quarantineDao: QuarantineRecordDao
) {

    // Freshness thresholds for farm data validation
    private companion object {
        const val VACCINATION_FRESHNESS_DAYS = 30
        const val HEALTH_LOG_FRESHNESS_DAYS = 7
        const val GROWTH_RECORD_FRESHNESS_WEEKS = 2
    }

    data class ValidationResult(
        val valid: Boolean,
        val reasons: List<String> = emptyList()
    )

    fun validate(product: ProductEntity, now: Long = System.currentTimeMillis()): ValidationResult {
        val reasons = mutableListOf<String>()

        // Basic required fields
        if (product.name.isBlank()) reasons += "Name is required"
        if (product.price <= 0.0) reasons += "Price must be positive"
        if (product.quantity < 0.0) reasons += "Quantity cannot be negative"

        // Category mapping and rules
        val category = ProductCategory.fromString(product.category)
        
        // Location verification: mandatory only for traceable adoption
        if (product.latitude == null || product.longitude == null) {
            if (category is ProductCategory.AdoptionTraceable) {
                reasons += "Accurate location (latitude/longitude) is required for traceable adoption"
            }
        }

        // Image quality/count: only enforce if images are already present (not queued for upload)
        if (product.imageUrls.isNotEmpty() && product.imageUrls.size < 2) reasons += "At least 2 product images are required"

        // Lifecycle enforcement
        val lifecycle = product.lifecycleStatus?.uppercase()
        if (lifecycle == "QUARANTINE") {
            reasons += "Complete quarantine protocol before listing"
        } else if (lifecycle == "DECEASED") {
            reasons += "Cannot list deceased birds"
        } else if (lifecycle == "TRANSFERRED") {
            reasons += "This bird has been transferred. Update ownership records before listing"
        } else if (lifecycle == "SOLD") {
            reasons += "Cannot re-list sold items"
        }

        // Age group mapping
        val ageGroup = AgeGroup.fromBirthDate(product.birthDate, now)

        // Age group validations
        when (ageGroup) {
            AgeGroup.CHICK_0_5_WEEKS -> {
                if (product.vaccinationRecordsJson.isNullOrBlank()) {
                    reasons += "Vaccination records are required for 0-5 weeks"
                }
            }
            AgeGroup.YOUNG_5_20_WEEKS -> {
                // Require growth monitoring: at least weight or height present
                if (product.weightGrams == null && product.heightCm == null) {
                    reasons += "Growth monitoring data (weight/height) required for 5-20 weeks"
                }
            }
            AgeGroup.ADULT_20_52_WEEKS -> {
                val gender = product.gender?.lowercase()
                if (gender.isNullOrBlank() || gender == "unknown") {
                    reasons += "Gender identification required for 20-52 weeks"
                }
            }
            AgeGroup.BREEDER_12_MONTHS_PLUS -> {
                if (product.breedingStatus.isNullOrBlank()) {
                    reasons += "Breeding history/status required for 12+ months"
                }
            }
            null -> Unit
        }

        // Category-specific requirements
        when (category) {
            is ProductCategory.Meat -> {
                // Meat should not require family tree
                if (!product.familyTreeId.isNullOrBlank()) {
                    // allow but warn? We'll not invalidate
                }
            }
            is ProductCategory.AdoptionTraceable -> {
                if (product.familyTreeId.isNullOrBlank()) {
                    reasons += "Family tree documentation is required for traceable adoption"
                }
                // Require explicit parent links for traceable adoption
                if (product.parentMaleId.isNullOrBlank() || product.parentFemaleId.isNullOrBlank()) {
                    reasons += "Both parentMaleId and parentFemaleId are required for traceable adoption"
                }
            }
            is ProductCategory.AdoptionNonTraceable -> {
                // No lineage required
            }
            null -> {
                reasons += "Unknown or unsupported category"
            }
        }

        // Price reasonableness heuristic
        val price = product.price
        val suspicious = when (ageGroup) {
            AgeGroup.CHICK_0_5_WEEKS -> price !in 50.0..2000.0
            AgeGroup.YOUNG_5_20_WEEKS -> price !in 200.0..10000.0
            AgeGroup.ADULT_20_52_WEEKS -> price !in 500.0..20000.0
            AgeGroup.BREEDER_12_MONTHS_PLUS -> price !in 1000.0..50000.0
            null -> price <= 0.0
        }
        if (suspicious) reasons += "Price appears unreasonable for the selected age group"

        return ValidationResult(valid = reasons.isEmpty(), reasons = reasons)
    }

    /**
     * Checks if a product is currently in active quarantine.
     */
    suspend fun checkQuarantineStatus(productId: String): Boolean {
        val quarantineRecords = quarantineDao.observeForProduct(productId).first()
        return quarantineRecords.any { it.status == "ACTIVE" }
    }

    /**
     * Validates farm data freshness for the given product and category.
     * Only applies to traceable adoptions; meat category relies on existing quarantine checks.
     */
    suspend fun validateFarmDataFreshness(
        productId: String,
        category: ProductCategory,
        now: Long
    ): ValidationResult {
        val reasons = mutableListOf<String>()

        if (category is ProductCategory.AdoptionTraceable) {
            // Check vaccination freshness (within last 30 days)
            val vaccinationRecords = vaccinationDao.observeForProduct(productId).first()
            val hasRecentVaccination = vaccinationRecords.any { record ->
                record.administeredAt != null && (now - record.administeredAt) <= (VACCINATION_FRESHNESS_DAYS * 24 * 60 * 60 * 1000L)
            }
            if (!hasRecentVaccination) {
                reasons += "Traceable adoption requires vaccination within last 30 days"
            }

            // Check health log freshness (within last 7 days)
            val dailyLogs = dailyLogDao.observeForProduct(productId).first()
            val hasRecentHealthLog = dailyLogs.any { log ->
                (now - log.createdAt) <= (HEALTH_LOG_FRESHNESS_DAYS * 24 * 60 * 60 * 1000L)
            }
            if (!hasRecentHealthLog) {
                reasons += "Traceable adoption requires health log within last 7 days"
            }

            // Check growth record freshness (within last 2 weeks)
            val growthRecords = growthDao.observeForProduct(productId).first()
            val hasRecentGrowthRecord = growthRecords.any { record ->
                (now - record.createdAt) <= (GROWTH_RECORD_FRESHNESS_WEEKS * 7 * 24 * 60 * 60 * 1000L)
            }
            if (!hasRecentGrowthRecord) {
                reasons += "Traceable adoption requires growth monitoring within last 2 weeks"
            }
        }
        // For ProductCategory.Meat, no additional checks beyond existing quarantine validation

        return ValidationResult(valid = reasons.isEmpty(), reasons = reasons)
    }

    /**
     * Extended validation that performs lineage verification and farm data freshness checks.
     * Returns combined reasons from basic validation, lineage checks, and freshness validation.
     */
    suspend fun validateWithTraceability(
        product: ProductEntity,
        now: Long = System.currentTimeMillis(),
        sourceProductId: String? = null
    ): ValidationResult {
        val base = validate(product, now)
        val reasons = base.reasons.toMutableList()

        val category = ProductCategory.fromString(product.category)
        if (category is ProductCategory.AdoptionTraceable) {
            val male = product.parentMaleId
            val female = product.parentFemaleId
            if (!male.isNullOrBlank() && !female.isNullOrBlank()) {
                when (val res = traceabilityRepository.verifyParentage(product.productId, male, female)) {
                    is com.rio.rostry.utils.Resource.Success -> if (res.data != true) {
                        reasons += "Parentage verification failed for provided parentMaleId/parentFemaleId"
                    }
                    else -> reasons += "Unable to verify parentage at this time"
                }
            }
        }

        // Add farm data freshness validation only for existing products (prefilled flows)
        val productIdForFreshness = sourceProductId ?: product.productId
        if (!productIdForFreshness.isNullOrBlank()) {
            val freshnessResult = validateFarmDataFreshness(productIdForFreshness, category ?: ProductCategory.AdoptionNonTraceable, now)
            reasons.addAll(freshnessResult.reasons)
        }

        return ValidationResult(valid = reasons.isEmpty(), reasons = reasons)
    }
}
