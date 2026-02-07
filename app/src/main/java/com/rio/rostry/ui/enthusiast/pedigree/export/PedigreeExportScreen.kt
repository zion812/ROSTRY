package com.rio.rostry.ui.enthusiast.pedigree.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.ui.enthusiast.pedigree.PedigreePdfGenerator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedigreeExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: PedigreeExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Export Pedigree") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Preview Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f / 1.414f) // A4 aspect ratio
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        PedigreeCertificatePreview(
                            pedigreeTree = uiState.pedigreeTree!!,
                            showBreederInfo = uiState.showBreederInfo,
                            showDate = uiState.showDate
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uiState.showBreederInfo,
                                onCheckedChange = { viewModel.toggleBreederInfo(it) }
                            )
                            Text("Breeder Info")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uiState.showDate,
                                onCheckedChange = { viewModel.toggleDate(it) }
                            )
                            Text("Date")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                val generator = PedigreePdfGenerator(context)
                                val fileName = generator.generateAndSavePdf(
                                    uiState.pedigreeTree!!.bird.name,
                                    uiState.pedigreeTree!!
                                )
                                if (fileName != null) {
                                    // Show success
                                }
                            }
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("PDF")
                        }

                        OutlinedButton(onClick = { /* Share Logic */ }) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedigreeCertificatePreview(
    pedigreeTree: PedigreeTree,
    showBreederInfo: Boolean,
    showDate: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val paint = Paint()

        drawContext.canvas.nativeCanvas.apply {
            // Background
            drawColor(AndroidColor.WHITE)

            // Border
            paint.color = AndroidColor.parseColor("#4A8C2A")
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 10f
            drawRect(20f, 20f, width - 20f, height - 20f, paint)

            paint.strokeWidth = 2f
            drawRect(30f, 30f, width - 30f, height - 30f, paint)

            // Title
            paint.style = Paint.Style.FILL
            paint.textSize = width * 0.08f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            paint.color = AndroidColor.BLACK
            drawText("CERTIFICATE OF PEDIGREE", width / 2f, height * 0.15f, paint)

            // Bird Name
            paint.textSize = width * 0.06f
            paint.color = AndroidColor.parseColor("#1B5E20")
            drawText(pedigreeTree.bird.name, width / 2f, height * 0.25f, paint)

            // Breed
            pedigreeTree.bird.breed?.let {
                paint.textSize = width * 0.04f
                paint.color = AndroidColor.DKGRAY
                paint.typeface = Typeface.DEFAULT
                drawText("Breed: $it", width / 2f, height * 0.30f, paint)
            }

            // Tree Visualization (Simplified for preview)
            // ... (Add drawing logic similar to PDF generator but scaled)
             val startX = width * 0.1f
             val startY = height * 0.5f
             val levelWidth = width * 0.25f
             
             drawNodePreview(this, pedigreeTree, startX, startY, 0, levelWidth, paint)

            // Footer
            if (showDate) {
                paint.textSize = width * 0.03f
                paint.color = AndroidColor.GRAY
                paint.textAlign = Paint.Align.CENTER
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                drawText("Generated on $date", width / 2f, height - 50f, paint)
            }
        }
    }
}

private fun drawNodePreview(
    canvas: android.graphics.Canvas,
    tree: PedigreeTree,
    x: Float,
    y: Float,
    level: Int,
    levelWidth: Float,
    paint: Paint
) {
    val childY = y
    val nextX = x + levelWidth
    val offset = 100f / (level + 1) // Reduce spacing as we go deeper

    val sireY = y - offset
    val damY = y + offset

    if (level < 2) { // Max depth 2 for preview
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = AndroidColor.BLACK
        
        if (tree.sire != null) {
            canvas.drawLine(x + 50, y, nextX, sireY, paint)
            drawNodePreview(canvas, tree.sire, nextX, sireY, level + 1, levelWidth, paint)
        }
        if (tree.dam != null) {
            canvas.drawLine(x + 50, y, nextX, damY, paint)
             drawNodePreview(canvas, tree.dam, nextX, damY, level + 1, levelWidth, paint)
        }
    }
    
    // Draw Box
    paint.style = Paint.Style.FILL
    paint.color = if (level == 0) AndroidColor.parseColor("#E8F5E9") else AndroidColor.parseColor("#FAFAFA")
    val rectHeight = 30f
    val rectWidth = 100f
    canvas.drawRect(x, y - rectHeight / 2, x + rectWidth, y + rectHeight / 2, paint)

    paint.style = Paint.Style.STROKE
    paint.color = AndroidColor.GRAY
    canvas.drawRect(x, y - rectHeight / 2, x + rectWidth, y + rectHeight / 2, paint)

    // Text
    paint.style = Paint.Style.FILL
    paint.color = AndroidColor.BLACK
    paint.textAlign = Paint.Align.LEFT
    paint.textSize = 10f
    val name = tree.bird.name
    canvas.drawText(if (name.length > 10) name.take(8) + ".." else name, x + 5, y + 5, paint)
}
