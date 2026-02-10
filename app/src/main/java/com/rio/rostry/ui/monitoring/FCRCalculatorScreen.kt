package com.rio.rostry.ui.monitoring

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.FCRRating
import com.rio.rostry.domain.service.HistoricalFCRPoint
import com.rio.rostry.domain.service.InsightPriority
import com.rio.rostry.domain.service.InsightType
import com.rio.rostry.ui.monitoring.vm.FCRCalculatorViewModel
import com.rio.rostry.ui.theme.*

/**
 * FCR Calculator Screen — Premium tier-aware.
 *
 * @param isPremium When true (Enthusiast), shows premium UI with gradient header,
 *                  breed comparison overlay, richer insights, and violet theme.
 *                  When false (Farmer), shows clean earthy version.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FCRCalculatorScreen(
    assetId: String,
    viewModel: FCRCalculatorViewModel = hiltViewModel(),
    isPremium: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val fcrAnalysis by viewModel.fcrAnalysis.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val historicalFCR by viewModel.historicalFCR.collectAsState()
    val benchmarkData by viewModel.benchmarkData.collectAsState()
    val availableBreeds by viewModel.availableBreeds.collectAsState()
    val selectedBreed by viewModel.selectedBreed.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var breedExpanded by remember { mutableStateOf(false) }
    
    // Theme-aware accent colors — uses MaterialTheme (Farmer=green, Enthusiast=violet)
    val accentColor = MaterialTheme.colorScheme.primary
    val accentContainer = MaterialTheme.colorScheme.primaryContainer
    val onAccentContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Premium gradient for Enthusiast header
    val headerGradient = if (isPremium) {
        Brush.horizontalGradient(
            colors = listOf(
                EnthusiastPrimary,
                EnthusiastSecondary,
                Color(0xFF7C4DFF)
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
            )
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("FCR Calculator")
                        if (isPremium) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EnthusiastElectric.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "PRO",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPremium) EnthusiastElectric else accentColor
                                )
                            }
                        }
                    }
                },
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
                text = "Analysis Period",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = onSurface
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
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor.copy(alpha = 0.15f),
                            selectedLabelColor = accentColor
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Loading State
            AnimatedVisibility(visible = isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accentColor)
                }
            }
            
            // Error State
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        
                        // ============ FCR Gauge Card (Premium-styled) ============
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (isPremium) Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
                                    else Modifier
                                ),
                            shape = RoundedCornerShape(if (isPremium) 16.dp else 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(headerGradient)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isPremium) "Feed Conversion Ratio — Pro Analysis" else "Feed Conversion Ratio",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // FCR Value Circle
                                    val ratingColor = when (analysis.rating) {
                                        FCRRating.EXCELLENT -> Color(0xFF4CAF50)
                                        FCRRating.GOOD -> Color(0xFF8BC34A)
                                        FCRRating.AVERAGE -> Color(0xFFFFC107)
                                        FCRRating.POOR -> Color(0xFFF44336)
                                        FCRRating.UNKNOWN -> Color.Gray
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(if (isPremium) 140.dp else 120.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.15f))
                                            .border(3.dp, ratingColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = String.format("%.2f", analysis.fcr),
                                                style = MaterialTheme.typography.headlineLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = if (isPremium) 36.sp else 30.sp
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
                                        shape = RoundedCornerShape(20.dp),
                                        color = ratingColor.copy(alpha = 0.3f)
                                    ) {
                                        Text(
                                            text = analysis.ratingDescription,
                                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                    }
                                    
                                    // Premium: Show breed standard comparison inline
                                    if (isPremium && selectedBreed.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "vs $selectedBreed standard",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = EnthusiastElectric.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // ============ Benchmark Selection ============
                        if (availableBreeds.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = surfaceColor)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Compare,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = if (isPremium) "Compare vs Breed Standard" else "Compare vs Benchmark",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = breedExpanded,
                                        onExpandedChange = { breedExpanded = !breedExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedBreed,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = breedExpanded,
                                            onDismissRequest = { breedExpanded = false }
                                        ) {
                                            availableBreeds.forEach { breed ->
                                                DropdownMenuItem(
                                                    text = { Text(breed) },
                                                    onClick = {
                                                        viewModel.selectBreed(breed)
                                                        breedExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // ============ Trend Chart ============
                        if (historicalFCR.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = surfaceColor)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ShowChart,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "FCR Trend vs Benchmark",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    FCRTrendChart(
                                        actual = historicalFCR,
                                        benchmark = benchmarkData,
                                        accentColor = accentColor,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(if (isPremium) 250.dp else 220.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LegendItem(color = accentColor, label = "Your Flock")
                                        LegendItem(color = Color.Gray, label = "$selectedBreed Standard", isDashed = true)
                                    }
                                }
                            }
                        }
                        
                        // ============ Metrics Grid ============
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PremiumMetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Feed",
                                value = String.format("%.1f kg", analysis.totalFeedKg),
                                icon = Icons.Default.Dining,
                                accentColor = accentColor,
                                isPremium = isPremium
                            )
                            PremiumMetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Weight Gain",
                                value = String.format("%.2f kg", analysis.weightGainKg),
                                icon = Icons.Default.TrendingUp,
                                accentColor = accentColor,
                                isPremium = isPremium
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PremiumMetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Initial Weight",
                                value = String.format("%.2f kg", analysis.initialWeightKg),
                                icon = Icons.Default.Scale,
                                accentColor = accentColor,
                                isPremium = isPremium
                            )
                            PremiumMetricCard(
                                modifier = Modifier.weight(1f),
                                title = "Final Weight",
                                value = String.format("%.2f kg", analysis.finalWeightKg),
                                icon = Icons.Default.Scale,
                                accentColor = accentColor,
                                isPremium = isPremium
                            )
                        }
                        
                        // ============ Premium: Cost Efficiency Section (Enthusiast Only) ============
                        if (isPremium) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = EnthusiastPrimary.copy(alpha = 0.08f)
                                )
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Diamond,
                                            contentDescription = null,
                                            tint = EnthusiastElectric,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = "Pro Insights",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = EnthusiastPrimary
                                        )
                                        Spacer(Modifier.weight(1f))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = EnthusiastElectric.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "ENTHUSIAST",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EnthusiastElectric,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Cost per kg gained
                                    val costPerKg = if (analysis.weightGainKg > 0 && analysis.totalFeedKg > 0) {
                                        // Approximate cost analysis using FCR
                                        analysis.fcr * 25.0 // ₹25/kg feed assumed
                                    } else null
                                    
                                    costPerKg?.let { cost ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Feed Cost per kg Gain",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = String.format("₹%.0f/kg", cost),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EnthusiastPrimary
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "Feed Efficiency",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = String.format("%.0f%%", (1.0 / analysis.fcr) * 100),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if ((1.0 / analysis.fcr) > 0.5) Emerald else Coral
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Breed optimization tip
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = EnthusiastElectric.copy(alpha = 0.08f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = EnthusiastElectric,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Track individual birds to identify top performers and optimize breeding pairs for better FCR in the next generation.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // ============ Smart Insights ============
                        val insights = viewModel.getInsights()
                        if (insights.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Smart Insights",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = onSurface
                                )
                            }
                            
                            insights.forEach { insight ->
                                val (bgColor, iconTint) = when (insight.type) {
                                    InsightType.FEED_EFFICIENCY -> {
                                        if (insight.priority == InsightPriority.HIGH || insight.priority == InsightPriority.CRITICAL)
                                            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
                                        else 
                                            accentContainer to accentColor
                                    }
                                    InsightType.COST_SAVINGS -> Color(0xFFE8F5E9) to Emerald
                                    InsightType.MORTALITY_ALERT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
                                    InsightType.GROWTH_TREND -> Color(0xFFFFF3E0) to FarmerEmphasis
                                    else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                                }
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        val icon = when (insight.type) {
                                            InsightType.FEED_EFFICIENCY -> Icons.Default.TrendingUp
                                            InsightType.COST_SAVINGS -> Icons.Default.AttachMoney
                                            InsightType.MORTALITY_ALERT -> Icons.Default.Warning
                                            InsightType.GROWTH_TREND -> Icons.Default.ShowChart
                                            else -> Icons.Default.Info
                                        }
                                        Icon(icon, contentDescription = null, tint = iconTint)
                                        Column {
                                            Text(
                                                text = insight.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = insight.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (insight.actionLabel != null) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = iconTint.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = insight.actionLabel!!,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = iconTint,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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
                            tint = accentColor.copy(alpha = 0.5f)
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

// ============ Premium Metric Card ============

@Composable
private fun PremiumMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    isPremium: Boolean = false
) {
    Card(
        modifier = modifier.then(
            if (isPremium) Modifier.shadow(2.dp, RoundedCornerShape(12.dp)) else Modifier
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, isDashed: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp, 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ============ Trend Chart (Theme-aware) ============

@Composable
fun FCRTrendChart(
    actual: List<HistoricalFCRPoint>,
    benchmark: List<HistoricalFCRPoint>,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    if (actual.isEmpty()) return
    
    val weeks = actual.map { it.week }
    val maxWeek = weeks.maxOrNull() ?: 0
    val minWeek = weeks.minOrNull() ?: 0
    val maxActual = actual.maxOf { it.fcr }
    val maxBench = if (benchmark.isNotEmpty()) benchmark.maxOf { it.fcr } else 0f
    val maxY = maxOf(maxActual, maxBench) * 1.1f
    
    Canvas(modifier = modifier.padding(16.dp)) {
        val width = size.width
        val height = size.height
        
        // Grid lines (subtle)
        for (i in 0..4) {
            val y = height * i / 4f
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }
        
        // Axes
        drawLine(Color.LightGray, Offset(0f, height), Offset(width, height), strokeWidth = 2f)
        drawLine(Color.LightGray, Offset(0f, 0f), Offset(0f, height), strokeWidth = 2f)
        
        fun getX(week: Int): Float {
            val range = (maxWeek - minWeek).coerceAtLeast(1)
            return ((week - minWeek).toFloat() / range) * width
        }
        
        fun getY(fcr: Float): Float = height - (fcr / maxY) * height
        
        // Benchmark line (dashed)
        if (benchmark.isNotEmpty()) {
            val validBenchmarks = benchmark.filter { it.week in minWeek..maxWeek }
            if (validBenchmarks.size > 1) {
                val path = Path()
                validBenchmarks.forEachIndexed { index, point ->
                    val x = getX(point.week)
                    val y = getY(point.fcr)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path, 
                    color = Color.Gray.copy(alpha = 0.5f), 
                    style = Stroke(
                        width = 3f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }
        
        // Actual data line (theme accent)
        val path = Path()
        actual.sortedBy { it.week }.forEachIndexed { index, point ->
            val x = getX(point.week)
            val y = getY(point.fcr)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            
            // Data points
            drawCircle(color = accentColor, radius = 5f, center = Offset(x, y))
            // White center for polish
            drawCircle(color = Color.White, radius = 2f, center = Offset(x, y))
        }
        
        drawPath(
            path = path,
            color = accentColor,
            style = Stroke(width = 4f)
        )
    }
}
