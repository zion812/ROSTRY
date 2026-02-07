package com.rio.rostry.ui.farmer.documentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rio.rostry.data.repository.AssetSummary
import com.rio.rostry.data.repository.FarmDocumentation
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDocumentScreen(
    onBack: () -> Unit,
    viewModel: FarmDocumentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show success message when exported
    LaunchedEffect(uiState.exportedFileName) {
        uiState.exportedFileName?.let { fileName ->
            snackbarHostState.showSnackbar("Saved to Downloads: $fileName")
            viewModel.clearExportState()
        }
    }
    
    // Show error message
    LaunchedEffect(uiState.exportError) {
        uiState.exportError?.let { error ->
            snackbarHostState.showSnackbar("Export failed: $error")
            viewModel.clearExportState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Documentation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.exportToPdf() },
                        enabled = uiState.documentation != null && !uiState.isExporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.documentation == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Agriculture, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Spacer(Modifier.height(16.dp))
                    Text("No farm data available", style = MaterialTheme.typography.titleMedium)
                    Text("Add some assets to generate documentation", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Farm Overview Card
                item {
                    FarmOverviewCard(uiState.documentation!!)
                }
                
                // Asset Type Breakdown
                item {
                    Text(
                        text = "Assets by Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    AssetTypeBreakdownCard(uiState.documentation!!)
                }
                
                // Top Assets
                if (uiState.assetSummaries.isNotEmpty()) {
                    item {
                        Text(
                            text = "Asset Performance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(uiState.assetSummaries.take(5)) { summary ->
                        AssetSummaryItem(summary)
                    }
                    
                    if (uiState.assetSummaries.size > 5) {
                        item {
                            Text(
                                text = "... and ${uiState.assetSummaries.size - 5} more assets",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Export Button
                item {
                    Button(
                        onClick = { viewModel.exportToPdf() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isExporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(if (uiState.isExporting) "Generating Farm Report..." else "Export Farm Report as PDF")
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmOverviewCard(doc: FarmDocumentation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = doc.farmName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Pets,
                    value = doc.assets.size.toString(),
                    label = "Assets",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.Restaurant,
                    value = "${String.format("%.1f", doc.totalFeedKg)} kg",
                    label = "Feed",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.CurrencyRupee,
                    value = "₹${String.format("%.0f", doc.totalExpensesInr)}",
                    label = "Expenses",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                StatItem(
                    icon = Icons.Default.Vaccines,
                    value = doc.totalVaccinations.toString(),
                    label = "Vaccinations",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun AssetTypeBreakdownCard(doc: FarmDocumentation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            doc.assetsByType.forEach { (type, assets) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = type, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${assets.size} (${assets.sumOf { it.quantity.toInt() }} qty)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetSummaryItem(summary: AssetSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.asset.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${summary.asset.assetType} • ${summary.asset.quantity.toInt()} qty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${summary.vaccinationCount}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("Vax", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("₹${String.format("%.0f", summary.expensesInr)}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("Spent", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = color
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
    }
}
