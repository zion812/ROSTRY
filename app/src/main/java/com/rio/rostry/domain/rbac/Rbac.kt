package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType

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
            Permission.MANAGE_ORDERS
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
            Permission.COIN_MANAGEMENT
        )
    )

    fun permissionsFor(userType: UserType): Set<Permission> = rolePermissions[userType].orEmpty()

    fun has(userType: UserType, permission: Permission): Boolean = rolePermissions[userType]?.contains(permission) == true
}
