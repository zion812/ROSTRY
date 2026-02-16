package com.rio.rostry.ui.enthusiast.digitalfarm.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
    
    // State Hoisting for Tab Selection to drive Camera Focus
    var selectedTab by remember { mutableIntStateOf(0) }
    
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
            // 1. LIVE INTERACTIVE VIEWPORT (Top 45%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                StudioViewport(
                    appearance = state.currentAppearance,
                    selectedTab = selectedTab,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // 2. CONTROLS AREA (Bottom 55%)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                StudioControls(
                    appearance = state.currentAppearance,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onUpdate = viewModel::updateAppearance
                )
            }
        }
    }
}

// Camera State Holder
data class CameraState(
    val zoom: Float,
    val offsetX: Float,
    val offsetY: Float
)

@Composable
fun StudioViewport(
    appearance: BirdAppearance,
    selectedTab: Int,
    modifier: Modifier = Modifier
) {
    // Animation Loop for "Live Juice"
    val infiniteTransition = rememberInfiniteTransition(label = "studio_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f, // Long cycle for looping
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Camera Targets based on Tab
    // Tabs: 0=General, 1=Head, 2=Body, 3=Plumage, 4=Legs
    val targetCamera = remember(selectedTab) {
        when (selectedTab) {
            1 -> CameraState(zoom = 6f, offsetX = 0f, offsetY = 150f) // Head Focus
            2 -> CameraState(zoom = 4f, offsetX = 0f, offsetY = 0f)   // Body Focus
            3 -> CameraState(zoom = 3.2f, offsetX = -50f, offsetY = 0f) // Plumage/Tail Focus
            4 -> CameraState(zoom = 5f, offsetX = 0f, offsetY = -200f) // Legs Focus
            else -> CameraState(zoom = 3.5f, offsetX = 0f, offsetY = 0f) // General/Full
        }
    }

    // Animate Camera to Target
    val zoom by animateFloatAsState(targetValue = targetCamera.zoom, animationSpec = tween(800, easing = FastOutSlowInEasing), label = "zoom")
    val camX by animateFloatAsState(targetValue = targetCamera.offsetX, animationSpec = tween(800, easing = FastOutSlowInEasing), label = "camX")
    val camY by animateFloatAsState(targetValue = targetCamera.offsetY, animationSpec = tween(800, easing = FastOutSlowInEasing), label = "camY")

    // Manual Gestures State
    var manualZoom by remember { mutableFloatStateOf(1f) }
    var birdRotation by remember { mutableFloatStateOf(0f) } // -1.0 (Left) to 1.0 (Right)
    var manualOffsetY by remember { mutableFloatStateOf(0f) }
    
    // Reset manual overrides when tab changes
    LaunchedEffect(selectedTab) {
        manualZoom = 1f
        birdRotation = 0f
        manualOffsetY = 0f
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoomChange, _ ->
                    manualZoom *= zoomChange
                    // Pan X maps to Rotation (Turntable effect)
                    birdRotation += pan.x * 0.005f 
                    // Clamp rotation to reasonable parallax limits
                    birdRotation = birdRotation.coerceIn(-1.2f, 1.2f)
                    
                    // Pan Y maps to vertical camera offset
                    manualOffsetY += pan.y / zoom
                }
            }
    ) {
        val cx = size.width / 2
        val cy = size.height / 2
        
        // Final Camera Transform
        val finalZoom = (zoom * manualZoom).coerceIn(1f, 10f)
        val finalY = camY + manualOffsetY
        // offsetX is now constantly 0 as we rotate instead of pan x
        val finalX = camX 
        
        translate(cx + finalX, cy + finalY) {
            scale(finalZoom) {
                // Background Render
                val bgBrush = when (appearance.background) {
                    StudioBackground.STUDIO -> androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(Color(0xFFE0E0E0), Color(0xFFB0B0B0)),
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 400f
                    )
                    StudioBackground.BARN -> androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.DarkGray, Color(0xFF3E2723), Color(0xFF5D4037)),
                        startY = -300f,
                        endY = 300f
                    )
                    StudioBackground.OUTDOORS -> androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color(0xFF81D4FA), Color(0xFF81C784)), // Sky to Grass
                        startY = -200f,
                        endY = 200f
                    )
                    StudioBackground.STAGE -> androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(Color(0xFFFFF9C4).copy(alpha = 0.6f), Color.Black),
                        center = androidx.compose.ui.geometry.Offset(0f, -100f),
                        radius = 300f
                    )
                }

                drawCircle(
                    brush = bgBrush,
                    radius = 500f, // Large backdrop
                    center = androidx.compose.ui.geometry.Offset(0f, 0f)
                )

                // Ground Shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.3f), // Darker shadow for contrast
                    topLeft = androidx.compose.ui.geometry.Offset(-25f, 5f),
                    size = androidx.compose.ui.geometry.Size(50f, 15f)
                )
                
                // Bird Render
                with(BirdPartRenderer) {
                    drawBirdFromAppearance(
                        x = 0f, 
                        y = 0f, 
                        appearance = appearance, 
                        isSelected = false,
                        animTime = animTime,
                        bobOffset = 0f,
                        rotation = birdRotation
                    )
                }
            }
        }
        
        // Overlay: "Live" indicator or Camera info (Accessory)
        // drawContext.canvas.nativeCanvas.drawText(...)
    }
}

@Composable
fun StudioControls(
    appearance: BirdAppearance,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onUpdate: (BirdAppearance) -> Unit
) {
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
                    onClick = { onTabSelected(index) },
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
        HorizontalDivider()
        EnumSelector("Studio Background", StudioBackground.values(), a.background) { onUpdate(a.copy(background = it)) }
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
