package com.rio.rostry.ui.enthusiast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import com.rio.rostry.ui.components.AddToFarmDialog
import com.rio.rostry.ui.components.OnboardingChecklistCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import com.rio.rostry.ui.onboarding.OnboardingChecklistViewModel
import com.rio.rostry.ui.enthusiast.components.EnthusiastActionCard
import com.rio.rostry.ui.enthusiast.components.EnthusiastAlertCard
import com.rio.rostry.ui.enthusiast.components.EnthusiastKpiCard
import com.rio.rostry.ui.enthusiast.components.HeroChampionBanner
import com.rio.rostry.ui.enthusiast.components.SpeedDialActions
import com.rio.rostry.ui.enthusiast.components.SpeedDialAction
import com.rio.rostry.ui.enthusiast.components.LiveActivityTicker
import com.rio.rostry.ui.enthusiast.components.CollapsibleSection
import com.rio.rostry.ui.enthusiast.components.ContextualActionBar
import com.rio.rostry.ui.enthusiast.components.QuickAction
import com.rio.rostry.ui.enthusiast.components.EnthusiastQuickActions
import com.rio.rostry.ui.enthusiast.components.QuickStatsRow
import com.rio.rostry.ui.enthusiast.components.QuickStat
import com.rio.rostry.ui.enthusiast.components.EnthusiastStats
import com.rio.rostry.ui.enthusiast.components.TodaysFocusCard
import com.rio.rostry.ui.enthusiast.components.FocusPriority
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.components.EnthusiastAuraBackground
import com.rio.rostry.ui.components.PremiumCard
import com.rio.rostry.ui.components.GradientButton
import androidx.compose.ui.graphics.Brush
import com.rio.rostry.ui.theme.EnthusiastGold

/**
 * Advanced Enthusiast interface with premium farm management features.
 * Tabs supported by Routes.EnthusiastNav: Home, Explore, Create, Dashboard, Transfers
 */

