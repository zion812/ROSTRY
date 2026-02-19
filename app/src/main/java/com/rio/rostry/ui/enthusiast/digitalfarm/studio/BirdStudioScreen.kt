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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow

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
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.luminance
import com.rio.rostry.domain.model.BirdAppearance
import com.rio.rostry.domain.model.PartColor
import com.rio.rostry.ui.theme.EnthusiastDarkColors
import com.rio.rostry.ui.theme.EnthusiastLightColors
import com.rio.rostry.domain.genetics.*
import com.rio.rostry.ui.enthusiast.digitalfarm.studio.BirdPhotoBooth

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
    
    // Photo Mode State
    var isCapturing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // We need to track the current rotation to pass to the photo booth
    var currentRotation by remember { mutableFloatStateOf(0f) }

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
                    IconButton(
                        onClick = {
                            if (!isCapturing) {
                                isCapturing = true
                                scope.launch {
                                    val timestamp = System.currentTimeMillis()
                                    val uri = BirdPhotoBooth.captureAndSave(
                                        context = context,
                                        appearance = state.currentAppearance,
                                        filename = "Rostry_Bird_$timestamp",
                                        rotation = currentRotation
                                    )
                                    isCapturing = false
                                    if (uri != null) {
                                        snackbarHostState.showSnackbar("Photo saved to Gallery!")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to save photo.")
                                    }
                                }
                            }
                        },
                        enabled = !isCapturing && !state.isSaving
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CameraAlt, "Photo Mode (4x Res)")
                        }
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
                    modifier = Modifier.fillMaxSize(),
                    onRotationChange = { currentRotation = it }
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
                    onUpdate = viewModel::updateAppearance,
                    viewModel = viewModel
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
    modifier: Modifier = Modifier,
    onRotationChange: (Float) -> Unit = {}
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
    // Higher zoom = closer to bird, offsets are in bird-local space (pre-scale)
    val targetCamera = remember(selectedTab) {
        when (selectedTab) {
            1 -> CameraState(zoom = 14f, offsetX = -5f, offsetY = 25f)  // Head Focus
            2 -> CameraState(zoom = 10f, offsetX = 0f, offsetY = 10f)   // Body Focus
            3 -> CameraState(zoom = 8f, offsetX = 10f, offsetY = 10f)   // Plumage/Tail Focus
            4 -> CameraState(zoom = 12f, offsetX = 0f, offsetY = -5f)   // Legs Focus
            else -> CameraState(zoom = 8f, offsetX = 0f, offsetY = 12f) // General/Full bird view
        }
    }

    // Animate Camera to Target
    val zoom by animateFloatAsState(targetValue = targetCamera.zoom, animationSpec = tween(800, easing = FastOutSlowInEasing), label = "zoom")
    val camX by animateFloatAsState(targetValue = targetCamera.offsetX, animationSpec = tween(800, easing = FastOutSlowInEasing), label = "camX")
    val camY by animateFloatAsState(targetValue = targetCamera.offsetY, animationSpec = tween(800, easing = FastOutSlowInEasing), label = "camY")

    // Manual Gestures State
    var manualZoom by remember { mutableFloatStateOf(1f) }
    var birdRotation by remember { mutableFloatStateOf(0f) } // -1.0 (Left) to 1.0 (Right)
    var manualOffsetX by remember { mutableFloatStateOf(0f) }
    var manualOffsetY by remember { mutableFloatStateOf(0f) }
    
    // Hoist rotation state back to parent for photo capture
    LaunchedEffect(birdRotation) {
        onRotationChange(birdRotation)
    }

    // Reset manual overrides when tab changes
    LaunchedEffect(selectedTab) {
        manualZoom = 1f
        // Keep rotation, it feels better to persist angle
        manualOffsetY = 0f
        manualOffsetX = 0f
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoomChange, _ ->
                    manualZoom *= zoomChange
                    
                    val currentScale = zoom * manualZoom
                    
                    // Intelligent Pan/Rotate
                    // If zoomed in (scale > 12.0), Drag X pans the camera
                    // If zoomed out (scale <= 12.0), Drag X rotates the bird (Turntable)
                    if (currentScale > 12.0f) {
                        // Pan in bird-local space (divide by scale for consistent feel)
                        manualOffsetX += pan.x / currentScale
                    } else {
                        // Turntable rotation
                        birdRotation += pan.x * 0.005f 
                        birdRotation = birdRotation.coerceIn(-1.2f, 1.2f)
                    }
                    
                    // Pan Y always moves camera (in bird-local space)
                    manualOffsetY += pan.y / currentScale
                }
            }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        
        // Final Camera Transform
        val finalZoom = (zoom * manualZoom).coerceIn(2f, 30f)
        // Camera offset in bird-local space (NOT multiplied by zoom)
        val finalOffX = camX + manualOffsetX
        val finalOffY = camY + manualOffsetY
        
        // Step 1: Translate canvas origin to screen center
        // Step 2: Scale around screen center (pivot = Zero after translate)
        // Step 3: Apply camera pan offset INSIDE scaled space
        // This ensures zoom always centers on the bird, not on a corner
        translate(cx, cy) {
            scale(finalZoom, pivot = androidx.compose.ui.geometry.Offset.Zero) {
                // Apply camera offset in bird-local coordinates (pre-scaled)
                translate(finalOffX, finalOffY) {

                    // Background Render — sized to match bird proportions
                    val bgRadius = 60f // Proportional to bird (~40px tall)
                    val bgBrush = when (appearance.background) {
                        StudioBackground.STUDIO -> androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFFE0E0E0), Color(0xFFB0B0B0)),
                            center = androidx.compose.ui.geometry.Offset(0f, -15f),
                            radius = bgRadius
                        )
                        StudioBackground.BARN -> androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.DarkGray, Color(0xFF3E2723), Color(0xFF5D4037)),
                            startY = -bgRadius,
                            endY = bgRadius
                        )
                        StudioBackground.OUTDOORS -> androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color(0xFF81D4FA), Color(0xFF81C784)),
                            startY = -bgRadius,
                            endY = bgRadius
                        )
                        StudioBackground.STAGE -> androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFFFFF9C4).copy(alpha = 0.6f), Color.Black),
                            center = androidx.compose.ui.geometry.Offset(0f, -bgRadius * 0.5f),
                            radius = bgRadius
                        )
                    }

                    drawCircle(
                        brush = bgBrush,
                        radius = bgRadius,
                        center = androidx.compose.ui.geometry.Offset(0f, -15f) // Center on bird body
                    )

                    // Ground Shadow
                    drawOval(
                        color = Color.Black.copy(alpha = 0.25f),
                        topLeft = androidx.compose.ui.geometry.Offset(-18f, 2f),
                        size = androidx.compose.ui.geometry.Size(36f, 10f)
                    )

                    // Bird Render — bird feet at (0, 0), body extends upward
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
                } // end camera offset translate
            } // end scale
        } // end center translate
    }
}

