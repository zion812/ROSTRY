package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DailyLogEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
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

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    data class DailyLogUiState(
        val products: List<ProductEntity> = emptyList(),
        val currentLog: DailyLogEntity? = null,
        val recentLogs: List<DailyLogEntity> = emptyList(),
        val hasToday: Boolean = false,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val selectedProductId = MutableStateFlow<String?>(null)
    private val _currentLog = MutableStateFlow<DailyLogEntity?>(null)
    private val _synced = MutableStateFlow(false)
    val isSynced: StateFlow<Boolean> = _synced.asStateFlow()

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
                DailyLogUiState(
                    products = products,
                    currentLog = cur,
                    recentLogs = logs,
                    hasToday = logs.any { it.logDate == todayMidnight() },
                    isLoading = false
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyLogUiState())

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
            dailyLogRepository.upsert(current.copy(author = current.author ?: uid, dirty = true, updatedAt = System.currentTimeMillis()))
            _synced.value = false
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
        // UI should handle ActivityResult; provide separate API for VM to receive photo
    }

    fun addCapturedPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                // Simplified: just add URI directly
                addPhoto(uri.toString())
            } catch (t: Throwable) {
                Timber.e(t, "Photo upload failed")
            }
        }
    }

    // Helper to handle context if compressor requires it; placeholder
    private fun applyContextSafely(uri: Uri): Uri = uri

    // ----- Debounced autosave -----
    private var debounceJob: Job? = null
    private fun debounceAutoSave() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(2000)
            save()
        }
    }
}
