package com.rio.rostry.ui.enthusiast.digitalfarm

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.DigitalFarmMode
import com.rio.rostry.domain.model.VisualBird
import com.rio.rostry.domain.model.*
import com.rio.rostry.domain.model.UserType
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import kotlinx.coroutines.delay

/**
 * Digital Farm Screen - Evolutionary Visuals
 * 
 * An interactive, gamified visualization of the user's farm
 * showing birds in different lifecycle stages as visual zones.
 * 
 * Features:
 * - Low-poly 2.5D isometric style rendering
 * - Idle animations (bird bobbing, cloud movement)
 * - Touch hit testing for bird/zone selection
 * - Ghost Eggs reminder system
 * - Gold star badges for ready birds
 * - Gamification stats bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalFarmScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToListProduct: (String) -> Unit,
    onNavigateToLogEggs: (String) -> Unit,
    onNavigateToAddBird: () -> Unit,
    onNavigateToBirdStudio: (String) -> Unit = {},
    viewModel: DigitalFarmViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val farmStats by viewModel.farmStats.collectAsState()
    val selectedBird by viewModel.selectedBird.collectAsState()
    val config by viewModel.config.collectAsState()

    // Search & Filter state
    val searchQuery by viewModel.searchQuery.collectAsState()
    val highlightedBirdIds by viewModel.highlightedBirdIds.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val activeZoneFilter by viewModel.activeZoneFilter.collectAsState()
    var isSearchBarVisible by remember { mutableStateOf(false) }
    
    // Initialize config for Enthusiast on launch
    LaunchedEffect(Unit) {
        viewModel.setConfigForRole(UserType.ENTHUSIAST)
    }

    var showListView by remember { mutableStateOf(false) }

    // Animation time for idle animations - GATED on RenderRate.DYNAMIC for future config support
    val isDynamicMode = config.renderRate == RenderRate.DYNAMIC
    
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
    val skyGradientBottom = Color(0xFF87CEEB)
    val grassGreen = Color(0xFF7CFC00)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐔 My Digital Farm")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Search toggle
                    IconButton(onClick = {
                        isSearchBarVisible = !isSearchBarVisible
                        if (!isSearchBarVisible) viewModel.clearSearch()
                    }) {
                        Icon(
                            if (isSearchActive) Icons.Default.SearchOff else Icons.Default.Search,
                            contentDescription = "Search Birds"
                        )
                    }
                    // Time-lapse button
                    IconButton(onClick = { 
                        android.widget.Toast.makeText(context, "Time-lapse feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    // Share button
                    IconButton(onClick = { 
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "Check out my Digital Farm on ROSTRY! I have ${farmStats.totalBirds} birds and ${farmStats.birdsReadyForSale} ready for sale! 🐔🥚")
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Farm Stats")
                        context.startActivity(shareIntent)
                    }) {
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
                        onAddBird = onNavigateToAddBird,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Main Farm Canvas - 3D Isometric with zoom/pan
                    IsometricFarmCanvas(
                        uiState = uiState,
                        animationTime = animationTime,
                        selectedBirdId = selectedBird?.productId,
                        config = config,
                        onBirdTapped = { viewModel.onBirdTapped(it) },
                        onNurseryTapped = { viewModel.onNurseryTapped(it) },
                        onBreedingHutTapped = { viewModel.onBreedingHutTapped(it) },
                        onMarketStandTapped = { viewModel.onMarketStandTapped() },
                        highlightedBirdIds = highlightedBirdIds,
                        isSearchActive = isSearchActive,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Top Stats Bar (Gamification UI) - Always visible when not loading
            if (!uiState.isLoading && uiState.error == null) {
                FarmStatsBar(
                    stats = farmStats,
                    pulseValue = pulseAnim,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                )
            }

            // ============ SEARCH BAR + ZONE FILTER CHIPS ============
            AnimatedVisibility(
                visible = isSearchBarVisible && !uiState.isLoading && uiState.error == null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 56.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Search TextField
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search by ID, name, breed, color...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00E5FF),
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedContainerColor = Color(0x80000000),
                            unfocusedContainerColor = Color(0x60000000),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    // Zone filter chips (horizontally scrollable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // "All" chip
                        FilterChip(
                            selected = activeZoneFilter == null,
                            onClick = { viewModel.setZoneFilter(null) },
                            label = { Text("All", fontSize = 11.sp) },
                            leadingIcon = { Text("🌐", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1E88E5),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0x60000000),
                                labelColor = Color.White
                            ),
                            modifier = Modifier.height(30.dp)
                        )

                        // Zone chips
                        val zones = listOf(
                            Triple(DigitalFarmZone.NURSERY, "🐣", "Nursery"),
                            Triple(DigitalFarmZone.FREE_RANGE, "🌿", "Free Range"),
                            Triple(DigitalFarmZone.GROW_OUT, "📈", "Grow Out"),
                            Triple(DigitalFarmZone.MAIN_COOP, "🏠", "Coop"),
                            Triple(DigitalFarmZone.BREEDING_UNIT, "💕", "Breeding"),
                            Triple(DigitalFarmZone.MARKET_STAND, "🏪", "Market"),
                            Triple(DigitalFarmZone.QUARANTINE, "⚠️", "Quarantine"),
                            Triple(DigitalFarmZone.READY_DISPLAY, "⭐", "Ready")
                        )
                        zones.forEach { (zone, emoji, name) ->
                            val count = uiState.countForZone(zone)
                            FilterChip(
                                selected = activeZoneFilter == zone,
                                onClick = {
                                    viewModel.setZoneFilter(
                                        if (activeZoneFilter == zone) null else zone
                                    )
                                },
                                label = { Text("$name ($count)", fontSize = 11.sp) },
                                leadingIcon = { Text(emoji, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF1E88E5),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0x60000000),
                                    labelColor = Color.White
                                ),
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }

                    // Search results count
                    if (isSearchActive) {
                        Text(
                            text = "${highlightedBirdIds.size} birds found",
                            color = Color(0xFF00E5FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
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
                    unitsNeedingLogs = uiState.breedingUnits.filter { it.showGhostEggs },
                    onLogEggs = { onNavigateToLogEggs(it.unitId) }
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
                    BirdStatsBubble(
                        bird = bird,
                        onDismiss = { viewModel.clearSelection() },
                        onViewDetails = { onNavigateToProduct(bird.productId) },
                        onListForSale = { onNavigateToListProduct(bird.productId) },
                        onCustomize = { onNavigateToBirdStudio(bird.productId) }
                    )
                }
            }

            if (!uiState.isEmpty && uiState.error == null && !uiState.isLoading) {
                QuickActionsFab(
                    onAddBird = onNavigateToAddBird,
                    onViewAll = { showListView = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }

        if (showListView) {
            ViewAllBirdsSheet(
                birds = uiState.allBirds,
                onDismiss = { showListView = false },
                onBirdClick = { bird ->
                    showListView = false
                    viewModel.onBirdTapped(bird)
                }
            )
        }
    }
}

@Composable
private fun EnhancedFarmCanvas(
    uiState: DigitalFarmState,
    animationTime: Float,
    selectedBirdId: String?,
    config: DigitalFarmConfig,
    onBirdTapped: (VisualBird) -> Unit,
    onNurseryTapped: (NurseryGroup) -> Unit,
    onBreedingHutTapped: (BreedingUnit) -> Unit,
    onMarketStandTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hearts = remember { mutableStateListOf<HeartParticle>() }
    
    // Heart animation loop
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { time ->
                val iterator = hearts.listIterator()
                while (iterator.hasNext()) {
                    val heart = iterator.next()
                    if (System.currentTimeMillis() - heart.createdAt > 1000) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(uiState) {
                    detectTapGestures(
                        onTap = { offset ->
                            val result = FarmCanvasRenderer.hitTest(
                                tapX = offset.x,
                                tapY = offset.y,
                                canvasWidth = size.width.toFloat(),
                                canvasHeight = size.height.toFloat(),
                                state = uiState,
                                useLiteMode = config.groupingMode == GroupingMode.BY_BATCH
                            )
                            
                            when (result) {
                                is FarmCanvasRenderer.HitTestResult.BirdHit -> onBirdTapped(result.bird)
                                is FarmCanvasRenderer.HitTestResult.NurseryHit -> onNurseryTapped(result.nursery)
                                is FarmCanvasRenderer.HitTestResult.BreedingHutHit -> onBreedingHutTapped(result.unit)
                                is FarmCanvasRenderer.HitTestResult.MarketStandHit -> onMarketStandTapped()
                                is FarmCanvasRenderer.HitTestResult.ZoneHit -> { /* Lite mode only - not used for Enthusiast */ }
                                is FarmCanvasRenderer.HitTestResult.Nothing -> { /* Empty tap */ }
                            }
                        },
                        onDoubleTap = { offset ->
                            // Add hearts at click position
                            repeat(3) {
                                hearts.add(
                                    HeartParticle(
                                        x = offset.x + (Math.random() * 60 - 30).toFloat(),
                                        y = offset.y + (Math.random() * 60 - 30).toFloat(),
                                        createdAt = System.currentTimeMillis()
                                    )
                                )
                            }
                            // Haptic or sound placeholder
                            // view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }
                    )
                }
        ) {
            with(FarmCanvasRenderer) {
                renderFarm(
                    state = uiState,
                    animationTime = animationTime,
                    selectedBirdId = selectedBirdId,
                    config = config
                )
            }
        }
        
        // Render overlay hearts
        hearts.forEach { heart ->
            HeartEffect(x = heart.x, y = heart.y, createdAt = heart.createdAt)
        }
    }
}

