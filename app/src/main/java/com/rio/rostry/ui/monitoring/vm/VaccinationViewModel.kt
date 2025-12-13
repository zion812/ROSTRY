package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.database.entity.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

import com.rio.rostry.data.repository.TraceabilityRepository

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val repo: VaccinationRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val productRepository: ProductRepository,
    private val taskRepository: TaskRepository,
    private val traceRepo: TraceabilityRepository
) : ViewModel() {

    data class UiState(
        val productId: String = "",
        val isBatch: Boolean = false,
        val records: List<VaccinationRecordEntity> = emptyList(),
        val nextDueAt: Long? = null,
        val suggestions: List<Suggestion> = emptyList(),
        val dueTasks: List<TaskEntity> = emptyList(),
        val overdueTasks: List<TaskEntity> = emptyList(),
        val products: List<com.rio.rostry.data.database.entity.ProductEntity> = emptyList()
    )

    data class Suggestion(val vaccineType: String, val dueAt: Long)

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        // Farmer-wide due/overdue vaccination tasks
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val now = System.currentTimeMillis()
            val endOfDay = endOfDay(now)
            repoTasksDue(farmerId, now, endOfDay)
            repoTasksOverdue(farmerId, now)
            loadProducts(farmerId)
        }
    }

    private fun loadProducts(farmerId: String) {
        viewModelScope.launch {
            productRepository.getProductsBySeller(farmerId).collect { res ->
                if (res is com.rio.rostry.utils.Resource.Success) {
                    _ui.update { it.copy(products = res.data ?: emptyList()) }
                }
            }
        }

    }

    private fun repoTasksDue(farmerId: String, now: Long, endOfDay: Long) {
        viewModelScope.launch {
            taskRepository.observeDueWindow(farmerId, now, endOfDay).collect { tasks ->
                _ui.update { it.copy(dueTasks = tasks.filter { t -> t.taskType == "VACCINATION" }) }
            }
        }
    }

    private fun repoTasksOverdue(farmerId: String, now: Long) {
        viewModelScope.launch {
            taskRepository.observeOverdue(farmerId, now).collect { tasks ->
                _ui.update { it.copy(overdueTasks = tasks.filter { t -> t.taskType == "VACCINATION" }) }
            }
        }
    }

    fun observe(productId: String) {
        viewModelScope.launch {
            val product = productRepository.findById(productId)
            _ui.update { it.copy(productId = productId, isBatch = product?.isBatch == true) }

            repo.observe(productId).collect { list ->
                val now = System.currentTimeMillis()
                // Compute next due by 5-week cadence from last administered or birth date
                val lastAdmin = list.filter { it.administeredAt != null }
                    .maxByOrNull { it.administeredAt ?: 0L }
                    ?.administeredAt
                val birth = product?.birthDate
                val anchor = lastAdmin ?: birth
                val fiveWeeksMs = 35L * 24 * 60 * 60 * 1000
                val nextDue = anchor?.let { it + fiveWeeksMs }

                val suggs = mutableListOf<Suggestion>()
                if (nextDue != null) {
                    suggs += Suggestion("VACCINE_5W_CADENCE", nextDue)
                }

                _ui.update { it.copy(records = list, nextDueAt = nextDue, suggestions = suggs) }
            }
        }
    }

    fun completeVaccinationTask(
        task: TaskEntity,
        vaccineType: String,
        batchCode: String?,
        administeredAt: Long,
        applyToChildren: Boolean = false
    ) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            // Find existing unadministered record for this product and vaccine or create one
            val existing = _ui.value.records
                .filter {
                    it.productId == task.productId && it.vaccineType.equals(
                        vaccineType,
                        ignoreCase = true
                    ) && it.administeredAt == null
                }
                .minByOrNull { it.scheduledAt }
            val toUpsert = if (existing != null) {
                existing.copy(
                    batchCode = batchCode,
                    administeredAt = administeredAt,
                    updatedAt = System.currentTimeMillis(),
                    dirty = true
                )
            } else {
                VaccinationRecordEntity(
                    vaccinationId = java.util.UUID.randomUUID().toString(),
                    productId = task.productId ?: return@launch,
                    farmerId = farmerId,
                    vaccineType = vaccineType,
                    batchCode = batchCode,
                    scheduledAt = administeredAt,
                    administeredAt = administeredAt,
                    dirty = true
                )
            }
            repo.upsert(toUpsert)
            // Mark task complete
            taskRepository.markComplete(task.taskId, farmerId)

            if (applyToChildren) {
                // Delegate to markAdministered logic for children, or replicate it
                // Since we have the record now, we can use markAdministered logic but we need to find children first
                val descRes = traceRepo.descendants(task.productId ?: return@launch, 1)
                if (descRes is com.rio.rostry.utils.Resource.Success) {
                    val children = descRes.data?.get(1) ?: emptyList()
                    children.forEach { childId ->
                        // Find task for child?
                        val childTasks = taskRepository.findPendingByTypeProduct(
                            farmerId,
                            childId,
                            "VACCINATION"
                        )
                        val matchingTask = childTasks.firstOrNull {
                            it.title.contains(vaccineType, ignoreCase = true) || (it.metadata
                                ?: "").contains(vaccineType, ignoreCase = true)
                        }

                        if (matchingTask != null) {
                            completeVaccinationTask(
                                matchingTask,
                                vaccineType,
                                batchCode,
                                administeredAt,
                                false
                            )
                        } else {
                            // Just record it
                            schedule(
                                childId,
                                vaccineType,
                                administeredAt,
                                null,
                                batchCode,
                                null,
                                null,
                                false
                            )
                            // Then mark administered? schedule creates un-administered.
                            // We should just create administered record directly.
                            // Re-use markAdministered logic?
                            // Let's just manually create the record like above.
                            val childRec = VaccinationRecordEntity(
                                vaccinationId = java.util.UUID.randomUUID().toString(),
                                productId = childId,
                                farmerId = farmerId,
                                vaccineType = vaccineType,
                                batchCode = batchCode,
                                scheduledAt = administeredAt,
                                administeredAt = administeredAt,
                                dirty = true
                            )
                            repo.upsert(childRec)
                        }
                    }
                }
            }
        }
    }

    fun snoozeTask(taskId: String, hours: Long = 24) {
        viewModelScope.launch {
            val snoozeUntil = System.currentTimeMillis() + hours * 60L * 60L * 1000L
            taskRepository.snooze(taskId, snoozeUntil)
        }
    }

    private fun endOfDay(now: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        cal.set(java.util.Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun schedule(
        productId: String,
        vaccineType: String,
        scheduledAt: Long,
        supplier: String? = null,
        batchCode: String? = null,
        doseMl: Double? = null,
        costInr: Double? = null,
        applyToChildren: Boolean = false
    ) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val rec = VaccinationRecordEntity(
                vaccinationId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                vaccineType = vaccineType,
                supplier = supplier,
                batchCode = batchCode,
                doseMl = doseMl,
                scheduledAt = scheduledAt,
                administeredAt = null,
                efficacyNotes = null,
                costInr = costInr
            )
            repo.upsert(rec)
            // Generate corresponding task for this vaccination
            taskRepository.generateVaccinationTask(
                productId,
                farmerId,
                vaccineType,
                scheduledAt
            )

            if (applyToChildren) {
                val descRes = traceRepo.descendants(productId, 1)
                if (descRes is com.rio.rostry.utils.Resource.Success) {
                    val children = descRes.data?.get(1) ?: emptyList()
                    children.forEach { childId ->
                        schedule(
                            childId,
                            vaccineType,
                            scheduledAt,
                            supplier,
                            batchCode,
                            doseMl,
                            costInr,
                            false
                        )
                    }
                }
            }
        }
    }

    fun markAdministered(
        vaccinationId: String,
        administeredAt: Long = System.currentTimeMillis(),
        applyToChildren: Boolean = false
    ) {
        viewModelScope.launch {
            val current = _ui.value.records.firstOrNull { it.vaccinationId == vaccinationId }
                ?: return@launch
            // 1) Persist administeredAt
            val updated = current.copy(administeredAt = administeredAt)
            repo.upsert(updated)

            // 2) Complete any matching VACCINATION task (by product + vaccineType)
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val pending = taskRepository.findPendingByTypeProduct(
                farmerId,
                current.productId,
                "VACCINATION"
            )
            // Prefer tasks with matching vaccineType in title or metadata
            val matching = pending.firstOrNull { t ->
                (t.title.contains(current.vaccineType, ignoreCase = true)) ||
                        ((t.metadata ?: "").contains(current.vaccineType, ignoreCase = true))
            } ?: pending.firstOrNull()
            matching?.let { taskRepository.markComplete(it.taskId, farmerId) }

            // 3) Schedule next dose in 35 days
            val nextDue = administeredAt + 35L * 24 * 60 * 60 * 1000
            taskRepository.generateVaccinationTask(
                productId = current.productId,
                farmerId = farmerId,
                vaccineType = current.vaccineType,
                dueAt = nextDue
            )

            // 4) Propagate to children if requested
            if (applyToChildren) {
                val descRes = traceRepo.descendants(current.productId, 1)
                if (descRes is com.rio.rostry.utils.Resource.Success) {
                    val children = descRes.data?.get(1) ?: emptyList()
                    children.forEach { childId ->
                        // Find existing record for child
                        val childRecs = repo.observe(childId).firstOrNull() ?: emptyList()
                        val childMatch = childRecs.firstOrNull {
                            it.vaccineType.equals(
                                current.vaccineType,
                                ignoreCase = true
                            ) && it.administeredAt == null
                        }

                        if (childMatch != null) {
                            // Recursively mark administered (without further propagation to avoid loops/complexity)
                            markAdministered(childMatch.vaccinationId, administeredAt, false)
                        } else {
                            // Create new administered record
                            val newRec = VaccinationRecordEntity(
                                vaccinationId = UUID.randomUUID().toString(),
                                productId = childId,
                                farmerId = farmerId,
                                vaccineType = current.vaccineType,
                                supplier = current.supplier,
                                batchCode = current.batchCode,
                                doseMl = current.doseMl,
                                scheduledAt = administeredAt,
                                administeredAt = administeredAt,
                                efficacyNotes = null,
                                costInr = current.costInr
                            )
                            repo.upsert(newRec)
                            // Also complete task if exists? Yes, implicitly handled if we had a task.
                            // But if we just created the record, we might not have a task.
                            // Let's generate next dose task for child too.
                            taskRepository.generateVaccinationTask(
                                childId,
                                farmerId,
                                current.vaccineType,
                                nextDue
                            )
                        }
                    }
                }
            }
        }
    }
    }
