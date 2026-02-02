package com.rio.rostry.ui.admin.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGeneratorScreen(
    onNavigateBack: () -> Unit
) {
    var selectedReportType by remember { mutableStateOf<ReportType?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            snackbarHostState.showSnackbar("Report generated successfully!")
            showSuccess = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Generator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Select Report Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }

            items(ReportType.entries) { type ->
                ReportTypeCard(
                    type = type,
                    isSelected = selectedReportType == type,
                    onClick = { selectedReportType = type }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                Button(
                    onClick = {
                        isGenerating = true
                        scope.launch {
                            delay(1500)
                            isGenerating = false
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedReportType != null && !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary 
, strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Generating...")
                    } else {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Generate Report")
                    }
                }
            }
        }
    }
}

enum class ReportType(val label: String, val description: String, val icon: ImageVector, val color: Color) {
    USER_GROWTH("User Growth Report", "Registration trends, role distribution, activity", Icons.Default.People, Color(0xFF2196F3)),
    COMMERCE("Commerce Report", "Orders, revenue, top products and sellers", Icons.Default.Store, Color(0xFF4CAF50)),
    BIOSECURITY("Biosecurity Report", "Zone alerts, disease tracking, compliance", Icons.Default.Shield, Color(0xFFFF9800)),
    MORTALITY("Mortality Report", "Death rates, outbreaks, regional analysis", Icons.Default.TrendingDown, Color(0xFFD32F2F)),
    VERIFICATION("Verification Report", "KYC status, approval rates, pending reviews", Icons.Default.VerifiedUser, Color(0xFF9C27B0)),
    FULL_AUDIT("Full Audit Report", "Complete system audit with all activities", Icons.Default.Assessment, Color(0xFF607D8B))
}

@Composable
private fun ReportTypeCard(type: ReportType, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = if (isSelected) CardDefaults.cardColors(containerColor = type.color.copy(alpha = 0.15f))
                 else CardDefaults.cardColors()
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(type.icon, contentDescription = null, tint = type.color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(type.label, fontWeight = FontWeight.Bold)
                Text(type.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = type.color)
            }
        }
    }
}
