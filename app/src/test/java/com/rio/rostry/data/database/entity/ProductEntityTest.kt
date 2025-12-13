package com.rio.rostry.data.database.entity

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductEntityTest {

    @Test
    fun `isPublic returns false for private status`() {
        val product = ProductEntity(status = "private")
        assertFalse(product.isPublic)
    }

    @Test
    fun `isPublic returns true for active status`() {
        val product = ProductEntity(status = "active")
        assertTrue(product.isPublic)
    }

    @Test
    fun `isPublic returns false for empty status`() {
        val product = ProductEntity(status = "")
        assertFalse(product.isPublic)
    }

    @Test
    fun `isPublic returns false for blank status`() {
        val product = ProductEntity(status = "")
        assertFalse(product.isPublic)
    }

    @Test
    fun `isPublic returns false for whitespace status`() {
        val product = ProductEntity(status = "   ")
        assertFalse(product.isPublic)
    }

    @Test
    fun `isPublic returns true for other statuses`() {
        val product = ProductEntity(status = "available")
        assertTrue(product.isPublic)
    }
}