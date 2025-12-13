package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalFarmScreen(
    viewModel: DigitalFarmViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onManageBird: (String) -> Unit,
    onViewLineage: (String) -> Unit,
    onListForSale: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedBird by remember { mutableStateOf<VisualBird?>(null) }
    val sheetState = rememberModalBottomSheetState()
    
    // Filter State
    var activeFilter by remember { mutableStateOf(FarmFilter.ALL) }
    
    // Share Logic
    val picture = remember { android.graphics.Picture() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Digital Farm") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {
                    // Share Logic
                    try {
                        if (picture.width > 0 && picture.height > 0) {
                            val bitmap = android.graphics.Bitmap.createBitmap(
                                picture.width, picture.height, android.graphics.Bitmap.Config.ARGB_8888
                            )
                            val canvas = android.graphics.Canvas(bitmap)
                            canvas.drawColor(android.graphics.Color.WHITE) // Background
                            canvas.drawPicture(picture)
                            
                            // Save and Share
                            val filename = "farm_snapshot_${System.currentTimeMillis()}.png"
                            val file = java.io.File(context.cacheDir, filename)
                            java.io.FileOutputStream(file).use { out ->
                                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                            }
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context, "${context.packageName}.fileprovider", file
                            )
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                putExtra(android.content.Intent.EXTRA_TEXT, "Check out my digital flock on ROSTRY!")
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share Farm"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Share Farm")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is DigitalFarmUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DigitalFarmUiState.Error -> {
                    Text("Error loading farm data", modifier = Modifier.align(Alignment.Center))
                }
                is DigitalFarmUiState.Empty -> {
                    Text("Your farm is empty! Add some birds to see them here.", modifier = Modifier.align(Alignment.Center))
                }
                is DigitalFarmUiState.Success -> {
                    val allBirds = state.birds
                    val filteredBirds = remember(allBirds, activeFilter) {
                        when (activeFilter) {
                            FarmFilter.ALL -> allBirds
                            FarmFilter.SICK -> allBirds.filter { it.isSick }
                            FarmFilter.BREEDERS -> allBirds.filter { it.stage == BirdStage.BREEDER }
                        }
                    }
                    
                    // Zone Counts (always based on ALL birds to show accurate capacity)
                    val coopCount = allBirds.count { it.location == "Coop" }
                    val quarantineCount = allBirds.count { it.location == "Quarantine" }
                    val freeRangeCount = allBirds.count { it.location == "Free Range" }
                    val sickCount = allBirds.count { it.isSick }

                    FarmCanvas(
                        birds = filteredBirds,
                        picture = picture,
                        onBirdClick = { bird ->
                            selectedBird = bird
                        }
                    )
                    
                    // --- OVERLAYS ---
                    
                    // 1. Filter Chips (Top Center)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = activeFilter == FarmFilter.ALL,
                            onClick = { activeFilter = FarmFilter.ALL },
                            label = { Text("All") }
                        )
                        FilterChip(
                            selected = activeFilter == FarmFilter.SICK,
                            onClick = { activeFilter = FarmFilter.SICK },
                            label = { Text("Sick (${sickCount})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                        FilterChip(
                            selected = activeFilter == FarmFilter.BREEDERS,
                            onClick = { activeFilter = FarmFilter.BREEDERS },
                            label = { Text("Breeders") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFD700).copy(alpha = 0.5f),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }

                    // 2. Zone Dashboards
                    // Coop (Top Left)
                    ZoneStatusCard(
                        name = "COOP",
                        count = coopCount,
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                    )
                    
                    // Quarantine (Top Right)
                    ZoneStatusCard(
                        name = "QUARANTINE",
                        count = quarantineCount,
                        isAlert = quarantineCount > 0,
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    )
                    
                    // Free Range (Bottom Center - above bottom sheet area)
                    ZoneStatusCard(
                        name = "FREE RANGE",
                        count = freeRangeCount,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
                    )
                }
            }
        }
        
        if (selectedBird != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedBird = null },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = selectedBird?.label ?: "",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = selectedBird?.details ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    // Status Alert
                    if (selectedBird?.status == BirdHealthStatus.VACCINE_DUE) {
                        Text("⚠️ Vaccine Due!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelLarge)
                    }
                    if (selectedBird?.status == BirdHealthStatus.READY_TO_SELL) {
                        Text("💰 Ready to Sell!", color = Color(0xFFD4AF37), style = MaterialTheme.typography.labelLarge)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedBird?.id?.let { onManageBird(it) }
                                selectedBird = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Manage")
                        }
                        
                        // Contextual Action: List for Sale
                        if (selectedBird?.status == BirdHealthStatus.READY_TO_SELL) {
                            val isVerified = (uiState as? DigitalFarmUiState.Success)?.isVerified == true
                            Button(
                                onClick = {
                                    if (isVerified) {
                                        selectedBird?.id?.let { onListForSale(it) }
                                        selectedBird = null
                                    }
                                },
                                enabled = isVerified,
                                modifier = Modifier.weight(1f),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = if (isVerified) Color(0xFFD4AF37) else Color.Gray,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(if (isVerified) "List for Sale" else "Verify to Sell")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    selectedBird?.id?.let { onViewLineage(it) }
                                    selectedBird = null
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Lineage")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

enum class FarmFilter { ALL, SICK, BREEDERS }

@Composable
fun ZoneStatusCard(
    name: String,
    count: Int,
    isAlert: Boolean = false,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        color = if (isAlert) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f) 
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isAlert) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = if (isAlert) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// --- ISOMETRIC FARM IMPLEMENTATION ---

// Constants for Isometric View
private const val GRID_ROWS = 20
private const val GRID_COLS = 20
private const val TILE_SIZE = 40f // Base tile size (arbitrary units, scaled by view)

@Composable
fun FarmCanvas(
    birds: List<VisualBird>,
    picture: android.graphics.Picture,
    onBirdClick: (VisualBird) -> Unit
) {
    // Transform State
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    // Bounds for panning
    // We want to keep the farm somewhat centered.
    // The farm center is roughly (0, 0) after initial offset? 
    // Let's rely on user freedom for now, similar to Google Maps.

    // Calculate Screen Center (approx) to center the map initially
    // We'll handle this in the draw phase or init.
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f) // Zoom Limits
                    // Rotate pan vector if we want to align with camera, but standard pan is fine.
                    // Actually, standard pan moves the viewport.
                    offset += pan
                }
            }
            .pointerInput(birds, scale, offset) {
                detectTapGestures { tapOffset ->
                    // Hit Test Logic
                    // We need to inverse the transform: (Tap - Offset) / Scale
                    val localTap = (tapOffset - offset) / scale
                    
                    // Center of the map in local coords
                    val mapCenterX = size.width / 2f
                    val mapCenterY = size.height / 2f // Adjusting generic center is tricky without draw scope size.
                    // Let's do hit testing INSIDE the canvas loop or REPLICATE the draw transforms.
                    // Replicating is safer.
                    
                    // Best way: Iterate birds, project their ISO coordinates to Screen coordinates using current Scale/Offset.
                    // Hit test against the screen coordinates.
                    
                    // Sort birds by Y (depth) to hit top ones first
                    val sortedBirds = birds.sortedByDescending { it.y } // Draw order is Y asc, Hit order Y desc
                    
                    val screenWidth = size.width
                    val screenHeight = size.height
                    val mapCenter = Offset(screenWidth / 2f, screenHeight / 2f)

                    for (bird in sortedBirds) {
                        // 1. Grid Coords
                        val gridX = bird.x * GRID_COLS
                        val gridY = bird.y * GRID_ROWS
                        
                        // 2. Iso Coords (Pre-scale)
                        // Iso conversion: 
                        // x' = (x - y) * cos(30)
                        // y' = (x + y) * sin(30)
                        // Simplified 2:1 ratio
                        val isoX = (gridX - gridY) * TILE_SIZE
                        val isoY = (gridX + gridY) * (TILE_SIZE / 2f)
                        
                        // 3. Apply Transform
                        val finalX = mapCenter.x + offset.x + (isoX * scale)
                        val finalY = mapCenter.y + offset.y + (isoY * scale)
                        
                        val hitRadius = bird.sizeRadius * scale * 2f // Larger hit area
                        val dist = (tapOffset - Offset(finalX, finalY)).getDistance()
                        
                        if (dist <= hitRadius) {
                            onBirdClick(bird)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val mapCenter = Offset(canvasWidth / 2f, canvasHeight / 2f)

            // 1. Draw to Screen
            drawFarmFrame(
                drawScope = this,
                birds = birds,
                scale = scale,
                panOffset = offset,
                mapCenter = mapCenter,
                forSharing = false
            )
        }
        
        // 2. Recording Logic (One-time or on-demand would be better, but we follow existing pattern)
        // To allow sharing the CURRENT view (WYSIWYG), we should use the current scale/offset.
        // Or if we want to share the "Whole Farm", we reset scale.
        // Let's share the "Whole Farm" centered.
        
        // Note: We can't easily run this every frame without perf cost.
        // User's original code did this. We will optimize: Only record if needed? 
        // Can't know when user clicks buttons. 
        // We'll stick to 'draw' but maybe throttle or rely on the Fact that `picture` is just a recording buffer.
        
        // Actual fix: We create a Canvas for the picture and draw blindly.
        val recordingCanvas = picture.beginRecording(1080, 1920) // Fixed high res
        // Use a helper to draw on Native Canvas
        drawFarmOnNative(
            canvas = recordingCanvas,
            birds = birds,
            width = 1080f,
            height = 1920f
        )
        picture.endRecording()
    }
}

// --- RENDERING HELPERS ---

private fun drawFarmFrame(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    birds: List<VisualBird>,
    scale: Float,
    panOffset: Offset,
    mapCenter: Offset,
    forSharing: Boolean
) {
    drawScope.apply {
        // Transform Context
        // We apply manual transforms to coordinates to keep control
        
        // 1. DRAW GROUND (TILES)
        for (row in 0 until GRID_ROWS) {
            for (col in 0 until GRID_COLS) {
                // Determine Zone
                val isCoop = row < GRID_ROWS / 2 && col < GRID_COLS / 2
                val isQuarantine = row < GRID_ROWS / 2 && col >= GRID_COLS / 2
                // Free range is the rest
                
                val baseColor = when {
                    isCoop -> Color(0xFF8D6E63) // Brown
                    isQuarantine -> Color(0xFFEF9A9A) // Reddish
                    else -> Color(0xFFA5D6A7) // Green
                }
                
                // Checkerboard effect
                val color = if ((row + col) % 2 == 0) baseColor else baseColor.copy(alpha = 0.8f)

                // Calculate Iso Vertices
                val gridX = col.toFloat()
                val gridY = row.toFloat()
                
                val centerIsoX = (gridX - gridY) * TILE_SIZE
                val centerIsoY = (gridX + gridY) * (TILE_SIZE / 2f)
                
                val finalX = mapCenter.x + panOffset.x + (centerIsoX * scale)
                val finalY = mapCenter.y + panOffset.y + (centerIsoY * scale)
                
                // Draw Tile (Diamond)
                val halfW = TILE_SIZE * scale
                val halfH = (TILE_SIZE / 2f) * scale
                
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(finalX, finalY - halfH) // Top
                    lineTo(finalX + halfW, finalY) // Right
                    lineTo(finalX, finalY + halfH) // Bottom
                    lineTo(finalX - halfW, finalY) // Left
                    close()
                }
                
                drawPath(path, color)
                drawPath(path, Color.Black.copy(alpha=0.05f), style = Stroke(width = 1f * scale))
            }
        }
        
        // 2. DRAW BIRDS (Sorted by Y for depth)
        // Sort by (row + col) or just Y logic
        val sortedBirds = birds.sortedBy { it.x + it.y }
        
        sortedBirds.forEach { bird ->
            val gridX = bird.x * GRID_COLS
            val gridY = bird.y * GRID_ROWS
            
            val isoX = (gridX - gridY) * TILE_SIZE
            val isoY = (gridX + gridY) * (TILE_SIZE / 2f)
            
            val finalX = mapCenter.x + panOffset.x + (isoX * scale)
            val finalY = mapCenter.y + panOffset.y + (isoY * scale)
            
            val radius = bird.sizeRadius * scale
            
            // SHADOW (at base)
            drawOval(
                color = Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(finalX - radius, finalY - radius/2),
                size = Size(radius * 2, radius),
            )
            
            // POLE (Lift effect)
            val liftHeight = radius * 2f
            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = Offset(finalX, finalY),
                end = Offset(finalX, finalY - liftHeight),
                strokeWidth = 2f * scale
            )
            
            // BIRD BODY (Billboard at top of pole)
            val bodyCenter = Offset(finalX, finalY - liftHeight)
            
            // Status Glow
             if (bird.status == BirdHealthStatus.READY_TO_SELL) {
                 drawCircle(
                     color = Color(0xFFFFD700).copy(alpha = 0.6f),
                     radius = radius * 1.5f,
                     center = bodyCenter
                 )
             }
            
            drawCircle(
                color = bird.color,
                radius = radius,
                center = bodyCenter
            )
            
            // Status Icon/Dot
            if (bird.isSick) {
                drawCircle(Color.Red, radius * 0.4f, Offset(bodyCenter.x + radius*0.6f, bodyCenter.y - radius*0.6f))
            } 
            else if (bird.stage == BirdStage.CHICK) {
                drawCircle(Color(0xFFFFE0B2), radius * 0.4f, bodyCenter) // Yellow center for chick
            }
            
            // ID Label (Only if zoomed in)
            if (scale > 1.5f) {
                // Text drawing in Compose Canvas is tricky without nativeCanvas access or TextMeasurer
                // We'll skip text label for now or use simplified indicator
                // Maybe a small ring for selected?
            }
        }
    }
}

