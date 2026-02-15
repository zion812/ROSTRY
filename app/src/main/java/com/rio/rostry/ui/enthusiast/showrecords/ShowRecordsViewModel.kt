package com.rio.rostry.ui.enthusiast.showrecords

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ShowRecordEntity
import com.rio.rostry.data.repository.ShowRecordRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import org.json.JSONArray

data class ShowRecordsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val records: List<ShowRecordEntity> = emptyList(),
    val totalShows: Int = 0,
    val totalWins: Int = 0,
    val winRate: Int = 0,
    // Add Sheet State
    val isAddSheetOpen: Boolean = false,
    val isSaving: Boolean = false,
    // Gallery State
    val isGalleryOpen: Boolean = false,
    val galleryPhotos: List<String> = emptyList(),
    val selectedProtoIndex: Int = -1
)

@HiltViewModel
class ShowRecordsViewModel @Inject constructor(
    private val repository: ShowRecordRepository,
    private val currentUserProvider: com.rio.rostry.session.CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowRecordsUiState())
    val uiState: StateFlow<ShowRecordsUiState> = _uiState.asStateFlow()

    private var currentBirdId: String? = null

    // Helper for photos in Add Sheet
    private val _inputPhotos = MutableStateFlow<List<String>>(emptyList())
    val inputPhotos: StateFlow<List<String>> = _inputPhotos.asStateFlow()

    fun loadRecords(birdId: String) {
        currentBirdId = birdId
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repository.getRecordsForProduct(birdId).collect { records ->
                val sorted = records.sortedByDescending { it.eventDate }
                val stats = calculateStats(sorted)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    records = sorted,
                    totalShows = sorted.size,
                    totalWins = stats.first,
                    winRate = stats.second
                )
            }
        }
    }

    private fun calculateStats(records: List<ShowRecordEntity>): Pair<Int, Int> {
        if (records.isEmpty()) return Pair(0, 0)
        
        val wins = records.count { it.isWin || it.isPodium }
        val winRate = if (records.isNotEmpty()) ((wins.toDouble() / records.size) * 100).toInt() else 0
        
        return Pair(wins, winRate)
    }

    fun openAddSheet() {
        _inputPhotos.value = emptyList() // Reset photos
        _uiState.value = _uiState.value.copy(isAddSheetOpen = true)
    }

    fun closeAddSheet() {
        _uiState.value = _uiState.value.copy(isAddSheetOpen = false)
    }

    fun addPhoto(uri: Uri) {
        // In a real app, we'd copy/upload this Uri. For now, we persist the string uri.
        // Assuming local persistence or sync handles it.
        val current = _inputPhotos.value.toMutableList()
        current.add(uri.toString())
        _inputPhotos.value = current
    }
    
    fun removePhoto(index: Int) {
        val current = _inputPhotos.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _inputPhotos.value = current
        }
    }

    fun saveRecord(
        eventName: String,
        eventDate: Long,
        type: String, // SHOW, EXHIBITION, SPARRING
        result: String,
        placement: String?, // Can be parsed to Int or stored in notes if complex
        judge: String?,
        notes: String?
    ) {
        val birdId = currentBirdId ?: return
        if (eventName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Event name required")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true)

        val photosJson = com.google.gson.Gson().toJson(_inputPhotos.value)
        val userId = currentUserProvider.userIdOrNull() ?: "current_user"

        val record = ShowRecordEntity(
            recordId = UUID.randomUUID().toString(),
            productId = birdId,
            ownerId = userId,
            recordType = type,
            eventName = eventName,
            eventDate = eventDate,
            result = result,
            placement = placement?.toIntOrNull(),
            judgesNotes = judge, // Mapping judge name to notes field temporarily or handling separately
            notes = notes,
            photoUrls = photosJson,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            when (val result = repository.addRecord(record)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isAddSheetOpen = false,
                        errorMessage = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteRecord(recordId)
        }
    }


    fun openGallery(photos: List<String>, index: Int) {
        _uiState.value = _uiState.value.copy(
            isGalleryOpen = true,
            galleryPhotos = photos,
            selectedProtoIndex = index
        )
    }

    fun closeGallery() {
        _uiState.value = _uiState.value.copy(isGalleryOpen = false)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
