package com.rio.rostry.ui.shared.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.model.media.MediaFilter
import com.rio.rostry.domain.model.media.PaginationState
import com.rio.rostry.domain.model.media.TagType
import com.rio.rostry.domain.repository.MediaGalleryRepository
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.ui.shared.gallery.state.GalleryEvent
import com.rio.rostry.ui.shared.gallery.state.GalleryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.rio.rostry.data.repository.UserRepository

@HiltViewModel
class MediaGalleryViewModel @Inject constructor(
    private val repository: MediaGalleryRepository,
    private val syncManager: SyncManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        // Trigger a background network sync on launch, without blocking the UI observing below
        viewModelScope.launch {
            try {
                syncManager.syncAll()
            } catch (e: Exception) {
                Timber.e(e, "Initial gallery sync failed")
            }
        }
        
        loadFilterOptions()
        observeMedia()
    }

    fun onEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.LoadMore -> loadMore(event.force)
            is GalleryEvent.ApplyFilter -> applyFilter(event.filter)
            is GalleryEvent.ToggleSelection -> toggleSelection(event.mediaId)
            GalleryEvent.EnterSelectionMode -> enterSelectionMode()
            GalleryEvent.ClearSelection -> clearSelection()
            is GalleryEvent.DeleteSelected -> deleteSelected(event.onDeleted)
            is GalleryEvent.ExportSelected -> exportSelected(event.destinationDir, event.onExported)
            is GalleryEvent.ShareSelected -> shareSelected(event.onShareReady)
            GalleryEvent.Retry -> observeMedia()
            is GalleryEvent.SyncCache -> syncCache(event.onComplete)
            is GalleryEvent.UpdateCaption -> updateCaption(event.mediaId, event.caption, event.notes)
        }
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            try {
                val ageGroups = repository.getDistinctTagValues(TagType.AGE_GROUP.name)
                val sourceTypes = repository.getDistinctTagValues(TagType.SOURCE_TYPE.name)
                
                _uiState.update { 
                    it.copy(
                        availableAgeGroups = ageGroups,
                        availableSourceTypes = sourceTypes
                    ) 
                }
            } catch (e: Exception) {
                // Ignore for now, keep default empty lists
            }
        }
    }

    private fun observeMedia() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Auto-scope by current user ownerId
            val currentUserResource = userRepository.getCurrentUser().firstOrNull()
            val currentUser = currentUserResource?.data
            val filteredState = _uiState.value.filter.copy(ownerId = currentUser?.userId)
            
            repository.observeMedia(filteredState, _uiState.value.pagination)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load media") }
                }
                .collectLatest { mediaList ->
                    _uiState.update { 
                        it.copy(
                            mediaItems = mediaList,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun loadMore(force: Boolean) {
        if (!force && (_uiState.value.isLoading || !_uiState.value.pagination.hasMore)) return
        
        val newPagination = _uiState.value.pagination.copy(
            currentPage = _uiState.value.pagination.currentPage + 1
        )
        _uiState.update { it.copy(pagination = newPagination) }
        observeMedia()
    }

    private fun applyFilter(filter: MediaFilter) {
        _uiState.update { 
            it.copy(
                filter = filter,
                pagination = PaginationState() // Reset pagination on filter change
            ) 
        }
        observeMedia()
    }

    private fun toggleSelection(mediaId: String) {
        _uiState.update { state ->
            val currentSelection = state.selectedItemIds.toMutableSet()
            if (currentSelection.contains(mediaId)) {
                currentSelection.remove(mediaId)
            } else {
                currentSelection.add(mediaId)
            }
            
            state.copy(
                selectedItemIds = currentSelection,
                isSelectionMode = currentSelection.isNotEmpty()
            )
        }
    }

    private fun enterSelectionMode() {
        _uiState.update { it.copy(isSelectionMode = true) }
    }

    private fun clearSelection() {
        _uiState.update { 
            it.copy(
                selectedItemIds = emptySet(),
                isSelectionMode = false
            )
        }
    }

    private fun deleteSelected(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val selectedIds = _uiState.value.selectedItemIds
            
            var hasError = false
            selectedIds.forEach { id ->
                val result = repository.deleteMediaItem(id)
                if (result.isFailure) hasError = true
            }
            
            clearSelection()
            _uiState.update { it.copy(isLoading = false, error = if (hasError) "Some items failed to delete" else null) }
            onDeleted()
        }
    }
    
    private fun syncCache(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.synchronizeCache()
            onComplete()
        }
    }

    private fun exportSelected(destinationDir: java.io.File, onExported: (java.io.File?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val selectedIds = _uiState.value.selectedItemIds.toList()
            val result = repository.exportMediaItems(selectedIds, destinationDir)
            clearSelection()
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = if (result.isFailure) "Export failed" else null
                ) 
            }
            onExported(result.getOrNull())
        }
    }

    private fun shareSelected(onShareReady: (List<java.io.File>) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val selectedIds = _uiState.value.selectedItemIds
            val filesToShare = mutableListOf<java.io.File>()
            
            selectedIds.forEach { id ->
                val item = repository.getMediaItem(id)
                if (item != null) {
                    val fileResult = repository.getMediaFile(item)
                    fileResult.getOrNull()?.let { filesToShare.add(it) }
                }
            }
            
            clearSelection()
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    error = if (filesToShare.isEmpty()) "No files available to share" else null
                ) 
            }
            if (filesToShare.isNotEmpty()) {
                onShareReady(filesToShare)
            }
        }
    }

    private fun updateCaption(mediaId: String, caption: String?, notes: String?) {
        viewModelScope.launch {
            val result = repository.updateCaption(mediaId, caption, notes)
            if (result.isFailure) {
                _uiState.update { it.copy(error = "Failed to update caption") }
            }
        }
    }
}
