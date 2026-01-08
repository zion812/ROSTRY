package com.rio.rostry.ui.farmer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.BirdStatusIndicator
import com.rio.rostry.domain.model.BreedingUnit
import com.rio.rostry.domain.model.DigitalFarmState
import com.rio.rostry.domain.model.FarmStats
import com.rio.rostry.domain.model.NurseryGroup
import com.rio.rostry.domain.model.RenderRate
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VisualBird as DomainVisualBird
import com.rio.rostry.ui.enthusiast.digitalfarm.DigitalFarmViewModel
import com.rio.rostry.ui.enthusiast.digitalfarm.FarmCanvasRenderer

/**
 * Farmer Digital Farm Screen - Upgraded to use the shared Premium Renderer
 * 
 * Features:
 * - Uses shared FarmCanvasRenderer from enthusiast module
 * - Persona-specific configuration via DigitalFarmConfig.FARMER
 * - Day/night cycle, weather effects, zone-based rendering
 * - Touch hit testing for bird/zone selection
 * - Ghost Eggs reminder system
 * - Gold star badges for ready birds
 * - Gamification stats bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalFarmScreen(
    viewModel: DigitalFarmViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onManageBird: (String) -> Unit,
    onViewLineage: (String) -> Unit,
    onListForSale: (String) -> Unit
) {
    // Set Farmer-specific configuration
    LaunchedEffect(Unit) {
        viewModel.setConfigForRole(UserType.FARMER)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val farmStats by viewModel.farmStats.collectAsState()
    val selectedBird by viewModel.selectedBird.collectAsState()
    val config by viewModel.config.collectAsState()
    
    // Zone-Based Tasks: Track selected zone for bottom sheet
    var selectedZone by remember { mutableStateOf<com.rio.rostry.domain.model.DigitalFarmZone?>(null) }
    var zoneBirds by remember { mutableStateOf<List<DomainVisualBird>>(emptyList()) }
    
    // Collect tap results for zone handling
    LaunchedEffect(Unit) {
        viewModel.tapResult.collect { result ->
            when (result) {
                is com.rio.rostry.domain.model.FarmTapResult.ZoneTapped -> {
                    selectedZone = result.zone
                    zoneBirds = result.birds
                }
                else -> { /* Handled elsewhere */ }
            }
        }
    }

    // Animation time for idle animations - GATED on RenderRate.DYNAMIC for lite mode performance
    val isDynamicMode = config.renderRate == RenderRate.DYNAMIC
    
    // Only create transitions when in DYNAMIC mode (Enthusiast)
    // STATIC mode (Farmer lite) skips animations entirely for performance
    val animationTime: Float
    val pulseAnim: Float
    
    if (isDynamicMode) {
        val infiniteTransition = rememberInfiniteTransition(label = "farmAnim")
        animationTime = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(100000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "time"
        ).value
        
        pulseAnim = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        ).value
    } else {
        // STATIC mode: No animations, use fixed values
        animationTime = 0f
        pulseAnim = 0f
    }

    // Colors
    val skyGradientTop = Color(0xFF1E90FF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐔 My Digital Farm")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Time-lapse button
                    IconButton(onClick = { /* TODO: Time-lapse mode */ }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    // Share button
                    IconButton(onClick = { /* TODO: Share screenshot */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = skyGradientTop.copy(alpha = 0.95f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.loadFarmData() }
                    )
                }
                uiState.isEmpty -> {
                    EmptyFarmState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Main Farm Canvas with premium renderer
                    FarmerFarmCanvas(
                        uiState = uiState,
                        animationTime = animationTime,
                        selectedBirdId = selectedBird?.productId,
                        config = config,
                        onBirdTapped = { viewModel.onBirdTapped(it) },
                        onNurseryTapped = { viewModel.onNurseryTapped(it) },
                        onBreedingHutTapped = { viewModel.onBreedingHutTapped(it) },
                        onMarketStandTapped = { viewModel.onMarketStandTapped() },
                        onZoneTapped = { zone -> viewModel.onZoneTapped(zone) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Top Stats Bar (Farmer-specific metrics) - Always visible when not loading
            if (!uiState.isLoading && uiState.error == null) {
                FarmerStatsBar(
                    stats = farmStats,
                    pulseValue = pulseAnim,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                )
            }

            // Ghost Eggs Reminder
            AnimatedVisibility(
                visible = uiState.hasEggsToLog,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                GhostEggsReminder(
                    unitsNeedingLogs = uiState.breedingUnits.filter { it.showGhostEggs }
                )
            }

            // Selected Bird Popup with animation
            AnimatedVisibility(
                visible = selectedBird != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                selectedBird?.let { bird ->
                    FarmerBirdActionSheet(
                        bird = bird,
                        onDismiss = { viewModel.clearSelection() },
                        onManage = { onManageBird(bird.productId) },
                        onViewLineage = { onViewLineage(bird.productId) },
                        onListForSale = { onListForSale(bird.productId) }
                    )
                }
            }
            
            // Zone-Based Tasks Bottom Sheet
            if (selectedZone != null) {
                ZoneTasksBottomSheet(
                    zone = selectedZone!!,
                    birdCount = zoneBirds.size,
                    onDismiss = { selectedZone = null }
                )
            }
        }
    }
}

@Composable
private fun FarmerFarmCanvas(
    uiState: DigitalFarmState,
    animationTime: Float,
    selectedBirdId: String?,
    config: com.rio.rostry.domain.model.DigitalFarmConfig,
    onBirdTapped: (DomainVisualBird) -> Unit,
    onNurseryTapped: (NurseryGroup) -> Unit,
    onBreedingHutTapped: (BreedingUnit) -> Unit,
    onMarketStandTapped: () -> Unit,
    onZoneTapped: (com.rio.rostry.domain.model.DigitalFarmZone) -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine if we're in Lite mode based on config
    val useLiteMode = config.groupingMode == com.rio.rostry.domain.model.GroupingMode.BY_BATCH
    
    Canvas(
        modifier = modifier
            .pointerInput(uiState, useLiteMode) {
                detectTapGestures { offset ->
                    val result = FarmCanvasRenderer.hitTest(
                        tapX = offset.x,
                        tapY = offset.y,
                        canvasWidth = size.width.toFloat(),
                        canvasHeight = size.height.toFloat(),
                        state = uiState,
                        useLiteMode = useLiteMode
                    )
                    
                    when (result) {
                        is FarmCanvasRenderer.HitTestResult.BirdHit -> onBirdTapped(result.bird)
                        is FarmCanvasRenderer.HitTestResult.NurseryHit -> onNurseryTapped(result.nursery)
                        is FarmCanvasRenderer.HitTestResult.BreedingHutHit -> onBreedingHutTapped(result.unit)
                        is FarmCanvasRenderer.HitTestResult.MarketStandHit -> onMarketStandTapped()
                        is FarmCanvasRenderer.HitTestResult.ZoneHit -> onZoneTapped(result.zone)
                        is FarmCanvasRenderer.HitTestResult.Nothing -> { /* Empty tap */ }
                    }
                }
            }
    ) {
        with(FarmCanvasRenderer) {
            renderFarm(
                state = uiState,
                animationTime = animationTime,
                selectedBirdId = selectedBirdId,
                config = config  // Pass config for Lite mode rendering
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E90FF), Color(0xFF90EE90))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp
            )
            Text(
                "Loading your farm...",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(72.dp)
            )
            Text(
                "Oops! Something went wrong",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Try Again")
            }
        }
    }
}

