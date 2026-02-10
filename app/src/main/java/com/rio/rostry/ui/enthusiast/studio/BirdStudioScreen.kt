package com.rio.rostry.ui.enthusiast.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rio.rostry.domain.model.*
import com.rio.rostry.ui.enthusiast.digitalfarm.BirdPartRenderer.drawBirdFromAppearance
import kotlin.math.sin

// ==================== STUDIO CATEGORIES ====================

private enum class StudioCategory(val icon: String, val label: String) {
    BODY("🦴", "Body"),
    HEAD("👑", "Head"),
    LEGS("🦵", "Legs"),
    PLUMAGE("🪶", "Plumage"),
    COLORS("🎨", "Colors")
}

private enum class StudioSubPart(val label: String, val category: StudioCategory) {
    // Body
    BODY_SIZE("Size", StudioCategory.BODY),
    BACK_STYLE("Back", StudioCategory.BODY),
    WING_STYLE("Wings", StudioCategory.BODY),
    TAIL_STYLE("Tail", StudioCategory.BODY),

    // Head
    COMB_STYLE("Comb", StudioCategory.HEAD),
    CROWN_STYLE("Crown", StudioCategory.HEAD),
    BEAK_STYLE("Beak", StudioCategory.HEAD),
    EYE_COLOR("Eye", StudioCategory.HEAD),
    WATTLE_STYLE("Wattle", StudioCategory.HEAD),
    EAR_LOBE("Ear Lobe", StudioCategory.HEAD),

    // Legs
    LEG_STYLE("Legs", StudioCategory.LEGS),
    JOINT_STYLE("Joints", StudioCategory.LEGS),
    NAIL_STYLE("Nails/Spurs", StudioCategory.LEGS),

    // Plumage
    CHEST_PATTERN("Chest Pattern", StudioCategory.PLUMAGE),
    WING_PATTERN("Wing Pattern", StudioCategory.PLUMAGE),

    // Colors
    CHEST_COLOR("Chest", StudioCategory.COLORS),
    BACK_COLOR("Back", StudioCategory.COLORS),
    WING_COLOR("Wings", StudioCategory.COLORS),
    TAIL_COLOR("Tail", StudioCategory.COLORS),
    LEG_COLOR("Legs", StudioCategory.COLORS),
    COMB_COLOR("Comb", StudioCategory.COLORS),
    BEAK_COLOR("Beak", StudioCategory.COLORS),
    NAIL_COLOR("Nails", StudioCategory.COLORS),
    WATTLE_COLOR("Wattle", StudioCategory.COLORS),
    CROWN_COLOR("Crown", StudioCategory.COLORS);
}

// ==================== ASEEL PRESETS ====================

private data class BreedPreset(
    val name: String,
    val breed: String,
    val emoji: String
)

private val ASEEL_PRESETS = listOf(
    BreedPreset("Reza", "reza aseel", "🔥"),
    BreedPreset("Mianwali", "mianwali aseel", "🌾"),
    BreedPreset("Sindhi", "sindhi aseel", "🏔️"),
    BreedPreset("Madras", "madras aseel", "🌶️"),
    BreedPreset("Lasani", "lasani aseel", "⚪"),
    BreedPreset("Mushki", "mushki aseel", "🖤"),
    BreedPreset("Lakha", "lakha aseel", "🎯"),
    BreedPreset("Kilmora", "kilmora aseel", "🏆"),
    BreedPreset("Java", "java aseel", "💪"),
    BreedPreset("Generic", "aseel", "🐔"),
)

