package com.rio.rostry.utils.storage

import com.rio.rostry.domain.model.UpgradeType
import java.util.Locale

/**
 * Utility class to generate organized storage paths for verification documents.
 */
object VerificationStoragePathBuilder {

    /**
     * Builds a path for a document file.
     * Format: verifications/{upgradeType}/{userId}/documents/{timestamp}_{documentType}.{ext}
     */
    fun buildDocumentPath(
        upgradeType: UpgradeType,
        userId: String,
        documentType: String,
        fileExtension: String
    ): String {
        val timestamp = System.currentTimeMillis()
        val safeDocType = documentType.uppercase(Locale.ROOT).replace(" ", "_")
        val ext = if (fileExtension.startsWith(".")) fileExtension.substring(1) else fileExtension
        return "verifications/${upgradeType.name}/$userId/documents/${timestamp}_${safeDocType}.$ext"
    }

    /**
     * Builds a path for an image file.
     * Format: verifications/{upgradeType}/{userId}/images/{timestamp}_{imageType}.{ext}
     */
    fun buildImagePath(
        upgradeType: UpgradeType,
        userId: String,
        imageType: String,
        fileExtension: String
    ): String {
        val timestamp = System.currentTimeMillis()
        val safeImageType = imageType.uppercase(Locale.ROOT).replace(" ", "_")
        val ext = if (fileExtension.startsWith(".")) fileExtension.substring(1) else fileExtension
        return "verifications/${upgradeType.name}/$userId/images/${timestamp}_${safeImageType}.$ext"
    }

    /**
     * Returns the base path for a specific verification submission.
     * Format: verifications/{upgradeType}/{userId}/
     */
    fun buildBasePath(upgradeType: UpgradeType, userId: String): String {
        return "verifications/${upgradeType.name}/$userId/"
    }

    /**
     * Extracts upgrade type from a storage path.
     */
    fun parseUpgradeTypeFromPath(path: String): UpgradeType? {
        val parts = path.split("/")
        if (parts.size > 1 && parts[0] == "verifications") {
            return try {
                UpgradeType.valueOf(parts[1])
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        return null
    }
}
