package com.rio.rostry.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.rio.rostry.utils.NetworkMonitor
import com.rio.rostry.utils.PerformanceLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val performanceLogger: PerformanceLogger
) : StorageRepository {

    override suspend fun uploadImage(uri: Uri): String? {
        if (!networkMonitor.isConnected().first()) return null
        var success = false
        var url: String? = null
        val compressedImage = compressImage(uri)
        val duration = measureTimeMillis {
            try {
                val storageRef = storage.reference
                val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
                imageRef.putBytes(compressedImage).await()
                url = imageRef.downloadUrl.await().toString()
                success = true
            } catch (e: Exception) {
                // Error handled by returning null, success remains false
            }
        }
        performanceLogger.logImageUpload(compressedImage.size.toLong(), duration, success)
        return url
    }

    override suspend fun uploadBitmap(bitmap: Bitmap): String? {
        if (!networkMonitor.isConnected().first()) return null
        var success = false
        var url: String? = null
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()
        val duration = measureTimeMillis {
            try {
                val storageRef = storage.reference
                val imageRef = storageRef.child("qrcodes/${UUID.randomUUID()}.jpg")
                imageRef.putBytes(data).await()
                url = imageRef.downloadUrl.await().toString()
                success = true
            } catch (e: Exception) {
                // Error handled by returning null, success remains false
            }
        }
        performanceLogger.logImageUpload(data.size.toLong(), duration, success)
        return url
    }

    override suspend fun uploadProfileImage(uri: Uri, userId: String): String? {
        if (!networkMonitor.isConnected().first()) return null
        var success = false
        var url: String? = null
        val compressedImage = compressImage(uri)
        val duration = measureTimeMillis {
            try {
                val storageRef = storage.reference
                val imageRef = storageRef.child("profile_images/${userId}.jpg")
                imageRef.putBytes(compressedImage).await()
                url = imageRef.downloadUrl.await().toString()
                success = true
            } catch (e: Exception) {
                // Error handled by returning null, success remains false
            }
        }
        performanceLogger.logImageUpload(compressedImage.size.toLong(), duration, success)
        return url
    }

    private suspend fun compressImage(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.toByteArray()
    }
}
