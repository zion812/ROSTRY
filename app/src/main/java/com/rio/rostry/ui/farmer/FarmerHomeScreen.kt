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
import com.rio.rostry.ui.farmer.SmartActionCard
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
    var showEnthusiastUpgradeSheet by remember { mutableStateOf(false) }  // New state for upgrade form
    val onboardingViewModel: OnboardingChecklistViewModel = hiltViewModel()
    val checklistState by onboardingViewModel.uiState.collectAsState()
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var showVerificationPendingDialog by remember { mutableStateOf(false) }
    
    // Stage Transition State
    var showStageTransitionDialog by remember { mutableStateOf(false) }
    var selectedTransitionTask by remember { mutableStateOf<com.rio.rostry.data.database.entity.TaskEntity?>(null) }

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
        floatingActionButton = {},
        contentWindowInsets = WindowInsets(0.dp)
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
            
            // Smart Action Card - Aggregated urgent actions (Priority 1: Smart Task Aggregator)
            item {
                SmartActionCard(
                    vaccinationOverdue = uiState.vaccinationOverdueCount,
                    vaccinationDue = uiState.vaccinationDueCount,
                    tasksOverdue = uiState.tasksOverdueCount,
                    tasksDue = uiState.tasksDueCount,
                    hatchingDueThisWeek = uiState.hatchingDueThisWeek,
                    ordersToVerify = paymentsToVerify.size,
                    enquiriesPending = incomingEnquiries.size,
                    onVaccinationClick = { onNavigateRoute(Routes.Builders.monitoringVaccinationWithFilter("today")) },
                    onTasksClick = { onNavigateRoute(Routes.MONITORING_TASKS) },
                    onHatchingClick = { onNavigateRoute(Routes.Builders.monitoringHatching()) },
                    onOrdersClick = { onNavigateRoute(Routes.Order.MY_ORDERS_FARMER) }
                )
            }
            
            // Farm-Wide Documentation Export Card
            item {
                Card(
                    onClick = { onNavigateRoute(Routes.Monitoring.FARM_DOCUMENT) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Farm Report",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Export complete farm documentation as PDF",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            
            // Expense Ledger Card
            item {
                Card(
                    onClick = { onNavigateRoute(Routes.Monitoring.EXPENSE_LEDGER) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Expense Ledger",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Track feed, medicine, and other farm costs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            
            // Market Timing Widget (New)
            item {
                com.rio.rostry.ui.analytics.market.MarketTimingWidget(
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Weather Card - Heat Stress Warning (Open-Meteo API)
            item {
                val weatherData by viewModel.weatherData.collectAsState()
                val temperature = weatherData?.temperature?.toInt() ?: 28
                val isHeatStress = weatherData?.isHeatStress ?: false
                
                WeatherCard(
                    temperature = temperature,
                    isHeatStress = isHeatStress,
                    isLoading = weatherData == null,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Predictive Feed Card - Breed-specific nutrition recommendations
            item {
                val feedRecommendation by viewModel.feedRecommendation.collectAsState()
                val suggestedFeedKg by viewModel.suggestedFeedKg.collectAsState()
                
                // Track state for Feed Detail Sheet
                var showFeedDetail by remember { mutableStateOf(false) }
                // Track state for Quick Feed Log Dialog
                var showFeedLogDialog by remember { mutableStateOf(false) }
                
                if (feedRecommendation != null) {
                    com.rio.rostry.ui.farmer.feed.FeedRecommendationCard(
                        recommendation = feedRecommendation,
                        todayLogAmount = uiState.todayFeedLogAmount,
                        onLogClick = { showFeedLogDialog = true },
                        onDetailClick = { showFeedDetail = true },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                if (showFeedDetail && feedRecommendation != null) {
                    com.rio.rostry.ui.farmer.feed.FeedDetailSheet(
                        recommendation = feedRecommendation!!,
                        onDismiss = { showFeedDetail = false },
                        onLogFeed = { 
                            showFeedDetail = false
                            showFeedLogDialog = true 
                        }
                    )
                }
                
                if (showFeedLogDialog) {
                    val allProducts by viewModel.allProducts.collectAsState()
                    com.rio.rostry.ui.farmer.feed.QuickFeedLogDialog(
                        suggestedAmount = suggestedFeedKg ?: feedRecommendation?.dailyFeedKg,
                        products = allProducts,
                        onDismiss = { showFeedLogDialog = false },
                        onConfirm = { amount, notes ->
                            viewModel.submitQuickLog(null, QuickLogType.FEED, amount, notes)
                            showFeedLogDialog = false
                            // Optional: Show success snackbar
                            scope.launch { 
                                snackbarHostState.showSnackbar("Feed logged successfully") 
                            }
                        }
                    )
                }
            }

            // Stage Transition Dialog logic
            if (showStageTransitionDialog && selectedTransitionTask != null) {
                item {
                    // Parse metadata to get old/new stage
                    val metaJson = selectedTransitionTask!!.metadata
                    var oldStage = "Previous Stage"
                    var newStage = "Next Stage"
                    var assetName = "Batch/Bird"
                    
                    try {
                        if (!metaJson.isNullOrEmpty()) {
                            val metaObj = com.google.gson.JsonParser.parseString(metaJson).asJsonObject
                            if (metaObj.has("oldStage")) oldStage = metaObj.get("oldStage").asString
                            if (metaObj.has("newStage")) newStage = metaObj.get("newStage").asString
                        }
                    } catch (e: Exception) {
                        // Fallback to defaults
                    }
                    
                    com.rio.rostry.ui.farmer.lifecycle.StageTransitionDialog(
                        assetName = assetName,
                        oldStage = oldStage,
                        newStage = newStage,
                        onDismiss = { 
                            showStageTransitionDialog = false 
                            selectedTransitionTask = null
                        },
                        onConfirm = { mortality, feedKg, notes ->
                            viewModel.completeStageTransition(
                                selectedTransitionTask!!.taskId,
                                selectedTransitionTask!!.productId,
                                mortality,
                                feedKg,
                                notes
                            )
                            showStageTransitionDialog = false
                            selectedTransitionTask = null
                            scope.launch { 
                                snackbarHostState.showSnackbar("Stage transition recorded successfully") 
                            }
                        }
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
                        onClick = { viewModel.navigateToModule(Routes.FarmerNav.MARKET) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Flock Value Widget (Profitability) - shows estimated worth of standing flock
            if (uiState.estimatedFlockValue > 0) {
                item {
                    FlockValueCard(
                        estimatedValue = uiState.estimatedFlockValue,
                        birdCount = uiState.activeBirdCount,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Enthusiast Upgrade Recommendation Banner (for verified farmers with 50+ birds)
            if (uiState.showEnthusiastUpgradeBanner) {
                item {
                    EnthusiastUpgradeBanner(
                        birdCount = uiState.activeBirdCount,
                        onClick = { showEnthusiastUpgradeSheet = true }  // Show upgrade form sheet
                    )
                }
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
                            if (task.taskType == "STAGE_TRANSITION") {
                                selectedTransitionTask = task
                                showStageTransitionDialog = true
                            } else {
                                val route = when (task.taskType) {
                                    "VACCINATION" -> Routes.Builders.monitoringVaccinationWithProductId(task.productId ?: "")
                                    "GROWTH_UPDATE" -> Routes.Builders.monitoringGrowthWithProductId(task.productId ?: "")
                                    "FEED_SCHEDULE" -> Routes.Builders.monitoringDailyLog()
                                    "HEALTH_CHECK" -> Routes.Builders.monitoringQuarantine("quarantine_12h")
                                    else -> Routes.Builders.monitoringTasks("due")
                                }
                                onNavigateRoute(route)
                            }
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
            
            // Farm Log Fetcher 2.0 - Shows latest activity and today's count
            item {
                LiveFetcherCard(
                    title = "Farm Log",
                    count = uiState.todayLogCount,
                    latestLog = uiState.latestFarmLog,
                    icon = Icons.Filled.History,
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_FARM_LOG) }
                )
            }
            
            // Layout the remaining 3 main cards in a grid-like fashion (2 rows of 2 if needed, or row + single)
            // But since we want "Live" feel, we can stack them or use a Row.
            // Let's use a 2x2 grid for the main 4 live fetchers: My Farm, Calendar | Digital Farm, Farm Log
            // Wait, Farm Log is already full width above.
            // Let's put My Farm and Calendar side-by-side? No, Live cards are detailed.
            // Let's stack them for now to ensure room for "Next Task" details.
            
            item {
                 LiveMyFarmFetcherCard(
                    title = "My Farm",
                    assetCount = uiState.farmAssetCount,
                    activeFlockCount = uiState.activeFlockCount,
                    icon = Icons.Filled.Pets,
                    onClick = { viewModel.navigateToModule(Routes.FarmerNav.FARM_ASSETS) }
                )
            }
            
            item {
                LiveCalendarFetcherCard(
                    title = "Calendar",
                    count = uiState.todayTasks.size,
                    nextTask = uiState.nextTask,
                    icon = Icons.Filled.DateRange,
                    onClick = { viewModel.navigateToModule(Routes.FarmerNav.CALENDAR) }
                )
            }
            
            // Digital Farm Card - Lite visualization (keep as standard fetcher for now or custom)
            // We can treat it as a standard fetcher card in the list, OR give it special treatment.
            // Let's add it to the list of standard fetchers.
            
            val digitalFarmCard = FetcherCard(
                title = "Digital Farm",
                count = uiState.activeBirdCount, 
                badgeCount = 0,
                icon = Icons.Filled.Landscape, 
                action = "View Farm",
                onClick = { viewModel.navigateToModule(Routes.FarmerNav.DIGITAL_FARM) },
                isLocked = false
            )
            
            val allFetcherCards = listOf(digitalFarmCard) + fetcherCards

            // Convert grid to rows
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
        val suggestedFeed by viewModel.suggestedFeedKg.collectAsState()
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
            },
            suggestedFeedKg = suggestedFeed
        )
    }
    
    // Enthusiast Upgrade Sheet - Request form for role upgrade
    if (showEnthusiastUpgradeSheet) {
        EnthusiastUpgradeSheet(
            onDismiss = { showEnthusiastUpgradeSheet = false },
            onSubmit = { formData ->
                // Navigate to upgrade wizard with Enthusiast role target
                viewModel.navigateToModule(Routes.Builders.upgradeWizard(com.rio.rostry.domain.model.UserType.ENTHUSIAST))
                scope.launch {
                    snackbarHostState.showSnackbar("Upgrade request submitted! Review in 24-48 hours.")
                }
                showEnthusiastUpgradeSheet = false
            }
        )
    }
        }
    }


/**
 * Premium-styled banner for recommending Enthusiast upgrade to verified farmers with 50+ birds.
 * Uses gold gradient to convey premium/advanced tier.
 */
@Composable
private fun EnthusiastUpgradeBanner(
    birdCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D)  // Dark base for premium feel
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFD4AF37).copy(alpha = 0.15f),  // Gold accent
                            Color(0xFF2D2D2D)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WorkspacePremium,
                        contentDescription = "Premium",
                        tint = Color(0xFFD4AF37),  // Gold
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ready to Level Up?",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text(
                            text = "You're managing $birdCount birds! Unlock advanced tracking with Enthusiast.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "View Benefits",
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Flock Value Card - Displays estimated market value of standing flock.
 * Helps farmers understand the profitability of their farm at a glance.
 * Formula: Birds × Avg Weight (1.5kg default) × ₹200/kg
 */
@Composable
private fun FlockValueCard(
    estimatedValue: Double,
    birdCount: Int,
    modifier: Modifier = Modifier
) {
    val formattedValue = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN"))
        .format(estimatedValue)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B5E20) // Deep green for profitability
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Estimated Flock Value",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    formattedValue,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "$birdCount birds × ₹300 avg/bird",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = "Profitability",
                tint = Color(0xFF81C784), // Light green
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/**
 * Weather Card - Shows temperature and heat stress warning for poultry.
 * Poultry is sensitive to heat stress above 32°C (90°F).
 */
@Composable
private fun WeatherCard(
    temperature: Int,
    isHeatStress: Boolean,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isLoading -> Color(0xFF78909C) // Gray while loading
        isHeatStress -> Color(0xFFE53935) // Red for heat stress
        else -> Color(0xFF42A5F5) // Blue for normal
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isHeatStress) Icons.Filled.Warning else Icons.Filled.WbSunny,
                    contentDescription = if (isHeatStress) "Heat Warning" else "Weather",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${temperature}°C",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isHeatStress) "Heat Stress Alert! Ensure water & ventilation" else "Good weather for poultry",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

/**
 * Predictive Feed Card - Shows breed-specific feed recommendations.
 */
@Composable
private fun PredictiveFeedCard(
    recommendation: com.rio.rostry.domain.model.FeedRecommendation,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (recommendation.isLowInventoryAlert) {
        Color(0xFFFF7043) // Orange for low inventory warning
    } else {
        Color(0xFF66BB6A) // Green for normal
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = recommendation.feedType.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${recommendation.feedType.displayName} Feed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Protein: ${recommendation.proteinTarget}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format("%.1f", recommendation.dailyFeedKg)} kg/day",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${String.format("%.1f", recommendation.weeklyFeedKg)} kg/week",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            if (recommendation.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recommendation.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2
                )
            }
            
            // Bird count badge
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${recommendation.birdCount} birds • ${recommendation.stage.displayName} stage",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