// ==================== MAIN SCREEN ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdStudioScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: BirdStudioViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Bird data from ViewModel
    val asset = state.asset
    val birdName = asset?.name ?: "My Bird"
    val birdBreed = asset?.breed
    val birdGender = asset?.gender
    val birdAgeWeeks = asset?.ageWeeks ?: 24

    // Derive default appearance from ViewModel state
    val defaultAppearance = remember(state.appearance) {
        state.appearance ?: deriveAppearanceFromBreed(birdBreed, birdGender, birdAgeWeeks)
    }

    // Mutable state for the appearance being edited
    var appearance by remember(defaultAppearance) { mutableStateOf(defaultAppearance) }
    var selectedCategory by remember { mutableStateOf(StudioCategory.BODY) }
    var selectedSubPart by remember { mutableStateOf<StudioSubPart?>(null) }
    var hasChanges by remember { mutableStateOf(false) }

    // Handle save success
    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) {
            hasChanges = false
            viewModel.clearSaveFlag()
        }
    }

    // Animation for live preview
    val infiniteTransition = rememberInfiniteTransition(label = "studio_anim")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "studio_time"
    )

    // Preview zoom
    var previewScale by remember { mutableFloatStateOf(2.5f) }

    // Show loading
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF00E5FF))
                Spacer(Modifier.height(12.dp))
                Text("Loading bird data...", color = Color.White.copy(alpha = 0.6f))
            }
        }
        return
    }

    val bgGradient = Brush.verticalGradient(
        listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Bird Studio",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "$birdName • ${birdBreed ?: "Unknown"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Reset button
                    AnimatedVisibility(visible = hasChanges, enter = fadeIn(), exit = fadeOut()) {
                        TextButton(onClick = {
                            appearance = defaultAppearance
                            hasChanges = false
                        }) {
                            Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF5252))
                            Spacer(Modifier.width(4.dp))
                            Text("Reset", color = Color(0xFFFF5252))
                        }
                    }
                    // Save button
                    Button(
                        onClick = {
                            viewModel.saveAppearance(appearance)
                            hasChanges = false
                        },
                        enabled = hasChanges,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00E676),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFF2A2A3E)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
        ) {
            // ==================== LIVE PREVIEW AREA (top 40%) ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.38f)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF1E3A5F), Color(0xFF0F2027)),
                            center = Offset(0.5f, 0.5f),
                            radius = 600f
                        )
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            previewScale = (previewScale * zoom).coerceIn(1.5f, 5f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Ground platform glow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height * 0.65f

                    // Platform shadow
                    drawOval(
                        brush = Brush.radialGradient(
                            listOf(Color(0x40673AB7), Color.Transparent),
                            center = Offset(cx, cy + 20f),
                            radius = 120f * previewScale / 2.5f
                        ),
                        topLeft = Offset(cx - 100f * previewScale / 2.5f, cy),
                        size = Size(200f * previewScale / 2.5f, 50f * previewScale / 2.5f)
                    )

                    // Grid floor lines
                    val gridAlpha = 0.08f
                    for (i in -5..5) {
                        val shift = i * 30f * previewScale / 2.5f
                        drawLine(
                            Color.White.copy(alpha = gridAlpha),
                            Offset(cx + shift - 150f, cy + 25f),
                            Offset(cx + shift + 150f, cy - 25f),
                            strokeWidth = 0.5f
                        )
                        drawLine(
                            Color.White.copy(alpha = gridAlpha),
                            Offset(cx - 150f, cy + shift / 3f),
                            Offset(cx + 150f, cy + shift / 3f),
                            strokeWidth = 0.5f
                        )
                    }

                    // Draw the actual bird using BirdPartRenderer
                    val bobOffset = sin((animTime * 3f).toDouble()).toFloat() * 3f * previewScale / 2.5f
                    drawBirdFromAppearance(
                        x = cx,
                        y = cy,
                        appearance = appearance,
                        isSelected = false,
                        animTime = animTime,
                        bobOffset = bobOffset
                    )
                }

                // Gender badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = if (appearance.isMale) Color(0xFF1565C0).copy(alpha = 0.8f)
                    else Color(0xFFC62828).copy(alpha = 0.8f)
                ) {
                    Text(
                        if (appearance.isMale) "♂ Male" else "♀ Female",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Body size badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF2E2E4E).copy(alpha = 0.85f)
                ) {
                    Text(
                        "📏 ${appearance.bodySize.displayName()}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                // Pinch hint
                Text(
                    "Pinch to zoom",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp
                )
            }

            // ==================== ASEEL PRESETS BAR ====================
            if (birdBreed?.lowercase()?.contains("aseel") == true || birdBreed?.lowercase() == "asil") {
                Surface(
                    color = Color(0xFF2A1A4E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "  🐔 Aseel Presets",
                            color = Color(0xFFCE93D8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp, top = 6.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(ASEEL_PRESETS) { preset ->
                                val isActive = birdBreed?.lowercase()?.contains(preset.breed.split(" ").first()) == true
                                Surface(
                                    onClick = {
                                        appearance = deriveAppearanceFromBreed(preset.breed, birdGender, birdAgeWeeks)
                                        hasChanges = true
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isActive) Color(0xFF673AB7) else Color(0xFF3A2A5E),
                                    border = if (isActive) BorderStroke(1.dp, Color(0xFFCE93D8)) else null
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(preset.emoji, fontSize = 14.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            preset.name,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==================== CATEGORY TABS ====================
            ScrollableTabRow(
                selectedTabIndex = StudioCategory.entries.indexOf(selectedCategory),
                containerColor = Color(0xFF1A1A2E),
                contentColor = Color.White,
                edgePadding = 8.dp,
                indicator = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(Color.Transparent)
                    )
                },
                divider = {}
            ) {
                StudioCategory.entries.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            selectedSubPart = StudioSubPart.entries
                                .firstOrNull { it.category == cat }
                        },
                        text = {
                            Text(
                                "${cat.icon} ${cat.label}",
                                fontSize = 13.sp,
                                fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Color(0xFF00E5FF),
                        unselectedContentColor = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // ==================== SUB-PART SELECTOR ====================
            val subParts = StudioSubPart.entries.filter { it.category == selectedCategory }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF12122A))
            ) {
                items(subParts) { part ->
                    val isSelected = selectedSubPart == part
                    val bgColor by animateColorAsState(
                        if (isSelected) Color(0xFF673AB7) else Color(0xFF2A2A3E),
                        label = "sub_part_bg"
                    )
                    Surface(
                        onClick = { selectedSubPart = part },
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = if (isSelected) BorderStroke(1.dp, Color(0xFF00E5FF)) else null
                    ) {
                        Text(
                            part.label,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ==================== OPTIONS PANEL ====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.32f)
                    .background(Color(0xFF12122A))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                when (selectedSubPart) {
                    // ---- BODY ----
                    StudioSubPart.BODY_SIZE -> {
                        OptionGrid(
                            items = BodySize.entries.map { it.displayName() },
                            selected = appearance.bodySize.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(bodySize = BodySize.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.BACK_STYLE -> {
                        OptionGrid(
                            items = BackStyle.entries.map { it.displayName() },
                            selected = appearance.back.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(back = BackStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.WING_STYLE -> {
                        OptionGrid(
                            items = WingStyle.entries.map { it.displayName() },
                            selected = appearance.wings.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(wings = WingStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.TAIL_STYLE -> {
                        OptionGrid(
                            items = TailStyle.entries.map { it.displayName() },
                            selected = appearance.tail.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(tail = TailStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }

                    // ---- HEAD ----
                    StudioSubPart.COMB_STYLE -> {
                        OptionGrid(
                            items = CombStyle.entries.map { it.displayName() },
                            selected = appearance.comb.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(comb = CombStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.CROWN_STYLE -> {
                        OptionGrid(
                            items = CrownStyle.entries.map { it.displayName() },
                            selected = appearance.crown.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(crown = CrownStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.BEAK_STYLE -> {
                        OptionGrid(
                            items = BeakStyle.entries.map { it.displayName() },
                            selected = appearance.beak.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(beak = BeakStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.EYE_COLOR -> {
                        OptionGrid(
                            items = EyeColor.entries.map { it.displayName() },
                            selected = appearance.eye.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(eye = EyeColor.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.WATTLE_STYLE -> {
                        OptionGrid(
                            items = WattleStyle.entries.map { it.displayName() },
                            selected = appearance.wattle.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(wattle = WattleStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.EAR_LOBE -> {
                        OptionGrid(
                            items = EarLobeColor.entries.map { it.displayName() },
                            selected = appearance.earLobe.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(earLobe = EarLobeColor.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }

                    // ---- LEGS ----
                    StudioSubPart.LEG_STYLE -> {
                        OptionGrid(
                            items = LegStyle.entries.map { it.displayName() },
                            selected = appearance.legs.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(legs = LegStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.JOINT_STYLE -> {
                        OptionGrid(
                            items = JointStyle.entries.map { it.displayName() },
                            selected = appearance.joints.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(joints = JointStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.NAIL_STYLE -> {
                        OptionGrid(
                            items = NailStyle.entries.map { it.displayName() },
                            selected = appearance.nails.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(nails = NailStyle.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }

                    // ---- PLUMAGE PATTERNS ----
                    StudioSubPart.CHEST_PATTERN -> {
                        OptionGrid(
                            items = PlumagePattern.entries.map { it.displayName() },
                            selected = appearance.chest.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(chest = PlumagePattern.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }
                    StudioSubPart.WING_PATTERN -> {
                        OptionGrid(
                            items = PlumagePattern.entries.map { it.displayName() },
                            selected = appearance.wingPattern.displayName(),
                            onSelect = { name ->
                                appearance = appearance.copy(wingPattern = PlumagePattern.entries.first { it.displayName() == name })
                                hasChanges = true
                            }
                        )
                    }

                    // ---- COLORS ----
                    StudioSubPart.CHEST_COLOR -> ColorPalette(appearance.chestColor) { c -> appearance = appearance.copy(chestColor = c); hasChanges = true }
                    StudioSubPart.BACK_COLOR -> ColorPalette(appearance.backColor) { c -> appearance = appearance.copy(backColor = c); hasChanges = true }
                    StudioSubPart.WING_COLOR -> ColorPalette(appearance.wingColor) { c -> appearance = appearance.copy(wingColor = c); hasChanges = true }
                    StudioSubPart.TAIL_COLOR -> ColorPalette(appearance.tailColor) { c -> appearance = appearance.copy(tailColor = c); hasChanges = true }
                    StudioSubPart.LEG_COLOR -> ColorPalette(appearance.legColor) { c -> appearance = appearance.copy(legColor = c); hasChanges = true }
                    StudioSubPart.COMB_COLOR -> ColorPalette(appearance.combColor) { c -> appearance = appearance.copy(combColor = c); hasChanges = true }
                    StudioSubPart.BEAK_COLOR -> ColorPalette(appearance.beakColor) { c -> appearance = appearance.copy(beakColor = c); hasChanges = true }
                    StudioSubPart.NAIL_COLOR -> ColorPalette(appearance.nailColor) { c -> appearance = appearance.copy(nailColor = c); hasChanges = true }
                    StudioSubPart.WATTLE_COLOR -> ColorPalette(appearance.wattleColor) { c -> appearance = appearance.copy(wattleColor = c); hasChanges = true }
                    StudioSubPart.CROWN_COLOR -> ColorPalette(appearance.crownColor) { c -> appearance = appearance.copy(crownColor = c); hasChanges = true }

                    null -> {
                        // Show welcome/hint
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎮", fontSize = 32.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Select a part to customize",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // ==================== GENDER TOGGLE + QUICK ACTIONS ====================
            Surface(
                color = Color(0xFF1A1A2E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gender toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Gender:", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = appearance.isMale,
                            onClick = {
                                appearance = appearance.copy(isMale = true)
                                hasChanges = true
                            },
                            label = { Text("♂ Male", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1565C0),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF2A2A3E),
                                labelColor = Color.White.copy(alpha = 0.6f)
                            )
                        )
                        Spacer(Modifier.width(6.dp))
                        FilterChip(
                            selected = !appearance.isMale,
                            onClick = {
                                appearance = appearance.copy(isMale = false)
                                hasChanges = true
                            },
                            label = { Text("♀ Female", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFC62828),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF2A2A3E),
                                labelColor = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }

                    // Auto-derive button
                    OutlinedButton(
                        onClick = {
                            appearance = deriveAppearanceFromBreed(birdBreed, if (appearance.isMale) "Male" else "Female", birdAgeWeeks)
                            hasChanges = true
                        },
                        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Autorenew, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Auto", color = Color(0xFF00E5FF), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==================== REUSABLE COMPONENTS ====================

@Composable
private fun OptionGrid(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        // Create rows of 3
        items.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { item ->
                    val isSelected = item == selected
                    val bgColor by animateColorAsState(
                        if (isSelected) Color(0xFF673AB7) else Color(0xFF2A2A3E),
                        label = "option_bg"
                    )
                    Surface(
                        onClick = { onSelect(item) },
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        border = if (isSelected) BorderStroke(2.dp, Color(0xFF00E5FF)) else BorderStroke(1.dp, Color(0xFF3A3A5E)),
                        modifier = Modifier.weight(1f),
                        shadowElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Text(
                            item,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // Fill remaining space if row has less than 3
                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ColorPalette(
    currentColor: PartColor,
    onColorSelected: (PartColor) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        Text(
            "Tap to select color",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
        )

        // Color grid - 5 per row
        PartColor.entries.toList().chunked(5).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { color ->
                    val isSelected = color == currentColor
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 42.dp else 36.dp)
                                .shadow(
                                    elevation = if (isSelected) 8.dp else 2.dp,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .background(color.color)
                                .then(
                                    if (isSelected) Modifier.border(
                                        3.dp, Color(0xFF00E5FF), CircleShape
                                    ) else Modifier.border(
                                        1.dp, Color.White.copy(alpha = 0.2f), CircleShape
                                    )
                                )
                                .clickable { onColorSelected(color) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (color.color.luminance() > 0.5f) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            color.displayName(),
                            color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                // Fill remaining
                repeat(5 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Spacer(Modifier.height(8.dp))
    }
}

// Utility extension for color luminance
private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}
