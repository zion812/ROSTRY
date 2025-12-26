package com.rio.rostry.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.dao.VerificationDraftDao
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.entity.VerificationDraftEntity
import com.rio.rostry.domain.model.FarmLocation
import com.rio.rostry.ui.verification.state.VerificationFormState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class VerificationDraftRepositoryImpl @Inject constructor(
    private val draftDao: VerificationDraftDao,
    private val uploadTaskDao: UploadTaskDao,
    private val gson: Gson
) : VerificationDraftRepository {

    override suspend fun saveDraft(userId: String, formState: VerificationFormState) {
        val now = System.currentTimeMillis()
        val existingDraft = draftDao.getDraft(userId)
        val draft = VerificationDraftEntity(
            draftId = existingDraft?.draftId ?: UUID.randomUUID().toString(),
            userId = userId,
            upgradeType = formState.upgradeType,
            farmLocationJson = formState.farmLocation?.let { gson.toJson(it) },
            uploadedImagesJson = gson.toJson(formState.uploadedImages),
            uploadedDocsJson = gson.toJson(formState.uploadedDocuments),
            uploadedImageTypesJson = gson.toJson(formState.uploadedImageTypes),
            uploadedDocTypesJson = gson.toJson(formState.uploadedDocTypes),
            uploadProgressJson = gson.toJson(formState.uploadProgress),
            lastSavedAt = now,
            createdAt = existingDraft?.createdAt ?: now,
            updatedAt = now
        )
        draftDao.upsertDraft(draft)
    }

    override suspend fun loadDraft(userId: String): VerificationFormState? {
        val draft = draftDao.getDraft(userId) ?: return null
        var formState = mapToFormState(draft)
        
        // Merge background completions (tasks finished while app was closed)
        val successfulTasks = uploadTaskDao.getSuccessfulByUser(userId)
        if (successfulTasks.isNotEmpty()) {
            val docs = formState.uploadedDocuments.toMutableList()
            val images = formState.uploadedImages.toMutableList()
            val docTypes = formState.uploadedDocTypes.toMutableMap()
            val imageTypes = formState.uploadedImageTypes.toMutableMap()
            val progressMap = formState.uploadProgress.toMutableMap()
            
            var changed = false
            successfulTasks.forEach { task ->
                val downloadUrl = try { JSONObject(task.contextJson ?: "{}").optString("downloadUrl") } catch (e: Exception) { null }
                if (downloadUrl.isNullOrEmpty()) return@forEach
                
                val type = extractTypeFromPath(task.remotePath)
                if (task.remotePath.contains("/documents/")) {
                    if (!docs.contains(downloadUrl)) {
                        docs.add(downloadUrl)
                        docTypes[downloadUrl] = type
                        changed = true
                    }
                } else {
                    if (!images.contains(downloadUrl)) {
                        images.add(downloadUrl)
                        imageTypes[downloadUrl] = type
                        changed = true
                    }
                }
                if (progressMap.containsKey(task.remotePath)) {
                    progressMap.remove(task.remotePath)
                    changed = true
                }
            }
            
            if (changed) {
                formState = formState.copy(
                    uploadedDocuments = docs,
                    uploadedImages = images,
                    uploadedDocTypes = docTypes,
                    uploadedImageTypes = imageTypes,
                    uploadProgress = progressMap
                )
                saveDraft(userId, formState)
            }
        }
        
        return formState
    }

    override suspend fun deleteDraft(userId: String) {
        draftDao.deleteDraft(userId)
    }

    override fun observeDraft(userId: String): Flow<VerificationFormState?> {
        return draftDao.observeDraft(userId).map { draft ->
            draft?.let { mapToFormState(it) }
        }
    }

    private fun mapToFormState(draft: VerificationDraftEntity): VerificationFormState {
        val uploadedDocs = deserializeList(draft.uploadedDocsJson).toMutableList()
        val uploadedImages = deserializeList(draft.uploadedImagesJson).toMutableList()
        val docTypes = deserializeMap(draft.uploadedDocTypesJson).toMutableMap()
        val imageTypes = deserializeMap(draft.uploadedImageTypesJson).toMutableMap()

        // Implementation of: "Merge uploaded file URLs from UploadTaskEntity (SUCCESS status) with draft data"
        // This is complex because we don't have direct userId in upload_tasks. 
        // We'll rely on the ViewModel to do the re-tracking and sync, but we can do a best-effort here if needed.
        // For now, let's keep it simple as the ViewModel update (Step 3/5) will handle the sync.

        return VerificationFormState(
            uploadedDocuments = uploadedDocs,
            uploadedImages = uploadedImages,
            uploadedDocTypes = docTypes,
            uploadedImageTypes = imageTypes,
            uploadProgress = deserializeMapInt(draft.uploadProgressJson),
            farmLocation = draft.farmLocationJson?.let { gson.fromJson(it, FarmLocation::class.java) },
            upgradeType = draft.upgradeType,
            isSubmitting = false,
            submissionSuccess = false
        )
    }

    private fun deserializeList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun deserializeMap(json: String?): Map<String, String> {
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            gson.fromJson(json, object : TypeToken<Map<String, String>>() {}.type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun deserializeMapInt(json: String?): Map<String, Int> {
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            gson.fromJson(json, object : TypeToken<Map<String, Int>>() {}.type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun extractTypeFromPath(remotePath: String): String {
        val fileName = remotePath.substringAfterLast('/')
        return fileName.substringAfter('_').substringBeforeLast('.').uppercase()
    }
}