// =============== HOME TAB ===============
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EnthusiastHomeScreen(
    onOpenProfile: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPerformanceAnalytics: () -> Unit = {},
    onOpenFinancialAnalytics: () -> Unit = {},
    onOpenTransfers: () -> Unit,
    onOpenTraceability: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onVerifyKyc: () -> Unit,
    onOpenReports: () -> Unit = {},
    onOpenMonitoringDashboard: () -> Unit = {},
    onOpenVaccination: () -> Unit,
    onOpenMortality: () -> Unit,
    onOpenQuarantine: () -> Unit,
    onOpenBreeding: () -> Unit,
    onNavigateToAddBird: () -> Unit = {},
    onNavigateToAddBatch: () -> Unit = {},
    onNavigateRoute: (String) -> Unit = {},
    onOpenRoosterCard: (String) -> Unit = {},
    onOpenBreedingCalculator: () -> Unit = {},
    onOpenPerformanceJournal: () -> Unit = {},
    onOpenVirtualArena: () -> Unit = {},
    // Premium Features Callbacks
    onOpenHallOfFame: () -> Unit = {},
    onOpenDigitalFarm: () -> Unit = {},
    // Farmer Parity Callbacks
    onOpenFarmAssets: () -> Unit = {},
    onOpenFarmLog: () -> Unit = {}
) {

    val vm: EnthusiastHomeViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()
    val refreshing by vm.isRefreshing.collectAsState()
    val swipeState = rememberSwipeRefreshState(isRefreshing = refreshing)
    val activePairs by vm.activePairs.collectAsState()
    val quickStatus by vm.quickStatus.collectAsState()
    
    // Premium component data (Comment 2)
    val topChampions by vm.topChampions.collectAsState()
    val trustScore by vm.trustScore.collectAsState()
    val urgentActivity by vm.urgentActivity.collectAsState()
    val pendingTaskCounts by vm.pendingTaskCounts.collectAsState()
    val scrollState = rememberScrollState()
    
    var now by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            now = System.currentTimeMillis()
        }
    }
    val flockVm: EnthusiastFlockViewModel = hiltViewModel()
    val flock by flockVm.state.collectAsState()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var speedDialExpanded by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val checklistVm: OnboardingChecklistViewModel = hiltViewModel()
    val checklistState by checklistVm.uiState.collectAsState()
    var showCelebration by remember { mutableStateOf(false) }
    LaunchedEffect(checklistState.showCelebration) {
        showCelebration = checklistState.showCelebration
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Replace FAB with SpeedDialActions (Comment 1)
            SpeedDialActions(
                isExpanded = speedDialExpanded,
                onExpandToggle = { speedDialExpanded = !speedDialExpanded },
                pendingTasks = mapOf(
                    SpeedDialAction.VACCINATION to (pendingTaskCounts["vaccination"] ?: 0),
                    SpeedDialAction.EGGS to (pendingTaskCounts["eggs"] ?: 0),
                    SpeedDialAction.BREEDING to (pendingTaskCounts["hatching"] ?: 0)
                ),
                onActionClick = { action ->
                    speedDialExpanded = false
                    when (action) {
                        SpeedDialAction.VACCINATION -> onOpenVaccination()
                        SpeedDialAction.EGGS -> showDialog = true
                        SpeedDialAction.ANALYTICS -> onOpenAnalytics()
                        SpeedDialAction.BREEDING -> onOpenBreeding()
                    }
                }
            )
        }
    ) { padding ->
        // Use trust-score-based aura (Comment 1)
        EnthusiastAuraBackground(trustScore = trustScore) {
            SwipeRefresh(state = swipeState, onRefresh = vm::refresh) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(Dimens.space_large)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(Dimens.space_large)
            ) {
        // Live Activity Ticker (Comment 1)
        LiveActivityTicker(
            activity = urgentActivity,
            onDismiss = { /* Handled by LiveActivityTicker auto-dismiss */ },
            onClick = { 
                when (urgentActivity) {
                    is com.rio.rostry.ui.enthusiast.components.UrgentActivity.HatchingDue -> onOpenBreeding()
                    is com.rio.rostry.ui.enthusiast.components.UrgentActivity.SickBirds -> onOpenQuarantine()
                    is com.rio.rostry.ui.enthusiast.components.UrgentActivity.Incubation -> onOpenBreeding()
                    is com.rio.rostry.ui.enthusiast.components.UrgentActivity.VaccinationDue -> onOpenVaccination()
                    else -> {}
                }
            }
        )

        // Hero Champion Banner (Comment 1)
        if (topChampions.isNotEmpty()) {
            HeroChampionBanner(
                champions = topChampions,
                scrollState = scrollState,
                onShareCard = { champion -> onOpenRoosterCard(champion.id) }
            )
        }

        PremiumGateCard(
            icon = Icons.Filled.Verified,
            title = "Premium Enthusiast",
            description = "Access advanced analytics, transfers, and leadership tools. Verify KYC to unlock full features.",
            actionText = "Verify KYC",
            onAction = onVerifyKyc
        )

        if (checklistState.isChecklistRelevant) {
            OnboardingChecklistCard(
                items = checklistState.items,
                completionPercentage = checklistState.completionPercentage,
                onNavigate = { route -> onNavigateRoute(route) },
                onDismiss = { checklistVm.dismissChecklist() }
            )
        }

        // Today's Focus Card - Shows most urgent item
        val hatchingDueCount = ui.hatchingDueCount
        if (hatchingDueCount > 0) {
            TodaysFocusCard(
                title = "$hatchingDueCount Eggs Ready to Hatch",
                subtitle = "Due within 7 days",
                icon = Icons.Filled.EggAlt,
                priority = FocusPriority.URGENT,
                ctaText = "Check Now",
                onCta = onOpenBreeding
            )
        } else if (ui.sickBirdsCount > 0) {
            TodaysFocusCard(
                title = "${ui.sickBirdsCount} Birds Need Attention",
                subtitle = "Health check required",
                icon = Icons.Filled.Warning,
                priority = FocusPriority.IMPORTANT,
                ctaText = "Review Now",
                onCta = onOpenQuarantine
            )
        }

        // Quick Stats Row - Horizontal scrollable KPIs
        QuickStatsRow(
            stats = listOf(
                EnthusiastStats.breedingSuccess(ui.dashboard.breedingSuccessRate.toFloat()),
                EnthusiastStats.transfers(ui.dashboard.transfers.toInt()),
                EnthusiastStats.trustScore(trustScore)
            ),
            onStatClick = { index ->
                when (index) {
                    0 -> onOpenAnalytics()
                    1 -> onOpenTransfers()
                    2 -> onOpenAnalytics()
                }
            },
            modifier = Modifier.padding(vertical = Dimens.space_medium)
        )

        // Top Bloodlines (simplified)
        if (ui.topBloodlines.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Top Bloodlines:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ui.topBloodlines.take(3).forEach { (id, eggs) ->
                    AssistChip(
                        onClick = { onOpenTraceability(id) },
                        label = { Text("${id.take(6)}…") }
                    )
                }
            }
        }

        // Contextual Action Bar - Smart 3 actions based on state
        ContextualActionBar(
            actions = buildList {
                // Show eggs action if eggs were collected today or need logging
                add(EnthusiastQuickActions.LOG_EGGS.copy(badgeCount = pendingTaskCounts["eggs"] ?: 0))
                // Show vaccination if due
                add(EnthusiastQuickActions.VACCINATION.copy(badgeCount = pendingTaskCounts["vaccination"] ?: 0))
                // Show breeding if pairs need attention
                add(EnthusiastQuickActions.BREEDING.copy(badgeCount = pendingTaskCounts["hatching"] ?: 0))
            },
            onActionClick = { actionId ->
                when (actionId) {
                    "log_eggs" -> showDialog = true
                    "vaccination" -> onOpenVaccination()
                    "breeding" -> onOpenBreeding()
                    "analytics" -> onOpenAnalytics()
                    "add_bird" -> onNavigateToAddBird()
                }
            },
            modifier = Modifier.padding(vertical = Dimens.space_medium)
        )

        // Breeding Management - Collapsed by default for cleaner UI
        CollapsibleSection(
            title = "Breeding",
            icon = Icons.Filled.Favorite,
            badgeCount = ui.pairsToMateCount + ui.hatchingDueCount,
            initiallyExpanded = ui.pairsToMateCount > 0 || ui.hatchingDueCount > 0
        ) {
            // Simple summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${ui.pairsToMateCount}", style = MaterialTheme.typography.headlineMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Pairs", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${ui.hatchingDueCount}", style = MaterialTheme.typography.headlineMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Hatching", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${ui.eggsCollectedToday}", style = MaterialTheme.typography.headlineMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Eggs", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(Modifier.size(Dimens.space_medium))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenBreeding, modifier = Modifier.weight(1f)) { Text("Manage") }
                Button(onClick = { showDialog = true }, modifier = Modifier.weight(1f)) { Text("Log Eggs") }
            }
        }
        
        // Monitoring - Collapsed by default
        CollapsibleSection(
            title = "Monitoring",
            icon = Icons.Filled.Timer,
            badgeCount = ui.sickBirdsCount,
            initiallyExpanded = ui.sickBirdsCount > 0
        ) {
            if (ui.sickBirdsCount > 0) {
                Button(
                    onClick = onOpenQuarantine,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${ui.sickBirdsCount} Birds Need Care")
                }
            } else {
                Text("✓ All birds healthy", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenPerformanceJournal, modifier = Modifier.weight(1f)) { Text("Journal") }
                OutlinedButton(onClick = onOpenVirtualArena, modifier = Modifier.weight(1f)) { Text("Arena") }
            }
            Spacer(Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenHallOfFame, modifier = Modifier.weight(1f)) { 
                    Text("🏆 Hall of Fame") 
                }
                OutlinedButton(onClick = onOpenDigitalFarm, modifier = Modifier.weight(1f)) { 
                    Text("🐔 Digital Farm") 
                }
            }
        }


        // Dialog for egg collection
        if (showDialog) {
            var selectedPairId by rememberSaveable { mutableStateOf(activePairs.firstOrNull()?.pairId ?: "") }
            var countText by rememberSaveable { mutableStateOf("") }
            var grade by rememberSaveable { mutableStateOf("A") }
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Log Eggs") },
                text = {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (activePairs.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                activePairs.take(4).forEach { p ->
                                    AssistChip(onClick = { selectedPairId = p.pairId }, label = { Text(p.pairId.take(6)) })
                                }
                            }
                        }
                        OutlinedTextField(value = countText, onValueChange = { countText = it.filter { ch -> ch.isDigit() } }, label = { Text("Count") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val count = countText.toIntOrNull() ?: 0
                        vm.quickCollectEggs(selectedPairId.trim(), count, grade.ifBlank { "A" }, null)
                        showDialog = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Transfers - Simple card if pending
        if (ui.pendingTransfersCount > 0 || ui.disputedTransfersCount > 0) {
            CollapsibleSection(
                title = "Transfers",
                icon = Icons.Filled.Send,
                badgeCount = ui.pendingTransfersCount + ui.disputedTransfersCount,
                initiallyExpanded = ui.disputedTransfersCount > 0
            ) {
                Text("${ui.pendingTransfersCount} pending • ${ui.disputedTransfersCount} disputed", 
                    style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onOpenTransfers, modifier = Modifier.fillMaxWidth()) { 
                    Text("Review Transfers") 
                }
            }
        }

        // Farm Operations - Collapsed always-available section
        CollapsibleSection(
            title = "Farm",
            icon = Icons.Filled.Agriculture,
            badgeCount = flock.vaccinationsDue
        ) {
            // Simple stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${flock.activeBirds}", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Birds", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${flock.breedingPairs}", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Pairs", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${flock.chicks}", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text("Chicks", style = MaterialTheme.typography.labelSmall)
                }
            }
            
            Spacer(Modifier.size(Dimens.space_medium))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpenFarmAssets, modifier = Modifier.weight(1f)) { Text("Manage Flock") }
                OutlinedButton(onClick = onOpenFarmLog, modifier = Modifier.weight(1f)) { Text("Farm Log") }
            }
            Spacer(Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenVaccination, modifier = Modifier.weight(1f)) { Text("Vaccinate") }
                OutlinedButton(onClick = onOpenMortality, modifier = Modifier.weight(1f)) { Text("Mortality") }
            }
        }

        // Alerts - Only show if there are any
        if (ui.alerts.isNotEmpty()) {
            EnthusiastAlertCard(
                alerts = ui.alerts,
                onDismiss = { vm.dismissAllAlerts() }
            )
        }
            }
        }
    }
}

    // Observe navigation events
    LaunchedEffect(Unit) {
        vm.navigationEvent.collect { route -> onNavigateRoute(route) }
    }

    // Observe error events and show snackbar
    LaunchedEffect(Unit) {
        vm.errorEvents.collect { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
    }

    // Loading overlay
    if (ui.isLoading) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            com.rio.rostry.ui.components.LoadingOverlay()
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

    if (showCelebration) {
        AlertDialog(
            onDismissRequest = { showCelebration = false },
            title = { Text("Congratulations!") },
            text = { Text("You've completed the onboarding checklist!") },
            confirmButton = {
                Button(onClick = { showCelebration = false }) {
                    Text("Continue")
                }
            }
        )
    }
}

