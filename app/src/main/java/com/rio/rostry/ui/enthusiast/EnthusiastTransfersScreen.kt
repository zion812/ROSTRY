package com.rio.rostry.ui.enthusiast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AssistChipDefaults
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.rio.rostry.ui.components.EmptyState
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.enthusiast.components.TransferFilterCard
import com.rio.rostry.ui.enthusiast.components.TransferStatsCard
import com.rio.rostry.ui.enthusiast.components.TransferItemCard
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Badge
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastTransfersScreen(
    onOpenTransfer: (String) -> Unit,
    onVerifyTransfer: (String) -> Unit,
    onCreateTransfer: () -> Unit,
    onOpenTraceability: (String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val vm: EnthusiastTransferViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val historyPaging = vm.pagingHistoryFlow.collectAsLazyPagingItems()
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }
    var verificationExpanded by rememberSaveable { mutableStateOf(true) }
    val rotation by animateFloatAsState(targetValue = if (filtersExpanded) 180f else 0f, label = "filter rotation")
    val verificationRotation by animateFloatAsState(targetValue = if (verificationExpanded) 180f else 0f, label = "verification rotation")
    var showAllPending by rememberSaveable { mutableStateOf(false) }
    var showAllHistory by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (state.selection.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
                    ) {
                        OutlinedButton(onClick = { vm.bulkCancelSelected() }) {
                            Text("Cancel (${state.selection.size})")
                        }
                        OutlinedButton(onClick = { vm.requestPlatformReviewSelected() }) {
                            Text("Request Review (${state.selection.size})")
                        }
                        TextButton(onClick = { vm.clearSelection() }) {
                            Text("Clear")
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(Dimens.space_large),
            verticalArrangement = Arrangement.spacedBy(Dimens.space_large)
        ) {
            Text("Ownership Transfer Management", style = MaterialTheme.typography.titleLarge)
            if (state.loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error?.let { err ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.space_large),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = "Error")
                        Text(err, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        IconButton(onClick = { vm.refresh() }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry")
                        }
                    }
                }
            }

            TransferFilterCard(
                statusFilter = state.statusFilter,
                typeFilter = state.typeFilter,
                startDate = state.startDate,
                endDate = state.endDate,
                onStatusFilterChange = { vm.setStatusFilter(it) },
                onTypeFilterChange = { vm.setTypeFilter(it) },
                onDateRangeChange = { startDate, endDate -> vm.setDateRange(startDate, endDate) },
                modifier = Modifier.fillMaxWidth()
            )

            // Stats Card
            TransferStatsCard(
                totalCount = state.pending.size + historyPaging.itemCount,
                pendingCount = state.pending.size,
                disputedCount = state.pending.count { it.status == "DISPUTED" } + historyPaging.itemSnapshotList.items.count { it.status == "DISPUTED" },
                failedCount = historyPaging.itemSnapshotList.items.count { it.status.uppercase() in listOf("TIMEOUT", "DENIED", "CANCELLED", "FAILED") },
                completedCount = historyPaging.itemSnapshotList.items.count { it.status == "COMPLETED" },
                onFilterByStatus = { vm.setStatusFilter(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Pending Transfers
            Text("Pending Transfers", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = Dimens.space_xl))
            if (state.pending.isEmpty() && !state.loading) {
                EmptyState(
                    title = "No pending transfers",
                    subtitle = "You're all caught up",
                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.space_large)
                )
            } else {
                (if (showAllPending) state.pending else state.pending.take(10)).forEach { t ->
                    var trustScore by remember(t.transferId) { mutableStateOf<Int?>(null) }
                    LaunchedEffect(t.transferId) {
                        trustScore = vm.computeTrustScore(t.transferId)
                    }
                    TransferItemCard(
                        transfer = t,
                        isSelected = state.selection.contains(t.transferId),
                        trustScore = trustScore,
                        onToggleSelection = { vm.toggleSelection(t.transferId) },
                        onOpenDetails = { onOpenTransfer(t.transferId) },
                        onVerify = { onVerifyTransfer(t.transferId) },
                        onOpenChain = { t.productId?.let { productId -> onOpenTraceability(productId) } },
                        onExport = {
                            scope.launch {
                                val json = vm.generateDocumentation(t.transferId)
                                if (!json.isNullOrBlank()) {
                                    com.rio.rostry.utils.export.PdfExporter.writeSimpleTable(
                                        context,
                                        fileName = "transfer_${t.transferId}.pdf",
                                        title = "Transfer ${t.transferId}",
                                        headers = listOf("Payload (json)"),
                                        rows = listOf(listOf(json.take(900)))
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (state.pending.size > 10) {
                    OutlinedButton(onClick = { showAllPending = !showAllPending }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (showAllPending) "Show less" else "Show all (${state.pending.size})")
                    }
                }
            }

            // Transfer History
            Text("Transfer History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = Dimens.space_xl))
            if (historyPaging.itemSnapshotList.items.isEmpty() && !state.loading) {
                EmptyState(
                    title = "No transfer history",
                    subtitle = "Transfers you participate in will appear here",
                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.space_large)
                )
            } else {
                historyPaging.itemSnapshotList.items.take(20).forEach { t ->
                    // For history items, we'll use a simplified version since trust score computation
                    // may not be appropriate for historical data
                    TransferItemCard(
                        transfer = t,
                        isSelected = false, // History items probably shouldn't be selectable
                        trustScore = null, // No trust score for history items
                        onToggleSelection = {}, // No-op since not selectable
                        onOpenDetails = { onOpenTransfer(t.transferId) },
                        onVerify = {}, // No-op for history items
                        onOpenChain = { t.productId?.let { productId -> onOpenTraceability(productId) } },
                        onExport = {
                            scope.launch {
                                val json = vm.generateDocumentation(t.transferId)
                                if (!json.isNullOrBlank()) {
                                    com.rio.rostry.utils.export.PdfExporter.writeSimpleTable(
                                        context,
                                        fileName = "transfer_${t.transferId}.pdf",
                                        title = "Transfer ${t.transferId}",
                                        headers = listOf("Payload (json)"),
                                        rows = listOf(listOf(json.take(900)))
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (historyPaging.itemCount > 20) {
                    OutlinedButton(onClick = { historyPaging.refresh() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Load More")
                    }
                }
            }

            // Verification Steps
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Dimens.space_large)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Verification Steps", style = MaterialTheme.typography.titleMedium)
                        IconButton(
                            onClick = { verificationExpanded = !verificationExpanded },
                            modifier = Modifier.semantics { contentDescription = if (verificationExpanded) "Collapse verification steps" else "Expand verification steps" }
                        ) {
                            Icon(
                                Icons.Filled.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.rotate(verificationRotation)
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = verificationExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
                            val steps = listOf(
                                "Photo Verification" to Icons.Filled.CameraAlt,
                                "GPS Confirmation" to Icons.Filled.LocationOn,
                                "Digital Signature" to Icons.Filled.Draw,
                                "Platform Review" to Icons.Filled.Verified
                            )
                            steps.forEachIndexed { index, (title, icon) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(MaterialTheme.colorScheme.primary, shape = androidx.compose.foundation.shape.CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text((index + 1).toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(Dimens.space_medium))
                                    Icon(icon, contentDescription = null)
                                    Spacer(modifier = Modifier.width(Dimens.space_medium))
                                    Text(title, style = MaterialTheme.typography.bodyMedium)
                                }
                                if (index < steps.lastIndex) {
                                    val outlineColor = MaterialTheme.colorScheme.outline
                                    Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
                                        drawLine(
                                            color = outlineColor,
                                            start = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
                                            end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height),
                                            strokeWidth = 1f,
                                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val now = Instant.now()
    val days = ChronoUnit.DAYS.between(instant, now)
    return when {
        days == 0L -> "Today"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        else -> DateTimeFormatter.ofPattern("MMM dd").withZone(ZoneId.systemDefault()).format(instant)
    }
}
