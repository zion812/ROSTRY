package com.rio.rostry.domain.upgrade

import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.session.RolePreferenceStorage
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized manager for role upgrades.
 * 
 * This is the single source of truth for role changes, ensuring consistency
 * across all entry points by providing:
 * - Prerequisite validation
 * - Transition rule enforcement
 * - User type updates
 * - Role preference persistence
 * - Audit logging
 * - Analytics tracking
 */
@Singleton
class RoleUpgradeManager @Inject constructor(
    private val userRepository: UserRepository,
    private val rbacGuard: RbacGuard,
    private val rolePreferenceStorage: RolePreferenceStorage,
    private val auditLogDao: AuditLogDao,
    private val flowAnalyticsTracker: FlowAnalyticsTracker,
    private val currentUserProvider: CurrentUserProvider
) {
    /**
     * Performs a complete role upgrade flow.
     * 
     * @param userId The user ID to upgrade
     * @param targetRole The target role to upgrade to
     * @param skipValidation If true, skips prerequisite validation (use for wizard flows)
     * @return Resource<Unit> indicating success or error with message
     */
    suspend fun upgradeRole(
        userId: String,
        targetRole: UserType,
        skipValidation: Boolean = false
    ): Resource<Unit> {
        try {
            // Get current user to track from/to roles
            val currentUserResource = userRepository.getUserById(userId).first()
            val currentUser = when (currentUserResource) {
                is Resource.Success -> currentUserResource.data
                is Resource.Error -> return Resource.Error(currentUserResource.message ?: "Failed to get user")
                else -> null
            } ?: return Resource.Error("User not found")
            
            val currentRole = currentUser.role
            
            // Track upgrade start
            flowAnalyticsTracker.trackRoleUpgradeStarted(currentRole, targetRole)
            
            // Step 1: Validate prerequisites if required
            if (!skipValidation) {
                val validationErrors = validatePrerequisites(currentUser, targetRole)
                if (validationErrors.isNotEmpty()) {
                    flowAnalyticsTracker.trackRoleUpgradeFailed(
                        targetRole.name,
                        "Validation failed: ${validationErrors.values.firstOrNull() ?: "Unknown"}"
                    )
                    return Resource.Error(
                        validationErrors.values.joinToString(", ")
                    )
                }
            }
            
            // Step 2: Update user type in repository
            val updateResult = userRepository.updateUserType(userId, targetRole)
            if (updateResult is Resource.Error) {
                flowAnalyticsTracker.trackRoleUpgradeFailed(
                    targetRole.name,
                    updateResult.message ?: "Repository update failed"
                )
                return updateResult
            }
            
            // Step 3: Persist role preference
            rolePreferenceStorage.persist(targetRole)
            
            // Step 4: Create audit log entry
            val auditLog = AuditLogEntity(
                logId = java.util.UUID.randomUUID().toString(),
                type = "ROLE_UPGRADE",
                refId = userId,
                action = "UPGRADE",
                actorUserId = userId,
                detailsJson = com.google.gson.Gson().toJson(mapOf(
                    "fromRole" to currentRole.name,
                    "toRole" to targetRole.name,
                    "validated" to (!skipValidation).toString(),
                    "timestamp" to System.currentTimeMillis()
                )),
                createdAt = System.currentTimeMillis()
            )
            auditLogDao.insert(auditLog)
            
            // Step 5: Track successful upgrade
            flowAnalyticsTracker.trackRoleUpgradeCompleted(currentRole, targetRole)
            
            return Resource.Success(Unit)
            
        } catch (e: Exception) {
            flowAnalyticsTracker.trackRoleUpgradeFailed(
                targetRole.name,
                e.message ?: "Unknown error"
            )
            return Resource.Error(e.message ?: "Failed to upgrade role")
        }
    }
    
    /**
     * Validates prerequisites for upgrading to the target role.
     * Includes transition rule validation.
     * Returns a map of validation errors (empty if all valid).
     */
    private suspend fun validatePrerequisites(user: UserEntity, targetRole: UserType): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val currentRole = user.role
        
        // Validate transition rules (Comment 4)
        if (targetRole == currentRole) {
            errors["sameRole"] = "You are already ${currentRole.displayName}"
            return errors
        }
        
        val allowedNext = currentRole.nextLevel()
        if (allowedNext == null) {
            errors["transition"] = "You are already at the highest role level"
            return errors
        }
        
        if (targetRole != allowedNext) {
            errors["transition"] = "You can only upgrade from ${currentRole.displayName} to ${allowedNext.displayName}. Please upgrade incrementally."
            return errors
        }
        
        // Check full name (direct check instead of rbacGuard helper)
        if (user.fullName.isNullOrBlank()) {
            errors["fullName"] = "Complete your full name to upgrade to ${targetRole.displayName}"
        }
        
        // Check email (direct check)
        if (user.email.isNullOrBlank()) {
            errors["email"] = "Add your email address to upgrade to ${targetRole.displayName}"
        }
        
        // Check phone number (direct check)
        if (user.phoneNumber.isNullOrBlank()) {
            errors["phone"] = "Verify your phone number to upgrade to ${targetRole.displayName}"
        }
        
        // Check KYC verification for Enthusiast role (use rbacGuard for verification)
        if (targetRole == UserType.ENTHUSIAST && user.verificationStatus != VerificationStatus.VERIFIED) {
            errors["verification"] = "Complete KYC verification to become an Enthusiast. Go to Profile → Verification."
        }
        
        return errors
    }
}