@Composable
private fun PremiumGateCard(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(Dimens.icon_large))
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
                Button(onClick = onAction) { Text(actionText) }
                OutlinedButton(
                    onClick = onAction,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) { Text("Learn More") }
            }
        }
    }
}

private fun formatCountdown(ms: Long): String {
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

// =============== EXPLORE TAB ===============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastExploreScreenContent(
    onOpenProfile: (String) -> Unit,
    onOpenDiscussion: () -> Unit,
    onShare: (String) -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val vm: EnthusiastExploreViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    // Local UI states for search and filter visibility
    var searchText by rememberSaveable { mutableStateOf("") }
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }

    // Client-side quick filter for display
    val displayed = if (searchText.isBlank()) state.items else state.items.filter {
        it.name.contains(searchText, ignoreCase = true) || it.category.contains(searchText, ignoreCase = true)
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Explore") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onNavigateBack) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search and filters toggle
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search icon") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { filtersExpanded = !filtersExpanded }) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filters")
                }
            }

            if (filtersExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.priceRange, onValueChange = { vm.update("priceRange", it) }, label = { Text("Price Range (e.g. 100-500)") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = state.region, onValueChange = { vm.update("region", it) }, label = { Text("Region") })
                        OutlinedTextField(value = state.traits, onValueChange = { vm.update("traits", it) }, label = { Text("Traits") })
                    }
                    // Sort controls
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.update("sort", EnthusiastExploreViewModel.SortOption.RECENCY.name) }, enabled = state.sort != EnthusiastExploreViewModel.SortOption.RECENCY) { Text("Recency") }
                        OutlinedButton(onClick = { vm.update("sort", EnthusiastExploreViewModel.SortOption.VERIFIED_FIRST.name) }, enabled = state.sort != EnthusiastExploreViewModel.SortOption.VERIFIED_FIRST) { Text("Verified") }
                        OutlinedButton(onClick = { vm.update("sort", EnthusiastExploreViewModel.SortOption.ENGAGEMENT.name) }, enabled = state.sort != EnthusiastExploreViewModel.SortOption.ENGAGEMENT) { Text("Engagement") }
                        OutlinedButton(onClick = { vm.update("sort", EnthusiastExploreViewModel.SortOption.PRICE_ASC.name) }, enabled = state.sort != EnthusiastExploreViewModel.SortOption.PRICE_ASC) { Text("Price ↑") }
                        OutlinedButton(onClick = { vm.update("sort", EnthusiastExploreViewModel.SortOption.PRICE_DESC.name) }, enabled = state.sort != EnthusiastExploreViewModel.SortOption.PRICE_DESC) { Text("Price ↓") }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.refresh() }, enabled = !state.loading) { Text(if (state.loading) "Searching..." else "Search") }
                OutlinedButton(onClick = onOpenDiscussion) { Text("Open Breeding Community") }
            }
            Divider()
            Text("Results")
            state.error?.let { err ->
                ElevatedCard { Column(Modifier.padding(12.dp)) { Text("Error: $err") } }
            }
            if (state.loading && displayed.isEmpty()) {
                androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
                    com.rio.rostry.ui.components.LoadingOverlay()
                }
            }
            if (!state.loading && displayed.isEmpty()) {
                com.rio.rostry.ui.components.EmptyState(
                    title = "No results",
                    subtitle = "Try adjusting breed, price, region, or traits",
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(displayed) { item ->
                    ElevatedCard {
                        Row(Modifier.padding(12.dp)) {
                            Text(item.name.ifBlank { item.category }, modifier = Modifier.weight(1f))
                            TextButton(onClick = { onShare(item.productId) }) { Text("Share") }
                        }
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth().padding(8.dp)) {
                        if (state.isLoadingMore) {
                            CircularProgressIndicator()
                        } else if (state.hasMore && !state.loading) {
                            OutlinedButton(onClick = { vm.loadMore() }, enabled = state.hasMore && !state.loading) { Text("Load more") }
                        }
                    }
                }
            }
        }
    }
}

