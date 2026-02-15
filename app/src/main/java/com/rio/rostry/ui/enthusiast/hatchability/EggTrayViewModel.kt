package com.rio.rostry.ui.enthusiast.hatchability

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EggTrayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eggCollectionDao: EggCollectionDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao
) : ViewModel() {

    data class EggTrayUiState(
        val isLoading: Boolean = true,
        val collectionId: String = "",
        val totalEggs: Int = 0,
        val eggs: List<EggStatus> = emptyList(),
        val hatchedCount: Int = 0,
        val fertileCount: Int = 0,
        val infertileCount: Int = 0,
        val pendingCount: Int = 0,
        val error: String? = null
    )

    private val _state = MutableStateFlow(EggTrayUiState())
    val state: StateFlow<EggTrayUiState> = _state.asStateFlow()

    init {
        val collectionId = savedStateHandle.get<String>("collectionId") ?: ""
        if (collectionId.isNotBlank()) {
            loadEggData(collectionId)
        } else {
            _state.value = EggTrayUiState(isLoading = false, error = "No collection ID provided")
        }
    }

    private fun loadEggData(collectionId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, collectionId = collectionId)
            try {
                val collection = eggCollectionDao.getById(collectionId)
                if (collection == null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Egg collection not found"
                    )
                    return@launch
                }

                val totalEggs = collection.eggsCollected
                val good = collection.goodCount
                val damaged = collection.damagedCount
                val broken = collection.brokenCount

                // Check if eggs were set for hatching and have a linked batch
                var hatchedCount = 0
                if (collection.setForHatching && collection.linkedBatchId != null) {
                    hatchedCount = hatchingLogDao.countByBatchAndType(collection.linkedBatchId, "HATCHED")
                }

                // Build egg statuses from real data
                // Good eggs that hatched → HATCHED
                // Good eggs not yet hatched → FERTILE (if set for hatching) or PENDING
                // Damaged/broken → INFERTILE
                val eggs = mutableListOf<EggStatus>()
                var position = 1

                // Hatched eggs
                repeat(hatchedCount.coerceAtMost(good)) {
                    eggs.add(EggStatus(position = position++, status = EggState.HATCHED))
                }

                // Remaining good eggs: fertile if set for hatching, pending otherwise
                val remainingGood = (good - hatchedCount).coerceAtLeast(0)
                if (collection.setForHatching) {
                    repeat(remainingGood) {
                        eggs.add(EggStatus(position = position++, status = EggState.FERTILE))
                    }
                } else {
                    repeat(remainingGood) {
                        eggs.add(EggStatus(position = position++, status = EggState.PENDING))
                    }
                }

                // Damaged + broken eggs → infertile
                val infertile = damaged + broken
                repeat(infertile) {
                    eggs.add(EggStatus(position = position++, status = EggState.INFERTILE))
                }

                // Any unaccounted eggs (total - good - damaged - broken) → pending
                val unaccounted = (totalEggs - good - damaged - broken).coerceAtLeast(0)
                repeat(unaccounted) {
                    eggs.add(EggStatus(position = position++, status = EggState.PENDING))
                }

                _state.value = EggTrayUiState(
                    isLoading = false,
                    collectionId = collectionId,
                    totalEggs = totalEggs,
                    eggs = eggs,
                    hatchedCount = eggs.count { it.status == EggState.HATCHED },
                    fertileCount = eggs.count { it.status == EggState.FERTILE },
                    infertileCount = eggs.count { it.status == EggState.INFERTILE },
                    pendingCount = eggs.count { it.status == EggState.PENDING }
                )

                Timber.d("EggTray loaded: total=$totalEggs, good=$good, damaged=$damaged, broken=$broken, hatched=$hatchedCount")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load egg tray data")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load: ${e.message}"
                )
            }
        }
    }
}
