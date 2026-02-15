package com.rio.rostry.ui.enthusiast.digitalfarm.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.*
import com.rio.rostry.ui.enthusiast.digitalfarm.BirdPartRenderer

import androidx.compose.ui.graphics.luminance
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.PartColor
import com.rio.rostry.ui.theme.EnthusiastDarkColors
import com.rio.rostry.ui.theme.EnthusiastLightColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdStudioScreen(
    birdId: String?,
    viewModel: BirdStudioViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Success/Error handling
    LaunchedEffect(state.success) {
        if (state.success) {
            onNavigateUp()
        }
    }
    
    LaunchedEffect(state.error) {
        state.error?.let { 
            snackbarHostState.showSnackbar(it) 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Bird Studio", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${state.breed} • ${state.gender}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::resetToBreedStandard) {
                        Icon(Icons.Default.Refresh, "Reset to Breed Standard")
                    }
                    Button(
                        onClick = viewModel::saveAppearance,
                        enabled = !state.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, "Save", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Save")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. LIVE PREVIEW AREA (Top 40%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                StudioPreviewCanvas(
                    appearance = state.currentAppearance,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Rotation/View controls could go here
            }
            
            // 2. CONTROLS AREA (Bottom 60%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                StudioControls(
                    appearance = state.currentAppearance,
                    onUpdate = viewModel::updateAppearance
                )
            }
        }
    }
}

@Composable
fun StudioPreviewCanvas(
    appearance: BirdAppearance,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val scale = 3.5f // Larger scale for studio
        
        translate(cx, cy) {
            scale(scale) {
                // Draw shadow first
                drawOval(
                    color = Color.Black.copy(alpha = 0.15f),
                    topLeft = androidx.compose.ui.geometry.Offset(-25f, 5f),
                    size = androidx.compose.ui.geometry.Size(50f, 15f)
                )
                
                // Draw Bird
                with(BirdPartRenderer) {
                    drawBirdFromAppearance(
                        x = 0f, 
                        y = 0f, 
                        appearance = appearance, 
                        isSelected = false,
                        animTime = 0f // Static for now, or animate breathing later
                    )
                }
            }
        }
    }
}

@Composable
fun StudioControls(
    appearance: BirdAppearance,
    onUpdate: (BirdAppearance) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Head", "Body", "Plumage", "Legs")
    
    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> GeneralTab(appearance, onUpdate)
                1 -> HeadTab(appearance, onUpdate)
                2 -> BodyTab(appearance, onUpdate)
                3 -> PlumageTab(appearance, onUpdate)
                4 -> LegsTab(appearance, onUpdate)
            }
        }
    }
}

// ==================== TABS ====================

@Composable
fun GeneralTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        EnumSelector("Stance / Posture", Stance.values(), a.stance) { onUpdate(a.copy(stance = it)) }
        EnumSelector("Body Size", BodySize.values(), a.bodySize) { onUpdate(a.copy(bodySize = it)) }
    }
}

@Composable
fun HeadTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        EnumSelector("Head Shape", HeadShape.values(), a.headShape) { onUpdate(a.copy(headShape = it)) }
        
        EnumSelector("Comb Style", CombStyle.values(), a.comb) { onUpdate(a.copy(comb = it)) }
        MorphSlider("Comb Size", a.combSize) { onUpdate(a.copy(combSize = it)) }
        // ColorSelector("Comb Color", a.combColor) { onUpdate(a.copy(combColor = it)) } 
        // Replaced with Advanced Control in Plumage or keep here?
        // Let's keep simple selector but add Advanced for "Accent" which covers Comb/Wattle
        AdvancedColorControl(
            title = "Comb/Wattle Accent Color",
            currentColor = a.customAccentColor,
            baseColor = a.combColor.color,
            onColorChange = { onUpdate(a.copy(customAccentColor = it)) }
        )
        
        EnumSelector("Beak Style", BeakStyle.values(), a.beak) { onUpdate(a.copy(beak = it)) }
        MorphSlider("Beak Length", a.beakScale) { onUpdate(a.copy(beakScale = it)) }
        MorphSlider("Beak Curvature", a.beakCurvature) { onUpdate(a.copy(beakCurvature = it)) }
        
        EnumSelector("Eye Color", EyeColor.values(), a.eye) { onUpdate(a.copy(eye = it)) }
        EnumSelector("Ear Lobe", EarLobeColor.values(), a.earLobe) { onUpdate(a.copy(earLobe = it)) }
    }
}

@Composable
fun BodyTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        EnumSelector("Neck Style", NeckStyle.values(), a.neck) { onUpdate(a.copy(neck = it)) }
        
        EnumSelector("Breast Shape", BreastShape.values(), a.breast) { onUpdate(a.copy(breast = it)) }
        MorphSlider("Body Width / Bulk", a.bodyWidth) { onUpdate(a.copy(bodyWidth = it)) }
        MorphSlider("Body Roundness", a.bodyRoundness) { onUpdate(a.copy(bodyRoundness = it)) }
        
        EnumSelector("Back Style", BackStyle.values(), a.back) { onUpdate(a.copy(back = it)) }
        EnumSelector("Skin Color", SkinColor.values(), a.skin) { onUpdate(a.copy(skin = it)) }
    }
}

