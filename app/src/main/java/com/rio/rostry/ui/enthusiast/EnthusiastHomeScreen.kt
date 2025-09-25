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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
) {
    val vm: EnthusiastHomeViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()
    val flockVm: EnthusiastFlockViewModel = hiltViewModel()
    val flock by flockVm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumGateCard(
            title = "Premium Enthusiast",
            description = "Access advanced analytics, transfers, and leadership tools. Verify KYC to unlock full features.",
            actionText = "Verify KYC",
            onAction = onVerifyKyc
        )

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Farm Overview Dashboard")
                Text("• Breeding success: ${"%.0f".format(ui.dashboard.breedingSuccessRate * 100)}% • Transfers(30d): ${ui.dashboard.transfers}")
                Text("• Engagement(7d): ${ui.dashboard.engagementScore} • Pending verifications: ${ui.pendingTransfersCount}")
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

        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Rank Board")
                Text("• Suggestions:")
                ui.dashboard.suggestions.take(3).forEach { s -> Text("- $s") }
                TextButton(onClick = onOpenProfile) { Text("View Profile & Badges") }
            }
        }

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
                Text("Critical Alerts")
                if (flock.alerts.isEmpty()) {
                    Text("No critical alerts")
                } else {
                    flock.alerts.forEach { a ->
                        Text("• [${a.severity}] ${a.title}: ${a.description}")
                    }
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

// =============== EXPLORE TAB ===============
@Composable
fun EnthusiastExploreScreen(
    onOpenProfile: (String) -> Unit,
    onOpenDiscussion: () -> Unit,
    onShare: (String) -> Unit,
) {
    val vm: EnthusiastExploreViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { vm.refresh() }, enabled = !state.loading) { Text(if (state.loading) "Searching..." else "Search") }
            OutlinedButton(onClick = onOpenDiscussion) { Text("Open Breeding Community") }
        }
        Divider()
        Text("Results")
        if (state.error != null) {
            ElevatedCard { Column(Modifier.padding(12.dp)) { Text("Error: ${state.error}") } }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.items) { item ->
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
                        OutlinedButton(onClick = { vm.loadMore() }) { Text("Load more") }
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

// =============== TRANSFERS TAB ===============
@Composable
fun EnthusiastTransfersScreen(
    onOpenTransfer: (String) -> Unit,
    onVerifyTransfer: (String) -> Unit,
    onCreateTransfer: () -> Unit,
    onOpenTraceability: (String) -> Unit,
) {
    val vm: EnthusiastTransferViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ownership Transfer Management")
        if (state.loading) {
            Text("Loading...")
        }
        state.error?.let { err ->
            ElevatedCard { Column(Modifier.padding(12.dp)) { Text("Error: $err") } }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Pending Transfers (Verification Required)")
                if (state.pending.isEmpty() && !state.loading) {
                    Text("No pending transfers")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(state.pending) { t ->
                            Row(Modifier.fillMaxWidth()) {
                                Text(t.transferId, modifier = Modifier.weight(1f))
                                Text("${t.type} • ${t.status}")
                                Spacer(modifier = Modifier.padding(4.dp))
                                TextButton(onClick = { onOpenTransfer(t.transferId) }) { Text("Details") }
                                Spacer(modifier = Modifier.padding(2.dp))
                                TextButton(onClick = { onVerifyTransfer(t.transferId) }) { Text("Verify") }
                                if (!t.productId.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.padding(2.dp))
                                    TextButton(onClick = { onOpenTraceability(t.productId!!) }) { Text("Chain") }
                                }
                            }
                        }
                    }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("History & Documentation")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCreateTransfer) { Text("New Transfer") }
                    TextButton(onClick = { vm.refresh() }) { Text("Refresh") }
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(state.history) { t ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(t.transferId, modifier = Modifier.weight(1f))
                            Text("${t.type} • ${t.status}")
                            Spacer(modifier = Modifier.padding(4.dp))
                            TextButton(onClick = { onOpenTransfer(t.transferId) }) { Text("Details") }
                            if (!t.productId.isNullOrBlank()) {
                                Spacer(modifier = Modifier.padding(2.dp))
                                TextButton(onClick = { onOpenTraceability(t.productId!!) }) { Text("Chain") }
                            }
                        }
                    }
                }
            }
        }
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Verification Steps")
                Text("• Photo: before/after • GPS confirm • Digital signature • Platform verification")
            }
        }
    }
}
