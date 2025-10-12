package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.VaccinationRepository
import com.rio.rostry.data.database.entity.VaccinationRecordEntity
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.data.database.entity.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val repo: VaccinationRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val productDao: ProductDao,
    private val taskRepository: TaskRepository
) : ViewModel() {

    data class UiState(
        val productId: String = "",
        val records: List<VaccinationRecordEntity> = emptyList(),
        val nextDueAt: Long? = null,
        val suggestions: List<Suggestion> = emptyList(),
        val dueTasks: List<TaskEntity> = emptyList(),
        val overdueTasks: List<TaskEntity> = emptyList()
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
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                val now = System.currentTimeMillis()
                // Compute next due by 5-week cadence from last administered or birth date
                val lastAdmin = list.filter { it.administeredAt != null }
                    .maxByOrNull { it.administeredAt ?: 0L }
                    ?.administeredAt
                val birth = productDao.findById(productId)?.birthDate
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
        administeredAt: Long
    ) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            // Find existing unadministered record for this product and vaccine or create one
            val existing = _ui.value.records
                .filter { it.productId == task.productId && it.vaccineType.equals(vaccineType, ignoreCase = true) && it.administeredAt == null }
                .minByOrNull { it.scheduledAt }
            val toUpsert = if (existing != null) {
                existing.copy(batchCode = batchCode, administeredAt = administeredAt, updatedAt = System.currentTimeMillis(), dirty = true)
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
        costInr: Double? = null
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
            taskRepository.generateVaccinationTask(productId, farmerId, vaccineType, scheduledAt)
        }
    }

    fun markAdministered(vaccinationId: String, administeredAt: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            val current = _ui.value.records.firstOrNull { it.vaccinationId == vaccinationId } ?: return@launch
            // 1) Persist administeredAt
            val updated = current.copy(administeredAt = administeredAt)
            repo.upsert(updated)

            // 2) Complete any matching VACCINATION task (by product + vaccineType)
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val pending = taskRepository.findPendingByTypeProduct(farmerId, current.productId, "VACCINATION")
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
        }
    }
}
