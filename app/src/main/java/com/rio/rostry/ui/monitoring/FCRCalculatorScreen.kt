package com.rio.rostry.ui.monitoring

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.FCRRating
import com.rio.rostry.ui.monitoring.vm.FCRCalculatorViewModel
import com.rio.rostry.ui.monitoring.vm.InsightType

/**
 * FCR Calculator Screen for calculating and displaying Feed Conversion Ratio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FCRCalculatorScreen(
    assetId: String,
    viewModel: FCRCalculatorViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val fcrAnalysis by viewModel.fcrAnalysis.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FCR Calculator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period Selector
            Text(
                text = "Select Period",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.periodOptions.forEach { days ->
                    FilterChip(
                        selected = selectedPeriod == days,
                        onClick = { viewModel.selectPeriod(days) },
                        label = { 
                            Text(
                                when (days) {
                                    7 -> "7 Days"
                                    14 -> "2 Weeks"
                                    30 -> "1 Month"
                                    90 -> "3 Months"
                                    else -> "$days Days"
                                }
                            )
                        },
                        leadingIcon = if (selectedPeriod == days) {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Loading State
            AnimatedVisibility(visible = isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // Error State
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            // FCR Result
            AnimatedVisibility(
                visible = !isLoading && fcrAnalysis != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                fcrAnalysis?.let { analysis ->
                    // FCR Gauge Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Feed Conversion Ratio",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // FCR Value with colored background
                            val ratingColor = when (analysis.rating) {
                                FCRRating.EXCELLENT -> Color(0xFF4CAF50) // Green
                                FCRRating.GOOD -> Color(0xFF8BC34A) // Light Green
                                FCRRating.AVERAGE -> Color(0xFFFFC107) // Yellow
                                FCRRating.POOR -> Color(0xFFF44336) // Red
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(ratingColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = String.format("%.2f", analysis.fcr),
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "FCR",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Rating Badge
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = ratingColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = analysis.ratingDescription,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = ratingColor
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Feed",
                            value = String.format("%.1f kg", analysis.totalFeedKg),
                            icon = Icons.Default.Dining
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Weight Gain",
                            value = String.format("%.2f kg", analysis.weightGainKg),
                            icon = Icons.Default.TrendingUp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Initial Weight",
                            value = String.format("%.2f kg", analysis.initialWeightKg),
                            icon = Icons.Default.Scale
                        )
                        MetricCard(
                            modifier = Modifier.weight(1f),
                            title = "Final Weight",
                            value = String.format("%.2f kg", analysis.finalWeightKg),
                            icon = Icons.Default.Scale
                        )
                    }
                    
                    // Industry Comparison
                    if (analysis.percentageVsBenchmark != 0.0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val isBetter = analysis.percentageVsBenchmark > 0
                                Icon(
                                    if (isBetter) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isBetter) Color(0xFF4CAF50) else Color(0xFFF44336)
                                )
                                Column {
                                    Text(
                                        text = "vs Industry Benchmark (${analysis.industryBenchmark})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${if (isBetter) "+" else ""}${String.format("%.1f", analysis.percentageVsBenchmark)}%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBetter) Color(0xFF4CAF50) else Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Insights
                    val insights = viewModel.getInsights()
                    if (insights.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        insights.forEach { insight ->
                            val (bgColor, iconTint) = when (insight.type) {
                                InsightType.POSITIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
                                InsightType.WARNING -> Color(0xFFFFF3E0) to Color(0xFFFF9800)
                                InsightType.ALERT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
                                InsightType.INFO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = bgColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val icon = when (insight.type) {
                                        InsightType.POSITIVE -> Icons.Default.CheckCircle
                                        InsightType.WARNING -> Icons.Default.Warning
                                        InsightType.ALERT -> Icons.Default.Error
                                        InsightType.INFO -> Icons.Default.Info
                                    }
                                    Icon(icon, contentDescription = null, tint = iconTint)
                                    Column {
                                        Text(
                                            text = insight.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = insight.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            
            // Empty State
            if (!isLoading && fcrAnalysis == null && error == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select a period to calculate FCR",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