/**
 * Farmer-specific stats bar with operational metrics.
 * Shows: Total Birds, Batches, Ready for Sale, Feed Usage (as per DigitalFarmConfig.FARMER)
 */
@Composable
private fun FarmerStatsBar(
    stats: FarmStats,
    pulseValue: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total birds
            StatItem(
                icon = "🐔",
                value = stats.totalBirds.toString(),
                label = "Birds",
                color = Color(0xFF4CAF50)
            )
            
            // Batches (Farmer operational metric)
            StatItem(
                icon = "📦",
                value = stats.totalBatches.toString(),
                label = "Batches",
                color = Color(0xFF2196F3)
            )
            
            // Ready for sale (gold star with pulse)
            StatItem(
                icon = "⭐",
                value = stats.birdsReadyForSale.toString(),
                label = "Ready",
                color = Color(0xFFFFD700),
                isPulsing = stats.birdsReadyForSale > 0,
                pulseValue = pulseValue
            )
            
            // Feed Usage (Farmer operational metric)
            StatItem(
                icon = "🌾",
                value = "%.1f".format(stats.feedUsageKg),
                label = "Feed/kg",
                color = Color(0xFF8D6E63)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String,
    color: Color,
    isPulsing: Boolean = false,
    pulseValue: Float = 0f
) {
    val scale = if (isPulsing) 1f + pulseValue * 0.1f else 1f
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = (20 * scale).sp
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = color
        )
    }
}

@Composable
private fun EmptyFarmState(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(32.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "🌾",
                fontSize = 64.sp
            )
            Text(
                "Your Digital Farm is Empty",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Add your first flock to see your farm come to life!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Button(
                onClick = { /* Navigate to add product */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Your First Birds")
            }
        }
    }
}

