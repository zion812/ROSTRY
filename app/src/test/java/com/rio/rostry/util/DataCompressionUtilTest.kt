package com.rio.rostry.util

import org.junit.Assert.*
import org.junit.Test

class DataCompressionUtilTest {
    
    @Test
    fun `compress and decompress should return original data`() {
        val originalData = "This is a test string for compression".toByteArray()
        
        val compressedData = DataCompressionUtil.compress(originalData)
        val decompressedData = DataCompressionUtil.decompress(compressedData)
        
        assertNotEquals("Compressed data should be different size", originalData.size, compressedData.size)
        assertArrayEquals("Decompressed data should match original", originalData, decompressedData)
    }
    
    @Test
    fun `getCompressionRatio should calculate correct ratio`() {
        val originalSize = 1000
        val compressedSize = 500
        
        val ratio = DataCompressionUtil.getCompressionRatio(originalSize, compressedSize)
        assertEquals("Compression ratio should be 50%", 50.0, ratio, 0.01)
    }
    
    @Test
    fun `getCompressionRatio should handle zero original size`() {
        val originalSize = 0
        val compressedSize = 0
        
        val ratio = DataCompressionUtil.getCompressionRatio(originalSize, compressedSize)
        assertEquals("Compression ratio should be 0% for zero size", 0.0, ratio, 0.01)
    }
}