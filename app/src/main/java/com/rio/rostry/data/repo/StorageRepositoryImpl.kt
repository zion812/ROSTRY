package com.rio.rostry.data.repo

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadImage(uri: Uri): String? {
        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${UUID.randomUUID()}")
            imageRef.putFile(uri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun uploadBitmap(bitmap: Bitmap): String? {
        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("qrcodes/${UUID.randomUUID()}.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            imageRef.putBytes(data).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}