data class HeartParticle(val x: Float, val y: Float, val createdAt: Long)

@Composable
private fun HeartEffect(x: Float, y: Float, createdAt: Long) {
    val duration = 1000
    val elapsed = System.currentTimeMillis() - createdAt
    val progress = (elapsed / duration.toFloat()).coerceIn(0f, 1f)
    
    if (progress >= 1f) return
    
    val alpha = 1f - progress
    val offsetY = -100f * progress // Float up
    val scale = 0.5f + (1.5f * progress) // Grow then fade
    
    Text(
        "❤️",
        fontSize = 24.sp,
        modifier = Modifier
            .offset(x = with(LocalDensity.current) { x.toDp() }, y = with(LocalDensity.current) { (y + offsetY).toDp() })
            .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
    )
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

@Composable
private fun FarmStatsBar(
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
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Total birds
            StatItem(
                icon = "🐔",
                value = stats.totalBirds.toString(),
                label = "Birds",
                color = Color(0xFF4CAF50)
            )
            
            // Eggs today
            StatItem(
                icon = "🥚",
                value = stats.totalEggsToday.toString(),
                label = "Eggs",
                color = Color(0xFFFFC107)
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
            
            // Coins
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("₹", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text(
                    stats.coins.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2E7D32)
                )
            }
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
private fun EmptyFarmState(onAddBird: () -> Unit, modifier: Modifier = Modifier) {
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
                onClick = onAddBird,
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
    unitsNeedingLogs: List<BreedingUnit>,
    onLogEggs: (BreedingUnit) -> Unit
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
                onClick = { unitsNeedingLogs.firstOrNull()?.let { onLogEggs(it) } },
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

@Composable
private fun BirdStatsBubble(
    bird: VisualBird,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit,
    onListForSale: () -> Unit,
    onCustomize: () -> Unit = {},
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
                Column {
                    Text("🐔", fontSize = 32.sp)
                    bird.birdCode?.let { code ->
                        Text(
                            code,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
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

            // Bird name
            Text(
                bird.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
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
            
            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color.White)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Details")
                }

                Button(
                    onClick = onCustomize,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF673AB7)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Studio", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            
            Button(
                onClick = onListForSale,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Sell, contentDescription = null, tint = Color(0xFF3E2723), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("List for Sale", color = Color(0xFF3E2723), fontWeight = FontWeight.Bold)
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
        BirdStatusIndicator.READY_FOR_SALE -> Triple("🏷️", "For Sale", Color(0xFF4CAF50))
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

@Composable
private fun QuickActionsFab(
    onAddBird: () -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        expanded = false
                        onAddBird()
                    },
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Bird", tint = Color.White)
                }
                
                SmallFloatingActionButton(
                    onClick = {
                        expanded = false
                        onViewAll()
                    },
                    containerColor = Color(0xFF2196F3)
                ) {
                    Icon(Icons.Default.List, contentDescription = "View All", tint = Color.White)
                }
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = if (expanded) Color(0xFFE53935) else Color(0xFF4CAF50)
        ) {
            Icon(
                if (expanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = if (expanded) "Close" else "Actions",
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewAllBirdsSheet(
    birds: List<VisualBird>,
    onDismiss: () -> Unit,
    onBirdClick: (VisualBird) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                "All Birds (${birds.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(birds) { bird ->
                    Card(
                        onClick = { onBirdClick(bird) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    bird.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${bird.breed ?: "Unknown"} • ${bird.ageText}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            StatusBadge(bird.statusIndicator)
                        }
                    }
                }
            }
        }
    }
}