// Native Canvas Drawing for Sharing (Simplified Iso)
private fun drawFarmOnNative(
    canvas: android.graphics.Canvas,
    birds: List<VisualBird>,
    width: Float,
    height: Float
) {
    val paint = Paint().apply { isAntiAlias = true }
    
    // Fill BG
    canvas.drawColor(android.graphics.Color.parseColor("#F5F5F5"))
    
    val centerX = width / 2f
    val centerY = height / 2f
    val scale = 2f // Fixed scale for share
    val tileSize = TILE_SIZE * scale
    
    // Tiles
    for (row in 0 until GRID_ROWS) {
        for (col in 0 until GRID_COLS) {
            val isCoop = row < GRID_ROWS / 2 && col < GRID_COLS / 2
            val isQuarantine = row < GRID_ROWS / 2 && col >= GRID_COLS / 2
            
            paint.color = when {
                isCoop -> android.graphics.Color.parseColor("#8D6E63")
                isQuarantine -> android.graphics.Color.parseColor("#EF9A9A")
                else -> android.graphics.Color.parseColor("#A5D6A7")
            }
            if ((col+row)%2!=0) paint.alpha = 200
            
            val gridX = col.toFloat()
            val gridY = row.toFloat()
            val isoX = (gridX - gridY) * TILE_SIZE
            val isoY = (gridX + gridY) * (TILE_SIZE / 2f)
            
            val finalX = centerX + (isoX * scale)
            val finalY = centerY + (isoY * scale)
            
            val path = android.graphics.Path()
            val halfW = tileSize
            val halfH = tileSize / 2f
            path.moveTo(finalX, finalY - halfH)
            path.lineTo(finalX + halfW, finalY)
            path.lineTo(finalX, finalY + halfH)
            path.lineTo(finalX - halfW, finalY)
            path.close()
            
            canvas.drawPath(path, paint)
        }
    }
    
    // Birds
    birds.sortedBy { it.x + it.y }.forEach { bird ->
        val gridX = bird.x * GRID_COLS
        val gridY = bird.y * GRID_ROWS
        val isoX = (gridX - gridY) * TILE_SIZE
        val isoY = (gridX + gridY) * (TILE_SIZE / 2f)
        val finalX = centerX + (isoX * scale)
        val finalY = centerY + (isoY * scale)
        val radius = bird.sizeRadius * scale
        
        paint.color = android.graphics.Color.argb(255, (bird.color.red*255).toInt(), (bird.color.green*255).toInt(), (bird.color.blue*255).toInt())
        canvas.drawCircle(finalX, finalY - radius*2, radius, paint) // Simplified stick
    }
}
