package com.rio.rostry.ui.shared.gallery.state

import com.rio.rostry.domain.model.media.MediaFilter
import com.rio.rostry.domain.model.media.MediaItem
import com.rio.rostry.domain.model.media.PaginationState

data class GalleryUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val filter: MediaFilter = MediaFilter(),
    val pagination: PaginationState = PaginationState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedItemIds: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    
    // Available filter options fetched from distinct tags
    val availableAgeGroups: List<String> = emptyList(),
    val availableSourceTypes: List<String> = emptyList()
)

sealed class GalleryEvent {
    data class LoadMore(val force: Boolean = false) : GalleryEvent()
    data class ApplyFilter(val filter: MediaFilter) : GalleryEvent()
    data class ToggleSelection(val mediaId: String) : GalleryEvent()
    object EnterSelectionMode : GalleryEvent()
    object ClearSelection : GalleryEvent()
    data class DeleteSelected(val onDeleted: () -> Unit) : GalleryEvent()
    data class ExportSelected(val destinationDir: java.io.File, val onExported: (java.io.File?) -> Unit) : GalleryEvent()
    data class ShareSelected(val onShareReady: (List<java.io.File>) -> Unit) : GalleryEvent()
    object Retry : GalleryEvent()
    data class SyncCache(val onComplete: () -> Unit) : GalleryEvent()
    data class UpdateCaption(val mediaId: String, val caption: String?, val notes: String?) : GalleryEvent()
}