@Composable
fun PlumageTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        AdvancedColorControl(
            title = "Body/Back/Wing Primary Color",
            currentColor = a.customPrimaryColor,
            baseColor = a.backColor.color,
            onColorChange = { onUpdate(a.copy(customPrimaryColor = it)) }
        )
        AdvancedColorControl(
            title = "Chest/Neck Secondary Color",
            currentColor = a.customSecondaryColor,
            baseColor = a.chestColor.color,
            onColorChange = { onUpdate(a.copy(customSecondaryColor = it)) }
        )
        
        HorizontalDivider()
        
        EnumSelector("Feather Sheen", Sheen.values(), a.sheen) { onUpdate(a.copy(sheen = it)) }
        EnumSelector("Pattern", PlumagePattern.values(), a.wingPattern) { onUpdate(a.copy(wingPattern = it)) }
        
        HorizontalDivider()
        Text("Tail Customization", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        MorphSlider("Tail Length", a.tailLength) { onUpdate(a.copy(tailLength = it)) }
        MorphSlider("Tail Angle", a.tailAngle) { onUpdate(a.copy(tailAngle = it)) }
        MorphSlider("Tail Spread", a.tailSpread) { onUpdate(a.copy(tailSpread = it)) }
    }
}

@Composable
fun LegsTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        EnumSelector("Leg Style", LegStyle.values(), a.legs) { onUpdate(a.copy(legs = it)) }
        EnumSelector("Joints", JointStyle.values(), a.joints) { onUpdate(a.copy(joints = it)) }
        
        MorphSlider("Leg Thickness", a.legThickness) { onUpdate(a.copy(legThickness = it)) }
        MorphSlider("Leg Length", a.legLength) { onUpdate(a.copy(legLength = it)) }
        
        ColorSelector("Leg Color", a.legColor) { onUpdate(a.copy(legColor = it)) }
        EnumSelector("Nails / Spurs", NailStyle.values(), a.nails) { onUpdate(a.copy(nails = it)) }
    }
}

// ==================== COMPONENTS ====================

@Composable
fun <T : Enum<T>> EnumSelector(
    title: String,
    values: Array<T>,
    current: T,
    onSelected: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(values) { item ->
                val selected = item == current
                FilterChip(
                    selected = selected,
                    onClick = { onSelected(item) },
                    label = { Text(item.name.replace("_", " ").lowercase().capitalize()) },
                    leadingIcon = if (selected) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
fun ColorSelector(
    title: String,
    current: PartColor,
    onSelected: (PartColor) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(PartColor.values()) { item ->
                val selected = item == current
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(item.color)
                        .clickable { onSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Icon(
                            Icons.Default.Check, 
                            null, 
                            tint = if (item.color.luminance() > 0.5f) Color.Black else Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

@Composable
fun MorphSlider(
    title: String,
    value: Float,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}

@Composable
fun AdvancedColorControl(
    title: String,
    currentColor: Long?,
    baseColor: Color,
    onColorChange: (Long?) -> Unit
) {
    // If currentColor is null, we are using the Enum baseColor.
    // If not null, we are using Custom.
    val isCustom = currentColor != null
    
    // Parse current color or fall back to base
    val activeColor = if (currentColor != null) Color(currentColor.toULong()) else baseColor
    
    // HSV state
    var hue by remember(activeColor) { mutableFloatStateOf(0f) }
    var saturation by remember(activeColor) { mutableFloatStateOf(0f) }
    var value by remember(activeColor) { mutableFloatStateOf(0f) }
    
    // Initialize HSV from activeColor on first composition or color change
    LaunchedEffect(activeColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(
            android.graphics.Color.argb(
                (activeColor.alpha * 255).toInt(),
                (activeColor.red * 255).toInt(),
                (activeColor.green * 255).toInt(),
                (activeColor.blue * 255).toInt()
            ),
            hsv
        )
        hue = hsv[0]
        saturation = hsv[1]
        value = hsv[2]
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Switch(
                checked = isCustom,
                onCheckedChange = { checked ->
                    if (checked) {
                        // Enable custom - start with base color
                        val hsv = FloatArray(3)
                        android.graphics.Color.colorToHSV(
                            android.graphics.Color.argb(
                                (baseColor.alpha * 255).toInt(),
                                (baseColor.red * 255).toInt(),
                                (baseColor.green * 255).toInt(),
                                (baseColor.blue * 255).toInt()
                            ),
                            hsv
                        )
                        val newColor = android.graphics.Color.HSVToColor(hsv)
                        onColorChange(newColor.toLong() and 0xFFFFFFFF) // Ensure unsigned behavior
                    } else {
                        // Disable custom - revert to valid Enum based colors
                        onColorChange(null)
                    }
                }
            )
        }
        
        if (isCustom) {
            // Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(activeColor)
            )
            
            // Sliders
            Text("Hue: ${hue.toInt()}°", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = hue,
                onValueChange = { h -> 
                    hue = h
                    val newColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
                    onColorChange(newColor.toLong() and 0xFFFFFFFF)
                },
                valueRange = 0f..360f,
                colors = SliderDefaults.colors(thumbColor = Color.hsv(hue, 1f, 1f))
            )

            Text("Saturation: ${(saturation * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = saturation,
                onValueChange = { s -> 
                    saturation = s
                    val newColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
                    onColorChange(newColor.toLong() and 0xFFFFFFFF)
                },
                valueRange = 0f..1f
            )

            Text("Brightness: ${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            Slider(
                value = value,
                onValueChange = { v -> 
                    value = v
                    val newColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
                    onColorChange(newColor.toLong() and 0xFFFFFFFF)
                },
                valueRange = 0f..1f
            )
        } else {
            // Show palette of preset enum colors as fallback/reference
            // Need to filter PartColor based on usage context? complicated.
            // Just show text "Using Standard Palette"
            Text("Using Standard Palette (Select 'Custom' to edit)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
