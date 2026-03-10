package com.rio.rostry.data.repository.monitoring

import com.google.gson.Gson
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.database.entity.TaskEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.domain.monitoring.repository.AnalyticsRepository
import com.rio.rostry.domain.monitoring.repository.FarmOnboardingRepository
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FarmOnboardingRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val growthRepository: GrowthRepository,
    private val vaccinationRepository: VaccinationRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val taskRepository: TaskRepository,
    private val intelligentNotificationService: IntelligentNotificationService,
    private val analyticsRepository: AnalyticsRepository,
    private val farmAssetRepository: FarmAssetRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : FarmOnboardingRepository {

    override suspend fun addProductToFarmMonitoring(
        productId: String,
        farmerId: String,
        healthStatus: String
    ): Resource<List<String>> {
        return try {
            val existingGrowth = growthRepository.observe(productId).first()
            if (existingGrowth.any { it.farmerId == farmerId }) {
                return Resource.Success(emptyList())
            }

            val product = productDao.findById(productId) ?: return Resource.Error("Product not found")

            val now = System.currentTimeMillis()
            val taskIds = mutableListOf<String>()

            val farmAsset = FarmAssetEntity(
                assetId = productId,
                farmerId = farmerId,
                name = product.name,
                assetType = if (product.isBatch == true) "BATCH" else "ANIMAL",
                category = "Chicken",
                status = "ACTIVE",
                locationName = product.location,
                latitude = product.latitude,
                longitude = product.longitude,
                quantity = product.quantity,
                initialQuantity = product.quantity,
                unit = product.unit,
                birthDate = product.birthDate,
                ageWeeks = product.ageWeeks,
                breed = product.breed,
                gender = product.gender,
                color = product.color,
                healthStatus = healthStatus,
                description = product.description,
                imageUrls = product.imageUrls,
                createdAt = now,
                updatedAt = now,
                dirty = true,
                metadataJson = if(product.isBatch == true) "{\"tagGroups\":[]}" else "{}",
                batchId = product.batchId
            )
            farmAssetRepository.addAsset(farmAsset)

            val initialGrowthRecord = GrowthRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                week = 0,
                weightGrams = product.weightGrams?.toDouble(),
                heightCm = product.heightCm,
                healthStatus = healthStatus,
                milestone = "Initial record created from marketplace purchase",
                createdAt = now,
                dirty = true,
                syncedAt = null
            )
            growthRepository.upsert(initialGrowthRecord)

            val bd = product.birthDate
            if (bd != null) {
                val ageInDays = ((now - bd) / TimeUnit.DAYS.toMillis(1)).toInt()
                createVaccinationSchedule(productId, farmerId, bd, ageInDays)
            }

            val todayMidnight = todayMidnight()
            val initialLog = com.rio.rostry.data.database.entity.DailyLogEntity(
                logId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                logDate = todayMidnight,
                weightGrams = product.weightGrams?.toDouble(),
                activityLevel = "NORMAL",
                notes = if (product.isBatch == true) "Batch onboarding: ${product.quantity.toInt()} birds" else "Initial onboarding record"
            )
            dailyLogRepository.upsert(initialLog)

            val bd2 = product.birthDate
            if (bd2 != null) {
                val ageDays = ((now - bd2) / TimeUnit.DAYS.toMillis(1)).toInt()
                if (ageDays < 35) {
                    val dueVax = bd2 + TimeUnit.DAYS.toMillis(7)
                    val vaxTask = TaskEntity(
                        taskId = generateId("task_vax_"),
                        farmerId = farmerId,
                        productId = productId,
                        taskType = "VACCINATION",
                        title = "Vaccination: First vaccination",
                        dueAt = dueVax,
                        priority = "HIGH",
                        metadata = Gson().toJson(mapOf("vaccineType" to "First vaccination"))
                    )
                    taskRepository.upsert(vaxTask)
                    taskIds.add(vaxTask.taskId)
                }
            }
            val dueGrowth = now + TimeUnit.DAYS.toMillis(7)
            val growthTask = TaskEntity(
                taskId = generateId("task_growth_"),
                farmerId = farmerId,
                productId = productId,
                taskType = "GROWTH_UPDATE",
                title = "Growth update: Week 1",
                dueAt = dueGrowth,
                priority = "MEDIUM",
                metadata = Gson().toJson(mapOf("week" to 1))
            )
            taskRepository.upsert(growthTask)
            taskIds.add(growthTask.taskId)

            if (product.isBatch == true) {
                val base = product.birthDate ?: now
                val dueSplit = base + TimeUnit.DAYS.toMillis(84)
                val splitTask = TaskEntity(
                    taskId = generateId("task_batch_split_"),
                    farmerId = farmerId,
                    productId = null,
                    batchId = productId,
                    taskType = "BATCH_SPLIT",
                    title = "Split batch into individuals",
                    dueAt = dueSplit,
                    priority = "MEDIUM",
                    metadata = Gson().toJson(mapOf("reminder" to "proactive"))
                )
                taskRepository.upsert(splitTask)
                taskIds.add(splitTask.taskId)
            }

            val dailyTask = TaskEntity(
                taskId = generateId("task_daily_"),
                farmerId = farmerId,
                productId = productId,
                taskType = "DAILY_LOG",
                title = "First Daily Log",
                dueAt = now,
                priority = "HIGH"
            )
            taskRepository.upsert(dailyTask)
            taskIds.add(dailyTask.taskId)

            val reviewVaxTask = TaskEntity(
                taskId = generateId("task_vax_"),
                farmerId = farmerId,
                productId = productId,
                taskType = "VACCINATION",
                title = "Vaccination: Review Vaccination Schedule",
                dueAt = now + TimeUnit.DAYS.toMillis(3),
                priority = "MEDIUM",
                metadata = Gson().toJson(mapOf("vaccineType" to "Review Vaccination Schedule"))
            )
            taskRepository.upsert(reviewVaxTask)
            taskIds.add(reviewVaxTask.taskId)

            val firstGrowthTask = TaskEntity(
                taskId = generateId("task_growth_"),
                farmerId = farmerId,
                productId = productId,
                taskType = "GROWTH_UPDATE",
                title = "Growth update: Week 0",
                dueAt = now + TimeUnit.DAYS.toMillis(7),
                priority = "MEDIUM",
                metadata = Gson().toJson(mapOf("week" to 0))
            )
            taskRepository.upsert(firstGrowthTask)
            taskIds.add(firstGrowthTask.taskId)

            intelligentNotificationService.notifyOnboardingComplete(
                productId,
                product.name ?: "Unknown Product",
                taskIds.size,
                product.isBatch == true
            )

            Resource.Success(taskIds)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add product to farm monitoring")
        }
    }

    override fun observeRecentOnboardingActivity(farmerId: String, days: Int): Flow<List<OnboardingActivity>> {
        val since = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong())
        val productsFlow = productDao.observeRecentlyAddedForFarmer(farmerId, since)
        val tasksFlow = taskRepository.observeByFarmer(farmerId)
        val vaccinationsFlow = vaccinationRepository.observeByFarmer(farmerId)

        return combine(productsFlow, tasksFlow, vaccinationsFlow) { products, tasks, vaccinations ->
            products.map { product ->
                val tasksCreated = tasks.count { it.productId == product.productId }
                val vaccinationsScheduled = vaccinations.count { it.productId == product.productId }
                OnboardingActivity(
                    productId = product.productId,
                    productName = product.name ?: "Unknown",
                    addedAt = product.createdAt,
                    tasksCreated = tasksCreated,
                    vaccinationsScheduled = vaccinationsScheduled,
                    isBatch = product.isBatch == true
                )
            }
        }
    }

    override suspend fun getOnboardingStats(farmerId: String): OnboardingStats {
        val weekAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        val products = productDao.observeRecentlyAddedForFarmer(farmerId, weekAgo).first()
        val tasks = taskRepository.observeByFarmer(farmerId).first()
        val birdsAdded = products.count { it.isBatch != true }
        val batchesAdded = products.count { it.isBatch == true }
        val tasksGenerated = tasks.count { it.createdAt >= weekAgo }
        return OnboardingStats(
            birdsAddedThisWeek = birdsAdded,
            batchesAddedThisWeek = batchesAdded,
            tasksGenerated = tasksGenerated
        )
    }

    private fun generateId(prefix: String): String = "$prefix${System.currentTimeMillis()}"

    private suspend fun createVaccinationSchedule(
        productId: String,
        farmerId: String,
        birthDate: Long,
        currentAgeInDays: Int
    ) {
        val standardSchedule = listOf(
            VaccinationSchedule("Marek's Disease", 1),
            VaccinationSchedule("Newcastle Disease", 7),
            VaccinationSchedule("Infectious Bronchitis", 14),
            VaccinationSchedule("Gumboro", 21),
            VaccinationSchedule("Fowl Pox", 30),
            VaccinationSchedule("Newcastle Booster", 60),
            VaccinationSchedule("Fowl Cholera", 90)
        )

        val now = System.currentTimeMillis()

        standardSchedule
            .filter { it.dayOfLife > currentAgeInDays }
            .forEach { schedule ->
                val scheduledDate = birthDate + TimeUnit.DAYS.toMillis(schedule.dayOfLife.toLong())
                val vaccinationRecord = VaccinationRecordEntity(
                    vaccinationId = UUID.randomUUID().toString(),
                    productId = productId,
                    farmerId = farmerId,
                    vaccineType = schedule.vaccineType,
                    scheduledAt = scheduledDate,
                    administeredAt = null,
                    supplier = null,
                    batchCode = null,
                    doseMl = null,
                    efficacyNotes = "Auto-scheduled based on purchase",
                    costInr = null,
                    createdAt = now,
                    dirty = true,
                    syncedAt = null
                )
                vaccinationRepository.upsert(vaccinationRecord)
            }
    }

    private data class VaccinationSchedule(
        val vaccineType: String,
        val dayOfLife: Int
    )

    private fun todayMidnight(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
