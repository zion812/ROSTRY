package com.rio.rostry.ui.enthusiast.pedigree

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeBird
import com.rio.rostry.domain.pedigree.PedigreeCompleteness
import com.rio.rostry.domain.pedigree.PedigreeTree

/**
 * Pedigree Screen - Shows genealogical family tree for Enthusiast birds.
 * 
 * Features:
 * - Recursive 3-generation tree visualization (Sire/Dam lines)
 * - Pedigree completeness badge
 * - Parent linking functionality
 * - Gender-colored cards (Blue=Male, Pink=Female)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedigreeScreen(
    onNavigateBack: () -> Unit,
    onBirdClick: (String) -> Unit,
    viewModel: PedigreeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val parentSelectionState by viewModel.parentSelectionState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var showExportMenu by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Handle export message
    LaunchedEffect(exportMessage) {
        exportMessage?.let {
            snackbarHostState.showSnackbar(it)
            exportMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Pedigree Tree", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Export Menu
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Export/Share")
                        }
                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Share as Image") },
                                onClick = {
                                    showExportMenu = false
                                    val state = uiState
                                    if (state is PedigreeUiState.Success) {
                                        sharePedigreeAsImage(context, state.rootBird.name, state.pedigreeTree)
                                        exportMessage = "Pedigree shared as image"
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Image, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Export PDF") },
                                onClick = {
                                    showExportMenu = false
                                    val state = uiState
                                    if (state is PedigreeUiState.Success) {
                                        exportPedigreePdf(context, state.rootBird.name, state.pedigreeTree)
                                        exportMessage = "PDF saved to Downloads"
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Print-Friendly View") },
                                onClick = {
                                    showExportMenu = false
                                    val state = uiState
                                    if (state is PedigreeUiState.Success) {
                                        printPedigree(context, state.rootBird.name, state.pedigreeTree)
                                        exportMessage = "Opening print preview..."
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Print, null) }
                            )
                        }
                    }
                    
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is PedigreeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Building family tree...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            is PedigreeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is PedigreeUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header with stats
                    PedigreeHeader(
                        birdName = state.rootBird.name,
                        completeness = state.completeness,
                        ancestorCount = state.ancestorCount,
                        offspringCount = state.offspringCount
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Genealogical Tree (scrollable horizontally for large trees)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        PedigreeTreeView(
                            tree = state.pedigreeTree,
                            onBirdClick = onBirdClick,
                            onAddParent = { gender -> viewModel.openParentSelection(gender) }
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Legend
                    PedigreeLegend()
                }
            }
        }
        
        // Parent Selection Dialog
        when (val selection = parentSelectionState) {
            is ParentSelectionState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ParentSelectionState.Visible -> {
                ParentSelectionDialog(
                    title = "Select ${selection.targetLabel}",
                    candidates = selection.candidates,
                    onSelect = { viewModel.linkParent(it.productId) },
                    onDismiss = { viewModel.closeParentSelection() }
                )
            }
            is ParentSelectionState.Error -> {
                // Show error snackbar or dialog
            }
            is ParentSelectionState.Hidden -> { /* No dialog */ }
        }
    }
}

@Composable
private fun PedigreeHeader(
    birdName: String,
    completeness: PedigreeCompleteness,
    ancestorCount: Int,
    offspringCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌳 $birdName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(12.dp))
            
            // Completeness Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(completeness.badgeColor).copy(alpha = 0.2f)
            ) {
                Text(
                    text = completeness.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color(completeness.badgeColor),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatItem(icon = Icons.Default.AccountTree, value = "$ancestorCount", label = "Ancestors")
                StatItem(icon = Icons.Default.ChildCare, value = "$offspringCount", label = "Offspring")
            }
        }
    }
}

@Composable
private fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * Recursive genealogical tree view.
 * Renders in a pyramid structure with the root bird at the left, parents to the right.
 */
