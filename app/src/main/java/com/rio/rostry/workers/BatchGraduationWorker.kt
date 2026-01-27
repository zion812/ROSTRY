package com.rio.rostry.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.ui.farmer.asset.TagGroup
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.UUID

@HiltWorker
class BatchGraduationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val farmAssetDao: FarmAssetDao,
    private val farmAssetRepository: FarmAssetRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "BatchGraduationWorker"
        const val GRADUATION_AGE_WEEKS = 30 // ~7 months
    }

    override suspend fun doWork(): Result {
        Timber.d("Starting Batch Graduation Check...")
        return try {
            // 1. Find eligible batches
            // Note: In real app, we should add a specific query to DAO for efficiency.
            // For now, fetching ACTIVE BATCH/FLOCK and filtering is acceptable for < 1000 items.
            val activeAssets = farmAssetDao.getActiveAssetsOneShot() 
            val eligibleBatches = activeAssets.filter { 
                (it.assetType == "BATCH" || it.assetType == "FLOCK") && 
                (it.ageWeeks ?: 0) >= GRADUATION_AGE_WEEKS &&
                it.status == "ACTIVE"
            }
            
            if (eligibleBatches.isEmpty()) {
                Timber.d("No batches eligible for graduation.")
                return Result.success()
            }

            var graduationCount = 0

            eligibleBatches.forEach { batch ->
                // 2. Parse Tag Groups
                val tagGroups = parseTagGroups(batch.metadataJson)
                
                if (tagGroups.isEmpty()) {
                    Timber.w("Batch ${batch.name} (${batch.assetId}) is old enough but has no Tag Groups. Skipping auto-graduation.")
                    // Optional: Notification to user "Please tag Batch X for graduation"
                    return@forEach
                }

                // 3. Generate Individual Assets
                val newAssets = mutableListOf<FarmAssetEntity>()
                
                tagGroups.forEach { group ->
                    for (i in 0 until group.count) {
                        val seqNum = group.rangeStart + i
                        val tagId = "${group.prefix}$seqNum"
                        val uniqueId = UUID.randomUUID().toString()
                        
                        val individual = FarmAssetEntity(
                            assetId = uniqueId,
                            farmerId = batch.farmerId,
                            name = "${batch.name} - $tagId",
                            assetType = "ANIMAL",
                            category = batch.category,
                            status = "ACTIVE",
                            locationName = batch.locationName,
                            latitude = batch.latitude,
                            longitude = batch.longitude,
                            quantity = 1.0,
                            initialQuantity = 1.0,
                            unit = "count",
                            birthDate = batch.birthDate,
                            ageWeeks = batch.ageWeeks,
                            breed = batch.breed,
                            gender = group.gender,
                            color = batch.color, // Or specific color from tag?
                            // Store tag info clearly
                            birdCode = tagId,
                            description = "Graduated from ${batch.name}. Tag Color: ${group.color}",
                            batchId = batch.assetId,
                            origin = "GRADUATED_BATCH",
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        newAssets.add(individual)
                    }
                }
                
                // 4. Graduate
                if (newAssets.isNotEmpty()) {
                    val result = farmAssetRepository.graduateBatch(batch.assetId, newAssets)
                    if (result is com.rio.rostry.utils.Resource.Success) {
                        Timber.i("Successfully graduated Batch ${batch.name} into ${newAssets.size} individuals.")
                        graduationCount++
                    } else {
                        Timber.e("Failed to graduate batch ${batch.name}: ${result.message}")
                    }
                }
            }
            
            Timber.d("Batch Graduation Complete. Processed $graduationCount batches.")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Batch Graduation Worker failed")
            Result.failure()
        }
    }

    private fun parseTagGroups(json: String): List<TagGroup> {
        return try {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
            val meta: Map<String, Any> = gson.fromJson(json, type) ?: return emptyList()
            
            if (meta.containsKey("tagGroups")) {
                val groupsJson = gson.toJson(meta["tagGroups"])
                val groupType = object : com.google.gson.reflect.TypeToken<List<TagGroup>>() {}.type
                gson.fromJson<List<TagGroup>>(groupsJson, groupType)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
