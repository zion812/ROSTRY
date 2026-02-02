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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationQueueScreen(
    onNavigateBack: () -> Unit
) {
    val items = remember {
        mutableStateListOf(
            ModerationItem("MOD-001", "Product Listing", "Suspicious pricing", "Sharma Farms", Date(), ModerationStatus.PENDING),
            ModerationItem("MOD-002", "User Profile", "Inappropriate image", "RandomUser123", Date(System.currentTimeMillis() - 3600000), ModerationStatus.PENDING),
            ModerationItem("MOD-003", "Review", "Spam content detected", "TestUser", Date(System.currentTimeMillis() - 7200000), ModerationStatus.UNDER_REVIEW),
            ModerationItem("MOD-004", "Product Description", "Misleading claims", "Green Valley", Date(System.currentTimeMillis() - 86400000), ModerationStatus.RESOLVED)
        )
    }
    var filterStatus by remember { mutableStateOf<ModerationStatus?>(null) }

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
            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { FilterChip(filterStatus == null, { filterStatus = null }, { Text("All") }) }
                items(ModerationStatus.entries) { status ->
                    FilterChip(filterStatus == status, { filterStatus = status }, { Text(status.label) })
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filtered = if (filterStatus != null) items.filter { it.status == filterStatus } else items
                
                items(filtered) { item ->
                    ModerationCard(
                        item = item,
                        onApprove = { items[items.indexOf(item)] = item.copy(status = ModerationStatus.RESOLVED) },
                        onReject = { items.remove(item) }
                    )
                }
            }
        }
    }
}

private data class ModerationItem(val id: String, val type: String, val reason: String, val submittedBy: String, val reportedAt: Date, val status: ModerationStatus)

private enum class ModerationStatus(val label: String, val color: Color) {
    PENDING("Pending", Color(0xFFFF9800)),
    UNDER_REVIEW("Under Review", Color(0xFF2196F3)),
    RESOLVED("Resolved", Color(0xFF4CAF50))
}

@Composable
private fun ModerationCard(item: ModerationItem, onApprove: () -> Unit, onReject: () -> Unit) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.id, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = item.status.color.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
                        Text(item.status.label, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = item.status.color)
                    }
                }
                Text(dateFormatter.format(item.reportedAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text("Type: ${item.type}", style = MaterialTheme.typography.bodyMedium)
            Text("Reason: ${item.reason}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("By: ${item.submittedBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            if (item.status == ModerationStatus.PENDING) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onReject, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))) {
                        Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reject")
                    }
                    Button(onClick = onApprove) {
                        Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Approve")
                    }
                }
            }
        }
    }
}
