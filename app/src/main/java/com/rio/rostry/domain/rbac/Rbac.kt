package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType

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
 * Permissions requiring KYC verification: LIST_PRODUCT, TRANSFER_SYSTEM, EDIT_LINEAGE
 */
object Rbac {
    private val rolePermissions: Map<UserType, Set<Permission>> = mapOf(
        UserType.GENERAL to setOf(
            Permission.BROWSE_MARKET,
            Permission.PLACE_ORDER,
            Permission.BASIC_PROFILE
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
        )
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
     * Permissions requiring verification: LIST_PRODUCT, TRANSFER_SYSTEM, EDIT_LINEAGE
     */
    fun requiresVerification(permission: Permission): Boolean = when (permission) {
        Permission.LIST_PRODUCT, Permission.TRANSFER_SYSTEM, Permission.EDIT_LINEAGE -> true
        else -> false
    }

    /**
     * Returns the minimum user type required for the permission, or null if no minimum (admin-only).
     */
    fun minimumUserType(permission: Permission): UserType? = when (permission) {
        Permission.BROWSE_MARKET, Permission.PLACE_ORDER, Permission.BASIC_PROFILE -> UserType.GENERAL
        Permission.LIST_PRODUCT, Permission.BASIC_TRACKING, Permission.MANAGE_ORDERS, Permission.EDIT_LINEAGE -> UserType.FARMER
        Permission.ADVANCED_TRACKING, Permission.BREEDING_RECORDS, Permission.TRANSFER_SYSTEM, Permission.COIN_MANAGEMENT -> UserType.ENTHUSIAST
        Permission.VIEW_AUDIT_LOGS, Permission.ADMIN_VERIFICATION -> null
        else -> null
    }
}