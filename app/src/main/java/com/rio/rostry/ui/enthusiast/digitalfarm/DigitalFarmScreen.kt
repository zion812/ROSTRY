package com.rio.rostry.ui.enthusiast.digitalfarm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.*

/**
 * Digital Farm Screen - Evolutionary Visuals
 * 
 * An interactive, gamified visualization of the user's farm
 * showing birds in different lifecycle stages as visual zones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalFarmScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToListProduct: (String) -> Unit,
    onNavigateToLogEggs: (String) -> Unit,
    viewModel: DigitalFarmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val farmStats by viewModel.farmStats.collectAsState()
    val selectedBird by viewModel.selectedBird.collectAsState()

    // Colors for the farm
    val grassGreen = Color(0xFF7CFC00)
    val skyBlue = Color(0xFF87CEEB)
    val earthBrown = Color(0xFF8B4513)
    val fenceWood = Color(0xFFDEB887)
    val hutYellow = Color(0xFFFFD700)
    val hutOrange = Color(0xFFFF8C00)
    val roofRed = Color(0xFFDC143C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Digital Farm") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                    containerColor = grassGreen.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(skyBlue, grassGreen)
                    )
                )
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text("Error loading farm", style = MaterialTheme.typography.titleMedium)
                        Text(uiState.error ?: "", style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { viewModel.loadFarmData() }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.isEmpty -> {
                    EmptyFarmState(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Main Farm Canvas
                    FarmCanvas(
                        uiState = uiState,
                        onBirdTapped = { viewModel.onBirdTapped(it) },
                        onNurseryTapped = { viewModel.onNurseryTapped(it) },
                        onBreedingHutTapped = { viewModel.onBreedingHutTapped(it) },
                        onMarketStandTapped = { viewModel.onMarketStandTapped() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Top Stats Bar (Gamification UI)
            FarmStatsBar(
                stats = farmStats,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )

            // Ghost Eggs Reminder
            AnimatedVisibility(
                visible = uiState.hasEggsToLog,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                GhostEggsReminder(
                    unitsNeedingLogs = uiState.breedingUnits.filter { it.showGhostEggs },
                    onLogEggs = { onNavigateToLogEggs(it.unitId) }
                )
            }

            // Selected Bird Popup
            selectedBird?.let { bird ->
                BirdStatsBubble(
                    bird = bird,
                    onDismiss = { viewModel.clearSelection() },
                    onViewDetails = { onNavigateToProduct(bird.productId) },
                    onListForSale = { onNavigateToListProduct(bird.productId) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun FarmStatsBar(
    stats: FarmStats,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Total birds
        StatItem(
            icon = Icons.Default.Pets,
            value = stats.totalBirds.toString(),
            color = Color(0xFF4CAF50)
        )
        
        // Eggs today
        StatItem(
            icon = Icons.Default.EggAlt,
            value = stats.totalEggsToday.toString(),
            color = Color(0xFFFFC107)
        )
        
        // Ready for sale (gold star)
        StatItem(
            icon = Icons.Default.Star,
            value = stats.birdsReadyForSale.toString(),
            color = Color(0xFFFFD700)
        )
        
        // Coins
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Text("₹", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Text(
                stats.coins.toString(),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun FarmCanvas(
    uiState: DigitalFarmState,
    onBirdTapped: (VisualBird) -> Unit,
    onNurseryTapped: (NurseryGroup) -> Unit,
    onBreedingHutTapped: (BreedingUnit) -> Unit,
    onMarketStandTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Canvas(
        modifier = modifier
            .pointerInput(uiState) {
                detectTapGestures { offset ->
                    val canvasWidth = size.width.toFloat()
                    val canvasHeight = size.height.toFloat()
                    val normalizedX = offset.x / canvasWidth
                    val normalizedY = offset.y / canvasHeight
                    
                    // TODO: Hit testing for zones and birds
                    // For now, simplified tap detection
                }
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Layer 1: Ground
        drawGround(canvasWidth, canvasHeight)

        // Layer 2: Fences
        drawFences(canvasWidth, canvasHeight)

        // Layer 3: Structures (huts, market stand)
        drawBreedingHuts(uiState.breedingUnits, canvasWidth, canvasHeight)
        drawMarketStand(canvasWidth, canvasHeight)

        // Layer 4: Birds
        drawNurseries(uiState.nurseries, canvasWidth, canvasHeight)
        drawFreeRangeBirds(uiState.freeRange, canvasWidth, canvasHeight)
        drawGrowOutBirds(uiState.growOut, canvasWidth, canvasHeight)
        drawReadyBirds(uiState.readyDisplay, canvasWidth, canvasHeight)
        drawMarketBirds(uiState.marketReady, canvasWidth, canvasHeight)
    }
}

// Canvas drawing extension functions
private fun DrawScope.drawGround(width: Float, height: Float) {
    // Main grass area
    drawRect(
        color = Color(0xFF90EE90),
        topLeft = Offset(0f, height * 0.3f),
        size = Size(width, height * 0.7f)
    )
    
    // Add some grass tufts
    val grassColor = Color(0xFF228B22)
    repeat(20) { i ->
        val x = (i * width / 20) + (i * 17 % 30)
        val y = height * (0.4f + (i * 0.02f))
        drawGrassTuft(x, y, grassColor)
    }
}

private fun DrawScope.drawGrassTuft(x: Float, y: Float, color: Color) {
    val path = Path().apply {
        moveTo(x, y)
        lineTo(x - 5f, y - 20f)
        moveTo(x, y)
        lineTo(x, y - 25f)
        moveTo(x, y)
        lineTo(x + 5f, y - 18f)
    }
    drawPath(path, color, style = Stroke(width = 3f))
}

private fun DrawScope.drawFences(width: Float, height: Float) {
    val fenceColor = Color(0xFFDEB887)
    val postColor = Color(0xFF8B4513)
    
    // Main enclosure fence
    val fenceTop = height * 0.35f
    val fenceBottom = height * 0.85f
    val fenceLeft = width * 0.1f
    val fenceRight = width * 0.9f
    
    // Horizontal rails
    drawLine(fenceColor, Offset(fenceLeft, fenceTop), Offset(fenceRight, fenceTop), strokeWidth = 6f)
    drawLine(fenceColor, Offset(fenceLeft, fenceTop + 20f), Offset(fenceRight, fenceTop + 20f), strokeWidth = 4f)
    
    // Posts
    val postCount = 8
    repeat(postCount) { i ->
        val x = fenceLeft + (i * (fenceRight - fenceLeft) / (postCount - 1))
        drawLine(postColor, Offset(x, fenceTop - 10f), Offset(x, fenceTop + 40f), strokeWidth = 8f)
    }
}

private fun DrawScope.drawBreedingHuts(units: List<BreedingUnit>, width: Float, height: Float) {
    units.forEachIndexed { index, unit ->
        val hutX = width * (0.6f + index * 0.15f)
        val hutY = height * 0.15f
        drawHut(hutX, hutY, Color(0xFFFFD700), Color(0xFFDC143C))
        
        // Draw eggs if ghost eggs visible
        if (unit.showGhostEggs) {
            drawGhostEggs(hutX, hutY + 60f)
        }
    }
}

private fun DrawScope.drawHut(x: Float, y: Float, wallColor: Color, roofColor: Color) {
    // Hut body
    drawRoundRect(
        color = wallColor,
        topLeft = Offset(x - 30f, y),
        size = Size(60f, 50f),
        cornerRadius = CornerRadius(5f)
    )
    
    // Roof
    val roofPath = Path().apply {
        moveTo(x - 40f, y)
        lineTo(x, y - 30f)
        lineTo(x + 40f, y)
        close()
    }
    drawPath(roofPath, roofColor)
    
    // Door
    drawRoundRect(
        color = Color(0xFF5D4037),
        topLeft = Offset(x - 8f, y + 20f),
        size = Size(16f, 30f),
        cornerRadius = CornerRadius(3f)
    )
}

private fun DrawScope.drawGhostEggs(x: Float, y: Float) {
    val eggColor = Color.White.copy(alpha = 0.7f)
    repeat(3) { i ->
        drawOval(
            color = eggColor,
            topLeft = Offset(x - 30f + (i * 20f), y),
            size = Size(15f, 20f)
        )
    }
}

private fun DrawScope.drawMarketStand(width: Float, height: Float) {
    val standX = width * 0.75f
    val standY = height * 0.75f
    
    // Table
    drawRect(
        color = Color(0xFFDEB887),
        topLeft = Offset(standX - 40f, standY),
        size = Size(80f, 10f)
    )
    
    // Legs
    drawRect(Color(0xFF8B4513), Offset(standX - 35f, standY), Size(8f, 40f))
    drawRect(Color(0xFF8B4513), Offset(standX + 27f, standY), Size(8f, 40f))
    
    // Awning (red and white stripes)
    val awningPath = Path().apply {
        moveTo(standX - 50f, standY - 30f)
        lineTo(standX, standY - 50f)
        lineTo(standX + 50f, standY - 30f)
        lineTo(standX + 50f, standY)
        lineTo(standX - 50f, standY)
        close()
    }
    
    // Red base
    drawPath(awningPath, Color(0xFFDC143C))
    
    // White stripes
    repeat(5) { i ->
        if (i % 2 == 0) {
            val stripeX = standX - 40f + (i * 20f)
            drawRect(
                Color.White,
                Offset(stripeX, standY - 45f),
                Size(10f, 45f)
            )
        }
    }
}

private fun DrawScope.drawNurseries(nurseries: List<NurseryGroup>, width: Float, height: Float) {
    nurseries.forEach { nursery ->
        val x = nursery.nestPosition.x * width
        val y = nursery.nestPosition.y * height
        
        // Draw nest
        drawOval(
            color = Color(0xFF8B4513),
            topLeft = Offset(x - 25f, y + 10f),
            size = Size(50f, 20f)
        )
        
        // Draw mother hen
        drawBird(x, y, isLarge = true, Color.White)
        
        // Draw chicks around mother
        nursery.chicks.forEachIndexed { i, chick ->
            val chickX = x + (chick.position.x * width * 0.5f)
            val chickY = y + (chick.position.y * height * 0.5f) + 30f
            drawChick(chickX, chickY)
        }
    }
}

private fun DrawScope.drawFreeRangeBirds(birds: List<VisualBird>, width: Float, height: Float) {
    birds.forEach { bird ->
        val x = bird.position.x * width
        val y = bird.position.y * height
        drawBird(x, y, isLarge = false, Color.White)
    }
}

private fun DrawScope.drawGrowOutBirds(birds: List<VisualBird>, width: Float, height: Float) {
    birds.forEach { bird ->
        val x = bird.position.x * width
        val y = bird.position.y * height
        drawBird(x, y, isLarge = false, Color(0xFFFFA500)) // Orange tint for grow-out
    }
}

private fun DrawScope.drawReadyBirds(birds: List<VisualBird>, width: Float, height: Float) {
    birds.forEach { bird ->
        val x = bird.position.x * width
        val y = bird.position.y * height
        drawBird(x, y, isLarge = true, Color.White)
        
        // Gold star above
        drawGoldStar(x, y - 50f)
        
        // Weight label
        bird.weightText?.let { weight ->
            // Weight text would be drawn with native text API
        }
    }
}

private fun DrawScope.drawMarketBirds(birds: List<VisualBird>, width: Float, height: Float) {
    birds.take(4).forEachIndexed { i, bird ->
        val x = width * 0.7f + (i % 2) * 30f
        val y = height * 0.72f + (i / 2) * 20f
        drawBird(x, y, isLarge = false, Color(0xFFD2691E))
    }
}

private fun DrawScope.drawBird(x: Float, y: Float, isLarge: Boolean, color: Color) {
    val size = if (isLarge) 30f else 20f
    
    // Body (oval)
    drawOval(
        color = color,
        topLeft = Offset(x - size/2, y - size/3),
        size = Size(size, size * 0.7f)
    )
    
    // Head
    drawCircle(
        color = color,
        radius = size * 0.3f,
        center = Offset(x + size * 0.3f, y - size * 0.3f)
    )
    
    // Comb (red)
    drawCircle(
        color = Color(0xFFDC143C),
        radius = size * 0.15f,
        center = Offset(x + size * 0.35f, y - size * 0.5f)
    )
    
    // Beak
    val beakPath = Path().apply {
        moveTo(x + size * 0.5f, y - size * 0.25f)
        lineTo(x + size * 0.7f, y - size * 0.2f)
        lineTo(x + size * 0.5f, y - size * 0.15f)
        close()
    }
    drawPath(beakPath, Color(0xFFFFA500))
    
    // Legs
    drawLine(
        Color(0xFFFFA500),
        Offset(x - 5f, y + size * 0.2f),
        Offset(x - 8f, y + size * 0.5f),
        strokeWidth = 2f
    )
    drawLine(
        Color(0xFFFFA500),
        Offset(x + 5f, y + size * 0.2f),
        Offset(x + 8f, y + size * 0.5f),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawChick(x: Float, y: Float) {
    // Small yellow chick
    drawCircle(
        color = Color(0xFFFFEB3B),
        radius = 8f,
        center = Offset(x, y)
    )
    
    // Tiny beak
    drawCircle(
        color = Color(0xFFFFA500),
        radius = 2f,
        center = Offset(x + 8f, y - 2f)
    )
}

private fun DrawScope.drawGoldStar(x: Float, y: Float) {
    // Simplified gold star
    drawRoundRect(
        color = Color(0xFFFFD700),
        topLeft = Offset(x - 20f, y - 20f),
        size = Size(40f, 40f),
        cornerRadius = CornerRadius(8f)
    )
    
    // Star shape inside (simplified as diamond)
    val starPath = Path().apply {
        moveTo(x, y - 15f)
        lineTo(x + 12f, y)
        lineTo(x, y + 15f)
        lineTo(x - 12f, y)
        close()
    }
    drawPath(starPath, Color.White)
}

@Composable
private fun EmptyFarmState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.Landscape,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF4CAF50)
        )
        Text(
            "Your Digital Farm is Empty",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            "Add your first flock to see your farm come to life!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Button(onClick = { /* Navigate to add product */ }) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add Birds")
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
            containerColor = Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.EggAlt,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(32.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Eggs to Log!",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${unitsNeedingLogs.size} breeding ${if (unitsNeedingLogs.size == 1) "unit needs" else "units need"} egg logging",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                onClick = { unitsNeedingLogs.firstOrNull()?.let { onLogEggs(it) } },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                )
            ) {
                Text("Log Now")
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4FC3F7).copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
            
            // Stats
            Text(
                "Weight: ${bird.weightText ?: "N/A"}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Age: ${bird.ageText}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            bird.breed?.let {
                Text(
                    "Breed: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Details")
                }
                Button(
                    onClick = onListForSale,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    )
                ) {
                    Icon(Icons.Default.Sell, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("List for Sale", color = Color.Black)
                }
            }
        }
    }
}
