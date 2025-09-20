package com.rio.rostry.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

object DataCompressionUtil {
    
    /**
     * Compress byte array using Deflate algorithm
     */
    fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()
        
        val outputStream = ByteArrayOutputStream(data.size)
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        deflater.end()
        
        return outputStream.toByteArray()
    }
    
    /**
     * Decompress byte array using Inflate algorithm
     */
    fun decompress(data: ByteArray): ByteArray {
        val inflater = Inflater()
        inflater.setInput(data)
        
        val outputStream = ByteArrayOutputStream(data.size)
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        inflater.end()
        
        return outputStream.toByteArray()
    }
    
    /**
     * Compress bitmap to reduce size
     */
    fun compressBitmap(bitmap: Bitmap, quality: Int = 80): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }
    
    /**
     * Decompress byte array to bitmap
     */
    fun decompressBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
    
    /**
     * Calculate compression ratio
     */
    fun getCompressionRatio(originalSize: Int, compressedSize: Int): Double {
        return if (originalSize > 0) {
            (originalSize - compressedSize).toDouble() / originalSize.toDouble() * 100
        } else {
            0.0
        }
    }
}