package com.rio.rostry.ui.sync

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.sync.SyncConflict
import com.rio.rostry.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SyncConflictScreen: Shows list of sync conflicts and allows user to resolve them.
 * 
 * Options per conflict:
 * - Keep Local: Use local version, overwrite remote
 * - Keep Remote: Use remote version, overwrite local
 * - Merge: Show diff and let user pick fields (future enhancement)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncConflictScreen(
    viewModel: SyncConflictViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAllResolved: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    LaunchedEffect(state.allResolved) {
        if (state.allResolved) {
            onAllResolved()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sync Conflicts (${state.conflicts.size})") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.conflicts.isNotEmpty()) {
                        TextButton(onClick = { viewModel.resolveAllLocal() }) {
                            Text("Keep All Local")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Detecting conflicts...")
                }
            }
        } else if (state.conflicts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("All data synced!", style = MaterialTheme.typography.titleLarge)
                    Text("No conflicts detected", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "${state.conflicts.size} conflicts found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Choose which version to keep for each conflict",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                
                items(state.conflicts, key = { "${it.entityType}_${it.entityId}" }) { conflict ->
                    ConflictCard(
                        conflict = conflict,
                        onKeepLocal = { viewModel.resolveConflict(conflict, useLocal = true) },
                        onKeepRemote = { viewModel.resolveConflict(conflict, useLocal = false) },
                        isResolving = state.resolvingId == conflict.entityId
                    )
                }
                
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun ConflictCard(
    conflict: SyncConflict,
    onKeepLocal: () -> Unit,
    onKeepRemote: () -> Unit,
    isResolving: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (conflict.entityType) {
                            "FarmAsset" -> Icons.Default.Pets
                            "MarketListing" -> Icons.Default.Store
                            "Order" -> Icons.Default.ShoppingCart
                            else -> Icons.Default.Article
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        conflict.entityType,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    conflict.entityId.take(8) + "...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Conflicting fields
            Text(
                "Conflicting fields:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                conflict.conflictingFields.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Timestamps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Local", style = MaterialTheme.typography.labelSmall)
                    Text(
                        formatTimestamp(conflict.localTimestamp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Remote", style = MaterialTheme.typography.labelSmall)
                    Text(
                        formatTimestamp(conflict.remoteTimestamp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Action buttons
            if (isResolving) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onKeepLocal,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Keep Local")
                    }
                    Button(
                        onClick = onKeepRemote,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Keep Remote")
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> "${diff / 86_400_000}d ago"
    }
}

// ViewModel
@HiltViewModel
class SyncConflictViewModel @Inject constructor(
    private val syncManager: SyncManager
) : ViewModel() {
    
    data class UiState(
        val conflicts: List<SyncConflict> = emptyList(),
        val isLoading: Boolean = true,
        val resolvingId: String? = null,
        val allResolved: Boolean = false,
        val error: String? = null
    )
    
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    init {
        loadConflicts()
    }
    
    private fun loadConflicts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val conflicts = syncManager.detectConflicts()
                _uiState.update { 
                    it.copy(
                        conflicts = conflicts, 
                        isLoading = false,
                        allResolved = conflicts.isEmpty()
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message 
                    ) 
                }
            }
        }
    }
    
    fun resolveConflict(conflict: SyncConflict, useLocal: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(resolvingId = conflict.entityId) }
            try {
                syncManager.resolveConflict(conflict.entityId, conflict.entityType, useLocal)
                _uiState.update { state ->
                    val remaining = state.conflicts.filter { it.entityId != conflict.entityId }
                    state.copy(
                        conflicts = remaining,
                        resolvingId = null,
                        allResolved = remaining.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(resolvingId = null, error = e.message) }
            }
        }
    }
    
    fun resolveAllLocal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                _uiState.value.conflicts.forEach { conflict ->
                    syncManager.resolveConflict(conflict.entityId, conflict.entityType, useLocal = true)
                }
                _uiState.update { it.copy(conflicts = emptyList(), isLoading = false, allResolved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
