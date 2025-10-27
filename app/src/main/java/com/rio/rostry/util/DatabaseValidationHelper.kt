package com.rio.rostry.util

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.UserEntity
import kotlinx.coroutines.flow.firstOrNull
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

data class ValidationReport(
    val isValid: Boolean,
    val invalidCount: Int,
    val invalidIds: List<String>
)

enum class RepairStrategy {
    DELETE,
    ASSIGN_PLACEHOLDER,
    LOG_ONLY
}

@Singleton
class DatabaseValidationHelper @Inject constructor(
    private val productDao: ProductDao,
    private val userDao: UserDao
) {

    suspend fun validateProductForeignKeys(): ValidationReport {
        val orphaned = productDao.getProductsWithMissingSellers()
        val ids = orphaned.map { it.productId }
        Timber.d("Validated product foreign keys: ${orphaned.size} orphaned products")
        return ValidationReport(
            isValid = orphaned.isEmpty(),
            invalidCount = orphaned.size,
            invalidIds = ids
        )
    }

    suspend fun validateAllForeignKeys(): ValidationReport {
        // Currently only validates products; extend for orders, cart items, etc. as needed
        return validateProductForeignKeys()
    }

    suspend fun repairOrphanedProducts(strategy: RepairStrategy) {
        val report = validateProductForeignKeys()
        if (report.isValid) {
            Timber.d("No orphaned products to repair")
            return
        }

        when (strategy) {
            RepairStrategy.DELETE -> {
                report.invalidIds.forEach { productDao.deleteProduct(it) }
                Timber.i("Deleted ${report.invalidCount} orphaned products")
            }
            RepairStrategy.ASSIGN_PLACEHOLDER -> {
                // Create or ensure placeholder user exists
                val placeholderId = "placeholder-user"
                val placeholder = userDao.getUserById(placeholderId).firstOrNull()
                if (placeholder == null) {
                    val user = UserEntity(
                        userId = placeholderId,
                        fullName = "Placeholder User",
                        email = "placeholder@example.com",
                        userType = UserType.GENERAL,
                        verificationStatus = VerificationStatus.UNVERIFIED,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    userDao.insertUser(user)
                    Timber.d("Created placeholder user for orphaned products")
                }
                // Update sellerId for orphaned products and touch updatedAt/dirty
                val now = System.currentTimeMillis()
                report.invalidIds.forEach { productId ->
                    try {
                        productDao.updateSellerIdAndTouch(productId, placeholderId, now)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to reassign seller for orphaned product: $productId")
                    }
                }
                Timber.i("Attempted to assign ${report.invalidCount} orphaned products to placeholder user")
            }
            RepairStrategy.LOG_ONLY -> {
                Timber.w("Orphaned products found: ${report.invalidIds.joinToString()}")
            }
        }
    }
}