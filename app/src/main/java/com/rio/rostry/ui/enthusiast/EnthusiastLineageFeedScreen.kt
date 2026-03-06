package com.rio.rostry.ui.enthusiast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.WatchedLineageEntity
import com.rio.rostry.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastLineageFeedScreen(
    onNavigateBack: () -> Unit,
    viewModel: WatchedLineagesFeedViewModel = hiltViewModel()
) {
    val feedResource by viewModel.watchedLineages.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lineage discovery") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val res = feedResource) {
            is Resource.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Failed to load feed: ${res.message}")
                }
            }
            is Resource.Success -> {
                val items = res.data ?: emptyList()
                if (items.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Hub, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.height(16.dp))
                            Text("No watched lineages yet", style = MaterialTheme.typography.titleMedium)
                            Text("Scan a bird's QR code to follow its lineage updates!", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items) { watch ->
                            LineageUpdateCard(watch)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineageUpdateCard(watch: WatchedLineageEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mock Avatar or Icon
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(watch.birdName ?: "Unknown Bird", style = MaterialTheme.typography.titleMedium)
                    Text(watch.breed ?: "Unknown Breed", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.weight(1f))
                if (watch.isDiscoveryFeedEnabled) {
                    Icon(Icons.Default.Verified, contentDescription = "Verified Lineage", tint = Color(0xFF4CAF50))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Lineage Hash: ${watch.lineageHash.take(8)}...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            // Add more simulated update info here (e.g. "Recently won a match" or "Produced 5 offspring")
        }
    }
}
