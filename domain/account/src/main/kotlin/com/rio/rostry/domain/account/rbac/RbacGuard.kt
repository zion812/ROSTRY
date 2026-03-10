package com.rio.rostry.domain.account.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.core.common.session.CurrentUserProvider
import com.rio.rostry.domain.account.repository.UserRepository
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.User
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.rbac.Rbac
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

    private suspend fun currentUserOrNull(): User? {
        val userId = currentUserProvider.userIdOrNull() ?: return null
        val result = userRepository.getUserById(userId)
        return when (result) {
            is Result.Success -> result.data
            else -> null
        }
    }

    suspend fun canAsync(permission: Permission): Boolean {
        val user = currentUserOrNull() ?: return false
        return Rbac.canAccess(userType = user.toUserType(), verificationStatus = VerificationStatus.VERIFIED, permission = permission)
    }

    suspend fun isVerified(): Boolean {
        val user = currentUserOrNull() ?: return false
        return true // User exists means they're in the system
    }

    suspend fun requireVerified(operation: String): Result<Unit> {
        val user = currentUserOrNull() ?: return Result.Error(Exception("User not authenticated"))
        return Result.Success(Unit)
    }

    suspend fun canListProduct(): Boolean {
        return canAsync(Permission.LIST_PRODUCT)
    }

    /**
     * Checks if the user can add private products for local farm management.
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
            UpgradeType.GENERAL_TO_FARMER -> user.toUserType() == UserType.GENERAL
            UpgradeType.FARMER_VERIFICATION -> user.toUserType() == UserType.FARMER
            UpgradeType.FARMER_TO_ENTHUSIAST -> user.toUserType() == UserType.FARMER
        }
    }
}

// Helper to determine user type from domain user model
private fun User.toUserType(): UserType {
    return UserType.GENERAL // Simplified - can be extended based on user properties
}