@Composable
fun StudioControls(
    appearance: BirdAppearance,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onUpdate: (BirdAppearance) -> Unit,
    viewModel: BirdStudioViewModel? = null
) {
    val tabs = listOf("Field Mode", "Color DNA", "Quick Select", "General", "Head", "Body", "Plumage", "Legs", "Genetics")
    
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
        
        // ASI Score Indicator (Visible on all tabs)
        // ... (Keep existing ASI Indicator code if it was here, but in previous view it was below tabs)
        // Wait, I need to be careful not to delete the ASI indicator if it's in the range. 
        // The EndLine 382 is inside the `when` block or before it?
        // Let's look at the previous file view. 
        // Lines 346-400 were viewed. 
        // `StudioControls` starts at 346.
        // The `when` block was around 375.
        // I should just update the `tabs` list and the `when` block.
        
        // ... (ASI Indicator code omitted for brevity in replacement, assuming it's safe if not in range OR I need to include it)
        // Actually, the replacement range 352-382 covers the tabs list and the start of the `when` block?
        // No, `val tabs = ...` is at 352.
        // `when` starts at 375.
        // So I will replace the whole `when` block and the tabs list definition.
        
    } // This closing brace is wrong for the replacement content if I am replacing a block.
    
    // Let's refine the range.
    // I want to replace `val tabs = ...` (Line 352)
    // AND the `when` block (Line 375-383).
    // But there is code in between (ASI Indicator) which I added in a previous step!
    // I should use `multi_replace_file_content` or two separate `replace_file_content` calls.
    // Or just one `replace_file_content` if I can match the context.
    
    // Step 1: Update tabs list.
    // Step 2: Update `when` block.
    
    // Let's do Step 1 first.

    
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
        
        // ASI Score Indicator (Visible on all tabs)
        appearance.digitalTwinProfile?.let { dt ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (dt.asiScore.score >= 90) Color(0xFFE8F5E9) 
                                   else if (dt.asiScore.score >= 60) Color(0xFFFFF3E0) 
                                   else Color(0xFFFFEBEE)
                ),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Aseel Structural Index (ASI)", 
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "${dt.asiScore.score}/100", 
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (dt.asiScore.score >= 90) Color(0xFF2E7D32) 
                                    else if (dt.asiScore.score >= 60) Color(0xFFEF6C00) 
                                    else Color(0xFFC62828)
                        )
                    }
                    
                    if (dt.asiScore.validationWarnings.isNotEmpty()) {
                         Icon(Icons.Default.Warning, null, tint = Color(0xFFC62828))
                    } else if (dt.asiScore.score >= 90) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32))
                    }
                }
                
                if (dt.asiScore.validationWarnings.isNotEmpty()) {
                    HorizontalDivider()
                    Column(Modifier.padding(12.dp)) {
                        dt.asiScore.validationWarnings.take(2).forEach { warning ->
                            Text("• $warning", style = MaterialTheme.typography.bodySmall, color = Color(0xFFC62828))
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> FieldModeTab(appearance, onUpdate, viewModel)
                1 -> ColorDNATab(appearance, onUpdate)
                2 -> QuickSelectTab(appearance, onUpdate) // Was 1
                3 -> GeneralTab(appearance, onUpdate)     // Was 2
                4 -> HeadTab(appearance, onUpdate)        // Was 3
                5 -> BodyTab(appearance, onUpdate)        // Was 4
                6 -> PlumageTab(appearance, onUpdate)     // Was 5
                7 -> LegsTab(appearance, onUpdate)        // Was 6
                8 -> GeneticsTab(appearance, onUpdate)    // Was 7
            }
        }
    }
}