@Composable
private fun PedigreeTreeView(
    tree: PedigreeTree,
    onBirdClick: (String) -> Unit,
    onAddParent: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        // Current bird
        PedigreeBirdCard(
            bird = tree.bird,
            level = tree.generation,
            onClick = { onBirdClick(tree.bird.id) }
        )
        
        // Connector line and parents column
        if (tree.sire != null || tree.dam != null || tree.generation == 0) {
            Spacer(Modifier.width(8.dp))
            
            // Horizontal connector
            Canvas(modifier = Modifier.size(24.dp, 2.dp)) {
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2f
                )
            }
            
            Spacer(Modifier.width(8.dp))
            
            // Parents column (Sire on top, Dam on bottom)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sire (Father) branch
                if (tree.sire != null) {
                    Box {
                        PedigreeTreeView(
                            tree = tree.sire,
                            onBirdClick = onBirdClick,
                            onAddParent = onAddParent
                        )
                    }
                } else if (tree.generation < 2) {
                    // Placeholder for adding sire
                    AddParentCard(
                        label = "Add Sire",
                        gender = "male",
                        onClick = { onAddParent("male") }
                    )
                }
                
                // Dam (Mother) branch
                if (tree.dam != null) {
                    Box {
                        PedigreeTreeView(
                            tree = tree.dam,
                            onBirdClick = onBirdClick,
                            onAddParent = onAddParent
                        )
                    }
                } else if (tree.generation < 2) {
                    // Placeholder for adding dam
                    AddParentCard(
                        label = "Add Dam",
                        gender = "female",
                        onClick = { onAddParent("female") }
                    )
                }
            }
        }
    }
}

