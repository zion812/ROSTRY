package com.rio.rostry.ui.enthusiast.hatchability

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.data.database.entity.EggCollectionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HatchabilityAnalysisState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val pairName: String = "",
    val totalEggs: Int = 0,
    val totalHatched: Int = 0,
    val hatchRate: Int = 0,
    val collections: List<CollectionSummary> = emptyList(),
    val insight: String? = null
)

data class CollectionSummary(
    val collectionId: String,
    val date: Long,
    val eggsCollected: Int,
    val linkedBatchId: String? = null,
    val setForHatching: Boolean = false
)

@HiltViewModel
class HatchabilityAnalysisViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val breedingPairDao: BreedingPairDao,
    private val eggCollectionDao: EggCollectionDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao
) : ViewModel() {

    private val pairId: String = savedStateHandle.get<String>("pairId") ?: ""

    private val _state = MutableStateFlow(HatchabilityAnalysisState())
    val state: StateFlow<HatchabilityAnalysisState> = _state.asStateFlow()

    init {
        if (pairId.isNotBlank()) {
            loadAnalysis()
        } else {
            _state.value = HatchabilityAnalysisState(
                isLoading = false,
                errorMessage = "No pair ID provided"
            )
        }
    }

    private fun loadAnalysis() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                // 1. Get pair info
                val pair = breedingPairDao.getById(pairId)
                val pairName = if (pair != null) {
                    "Pair ${pair.pairId.takeLast(4).uppercase()}"
                } else {
                    "Unknown Pair"
                }

                // 2. Get all egg collections for this pair
                val collections = eggCollectionDao.getCollectionsByPair(pairId)
                val totalEggs = eggCollectionDao.getTotalEggsByPair(pairId)

                // 3. Calculate hatched count from linked hatching batches
                val collectionIds = collections.map { it.collectionId }
                var totalHatched = 0
                var totalIncubated = 0

                if (collectionIds.isNotEmpty()) {
                    val batches = hatchingBatchDao.getBySourceCollectionIds(collectionIds)
                    batches.forEach { batch ->
                        val eggsSet = batch.eggsCount ?: 0
                        if (eggsSet > 0) {
                            totalIncubated += eggsSet
                            val hatchedInBatch = hatchingLogDao.countByBatchAndType(batch.batchId, "HATCHED")
                            totalHatched += hatchedInBatch
                        }
                    }
                }

                // 4. Calculate hatch rate
                val hatchRate = if (totalIncubated > 0) {
                    ((totalHatched.toDouble() / totalIncubated) * 100).toInt()
                } else if (totalEggs > 0) {
                    0 // Eggs exist but none set for hatching yet
                } else {
                    0
                }

                // 5. Build collection summaries
                val collectionSummaries = collections
                    .sortedByDescending { it.collectedAt }
                    .map { c ->
                        CollectionSummary(
                            collectionId = c.collectionId,
                            date = c.collectedAt,
                            eggsCollected = c.eggsCollected,
                            linkedBatchId = c.linkedBatchId,
                            setForHatching = c.setForHatching
                        )
                    }

                // 6. Generate insight
                val insight = when {
                    totalEggs == 0 -> "No egg collections recorded yet. Start logging collections to track hatchability."
                    hatchRate >= 80 -> "Excellent fertility! This pair shows above-average hatch rates. Consider increasing collection frequency."
                    hatchRate >= 60 -> "Good performance. Monitor environmental conditions to improve hatch rates further."
                    hatchRate > 0 -> "Hatch rate is below average. Check incubation conditions and pair compatibility."
                    totalIncubated == 0 && totalEggs > 0 -> "You have $totalEggs eggs collected but none set for hatching yet."
                    else -> null
                }

                _state.value = HatchabilityAnalysisState(
                    isLoading = false,
                    pairName = pairName,
                    totalEggs = totalEggs,
                    totalHatched = totalHatched,
                    hatchRate = hatchRate,
                    collections = collectionSummaries,
                    insight = insight
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load analysis"
                )
            }
        }
    }

    fun retry() {
        if (pairId.isNotBlank()) loadAnalysis()
    }
}
