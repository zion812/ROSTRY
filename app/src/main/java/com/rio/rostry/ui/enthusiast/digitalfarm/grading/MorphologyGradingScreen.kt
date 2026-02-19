package com.rio.rostry.ui.enthusiast.digitalfarm.grading

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.digitaltwin.ManualMorphologyGrades

// ═══════════════════════════════════════════════════════════════
// Digital Twin Palette (Shared)
// ═══════════════════════════════════════════════════════════════
private val GradeBackground = Color(0xFF0D1117)
private val GradeSurface = Color(0xFF161B22)
private val GradePrimary = Color(0xFF7C4DFF)
private val GradeAccent = Color(0xFF00E5FF)
private val GradeTextPrimary = Color(0xFFE6EDF3)
private val GradeTextSecondary = Color(0xFF8B949E)
private val GradeGold = Color(0xFFFFD700)
private val GradeRed = Color(0xFFF85149)
private val GradeGreen = Color(0xFF3FB950)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorphologyGradingScreen(
    birdId: String?,
    viewModel: MorphologyGradingViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(state.error) {
        state.error?.let { 
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            onNavigateUp()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Manual Grading", 
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (state.birdName.isNotBlank()) {
                            Text(
                                "${state.birdName} • ${state.breed}",
                                fontSize = 12.sp,
                                color = GradeAccent
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradeBackground,
                    titleContentColor = GradeTextPrimary,
                    navigationIconContentColor = GradeTextPrimary
                )
            )
        },
        containerColor = GradeBackground
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GradePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GradeSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Info, null, tint = GradeAccent, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Expert Assessment",
                                    color = GradeTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Grade honestly against the ${state.breed} standard. These inputs override automated analysis and directly affect the valuation score.",
                                    color = GradeTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // Head Section
                item {
                    GradingSection(title = "HEAD & FACE", icon = "🧐") {
                        GradingChipGroup(
                            label = "Beak Type",
                            options = listOf("PARROT", "MEDIUM", "STRAIGHT", "HOOKED"),
                            selected = state.grades.beakType,
                            onSelected = viewModel::updateBeak
                        )
                        Spacer(Modifier.height(16.dp))
                        GradingChipGroup(
                            label = "Eye Color",
                            options = listOf("PEARL", "WHITE", "YELLOW", "RED", "DARK"),
                            selected = state.grades.eyeColor,
                            onSelected = viewModel::updateEye
                        )
                    }
                }

                // Body Section
                item {
                    GradingSection(title = "BODY STRUCTURE", icon = "💪") {
                        ScoreSlider(
                            label = "Structure Quality",
                            value = state.grades.bodyStructureScore.toFloat(),
                            onValueChange = viewModel::updateBodyScore
                        )
                        Spacer(Modifier.height(16.dp))
                        ScoreSlider(
                            label = "Stance / Carriage",
                            value = state.grades.stanceScore.toFloat(),
                            onValueChange = viewModel::updateStanceScore
                        )
                    }
                }

                // Plumage & Legs
                item {
                    GradingSection(title = "PLUMAGE & LEGS", icon = "🪶") {
                        ScoreSlider(
                            label = "Plumage Condition",
                            value = state.grades.plumageQualityScore.toFloat(),
                            onValueChange = viewModel::updatePlumageScore
                        )
                        Spacer(Modifier.height(16.dp))
                        GradingChipGroup(
                            label = "Leg Color",
                            options = listOf("WHITE", "IVORY", "YELLOW", "DARK", "GREEN"),
                            selected = state.grades.legColor,
                            onSelected = viewModel::updateLegColor
                        )
                        Spacer(Modifier.height(16.dp))
                        GradingChipGroup(
                            label = "Tail Carry",
                            options = listOf("LOW", "DROOPING", "LEVEL", "HIGH", "SQUIRREL"),
                            selected = state.grades.tailCarry,
                            onSelected = viewModel::updateTailCarry
                        )
                    }
                }

                // Faults
                item {
                    GradingSection(title = "MAJOR FAULTS (PENALTIES)", icon = "⚠️", borderColor = GradeRed.copy(alpha = 0.3f)) {
                        FaultCheckbox("Wry Tail (-10 pts)", state.grades.hasWryTail, viewModel::toggleWryTail)
                        FaultCheckbox("Split Wing (-5 pts)", state.grades.hasSplitWing, viewModel::toggleSplitWing)
                        FaultCheckbox("Crooked Toes (-5 pts)", state.grades.hasCrookedToes, viewModel::toggleCrookedToes)
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::submitGrading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = !state.isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GradePrimary,
                            contentColor = Color.White,
                            disabledContainerColor = GradeSurface,
                            disabledContentColor = GradeTextSecondary
                        )
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Submit Grading Assessment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun GradingSection(
    title: String, 
    icon: String,
    borderColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                color = GradeTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = GradeSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradingChipGroup(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column {
        Text(
            label, 
            color = GradeTextPrimary, 
            fontSize = 14.sp, 
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(options.size) { index ->
                val option = options[index]
                val isSelected = option == selected
                
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(option) },
                    label = { Text(option) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = GradeBackground,
                        labelColor = GradeTextSecondary,
                        selectedContainerColor = GradePrimary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) GradePrimary else GradeTextSecondary.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ScoreSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label, 
                color = GradeTextPrimary, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.SemiBold
            )
            Surface(
                color = if (value >= 8) GradeGreen.copy(alpha=0.2f) else if (value >= 5) GradeGold.copy(alpha=0.2f) else GradeRed.copy(alpha=0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "${value.toInt()}/10",
                    color = if (value >= 8) GradeGreen else if (value >= 5) GradeGold else GradeRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = GradePrimary,
                activeTrackColor = GradePrimary,
                inactiveTrackColor = GradeSurface.copy(alpha = 0.5f)
            )
        )
        Row(
            Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Poor", color = GradeTextSecondary, fontSize = 10.sp)
            Text("Average", color = GradeTextSecondary, fontSize = 10.sp)
            Text("Excellent", color = GradeTextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun FaultCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Checkbox(
                checked = checked, 
                onCheckedChange = null, // Handled by Surface
                colors = CheckboxDefaults.colors(
                    checkedColor = GradeRed,
                    uncheckedColor = GradeTextSecondary
                )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label, 
                color = if (checked) GradeRed else GradeTextPrimary,
                fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}
