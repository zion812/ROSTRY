package com.rio.rostry.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.StorageQuotaEntity
import com.rio.rostry.ui.components.EmptyState
import com.rio.rostry.ui.components.ErrorBanner
import com.rio.rostry.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageQuotaScreen(
    onNavigateBack: () -> Unit,
    viewModel: StorageQuotaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Storage") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is StorageQuotaUiState.Loading -> LoadingScreen()
                is StorageQuotaUiState.Error -> ErrorBanner(state.message)
                is StorageQuotaUiState.Success -> StorageQuotaContent(
                    quota = state.quota,
                    roleName = state.roleName,
                    onExportData = { viewModel.exportData() }
                )
            }
        }
    }
}

@Composable
fun StorageQuotaContent(
    quota: StorageQuotaEntity,
    roleName: String,
    onExportData: () -> Unit
) {
    val scrollState = rememberScrollState()
    val usedPercentage = if (quota.quotaBytes > 0) (quota.usedBytes.toFloat() / quota.quotaBytes) else 0f
    val animatedProgress by animateFloatAsState(targetValue = usedPercentage.coerceIn(0f, 1f))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Quota Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Usage Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(roleName, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    color = when {
                        usedPercentage > 0.9f -> MaterialTheme.colorScheme.error
                        usedPercentage > 0.8f -> Color(0xFFFFA000) // Amber
                        else -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${formatBytes(quota.usedBytes)} of ${formatBytes(quota.quotaBytes)} used",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${(usedPercentage * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Breakdown Section
        Text(
            text = "Storage Breakdown",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )

        // For now we don't have separate counters for images/docs in StorageQuotaEntity
        // but it's planned. I'll show what we have.
        StorageItem(
            icon = Icons.Default.CloudQueue,
            label = "Total Cloud Storage",
            size = formatBytes(quota.usedBytes),
            color = MaterialTheme.colorScheme.primary
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Upgrade CTA if close to limit
        if (usedPercentage > 0.8f) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Upgrade,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Running low on space?",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Upgrade your plan to get more cloud storage for your farm data.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Info Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = ButtonDefaults.outlinedButtonBorder
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "About ROSTRY Storage",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "ROSTRY uses a hybrid storage model. Operational data is stored locally for offline access, while verification documents, profile images, and marketplace listings are backed up to the cloud. Quotas help us keep the platform sustainable.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Export Button
        Button(
            onClick = onExportData,
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export My Data")
        }

        Text(
            text = "Generates a backup of your farm data, products, and profile in JSON format (ZIP archive).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun StorageItem(
    icon: ImageVector,
    label: String,
    size: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        Text(size, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
    val pre = "KMGTPE"[exp - 1]
    return String.format("%.1f %sB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
}
