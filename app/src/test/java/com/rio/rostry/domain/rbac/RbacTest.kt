package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RbacTest {

    @Test
    fun generalPermissions() {
        val role = UserType.GENERAL
        assertTrue(Rbac.has(role, Permission.BROWSE_MARKET))
        assertTrue(Rbac.has(role, Permission.PLACE_ORDER))
        assertFalse(Rbac.has(role, Permission.LIST_PRODUCT))
        assertFalse(Rbac.has(role, Permission.BREEDING_RECORDS))
    }

    @Test
    fun farmerPermissions() {
        val role = UserType.FARMER
        assertTrue(Rbac.has(role, Permission.LIST_PRODUCT))
        assertTrue(Rbac.has(role, Permission.MANAGE_ORDERS))
        assertFalse(Rbac.has(role, Permission.COIN_MANAGEMENT))
    }

    @Test
    fun enthusiastPermissions() {
        val role = UserType.ENTHUSIAST
        for (p in Permission.values()) {
            // Enthusiast should include all listed permissions
            assertTrue(Rbac.has(role, p))
        }
    }
}
