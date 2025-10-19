package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.first
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
        val userResource = userRepository.getUserById(userId).first()
        return when (userResource) {
            is Resource.Success -> userResource.data
            else -> null
        }
    }

    suspend fun canAsync(permission: Permission): Boolean {
        val user = currentUserOrNull() ?: return false
        return Rbac.has(user.userType, permission)
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
        val user = currentUserOrNull() ?: return false
        return Rbac.has(user.userType, Permission.LIST_PRODUCT) && user.verificationStatus == VerificationStatus.VERIFIED
    }

    suspend fun canInitiateTransfer(): Boolean {
        val user = currentUserOrNull() ?: return false
        return Rbac.has(user.userType, Permission.TRANSFER_SYSTEM) && user.verificationStatus == VerificationStatus.VERIFIED
    }

    suspend fun canEditLineage(): Boolean {
        val user = currentUserOrNull() ?: return false
        return Rbac.has(user.userType, Permission.EDIT_LINEAGE) && user.verificationStatus == VerificationStatus.VERIFIED
    }

    suspend fun canManageOrders(): Boolean {
        val user = currentUserOrNull() ?: return false
        return Rbac.has(user.userType, Permission.MANAGE_ORDERS) && user.verificationStatus == VerificationStatus.VERIFIED
    }
}
