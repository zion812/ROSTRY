package com.rio.rostry.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class QrUtilsTest {
    @Test
    fun generateQr_returnsBitmapWithRequestedSize() {
        val size = 128
        val bmp = QrUtils.generateQr("test-content", size)
        assertNotNull(bmp)
        assertEquals(size, bmp.width)
        assertEquals(size, bmp.height)
    }
}
