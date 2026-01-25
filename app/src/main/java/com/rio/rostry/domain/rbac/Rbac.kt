package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus

/**
 * Role-Based Access Control (RBAC) system for Rostry.
 *
 * Defines permissions for different user types and provides utility methods
 * for checking access and verification requirements.
 *
 * Permission hierarchy:
 * - GENERAL: Basic marketplace access and profile management
 * - FARMER: GENERAL + product listing, basic tracking, order management, lineage editing for own products
 * - ENTHUSIAST: FARMER + advanced tracking, breeding records, transfer system, coin management
 * - ADMIN (future): All permissions + verification management, audit log viewing
 *
 * Permissions requiring KYC verification: LIST_PRODUCT only applies to PUBLIC market listings, not private/local products.
 * Products with status="private" are for local farm management and don't require verification.
 * This means PENDING farmers can use all farm features (bird tracking, breeding, monitoring, etc.) but cannot publish products to the public marketplace.
 */
object Rbac {
    private val rolePermissions: Map<UserType, Set<Permission>> = mapOf(
        UserType.GENERAL to setOf(
            Permission.BROWSE_MARKET,
            Permission.PLACE_ORDER,
            Permission.BASIC_PROFILE,
            Permission.BASIC_TRACKING
        ),
        UserType.FARMER to setOf(
            Permission.BROWSE_MARKET,
            Permission.PLACE_ORDER,
            Permission.BASIC_PROFILE,
            Permission.LIST_PRODUCT,
            Permission.BASIC_TRACKING,
            Permission.MANAGE_ORDERS,
            Permission.EDIT_LINEAGE
        ),
        UserType.ENTHUSIAST to setOf(
            Permission.BROWSE_MARKET,
            Permission.PLACE_ORDER,
            Permission.BASIC_PROFILE,
            Permission.LIST_PRODUCT,
            Permission.BASIC_TRACKING,
            Permission.MANAGE_ORDERS,
            Permission.ADVANCED_TRACKING,
            Permission.BREEDING_RECORDS,
            Permission.TRANSFER_SYSTEM,
            Permission.COIN_MANAGEMENT,
            Permission.EDIT_LINEAGE
        ),
        UserType.ADMIN to Permission.values().toSet()
    )

    /**
     * Returns the set of permissions for the given user type.
     */
    fun permissionsFor(userType: UserType): Set<Permission> = rolePermissions[userType].orEmpty()

    /**
     * Checks if the given user type has the specified permission.
     */
    fun has(userType: UserType, permission: Permission): Boolean = rolePermissions[userType]?.contains(permission) == true

    /**
     * Returns true if the permission requires KYC verification to be performed.
     * Permissions requiring verification: LIST_PRODUCT only (other features available during PENDING status)
     */
    fun requiresVerification(permission: Permission): Boolean = when (permission) {
        Permission.LIST_PRODUCT -> true
        else -> false
    }

    /**
     * Checks if a permission requires VERIFIED status based on the current status.
     * Returns true if the user is NOT verified and the permission requires verification.
     */
    fun requiresVerifiedStatus(permission: Permission, verificationStatus: VerificationStatus): Boolean {
        return requiresVerification(permission) && verificationStatus != VerificationStatus.VERIFIED
    }

    /**
     * Comprehensive access check combining role and verification status.
     * Returns true if the user has the role permission AND meets verification requirements.
     */
    fun canAccess(userType: UserType, verificationStatus: VerificationStatus, permission: Permission): Boolean {
        if (!has(userType, permission)) return false
        if (requiresVerifiedStatus(permission, verificationStatus)) return false
        return true
    }

    /**
     * Returns the minimum user type required for the permission, or null if no minimum (admin-only).
     */
    fun minimumUserType(permission: Permission): UserType? = when (permission) {
        Permission.BROWSE_MARKET, Permission.PLACE_ORDER, Permission.BASIC_PROFILE, Permission.BASIC_TRACKING -> UserType.GENERAL
        Permission.LIST_PRODUCT, Permission.MANAGE_ORDERS, Permission.EDIT_LINEAGE -> UserType.FARMER
        Permission.ADVANCED_TRACKING, Permission.BREEDING_RECORDS, Permission.TRANSFER_SYSTEM, Permission.COIN_MANAGEMENT -> UserType.ENTHUSIAST
        Permission.VIEW_AUDIT_LOGS, Permission.ADMIN_VERIFICATION -> UserType.ADMIN
        else -> null
    }

    /**
     * Checks if the current user can manage a resource owned by another user.
     * Admins can manage everything ("Superuser" power). Owners can manage their own resources.
     */
    fun canManageResource(currentUserType: UserType?, currentUserId: String?, resourceOwnerId: String?): Boolean {
        // 1. Admin Override (Superuser)
        if (currentUserType == UserType.ADMIN) return true
        
        // 2. Ownership Check
        if (currentUserId != null && resourceOwnerId != null && currentUserId == resourceOwnerId) return true
        
        return false
    }
}