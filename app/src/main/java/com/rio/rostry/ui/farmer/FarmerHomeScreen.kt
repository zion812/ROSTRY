package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
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
import com.rio.rostry.ui.order.evidence.PendingVerificationsWidget
import com.rio.rostry.ui.order.evidence.IncomingEnquiriesWidget
import com.rio.rostry.ui.farmer.TodayTasksCard
import com.rio.rostry.ui.farmer.QuickLogBottomSheet
import com.rio.rostry.ui.farmer.QuickLogType
import com.rio.rostry.ui.farmer.HarvestTriggerCard
import com.rio.rostry.ui.components.VerificationStatusCard

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

    // Evidence Order System data
    val incomingEnquiries by viewModel.incomingEnquiries.collectAsState()
    val paymentsToVerify by viewModel.paymentsToVerify.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showQuickLogSheet by remember { mutableStateOf(false) }
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
    
    // Collect navigation events from ViewModel (for Market button, etc.)
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            onNavigateRoute(route)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // FABs removed in favor of Quick Actions Row for cleaner UI
        floatingActionButton = {}
    ) { padding ->
        SwipeRefresh(state = refreshingState, onRefresh = { viewModel.refreshData() }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Visual Overhaul: Greeting & Quick Actions (Farmer-First Phase 2)
            item {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    // Smart Greeting
                    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                    val greeting = when (currentHour) {
                        in 5..11 -> "Good Morning,"
                        in 12..16 -> "Good Afternoon,"
                        in 17..20 -> "Good Evening,"
                        else -> "Hello,"
                    }
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.userName ?: "Farmer",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = java.text.SimpleDateFormat("EEEE, MMM dd", java.util.Locale.getDefault()).format(java.util.Date()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Quick Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionItem(
                        icon = Icons.Filled.Add,
                        label = "Add Bird",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onClick = onNavigateToAddBird
                    )
                    QuickActionItem(
                        icon = Icons.Filled.EditNote,
                        label = "Quick Log",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = { showQuickLogSheet = true }
                    )
                    QuickActionItem(
                        icon = Icons.Filled.Pets,
                        label = "My Farm",
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { viewModel.navigateToModule(Routes.FarmerNav.FARM_ASSETS) }
                    )
                    QuickActionItem(
                        icon = Icons.Filled.Storefront,
                        label = "Market",
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = { viewModel.navigateToModule(Routes.Builders.productsWithFilter("ready_to_list")) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
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
                                        "You can add birds, track growth, manage breeding, use all farm features, and access market listings while your location is under review.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Expand")
                        }
                    }
                }
            }

            // Trust-But-Verify: Verification Status Card for UNVERIFIED users
            if (uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED) {
                item {
                    VerificationStatusCard(
                        verificationStatus = uiState.verificationStatus,
                        onStartVerification = { onNavigateRoute(com.rio.rostry.ui.navigation.Routes.VERIFY_FARMER_LOCATION) },
                        onRetryVerification = { onNavigateRoute(com.rio.rostry.ui.navigation.Routes.VERIFY_FARMER_LOCATION) }
                    )
                }
            }

            // Digital Farm Pipeline (Live Status)
            item {
                com.rio.rostry.ui.farmer.digital.DigitalFarmPipeline(
                    onStageClick = { stage -> 
                        // Navigate to filtered view for stage
                        viewModel.navigateToModule(Routes.Builders.productsWithFilter(stage.name))
                    },
                    onNavigate = { route ->
                        onNavigateRoute(route)
                    }
                )
            }

            // Today's Tasks Card (Farmer-First Phase 1)
            if (uiState.todayTasks.isNotEmpty() || uiState.completedTasksCount > 0) {
                item {
                    TodayTasksCard(
                        tasks = uiState.todayTasks,
                        completedCount = uiState.completedTasksCount,
                        onTaskClick = { task ->
                            // Navigate based on task type
                            val route = when (task.taskType) {
                                "VACCINATION" -> Routes.Builders.monitoringVaccinationWithProductId(task.productId ?: "")
                                "GROWTH_UPDATE" -> Routes.Builders.monitoringGrowthWithProductId(task.productId ?: "")
                                "FEED_SCHEDULE" -> Routes.Builders.monitoringDailyLog()
                                "HEALTH_CHECK" -> Routes.Builders.monitoringQuarantine("quarantine_12h")
                                else -> Routes.Builders.monitoringTasks("due")
                            }
                            onNavigateRoute(route)
                        },
                        onTaskComplete = { taskId ->
                            viewModel.markTaskComplete(taskId)
                        },
                        onViewAllTasks = { onNavigateRoute(Routes.MONITORING_TASKS) }
                    )
                }
            }

            // Storage Quota Warning Banner
            uiState.storageQuota?.let { quota ->
                if (quota.quotaBytes > 0) {
                    val usageRatio = quota.usedBytes.toDouble() / quota.quotaBytes.toDouble()
                    if (usageRatio > 0.8) {
                        item {
                            val isCritical = usageRatio > 0.95
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCritical) MaterialTheme.colorScheme.errorContainer 
                                                else Color(0xFFFFF3E0) // Warm orange-ish for warning
                            ),
                            onClick = { onNavigateRoute(Routes.STORAGE_QUOTA) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCritical) Icons.Filled.CloudOff else Icons.Filled.CloudQueue,
                                        contentDescription = "Storage warning icon",
                                        tint = if (isCritical) MaterialTheme.colorScheme.error else Color(0xFFE65100)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (isCritical) "Cloud Storage Full" else "Cloud Storage Almost Full",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (isCritical) MaterialTheme.colorScheme.onErrorContainer else Color(0xFFBF360C)
                                        )
                                        Text(
                                            text = if (isCritical) "You have reached your storage limit. New uploads are disabled." 
                                                   else "You have used ${(usageRatio * 100).toInt()}% of your cloud storage.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isCritical) MaterialTheme.colorScheme.onErrorContainer else Color(0xFFD84315)
                                        )
                                    }
                                }
                                Icon(
                                    Icons.Filled.ChevronRight, 
                                    contentDescription = "Manage Storage",
                                    tint = if (isCritical) MaterialTheme.colorScheme.onErrorContainer else Color(0xFFD84315)
                                )
                            }
                        }
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
            
            // Evidence Order System Widgets
            // Pending Payments Verification (for sellers)
            if (paymentsToVerify.isNotEmpty()) {
                item {
                    PendingVerificationsWidget(
                        payments = paymentsToVerify,
                        onViewAll = { onNavigateRoute(Routes.Order.MY_ORDERS_FARMER) },
                        onVerifyPayment = { paymentId -> 
                            onNavigateRoute(Routes.Order.paymentVerify(paymentId))
                        }
                    )
                }
            }
            
            // Incoming Enquiries Widget (for sellers)
            if (incomingEnquiries.isNotEmpty()) {
                item {
                    IncomingEnquiriesWidget(
                        quotes = incomingEnquiries,
                        onViewAll = { onNavigateRoute(Routes.Order.MY_ORDERS_FARMER) },
                        onQuoteClick = { quoteId -> 
                            onNavigateRoute(Routes.Order.quoteNegotiation(quoteId, false))
                        }
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


            // Harvest Ready Cards (Farmer-First Phase 2)
            val harvestReadyAlerts = uiState.unreadAlerts.filter { it.alertType == "HARVEST_READY" }
            if (harvestReadyAlerts.isNotEmpty()) {
                items(harvestReadyAlerts) { alert ->
                    HarvestTriggerCard(
                        alert = alert,
                        onSellNow = { batchId ->
                            onNavigateRoute(Routes.Builders.createListingFromAsset(batchId))
                        },
                        onDismiss = { alertId ->
                            viewModel.markAlertRead(alertId)
                        }
                    )
                }
            }

            // Alerts Banner (non-harvest alerts)
            val nonHarvestAlerts = uiState.unreadAlerts.filter { it.alertType != "HARVEST_READY" }
            if (nonHarvestAlerts.isNotEmpty()) {
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
                                "${nonHarvestAlerts.size} Urgent Alerts",
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
                        if (uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED || 
                            uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.PENDING) {
                            viewModel.navigateToModule(Routes.Builders.productsWithFilter("ready_to_list")) 
                        } else {
                            showVerificationPendingDialog = true
                        }
                    }
                    WidgetType.NEW_LISTING -> Icons.Filled.AddCircle to { 
                        if (uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.VERIFIED ||
                            uiState.verificationStatus == com.rio.rostry.domain.model.VerificationStatus.PENDING) {
                            viewModel.navigateToModule(Routes.FarmerNav.CREATE)
                        } else {
                            showVerificationPendingDialog = true
                        }
                    }
                    WidgetType.TRANSFERS -> Icons.Filled.Send to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("ELIGIBLE")) }
                    WidgetType.TRANSFERS_PENDING -> Icons.Filled.PendingActions to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("PENDING")) }
                    WidgetType.TRANSFERS_VERIFICATION -> Icons.Filled.VerifiedUser to { viewModel.navigateToModule(Routes.Builders.transfersWithFilter("AWAITING_VERIFICATION")) }
                    WidgetType.COMPLIANCE -> Icons.Filled.Warning to { viewModel.navigateToCompliance() }
                    // Evidence Order widgets
                    WidgetType.INCOMING_ENQUIRIES -> Icons.Filled.Mail to { onNavigateRoute(Routes.Order.MY_ORDERS_FARMER) }
                    WidgetType.PAYMENTS_TO_VERIFY -> Icons.Filled.Payment to { onNavigateRoute(Routes.Order.MY_ORDERS_FARMER) }
                    WidgetType.ACTIVE_ORDERS -> Icons.Filled.ShoppingCart to { onNavigateRoute(Routes.Order.MY_ORDERS) }
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
                               uiState.verificationStatus != com.rio.rostry.domain.model.VerificationStatus.VERIFIED &&
                               uiState.verificationStatus != com.rio.rostry.domain.model.VerificationStatus.PENDING

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
            
            // Add My Farm (Farm Assets) Card - NEW entry point to asset management
            val myFarmCard = FetcherCard(
                title = "My Farm",
                count = uiState.farmAssetCount, // Show asset count from ViewModel
                badgeCount = 0,
                icon = Icons.Filled.Pets, // Matches bottom nav icon
                action = "Manage Assets",
                onClick = { viewModel.navigateToModule(Routes.FarmerNav.FARM_ASSETS) },
                isLocked = false
            )
            
            // Farm Log Card - Entry point to comprehensive activity log
            val farmLogCard = FetcherCard(
                title = "Farm Log",
                count = 0, // TODO: Show count of recent activities
                badgeCount = 0,
                icon = Icons.Filled.History, // Represents activity history
                action = "View All",
                onClick = { viewModel.navigateToModule(Routes.MONITORING_FARM_LOG) },
                isLocked = false
            )
            
            val allFetcherCards = listOf(myFarmCard, farmLogCard) + fetcherCards

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

    // Quick Log Bottom Sheet (Farmer-First Phase 1)
    if (showQuickLogSheet) {
        val allProducts by viewModel.allProducts.collectAsState()
        QuickLogBottomSheet(
            products = allProducts,
            onDismiss = { showQuickLogSheet = false },
            onLogSubmit = { productIds, logType, value, notes ->
                // Submit log for each selected product
                viewModel.submitQuickLogBatch(productIds, logType, value, notes)
                // Show confirmation snackbar with count
                val countText = if (productIds.size == 1) "1 bird" else "${productIds.size} birds"
                scope.launch {
                    snackbarHostState.showSnackbar("${logType.label} logged for $countText: $value ${logType.unit}")
                }
                showQuickLogSheet = false
            }
        )
    }
}
