package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.QuarantineRepository
import com.rio.rostry.data.repository.monitoring.FarmAlertRepository
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class QuarantineViewModel @Inject constructor(
    private val repo: QuarantineRepository,
    private val farmAlertRepository: FarmAlertRepository,
    private val farmAlertDao: com.rio.rostry.data.database.dao.FarmAlertDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    @ApplicationContext private val appContext: android.content.Context,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao,
    private val taskRepository: com.rio.rostry.data.repository.monitoring.TaskRepository
) : ViewModel() {

    data class QuarantineStatusEntry(val status: String, val at: Long)

    data class UiState(
        val productId: String = "",
        val active: List<QuarantineRecordEntity> = emptyList(),
        val history: List<QuarantineRecordEntity> = emptyList(),
        val canDischarge: Map<String, Boolean> = emptyMap(),
        val nextUpdateDue: Map<String, Long> = emptyMap(),
        val isOverdue: Map<String, Boolean> = emptyMap(),
        val healthyUpdatesCount: Map<String, Int> = emptyMap()
    )
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun observe(productId: String) {
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                val active = list.filter { r -> r.status == "ACTIVE" }
                val history = list.filter { r -> r.status != "ACTIVE" }

                val now = System.currentTimeMillis()
                val twelveHours = java.util.concurrent.TimeUnit.HOURS.toMillis(12)

                val healthyCounts = active.associate { rec ->
                    val count = parseStatusHistory(rec).count { st ->
                        val s = st.status.uppercase()
                        s == "IMPROVING" || s == "STABLE"
                    }
                    rec.quarantineId to count
                }

                val canDischarge = active.associate { rec ->
                    val timeSinceUpdate = now - rec.lastUpdatedAt
                    val can = (healthyCounts[rec.quarantineId] ?: 0) >= 2 && (timeSinceUpdate <= twelveHours)
                    rec.quarantineId to can
                }

                val nextUpdateDue = active.associate { rec ->
                    rec.quarantineId to (rec.lastUpdatedAt + twelveHours)
                }

                val isOverdue = active.associate { rec ->
                    val nextDue = rec.lastUpdatedAt + twelveHours
                    rec.quarantineId to (now > nextDue)
                }

                _ui.update {
                    it.copy(
                        active = active,
                        history = history,
                        canDischarge = canDischarge,
                        nextUpdateDue = nextUpdateDue,
                        isOverdue = isOverdue,
                        healthyUpdatesCount = healthyCounts
                    )
                }
            }
        }
    }

    fun start(productId: String, reason: String, protocol: String?) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val now = System.currentTimeMillis()
            val rec = QuarantineRecordEntity(
                quarantineId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                reason = reason,
                protocol = protocol,
                medicationScheduleJson = null,
                vetNotes = null,
                startedAt = now,
                lastUpdatedAt = now,
                updatesCount = 0,
                endedAt = null,
                status = "ACTIVE",
                updatedAt = now,
                dirty = true
            )
            repo.insert(rec)
            // Set product lifecycle to QUARANTINE and schedule first 12h check
            productDao.updateLifecycleStatus(productId, "QUARANTINE", now)
            val twelveHours = java.util.concurrent.TimeUnit.HOURS.toMillis(12)
            taskRepository.generateQuarantineCheckTask(productId, farmerId, now + twelveHours)
        }
    }

    fun dischargeQuarantine(quarantineId: String) {
        viewModelScope.launch {
            val active = _ui.value.active.find { it.quarantineId == quarantineId } ?: return@launch
            val now = System.currentTimeMillis()
            val updated = active.copy(
                endedAt = now,
                status = "RECOVERED",
                updatedAt = now,
                dirty = true
            )
            repo.update(updated)
            // Restore product lifecycle to ACTIVE
            productDao.updateLifecycleStatus(active.productId, "ACTIVE", now)
        }
    }

    private fun parseStatusHistory(rec: QuarantineRecordEntity): List<QuarantineStatusEntry> {
        // Prefer new column
        val direct = rec.statusHistoryJson
        if (!direct.isNullOrBlank()) {
            return runCatching {
                val arr = com.google.gson.JsonParser.parseString(direct).asJsonArray
                arr.mapNotNull { el ->
                    val o = el.asJsonObject
                    val st = o.get("status")?.asString
                    val at = o.get("at")?.asLong
                    if (st != null && at != null) QuarantineStatusEntry(st, at) else null
                }
            }.getOrElse { emptyList() }
        }
        // Legacy fallback: embedded under medicationScheduleJson.statusHistory
        val legacyJson = rec.medicationScheduleJson ?: return emptyList()
        return runCatching {
            val obj = com.google.gson.JsonParser.parseString(legacyJson).asJsonObject
            if (!obj.has("statusHistory")) return emptyList()
            val arr = obj.getAsJsonArray("statusHistory")
            arr.mapNotNull { el ->
                val o = el.asJsonObject
                val st = o.get("status")?.asString
                val at = o.get("at")?.asLong
                if (st != null && at != null) QuarantineStatusEntry(st, at) else null
            }
        }.getOrElse { emptyList() }
    }

    private fun appendStatusHistoryArrayJson(existingArrayJson: String?, status: String, at: Long): String {
        val gson = com.google.gson.Gson()
        val arr = when {
            existingArrayJson.isNullOrBlank() -> com.google.gson.JsonArray()
            else -> runCatching { com.google.gson.JsonParser.parseString(existingArrayJson).asJsonArray }.getOrElse { com.google.gson.JsonArray() }
        }
        val entry = com.google.gson.JsonObject().apply {
            addProperty("status", status)
            addProperty("at", at)
        }
        arr.add(entry)
        return gson.toJson(arr)
    }

    fun updateQuarantine(
        quarantineId: String,
        notes: String?,
        medication: String?,
        status: String?
    ) {
        viewModelScope.launch {
            val active = _ui.value.active.find { it.quarantineId == quarantineId } ?: return@launch
            
            val now = System.currentTimeMillis()
            val newStatusHistoryJson = status?.let { appendStatusHistoryArrayJson(active.statusHistoryJson, it, now) } ?: active.statusHistoryJson
            val updated = active.copy(
                vetNotes = notes,
                medicationScheduleJson = medication ?: active.medicationScheduleJson,
                statusHistoryJson = newStatusHistoryJson,
                lastUpdatedAt = now,
                updatesCount = active.updatesCount + 1,
                updatedAt = now,
                dirty = true
            )
            repo.update(updated)

            // Complete existing pending quarantine-check tasks instead of scheduling new ones
            val farmerId = firebaseAuth.currentUser?.uid
            if (farmerId != null) {
                val pending = taskRepository.findPendingByTypeProduct(farmerId, active.productId, "QUARANTINE_CHECK")
                pending.forEach { taskRepository.markComplete(it.taskId, farmerId) }
                // Schedule next check at +12h
                val twelveHours = java.util.concurrent.TimeUnit.HOURS.toMillis(12)
                taskRepository.generateQuarantineCheckTask(active.productId, farmerId, now + twelveHours)
            }
        }
    }
}
