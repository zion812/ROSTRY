package com.rio.rostry.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class BirdIdGeneratorTest {

    @Test
    fun generate_creates_deterministic_code() {
        val color = "Black"
        val breed = "Rhode Island Red"
        val sellerId = "seller-123"
        val productId = "product-abc123"

        val code1 = BirdIdGenerator.generate(color, breed, sellerId, productId)
        val code2 = BirdIdGenerator.generate(color, breed, sellerId, productId)

        assertEquals("BLK-RIR-123", code1)
        assertEquals(code1, code2)
    }

    @Test
    fun generate_handles_null_color_and_breed() {
        val sellerId = "seller-123"
        val productId = "product-abc456"

        val code = BirdIdGenerator.generate(null, null, sellerId, productId)

        assertEquals("UNK-XX-456", code)
    }

    @Test
    fun generate_uses_last_three_chars_of_productId() {
        val color = "White"
        val breed = "Leghorn"
        val sellerId = "seller-123"
        val productId = "product-xyz789"

        val code = BirdIdGenerator.generate(color, breed, sellerId, productId)

        assertEquals("WHI-L-789", code)
    }

    @Test
    fun colorTag_maps_common_colors() {
        assertEquals("BLACK", BirdIdGenerator.colorTag("black"))
        assertEquals("BLACK", BirdIdGenerator.colorTag("dark"))
        assertEquals("WHITE", BirdIdGenerator.colorTag("white"))
        assertEquals("WHITE", BirdIdGenerator.colorTag("light"))
        assertEquals("BROWN", BirdIdGenerator.colorTag("brown"))
        assertEquals("BROWN", BirdIdGenerator.colorTag("red"))
        assertEquals("YELLOW", BirdIdGenerator.colorTag("yellow"))
        assertEquals("YELLOW", BirdIdGenerator.colorTag("gold"))
    }

    @Test
    fun colorTag_handles_null_and_unknown() {
        assertEquals("MIXED", BirdIdGenerator.colorTag(null))
        assertEquals("MIXED", BirdIdGenerator.colorTag("unknown"))
        assertEquals("MIXED", BirdIdGenerator.colorTag("purple"))
    }
}