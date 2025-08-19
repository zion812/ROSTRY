package com.rio.rostry.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class ImagePickerUtil private constructor() {
    companion object {
        fun getInstance(): ImagePickerUtil = ImagePickerUtil()
    }

    /**
     * Uploads an image to Firebase Storage.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> {
        return try {
            // Get Firebase Storage reference
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            
            // Create a reference to the image file
            val imageRef = storageRef.child("fowl_photos/${UUID.randomUUID()}.jpg")
            
            // Get the file path from the URI
            val imagePath = getPathFromUri(context, imageUri)
            if (imagePath == null) {
                return Result.failure(Exception("Failed to get image path"))
            }
            
            // Upload the file
            val uploadTask = imageRef.putFile(imageUri)
            val downloadUri = uploadTask.await().storage.downloadUrl.await()
            
            Result.success(downloadUri.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Gets the file path from a URI.
     */
    private fun getPathFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            tempFile.absolutePath
        } catch (e: Exception) {
            null
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