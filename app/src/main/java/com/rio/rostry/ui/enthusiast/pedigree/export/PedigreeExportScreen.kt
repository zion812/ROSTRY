package com.rio.rostry.ui.enthusiast.pedigree.export

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.pedigree.PedigreeTree
import com.rio.rostry.utils.export.PedigreeRenderer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedigreeExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: PedigreeExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Handle messages
    LaunchedEffect(uiState.exportMessage, uiState.error) {
        val msg = uiState.exportMessage ?: uiState.error
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (uiState.pedigreeTree != null) {
                        // Preview Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.414f) // Landscape A4 ratio approx
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            PedigreeCertificatePreview(
                                pedigreeTree = uiState.pedigreeTree!!
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Preview",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                                onClick = { viewModel.exportAsPdf() },
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Save PDF")
                            }

                            FilledTonalButton(
                                onClick = { viewModel.exportAsImage() },
                                modifier = Modifier.weight(1f).padding(start = 8.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Save Image")
                            }
                        }
                    } else {
                        // Error state if tree is null but loading is false
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Failed to load pedigree data", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PedigreeCertificatePreview(
    pedigreeTree: PedigreeTree
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawContext.canvas.nativeCanvas.apply {
            // Use shared renderer logic
            val renderer = PedigreeRenderer(this, size.width.toInt(), size.height.toInt())
            renderer.draw(pedigreeTree)
        }
    }
}

