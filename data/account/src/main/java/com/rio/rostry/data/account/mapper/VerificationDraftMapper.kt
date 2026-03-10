package com.rio.rostry.data.account.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.core.model.FarmLocation
import com.rio.rostry.core.model.VerificationDraft
import com.rio.rostry.data.database.entity.VerificationDraftEntity
import com.rio.rostry.domain.model.UpgradeType

/**
 * Converts VerificationDraftEntity to domain model.
 */
fun VerificationDraftEntity.toVerificationDraft(gson: Gson): VerificationDraft {
    return VerificationDraft(
        draftId = this.draftId,
        userId = this.userId,
        upgradeType = this.upgradeType?.name,
        farmLocation = this.farmLocationJson?.let { gson.fromJson(it, FarmLocation::class.java) },
        uploadedImages = deserializeList(this.uploadedImagesJson, gson),
        uploadedDocuments = deserializeList(this.uploadedDocsJson, gson),
        uploadedImageTypes = deserializeMap(this.uploadedImageTypesJson, gson),
        uploadedDocTypes = deserializeMap(this.uploadedDocTypesJson, gson),
        uploadProgress = deserializeMapInt(this.uploadProgressJson, gson),
        lastSavedAt = this.lastSavedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Converts domain model to VerificationDraftEntity.
 */
fun VerificationDraft.toEntity(gson: Gson): VerificationDraftEntity {
    return VerificationDraftEntity(
        draftId = this.draftId,
        userId = this.userId,
        upgradeType = this.upgradeType?.let { value ->
            runCatching { UpgradeType.valueOf(value) }.getOrNull()
        },
        farmLocationJson = this.farmLocation?.let { gson.toJson(it) },
        uploadedImagesJson = gson.toJson(this.uploadedImages),
        uploadedDocsJson = gson.toJson(this.uploadedDocuments),
        uploadedImageTypesJson = gson.toJson(this.uploadedImageTypes),
        uploadedDocTypesJson = gson.toJson(this.uploadedDocTypes),
        uploadProgressJson = gson.toJson(this.uploadProgress),
        lastSavedAt = this.lastSavedAt,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

private fun deserializeList(json: String?, gson: Gson): List<String> {
    if (json.isNullOrBlank()) return emptyList()
    return try {
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }
}

private fun deserializeMap(json: String?, gson: Gson): Map<String, String> {
    if (json.isNullOrBlank()) return emptyMap()
    return try {
        gson.fromJson(json, object : TypeToken<Map<String, String>>() {}.type)
    } catch (e: Exception) {
        emptyMap()
    }
}

private fun deserializeMapInt(json: String?, gson: Gson): Map<String, Int> {
    if (json.isNullOrBlank()) return emptyMap()
    return try {
        gson.fromJson(json, object : TypeToken<Map<String, Int>>() {}.type)
    } catch (e: Exception) {
        emptyMap()
    }
}
