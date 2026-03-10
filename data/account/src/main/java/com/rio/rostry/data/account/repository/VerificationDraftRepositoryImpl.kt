package com.rio.rostry.data.account.repository

import com.google.gson.Gson
import com.rio.rostry.core.model.Result
import com.rio.rostry.core.model.VerificationDraft
import com.rio.rostry.data.account.mapper.toEntity
import com.rio.rostry.data.account.mapper.toVerificationDraft
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.dao.VerificationDraftDao
import com.rio.rostry.domain.account.repository.VerificationDraftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VerificationDraftRepository for managing verification drafts.
 * 
 * Handles saving, loading, and observing verification form state with
 * automatic merging of background upload completions.
 */
@Singleton
class VerificationDraftRepositoryImpl @Inject constructor(
    private val draftDao: VerificationDraftDao,
    private val uploadTaskDao: UploadTaskDao,
    private val gson: Gson
) : VerificationDraftRepository {

    override suspend fun saveDraft(userId: String, draft: VerificationDraft): Result<Unit> {
        return try {
            val entity = draft.toEntity(gson)
            draftDao.upsertDraft(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun loadDraft(userId: String): Result<VerificationDraft?> {
        return try {
            val draft = draftDao.getDraft(userId) ?: return Result.Success(null)
            var domainDraft = draft.toVerificationDraft(gson)
            
            // Merge background completions (tasks finished while app was closed)
            val successfulTasks = uploadTaskDao.getSuccessfulByUser(userId)
            if (successfulTasks.isNotEmpty()) {
                val docs = domainDraft.uploadedDocuments.toMutableList()
                val images = domainDraft.uploadedImages.toMutableList()
                val docTypes = domainDraft.uploadedDocTypes.toMutableMap()
                val imageTypes = domainDraft.uploadedImageTypes.toMutableMap()
                val progressMap = domainDraft.uploadProgress.toMutableMap()
                
                var changed = false
                successfulTasks.forEach { task ->
                    val downloadUrl = try { 
                        JSONObject(task.contextJson ?: "{}").optString("downloadUrl") 
                    } catch (e: Exception) { 
                        null 
                    }
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
                    domainDraft = domainDraft.copy(
                        uploadedDocuments = docs,
                        uploadedImages = images,
                        uploadedDocTypes = docTypes,
                        uploadedImageTypes = imageTypes,
                        uploadProgress = progressMap
                    )
                    saveDraft(userId, domainDraft)
                }
            }
            
            Result.Success(domainDraft)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteDraft(userId: String): Result<Unit> {
        return try {
            draftDao.deleteDraft(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeDraft(userId: String): Flow<VerificationDraft?> {
        return draftDao.observeDraft(userId).map { draft ->
            draft?.toVerificationDraft(gson)
        }
    }

    private fun extractTypeFromPath(remotePath: String): String {
        val fileName = remotePath.substringAfterLast('/')
        return fileName.substringAfter('_').substringBeforeLast('.').uppercase()
    }
}


