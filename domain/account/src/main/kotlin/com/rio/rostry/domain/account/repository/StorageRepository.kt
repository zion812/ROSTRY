package com.rio.rostry.domain.account.repository

import com.rio.rostry.core.model.Result

/**
 * Repository contract for Firebase Storage operations with quota enforcement.
 */
interface StorageRepository {
    
    data class UploadResult(
        val downloadUrl: String,
        val bytesUploaded: Long,
    )
    
    /**
     * Uploads a file to Firebase Storage after checking quotas.
     * 
     * @param localUriString Local file URI
     * @param remotePath Remote storage path
     * @param compress Whether to compress images before upload
     * @param sizeLimitBytes Maximum file size in bytes
     * @param onProgress Progress callback (0-100)
     * @return Result with upload details or error
     */
    suspend fun uploadFile(
        localUriString: String,
        remotePath: String,
        compress: Boolean = true,
        sizeLimitBytes: Long = 20_000_000L,
        onProgress: (percent: Int) -> Unit = {},
    ): Result<UploadResult>
    
    /**
     * Deletes a file from Firebase Storage and updates quota.
     * 
     * @param remotePath Remote storage path
     * @return Result indicating success or error
     */
    suspend fun deleteFile(remotePath: String): Result<Unit>
}

