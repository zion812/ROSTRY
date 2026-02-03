package com.rio.rostry.ui.admin.moderation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.ContentType
import com.rio.rostry.data.repository.ModerationItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationQueueScreen(
    viewModel: ModerationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    // Simple filter state
    var filterType by remember { mutableStateOf<ContentType?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content Moderation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Filter by Type
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { 
                    FilterChip(
                        selected = filterType == null, 
                        onClick = { filterType = null }, 
                        label = { Text("All") }
                    ) 
                }
                items(ContentType.values()) { type ->
                    FilterChip(
                        selected = filterType == type, 
                        onClick = { filterType = type }, 
                        label = { Text(type.name) }
                    )
                }
            }

            when (val s = state) {
                is ModerationViewModel.State.Loading -> {
                     Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         CircularProgressIndicator()
                     }
                }
                is ModerationViewModel.State.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is ModerationViewModel.State.Content -> {
                    val filteredItems = if (filterType != null) {
                        s.items.filter { it.type == filterType }
                    } else {
                        s.items
                    }
                    
                    if (filteredItems.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No flagged content found.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredItems) { item ->
                                ModerationCard(
                                    item = item,
                                    onApprove = { viewModel.approve(item) },
                                    onReject = { viewModel.reject(item, "Rejected by Admin") } // Expand to show dialog for reason
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModerationCard(item: ModerationItem, onApprove: () -> Unit, onReject: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.id.take(8), fontWeight = FontWeight.Bold) // Show short ID
                    Spacer(Modifier.width(8.dp))
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small) {
                        Text(item.type.name, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
                Text(dateFormatter.format(Date(item.flaggedAt)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            Text(item.content, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            
            if (item.reason != null) {
                Spacer(Modifier.height(4.dp))
                Text("Flag Reason: ${item.reason}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReject, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))) {
                    Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reject (Hide)")
                }
                Button(onClick = onApprove) {
                    Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Approve (Clear Flag)")
                }
            }
        }
    }
}
