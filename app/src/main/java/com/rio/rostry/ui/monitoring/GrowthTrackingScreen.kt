package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.rio.rostry.ui.monitoring.vm.GrowthViewModel
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import com.rio.rostry.ui.components.GrowthChart
import com.rio.rostry.ui.components.BirdSelectionSheet
import com.rio.rostry.ui.components.BirdSelectionItem

@Composable
fun GrowthTrackingScreen(
    productId: String = "",
    onListProduct: (String) -> Unit = {}
) {
    val vm: GrowthViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    val pid = remember { mutableStateOf(productId) }
    val showSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val photoUri = remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            photoUri.value = it.toString()
        }
    }
    
    // Observe product ID changes and start Flow collection only once per productId
    androidx.compose.runtime.LaunchedEffect(pid.value) {
        if (pid.value.isNotBlank()) {
            vm.observe(pid.value)
        }
    }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text("Growth Tracking", style = MaterialTheme.typography.titleLarge)
        }

        item {
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (pid.value.isBlank()) {
                        OutlinedButton(
                            onClick = { showSheet.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Bird / Batch")
                        }
                    } else {
                        val selectedProduct = state.products.find { it.productId == pid.value }
                        if (selectedProduct != null) {
                            BirdSelectionItem(
                                product = selectedProduct,
                                onClick = { showSheet.value = true }
                            )
                        } else {
                            // Fallback if product not found in list (e.g. loading or error)
                            OutlinedButton(
                                onClick = { showSheet.value = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Selected ID: ${pid.value} (Change)")
                            }
                        }
                    }
                    
                    val weight = remember { mutableStateOf("") }
                    val height = remember { mutableStateOf("") }
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weight.value, 
                            onValueChange = { weight.value = it.filter { ch -> ch.isDigit() || ch == '.' } }, 
                            label = { Text("Weight (g)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = height.value, 
                            onValueChange = { height.value = it.filter { ch -> ch.isDigit() || ch == '.' } }, 
                            label = { Text("Height (cm)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }
                    
                    // Photo Picker
                    if (photoUri.value != null) {
                        Box(Modifier.fillMaxWidth().height(200.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(photoUri.value),
                                contentDescription = "Selected photo",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            IconButton(
                                onClick = { photoUri.value = null },
                                modifier = Modifier.align(androidx.compose.ui.Alignment.TopEnd).padding(8.dp)
                            ) {
                                Icon(Icons.Filled.Close, "Remove photo", tint = Color.White)
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch(arrayOf("image/*")) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Add Photo")
                        }
                    }

                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { 
                                if (pid.value.isNotBlank()) {
                                    vm.recordToday(
                                        pid.value, 
                                        weight.value.toDoubleOrNull(), 
                                        height.value.toDoubleOrNull(),
                                        photoUri.value
                                    )
                                    // Clear inputs after save? Maybe keep for reference.
                                    weight.value = ""
                                    height.value = ""
                                    photoUri.value = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = pid.value.isNotBlank() && (weight.value.isNotBlank() || height.value.isNotBlank())
                        ) { 
                            Text("Record Today") 
                        }
                        
                        OutlinedButton(
                            onClick = { 
                                vm.trackListOnMarketplaceClick(pid.value)
                                onListProduct(pid.value) 
                            },
                            enabled = pid.value.isNotBlank() && state.records.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Storefront, contentDescription = "List on marketplace", modifier = Modifier.padding(end = 4.dp))
                            Text("List")
                        }
                    }
                    
                    if (pid.value.isNotBlank() && state.records.isNotEmpty()) {
                        Text(
                            "Create a marketplace listing using your farm data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (pid.value.isBlank()) {
            item {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No product selected",
                    subtitle = "Enter a Product ID to start tracking growth",
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else if (state.records.isEmpty()) {
            item {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No growth records",
                    subtitle = "Record today's weight and height to begin",
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else {
            item {
                Text("History (${state.records.size})", style = MaterialTheme.typography.titleMedium)
            }

            if (state.records.size >= 2) {
                item {
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Growth Curve", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            GrowthChart(
                                records = state.records,
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            )
                        }
                    }
                }
            }
            
            items(items = state.records.sortedByDescending { it.createdAt }) { record ->
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column {
                            val dateStr = remember(record.createdAt) {
                                java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(record.createdAt))
                            }
                            Text(dateStr, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Week ${record.week}", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                            if (record.weightGrams != null) {
                                Text("${record.weightGrams} g", style = MaterialTheme.typography.bodyLarge)
                            }
                            if (record.heightCm != null) {
                                Text("${record.heightCm} cm", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSheet.value) {
        BirdSelectionSheet(
            products = state.products,
            onDismiss = { showSheet.value = false },
            onSelect = { product ->
                pid.value = product.productId
                showSheet.value = false
            }
        )
    }
}