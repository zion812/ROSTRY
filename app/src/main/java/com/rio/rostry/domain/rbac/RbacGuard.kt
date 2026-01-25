package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class RbacGuard @Inject constructor(
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: UserRepository
) {
    companion object {
        fun can(userType: UserType, permission: Permission): Boolean = Rbac.has(userType, permission)
    }

    private suspend fun currentUserOrNull(): com.rio.rostry.data.database.entity.UserEntity? {
        val userId = currentUserProvider.userIdOrNull() ?: return null
        val userResource = userRepository.getUserById(userId)
            .filter { it !is Resource.Loading }
            .firstOrNull()
            
        return when (userResource) {
            is Resource.Success -> userResource.data
            else -> null
        }
    }

    suspend fun canAsync(permission: Permission): Boolean {
        val user = currentUserOrNull() ?: return false
        if (user.isSuspended) return false // Deny all access if suspended
        return Rbac.canAccess(user.role, user.verificationStatus, permission)
    }

    suspend fun isVerified(): Boolean {
        val user = currentUserOrNull() ?: return false
        return user.verificationStatus == VerificationStatus.VERIFIED
    }

    suspend fun requireVerified(operation: String): Resource<Unit> {
        val user = currentUserOrNull() ?: return Resource.Error("User not authenticated")
        return if (user.verificationStatus == VerificationStatus.VERIFIED) {
            Resource.Success(Unit)
        } else {
            Resource.Error("Complete KYC verification to $operation. Go to Profile → Verification.")
        }
    }

    suspend fun canListProduct(): Boolean {
        return canAsync(Permission.LIST_PRODUCT)
    }

    /**
     * Checks if the user can add private products for local farm management.
     * Uses BASIC_TRACKING permission since this is a farm tracking operation,
     * not a marketplace listing operation. No verification status check required.
     */
    suspend fun canAddPrivateProduct(): Boolean {
        return canAsync(Permission.BASIC_TRACKING)
    }

    suspend fun canInitiateTransfer(): Boolean {
        return canAsync(Permission.TRANSFER_SYSTEM)
    }

    suspend fun canEditLineage(): Boolean {
        return canAsync(Permission.EDIT_LINEAGE)
    }

    suspend fun canManageOrders(): Boolean {
        return canAsync(Permission.MANAGE_ORDERS)
    }

    suspend fun canRequestUpgrade(upgradeType: UpgradeType): Boolean {
        val user = currentUserOrNull() ?: return false
        return when (upgradeType) {
            UpgradeType.GENERAL_TO_FARMER -> user.role == UserType.GENERAL
            UpgradeType.FARMER_VERIFICATION -> user.role == UserType.FARMER && user.verificationStatus != VerificationStatus.VERIFIED
            UpgradeType.FARMER_TO_ENTHUSIAST -> user.role == UserType.FARMER && user.verificationStatus == VerificationStatus.VERIFIED
        }
    }
}
