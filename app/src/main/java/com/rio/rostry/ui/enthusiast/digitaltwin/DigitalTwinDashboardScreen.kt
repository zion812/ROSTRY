package com.rio.rostry.ui.enthusiast.digitaltwin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.BirdEventEntity
import com.rio.rostry.data.database.entity.DigitalTwinEntity
import com.rio.rostry.domain.digitaltwin.AseelLifecycleEngine
import com.rio.rostry.domain.digitaltwin.lifecycle.MorphSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ═══════════════════════════════════════════════════════════════
// Digital Twin color palette — premium purple/violet tones
// ═══════════════════════════════════════════════════════════════
private val TwinPrimary = Color(0xFF7C4DFF)
private val TwinSecondary = Color(0xFF448AFF)
private val TwinAccent = Color(0xFF00E5FF)
private val TwinBackground = Color(0xFF0D1117)
private val TwinSurface = Color(0xFF161B22)
private val TwinSurfaceLight = Color(0xFF21262D)
private val TwinTextPrimary = Color(0xFFE6EDF3)
private val TwinTextSecondary = Color(0xFF8B949E)
private val TwinGold = Color(0xFFFFD700)
private val TwinGreen = Color(0xFF3FB950)
private val TwinOrange = Color(0xFFF0883E)
private val TwinRed = Color(0xFFF85149)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalTwinDashboardScreen(
    onBack: () -> Unit,
    onNavigateToGrowthTracker: (String) -> Unit,
    onNavigateToBirdStudio: (String) -> Unit,
    onNavigateToGrading: (String) -> Unit,
    viewModel: DigitalTwinDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            state.twin?.birdName ?: "Digital Twin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        state.twin?.registryId?.let {
                            Text(
                                it,
                                fontSize = 11.sp,
                                color = TwinAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TwinBackground,
                    titleContentColor = TwinTextPrimary,
                    navigationIconContentColor = TwinTextPrimary,
                    actionIconContentColor = TwinAccent
                )
            )
        },
        containerColor = TwinBackground
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TwinPrimary)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, null, tint = TwinOrange, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(state.error ?: "Unknown error", color = TwinTextSecondary)
                    }
                }
            }
            else -> {
                val twin = state.twin ?: return@Scaffold

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Hero Card — Identity + Stage
                    item { HeroCard(twin, state.ageProfile, state.morphSummary) }

                    // 2. Score Radar
                    item { ScoreRadarCard(twin) }

                    // 3. Lifecycle Progress
                    item { LifecycleProgressCard(twin, state.transitionInfo) }

                    // 4. Quick Actions
                    item { QuickActionsRow(
                        twin = twin,
                        onGrowthTracker = { onNavigateToGrowthTracker(twin.birdId) },
                        onBirdStudio = { onNavigateToBirdStudio(twin.birdId) },
                        onMorphologyGrading = { onNavigateToGrading(twin.birdId) },
                        canMorphology = state.canMeasureMorphology,
                        canPerformance = state.canMeasurePerformance,
                        isBreedingEligible = state.isBreedingEligible,
                        isShowEligible = state.isShowEligible
                    ) }

                    // 5. Weight & Growth Summary
                    state.weightAnalytics?.let { analytics ->
                        item { WeightSummaryCard(analytics) }
                    }

                    // 6. Morphology Summary
                    state.morphSummary?.let { morph ->
                        item { MorphologySummaryCard(morph) }
                    }

                    // 7. Recent Events
                    if (state.recentEvents.isNotEmpty()) {
                        item {
                            Text(
                                "📋 Event Timeline",
                                color = TwinTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        items(state.recentEvents.take(10)) { event ->
                            EventCard(event)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// HERO CARD — Bird Identity + Stage + Age
// ═══════════════════════════════════════════════════════════════
@Composable
private fun HeroCard(
    twin: DigitalTwinEntity,
    ageProfile: com.rio.rostry.domain.digitaltwin.lifecycle.AgeProfile?,
    morphSummary: MorphSummary?
) {
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
                            TwinPrimary.copy(alpha = 0.3f),
                            TwinSecondary.copy(alpha = 0.2f),
                            TwinSurface
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                // Top row: Name + Gender badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Stage emoji
                    Text(
                        morphSummary?.stageEmoji ?: "🐓",
                        fontSize = 40.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            twin.birdName ?: "Unnamed",
                            color = TwinTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            "${twin.baseBreed} • ${twin.strainType ?: "Unknown Strain"}",
                            color = TwinTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    // Gender badge
                    Surface(
                        shape = CircleShape,
                        color = if (twin.gender?.equals("MALE", true) == true)
                            TwinSecondary.copy(alpha = 0.2f) else TwinPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            if (twin.gender?.equals("MALE", true) == true) "♂" else "♀",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 20.sp,
                            color = if (twin.gender?.equals("MALE", true) == true) TwinSecondary else TwinPrimary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Info chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        label = "Age",
                        value = ageProfile?.let { formatAge(it.ageInDays) } ?: "${twin.ageDays ?: 0}d",
                        color = TwinAccent
                    )
                    InfoChip(
                        label = "Stage",
                        value = morphSummary?.stageName ?: (twin.lifecycleStage ?: "Unknown"),
                        color = TwinGold
                    )
                    twin.weightKg?.let { weight ->
                        InfoChip(
                            label = "Weight",
                            value = if (weight >= 1.0) "${"%.1f".format(weight)}kg" else "${(weight * 1000).toInt()}g",
                            color = TwinGreen
                        )
                    }
                }

                // Maturity bar
                ageProfile?.let { profile ->
                    Spacer(Modifier.height(12.dp))
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Maturity", color = TwinTextSecondary, fontSize = 11.sp)
                            Text(
                                "${(profile.maturityIndex * 100).toInt()}%",
                                color = TwinAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { profile.maturityIndex.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = TwinAccent,
                            trackColor = TwinSurfaceLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(label, color = color.copy(alpha = 0.7f), fontSize = 10.sp)
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// SCORE RADAR — 5-axis score display
// ═══════════════════════════════════════════════════════════════
@Composable
private fun ScoreRadarCard(twin: DigitalTwinEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TwinSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🎯 Score Breakdown",
                color = TwinTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))

            // Valuation score large
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${twin.valuationScore ?: 0}",
                    color = TwinGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 42.sp
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Valuation", color = TwinTextSecondary, fontSize = 12.sp)
                    Text("out of 100", color = TwinTextSecondary, fontSize = 10.sp)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Individual scores
            val scores = listOf(
                Triple("Morphology", twin.morphologyScore ?: 0, TwinPrimary),
                Triple("Genetics", twin.geneticsScore ?: 0, TwinAccent),
                Triple("Performance", twin.performanceScore ?: 0, TwinGreen),
                Triple("Health", twin.healthScore ?: 0, TwinOrange)
            )

            scores.forEach { (name, score, color) ->
                ScoreBar(name = name, score = score, maxScore = 100, color = color)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun ScoreBar(name: String, score: Int, maxScore: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            name,
            color = TwinTextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.width(85.dp)
        )
        LinearProgressIndicator(
            progress = { (score.toFloat() / maxScore).coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = TwinSurfaceLight
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "$score",
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.End
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// LIFECYCLE PROGRESS — Stage progression with countdown
// ═══════════════════════════════════════════════════════════════
@Composable
private fun LifecycleProgressCard(
    twin: DigitalTwinEntity,
    transitionInfo: AseelLifecycleEngine.TransitionInfo?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TwinSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🔄 Lifecycle Progress",
                color = TwinTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))

            if (transitionInfo != null) {
                // Compute progress: how far into current stage
                val stageSpan = (transitionInfo.nextStage.minDays - transitionInfo.currentStage.minDays).coerceAtLeast(1)
                val elapsed = (transitionInfo.currentAgeDays - transitionInfo.currentStage.minDays).coerceAtLeast(0)
                val progress = (elapsed.toFloat() / stageSpan).coerceIn(0f, 1f)

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Current: ${transitionInfo.currentStage.displayName}",
                            color = TwinTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Next: ${transitionInfo.nextStage.displayName}",
                            color = TwinAccent,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TwinAccent.copy(alpha = 0.12f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${transitionInfo.daysRemaining}",
                                color = TwinAccent,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            )
                            Text(
                                "days left",
                                color = TwinAccent.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Stage progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = TwinPrimary,
                    trackColor = TwinSurfaceLight
                )

                // New capabilities
                if (transitionInfo.capabilitiesUnlocked.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Unlocks:", color = TwinTextSecondary, fontSize = 11.sp)
                    transitionInfo.capabilitiesUnlocked.forEach { capability ->
                        Text(
                            "  ✦ $capability",
                            color = TwinGold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Text(
                    "This bird has reached its final lifecycle stage.",
                    color = TwinTextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// QUICK ACTIONS — Navigate to sub-features
// ═══════════════════════════════════════════════════════════════
@Composable
private fun QuickActionsRow(
    twin: DigitalTwinEntity,
    onGrowthTracker: () -> Unit,
    onBirdStudio: () -> Unit,
    onMorphologyGrading: () -> Unit,
    canMorphology: Boolean,
    canPerformance: Boolean,
    isBreedingEligible: Boolean,
    isShowEligible: Boolean
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            QuickActionChip(
                icon = "📈",
                label = "Growth\nTracker",
                color = TwinGreen,
                onClick = onGrowthTracker
            )
        }
        item {
            QuickActionChip(
                icon = "🎨",
                label = "Bird\nStudio",
                color = TwinPrimary,
                onClick = onBirdStudio
            )
        }
        if (canMorphology) {
            item {
                QuickActionChip(
                    icon = "📐", 
                    label = "Morphology\nMeasure", 
                    color = TwinSecondary, 
                    onClick = onMorphologyGrading
                )
            }
        }
        if (canPerformance) {
            item {
                QuickActionChip(icon = "⚔️", label = "Record\nFight", color = TwinRed, onClick = {})
            }
        }
        if (isBreedingEligible) {
            item {
                QuickActionChip(icon = "🥚", label = "Breeding\nReady", color = TwinOrange, onClick = {})
            }
        }
        if (isShowEligible) {
            item {
                QuickActionChip(icon = "🏆", label = "Show\nEligible", color = TwinGold, onClick = {})
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.width(85.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// WEIGHT SUMMARY — Quick look at growth status
// ═══════════════════════════════════════════════════════════════
@Composable
private fun WeightSummaryCard(analytics: com.rio.rostry.domain.digitaltwin.WeightAnalytics) {
    val eval = analytics.evaluation

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TwinSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "⚖️ Weight Analytics",
                color = TwinTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeightStat(
                    label = "Current",
                    value = "${analytics.currentWeightGrams ?: "—"}g",
                    color = TwinTextPrimary
                )
                WeightStat(
                    label = "Expected",
                    value = "${analytics.expectedWeightGrams}g",
                    color = TwinAccent
                )
                WeightStat(
                    label = "Rating",
                    value = eval?.rating?.displayName ?: "N/A",
                    color = when (eval?.rating) {
                        com.rio.rostry.domain.digitaltwin.lifecycle.WeightRating.EXCELLENT -> TwinGold
                        com.rio.rostry.domain.digitaltwin.lifecycle.WeightRating.GOOD -> TwinGreen
                        com.rio.rostry.domain.digitaltwin.lifecycle.WeightRating.FAIR -> TwinOrange
                        else -> TwinRed
                    }
                )
            }

            eval?.let {
                Spacer(Modifier.height(8.dp))

                val deviationColor = when {
                    it.deviationPercent > 10 -> TwinOrange
                    it.deviationPercent < -10 -> TwinRed
                    else -> TwinGreen
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Deviation: ${it.deviationPercent}%",
                        color = deviationColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = TwinTextSecondary, fontSize = 11.sp)
    }
}

// ═══════════════════════════════════════════════════════════════
// MORPHOLOGY SUMMARY — Key features of current stage
// ═══════════════════════════════════════════════════════════════
@Composable
private fun MorphologySummaryCard(morph: MorphSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TwinSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(morph.stageEmoji, fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "${morph.stageName} Morphology",
                        color = TwinTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Feather texture: ${morph.featherTexture}",
                        color = TwinTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            morph.keyFeatures.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text("✦", color = TwinAccent, fontSize = 12.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(feature, color = TwinTextPrimary, fontSize = 13.sp)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// EVENT CARD — Single timeline event
// ═══════════════════════════════════════════════════════════════
@Composable
private fun EventCard(event: BirdEventEntity) {
    val typeColor = when (event.eventType) {
        BirdEventEntity.TYPE_FIGHT_WIN -> TwinGold
        BirdEventEntity.TYPE_FIGHT_LOSS -> TwinRed
        BirdEventEntity.TYPE_WEIGHT_RECORDED -> TwinGreen
        BirdEventEntity.TYPE_VACCINATION -> TwinSecondary
        BirdEventEntity.TYPE_STAGE_TRANSITION -> TwinPrimary
        else -> TwinTextSecondary
    }

    val typeEmoji = when (event.eventType) {
        BirdEventEntity.TYPE_FIGHT_WIN -> "🏆"
        BirdEventEntity.TYPE_FIGHT_LOSS -> "💔"
        BirdEventEntity.TYPE_FIGHT_DRAW -> "🤝"
        BirdEventEntity.TYPE_WEIGHT_RECORDED -> "⚖️"
        BirdEventEntity.TYPE_VACCINATION -> "💉"
        BirdEventEntity.TYPE_STAGE_TRANSITION -> "🔄"
        BirdEventEntity.TYPE_INJURY -> "🩹"
        BirdEventEntity.TYPE_RECOVERY -> "💚"
        BirdEventEntity.TYPE_BREEDING_ATTEMPT -> "🥚"
        else -> "📝"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TwinSurface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Event emoji
            Surface(
                shape = CircleShape,
                color = typeColor.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(typeEmoji, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.eventTitle ?: event.eventType ?: "Event",
                    color = TwinTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                event.eventDescription?.let {
                    Text(
                        it,
                        color = TwinTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                formatEventDate(event.eventDate),
                color = TwinTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// UTILITIES
// ═══════════════════════════════════════════════════════════════

private fun formatAge(days: Int): String {
    return when {
        days < 7 -> "${days}d"
        days < 30 -> "${days / 7}w ${days % 7}d"
        days < 365 -> "${days / 30}m ${(days % 30) / 7}w"
        else -> "${days / 365}y ${(days % 365) / 30}m"
    }
}

private fun formatEventDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60 * 1000 -> "now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
