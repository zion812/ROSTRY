package com.rio.rostry.data.service

import android.content.Context
import com.google.gson.Gson
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val gson: Gson
) {

    data class ExportData(
        val user: Any?,
        val products: List<ProductEntity>,
        val assets: List<FarmAssetEntity>,
        val exportTimestamp: Long = System.currentTimeMillis()
    )

    suspend fun exportUserData(): Resource<File> = withContext(Dispatchers.IO) {
        try {
            val user = userRepository.getCurrentUser().first().let { 
                if (it is Resource.Success) it.data else null
            } ?: return@withContext Resource.Error("User not found")

            val userId = user.userId
            val products = database.productDao().getProductsBySeller(userId).first()
            val assets = database.farmAssetDao().getAssetsByFarmer(userId).first()

            val exportData = ExportData(
                user = user,
                products = products,
                assets = assets
            )

            val jsonString = gson.toJson(exportData)
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val jsonFile = File(exportDir, "rostry_data_${userId}.json")
            jsonFile.writeText(jsonString)

            val zipFile = File(exportDir, "rostry_backup_${System.currentTimeMillis()}.zip")
            ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
                val entry = ZipEntry("data.json")
                zos.putNextEntry(entry)
                zos.write(jsonString.toByteArray())
                zos.closeEntry()
            }

            Resource.Success(zipFile)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export data")
        }
    }

    fun clearExports() {
        val exportDir = File(context.cacheDir, "exports")
        if (exportDir.exists()) {
            exportDir.deleteRecursively()
        }
    }
}