@Composable
fun FieldModeTab(
    appearance: BirdAppearance, 
    onUpdate: (BirdAppearance) -> Unit,
    viewModel: BirdStudioViewModel?
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Structure-First Classification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Select a structural frame type. This sets the biological chassis of the bird.", style = MaterialTheme.typography.bodySmall)
        
        val presets = listOf("Power Frame", "Tall Lean", "Compact", "Standard")
        
        presets.chunked(2).forEach { rowPresets ->
             Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                rowPresets.forEach { preset ->
                    Button(
                        onClick = { viewModel?.applyFieldPreset(preset) },
                        modifier = Modifier.weight(1f).height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(preset, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
             }
        }
        
        HorizontalDivider()
        
        Text("Fine Tune Structure", style = MaterialTheme.typography.labelLarge)
        // Re-use existing sliders but grouped logically
        MorphSlider("Walking Height", appearance.legLength) { onUpdate(appearance.copy(legLength = it)) }
        MorphSlider("Neck Reach", appearance.neckLength ?: 0.5f) { val style = if(it > 0.7f) com.rio.rostry.domain.model.NeckStyle.LONG else com.rio.rostry.domain.model.NeckStyle.MEDIUM; onUpdate(appearance.copy(neckLength = it, neck = style)) }
    }
}

@Composable
fun ColorDNATab(
    appearance: BirdAppearance,
    onUpdate: (BirdAppearance) -> Unit
) {
    val dt = appearance.digitalTwinProfile ?: com.rio.rostry.domain.digitaltwin.DigitalTwinProfile()
    val color = dt.color

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Color DNA", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Genetic color profile independent of structure.", style = MaterialTheme.typography.bodySmall)

        // 1. Base Color Type
        DigitalTwinDropdown(
            label = "Base Chassis",
            values = com.rio.rostry.domain.digitaltwin.BaseColorType.entries.toTypedArray(),
            selected = color.baseType,
            onSelected = { newBase ->
                val newColor = color.copy(baseType = newBase)
                onUpdate(appearance.copy(digitalTwinProfile = dt.copy(color = newColor)))
            }
        )

        // 2. Distribution Map
        DigitalTwinDropdown(
            label = "Pattern Map",
            values = com.rio.rostry.domain.digitaltwin.DistributionMap.entries.toTypedArray(),
            selected = color.distribution,
            onSelected = { newDist ->
                val newColor = color.copy(distribution = newDist)
                onUpdate(appearance.copy(digitalTwinProfile = dt.copy(color = newColor)))
            }
        )

        // 3. Sheen Level
        DigitalTwinDropdown(
            label = "Sheen",
            values = com.rio.rostry.domain.digitaltwin.SheenLevel.entries.toTypedArray(),
            selected = color.sheen,
            onSelected = { newSheen ->
                val newColor = color.copy(sheen = newSheen)
                // Also update legacy sheen for renderer
                val legacySheen = when(newSheen) {
                    com.rio.rostry.domain.digitaltwin.SheenLevel.MATTE -> Sheen.MATTE
                    com.rio.rostry.domain.digitaltwin.SheenLevel.GLOSS -> Sheen.GLOSSY
                    com.rio.rostry.domain.digitaltwin.SheenLevel.IRIDESCENT -> Sheen.IRIDESCENT
                    com.rio.rostry.domain.digitaltwin.SheenLevel.METALLIC -> Sheen.METALLIC
                }
                onUpdate(appearance.copy(
                    sheen = legacySheen,
                    digitalTwinProfile = dt.copy(color = newColor)
                ))
            }
        )
    }
}

