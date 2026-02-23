package com.rio.rostry.ui.shared.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.media.MediaFilter
import com.rio.rostry.ui.shared.gallery.state.GalleryEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetMediaScreen(
    assetId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMediaViewer: (String) -> Unit,
    onNavigateToUpload: (String) -> Unit, // Asset ID
    viewModel: MediaGalleryViewModel = hiltViewModel()
) {
    // Initial load for a specific asset
    LaunchedEffect(assetId) {
        viewModel.onEvent(GalleryEvent.ApplyFilter(MediaFilter(assetId = assetId)))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Media") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToUpload(assetId) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Media")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Reusing the GalleryScreen concept here, we can embed it
            // or we just call the unified GalleryScreen minus the scaffold.
            // For now, let's keep it simple.
            GalleryScreen(
                onNavigateBack = onNavigateBack,
                onNavigateToMediaViewer = onNavigateToMediaViewer,
                viewModel = viewModel
            )
        }
    }
}