@Composable
private fun GhostEggsReminder(
    unitsNeedingLogs: List<BreedingUnit>
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Animated egg icon
            Text("🥚", fontSize = 32.sp)
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Time to Log Eggs!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Text(
                    "${unitsNeedingLogs.size} breeding ${if (unitsNeedingLogs.size == 1) "unit needs" else "units need"} egg logging",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF795548)
                )
            }
            
            Button(
                onClick = { /* TODO: Navigate to egg logging */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA000)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log Now", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Farmer-specific bird action sheet with operational actions.
 */
@Composable
private fun FarmerBirdActionSheet(
    bird: DomainVisualBird,
    onDismiss: () -> Unit,
    onManage: () -> Unit,
    onViewLineage: () -> Unit,
    onListForSale: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(24.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4FC3F7)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🐔 ${bird.name}", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            // Stats
            Text(
                "Weight: ${bird.weightText ?: "Not recorded"}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatChip(
                    icon = "📅",
                    text = bird.ageText,
                    backgroundColor = Color.White.copy(alpha = 0.2f)
                )
                
                bird.breed?.let {
                    StatChip(
                        icon = "🏷️",
                        text = it,
                        backgroundColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
            
            if (bird.statusIndicator != BirdStatusIndicator.NONE) {
                StatusBadge(status = bird.statusIndicator)
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Farmer-specific Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onManage,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color.White)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Manage")
                }
                
                OutlinedButton(
                    onClick = onViewLineage,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color.White)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Lineage")
                }
            }
            
            // Primary action: List for sale (if ready)
            if (bird.isReadyForSale) {
                Button(
                    onClick = onListForSale,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Sell, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("List for Sale", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: String,
    text: String,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 14.sp)
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
private fun StatusBadge(status: BirdStatusIndicator) {
    val (icon, text, color) = when (status) {
        BirdStatusIndicator.VACCINE_DUE -> Triple("💉", "Vaccine Due", Color(0xFFE53935))
        BirdStatusIndicator.WEIGHT_READY -> Triple("⭐", "Ready for Sale!", Color(0xFFFFD700))
        BirdStatusIndicator.WEIGHT_WARNING -> Triple("⚠️", "Below Target Weight", Color(0xFFFFC107))
        BirdStatusIndicator.SICK -> Triple("🏥", "Needs Attention", Color(0xFFE53935))
        BirdStatusIndicator.NEW_ARRIVAL -> Triple("✨", "New Arrival", Color(0xFF4FC3F7))
        BirdStatusIndicator.NONE -> Triple("", "", Color.Transparent)
    }
    
    if (status != BirdStatusIndicator.NONE) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 16.sp)
            Text(text, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * OOM Fallback: Simple list view when Canvas rendering fails or causes memory pressure.
 * Displays birds in a simple, memory-efficient list format.
 */
@Composable
private fun FarmFallbackListView(
    uiState: DigitalFarmState,
    onBirdClick: (DomainVisualBird) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Warning header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE65100)
                )
                Column {
                    Text(
                        "Low Memory Mode",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        "Farm view simplified to save memory",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Zone summary cards
        val zones = listOf(
            Triple("🐣 Nursery", uiState.nurseries.sumOf { 1 + it.chicks.size }, Color(0xFFFFEB3B)),
            Triple("🌿 Free Range", uiState.freeRange.size, Color(0xFF4CAF50)),
            Triple("🍽️ Grow Out", uiState.growOut.size, Color(0xFF2196F3)),
            Triple("⭐ Ready", uiState.readyDisplay.size, Color(0xFFFFD700)),
            Triple("🏪 Market", uiState.marketReady.size, Color(0xFFFF5722))
        ).filter { it.second > 0 }
        
        zones.forEach { (name, count, color) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, fontWeight = FontWeight.Medium)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = color.copy(alpha = 0.2f)
                    ) {
                        Text(
                            "$count birds",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Zone-Based Tasks Bottom Sheet
 * Shows suggested tasks specific to the tapped zone.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoneTasksBottomSheet(
    zone: com.rio.rostry.domain.model.DigitalFarmZone,
    birdCount: Int,
    onDismiss: () -> Unit
) {
    val zoneTasks = remember(zone) {
        getTasksForZone(zone)
    }
    
    val zoneColor = when (zone) {
        com.rio.rostry.domain.model.DigitalFarmZone.NURSERY -> Color(0xFFFFF176) // Yellow
        com.rio.rostry.domain.model.DigitalFarmZone.FREE_RANGE -> Color(0xFF81C784) // Green
        com.rio.rostry.domain.model.DigitalFarmZone.GROW_OUT -> Color(0xFF64B5F6) // Blue
        com.rio.rostry.domain.model.DigitalFarmZone.BREEDING_UNIT -> Color(0xFFBA68C8) // Purple
        com.rio.rostry.domain.model.DigitalFarmZone.READY_DISPLAY -> Color(0xFFFFD54F) // Gold
        com.rio.rostry.domain.model.DigitalFarmZone.MARKET_STAND -> Color(0xFFFF8A65) // Orange
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Zone Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = zoneColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getZoneIcon(zone),
                                contentDescription = null,
                                tint = zoneColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            getZoneName(zone),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$birdCount birds",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Suggested Tasks",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Task List
            zoneTasks.forEach { task ->
                TaskItem(
                    title = task.first,
                    description = task.second,
                    zoneColor = zoneColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun getZoneName(zone: com.rio.rostry.domain.model.DigitalFarmZone): String = when (zone) {
    com.rio.rostry.domain.model.DigitalFarmZone.NURSERY -> "Nursery Zone"
    com.rio.rostry.domain.model.DigitalFarmZone.FREE_RANGE -> "Free Range Zone"
    com.rio.rostry.domain.model.DigitalFarmZone.GROW_OUT -> "Grow-Out Zone"
    com.rio.rostry.domain.model.DigitalFarmZone.BREEDING_UNIT -> "Breeding Unit"
    com.rio.rostry.domain.model.DigitalFarmZone.READY_DISPLAY -> "Ready Display"
    com.rio.rostry.domain.model.DigitalFarmZone.MARKET_STAND -> "Market Stand"
}

@Composable
private fun getZoneIcon(zone: com.rio.rostry.domain.model.DigitalFarmZone): androidx.compose.ui.graphics.vector.ImageVector = when (zone) {
    com.rio.rostry.domain.model.DigitalFarmZone.NURSERY -> Icons.Default.ChildCare
    com.rio.rostry.domain.model.DigitalFarmZone.FREE_RANGE -> Icons.Default.Grass
    com.rio.rostry.domain.model.DigitalFarmZone.GROW_OUT -> Icons.Default.TrendingUp
    com.rio.rostry.domain.model.DigitalFarmZone.BREEDING_UNIT -> Icons.Default.Favorite
    com.rio.rostry.domain.model.DigitalFarmZone.READY_DISPLAY -> Icons.Default.Star
    com.rio.rostry.domain.model.DigitalFarmZone.MARKET_STAND -> Icons.Default.Storefront
}

private fun getTasksForZone(zone: com.rio.rostry.domain.model.DigitalFarmZone): List<Pair<String, String>> = when (zone) {
    com.rio.rostry.domain.model.DigitalFarmZone.NURSERY -> listOf(
        "Check Brooder Temperature" to "Ensure 32-35°C for chicks under 2 weeks",
        "Refill Water" to "Clean and refill brooder waterers",
        "Monitor Chick Health" to "Check for pasty vent or lethargy"
    )
    com.rio.rostry.domain.model.DigitalFarmZone.FREE_RANGE -> listOf(
        "Check Feed Levels" to "Refill feeders if running low",
        "Monitor Flock Behavior" to "Watch for signs of stress or bullying",
        "Collect Eggs" to "Check for any eggs in hidden spots"
    )
    com.rio.rostry.domain.model.DigitalFarmZone.GROW_OUT -> listOf(
        "Record Weights" to "Weekly weight check for growth tracking",
        "Administer Vitamins" to "Add supplements to water",
        "Check for Overcrowding" to "Ensure adequate space per bird"
    )
    com.rio.rostry.domain.model.DigitalFarmZone.BREEDING_UNIT -> listOf(
        "Collect & Log Eggs" to "Record daily egg production",
        "Check Breeding Performance" to "Monitor fertility rates",
        "Clean Nesting Boxes" to "Change bedding in nesting areas"
    )
    com.rio.rostry.domain.model.DigitalFarmZone.READY_DISPLAY -> listOf(
        "Final Weight Check" to "Confirm market-ready weight",
        "Update Listing Photos" to "Take fresh photos for marketplace",
        "Process Orders" to "Check for any pending enquiries"
    )
    com.rio.rostry.domain.model.DigitalFarmZone.MARKET_STAND -> listOf(
        "Update Prices" to "Adjust prices based on market rates",
        "Respond to Enquiries" to "Check and reply to buyer messages",
        "Prepare for Handover" to "Ready birds for collection"
    )
}

@Composable
private fun TaskItem(
    title: String,
    description: String,
    zoneColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = zoneColor.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircleOutline,
                contentDescription = null,
                tint = zoneColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