// Re-using AlleleDropdown but making it generic in previous step inside BirdStudioScreen.kt
// If it was private or inside GeneticsTab scope, I need to check. 
// It was defined as a standalone composable `AlleleDropdown` in `BirdStudioScreen.kt` (lines 719-753), so it should be accessible.
// However, the previous definition used <T : Allele> bound. 
// My Enums (BaseColorType) do NOT implement Allele interface. 
// I need to make a generic dropdown that doesn't enforce Allele interface OR make my enums implement it.
// Simpler: Create a generic String dropdown or specific dropdowns. 
// Let's create a `DigitalTwinDropdown` helper.

@Composable
fun <T : Enum<T>> DigitalTwinDropdown(
    label: String,
    values: Array<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: ${selected.name}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            values.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        onSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ==================== GENETICS TAB ====================
@Composable
fun GeneticsTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    // We need to maintain a state for the GeneticProfile to allow editing.
    // However, the Studio works on BirdAppearance.
    // Ideally, we'd have a two-way binding or store the underlying Genotype in the ViewModel.
    // FOR NOW: We will construct a temporary Genotype based on current Appearance (reverse engineering is hard/impossible uniquely),
    // OR more simply: We present default alleles and when the user changes them, we regenerate the Appearance completely.
    // This matches the "Reset to Breed Standard" logic but granularly.
    // To make this usable, we should probably start with a "Default Genotype" state if it doesn't exist.
    
    // Let's use a local state for the profile, initialized to a standard "Black" or similar if we can't derive it.
    // Since we don't store Genotype in Appearance yet, we'll start with a clean slate or "Wild Type".
    var profile by remember { 
        mutableStateOf(
            GeneticProfile(
                id = "temp",
                eLocus = Pair(AlleleE.EXTENDED, AlleleE.EXTENDED), // Default E/E
                sLocus = Pair(AlleleS.GOLD, AlleleS.GOLD),
                bLocus = Pair(AlleleB.NON_BARRED, AlleleB.NON_BARRED),
                coLocus = Pair(AlleleCo.NON_COLUMBIAN, AlleleCo.NON_COLUMBIAN),
                pgLocus = Pair(AllelePg.NON_PATTERNED, AllelePg.NON_PATTERNED),
                mlLocus = Pair(AlleleMl.NON_MELANOTIC, AlleleMl.NON_MELANOTIC),
                moLocus = Pair(AlleleMo.NON_MOTTLED, AlleleMo.NON_MOTTLED),
                blLocus = Pair(AlleleBl.BLACK, AlleleBl.BLACK)
            )
        ) 
    }
    
    // Helper to update profile and regenerate appearance
    fun updateGenotype(newProfile: GeneticProfile) {
        profile = newProfile
        val newAppearance = com.rio.rostry.domain.genetics.PhenotypeMapper.mapToAppearance(newProfile, 20)
        // Preserve some non-genetic traits if possible? (like pose/background)
        onUpdate(newAppearance.copy(
            stance = a.stance, 
            background = a.background, 
            bodySize = a.bodySize,
            headShape = a.headShape
            // Keep structural mutations that aren't in our minimal Genotype yet
        ))
    }

    // === BREEDING SANDBOX STATE ===
    var partnerProfile by remember { 
        mutableStateOf(
            GeneticProfile(
                id = "partner",
                eLocus = Pair(AlleleE.EXTENDED, AlleleE.EXTENDED), 
                sLocus = Pair(AlleleS.GOLD, AlleleS.GOLD),
                bLocus = Pair(AlleleB.NON_BARRED, AlleleB.NON_BARRED),
                coLocus = Pair(AlleleCo.NON_COLUMBIAN, AlleleCo.NON_COLUMBIAN),
                pgLocus = Pair(AllelePg.NON_PATTERNED, AllelePg.NON_PATTERNED),
                mlLocus = Pair(AlleleMl.NON_MELANOTIC, AlleleMl.NON_MELANOTIC),
                moLocus = Pair(AlleleMo.NON_MOTTLED, AlleleMo.NON_MOTTLED),
                blLocus = Pair(AlleleBl.BLACK, AlleleBl.BLACK)
            )
        ) 
    }
    
    var offspringList by remember { mutableStateOf<List<BirdAppearance>>(emptyList()) }
    var showPartnerConfig by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Real-Time Genetics Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Edit the genotype below to strictly enforce standard poultry genetics rules. This overrides manual color tweaks.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        HorizontalDivider()

        // E Locus (Base Color)
        GenotypeSelector("Base Color (E Locus)", AlleleE.values(), profile.eLocus) { 
            updateGenotype(profile.copy(eLocus = it)) 
        }

        // S Locus (Sex Linked)
        GenotypeSelector("Silver/Gold (S Locus)", AlleleS.values(), profile.sLocus) { 
            updateGenotype(profile.copy(sLocus = it)) 
        }

        // B Locus (Barring)
        GenotypeSelector("Barring (B Locus)", AlleleB.values(), profile.bLocus) { 
            updateGenotype(profile.copy(bLocus = it)) 
        }

        // Co Locus (Columbian)
        GenotypeSelector("Columbian (Co Locus)", AlleleCo.values(), profile.coLocus) { 
            updateGenotype(profile.copy(coLocus = it)) 
        }

        // Pg Locus (Pattern)
        GenotypeSelector("Pattern (Pg Locus)", AllelePg.values(), profile.pgLocus) { 
            updateGenotype(profile.copy(pgLocus = it)) 
        }

        // Ml Locus (Melanotic)
        GenotypeSelector("Melanotic (Ml Locus)", AlleleMl.values(), profile.mlLocus) { 
            updateGenotype(profile.copy(mlLocus = it)) 
        }

        // Mo Locus (Mottling)
        GenotypeSelector("Mottling (Mo Locus)", AlleleMo.values(), profile.moLocus) { 
            updateGenotype(profile.copy(moLocus = it)) 
        }

        // Bl Locus (Blue Dilution)
        GenotypeSelector("Blue (Bl Locus)", AlleleBl.values(), profile.blLocus) { 
            updateGenotype(profile.copy(blLocus = it)) 
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Genotype Visualization string
        Card(
             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
             modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Genotype Formula", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = profile.toGenotypeString(), 
                    style = MaterialTheme.typography.bodyLarge, 
                    fontWeight = FontWeight.Bold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
        
        HorizontalDivider()
        
        // === BREEDING SANDBOX ===
        Text("Breeding Sandbox", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
        
        Card(
            modifier = Modifier.fillMaxWidth().clickable { showPartnerConfig = !showPartnerConfig },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Partner Genotype (Tap to Edit)", fontWeight = FontWeight.Bold)
                    Icon(if (showPartnerConfig) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
                }
                
                if (showPartnerConfig) {
                    Spacer(Modifier.height(8.dp))
                    Text("Configure the partner's genetics:", style = MaterialTheme.typography.bodySmall)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    
                    // Simplified Partner Config (A few key loci)
                    GenotypeSelector("Base Color (E)", AlleleE.values(), partnerProfile.eLocus) { 
                        partnerProfile = partnerProfile.copy(eLocus = it) 
                    }
                    GenotypeSelector("Silver/Gold (S)", AlleleS.values(), partnerProfile.sLocus) { 
                         partnerProfile = partnerProfile.copy(sLocus = it)
                    }
                    GenotypeSelector("Barring (B)", AlleleB.values(), partnerProfile.bLocus) { 
                         partnerProfile = partnerProfile.copy(bLocus = it)
                    }
                     GenotypeSelector("Blue (Bl)", AlleleBl.values(), partnerProfile.blLocus) { 
                         partnerProfile = partnerProfile.copy(blLocus = it)
                    }
                }
                Text(
                    text = "Formula: ${partnerProfile.toGenotypeString()}", 
                    style = MaterialTheme.typography.bodySmall, 
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Button(
            onClick = {
                // HATCH 20 EGGS Logic
                val clutch = (1..20).map { i ->
                    val babyGeno = BreedingSimulator.breedOne(profile, partnerProfile, "chick_$i")
                    com.rio.rostry.domain.genetics.PhenotypeMapper.mapToAppearance(babyGeno, 1) // 1 week old
                }
                offspringList = clutch
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Icon(Icons.Default.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text("Hatch Clutch (20 Eggs)")
        }
        
        if (offspringList.isNotEmpty()) {
            Text("Offspring Results (${offspringList.size}):", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(offspringList) { chick ->
                    // Mini Chick Renderer
                    Card(modifier = Modifier.size(100.dp, 120.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                Canvas(Modifier.fillMaxSize()) {
                                     scale(0.8f) { // Zoom out a bit
                                        translate(size.width/2, size.height/2 + 50f) {
                                            with(BirdPartRenderer) {
                                                drawBirdFromAppearance(0f, 0f, chick, false, 0f, 0f, 0f)
                                            }
                                        }
                                     }
                                }
                            }
                            Text("Chick", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T : com.rio.rostry.domain.genetics.Allele> GenotypeSelector(
    title: String,
    values: Array<T>,
    currentPair: Pair<T, T>,
    onUpdate: (Pair<T, T>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("${currentPair.first.symbol}/${currentPair.second.symbol}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Allele 1 Selector
            Box(Modifier.weight(1f)) {
                 AlleleDropdown(values, currentPair.first) { onUpdate(currentPair.copy(first = it)) }
            }
            // Allele 2 Selector
            Box(Modifier.weight(1f)) {
                 AlleleDropdown(values, currentPair.second) { onUpdate(currentPair.copy(second = it)) }
            }
        }
    }
}

@Composable
fun <T : com.rio.rostry.domain.genetics.Allele> AlleleDropdown(
    values: Array<T>,
    selected: T,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${selected.symbol} (${selected.toString().lowercase().capitalize()})")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            values.forEach { allele ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(allele.symbol, fontWeight = FontWeight.Bold)
                            Text(allele.toString().lowercase().capitalize(), style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    onClick = { 
                        onSelected(allele)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ==================== TABS ====================

@Composable
fun QuickSelectTab(a: BirdAppearance, onUpdate: (BirdAppearance) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Current Classification Display
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Detected Local Type", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = a.localType?.let { "${it.typeName} (${it.teluguName})" } ?: "Unknown",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (a.localType != null) {
                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Text(
                    text = a.localType?.description ?: "Modify colors to detect type...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider()

        // 2. Quick Select Grid
        Text("Quick Select (Village Mode)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        val localTypes = LocalBirdType.entries.filter { it != LocalBirdType.UNKNOWN }
        
        // Simple Grid Layout
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            localTypes.chunked(2).forEach { rowTypes ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowTypes.forEach { type ->
                        QuickSelectButton(
                            type = type,
                            isSelected = a.localType == type,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                // Apply preset colors for this type
                                val preset = applyLocalTypePreset(a, type)
                                onUpdate(preset)
                            }
                        )
                    }
                    if (rowTypes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickSelectButton(
    type: LocalBirdType,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(56.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(type.teluguName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(type.typeName, style = MaterialTheme.typography.labelSmall)
        }
    }
}

// Helper to apply presets
fun applyLocalTypePreset(current: BirdAppearance, type: LocalBirdType): BirdAppearance {
    // Reset to base template then apply specific color overrides
    // This is a simplified preset logic. Ideally, we map each LocalBirdType to specific colors.
    
    val base = current.copy(localType = type) // Force set type? Or let classifier re-confirm? 
    // We set colors, classifier will re-confirm.
    
    return when (type) {
        LocalBirdType.KAKI -> base.copy(
            backColor = PartColor.BLACK, chestColor = PartColor.BLACK, wingColor = PartColor.BLACK, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.SETHU -> base.copy(
            backColor = PartColor.WHITE, chestColor = PartColor.WHITE, wingColor = PartColor.WHITE, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.DEGA -> base.copy(
            backColor = PartColor.DARK_RED, chestColor = PartColor.RED, wingColor = PartColor.RED, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.SAVALA -> base.copy(
            backColor = PartColor.RED, chestColor = PartColor.BLACK, wingColor = PartColor.RED, wingPattern = PlumagePattern.SOLID
        ) // Black neck/chest
        LocalBirdType.PARLA -> base.copy(
            backColor = PartColor.BLACK, chestColor = PartColor.WHITE, wingColor = PartColor.BLACK, wingPattern = PlumagePattern.BARRED
        )
        LocalBirdType.KOKKIRAYI -> base.copy(
            backColor = PartColor.BLACK, chestColor = PartColor.BLACK, wingColor = PartColor.RED, wingPattern = PlumagePattern.MOTTLED
        )
        LocalBirdType.NEMALI -> base.copy(
             backColor = PartColor.BLACK, chestColor = PartColor.BLACK, wingColor = PartColor.YELLOW, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.KOWJU -> base.copy(
             backColor = PartColor.BROWN, chestColor = PartColor.BLACK, wingColor = PartColor.GOLD, wingPattern = PlumagePattern.DOUBLE_LACED
        )
        LocalBirdType.MAILA -> base.copy(
             backColor = PartColor.RED, chestColor = PartColor.GRAY, wingColor = PartColor.GRAY, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.POOLA -> base.copy(
             backColor = PartColor.WHITE, chestColor = PartColor.BLACK, wingColor = PartColor.RED, wingPattern = PlumagePattern.SPECKLED
        )
        LocalBirdType.PINGALA -> base.copy(
             backColor = PartColor.BROWN, chestColor = PartColor.BROWN, wingColor = PartColor.WHITE, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.NALLA_BORA -> base.copy(
             backColor = PartColor.RED, chestColor = PartColor.BLACK, wingColor = PartColor.RED, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.MUNGISA -> base.copy(
             backColor = PartColor.BROWN, chestColor = PartColor.BROWN, wingColor = PartColor.GRAY, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.ABRASU -> base.copy(
             backColor = PartColor.BUFF, chestColor = PartColor.BUFF, wingColor = PartColor.BUFF, wingPattern = PlumagePattern.SOLID
        )
        LocalBirdType.GERUVA -> base.copy(
             backColor = PartColor.WHITE, chestColor = PartColor.WHITE_PINK, wingColor = PartColor.WHITE, wingPattern = PlumagePattern.SOLID
        )
        else -> base
    }
}

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
