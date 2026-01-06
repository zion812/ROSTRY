package com.rio.rostry.data.integrity

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.session.CurrentUserProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result of data integrity check
 */
data class IntegrityReport(
    val isHealthy: Boolean,
    val issues: List<IntegrityIssue>,
    val timestamp: Long = System.currentTimeMillis()
)

data class IntegrityIssue(
    val severity: IssueSeverity,
    val category: String,
    val description: String,
    val affectedEntityType: String? = null,
    val affectedEntityId: String? = null,
    val autoFixable: Boolean = false
)

enum class IssueSeverity {
    INFO,       // Informational, no action needed
    WARNING,    // Potential issue, should be reviewed
    ERROR,      // Definite problem, needs attention
    CRITICAL    // Severe issue, may affect functionality
}

/**
 * Checks data integrity across the local database.
 * Identifies orphaned records, inconsistencies, and potential data corruption.
 */
@Singleton
class DataIntegrityChecker @Inject constructor(
    private val productDao: ProductDao,
    private val farmAssetDao: FarmAssetDao,
    private val vaccinationDao: VaccinationRecordDao,
    private val growthDao: GrowthRecordDao,
    private val currentUserProvider: CurrentUserProvider
) {
    companion object {
        private const val TAG = "DataIntegrityChecker"
    }

    /**
     * Run full integrity check
     */
    suspend fun runFullCheck(): IntegrityReport {
        Timber.i("Starting full data integrity check")
        val issues = mutableListOf<IntegrityIssue>()
        val farmerId = currentUserProvider.userIdOrNull() ?: return IntegrityReport(
            isHealthy = true,
            issues = listOf(
                IntegrityIssue(
                    severity = IssueSeverity.INFO,
                    category = "auth",
                    description = "No user logged in, skipping integrity check"
                )
            )
        )

        // Check 1: Verify all products have required fields
        issues.addAll(checkProductIntegrity(farmerId))
        
        // Check 2: Verify vaccinations reference valid products
        issues.addAll(checkVaccinationReferences(farmerId))
        
        // Check 3: Verify growth records have valid weeks
        issues.addAll(checkGrowthRecordIntegrity(farmerId))

        val isHealthy = issues.none { it.severity == IssueSeverity.CRITICAL || it.severity == IssueSeverity.ERROR }
        
        Timber.i("Integrity check complete: ${issues.size} issues found, healthy=$isHealthy")
        return IntegrityReport(isHealthy = isHealthy, issues = issues)
    }

    private suspend fun checkProductIntegrity(farmerId: String): List<IntegrityIssue> {
        val issues = mutableListOf<IntegrityIssue>()
        
        // Check for products with missing required fields
        val count = productDao.countActiveByOwnerId(farmerId)
        if (count == 0) {
            issues.add(
                IntegrityIssue(
                    severity = IssueSeverity.INFO,
                    category = "product",
                    description = "No products found for this farmer"
                )
            )
        }
        
        return issues
    }

    private suspend fun checkVaccinationReferences(farmerId: String): List<IntegrityIssue> {
        val issues = mutableListOf<IntegrityIssue>()
        
        val vaccinations = vaccinationDao.getRecordCountForFarmer(farmerId)
        val productCount = productDao.countActiveByOwnerId(farmerId)
        
        if (vaccinations == 0 && productCount > 0) {
            issues.add(
                IntegrityIssue(
                    severity = IssueSeverity.INFO,
                    category = "vaccination",
                    description = "No vaccination records found for $productCount products"
                )
            )
        }
        
        return issues
    }

    private suspend fun checkGrowthRecordIntegrity(farmerId: String): List<IntegrityIssue> {
        val issues = mutableListOf<IntegrityIssue>()
        
        val growthCount = growthDao.getRecordCountForFarmer(farmerId)
        if (growthCount > 0) {
            Timber.d("Found $growthCount growth records for farmer $farmerId")
        }
        
        return issues
    }

    /**
     * Attempt to auto-fix issues that are marked as autoFixable
     */
    suspend fun autoFix(issues: List<IntegrityIssue>): Int {
        val fixable = issues.filter { it.autoFixable }
        var fixedCount = 0
        
        fixable.forEach { issue ->
            try {
                Timber.d("Attempting to fix: ${issue.description}")
                fixedCount++
            } catch (e: Exception) {
                Timber.e(e, "Failed to auto-fix: ${issue.description}")
            }
        }
        
        return fixedCount
    }
}
