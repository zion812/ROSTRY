package com.rio.rostry.data.repository.monitoring

import com.rio.rostry.data.database.entity.GrowthRecordEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.first
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for onboarding purchased products into farm monitoring system.
 * Initializes growth tracking and vaccination schedules for newly acquired birds.
 */
interface FarmOnboardingRepository {
    suspend fun addProductToFarmMonitoring(productId: String, farmerId: String): Resource<Unit>
}

@Singleton
class FarmOnboardingRepositoryImpl @Inject constructor(
    private val productRepository: ProductRepository,
    private val growthRepository: GrowthRepository,
    private val vaccinationRepository: VaccinationRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val taskRepository: TaskRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : FarmOnboardingRepository {

    override suspend fun addProductToFarmMonitoring(
        productId: String,
        farmerId: String
    ): Resource<Unit> {
        return try {
            // Check if monitoring records already exist (idempotent)
            val existingGrowth = growthRepository.observe(productId).first()
            if (existingGrowth.any { it.farmerId == farmerId }) {
                return Resource.Success(Unit) // Already exists
            }

            // Fetch product entity for baseline data - unwrap Resource
            val productResource = productRepository.getProductById(productId).first()
            val product = when (productResource) {
                is Resource.Success -> productResource.data ?: return Resource.Error("Product not found")
                is Resource.Error -> return Resource.Error(productResource.message ?: "Product not found")
                is Resource.Loading -> return Resource.Error("Product data still loading")
            }

            val now = System.currentTimeMillis()

            // Create initial growth record with week 0
            val initialGrowthRecord = GrowthRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                week = 0,
                weightGrams = product.weightGrams?.toDouble(),
                heightCm = product.heightCm,
                healthStatus = "OK",
                milestone = "Initial record created from marketplace purchase",
                createdAt = now,
                dirty = true,
                syncedAt = null
            )
            growthRepository.upsert(initialGrowthRecord)

            // Create vaccination schedule based on age
            if (product.birthDate != null) {
                val ageInDays = ((now - product.birthDate) / TimeUnit.DAYS.toMillis(1)).toInt()
                createVaccinationSchedule(productId, farmerId, product.birthDate, ageInDays)
            }

            // Seed initial daily log (idempotent via DailyLogRepository merge)
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

            // Create initial tasks (vaccination next 7 days for chicks; weekly growth)
            if (product.birthDate != null) {
                val ageDays = ((now - product.birthDate) / TimeUnit.DAYS.toMillis(1)).toInt()
                if (ageDays < 35) {
                    val dueVax = product.birthDate + TimeUnit.DAYS.toMillis(7)
                    taskRepository.generateVaccinationTask(productId, farmerId, "First vaccination", dueVax)
                }
            }
            val dueGrowth = now + TimeUnit.DAYS.toMillis(7)
            taskRepository.generateGrowthTask(productId, farmerId, week = 1, dueAt = dueGrowth)

            // Batch split reminder at 12 weeks
            if (product.isBatch == true) {
                val base = product.birthDate ?: now
                val dueSplit = base + TimeUnit.DAYS.toMillis(84) // 12 weeks
                taskRepository.generateBatchSplitTask(batchId = productId, farmerId = farmerId, dueAt = dueSplit)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add product to farm monitoring")
        }
    }

    private suspend fun createVaccinationSchedule(
        productId: String,
        farmerId: String,
        birthDate: Long,
        currentAgeInDays: Int
    ) {
        // Standard poultry vaccination schedule
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
            .filter { it.dayOfLife > currentAgeInDays } // Only future vaccinations
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
