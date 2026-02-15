package com.rio.rostry.ui.enthusiast.breeding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.breeding.EnhancedTraitPrediction
import com.rio.rostry.domain.breeding.TraitRange
import com.rio.rostry.domain.service.MateRecommendationService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateFinderScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBirdProfile: (String) -> Unit = {},
    viewModel: MateFinderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mate Finder") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Analyzing potential mates…", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(uiState.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Focal Bird Header
                    item {
                        FocalBirdHeader(uiState)
                    }

                    // Results Summary
                    item {
                        Text(
                            "${uiState.candidates.size} of ${uiState.totalEvaluated} candidates ranked",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Candidate List
                    items(uiState.candidates) { candidate ->
                        val isSelected = uiState.selectedCandidate?.bird?.productId == candidate.bird.productId
                        CandidateCard(
                            candidate = candidate,
                            isSelected = isSelected,
                            onSelect = {
                                if (isSelected) viewModel.clearSelection()
                                else viewModel.selectCandidate(candidate)
                            },
                            onViewProfile = { onNavigateToBirdProfile(candidate.bird.productId) }
                        )

                        // Expanded prediction panel
                        AnimatedVisibility(visible = isSelected) {
                            PredictionPanel(
                                prediction = uiState.enhancedPrediction,
                                isLoading = uiState.predictionLoading
                            )
                        }
                    }

                    if (uiState.candidates.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Filled.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "No eligible mates found",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "Add more birds of the opposite gender to your flock",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun FocalBirdHeader(uiState: MateFinderViewModel.UiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gender badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (uiState.focalBird?.gender?.lowercase() == "male")
                            Color(0xFF00BCD4) else Color(0xFFE91E63)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (uiState.focalBird?.gender?.lowercase() == "male") "♂" else "♀",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    uiState.focalBird?.name ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${uiState.focalBird?.breed ?: "Unknown"} • Finding best ${
                        if (uiState.focalBird?.gender?.lowercase() == "male") "dams" else "sires"
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            // BVI Badge
            uiState.focalBvi?.let { bvi ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(bvi.bvi * 100).toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = bviColor(bvi.bvi)
                    )
                    Text(
                        "BVI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CandidateCard(
    candidate: MateRecommendationService.MateCandidate,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gender indicator
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (candidate.bird.gender?.lowercase() == "male")
                                Color(0xFF00BCD4) else Color(0xFFE91E63)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (candidate.bird.gender?.lowercase() == "male") "♂" else "♀",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        candidate.bird.name ?: "Unnamed",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${candidate.bird.breed ?: "Unknown"} • ${candidate.bird.ageWeeks ?: "?"}w",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Pairing Score
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${candidate.pairingScore.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = pairingScoreColor(candidate.pairingScore)
                    )
                    Text(
                        "Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Score breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScorePill("Offspring", candidate.offspringPotential)
                ScorePill("Diversity", candidate.geneticDiversity)
                ScorePill("Traits", candidate.traitComplementarity)
                ScorePill("Practical", candidate.practicalScore)
            }

            Spacer(Modifier.height(8.dp))

            // Recommendation
            Text(
                candidate.recommendation,
                style = MaterialTheme.typography.bodySmall,
                color = pairingScoreColor(candidate.pairingScore),
                fontWeight = FontWeight.Medium
            )

            // Strengths & Risks
            if (candidate.keyStrengths.isNotEmpty() || candidate.keyRisks.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                
                candidate.keyStrengths.forEach { strength ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(strength, style = MaterialTheme.typography.bodySmall)
                    }
                }

                candidate.keyRisks.forEach { risk ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(risk, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // View Profile link
            if (isSelected) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onViewProfile,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("View Full Profile")
                }
            }
        }
    }
}

@Composable
private fun ScorePill(label: String, score: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(
            progress = { (score / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .width(60.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = pairingScoreColor(score),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun PredictionPanel(
    prediction: EnhancedTraitPrediction?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Offspring Prediction",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (isLoading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Computing trait predictions…", style = MaterialTheme.typography.bodySmall)
            } else if (prediction != null) {
                Spacer(Modifier.height(12.dp))

                // Quality + Show Potential
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(prediction.predictedQuality) },
                        leadingIcon = {
                            Icon(Icons.Filled.Star, null, Modifier.size(16.dp))
                        }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("Show: ${prediction.showPotential}") },
                        leadingIcon = {
                            Icon(Icons.Filled.EmojiEvents, null, Modifier.size(16.dp))
                        }
                    )
                }

                // Breed type
                Text(
                    "Breed: ${prediction.breedType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Color prediction
                Spacer(Modifier.height(8.dp))
                Text("Color Prediction:", style = MaterialTheme.typography.labelMedium)
                prediction.colorPrediction.entries.sortedByDescending { it.value }.forEach { (color, prob) ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(color, style = MaterialTheme.typography.bodySmall)
                        Text("${(prob * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Predicted traits
                if (prediction.predictedTraits.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Trait Predictions:", style = MaterialTheme.typography.labelMedium)
                    prediction.predictedTraits.forEach { (trait, range) ->
                        TraitPredictionRow(trait, range)
                    }
                }

                // Highlights
                if (prediction.traitHighlights.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    prediction.traitHighlights.forEach { highlight ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Lightbulb,
                                null,
                                Modifier.size(14.dp),
                                tint = Color(0xFFFFC107)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(highlight, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Data confidence
                Spacer(Modifier.height(8.dp))
                Text(
                    "Data confidence: ${prediction.dataConfidence}",
                    style = MaterialTheme.typography.labelSmall,
                    color = when (prediction.dataConfidence) {
                        "High" -> Color(0xFF4CAF50)
                        "Medium" -> Color(0xFFFFC107)
                        else -> Color(0xFFFF9800)
                    }
                )
            } else {
                Spacer(Modifier.height(8.dp))
                Text("Select a candidate to see predictions", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun TraitPredictionRow(traitName: String, range: TraitRange) {
    val displayName = traitName.replace("_", " ").replaceFirstChar { it.uppercase() }
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(displayName, style = MaterialTheme.typography.bodySmall)
            Text(
                "${String.format("%.1f", range.predicted)}${range.unit?.let { " $it" } ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        // Range bar
        LinearProgressIndicator(
            progress = { (range.predicted / 10f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = when {
                range.predicted >= 7f -> Color(0xFF4CAF50)
                range.predicted >= 4f -> Color(0xFFFFC107)
                else -> Color(0xFFFF5722)
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Low: ${String.format("%.1f", range.low)}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Text(
                "High: ${String.format("%.1f", range.high)}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun pairingScoreColor(score: Float): Color = when {
    score >= 80 -> Color(0xFF4CAF50)
    score >= 65 -> Color(0xFF8BC34A)
    score >= 50 -> Color(0xFFFFC107)
    score >= 35 -> Color(0xFFFF9800)
    else -> Color(0xFFF44336)
}

@Composable
private fun bviColor(bvi: Float): Color = when {
    bvi >= 0.8f -> Color(0xFF4CAF50)
    bvi >= 0.6f -> Color(0xFF8BC34A)
    bvi >= 0.4f -> Color(0xFFFFC107)
    else -> Color(0xFFFF9800)
}