// =============== CREATE TAB ===============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastCreateScreen(
    onScheduleContent: (String) -> Unit,
    onStartLive: () -> Unit,
    onCreateShowcase: (String) -> Unit,
    onOpenRoosterCard: (String) -> Unit
) {
    // Local ViewModel to fetch birds for selection
    val flockVm: EnthusiastFlockViewModel = hiltViewModel()
    val flockState by flockVm.state.collectAsState()
    var showBirdSelection by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Media Studio & Creation Tools")
        
        // Rooster Card Generator
        ElevatedCard(onClick = { showBirdSelection = true }) {
            Row(
                modifier = Modifier.padding(16.dp), 
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(Icons.Filled.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.weight(1f)) {
                    Text("Rooster Card Generator", style = MaterialTheme.typography.titleMedium)
                    Text("Create viral WWE-style cards for your birds.", style = MaterialTheme.typography.bodyMedium)
                }
                Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.rotate(180f))
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Live Broadcasting")
                Text("Prepare a live session with title and audience targeting.")
                var title by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Live Title") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    var audience by rememberSaveable { mutableIntStateOf(0) }
                    OutlinedButton(onClick = { audience = 0 }, enabled = audience != 0) { Text("Public") }
                    OutlinedButton(onClick = { audience = 1 }, enabled = audience != 1) { Text("Followers") }
                    OutlinedButton(onClick = { audience = 2 }, enabled = audience != 2) { Text("Verified") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onScheduleContent(title) }) { Text("Schedule") }
                    Button(onClick = onStartLive, enabled = title.isNotBlank()) { Text("Go Live") }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Professional Showcase Post")
                var caption by rememberSaveable { mutableStateOf("") }
                OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Caption") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onCreateShowcase(caption) }) { Text("Publish Showcase") }
                    TextButton(onClick = { /* open editor */ }) { Text("Open Editor") }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Breeding Documentation & Educational Content")
                Text("Attach lineage notes, pair performance, incubation data.")
                OutlinedButton(onClick = { onScheduleContent("Breeding Notes") }) { Text("Create Document") }
            }
        }
    }

    if (showBirdSelection) {
        AlertDialog(
            onDismissRequest = { showBirdSelection = false },
            title = { Text("Select Bird") },
            text = {
                // In a real app, this should be a lazy list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                     // Temporary: Input ID manually if list is empty or for debugging
                    var manualId by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(value = manualId, onValueChange = { manualId = it }, label = { Text("Enter Bird ID") })
                    Button(
                        onClick = { 
                            if (manualId.isNotBlank()) {
                                showBirdSelection = false
                                onOpenRoosterCard(manualId)
                            }
                        },
                        enabled = manualId.isNotBlank()
                    ) { Text("Go") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBirdSelection = false }) { Text("Cancel") }
            }
        )
    }
}


// =============== DASHBOARD TAB ===============
@Composable
fun EnthusiastDashboardHost(
    onOpenReports: () -> Unit,
    onOpenFeed: () -> Unit,
    onOpenTraceability: (String) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        // Reuse existing comprehensive analytics screen
        com.rio.rostry.ui.analytics.EnthusiastDashboardScreen(
            onOpenReports = onOpenReports,
            onOpenFeed = onOpenFeed
        )
        // Quick access to family tree visualization
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Column(Modifier.padding(16.dp)) {
            Text("Family Tree & Monitoring Shortcuts")
            var id by rememberSaveable { mutableStateOf("") }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("Root ID") })
                Button(onClick = { if (id.isNotBlank()) onOpenTraceability(id) }) { Text("Open Tree") }
            }
        }
    }
}

// Transfers tab moved to separate screen file: EnthusiastTransfersScreen.kt
