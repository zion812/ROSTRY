package com.rio.rostry.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImagePickerUtil private constructor() {
    companion object {
        fun getInstance(): ImagePickerUtil = ImagePickerUtil()
    }

    /**
     * Uploads an image to a remote storage service.
     * In a real implementation, this would connect to Firebase Storage or another service.
     * For now, it returns a mock URL.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> {
        return try {
            // In a real implementation, you would:
            // 1. Get the image from the URI
            // 2. Upload it to a storage service like Firebase Storage
            // 3. Return the download URL
            
            // Mock implementation for now
            Result.success("https://example.com/mock-image-${UUID.randomUUID()}.jpg")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Composable
fun rememberImagePickerLauncher(
    onImagePicked: (Uri) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    val context = LocalContext.current
    
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { 
                // Copy the image to a temporary file to ensure we have read permissions
                val tempFile = copyImageToTempFile(context, it)
                tempFile?.let { tempUri ->
                    onImagePicked(tempUri)
                }
            }
        }
    )
}

private fun copyImageToTempFile(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        Uri.fromFile(tempFile)
    } catch (e: Exception) {
        null
    }
}