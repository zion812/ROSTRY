package com.rio.rostry.ui.enthusiast.breeding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.entity.EggCollectionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EggCollectionViewModel @Inject constructor(
    private val eggCollectionDao: EggCollectionDao,
    private val currentUserProvider: com.rio.rostry.session.CurrentUserProvider
) : ViewModel() {

    data class UiState(
        val pairId: String = "",
        val eggsCollected: String = "",
        val qualityGrade: String = "A",
        val weight: String = "",
        val notes: String = "",
        val saving: Boolean = false,
        val error: String? = null,
        val saved: Boolean = false
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun init(pairId: String) { _state.value = _state.value.copy(pairId = pairId) }

    fun setEggs(v: String) { _state.value = _state.value.copy(eggsCollected = v) }
    fun setGrade(v: String) { _state.value = _state.value.copy(qualityGrade = v) }
    fun setWeight(v: String) { _state.value = _state.value.copy(weight = v) }
    fun setNotes(v: String) { _state.value = _state.value.copy(notes = v) }

    fun save() {
        val farmerId = currentUserProvider.userIdOrNull()
        if (farmerId == null) {
            _state.value = _state.value.copy(error = "Not signed in")
            return
        }
        val eggs = _state.value.eggsCollected.toIntOrNull()
        if (eggs == null || eggs <= 0) {
            _state.value = _state.value.copy(error = "Enter a valid egg count")
            return
        }
        val now = System.currentTimeMillis()
        val entity = EggCollectionEntity(
            collectionId = UUID.randomUUID().toString(),
            pairId = _state.value.pairId,
            farmerId = farmerId,
            eggsCollected = eggs,
            collectedAt = now,
            qualityGrade = _state.value.qualityGrade,
            weight = _state.value.weight.toDoubleOrNull(),
            notes = _state.value.notes.ifBlank { null },
            updatedAt = now,
            dirty = true
        )
        _state.value = _state.value.copy(saving = true, error = null)
        viewModelScope.launch {
            try {
                eggCollectionDao.upsert(entity)
                _state.value = _state.value.copy(saving = false, saved = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(saving = false, error = e.message)
            }
        }
    }
}
