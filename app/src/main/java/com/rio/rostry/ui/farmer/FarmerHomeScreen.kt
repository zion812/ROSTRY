package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.AddToFarmDialog
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rio.rostry.ui.navigation.Routes
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import com.rio.rostry.data.repository.analytics.DailyGoal
import com.rio.rostry.data.repository.analytics.ActionableInsight
import androidx.compose.ui.text.style.TextAlign
import com.rio.rostry.ui.components.OnboardingChecklistCard
import com.rio.rostry.ui.onboarding.OnboardingChecklistViewModel

data class FetcherCard(
    val title: String,
    val count: Int,
    val badgeCount: Int = 0,
    val badgeColor: Color = Color.Transparent,
    val icon: ImageVector,
    val action: String,
    val onClick: () -> Unit,
    val isLocked: Boolean = false
)

@Composable
fun FarmerHomeScreen(
    viewModel: FarmerHomeViewModel = hiltViewModel(),
    onOpenAlerts: () -> Unit = {},
    onNavigateToAddBird: () -> Unit = {},
    onNavigateToAddBatch: () -> Unit = {},
    onNavigateRoute: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val refreshingState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
    val colorScheme = MaterialTheme.colorScheme

    var showAddDialog by remember { mutableStateOf(false) }
    val onboardingViewModel: OnboardingChecklistViewModel = hiltViewModel()
    val checklistState by onboardingViewModel.uiState.collectAsState()
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var showVerificationPendingDialog by remember { mutableStateOf(false) }

    // Check if user is new (< 7 days since registration) and checklist is incomplete
    val isNewUser = remember(checklistState.isChecklistRelevant) {
        checklistState.isChecklistRelevant
    }

    LaunchedEffect(checklistState.showCelebration) {
        if (checklistState.showCelebration) {
            showCelebrationDialog = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add to Farm")
            }
        }
    ) { padding ->
        SwipeRefresh(state = refreshingState, onRefresh = { viewModel.refreshData() }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Verification Pending Banner
            if (uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.PENDING) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        onClick = { showVerificationPendingDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Timer, contentDescription = "Pending icon")
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Verification Pending",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "You can add birds, track growth, manage breeding, and use all farm features. Market listing will be enabled once your farm location is verified (24-48 hours).",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Expand")
                        }
                    }
                }
            }
            // Compliance Banner
            if (!uiState.kycVerified || uiState.complianceAlertsCount > 0) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        onClick = { viewModel.navigateToCompliance() }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Warning, contentDescription = "Compliance icon")
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Compliance Issues: ${uiState.complianceAlertsCount}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = "View compliance")
                        }
                    }
                }
            }
            // Onboarding Checklist Card
            if (isNewUser && checklistState.items.isNotEmpty() && checklistState.completionPercentage < 100) {
                item {
                    OnboardingChecklistCard(
                        items = checklistState.items,
                        completionPercentage = checklistState.completionPercentage,
                        onNavigate = { route -> onNavigateRoute(route) },
                        onDismiss = { onboardingViewModel.dismissChecklist() }
                    )
                }
            }
            // Weekly KPI Cards
            uiState.weeklySnapshot?.let { snapshot ->
                item {
                    Text(
                        "Weekly Performance",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            KpiCard("Revenue", "₹${snapshot.revenueInr.toInt()}")
                        }
                        item {
                            KpiCard("Orders", snapshot.ordersCount.toString())
                        }
                        item {
                            KpiCard("Hatch Rate", "${(snapshot.hatchSuccessRate * 100).toInt()}%")
                        }
                        item {
                            KpiCard("Mortality", "${(snapshot.mortalityRate * 100).toInt()}%")
                        }
                    }
                }
            }
            // Recent Activity Section
            item {
                Text(
                    "Recent Activity",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(12.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (uiState.recentActivity.isNotEmpty()) {
                            Text("Recently Onboarded", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            uiState.recentActivity.take(5).forEach { activity ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = activity.productName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (activity.isBatch) "Batch • ${java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date(activity.addedAt))}" 
                                                  else "Bird • ${java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(java.util.Date(activity.addedAt))}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (activity.tasksCreated > 0) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.padding(end = 4.dp)
                                            ) {
                                                Text(
                                                    text = "${activity.tasksCreated} tasks",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        if (activity.vaccinationsScheduled > 0) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "${activity.vaccinationsScheduled} vax",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                                if (uiState.recentActivity.last() != activity) {
                                    androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                            }
                            if (uiState.recentActivity.size > 5) {
                                TextButton(
                                    onClick = { viewModel.navigateToModule(Routes.Builders.productsWithFilter("recent")) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("View All")
                                }
                            }
                        } else {
                            Text("Recently added: ${uiState.recentlyAddedBirdsCount} birds, ${uiState.recentlyAddedBatchesCount} batches")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.navigateToModule(Routes.Builders.productsWithFilter("recent")) }) {
                                Text("View All")
                            }
                        }
                    }
                }
            }


            // Alerts Banner
            if (uiState.unreadAlerts.isNotEmpty()) {
                item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    onClick = onOpenAlerts
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = "Alert icon")
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${uiState.unreadAlerts.size} Urgent Alerts",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = "View alerts")
                    }
                }
                }
            }

            // Fetcher Grid
            item {
                Text(
                    "Farm Monitoring",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            val fetcherCards = uiState.widgets.map { widget ->
                val (icon, onClick) = when (widget.type) {
                    WidgetType.DAILY_LOG -> Icons.Filled.EditNote to { viewModel.navigateToModule(Routes.Builders.monitoringDailyLog()) }
                    WidgetType.TASKS -> Icons.Filled.Task to { viewModel.navigateToModule(Routes.Builders.monitoringTasks("due")) }
                    WidgetType.VACCINATION -> Icons.Filled.Vaccines to { viewModel.navigateToModule(Routes.Builders.monitoringVaccinationWithFilter("today")) }
                    WidgetType.GROWTH -> Icons.Filled.TrendingUp to { viewModel.navigateToModule(Routes.Builders.monitoringGrowthWithFilter("growth_record")) }
                    WidgetType.QUARANTINE -> Icons.Filled.LocalHospital to { viewModel.navigateToModule(Routes.Builders.monitoringQuarantine("quarantine_12h")) }
                    WidgetType.HATCHING -> Icons.Filled.EggAlt to { viewModel.navigateToModule(Routes.Builders.monitoringHatching()) }
                    WidgetType.MORTALITY -> Icons.Filled.Warning to { viewModel.navigateToModule(Routes.Builders.monitoringMortality()) }
                    WidgetType.BREEDING -> Icons.Filled.Favorite to { viewModel.navigateToModule(Routes.Builders.monitoringBreeding()) }
                    WidgetType.READY_TO_LIST -> Icons.Filled.Storefront to { 
                        if (uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED) {
                            viewModel.navigateToModule(Routes.Builders.productsWithFilter("ready_to_list")) 
                        } else {
                            showVerificationPendingDialog = true
                        }
                    }
                    WidgetType.NEW_LISTING -> Icons.Filled.AddCircle to { 
                        if (uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED) {
                            viewModel.navigateToModule(Routes.FarmerNav.CREATE)
                        } else {
                            showVerificationPendingDialog = true
                        }
                    }
                    WidgetType.TRANSFERS -> Icons.Filled.Send to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("ELIGIBLE")) }
                    WidgetType.TRANSFERS_PENDING -> Icons.Filled.PendingActions to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("PENDING")) }
                    WidgetType.TRANSFERS_VERIFICATION -> Icons.Filled.VerifiedUser to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("AWAITING_VERIFICATION")) }
                    WidgetType.TRANSFERS_VERIFICATION -> Icons.Filled.VerifiedUser to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("AWAITING_VERIFICATION")) }
                    WidgetType.COMPLIANCE -> Icons.Filled.Warning to { viewModel.navigateToCompliance() }
                }
                
                // Add Digital Farm Card manually if not in widgets list (or assume it's a static addition for now)
                // Since widgets come from API/ViewModel, I should probably add it to the list or hardcode it as a special item.
                // For now, I'll add it as a separate item in the grid construction or just append it to the list if possible.
                // But `uiState.widgets` is a list.
                // I'll add a static card to `fetcherCards` list.


                val badgeColor = when (widget.alertLevel) {
                    AlertLevel.CRITICAL -> colorScheme.error
                    AlertLevel.WARNING -> colorScheme.tertiary
                    AlertLevel.INFO -> colorScheme.primary
                    AlertLevel.NORMAL -> Color.Transparent
                }

                val isLocked = (widget.type == WidgetType.READY_TO_LIST || widget.type == WidgetType.NEW_LISTING) && 
                               uiState.verificationStatus != com.rio.rostry.domain.model.VerificationStatus.VERIFIED

                FetcherCard(
                    title = widget.title,
                    count = widget.count,
                    badgeCount = widget.alertCount,
                    badgeColor = badgeColor,
                    icon = icon,
                    action = if (isLocked) "Locked" else widget.actionLabel,
                    onClick = onClick,
                    isLocked = isLocked
                )
            }
            
            // Add Digital Farm Card
            val digitalFarmCard = FetcherCard(
                title = "Digital Farm",
                count = 0, // Dynamic count not needed for entry point
                badgeCount = 0,
                icon = Icons.Filled.Landscape, // Use a relevant icon
                action = "View Farm",
                onClick = { viewModel.navigateToModule(Routes.FarmerNav.DIGITAL_FARM) },
                isLocked = false
            )
            
            val allFetcherCards = listOf(digitalFarmCard) + fetcherCards

            // Convert grid to rows to avoid nested lazy layout with stable keys
            val fetcherRows = allFetcherCards.chunked(2)
            items(
                items = fetcherRows,
                key = { row -> row.joinToString(separator = "|") { it.title } },
                contentType = { "fetcher_row" }
            ) { rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCards.forEach { card ->
                        Box(modifier = Modifier.weight(1f)) {
                            FetcherCardItem(card)
                        }
                    }
                    if (rowCards.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // Daily Goals Section
            item {
                Text(
                    "Daily Goals",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.dailyGoals) { goal ->
                        GoalCard(
                            goal = goal,
                            onAction = { viewModel.navigateToModule(goal.deepLink) },
                            onDismiss = { viewModel.dismissGoal(goal.goalId) }
                        )
                    }
                }
            }
            // Analytics Insights Section Removed

            }
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Observe navigation events and route out
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            onNavigateRoute(route)
        }
    }

    // Observe error events and show snackbar
    LaunchedEffect(Unit) {
        viewModel.errorEvents.collect { msg ->
            scope.launch { snackbarHostState.showSnackbar(message = msg) }
        }
    }

    if (showAddDialog) {
        AddToFarmDialog(
            onDismiss = { showAddDialog = false },
            onSelectIndividual = {
                showAddDialog = false
                onNavigateToAddBird()
            },
            onSelectBatch = {
                showAddDialog = false
                onNavigateToAddBatch()
            }
        )
    }

    if (showCelebrationDialog) {
        AlertDialog(
            onDismissRequest = { showCelebrationDialog = false },
            title = { Text("Congratulations!") },
            text = { Text("You've completed your onboarding checklist. Welcome to ROSTRY!") },
            confirmButton = {
                Button(onClick = { showCelebrationDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }

    if (showVerificationPendingDialog) {
        val isPending = uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.PENDING
        val titleText = if (isPending) "Verification Pending" else "Verification Required"
        val bodyText = if (isPending) 
            "Your documents are under review. You can add birds and manage your farm, but market listing will be enabled once your location is verified (24-48 hours)."
        else 
            "To list items in the market, you need to verify your farm location. You can still manage your farm and inventory without verification."
        
        AlertDialog(
            onDismissRequest = { showVerificationPendingDialog = false },
            icon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            title = { Text(titleText) },
            text = { Text(bodyText) },
            confirmButton = {
                Button(onClick = { 
                    showVerificationPendingDialog = false
                    if (!isPending) {
                        onNavigateRoute(com.rio.rostry.ui.navigation.Routes.VERIFY_FARMER_LOCATION)
                    }
                }) {
                    Text(if (isPending) "Got it" else "Verify Now")
                }
            },
            dismissButton = {
                if (isPending) {
                    TextButton(onClick = { onNavigateRoute(com.rio.rostry.ui.navigation.Routes.VERIFY_FARMER_LOCATION) }) {
                        Text("Check Status")
                    }
                } else {
                     TextButton(onClick = { showVerificationPendingDialog = false }) {
                        Text("Later")
                    }
                }
            }
        )
    }
}

@Composable
private fun KpiCard(title: String, value: String) {
    Card(modifier = Modifier.width(120.dp)) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}



@Composable
private fun FetcherCardItem(card: FetcherCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .semantics { contentDescription = "${card.title}: ${card.count}. ${card.action}" },
        onClick = card.onClick,
        colors = if (card.isLocked) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) else CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    if (card.isLocked) Icons.Filled.Lock else card.icon,
                    contentDescription = "${card.title} icon",
                    tint = if (card.isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
                )
                if (card.badgeCount > 0 && !card.isLocked) {
                    Badge(
                        containerColor = card.badgeColor
                    ) {
                        Text(card.badgeCount.toString())
                    }
                }
            }

            Column {
                Text(
                    card.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (card.isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (card.isLocked) "--" else card.count.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (card.isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
                )
                Text(
                    card.action,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



@Composable
private fun GoalCard(
    goal: DailyGoal,
    onAction: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val icon = when (goal.iconName) {
                    "task_icon" -> Icons.Filled.Task
                    "log_icon" -> Icons.Filled.EditNote
                    "vaccination_icon" -> Icons.Filled.Vaccines
                    else -> Icons.Filled.Star
                }
                Icon(
                    icon,
                    contentDescription = goal.title
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Dismiss")
                }
            }

            Text(
                goal.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                CircularProgressIndicator(
                    progress = goal.progress,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    "${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                "${goal.currentCount}/${goal.targetCount}",
                style = MaterialTheme.typography.bodySmall
            )

            Button(onClick = onAction) {
                Text("Complete")
            }
        }
    }
}

@Composable
private fun InsightCard(
    insight: ActionableInsight,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(insight.description)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = "Dismiss")
            }
        }
    }
}
