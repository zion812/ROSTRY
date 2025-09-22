package com.rio.rostry.utils.upload

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object MediaUploader {
    suspend fun uploadImage(context: Context, storage: FirebaseStorage, uri: Uri): String = withContext(Dispatchers.IO) {
        val input = copyToCache(context, uri)
        val compressed = Compressor.compress(context, input) {
            quality(80)
            size(1_500_000) // ~1.5MB target
            format(android.graphics.Bitmap.CompressFormat.JPEG)
        }
        val path = "images/${UUID.randomUUID()}_${compressed.name}"
        val ref = storage.reference.child(path)
        ref.putFile(Uri.fromFile(compressed)).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun uploadVideo(context: Context, storage: FirebaseStorage, uri: Uri): String = withContext(Dispatchers.IO) {
        // Basic passthrough upload (compression/transcoding can be added later using MediaCodec/FFmpeg if needed)
        val input = copyToCache(context, uri)
        val path = "videos/${UUID.randomUUID()}_${input.name}"
        val ref = storage.reference.child(path)
        ref.putFile(Uri.fromFile(input)).await()
        ref.downloadUrl.await().toString()
    }

    private fun copyToCache(context: Context, uri: Uri): File {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        val name = cursor?.use {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && idx >= 0) it.getString(idx) else null
        } ?: ("upload_" + UUID.randomUUID())
        val input = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Cannot open uri: $uri")
        val outFile = File(context.cacheDir, name)
        input.use { ins -> outFile.outputStream().use { outs -> ins.copyTo(outs) } }
        return outFile
    }
}
