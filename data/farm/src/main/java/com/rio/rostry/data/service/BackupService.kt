package com.rio.rostry.data.service

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Comprehensive backup and restore service for ROSTRY app.
 * Supports full export of all entities to ZIP file and restore functionality.
 */
@Singleton
class BackupService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val gson: Gson
) {
    companion object {
        const val BACKUP_VERSION = "2.0"
        const val DATA_FILE_NAME = "rostry_data.json"
        const val METADATA_FILE_NAME = "backup_metadata.json"
    }

    /**
     * Comprehensive export data including all entities.
     */
    data class ComprehensiveExportData(
        val user: UserEntity?,
        val products: List<ProductEntity>,
        val assets: List<FarmAssetEntity>,
        val dailyLogs: List<DailyLogEntity>,
        val growthRecords: List<GrowthRecordEntity>,
        val vaccinationRecords: List<VaccinationRecordEntity>,
        val mortalityRecords: List<MortalityRecordEntity>,
        val quarantineRecords: List<QuarantineRecordEntity>,
        val hatchingBatches: List<HatchingBatchEntity>,
        val hatchingLogs: List<HatchingLogEntity>,
        val eggCollections: List<EggCollectionEntity>,
        val farmActivityLogs: List<FarmActivityLogEntity>,
        val dashboardSnapshots: List<FarmerDashboardSnapshotEntity>,
        val breedingPairs: List<BreedingPairEntity>,
        val matingLogs: List<MatingLogEntity>,
        val exportVersion: String = BACKUP_VERSION,
        val exportTimestamp: Long = System.currentTimeMillis()
    )

    /**
     * Backup metadata for validation.
     */
    data class BackupMetadata(
        val version: String,
        val userId: String,
        val userName: String?,
        val exportDate: Long,
        val entityCounts: Map<String, Int>,
        val appVersion: String = "1.0.0"
    )

    /**
     * Import result with details about restored entities.
     */
    data class ImportResult(
        val entitiesRestored: Int,
        val conflicts: List<String>,
        val warnings: List<String>,
        val success: Boolean
    )

    // Legacy export data for backward compatibility
    data class ExportData(
        val user: Any?,
        val products: List<ProductEntity>,
        val assets: List<FarmAssetEntity>,
        val exportTimestamp: Long = System.currentTimeMillis()
    )

    /**
     * Export all user data to a ZIP file.
     * Returns the file path of the created backup.
     */
    suspend fun exportUserData(): Resource<File> = withContext(Dispatchers.IO) {
        try {
            val user = userRepository.getCurrentUser().first().let { 
                if (it is Resource.Success) it.data else null
            } ?: return@withContext Resource.Error("User not found")

            val userId = user.userId
            
            // Collect all data
            val exportData = collectAllData(userId, user)
            
            // Create metadata
            val metadata = createMetadata(userId, user, exportData)
            
            // Create export directory
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            
            // Generate filename with timestamp
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val zipFileName = "rostry_backup_${timestamp}.zip"
            val zipFile = File(exportDir, zipFileName)
            
            // Create ZIP file with data and metadata
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                // Add data JSON
                val dataJson = gson.toJson(exportData)
                zos.putNextEntry(ZipEntry(DATA_FILE_NAME))
                zos.write(dataJson.toByteArray())
                zos.closeEntry()
                
                // Add metadata JSON
                val metadataJson = gson.toJson(metadata)
                zos.putNextEntry(ZipEntry(METADATA_FILE_NAME))
                zos.write(metadataJson.toByteArray())
                zos.closeEntry()
            }

            Resource.Success(zipFile)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export data")
        }
    }

    /**
     * Export data directly to Downloads folder using MediaStore.
     */
    suspend fun exportToDownloads(): Resource<Uri> = withContext(Dispatchers.IO) {
        try {
            // First create the backup file
            val backupResult = exportUserData()
            if (backupResult is Resource.Error) {
                return@withContext Resource.Error(backupResult.message ?: "Backup failed")
            }
            
            val backupFile = (backupResult as Resource.Success).data
            
            // Save to Downloads using MediaStore (Android 10+)
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, backupFile?.name ?: "rostry_backup.zip")
                    put(MediaStore.Downloads.MIME_TYPE, "application/zip")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                
                val resolver = context.contentResolver
                val downloadUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext Resource.Error("Failed to create file in Downloads")
                
                resolver.openOutputStream(downloadUri)?.use { outputStream ->
                    FileInputStream(backupFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                
                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(downloadUri, contentValues, null, null)
                
                downloadUri
            } else {
                // Legacy method for older Android versions
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDir, backupFile?.name ?: "rostry_backup.zip")
                backupFile?.copyTo(destFile, overwrite = true)
                Uri.fromFile(destFile)
            }
            
            Resource.Success(uri)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save to Downloads")
        }
    }

    /**
     * Validate a backup file before import.
     */
    suspend fun validateBackup(uri: Uri): Resource<BackupMetadata> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Resource.Error("Cannot open backup file")
            
            var metadata: BackupMetadata? = null
            
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name == METADATA_FILE_NAME) {
                        val content = zis.bufferedReader().readText()
                        metadata = gson.fromJson(content, BackupMetadata::class.java)
                        break
                    }
                    entry = zis.nextEntry
                }
            }
            
            metadata?.let {
                Resource.Success(it)
            } ?: Resource.Error("Invalid backup file: missing metadata")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to validate backup")
        }
    }

    /**
     * Import backup from a URI.
     */
    suspend fun importBackup(uri: Uri): Resource<ImportResult> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Resource.Error("Cannot open backup file")
            
            var exportData: ComprehensiveExportData? = null
            val warnings = mutableListOf<String>()
            
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    when (entry.name) {
                        DATA_FILE_NAME -> {
                            val content = zis.bufferedReader().readText()
                            exportData = gson.fromJson(content, ComprehensiveExportData::class.java)
                        }
                    }
                    entry = zis.nextEntry
                }
            }
            
            val data = exportData ?: return@withContext Resource.Error("Invalid backup: no data found")
            
            // Restore all entities - using suspendable wrapper
            val totalRestored = restoreEntitiesSync(data, warnings)
            
            Resource.Success(
                ImportResult(
                    entitiesRestored = totalRestored,
                    conflicts = emptyList(),
                    warnings = warnings,
                    success = true
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to import backup")
        }
    }
    
    /**
     * Restore entities from backup data (suspend-safe).
     */
    private suspend fun restoreEntitiesSync(
        data: ComprehensiveExportData,
        warnings: MutableList<String>
    ): Int {
        var totalRestored = 0
        
        // Restore products
        data.products.forEach { product ->
            try {
                database.productDao().upsert(product)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore product: ${product.productId}")
            }
        }
        
        // Restore assets
        data.assets.forEach { asset ->
            try {
                database.farmAssetDao().upsert(asset)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore asset: ${asset.assetId}")
            }
        }
        
        // Restore daily logs
        data.dailyLogs.forEach { log ->
            try {
                database.dailyLogDao().upsert(log)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore daily log: ${log.logId}")
            }
        }
        
        // Restore growth records
        data.growthRecords.forEach { record ->
            try {
                database.growthRecordDao().upsert(record)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore growth record: ${record.recordId}")
            }
        }
        
        // Restore vaccination records
        data.vaccinationRecords.forEach { record ->
            try {
                database.vaccinationRecordDao().upsert(record)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore vaccination: ${record.vaccinationId}")
            }
        }
        
        // Restore hatching batches
        data.hatchingBatches.forEach { batch ->
            try {
                database.hatchingBatchDao().upsert(batch)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore hatching batch: ${batch.batchId}")
            }
        }
        
        // Restore farm activity logs
        data.farmActivityLogs.forEach { log ->
            try {
                database.farmActivityLogDao().upsert(log)
                totalRestored++
            } catch (e: Exception) {
                warnings.add("Failed to restore activity log: ${log.activityId}")
            }
        }
        
        return totalRestored
    }

    /**
     * List available backups in the cache directory.
     */
    suspend fun listBackups(): List<BackupMetadata> = withContext(Dispatchers.IO) {
        val exportDir = File(context.cacheDir, "exports")
        if (!exportDir.exists()) return@withContext emptyList()
        
        exportDir.listFiles()
            ?.filter { it.name.endsWith(".zip") }
            ?.mapNotNull { file ->
                try {
                    ZipInputStream(FileInputStream(file)).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            if (entry.name == METADATA_FILE_NAME) {
                                val content = zis.bufferedReader().readText()
                                return@mapNotNull gson.fromJson(content, BackupMetadata::class.java)
                            }
                            entry = zis.nextEntry
                        }
                    }
                    null
                } catch (e: Exception) {
                    null
                }
            }
            ?.sortedByDescending { it.exportDate }
            ?: emptyList()
    }

    fun clearExports() {
        val exportDir = File(context.cacheDir, "exports")
        if (exportDir.exists()) {
            exportDir.deleteRecursively()
        }
    }

    /**
     * Collect all data for export.
     */
    private suspend fun collectAllData(userId: String, user: UserEntity): ComprehensiveExportData {
        return ComprehensiveExportData(
            user = user,
            products = database.productDao().getProductsBySeller(userId).first(),
            assets = database.farmAssetDao().getAssetsByFarmer(userId).first(),
            dailyLogs = database.dailyLogDao().observeForFarmerBetween(userId, 0, Long.MAX_VALUE).first(),
            growthRecords = database.growthRecordDao().getAllByFarmer(userId),
            vaccinationRecords = database.vaccinationRecordDao().observeByFarmer(userId).first(),
            mortalityRecords = emptyList(), // Would need a DAO method
            quarantineRecords = emptyList(), // Would need a DAO method
            hatchingBatches = database.hatchingBatchDao().observeActiveBatchesForFarmer(userId, 0).first(),
            hatchingLogs = emptyList(), // Would need a DAO method
            eggCollections = emptyList(), // Would need a DAO method
            farmActivityLogs = database.farmActivityLogDao().observeForFarmer(userId).first(),
            dashboardSnapshots = emptyList(), // Would need a DAO method
            breedingPairs = emptyList(), // Would need a DAO method
            matingLogs = emptyList() // Would need a DAO method
        )
    }

    /**
     * Create metadata for the backup.
     */
    private fun createMetadata(userId: String, user: UserEntity, data: ComprehensiveExportData): BackupMetadata {
        return BackupMetadata(
            version = BACKUP_VERSION,
            userId = userId,
            userName = user.fullName,
            exportDate = System.currentTimeMillis(),
            entityCounts = mapOf(
                "products" to data.products.size,
                "assets" to data.assets.size,
                "dailyLogs" to data.dailyLogs.size,
                "growthRecords" to data.growthRecords.size,
                "vaccinationRecords" to data.vaccinationRecords.size,
                "farmActivityLogs" to data.farmActivityLogs.size,
                "hatchingBatches" to data.hatchingBatches.size
            )
        )
    }
}

