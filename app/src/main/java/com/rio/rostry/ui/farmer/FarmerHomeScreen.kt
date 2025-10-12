package com.rio.rostry.ui.farmer

import androidx.compose.foundation.layout.*
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

data class FetcherCard(
    val title: String,
    val count: Int,
    val badgeCount: Int = 0,
    val badgeColor: Color = Color.Transparent,
    val icon: ImageVector,
    val action: String,
    val onClick: () -> Unit
)

@Composable
fun FarmerHomeScreen(
    viewModel: FarmerHomeViewModel = hiltViewModel(),
    onOpenDailyLog: () -> Unit = {},
    onOpenTasks: () -> Unit = {},
    onOpenVaccination: () -> Unit = {},
    onOpenGrowth: () -> Unit = {},
    onOpenQuarantine: () -> Unit = {},
    onOpenHatching: () -> Unit = {},
    onOpenMortality: () -> Unit = {},
    onOpenBreeding: () -> Unit = {},
    onOpenListing: () -> Unit = {},
    onOpenAlerts: () -> Unit = {},
    onNavigateToGrowthWithList: (String) -> Unit = {},
    onNavigateToAddBird: () -> Unit = {},
    onNavigateToAddBatch: () -> Unit = {},
    onNavigateRoute: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val refreshingState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add to Farm")
            }
        }
    ) { padding ->
        SwipeRefresh(state = refreshingState, onRefresh = { viewModel.refreshData() }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Weekly KPI Cards
            uiState.weeklySnapshot?.let { snapshot ->
                Text(
                    "Weekly Performance",
                    style = MaterialTheme.typography.titleLarge
                )
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

            // Alerts Banner
            if (uiState.unreadAlerts.isNotEmpty()) {
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
                            Icon(Icons.Filled.Warning, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${uiState.unreadAlerts.size} Urgent Alerts",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null)
                    }
                }
            }

            // Fetcher Grid
            Text(
                "Farm Monitoring",
                style = MaterialTheme.typography.titleLarge
            )

            val fetcherCards = listOf(
                // Daily Log (Sprint 1 priority)
                FetcherCard(
                    title = "Daily Log",
                    count = uiState.dailyLogsThisWeek,
                    icon = Icons.Filled.EditNote,
                    action = "Log Today",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_DAILY_LOG) }
                ),
                // Tasks (Sprint 1 priority)
                FetcherCard(
                    title = "Tasks Due",
                    count = uiState.tasksDueCount,
                    badgeCount = uiState.tasksOverdueCount,
                    badgeColor = Color.Red,
                    icon = Icons.Filled.Task,
                    action = "View Tasks",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_TASKS) }
                ),
                FetcherCard(
                    title = "Vaccination Today",
                    count = uiState.vaccinationDueCount,
                    badgeCount = uiState.vaccinationOverdueCount,
                    badgeColor = Color.Red,
                    icon = Icons.Filled.Vaccines,
                    action = "View Schedule",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_VACCINATION) }
                ),
                FetcherCard(
                    title = "Growth Updates",
                    count = uiState.growthRecordsThisWeek,
                    icon = Icons.Filled.TrendingUp,
                    action = "Record Growth",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_GROWTH) }
                ),
                FetcherCard(
                    title = "Quarantine 12h",
                    count = uiState.quarantineActiveCount,
                    badgeCount = uiState.quarantineUpdatesDue,
                    badgeColor = MaterialTheme.colorScheme.tertiary,
                    icon = Icons.Filled.LocalHospital,
                    action = "Update Now",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_QUARANTINE) }
                ),
                FetcherCard(
                    title = "Hatching Batches",
                    count = uiState.hatchingBatchesActive,
                    badgeCount = uiState.hatchingDueThisWeek,
                    badgeColor = MaterialTheme.colorScheme.secondary,
                    icon = Icons.Filled.EggAlt,
                    action = "View Batches",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_HATCHING) }
                ),
                FetcherCard(
                    title = "Mortality Log",
                    count = uiState.mortalityLast7Days,
                    icon = Icons.Filled.Warning,
                    action = "Report Mortality",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_MORTALITY) }
                ),
                FetcherCard(
                    title = "Breeding Pairs",
                    count = uiState.breedingPairsActive,
                    icon = Icons.Filled.Favorite,
                    action = "Manage Pairs",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_BREEDING) }
                ),
                FetcherCard(
                    title = "Ready to List",
                    count = uiState.productsReadyToListCount,
                    badgeCount = if (uiState.productsReadyToListCount > 0) uiState.productsReadyToListCount else 0,
                    badgeColor = MaterialTheme.colorScheme.primaryContainer,
                    icon = Icons.Filled.Storefront,
                    action = "Quick List",
                    onClick = { viewModel.navigateToModule(Routes.MONITORING_GROWTH) } // Navigate to growth tracking to see products
                ),
                FetcherCard(
                    title = "New Listing",
                    count = 0,
                    icon = Icons.Filled.AddCircle,
                    action = "Create Listing",
                    onClick = { viewModel.navigateToModule(Routes.PRODUCT_CREATE) }
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(fetcherCards) { card ->
                    FetcherCardItem(card)
                }
            }
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
            .height(140.dp),
        onClick = card.onClick
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
                    card.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                if (card.badgeCount > 0) {
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
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    card.count.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
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
