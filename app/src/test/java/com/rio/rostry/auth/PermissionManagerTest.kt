package com.rio.rostry.auth

import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.util.Permission
import com.rio.rostry.util.PermissionManager
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class PermissionManagerTest {

    @Test
    fun `general user should have basic permissions`() {
        val user = User(
            id = "123",
            phone = "9876543210",
            userType = UserType.GENERAL,
            createdAt = Date(),
            updatedAt = Date()
        )

        assertTrue(PermissionManager.canBrowseMarketplace(user))
        assertTrue(PermissionManager.canPlaceOrders(user))
        assertTrue(PermissionManager.canManageBasicProfile(user))
        assertFalse(PermissionManager.canListProducts(user))
        assertFalse(PermissionManager.canAccessBasicTracking(user))
        assertFalse(PermissionManager.canManageOrders(user))
        assertFalse(PermissionManager.canAccessAdvancedTracking(user))
        assertFalse(PermissionManager.canManageBreedingRecords(user))
        assertFalse(PermissionManager.canUseTransferSystem(user))
        assertFalse(PermissionManager.canManageCoins(user))
    }

    @Test
    fun `farmer user should have farmer permissions`() {
        val user = User(
            id = "123",
            phone = "9876543210",
            userType = UserType.FARMER,
            createdAt = Date(),
            updatedAt = Date()
        )

        assertTrue(PermissionManager.canBrowseMarketplace(user))
        assertTrue(PermissionManager.canPlaceOrders(user))
        assertTrue(PermissionManager.canManageBasicProfile(user))
        assertTrue(PermissionManager.canListProducts(user))
        assertTrue(PermissionManager.canAccessBasicTracking(user))
        assertTrue(PermissionManager.canManageOrders(user))
        assertFalse(PermissionManager.canAccessAdvancedTracking(user))
        assertFalse(PermissionManager.canManageBreedingRecords(user))
        assertFalse(PermissionManager.canUseTransferSystem(user))
        assertFalse(PermissionManager.canManageCoins(user))
    }

    @Test
    fun `enthusiast user should have all permissions`() {
        val user = User(
            id = "123",
            phone = "9876543210",
            userType = UserType.ENTHUSIAST,
            createdAt = Date(),
            updatedAt = Date()
        )

        assertTrue(PermissionManager.canBrowseMarketplace(user))
        assertTrue(PermissionManager.canPlaceOrders(user))
        assertTrue(PermissionManager.canManageBasicProfile(user))
        assertTrue(PermissionManager.canListProducts(user))
        assertTrue(PermissionManager.canAccessBasicTracking(user))
        assertTrue(PermissionManager.canManageOrders(user))
        assertTrue(PermissionManager.canAccessAdvancedTracking(user))
        assertTrue(PermissionManager.canManageBreedingRecords(user))
        assertTrue(PermissionManager.canUseTransferSystem(user))
        assertTrue(PermissionManager.canManageCoins(user))
    }

    @Test
    fun `null user should have no permissions`() {
        val user: User? = null

        assertFalse(PermissionManager.canBrowseMarketplace(user))
        assertFalse(PermissionManager.canPlaceOrders(user))
        assertFalse(PermissionManager.canManageBasicProfile(user))
        assertFalse(PermissionManager.canListProducts(user))
        assertFalse(PermissionManager.canAccessBasicTracking(user))
        assertFalse(PermissionManager.canManageOrders(user))
        assertFalse(PermissionManager.canAccessAdvancedTracking(user))
        assertFalse(PermissionManager.canManageBreedingRecords(user))
        assertFalse(PermissionManager.canUseTransferSystem(user))
        assertFalse(PermissionManager.canManageCoins(user))
    }

    @Test
    fun `getUserPermissions should return correct permissions for each user type`() {
        val generalUser = User(
            id = "123",
            phone = "9876543210",
            userType = UserType.GENERAL,
            createdAt = Date(),
            updatedAt = Date()
        )

        val farmerUser = User(
            id = "124",
            phone = "9876543211",
            userType = UserType.FARMER,
            createdAt = Date(),
            updatedAt = Date()
        )

        val enthusiastUser = User(
            id = "125",
            phone = "9876543212",
            userType = UserType.ENTHUSIAST,
            createdAt = Date(),
            updatedAt = Date()
        )

        val generalPermissions = PermissionManager.getUserPermissions(generalUser)
        assertEquals(3, generalPermissions.size)
        assertTrue(generalPermissions.contains(Permission.BROWSE_MARKETPLACE))
        assertTrue(generalPermissions.contains(Permission.PLACE_ORDERS))
        assertTrue(generalPermissions.contains(Permission.MANAGE_BASIC_PROFILE))

        val farmerPermissions = PermissionManager.getUserPermissions(farmerUser)
        assertEquals(6, farmerPermissions.size)
        assertTrue(farmerPermissions.contains(Permission.BROWSE_MARKETPLACE))
        assertTrue(farmerPermissions.contains(Permission.PLACE_ORDERS))
        assertTrue(farmerPermissions.contains(Permission.MANAGE_BASIC_PROFILE))
        assertTrue(farmerPermissions.contains(Permission.LIST_PRODUCTS))
        assertTrue(farmerPermissions.contains(Permission.ACCESS_BASIC_TRACKING))
        assertTrue(farmerPermissions.contains(Permission.MANAGE_ORDERS))

        val enthusiastPermissions = PermissionManager.getUserPermissions(enthusiastUser)
        assertEquals(10, enthusiastPermissions.size)
        assertTrue(enthusiastPermissions.contains(Permission.BROWSE_MARKETPLACE))
        assertTrue(enthusiastPermissions.contains(Permission.PLACE_ORDERS))
        assertTrue(enthusiastPermissions.contains(Permission.MANAGE_BASIC_PROFILE))
        assertTrue(enthusiastPermissions.contains(Permission.LIST_PRODUCTS))
        assertTrue(enthusiastPermissions.contains(Permission.ACCESS_BASIC_TRACKING))
        assertTrue(enthusiastPermissions.contains(Permission.MANAGE_ORDERS))
        assertTrue(enthusiastPermissions.contains(Permission.ACCESS_ADVANCED_TRACKING))
        assertTrue(enthusiastPermissions.contains(Permission.MANAGE_BREEDING_RECORDS))
        assertTrue(enthusiastPermissions.contains(Permission.USE_TRANSFER_SYSTEM))
        assertTrue(enthusiastPermissions.contains(Permission.MANAGE_COINS))
    }
}