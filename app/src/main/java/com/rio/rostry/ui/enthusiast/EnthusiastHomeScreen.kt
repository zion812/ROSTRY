package com.rio.rostry.ui.enthusiast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import com.rio.rostry.ui.components.AddToFarmDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState

/**
 * Advanced Enthusiast interface with premium farm management features.
 * Tabs supported by Routes.EnthusiastNav: Home, Explore, Create, Dashboard, Transfers
 */

// =============== HOME TAB ===============
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
) {
    val vm: EnthusiastHomeViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()
    val refreshing by vm.isRefreshing.collectAsState()
    val swipeState = rememberSwipeRefreshState(isRefreshing = refreshing)
    val activePairs by vm.activePairs.collectAsState()
    val quickStatus by vm.quickStatus.collectAsState()
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add to Farm")
            }
        }
    ) { padding ->
        SwipeRefresh(state = swipeState, onRefresh = vm::refresh) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
        PremiumGateCard(
            title = "Premium Enthusiast",
            description = "Access advanced analytics, transfers, and leadership tools. Verify KYC to unlock full features.",
            actionText = "Verify KYC",
            onAction = onVerifyKyc
        )

        // KPI summary card
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("KPI Summary (Last 30 days)")
                Text("• Breeding Success: ${"%.0f".format(ui.dashboard.breedingSuccessRate * 100)}% • Transfers: ${ui.dashboard.transfers}")
                Text("• Engagement Score: ${ui.dashboard.engagementScore} • Pending: ${ui.pendingTransfersCount} • Disputed: ${ui.disputedTransfersCount}")
                if (ui.topBloodlines.isNotEmpty()) {
                    Text("Top Bloodlines (by eggs this week)")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ui.topBloodlines.forEach { (id, eggs) ->
                            AssistChip(onClick = { onOpenTraceability(id) }, label = { Text("${id.take(8)}… ($eggs)") })
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onOpenAnalytics) { Text("Open Analytics") }
                    OutlinedButton(onClick = onOpenPerformanceAnalytics) { Text("Performance") }
                    OutlinedButton(onClick = onOpenFinancialAnalytics) { Text("Financial") }
                    OutlinedButton(onClick = onOpenNotifications) { Text("Notifications") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onOpenReports) { Text("Reports") }
                    OutlinedButton(onClick = onOpenMonitoringDashboard) { Text("Monitoring") }
                }
            }
        }

        // Pull-to-refresh replaces manual refresh button

        // Farm fetcher cards
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Pairs To Mate")
            Text("• ${ui.pairsToMateCount} pairs need attention")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenBreeding) { Text("View Pairs") }
                if (ui.pairsToMateCount == 0) {
                    OutlinedButton(onClick = onOpenBreeding) { Text("Create Pair") }
                }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Eggs Collected Today")
            Text("• ${ui.eggsCollectedToday} eggs logged")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var showDialog by rememberSaveable { mutableStateOf(false) }
                OutlinedButton(onClick = { showDialog = true }) { Text("Log Collection") }
                if (showDialog) {
                    // Hoist dialog inputs so both text and buttons can access them
                    var selectedPairId by rememberSaveable { mutableStateOf(activePairs.firstOrNull()?.pairId ?: "") }
                    var countText by rememberSaveable { mutableStateOf("") }
                    var grade by rememberSaveable { mutableStateOf("A") }
                    var weightText by rememberSaveable { mutableStateOf("") }
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Quick Egg Collection") },
                        text = {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Pair selector (simple chips from active pairs)
                                if (activePairs.isEmpty()) {
                                    Text("No active pairs found")
                                } else {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                        activePairs.take(4).forEach { p ->
                                            AssistChip(onClick = { selectedPairId = p.pairId }, label = { Text(p.pairId.take(6)) })
                                        }
                                    }
                                }
                                OutlinedTextField(value = selectedPairId, onValueChange = { selectedPairId = it }, label = { Text("Pair ID") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = countText, onValueChange = { countText = it.filter { ch -> ch.isDigit() } }, label = { Text("Egg count") }, modifier = Modifier.fillMaxWidth())
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = grade, onValueChange = { grade = it.take(2).uppercase() }, label = { Text("Grade (A/B/C)") }, modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = weightText, onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Weight (g, optional)") }, modifier = Modifier.weight(1f))
                                }
                                quickStatus?.let { msg -> Text(msg) }
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                val count = countText.toIntOrNull() ?: 0
                                val weight = weightText.toDoubleOrNull()
                                vm.quickCollectEggs(selectedPairId.trim(), count, grade.ifBlank { "A" }, weight)
                                showDialog = false
                            }) { Text("Save") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        },
                    )
                }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Incubation Timers")
            if (ui.incubationTimers.isEmpty()) {
                Text("No active batches")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onNavigateToAddBatch) { Text("Start Batch") }
                }
            } else {
                ui.incubationTimers.take(5).forEach { t ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(t.name, modifier = Modifier.weight(1f))
                        val remaining = (t.expectedHatchAt ?: 0L) - now
                        val text = if (remaining > 0) formatCountdown(remaining) else "–"
                        Text("ETA: $text")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenBreeding) { Text("View Batches") }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Hatching Due (7 days)")
            Text("• ${ui.hatchingDueCount} batches due soon")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenBreeding) { Text("View Schedule") }
                if (ui.hatchingDueCount == 0) {
                    OutlinedButton(onClick = onNavigateToAddBatch) { Text("Start Batch") }
                }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Weekly Growth Updates")
            Text("• ${ui.weeklyGrowthUpdatesCount} growth records this week")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenMonitoringDashboard) { Text("Log Growth") }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Breeder Status Checks")
            if (ui.breederStatusChecks.isEmpty()) Text("No breeder updates") else {
                ui.breederStatusChecks.take(5).forEach { s ->
                    Row(Modifier.fillMaxWidth()) {
                        Text("Pair ${s.pairId}", modifier = Modifier.weight(1f))
                        Text("${"%.0f".format(s.hatchSuccessRate * 100)}%")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenBreeding) { Text("View Details") }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Transfers")
            Text("• Pending: ${ui.pendingTransfersCount} • Disputed: ${ui.disputedTransfersCount}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenTransfers) { Text("Verify") }
                OutlinedButton(onClick = onOpenTransfers) { Text("New Transfer") }
            }
        } }

        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Events Today")
            if (ui.eventsToday.isEmpty()) Text("No events today") else {
                ui.eventsToday.take(5).forEach { e ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(e.title, modifier = Modifier.weight(1f))
                        Text("Starts: ${e.startTime}")
                    }
                }
            }
        } }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Critical Alerts")
                if (ui.alerts.isEmpty()) {
                    Text("No critical alerts")
                } else {
                    ui.alerts.forEach { a ->
                        Text("• [${a.severity}] ${a.message}")
                    }
                }
            }
        }

        // Legacy flock board retained below
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Flock Board")
                Text("• Active birds: ${flock.activeBirds} • Breeding pairs: ${flock.breedingPairs} • Chicks: ${flock.chicks}")
                Text("• Vaccinations due: ${flock.vaccinationsDue} • Recent mortality: ${flock.recentMortality}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onOpenVaccination) { Text("Vaccination Schedule") }
                    OutlinedButton(onClick = onOpenMortality) { Text("Log Mortality") }
                    OutlinedButton(onClick = onOpenQuarantine) { Text("Start Quarantine") }
                    OutlinedButton(onClick = onOpenBreeding) { Text("Breeding Mgmt") }
                }
            }
        }

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Top Sellers & Market Insights")
                Text("• Trending breed: Malay • Avg price ↑ 12% WoW")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    var id by rememberSaveable { mutableStateOf("") }
                    OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("Trace ID") })
                    Button(onClick = { if (id.isNotBlank()) onOpenTraceability(id) }) { Text("Trace") }
                }
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
}

@Composable
private fun PremiumGateCard(
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
) {
    Card {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title)
            Text(description)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAction) { Text(actionText) }
                TextButton(onClick = onAction) { Text("Learn More") }
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
    Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Search bar with filter toggle
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { filtersExpanded = !filtersExpanded }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filters")
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        if (filtersExpanded) {
            Text("Advanced Search")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = state.breed, onValueChange = { vm.update("breed", it) }, label = { Text("Breed") })
                OutlinedTextField(value = state.priceRange, onValueChange = { vm.update("priceRange", it) }, label = { Text("Price Range (e.g. 100-500)") })
            }
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
) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Media Studio & Creation Tools")
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
