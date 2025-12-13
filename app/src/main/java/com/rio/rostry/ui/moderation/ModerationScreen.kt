package com.rio.rostry.ui.moderation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.VerificationSubmission
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ModerationScreen(
    vm: ModerationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onOpenVerifications: () -> Unit = {},
    initialTab: Int = 0
) {
    val reports by vm.openReports.collectAsState()
    val pending by vm.pendingVerifications.collectAsState()
    val stats by vm.verificationStats.collectAsState()
    val filterState by vm.filterState.collectAsState()
    
    val selectedTab = remember { mutableIntStateOf(initialTab) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedSubmission by remember { mutableStateOf<VerificationSubmission?>(null) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moderation Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.refreshVerifications() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    if (selectedTab.intValue == 1) {
                        IconButton(onClick = { showFilterSheet = !showFilterSheet }) {
                            Icon(
                                Icons.Default.FilterList, 
                                "Filter",
                                tint = if (filterState != ModerationViewModel.FilterState()) MaterialTheme.colorScheme.primary else LocalContentColor.current
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab.intValue) {
                Tab(selected = selectedTab.intValue == 0, onClick = { selectedTab.intValue = 0 }, text = { Text("Reports") })
                Tab(selected = selectedTab.intValue == 1, onClick = { selectedTab.intValue = 1 }, text = { Text("Verifications") })
            }

            when (selectedTab.intValue) {
                0 -> ReportsTab(reports, vm)
                1 -> VerificationsTab(
                    pending = pending,
                    stats = stats,
                    filterState = filterState,
                    showFilterSheet = showFilterSheet,
                    onFilterChange = { vm.setFilters(it.upgradeType, it.role, it.status, it.dateRangeStart, it.dateRangeEnd, it.searchQuery) },
                    onClearFilters = { vm.clearFilters() },
                    onSelectSubmission = { selectedSubmission = it },
                    onApprove = { vm.approveVerification(it) },
                    onReject = { sub -> 
                        selectedSubmission = sub
                        showRejectDialog = true 
                    }
                )
            }
        }
    }

    if (selectedSubmission != null && !showRejectDialog) {
        VerificationDetailDialog(
            submission = selectedSubmission!!,
            onDismiss = { selectedSubmission = null },
            onApprove = { 
                vm.approveVerification(selectedSubmission!!)
                selectedSubmission = null
            },
            onReject = {
                showRejectDialog = true
            }
        )
    }

    if (showRejectDialog && selectedSubmission != null) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Verification") },
            text = {
                Column {
                    Text("Please provide a reason for rejection:")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.rejectVerification(selectedSubmission!!, rejectionReason)
                        showRejectDialog = false
                        selectedSubmission = null
                        rejectionReason = ""
                    },
                    enabled = rejectionReason.isNotBlank()
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReportsTab(reports: List<com.rio.rostry.data.database.entity.ModerationReportEntity>, vm: ModerationViewModel) {
    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(reports) { r ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(containerColor = MaterialTheme.colorScheme.errorContainer) {
                            Text(r.reason, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("${r.targetType} • ${r.targetId}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(r.reason, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { vm.updateStatus(r.reportId, "UNDER_REVIEW") }) { Text("Review") }
                        TextButton(onClick = { vm.updateStatus(r.reportId, "RESOLVED") }) { Text("Resolve") }
                        Button(onClick = { vm.updateStatus(r.reportId, "REJECTED") }) { Text("Reject Report") }
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationsTab(
    pending: List<VerificationSubmission>,
    stats: ModerationViewModel.VerificationStats,
    filterState: ModerationViewModel.FilterState,
    showFilterSheet: Boolean,
    onFilterChange: (ModerationViewModel.FilterState) -> Unit,
    onClearFilters: () -> Unit,
    onSelectSubmission: (VerificationSubmission) -> Unit,
    onApprove: (VerificationSubmission) -> Unit,
    onReject: (VerificationSubmission) -> Unit
) {
    Column {
        // Stats Row
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatsCard("Pending", stats.pending.toString(), MaterialTheme.colorScheme.primaryContainer)
            StatsCard("Approved", stats.approvedToday.toString(), MaterialTheme.colorScheme.tertiaryContainer)
            StatsCard("Rejected", stats.rejectedToday.toString(), MaterialTheme.colorScheme.errorContainer)
        }

        // Filters
        AnimatedVisibility(visible = showFilterSheet) {
            Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Filters", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    // Simple filter UI placeholders - in a real app use Dropdowns/Chips
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        FilterChip(
                            selected = filterState.upgradeType != null,
                            onClick = { /* Show upgrade type picker */ },
                            label = { Text(filterState.upgradeType?.displayName ?: "Upgrade Type") }
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = filterState.role != null,
                            onClick = { /* Show role picker */ },
                            label = { Text(filterState.role?.name ?: "Role") }
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = filterState.status != null,
                            onClick = { /* Show status picker */ },
                            label = { Text(filterState.status?.name ?: "Status") }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = filterState.searchQuery ?: "",
                        onValueChange = { onFilterChange(filterState.copy(searchQuery = it)) },
                        placeholder = { Text("Search User ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onClearFilters, modifier = Modifier.align(Alignment.End)) {
                        Text("Clear Filters")
                    }
                }
            }
        }

        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
            items(pending) { v ->
                VerificationCard(v, onSelectSubmission, onApprove, onReject)
            }
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier.width(100.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun VerificationCard(
    v: VerificationSubmission,
    onClick: (VerificationSubmission) -> Unit,
    onApprove: (VerificationSubmission) -> Unit,
    onReject: (VerificationSubmission) -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick(v) }
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Badge(
                    containerColor = when(v.upgradeType) {
                        UpgradeType.GENERAL_TO_FARMER -> Color(0xFFE8F5E9) // Light Green
                        UpgradeType.FARMER_VERIFICATION -> Color(0xFFFFF3E0) // Light Orange
                        UpgradeType.FARMER_TO_ENTHUSIAST -> Color(0xFFE3F2FD) // Light Blue
                    }
                ) {
                    Text(
                        v.upgradeType.displayName, 
                        modifier = Modifier.padding(4.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(v.submittedAt ?: Date()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(Modifier.height(8.dp))
            Text("User: ${v.userId}", style = MaterialTheme.typography.titleSmall)
            Text("${v.currentRole} → ${v.targetRole ?: "Verification"}", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(Modifier.height(8.dp))
            Row {
                Icon(Icons.Default.Description, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${v.documentUrls.size} Docs", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Default.Image, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${v.imageUrls.size} Images", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = { onReject(v) }, modifier = Modifier.padding(end = 8.dp)) { Text("Reject") }
                Button(onClick = { onApprove(v) }) { Text("Approve") }
            }
        }
    }
}

@Composable
fun VerificationDetailDialog(
    submission: VerificationSubmission,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Verification Details") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Type: ${submission.upgradeType.displayName}", fontWeight = FontWeight.Bold)
                Text("User ID: ${submission.userId}")
                Text("Submitted: ${submission.submittedAt}")
                
                Spacer(Modifier.height(16.dp))
                Text("Documents", style = MaterialTheme.typography.titleSmall)
                submission.documentUrls.forEach { url ->
                    Text(url.substringAfterLast("/"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                
                Spacer(Modifier.height(8.dp))
                Text("Images", style = MaterialTheme.typography.titleSmall)
                submission.imageUrls.forEach { url ->
                     Text(url.substringAfterLast("/"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                
                if (submission.farmLocation != null) {
                    Spacer(Modifier.height(8.dp))
                    Text("Location", style = MaterialTheme.typography.titleSmall)
                    Text("Lat: ${submission.farmLocation["lat"]}, Lng: ${submission.farmLocation["lng"]}")
                }
            }
        },
        confirmButton = {
            Button(onClick = onApprove) { Text("Approve") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onReject) { Text("Reject") }
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        }
    )
}
