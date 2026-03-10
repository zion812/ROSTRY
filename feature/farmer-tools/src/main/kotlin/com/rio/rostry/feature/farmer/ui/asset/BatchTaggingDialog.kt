package com.rio.rostry.ui.farmer.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class TagGroupInput(
    val id: String = java.util.UUID.randomUUID().toString(),
    val count: String = "",
    val gender: String = "Unknown",
    val tagColor: String = "Red",
    val prefix: String = "",
    val startNumber: String = "1"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchTaggingDialog(
    totalQuantity: Int,
    existingGroups: List<TagGroupInput> = emptyList(), // For editing future feature
    onDismiss: () -> Unit,
    onConfirm: (List<TagGroupInput>) -> Unit
) {
    var groups by remember { mutableStateOf(if (existingGroups.isNotEmpty()) existingGroups else listOf(TagGroupInput())) }
    
    val currentAllocated = groups.sumOf { it.count.toIntOrNull() ?: 0 }
    val remaining = totalQuantity - currentAllocated
    val isValid = remaining == 0 && groups.all { 
        it.count.isNotBlank() && it.tagColor.isNotBlank() && it.startNumber.isNotBlank() 
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Separate & Tag Batch",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Phase 2: Identification",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Status Bar
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (remaining == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Batch Size: $totalQuantity",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = if (remaining == 0) "All Allocated" else "Remaining: $remaining",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Groups List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groups.forEachIndexed { index, group ->
                        TagGroupCard(
                            group = group,
                            onUpdate = { updated ->
                                val newGroups = groups.toMutableList()
                                newGroups[index] = updated
                                groups = newGroups
                            },
                            onRemove = if (groups.size > 1) { {
                                val newGroups = groups.toMutableList()
                                newGroups.removeAt(index)
                                groups = newGroups
                            } } else null
                        )
                    }
                    
                    if (remaining > 0) {
                        OutlinedButton(
                            onClick = {
                                groups = groups + TagGroupInput(
                                    count = remaining.toString(),
                                    // Make best guess for next config
                                    startNumber = "1"
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add Another Group (e.g. Females)")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(groups) },
                        enabled = isValid
                    ) {
                        Text("Save Tags")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagGroupCard(
    group: TagGroupInput,
    onUpdate: (TagGroupInput) -> Unit,
    onRemove: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Group Details",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                if (onRemove != null) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Count
                OutlinedTextField(
                    value = group.count,
                    onValueChange = { onUpdate(group.copy(count = it.filter { c -> c.isDigit() })) },
                    label = { Text("Count") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                // Gender
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = group.gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("Male", "Female", "Unknown").forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    onUpdate(group.copy(gender = selection))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tag Color
                val colors = listOf("Red", "Blue", "Green", "Yellow", "White", "Black")
                var expandedColor by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedColor,
                    onExpandedChange = { expandedColor = !expandedColor },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = group.tagColor,
                        onValueChange = { onUpdate(group.copy(tagColor = it)) },
                        label = { Text("Tag Color") },
                        modifier = Modifier.menuAnchor(),
                        singleLine = true
                    )
                     ExposedDropdownMenu(
                        expanded = expandedColor,
                        onDismissRequest = { expandedColor = false }
                    ) {
                        colors.forEach { selection ->
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(Modifier.size(12.dp).background(getColor(selection), androidx.compose.foundation.shape.CircleShape))
                                        Spacer(Modifier.width(8.dp))
                                        Text(selection) 
                                    }
                                },
                                onClick = {
                                    onUpdate(group.copy(tagColor = selection))
                                    expandedColor = false
                                }
                            )
                        }
                    }
                }
                
                // Prefix
                OutlinedTextField(
                    value = group.prefix,
                    onValueChange = { onUpdate(group.copy(prefix = it)) },
                    label = { Text("Prefix") },
                    modifier = Modifier.weight(0.7f),
                    placeholder = { Text("A") },
                    singleLine = true
                )
                
                // Start Num
                OutlinedTextField(
                    value = group.startNumber,
                    onValueChange = { onUpdate(group.copy(startNumber = it.filter { c -> c.isDigit() })) },
                    label = { Text("Start #") },
                    modifier = Modifier.weight(0.8f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            
            // Preview
            if (group.count.isNotEmpty() && group.startNumber.isNotEmpty()) {
                val start = group.startNumber.toIntOrNull() ?: 1
                val count = group.count.toIntOrNull() ?: 0
                val end = start + count - 1
                Text(
                    text = "Will generate tags: ${group.prefix}$start to ${group.prefix}$end",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
        }
    }
}

private fun getColor(name: String): Color {
    return when(name.lowercase()) {
        "red" -> Color(0xFFE57373)
        "blue" -> Color(0xFF64B5F6)
        "green" -> Color(0xFF81C784)
        "yellow" -> Color(0xFFFFF176)
        "black" -> Color.Black
        "white" -> Color.LightGray
        else -> Color.Gray
    }
}
