package com.rio.rostry.ui.admin.biosecurity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.DiseaseZoneEntity
import com.rio.rostry.data.database.entity.ZoneSeverity
import com.rio.rostry.data.repository.BiosecurityRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BiosecurityManagementViewModel @Inject constructor(
    private val repository: BiosecurityRepository
) : ViewModel() {

    private val _zones = MutableStateFlow<List<DiseaseZoneEntity>>(emptyList())
    val zones = _zones.asStateFlow()
    
    // Add Zone Form State
    var newLat by mutableStateOf("")
    var newLng by mutableStateOf("")
    var newRadius by mutableStateOf("5000") // Default 5km
    var newReason by mutableStateOf("")
    var newSeverity by mutableStateOf(ZoneSeverity.WARNING)

    init {
        viewModelScope.launch {
            repository.getActiveZones().collect { result ->
                if (result is Resource.Success) {
                    _zones.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun addZone() {
        val lat = newLat.toDoubleOrNull()
        val lng = newLng.toDoubleOrNull()
        val radius = newRadius.toDoubleOrNull()
        
        if (lat != null && lng != null && radius != null && newReason.isNotBlank()) {
            val zone = DiseaseZoneEntity(
                zoneId = UUID.randomUUID().toString(),
                latitude = lat,
                longitude = lng,
                radiusMeters = radius,
                reason = newReason,
                severity = newSeverity,
                isActive = true
            )
            viewModelScope.launch {
                repository.addZone(zone)
                // Clear form
                newLat = ""
                newLng = ""
                newReason = ""
            }
        }
    }

    fun deactivateZone(zoneId: String) {
        viewModelScope.launch {
            repository.updateZoneStatus(zoneId, false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiosecurityManagementScreen(
    viewModel: BiosecurityManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val zones by viewModel.zones.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biosecurity Zones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Zone")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (zones.isEmpty()) {
                item {
                    Text(
                        "No active disease zones.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(zones) { zone ->
                DiseaseZoneCard(zone, onDelete = { viewModel.deactivateZone(zone.zoneId) })
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Red Zone") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.newLat,
                        onValueChange = { viewModel.newLat = it },
                        label = { Text("Latitude") }
                    )
                    OutlinedTextField(
                        value = viewModel.newLng,
                        onValueChange = { viewModel.newLng = it },
                        label = { Text("Longitude") }
                    )
                    OutlinedTextField(
                        value = viewModel.newRadius,
                        onValueChange = { viewModel.newRadius = it },
                        label = { Text("Radius (meters)") }
                    )
                    OutlinedTextField(
                        value = viewModel.newReason,
                        onValueChange = { viewModel.newReason = it },
                        label = { Text("Reason (e.g. flu)") }
                    )
                    // Simple Severity Selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Severity:")
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { viewModel.newSeverity = ZoneSeverity.WARNING }) {
                            Text("Warn", fontWeight = if(viewModel.newSeverity == ZoneSeverity.WARNING) FontWeight.Bold else FontWeight.Normal)
                        }
                        TextButton(onClick = { viewModel.newSeverity = ZoneSeverity.LOCKDOWN }) {
                            Text("Lockdown", color = MaterialTheme.colorScheme.error, fontWeight = if(viewModel.newSeverity == ZoneSeverity.LOCKDOWN) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addZone()
                    showAddDialog = false
                }) {
                    Text("Add Zone")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DiseaseZoneCard(zone: DiseaseZoneEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (zone.severity == ZoneSeverity.LOCKDOWN) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = zone.reason.ifBlank { "Unspecified Hazard" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onDelete) {
                    Text("Deactivate")
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("Radius: ${zone.radiusMeters}m")
            Text("Location: ${zone.latitude}, ${zone.longitude}")
            if (zone.severity == ZoneSeverity.LOCKDOWN) {
                 Text("⛔ LOCKDOWN ENFORCED", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        }
    }
}
