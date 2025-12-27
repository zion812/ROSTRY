package com.rio.rostry.ui.enthusiast.certificate

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.rio.rostry.data.database.entity.ProductEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Certificate Generator - Exports certificate as image and shares.
 */
@Singleton
class CertificateGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Save certificate bitmap to gallery.
     */
    fun saveCertificateToGallery(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore for Android 10+
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ROSTRY")
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let { imageUri ->
                    context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                }

                uri
            } else {
                // Fallback for older versions
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val rostryDir = File(picturesDir, "ROSTRY")
                if (!rostryDir.exists()) rostryDir.mkdirs()

                val file = File(rostryDir, "$fileName.png")
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }

                // Trigger media scan
                val uri = Uri.fromFile(file)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                uri
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Save certificate to cache and get shareable URI.
     */
    fun getCertificateShareUri(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val cacheDir = File(context.cacheDir, "certificates")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val file = File(cacheDir, "$fileName.png")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Share certificate image via Intent.
     */
    fun shareCertificate(bitmap: Bitmap, birdName: String) {
        val fileName = "certificate_${birdName.replace(" ", "_")}_${System.currentTimeMillis()}"
        val shareUri = getCertificateShareUri(bitmap, fileName) ?: return

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, shareUri)
            putExtra(Intent.EXTRA_SUBJECT, "Certificate of Ownership - $birdName")
            putExtra(Intent.EXTRA_TEXT, "Here is the digital certificate for $birdName from ROSTRY.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Certificate").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
