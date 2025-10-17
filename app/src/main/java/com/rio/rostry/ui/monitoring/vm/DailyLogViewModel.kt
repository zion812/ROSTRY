package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
import com.rio.rostry.ui.components.SyncState
import com.rio.rostry.ui.components.getSyncState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.net.Uri
import timber.log.Timber
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.utils.images.ImageCompressor
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.flow.collect
import java.io.File

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val mediaUploadManager: MediaUploadManager,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    data class DailyLogUiState(
        val products: List<ProductEntity> = emptyList(),
        val currentLog: DailyLogEntity? = null,
        val recentLogs: List<DailyLogEntity> = emptyList(),
        val hasToday: Boolean = false,
        val isLoading: Boolean = true,
        val error: String? = null,
        val currentLogSyncState: SyncState? = null
    )

    private val selectedProductId = MutableStateFlow<String?>(null)
    private val _currentLog = MutableStateFlow<DailyLogEntity?>(null)
    private val _synced = MutableStateFlow(false)
    val isSynced: StateFlow<Boolean> = _synced.asStateFlow()
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    // Chart data: last 30 days logs for selected product
    val chartData: StateFlow<List<DailyLogEntity>> = selectedProductId.flatMapLatest { pid ->
        if (pid.isNullOrBlank()) MutableStateFlow(emptyList()) else dailyLogRepository.observe(pid)
            .map { logs ->
                val since = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                logs.filter { it.logDate >= since }.sortedBy { it.logDate }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<DailyLogUiState> = selectedProductId.flatMapLatest { pid ->
        val farmerId = firebaseAuth.currentUser?.uid
        if (farmerId == null) {
            MutableStateFlow(DailyLogUiState(isLoading = false))
        } else {
            val productsFlow: kotlinx.coroutines.flow.Flow<List<ProductEntity>> =
                productRepository.getProductsBySeller(farmerId).map { res ->
                    when (res) {
                        is com.rio.rostry.utils.Resource.Success -> res.data ?: emptyList<ProductEntity>()
                        is com.rio.rostry.utils.Resource.Error -> emptyList<ProductEntity>()
                        is com.rio.rostry.utils.Resource.Loading -> emptyList<ProductEntity>()
                    }
                }
            val logsFlow: kotlinx.coroutines.flow.Flow<List<DailyLogEntity>> =
                pid?.let { dailyLogRepository.observe(it) } ?: MutableStateFlow(emptyList<DailyLogEntity>())
            combine(productsFlow, logsFlow, _currentLog) { products, logs, cur ->
                val syncState = cur?.let { getSyncState(it) }
                DailyLogUiState(
                    products = products,
                    currentLog = cur,
                    recentLogs = logs,
                    hasToday = logs.any { it.logDate == todayMidnight() },
                    isLoading = false,
                    currentLogSyncState = syncState
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyLogUiState())

    init {
        // Listen for media upload success and persist download URLs to current log
        viewModelScope.launch {
            mediaUploadManager.events.collect { ev ->
                when (ev) {
                    is MediaUploadManager.UploadEvent.Success -> {
                        // Heuristic: only handle daily log paths
                        if (ev.remotePath.startsWith("daily_logs/")) {
                            // Replace placeholder entry equal to remotePath with the final download URL
                            _currentLog.value?.let { cur ->
                                val listType = object : TypeToken<MutableList<String>>() {}.type
                                val currentList: MutableList<String> = when {
                                    cur.photoUrls.isNullOrBlank() -> mutableListOf()
                                    cur.photoUrls!!.trim().startsWith("[") -> runCatching { Gson().fromJson<MutableList<String>>(cur.photoUrls, listType) }.getOrElse { mutableListOf() }
                                    else -> mutableListOf()
                                }
                                val idx = currentList.indexOf(ev.remotePath)
                                if (idx >= 0) {
                                    currentList[idx] = ev.downloadUrl
                                    _currentLog.value = cur.copy(photoUrls = Gson().toJson(currentList))
                                    save()
                                } else {
                                    // Fallback: append if placeholder not found
                                    addPhoto(ev.downloadUrl)
                                    save()
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadForProduct(productId: String) {
        selectedProductId.value = productId
        val farmerId = firebaseAuth.currentUser?.uid ?: return
        _currentLog.value = newLog(productId, farmerId)
        _synced.value = false
    }

    fun updateWeight(grams: Double) {
        updateCurrent { it.copy(weightGrams = grams) }
        debounceAutoSave()
    }

    fun updateFeed(kg: Double) {
        updateCurrent { it.copy(feedKg = kg) }
        debounceAutoSave()
    }

    fun setActivityLevel(level: String) {
        updateCurrent { it.copy(activityLevel = level) }
        debounceAutoSave()
    }

    fun setNotes(notes: String) {
        updateCurrent { it.copy(notes = notes) }
        debounceAutoSave()
    }

    fun addPhoto(url: String) {
        updateCurrent { cur ->
            val listType = object : TypeToken<MutableList<String>>() {}.type
            val currentList: MutableList<String> = when {
                cur.photoUrls.isNullOrBlank() -> mutableListOf()
                cur.photoUrls!!.trim().startsWith("[") -> runCatching { Gson().fromJson<MutableList<String>>(cur.photoUrls, listType) }.getOrElse { _: Throwable -> mutableListOf<String>() }
                else -> mutableListOf() // legacy comma-separated; optional: split by comma
            }
            if (url.isNotBlank()) currentList.add(url)
            cur.copy(photoUrls = Gson().toJson(currentList))
        }
        debounceAutoSave()
    }

    fun save() {
        val current = _currentLog.value ?: return
        // Validation: require at least one meaningful field
        val hasContent = (current.weightGrams != null) || (current.feedKg != null) ||
                !current.medicationJson.isNullOrBlank() || !current.symptomsJson.isNullOrBlank() ||
                !current.activityLevel.isNullOrBlank() || !current.notes.isNullOrBlank() ||
                !current.photoUrls.isNullOrBlank()
        if (!hasContent) {
            Timber.w("DailyLog save blocked: no content")
            return
        }
        val uid = firebaseAuth.currentUser?.uid
        viewModelScope.launch {
            _saving.value = true
            dailyLogRepository.upsert(current.copy(author = current.author ?: uid, dirty = true, updatedAt = System.currentTimeMillis()))
            _synced.value = false
            _saving.value = false
        }
    }

    fun delete(logId: String) {
        viewModelScope.launch { dailyLogRepository.delete(logId) }
    }

    private fun updateCurrent(mod: (DailyLogEntity) -> DailyLogEntity) {
        val cur = _currentLog.value ?: return
        _currentLog.value = mod(cur)
    }

    fun toggleMedication(name: String) {
        updateCurrent { cur -> cur.copy(medicationJson = toggleInJsonList(cur.medicationJson, name)) }
    }

    fun toggleSymptom(name: String) {
        updateCurrent { cur -> cur.copy(symptomsJson = toggleInJsonList(cur.symptomsJson, name)) }
    }

    private fun toggleInJsonList(json: String?, item: String): String? {
        val listType = object : TypeToken<MutableList<String>>() {}.type
        val list: MutableList<String> = when {
            json.isNullOrBlank() -> mutableListOf()
            json.trim().startsWith("[") -> runCatching { Gson().fromJson<MutableList<String>>(json, listType) }.getOrElse { mutableListOf() }
            else -> mutableListOf()
        }
        if (list.contains(item)) list.remove(item) else list.add(item)
        return if (list.isEmpty()) null else Gson().toJson(list.distinct())
    }

    private fun newLog(productId: String?, farmerId: String): DailyLogEntity? {
        productId ?: return null
        val midnight = todayMidnight()
        val now = System.currentTimeMillis()
        val id = "dlog_${productId}_${midnight}"
        return DailyLogEntity(
            logId = id,
            productId = productId,
            farmerId = farmerId,
            logDate = midnight,
            deviceTimestamp = now,
            author = firebaseAuth.currentUser?.uid,
            dirty = true
        )
    }

    private fun todayMidnight(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // ----- Photo capture/upload integration -----
    fun requestPhotoPick() {
        // Intent handled by UI; no-op here
    }

    fun addCapturedPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                val productId = selectedProductId.value ?: return@launch
                val cur = _currentLog.value ?: return@launch
                val tempFile = copyUriToTempFile(uri)
                // Compress for upload (use low bandwidth profile if desired)
                val compressed = ImageCompressor.compressForUpload(appContext, tempFile, lowBandwidth = true)
                val remotePath = "daily_logs/${productId}/${cur.logId}/${System.currentTimeMillis()}.jpg"
                // Optimistically persist placeholder with remotePath token and save immediately
                run {
                    val listType = object : TypeToken<MutableList<String>>() {}.type
                    val currentList: MutableList<String> = when {
                        cur.photoUrls.isNullOrBlank() -> mutableListOf()
                        cur.photoUrls!!.trim().startsWith("[") -> runCatching { Gson().fromJson<MutableList<String>>(cur.photoUrls, listType) }.getOrElse { mutableListOf() }
                        else -> mutableListOf()
                    }
                    currentList.add(remotePath)
                    _currentLog.value = cur.copy(photoUrls = Gson().toJson(currentList))
                    save()
                }
                // Enqueue to outbox (handled by worker/background)
                mediaUploadManager.enqueueToOutbox(
                    localPath = compressed.absolutePath,
                    remotePath = remotePath,
                    contextJson = "{\"productId\":\"$productId\",\"logId\":\"${cur.logId}\"}"
                )
            } catch (t: Throwable) {
                Timber.e(t, "Photo upload failed")
            }
        }
    }

    // Helper to handle context if compressor requires it; placeholder
    private fun applyContextSafely(uri: Uri): Uri = uri

    private fun copyUriToTempFile(uri: Uri): File {
        val input = appContext.contentResolver.openInputStream(uri) ?: throw IllegalStateException("Cannot open input stream")
        val outFile = File.createTempFile("dlog_", ".jpg", appContext.cacheDir)
        outFile.outputStream().use { out -> input.copyTo(out) }
        input.close()
        return outFile
    }

    // ----- Debounced autosave -----
    private var debounceJob: Job? = null
    private fun debounceAutoSave() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            _saving.value = true
            delay(2000)
            save()
            _saving.value = false
        }
    }
}
