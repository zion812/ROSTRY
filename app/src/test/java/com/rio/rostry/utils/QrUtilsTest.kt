package com.rio.rostry.utils

import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
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

    @Test
    fun productLineageQrBitmap_generates_correct_deeplink() {
        val bmp = QrUtils.productLineageQrBitmap("test-product-123")
        val width = bmp.width
        val height = bmp.height
        val pixels = IntArray(width * height)
        bmp.getPixels(pixels, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        val result = reader.decode(binaryBitmap)
        assertEquals("rostry://traceability/test-product-123", result.text)
    }
}