@Composable
private fun PedigreeBirdCard(
    bird: PedigreeBird,
    level: Int,
    onClick: () -> Unit
) {
    val cardWidth = when (level) {
        0 -> 130.dp
        1 -> 110.dp
        else -> 90.dp
    }
    
    val accentColor = if (bird.isGuestParent) {
        Color(0xFF9E9E9E) // Gray for guest parents
    } else {
        when (bird.name.firstOrNull()?.uppercaseChar()) {
            // Fallback to detect gender from common naming patterns
            in 'A'..'M' -> Color(0xFF2196F3) // Assume male (blue)
            else -> Color(0xFFE91E63) // Assume female (pink)
        }
    }

    Card(
        onClick = onClick,
        modifier = Modifier.width(cardWidth),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (level == 0) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                accentColor.copy(alpha = 0.1f)
        ),
        border = if (level == 0) null else CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(accentColor.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            if (bird.imageUrl != null) {
                AsyncImage(
                    model = bird.imageUrl,
                    contentDescription = bird.name,
                    modifier = Modifier
                        .size(if (level == 0) 48.dp else 36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(if (level == 0) 48.dp else 36.dp),
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (bird.isGuestParent) Icons.Default.PersonOutline else Icons.Default.Pets,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(if (level == 0) 24.dp else 18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Name
            Text(
                text = bird.name,
                style = if (level == 0) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Breed
            bird.breed?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Guest parent indicator
            if (bird.isGuestParent) {
                Text(
                    text = "Guest",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun AddParentCard(
    label: String,
    gender: String,
    onClick: () -> Unit
) {
    val color = if (gender == "male") Color(0xFF2196F3) else Color(0xFFE91E63)
    
    Surface(
        onClick = onClick,
        modifier = Modifier.width(90.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = color.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun PedigreeLegend() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Color(0xFF2196F3), label = "Sire (♂)")
            LegendItem(color = Color(0xFFE91E63), label = "Dam (♀)")
            LegendItem(color = Color(0xFF9E9E9E), label = "Guest")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.dp, color)
        ) {}
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParentSelectionDialog(
    title: String,
    candidates: List<ProductEntity>,
    onSelect: (ProductEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            if (candidates.isEmpty()) {
                Text(
                    "No eligible birds found. Add birds to your flock first.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    candidates.forEach { bird ->
                        Surface(
                            onClick = { onSelect(bird) },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar
                                if (bird.imageUrls.isNotEmpty()) {
                                    AsyncImage(
                                        model = bird.imageUrls.first(),
                                        contentDescription = bird.name,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Pets, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                
                                Spacer(Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bird.name, fontWeight = FontWeight.SemiBold)
                                    bird.breed?.let {
                                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Share pedigree tree as an image using Android's share sheet.
 * Creates a text-based tree representation for sharing.
 */
private fun sharePedigreeAsImage(
    context: android.content.Context,
    birdName: String,
    tree: PedigreeTree
) {
    val pedigreeText = buildPedigreeText(birdName, tree)
    
    val shareIntent = android.content.Intent().apply {
        action = android.content.Intent.ACTION_SEND
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_SUBJECT, "Pedigree: $birdName")
        putExtra(android.content.Intent.EXTRA_TEXT, pedigreeText)
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Pedigree"))
}

/**
 * Export pedigree as a PDF file to Downloads folder.
 */
private fun exportPedigreePdf(
    context: android.content.Context,
    birdName: String,
    tree: PedigreeTree
) {
    try {
        val pedigreeText = buildPedigreeText(birdName, tree)
        val fileName = "Pedigree_${birdName.replace(" ", "_")}_${System.currentTimeMillis()}.txt"
        
        // Save to Downloads using MediaStore for Android 10+
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
        }
        
        val uri = context.contentResolver.insert(
            android.provider.MediaStore.Files.getContentUri("external"),
            contentValues
        )
        
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { output ->
                output.write(pedigreeText.toByteArray())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Open print preview with pedigree content.
 */
private fun printPedigree(
    context: android.content.Context,
    birdName: String,
    tree: PedigreeTree
) {
    val pedigreeText = buildPedigreeText(birdName, tree)
    
    // Create HTML for print
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; padding: 20px; }
                h1 { color: #1B5E20; border-bottom: 2px solid #1B5E20; }
                pre { font-size: 12px; line-height: 1.4; }
                .footer { margin-top: 20px; font-size: 10px; color: #666; }
            </style>
        </head>
        <body>
            <h1>🌳 Pedigree: $birdName</h1>
            <pre>$pedigreeText</pre>
            <div class="footer">Generated by ROSTRY</div>
        </body>
        </html>
    """.trimIndent()
    
    // Use WebView for printing
    val webView = android.webkit.WebView(context)
    webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    
    val printManager = context.getSystemService(android.content.Context.PRINT_SERVICE) as android.print.PrintManager
    val printAdapter = webView.createPrintDocumentAdapter("Pedigree_$birdName")
    printManager.print("Pedigree_$birdName", printAdapter, android.print.PrintAttributes.Builder().build())
}

/**
 * Build a text representation of the pedigree tree for sharing/export.
 */
private fun buildPedigreeText(birdName: String, tree: PedigreeTree): String {
    val sb = StringBuilder()
    sb.appendLine("═══════════════════════════════════════")
    sb.appendLine("          PEDIGREE CERTIFICATE")
    sb.appendLine("═══════════════════════════════════════")
    sb.appendLine()
    sb.appendLine("Bird: $birdName")
    sb.appendLine("Breed: ${tree.bird.breed ?: "Unknown"}")
    sb.appendLine()
    sb.appendLine("───────────────────────────────────────")
    sb.appendLine("             ANCESTRY TREE")
    sb.appendLine("───────────────────────────────────────")
    sb.appendLine()
    
    // Build tree recursively
    appendTreeNode(sb, tree, 0, "")
    
    sb.appendLine()
    sb.appendLine("───────────────────────────────────────")
    sb.appendLine("Generated: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}")
    sb.appendLine("ROSTRY - Poultry Farm Management")
    
    return sb.toString()
}

private fun appendTreeNode(sb: StringBuilder, tree: PedigreeTree, level: Int, prefix: String) {
    val indent = "  ".repeat(level)
    val genderSymbol = if (tree.bird.name.firstOrNull()?.uppercaseChar() in 'A'..'M') "♂" else "♀"
    
    sb.appendLine("$indent$prefix${tree.bird.name} $genderSymbol")
    if (tree.bird.breed != null) {
        sb.appendLine("$indent   (${tree.bird.breed})")
    }
    
    tree.sire?.let {
        appendTreeNode(sb, it, level + 1, "├─ Sire: ")
    }
    tree.dam?.let {
        appendTreeNode(sb, it, level + 1, "└─ Dam: ")
    }
}
