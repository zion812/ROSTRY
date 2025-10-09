package com.rio.rostry.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import com.rio.rostry.data.database.entity.ProductEntity

@Composable
fun ParentSelectorDialog(
    availableParents: List<ProductEntity>,
    gender: String,
    onDismiss: () -> Unit,
    onSelectParent: (ProductEntity) -> Unit,
    onScanQr: () -> Unit
) {
    val (query, setQuery) = remember { mutableStateOf("") }
    val filtered = remember(query, availableParents) {
        val q = query.trim().lowercase()
        if (q.isBlank()) availableParents else availableParents.filter {
            (it.name ?: "").lowercase().contains(q) ||
            (it.productId ?: "").lowercase().contains(q) ||
            (it.breed ?: "").lowercase().contains(q)
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select ${gender.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} Parent") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = setQuery,
                        label = { Text("Search by name or ID") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onScanQr) { Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan QR") }
                }
                if (filtered.isEmpty()) {
                    ElevatedCard { Text("No eligible parents found.", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium) }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filtered) { p ->
                            ElevatedCard(onClick = { onSelectParent(p) }) {
                                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                                    val title = (p.name ?: "").ifBlank { p.productId ?: "" }
                                    val meta = listOf(p.breed, p.color, p.stage).map { it ?: "" }.filter { it.isNotBlank() }.joinToString(" • ")
                                    Text(title, style = MaterialTheme.typography.titleMedium)
                                    Text(meta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
