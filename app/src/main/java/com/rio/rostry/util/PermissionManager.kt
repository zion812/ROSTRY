package com.rio.rostry.util

import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType

/**
 * Permission manager for role-based access control
 */
object PermissionManager {

    /**
     * Check if user has permission for marketplace browsing
     */
    fun canBrowseMarketplace(user: User?): Boolean {
        return user != null
    }

    /**
     * Check if user has permission for ordering
     */
    fun canPlaceOrders(user: User?): Boolean {
        return user != null
    }

    /**
     * Check if user has permission for basic profile management
     */
    fun canManageBasicProfile(user: User?): Boolean {
        return user != null
    }

    /**
     * Check if user has permission for product listing
     */
    fun canListProducts(user: User?): Boolean {
        return user?.userType == UserType.FARMER || user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user has permission for basic tracking
     */
    fun canAccessBasicTracking(user: User?): Boolean {
        return user?.userType == UserType.FARMER || user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user has permission for order management
     */
    fun canManageOrders(user: User?): Boolean {
        return user?.userType == UserType.FARMER || user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user has permission for advanced tracking
     */
    fun canAccessAdvancedTracking(user: User?): Boolean {
        return user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user has permission for breeding records
     */
    fun canManageBreedingRecords(user: User?): Boolean {
        return user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user has permission for transfer system
     */
    fun canUseTransferSystem(user: User?): Boolean {
        return user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user has permission for coin management
     */
    fun canManageCoins(user: User?): Boolean {
        return user?.userType == UserType.ENTHUSIAST
    }

    /**
     * Check if user is verified for their role
     */
    fun isUserVerified(user: User?): Boolean {
        return when (user?.userType) {
            UserType.GENERAL -> true // General users don't need special verification
            UserType.FARMER -> user.verificationStatus.name == "VERIFIED"
            UserType.ENTHUSIAST -> user.verificationStatus.name == "VERIFIED" && user.kycStatus.name == "VERIFIED"
            else -> false
        }
    }

    /**
     * Get all permissions for a user
     */
    fun getUserPermissions(user: User?): Set<Permission> {
        if (user == null) return emptySet()

        val permissions = mutableSetOf<Permission>()

        // All users can do these
        permissions.add(Permission.BROWSE_MARKETPLACE)
        permissions.add(Permission.PLACE_ORDERS)
        permissions.add(Permission.MANAGE_BASIC_PROFILE)

        // Farmers and enthusiasts get additional permissions
        if (user.userType == UserType.FARMER || user.userType == UserType.ENTHUSIAST) {
            permissions.add(Permission.LIST_PRODUCTS)
            permissions.add(Permission.ACCESS_BASIC_TRACKING)
            permissions.add(Permission.MANAGE_ORDERS)
        }

        // Enthusiasts get additional permissions
        if (user.userType == UserType.ENTHUSIAST) {
            permissions.add(Permission.ACCESS_ADVANCED_TRACKING)
            permissions.add(Permission.MANAGE_BREEDING_RECORDS)
            permissions.add(Permission.USE_TRANSFER_SYSTEM)
            permissions.add(Permission.MANAGE_COINS)
        }

        return permissions
    }

    /**
     * Check if user has a specific permission
     */
    fun hasPermission(user: User?, permission: Permission): Boolean {
        return getUserPermissions(user).contains(permission)
    }
}

enum class Permission {
    BROWSE_MARKETPLACE,
    PLACE_ORDERS,
    MANAGE_BASIC_PROFILE,
    LIST_PRODUCTS,
    ACCESS_BASIC_TRACKING,
    MANAGE_ORDERS,
    ACCESS_ADVANCED_TRACKING,
    MANAGE_BREEDING_RECORDS,
    USE_TRANSFER_SYSTEM,
    MANAGE_COINS
}