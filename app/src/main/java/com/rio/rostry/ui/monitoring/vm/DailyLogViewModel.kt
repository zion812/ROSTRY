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
    }

    fun updateWeight(grams: Double) {
        updateCurrent { it.copy(weightGrams = grams) }
    }

    fun updateFeed(kg: Double) {
        updateCurrent { it.copy(feedKg = kg) }
    }

    fun setActivityLevel(level: String) {
        updateCurrent { it.copy(activityLevel = level) }
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
    }

    fun save() {
        val current = _currentLog.value ?: return
        val uid = firebaseAuth.currentUser?.uid
        viewModelScope.launch { dailyLogRepository.upsert(current.copy(author = current.author ?: uid)) }
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
}
