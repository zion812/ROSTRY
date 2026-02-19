package com.rio.rostry.ui.enthusiast.digitaltwin

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.digitaltwin.lifecycle.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Colors (matching Dashboard)
private val GtBackground = Color(0xFF0D1117)
private val GtSurface = Color(0xFF161B22)
private val GtSurfaceLight = Color(0xFF21262D)
private val GtPrimary = Color(0xFF7C4DFF)
private val GtAccent = Color(0xFF00E5FF)
private val GtGreen = Color(0xFF3FB950)
private val GtOrange = Color(0xFFF0883E)
private val GtRed = Color(0xFFF85149)
private val GtGold = Color(0xFFFFD700)
private val GtTextPrimary = Color(0xFFE6EDF3)
private val GtTextSecondary = Color(0xFF8B949E)
private val GtChartIdeal = Color(0xFF448AFF)
private val GtChartActual = Color(0xFF3FB950)
private val GtChartMin = Color(0xFF8B949E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthTrackerScreen(
    onBack: () -> Unit,
    viewModel: GrowthTrackerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.savedMessage) {
        state.savedMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Growth Tracker", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        state.twin?.birdName?.let {
                            Text(it, fontSize = 12.sp, color = GtAccent)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openWeightDialog() }) {
                        Icon(Icons.Default.Add, "Record Weight", tint = GtGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GtBackground,
                    titleContentColor = GtTextPrimary,
                    navigationIconContentColor = GtTextPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openWeightDialog() },
                containerColor = GtGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Log Weight", fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = GtBackground
    ) { padding ->

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GtPrimary)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "Error", color = GtTextSecondary)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Current Weight Status
                    item { CurrentWeightCard(state) }

                    // 2. Growth Chart
                    item { GrowthChartCard(state) }

                    // 3. Prediction
                    state.prediction?.let { prediction ->
                        item { PredictionCard(prediction, state.ageProfile) }
                    }

                    // 4. Growth Timeline (Visual Evolution)
                    if (state.growthTimeline.isNotEmpty()) {
                        item { GrowthTimelineSection(state.growthTimeline) }
                    }

                    // 5. Weight History
                    if (state.actualWeights.isNotEmpty()) {
                        item {
                            Text(
                                "📋 Weight History",
                                color = GtTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        items(state.actualWeights.reversed()) { record ->
                            WeightHistoryCard(record)
                        }
                    }

                    // Bottom spacer for FAB
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }

        // Weight Input Dialog
        if (state.isWeightDialogOpen) {
            WeightInputDialog(
                weightInput = state.weightInputGrams,
                isSaving = state.isSavingWeight,
                onWeightChange = { viewModel.setWeightInput(it) },
                onSave = { viewModel.saveWeight() },
                onDismiss = { viewModel.closeWeightDialog() }
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// CURRENT WEIGHT STATUS CARD
// ═══════════════════════════════════════════════════════════════
@Composable
private fun CurrentWeightCard(state: GrowthTrackerState) {
    val eval = state.currentEvaluation

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            (if (eval != null) ratingColor(eval.rating).copy(alpha = 0.2f) else GtPrimary.copy(alpha = 0.2f)),
                            GtSurface
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            if (eval != null) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Current Weight", color = GtTextSecondary, fontSize = 12.sp)
                            Text(
                                "${eval.actualGrams}g",
                                color = GtTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ratingColor(eval.rating).copy(alpha = 0.15f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(eval.rating.emoji, fontSize = 28.sp)
                                Text(
                                    eval.rating.displayName,
                                    color = ratingColor(eval.rating),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MiniStat("Expected", "${eval.expected.idealGrams}g", GtAccent)
                        MiniStat("Min", "${eval.expected.minGrams}g", GtOrange)
                        MiniStat("Max", "${eval.expected.maxGrams}g", GtGreen)
                        MiniStat("Deviation", "${eval.deviationPercent}%",
                            if (eval.deviationPercent in -5..5) GtGreen else GtOrange
                        )
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("⚖️", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "No weight recorded yet",
                        color = GtTextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        "Tap + to log your bird's first weight",
                        color = GtTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = GtTextSecondary, fontSize = 10.sp)
    }
}

// ═══════════════════════════════════════════════════════════════
// GROWTH CHART — Canvas-drawn growth curve comparison
// ═══════════════════════════════════════════════════════════════
@Composable
private fun GrowthChartCard(state: GrowthTrackerState) {
    if (state.idealCurve.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GtSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📊 Growth Curve",
                color = GtTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                "Breed standard vs actual weight",
                color = GtTextSecondary,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))

            // Legend
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = GtChartIdeal, label = "Ideal")
                LegendItem(color = GtChartActual, label = "Actual")
                LegendItem(color = GtChartMin, label = "Min/Max Range")
            }

            Spacer(Modifier.height(12.dp))

            // Chart
            val idealCurve = state.idealCurve
            val actuals = state.actualWeights

            val maxDays = idealCurve.maxOfOrNull { it.ageDays } ?: 365
            val maxWeight = idealCurve.maxOfOrNull { it.maxGrams }?.let { it * 1.1f } ?: 5000f

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GtSurfaceLight)
                    .padding(8.dp)
            ) {
                val chartWidth = size.width
                val chartHeight = size.height

                fun dayToX(day: Int): Float = (day.toFloat() / maxDays) * chartWidth
                fun weightToY(grams: Int): Float = chartHeight - (grams.toFloat() / maxWeight) * chartHeight

                // Draw min/max range as filled area
                if (idealCurve.size > 1) {
                    val rangePath = Path()
                    rangePath.moveTo(dayToX(idealCurve.first().ageDays), weightToY(idealCurve.first().maxGrams))
                    idealCurve.forEach { rangePath.lineTo(dayToX(it.ageDays), weightToY(it.maxGrams)) }
                    idealCurve.reversed().forEach { rangePath.lineTo(dayToX(it.ageDays), weightToY(it.minGrams)) }
                    rangePath.close()
                    drawPath(rangePath, GtChartMin.copy(alpha = 0.1f))
                }

                // Draw ideal line
                idealCurve.zipWithNext().forEach { (a, b) ->
                    drawLine(
                        color = GtChartIdeal.copy(alpha = 0.7f),
                        start = Offset(dayToX(a.ageDays), weightToY(a.idealGrams)),
                        end = Offset(dayToX(b.ageDays), weightToY(b.idealGrams)),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }

                // Draw actual weight points and lines
                if (actuals.size > 1) {
                    actuals.zipWithNext().forEach { (a, b) ->
                        drawLine(
                            color = GtChartActual,
                            start = Offset(dayToX(a.ageDays), weightToY(a.weightGrams)),
                            end = Offset(dayToX(b.ageDays), weightToY(b.weightGrams)),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Draw actual weight dots
                actuals.forEach { w ->
                    drawCircle(
                        color = GtChartActual,
                        radius = 5f,
                        center = Offset(dayToX(w.ageDays), weightToY(w.weightGrams))
                    )
                    drawCircle(
                        color = GtSurfaceLight,
                        radius = 2.5f,
                        center = Offset(dayToX(w.ageDays), weightToY(w.weightGrams))
                    )
                }

                // Current age marker
                state.ageProfile?.let { profile ->
                    val x = dayToX(profile.ageInDays)
                    drawLine(
                        color = GtAccent.copy(alpha = 0.4f),
                        start = Offset(x, 0f),
                        end = Offset(x, chartHeight),
                        strokeWidth = 1.5f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(8f, 4f)
                        )
                    )
                }

                // X axis labels (approximate)
                val xLabels = listOf(0, maxDays / 4, maxDays / 2, maxDays * 3 / 4, maxDays)
                xLabels.forEach { day ->
                    drawLine(
                        color = GtTextSecondary.copy(alpha = 0.2f),
                        start = Offset(dayToX(day), chartHeight - 2),
                        end = Offset(dayToX(day), chartHeight),
                        strokeWidth = 1f
                    )
                }
            }

            // X axis labels
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0w", color = GtTextSecondary, fontSize = 9.sp)
                Text("${maxDays / 4 / 7}w", color = GtTextSecondary, fontSize = 9.sp)
                Text("${maxDays / 2 / 7}w", color = GtTextSecondary, fontSize = 9.sp)
                Text("${maxDays * 3 / 4 / 7}w", color = GtTextSecondary, fontSize = 9.sp)
                Text("${maxDays / 7}w", color = GtTextSecondary, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, color = GtTextSecondary, fontSize = 10.sp)
    }
}

// ═══════════════════════════════════════════════════════════════
// PREDICTION CARD
// ═══════════════════════════════════════════════════════════════
@Composable
private fun PredictionCard(prediction: WeightPrediction, ageProfile: AgeProfile?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GtSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🔮 Weight Prediction",
                color = GtTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "At ${prediction.targetAgeDays} days (~${prediction.targetAgeDays / 7} weeks)",
                        color = GtTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        "${prediction.predictedGrams}g",
                        color = GtAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GtAccent.copy(alpha = 0.12f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${(prediction.confidence * 100).toInt()}%",
                            color = GtAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text("confidence", color = GtAccent.copy(0.7f), fontSize = 10.sp)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Method: ${prediction.method}",
                color = GtTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// GROWTH TIMELINE — Visual Evolution
// ═══════════════════════════════════════════════════════════════
@Composable
private fun GrowthTimelineSection(timeline: List<GrowthSnapshot>) {
    Column {
        Text(
            "🧬 Visual Evolution",
            color = GtTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(timeline) { snapshot ->
                TimelineCard(snapshot)
            }
        }
    }
}

@Composable
private fun TimelineCard(snapshot: GrowthSnapshot) {
    Card(
        modifier = Modifier.width(130.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GtSurfaceLight)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stage emoji
            Text(snapshot.morphSummary.stageEmoji, fontSize = 32.sp)
            Spacer(Modifier.height(4.dp))

            // Stage name
            Text(
                snapshot.morphSummary.stageName,
                color = GtAccent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            // Age
            Text(
                formatAgeDays(snapshot.ageDays),
                color = GtTextSecondary,
                fontSize = 10.sp
            )

            // Maturity bar
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { snapshot.maturityIndex.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = GtPrimary,
                trackColor = GtSurface
            )
            Text(
                "${(snapshot.maturityIndex * 100).toInt()}%",
                color = GtTextSecondary,
                fontSize = 9.sp
            )

            // Key feature (first)
            snapshot.morphSummary.keyFeatures.firstOrNull()?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    it,
                    color = GtGold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// WEIGHT HISTORY CARD
// ═══════════════════════════════════════════════════════════════
@Composable
private fun WeightHistoryCard(record: WeightRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GtSurface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating indicator
            Surface(
                shape = CircleShape,
                color = ratingColorFromLabel(record.ratingLabel).copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(record.ratingEmoji, fontSize = 18.sp)
                }
            }
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${record.weightGrams}g",
                    color = GtTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "Day ${record.ageDays} • ${record.ratingLabel}",
                    color = GtTextSecondary,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (record.deviationPercent >= 0) "+" else ""}${record.deviationPercent}%",
                    color = if (record.deviationPercent in -5..5) GtGreen else GtOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(record.date)),
                    color = GtTextSecondary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// WEIGHT INPUT DIALOG
// ═══════════════════════════════════════════════════════════════
@Composable
private fun WeightInputDialog(
    weightInput: String,
    isSaving: Boolean,
    onWeightChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GtSurface,
        titleContentColor = GtTextPrimary,
        textContentColor = GtTextSecondary,
        title = { Text("⚖️ Record Weight", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Enter your bird's current weight in grams",
                    color = GtTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 5) {
                            onWeightChange(input)
                        }
                    },
                    label = { Text("Weight (grams)") },
                    placeholder = { Text("e.g. 2500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GtGreen,
                        unfocusedBorderColor = GtSurfaceLight,
                        focusedTextColor = GtTextPrimary,
                        unfocusedTextColor = GtTextPrimary,
                        cursorColor = GtGreen,
                        focusedLabelColor = GtGreen,
                        unfocusedLabelColor = GtTextSecondary
                    ),
                    suffix = { Text("g", color = GtTextSecondary) }
                )

                // Quick select buttons
                Spacer(Modifier.height(12.dp))
                Text("Quick select:", color = GtTextSecondary, fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(500, 1000, 1500, 2000, 3000).forEach { quickWeight ->
                        FilterChip(
                            selected = weightInput == quickWeight.toString(),
                            onClick = { onWeightChange(quickWeight.toString()) },
                            label = { Text("${quickWeight}g", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GtGreen.copy(alpha = 0.2f),
                                selectedLabelColor = GtGreen,
                                labelColor = GtTextSecondary
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = weightInput.toIntOrNull()?.let { it in 1..10000 } == true && !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = GtGreen)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GtTextSecondary)
            }
        }
    )
}

// ═══════════════════════════════════════════════════════════════
// UTILITIES
// ═══════════════════════════════════════════════════════════════

private fun ratingColor(rating: WeightRating): Color = when (rating) {
    WeightRating.EXCELLENT -> GtGold
    WeightRating.GOOD -> GtGreen
    WeightRating.FAIR -> GtOrange
    WeightRating.UNDERWEIGHT -> GtRed
    WeightRating.OVERWEIGHT -> GtOrange
}

private fun ratingColorFromLabel(label: String): Color = when (label.uppercase()) {
    "EXCELLENT" -> GtGold
    "GOOD" -> GtGreen
    "FAIR" -> GtOrange
    "UNDERWEIGHT" -> GtRed
    "OVERWEIGHT" -> GtOrange
    else -> GtTextSecondary
}

private fun formatAgeDays(days: Int): String = when {
    days < 7 -> "${days}d"
    days < 30 -> "${days / 7}w"
    days < 365 -> "${days / 30}mo"
    else -> "${days / 365}y ${(days % 365) / 30}mo"
}
