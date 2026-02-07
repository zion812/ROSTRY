package com.rio.rostry.ui.analytics.financial

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.repository.analytics.BreedROI
import com.rio.rostry.data.repository.analytics.MonthlyProfit
import com.rio.rostry.data.repository.analytics.ProfitabilitySummary

// Assuming Colors exist or use standard colors
val ColorProfit = Color(0xFF4CAF50)
val ColorLoss = Color(0xFFF44336)
val ColorRevenue = Color(0xFF2196F3)
val ColorExpense = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitabilityScreen(
    viewModel: ProfitabilityViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Time Range Label
                Text(
                    text = state.timeRangeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Net Profit Card
                state.summary?.let { summary ->
                    NetProfitCard(summary)
                }

                // Trends Chart
                if (state.trends.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Monthly Trends", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            FinancialTrendChart(
                                trends = state.trends,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Indicator(ColorRevenue, "Revenue")
                                Spacer(Modifier.width(16.dp))
                                Indicator(ColorExpense, "Expenses")
                            }
                        }
                    }
                }

                // Expense Breakdown
                if (state.expenseBreakdown.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Expense Breakdown", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(16.dp))
                            ExpensePieChart(
                                data = state.expenseBreakdown,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }

                // Top Breeds
                if (state.breedRoiList.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Breed Performance", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            state.breedRoiList.forEach { item ->
                                BreedRoiItem(item)
                                Divider(Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NetProfitCard(summary: ProfitabilitySummary) {
    val isProfitable = summary.netProfit >= 0
    val color = if (isProfitable) ColorProfit else ColorLoss
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Net Profit", style = MaterialTheme.typography.labelLarge)
            Text(
                text = "₹${String.format("%,.2f", summary.netProfit)}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Revenue", style = MaterialTheme.typography.bodySmall)
                    Text("₹${String.format("%,.0f", summary.totalRevenue)}", style = MaterialTheme.typography.titleMedium)
                }
                Column {
                    Text("Expenses", style = MaterialTheme.typography.bodySmall)
                    Text("₹${String.format("%,.0f", summary.totalExpenses)}", style = MaterialTheme.typography.titleMedium)
                }
                Column {
                    Text("Margin", style = MaterialTheme.typography.bodySmall)
                    Text("${String.format("%.1f", summary.profitMarginPercent)}%", style = MaterialTheme.typography.titleMedium, color = color)
                }
            }
        }
    }
}

@Composable
fun FinancialTrendChart(
    trends: List<MonthlyProfit>,
    modifier: Modifier = Modifier
) {
    val maxVal = trends.maxOfOrNull { maxOf(it.revenue, it.expenses) }?.toFloat() ?: 1f
    
    Canvas(modifier = modifier) {
        val barWidth = size.width / (trends.size * 3) // 2 bars + spacing
        val groupWidth = size.width / trends.size
        
        trends.forEachIndexed { index, item ->
            val xOffset = index * groupWidth + (groupWidth - barWidth * 2) / 2
            
            // Revenue Bar
            val revenueHeight = (item.revenue.toFloat() / maxVal) * size.height
            drawRect(
                color = ColorRevenue,
                topLeft = Offset(xOffset, size.height - revenueHeight),
                size = Size(barWidth, revenueHeight)
            )
            
            // Expense Bar
            val expenseHeight = (item.expenses.toFloat() / maxVal) * size.height
            drawRect(
                color = ColorExpense,
                topLeft = Offset(xOffset + barWidth, size.height - expenseHeight),
                size = Size(barWidth, expenseHeight)
            )
        }
    }
}

@Composable
fun ExpensePieChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    val colors = listOf(
        Color(0xFFE57373), Color(0xFFBA68C8), Color(0xFF64B5F6), 
        Color(0xFF4DB6AC), Color(0xFFFFD54F), Color(0xFFFF8A65)
    )
    
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.weight(1f).aspectRatio(1f)) {
            var startAngle = -90f
            data.entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value.toFloat() / total.toFloat()) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            data.entries.sortedByDescending { it.value }.forEachIndexed { index, entry ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(colors[index % colors.size], RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${entry.key}: ${(entry.value / total * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun Indicator(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BreedRoiItem(item: BreedROI) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.breedName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text("${item.assetCount} batches", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(
            "ROI: ${(item.roiPercent).toInt()}%", 
            style = MaterialTheme.typography.bodyMedium,
            color = if (item.roiPercent >= 0) ColorProfit else ColorLoss
        )
    }
}
