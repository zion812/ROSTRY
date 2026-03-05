package com.rio.rostry.ui.shared.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.media.MediaItem
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.ui.shared.gallery.components.GalleryFilterRow
import com.rio.rostry.ui.shared.gallery.components.GallerySelectionTopBar
import com.rio.rostry.ui.shared.gallery.components.MediaItemCard
import com.rio.rostry.ui.shared.gallery.state.GalleryEvent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToMediaViewer: (String) -> Unit,
    viewModel: MediaGalleryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    
    var editingCaptionFor by remember { mutableStateOf<MediaItem?>(null) }

    // Infinite scroll detection
    val isScrollToEnd by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - 4) // Load more when 4 items away from bottom
        }
    }

    LaunchedEffect(isScrollToEnd) {
        if (isScrollToEnd && !uiState.isLoading && uiState.pagination.hasMore) {
            viewModel.onEvent(GalleryEvent.LoadMore())
        }
    }

    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                GallerySelectionTopBar(
                    selectedCount = uiState.selectedItemIds.size,
                    onClearSelection = { viewModel.onEvent(GalleryEvent.ClearSelection) },
                    onDeleteSelected = {
                        viewModel.onEvent(GalleryEvent.DeleteSelected { /* Additional actions */ })
                    },
                    onShareSelected = {
                        viewModel.onEvent(GalleryEvent.ShareSelected { files ->
                            // TODO: Create Share Intent and launch
                        })
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Multimedia Gallery") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filters
            if (!uiState.isSelectionMode) {
                GalleryFilterRow(
                    filter = uiState.filter,
                    availableAgeGroups = uiState.availableAgeGroups,
                    availableSourceTypes = uiState.availableSourceTypes,
                    onFilterChanged = { viewModel.onEvent(GalleryEvent.ApplyFilter(it)) }
                )
            }

            // Media Grid
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState,
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.mediaItems,
                        key = { it.mediaId }
                    ) { mediaItem ->
                        MediaItemCard(
                            mediaItem = mediaItem,
                            isSelected = uiState.selectedItemIds.contains(mediaItem.mediaId),
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.onEvent(GalleryEvent.ToggleSelection(mediaItem.mediaId))
                                } else {
                                    onNavigateToMediaViewer(mediaItem.mediaId)
                                }
                            },
                            onLongClick = {
                                if (!uiState.isSelectionMode) {
                                    viewModel.onEvent(GalleryEvent.EnterSelectionMode)
                                }
                                viewModel.onEvent(GalleryEvent.ToggleSelection(mediaItem.mediaId))
                            },
                            onEditCaption = { editingCaptionFor = mediaItem }
                        )
                    }
                }

                // Empty state or loading
                if (uiState.mediaItems.isEmpty() && !uiState.isLoading) {
                    Text(
                        text = "No media found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (uiState.isLoading && uiState.mediaItems.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    // Caption Editing Dialog
    editingCaptionFor?.let { mediaItem ->
        var captionText by remember { mutableStateOf(mediaItem.caption ?: "") }
        var notesText by remember { mutableStateOf(mediaItem.notes ?: "") }
        
        AlertDialog(
            onDismissRequest = { editingCaptionFor = null },
            title = { Text("Edit Documentation") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        label = { Text("Caption") },
                        singleLine = true
                    )
                    TextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notes (optional)") },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(
                            GalleryEvent.UpdateCaption(
                                mediaId = mediaItem.mediaId,
                                caption = captionText.takeIf { it.isNotBlank() },
                                notes = notesText.takeIf { it.isNotBlank() }
                            )
                        )
                        editingCaptionFor = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCaptionFor = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
