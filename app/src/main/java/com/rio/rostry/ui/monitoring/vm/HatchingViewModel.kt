package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.HatchingRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.BreedingRecordDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HatchingViewModel @Inject constructor(
    private val hatchingRepository: HatchingRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val productDao: ProductDao,
    private val vaccinationRepository: VaccinationRepository,
    private val taskRepository: TaskRepository,
    private val hatchingBatchDao: HatchingBatchDao,
    private val eggCollectionDao: EggCollectionDao,
    private val breedingPairDao: BreedingPairDao,
    private val breedingRecordDao: BreedingRecordDao
) : ViewModel() {

    data class UiState(
        val batches: List<HatchingBatchEntity> = emptyList(),
        val selectedBatch: HatchingBatchEntity? = null,
        val logs: List<HatchingLogEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    private val _selectedBatchId = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            hatchingRepository.observeBatches().collect { batches ->
                _ui.update { it.copy(batches = batches) }
            }
        }
        
        // Use flatMapLatest to automatically cancel previous log collection when batch changes
        viewModelScope.launch {
            _selectedBatchId.flatMapLatest { batchId ->
                if (batchId != null) {
                    hatchingRepository.observeLogs(batchId)
                } else {
                    flowOf(emptyList())
                }
            }.collect { logs ->
                _ui.update { it.copy(logs = logs) }
            }
        }
    }

    // Expose tasks per batch for UI consumption
    fun tasksFor(batchId: String) = taskRepository.observeByBatch(batchId)

    fun startBatch(
        batchName: String,
        temperatureC: Double? = null,
        humidityPct: Double? = null,
        expectedHatchDays: Int? = null
    ) {
        if (batchName.isBlank()) {
            _ui.update { it.copy(error = "Batch name cannot be empty") }
            return
        }

        viewModelScope.launch {
            try {
                val batchId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val expectedHatchAt = expectedHatchDays?.let { days ->
                    now + (days * 24L * 60 * 60 * 1000)
                }

                val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
                val batch = HatchingBatchEntity(
                    batchId = batchId,
                    name = batchName,
                    farmerId = farmerId,
                    startedAt = now,
                    expectedHatchAt = expectedHatchAt,
                    temperatureC = temperatureC,
                    humidityPct = humidityPct
                )

                hatchingRepository.upsert(batch)
                // Schedule first incubation check in 24h
                val dayMs = 24L * 60 * 60 * 1000
                taskRepository.generateIncubationCheckTask(batchId, farmerId, now + dayMs)
                _ui.update { it.copy(successMessage = "Batch '$batchName' created successfully") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Failed to create batch") }
            }
        }
    }

    fun selectBatch(batchId: String) {
        val batch = _ui.value.batches.firstOrNull { it.batchId == batchId }
        _ui.update { it.copy(selectedBatch = batch) }
        // Update the selected batch ID, which will trigger flatMapLatest to switch log collection
        _selectedBatchId.value = batchId
    }

    fun clearMessages() {
        _ui.update { it.copy(error = null, successMessage = null) }
    }

    /**
     * Log hatch outcome for a batch and optionally auto-create chick products with lineage.
     * Creates a HATCHED HatchingLogEntity per chick created (or one summary log when count==0),
     * schedules Day-1 and Day-7 vaccinations, and a Week-1 GROWTH_UPDATE task for each chick.
     */
    fun logHatchOutcome(
        batchId: String,
        count: Int,
        parentMaleId: String?,
        parentFemaleId: String?,
        notes: String? = null
    ) {
        if (count < 0) {
            _ui.update { it.copy(error = "Invalid hatch count") }
            return
        }
        viewModelScope.launch {
            try {
                val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
                val now = System.currentTimeMillis()

                if (count == 0) {
                    // Just log outcome without creating products
                    val log = com.rio.rostry.data.database.entity.HatchingLogEntity(
                        logId = UUID.randomUUID().toString(),
                        batchId = batchId,
                        farmerId = farmerId,
                        productId = null,
                        eventType = "HATCHED",
                        notes = notes,
                        updatedAt = now,
                        dirty = true
                    )
                    hatchingRepository.insert(log)
                    _ui.update { it.copy(successMessage = "Hatch outcome logged") }
                    return@launch
                }

                // Resolve lineage from batch -> collection -> pair when possible
                val batch = hatchingBatchDao.getById(batchId)
                val sourceCollectionId = batch?.sourceCollectionId
                val pair = sourceCollectionId?.let { cid -> eggCollectionDao.getById(cid)?.let { col -> breedingPairDao.getById(col.pairId) } }
                val resolvedMale = pair?.maleProductId ?: parentMaleId
                val resolvedFemale = pair?.femaleProductId ?: parentFemaleId

                repeat(count) { idx ->
                    val chickId = "chick_${now}_${idx}_${UUID.randomUUID().toString().take(6)}"
                    val product = ProductEntity(
                        productId = chickId,
                        sellerId = farmerId,
                        name = "Chick from batch ${_ui.value.batches.firstOrNull { it.batchId == batchId }?.name ?: batchId}",
                        description = notes ?: "",
                        category = "BIRD",
                        price = 0.0,
                        quantity = 1.0,
                        unit = "unit",
                        location = "",
                        imageUrls = emptyList(),
                        status = "available",
                        condition = null,
                        harvestDate = null,
                        expiryDate = null,
                        birthDate = now,
                        vaccinationRecordsJson = null,
                        weightGrams = null,
                        heightCm = null,
                        gender = null,
                        color = null,
                        breed = null,
                        familyTreeId = null,
                        parentIdsJson = null,
                        breedingStatus = null,
                        transferHistoryJson = null,
                        createdAt = now,
                        updatedAt = now,
                        lastModifiedAt = now,
                        isDeleted = false,
                        deletedAt = null,
                        dirty = true,
                        stage = "CHICK",
                        lifecycleStatus = "ACTIVE",
                        parentMaleId = resolvedMale,
                        parentFemaleId = resolvedFemale,
                        ageWeeks = 0,
                        lastStageTransitionAt = now,
                        breederEligibleAt = null,
                        isBatch = false
                    )
                    productDao.upsert(product)

                    // Insert breeding lineage record for this chick
                    if (!resolvedMale.isNullOrBlank() && !resolvedFemale.isNullOrBlank()) {
                        val br = BreedingRecordEntity(
                            recordId = UUID.randomUUID().toString(),
                            parentId = resolvedMale,
                            partnerId = resolvedFemale,
                            childId = chickId,
                            success = true,
                            notes = "Derived from batch $batchId",
                            timestamp = now
                        )
                        breedingRecordDao.insert(br)
                    }

                    // Create HATCHED log referencing the chick product
                    val log = com.rio.rostry.data.database.entity.HatchingLogEntity(
                        logId = UUID.randomUUID().toString(),
                        batchId = batchId,
                        farmerId = farmerId,
                        productId = chickId,
                        eventType = "HATCHED",
                        notes = notes,
                        updatedAt = now,
                        dirty = true
                    )
                    hatchingRepository.insert(log)

                    // Schedule Day-1 and Day-7 vaccinations
                    val day1At = now + (1L * 24 * 60 * 60 * 1000)
                    val day7At = now + (7L * 24 * 60 * 60 * 1000)
                    val v1 = VaccinationRecordEntity(
                        vaccinationId = UUID.randomUUID().toString(),
                        productId = chickId,
                        farmerId = farmerId,
                        vaccineType = "STARTER_VACCINE_DAY1",
                        scheduledAt = day1At,
                        createdAt = now,
                        updatedAt = now,
                        dirty = true
                    )
                    val v2 = VaccinationRecordEntity(
                        vaccinationId = UUID.randomUUID().toString(),
                        productId = chickId,
                        farmerId = farmerId,
                        vaccineType = "BOOSTER_VACCINE_DAY7",
                        scheduledAt = day7At,
                        createdAt = now,
                        updatedAt = now,
                        dirty = true
                    )
                    vaccinationRepository.upsert(v1)
                    vaccinationRepository.upsert(v2)

                    // Generate tasks: vaccinations + growth week-1 + juvenile stage transition at 5 weeks
                    taskRepository.generateVaccinationTask(chickId, farmerId, v1.vaccineType, day1At)
                    taskRepository.generateVaccinationTask(chickId, farmerId, v2.vaccineType, day7At)
                    taskRepository.generateGrowthTask(chickId, farmerId, week = 1, dueAt = day7At)
                    val juvenileAt = now + (35L * 24 * 60 * 60 * 1000)
                    taskRepository.generateStageTransitionTask(chickId, farmerId, stage = "JUVENILE", dueAt = juvenileAt)
                }

                _ui.update { it.copy(successMessage = "Logged hatch outcome and created $count chicks") }
            } catch (e: Exception) {
                _ui.update { it.copy(error = e.message ?: "Failed to log hatch outcome") }
            }
        }
    }
}
