package com.rio.rostry.ui.monitoring

import androidx.compose.ui.text.font.FontWeight
import com.rio.rostry.ui.theme.*
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.IconButton
import com.rio.rostry.ui.components.GrowthChart
import com.rio.rostry.ui.components.BirdSelectionSheet
import com.rio.rostry.ui.components.BirdSelectionItem

@Composable
fun GrowthTrackingScreen(
    productId: String = "",
    onListProduct: (String) -> Unit = {},
    isPremium: Boolean = false
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
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Growth Tracking", style = MaterialTheme.typography.titleLarge)
                if (isPremium) {
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EnthusiastElectric.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "PRO",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EnthusiastElectric
                        )
                    }
                }
            }
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
                
                // Growth Stats Summary Card
                item {
                    val sortedRecords = state.records.filter { it.weightGrams != null }.sortedBy { it.week }
                    if (sortedRecords.size >= 2) {
                        val latestWeight = sortedRecords.last().weightGrams!!
                        val firstWeight = sortedRecords.first().weightGrams!!
                        val daysBetween = ((sortedRecords.last().createdAt - sortedRecords.first().createdAt) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
                        val avgDailyGain = (latestWeight - firstWeight) / daysBetween.toDouble()
                        
                        // Simple linear prediction for next 7 days
                        val predicted7Day = latestWeight + (avgDailyGain * 7).toInt()
                        
                        // Breed benchmark (average for common breeds - can be extended)
                        val breedBenchmark = mapOf(
                            "Aseel" to 2500.0,
                            "Shamo" to 3000.0,
                            "Kadaknath" to 1800.0,
                            "Default" to 2200.0
                        )
                        val selectedProduct = state.products.find { it.productId == pid.value }
                        val breed = selectedProduct?.breed ?: "Default"
                        val benchmark = breedBenchmark[breed] ?: breedBenchmark["Default"]!!
                        val progressVsBenchmark = (latestWeight / benchmark * 100).toInt()
                        
                        ElevatedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Growth Analysis", style = MaterialTheme.typography.titleMedium)
                                
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    GrowthStatItem(
                                        label = "Current",
                                        value = "${latestWeight}g",
                                        icon = Icons.Filled.Info
                                    )
                                    GrowthStatItem(
                                        label = "Daily Gain",
                                        value = "${String.format("%.1f", avgDailyGain)}g/day",
                                        icon = Icons.Filled.ArrowUpward
                                    )
                                    GrowthStatItem(
                                        label = "7-Day Predict",
                                        value = "${predicted7Day}g",
                                        icon = Icons.Filled.DateRange
                                    )
                                }
                                
                                // Breed Benchmark Progress
                                Column {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("vs $breed Benchmark", style = MaterialTheme.typography.labelMedium)
                                        Text("$progressVsBenchmark%", style = MaterialTheme.typography.labelMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    androidx.compose.material3.LinearProgressIndicator(
                                        progress = { (progressVsBenchmark / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = if (progressVsBenchmark >= 80) Color(0xFF4CAF50) else if (progressVsBenchmark >= 50) Color(0xFFFF9800) else Color(0xFFD32F2F),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    Text(
                                        "Target: ${benchmark.toInt()}g (mature $breed)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                // Premium: Genetic potential and breeding optimization
                                if (isPremium) {
                                    Spacer(Modifier.height(8.dp))
                                    androidx.compose.material3.Divider()
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Info,
                                            contentDescription = null,
                                            tint = EnthusiastElectric,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Track consistently to build a growth profile for breeding pair selection.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = EnthusiastPrimary
                                        )
                                    }
                                }
                            }
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

@Composable
private fun GrowthStatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}