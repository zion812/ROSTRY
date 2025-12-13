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
    private val productRepository: com.rio.rostry.data.repository.ProductRepository,
    private val taskRepository: com.rio.rostry.data.repository.monitoring.TaskRepository,
    private val mediaUploadManager: com.rio.rostry.utils.media.MediaUploadManager
) : ViewModel() {

    data class QuarantineStatusEntry(val status: String, val at: Long)

    data class UiState(
        val productId: String = "",
        val active: List<QuarantineRecordEntity> = emptyList(),
        val history: List<QuarantineRecordEntity> = emptyList(),
        val canDischarge: Map<String, Boolean> = emptyMap(),
        val nextUpdateDue: Map<String, Long> = emptyMap(),
        val isOverdue: Map<String, Boolean> = emptyMap(),
        val healthyUpdatesCount: Map<String, Int> = emptyMap(),
        val products: List<com.rio.rostry.data.database.entity.ProductEntity> = emptyList()
    )
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    // Error channel for user-facing messages
    private val _errors = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val errors: SharedFlow<String> = _errors.asSharedFlow()

    // Track enqueue mapping so we can update the right record on upload success
    private val pendingUploads = mutableMapOf<String, PendingUpload>() // remotePath -> info
    private data class PendingUpload(val quarantineId: String, val localPath: String, val at: Long)

    init {
        loadProducts()
        // Observe upload events to finalize attachment URLs
        viewModelScope.launch {
            mediaUploadManager.events.collect { ev ->
                when (ev) {
                    is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Success -> {
                        val info = pendingUploads.remove(ev.remotePath) ?: return@collect
                        // Update the corresponding record's attachments to set remote url
                        val current = _ui.value.active.find { it.quarantineId == info.quarantineId }
                            ?: _ui.value.history.find { it.quarantineId == info.quarantineId }
                        current?.let { rec ->
                            val updatedJson = upsertAttachment(rec.medicationScheduleJson, local = info.localPath, remote = ev.downloadUrl, at = info.at)
                            val updated = rec.copy(
                                medicationScheduleJson = updatedJson,
                                updatedAt = System.currentTimeMillis(),
                                dirty = true
                            )
                            repo.update(updated)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            productRepository.getProductsBySeller(farmerId).collect { res ->
                if (res is com.rio.rostry.utils.Resource.Success) {
                    _ui.update { it.copy(products = res.data ?: emptyList()) }
                }
            }
        }
    }

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
            // Server/VM-side guard: require two most recent statuses IMPROVING/STABLE and last update within 12h
            val history = parseStatusHistory(active).sortedBy { it.at }
            val lastTwo = history.takeLast(2)
            val twelveHours = java.util.concurrent.TimeUnit.HOURS.toMillis(12)
            val lastOk = lastTwo.isNotEmpty() && (now - (active.lastUpdatedAt)) <= twelveHours
            val statusesOk = lastTwo.size >= 2 && lastTwo.all { s ->
                val st = s.status.uppercase()
                st == "IMPROVING" || st == "STABLE"
            }
            if (!(lastOk && statusesOk)) {
                _errors.tryEmit("Cannot discharge: need 2 recent 'IMPROVING'/'STABLE' updates and a recent update within 12h")
                return@launch
            }
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
        status: String?,
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            val active = _ui.value.active.find { it.quarantineId == quarantineId } ?: return@launch
            
            val now = System.currentTimeMillis()
            // Enforce photo and notes when overdue
            val nextDue = active.lastUpdatedAt + java.util.concurrent.TimeUnit.HOURS.toMillis(12)
            val isOverdue = now > nextDue
            if (isOverdue) {
                if (photoUri.isNullOrBlank() || notes.isNullOrBlank()) {
                    // Reject and emit error message for UI
                    _errors.tryEmit("Overdue update requires both a photo and notes")
                    return@launch
                }
            }
            val newStatusHistoryJson = status?.let { appendStatusHistoryArrayJson(active.statusHistoryJson, it, now) } ?: active.statusHistoryJson
            
            // Adjust health score based on status
            val currentScore = active.healthScore
            val scoreChange = when (status?.uppercase()) {
                "IMPROVING" -> 5
                "STABLE" -> 0
                "WORSENING" -> -10
                else -> 0
            }
            val newHealthScore = (currentScore + scoreChange).coerceIn(0, 100)

            // Prepare upload if photo provided: resolve content URI to temp file and compress
            var tempLocalPath: String? = null
            if (!photoUri.isNullOrBlank()) {
                runCatching {
                    val input = appContext.contentResolver.openInputStream(android.net.Uri.parse(photoUri))
                        ?: throw IllegalStateException("Cannot open input stream")
                    val temp = java.io.File.createTempFile("qupdate_", ".jpg", appContext.cacheDir)
                    temp.outputStream().use { out -> input.copyTo(out) }
                    input.close()
                    val compressed = com.rio.rostry.utils.images.ImageCompressor.compressForUpload(appContext, temp, lowBandwidth = true)
                    tempLocalPath = compressed.absolutePath
                }.onFailure {
                    _errors.tryEmit("Failed to prepare photo for upload")
                }
            }
            // Offline-first attachment: write local placeholder (temp path if available) into attachments
            val newMedicationJson = when {
                tempLocalPath == null -> medication ?: active.medicationScheduleJson
                else -> {
                    val base = medication ?: active.medicationScheduleJson
                    upsertAttachment(base, local = tempLocalPath!!, remote = null, at = now)
                }
            }
            val updated = active.copy(
                vetNotes = notes,
                medicationScheduleJson = newMedicationJson,
                statusHistoryJson = newStatusHistoryJson,
                healthScore = newHealthScore,
                lastUpdatedAt = now,
                updatesCount = active.updatesCount + 1,
                updatedAt = now,
                dirty = true
            )
            repo.update(updated)

            // If photo provided, enqueue upload via outbox
            if (!tempLocalPath.isNullOrBlank()) {
                val remotePath = "quarantine/${quarantineId}/update_${now}.jpg"
                val ctxJson = com.google.gson.Gson().toJson(mapOf(
                    "type" to "quarantine_update",
                    "quarantineId" to quarantineId,
                    "at" to now
                ))
                pendingUploads[remotePath] = PendingUpload(quarantineId = quarantineId, localPath = tempLocalPath!!, at = now)
                mediaUploadManager.enqueueToOutbox(localPath = tempLocalPath!!, remotePath = remotePath, contextJson = ctxJson)
            }

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

    private fun upsertAttachment(existing: String?, local: String, remote: String?, at: Long): String {
        val gson = com.google.gson.Gson()
        val obj = when {
            existing.isNullOrBlank() -> com.google.gson.JsonObject()
            else -> runCatching { com.google.gson.JsonParser.parseString(existing).asJsonObject }.getOrElse { com.google.gson.JsonObject() }
        }
        val arr = if (obj.has("attachments")) obj.getAsJsonArray("attachments") else com.google.gson.JsonArray()
        // Try to find by local path
        val existingIdx = arr.indexOfFirst { el ->
            el.asJsonObject?.get("local")?.asString == local
        }
        val entry = com.google.gson.JsonObject().apply {
            addProperty("local", local)
            remote?.let { addProperty("remote", it) }
            addProperty("at", at)
        }
        if (existingIdx >= 0) {
            arr.set(existingIdx, entry)
        } else {
            arr.add(entry)
        }
        obj.add("attachments", arr)
        return gson.toJson(obj)
    }
}
