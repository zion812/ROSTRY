package com.rio.rostry.ui.enthusiast.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.navigation.NavHostController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.rio.rostry.ui.navigation.Routes

@Composable
fun EnthusiastDashboardTabs(
    onOpenReports: () -> Unit,
    onOpenFeed: () -> Unit,
    onOpenTraceability: (String) -> Unit,
    navController: NavHostController,
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Family Tree", "Breeding", "Analytics", "Documents")

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { idx, title ->
                Tab(selected = tabIndex == idx, onClick = { tabIndex = idx }, text = { Text(title) })
            }
        }
        when (tabIndex) {
            0 -> OverviewTab(onOpenReports, onOpenFeed)
            1 -> FamilyTreeTab(onOpenTraceability, navController)
            2 -> BreedingTab()
            3 -> AnalyticsTab(onOpenReports, onOpenFeed)
            4 -> DocumentsTab()
        }
    }
}

@Composable
private fun OverviewTab(onOpenReports: () -> Unit, onOpenFeed: () -> Unit) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        com.rio.rostry.ui.analytics.EnthusiastDashboardScreen(
            onOpenReports = onOpenReports,
            onOpenFeed = onOpenFeed
        )
    }
}

@Composable
private fun FamilyTreeTab(onOpenTraceability: (String) -> Unit, navController: NavHostController) {
    val vm: EnthusiastFamilyTreeViewModel = hiltViewModel()
    val rootId by vm.rootId.collectAsState()
    val showAnc by vm.showAnc.collectAsState()
    val showDesc by vm.showDesc.collectAsState()
    val depthState by vm.depth.collectAsState()
    val layersUp by vm.layersUp.collectAsState()
    val layersDown by vm.layersDown.collectAsState()
    val edges by vm.edges.collectAsState()
    var depth by rememberSaveable { mutableStateOf(depthState.toFloat()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(navController) {
        val handle = navController.currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<String>("scannedProductId")?.observe(lifecycleOwner) { id ->
            if (!id.isNullOrBlank()) {
                vm.setRoot(id)
                handle.remove<String>("scannedProductId")
            }
        }
    }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Family Tree Explorer")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rootId,
                        onValueChange = { vm.setRoot(it) },
                        label = { Text("Root ID") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { if (rootId.isNotBlank()) onOpenTraceability(rootId) }) { Text("Open") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { vm.toggleAnc() }) { Text(if (showAnc) "Hide Ancestors" else "Show Ancestors") }
                    OutlinedButton(onClick = { vm.toggleDesc() }) { Text(if (showDesc) "Hide Descendants" else "Show Descendants") }
                    OutlinedButton(onClick = { navController.navigate(Routes.SCAN_QR) }) { Text("Scan QR") }
                }
                Text("Depth: ${'$'}{depth.toInt()} generations")
                Slider(
                    value = depth,
                    onValueChange = { 
                        depth = it.coerceIn(1f, 5f)
                        vm.setDepth(depth.toInt())
                    },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors()
                )
                // Embedded interactive view
                if (rootId.isNotBlank()) {
                    com.rio.rostry.ui.traceability.FamilyTreeView(
                        rootId = rootId,
                        layersUp = if (showAnc) layersUp else emptyMap(),
                        layersDown = if (showDesc) layersDown else emptyMap(),
                        edges = edges,
                        resetKey = depth.toInt(),
                        onNodeClick = { id -> onOpenTraceability(id) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { if (rootId.isNotBlank()) onOpenTraceability(rootId) }) { Text("Open Full View") }
                }
            }
        }
    }
}

@Composable
private fun BreedingTab() {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Breeding Insights")
            Text("Use the Breeding Flow to manage pairs, eggs, and incubation.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { /* navigate to breeding flow via Routes.MONITORING_BREEDING */ }) { Text("Open Breeding Flow") }
                OutlinedButton(onClick = { /* navigate to hatching via Routes.MONITORING_HATCHING */ }) { Text("Open Hatching") }
            }
        } }
    }
}

@Composable
private fun AnalyticsTab(onOpenReports: () -> Unit, onOpenFeed: () -> Unit) {
    // Date range filters (epoch ms)
    var start by rememberSaveable { mutableStateOf("") }
    var end by rememberSaveable { mutableStateOf("") }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Analytics Filters")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = start, onValueChange = { start = it }, label = { Text("Start (epoch ms)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = end, onValueChange = { end = it }, label = { Text("End (epoch ms)") }, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { /* propagate filters to dashboard VM if needed */ }) { Text("Apply") }
            }
        } }
        // Inline charts using ChartComponents
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Breeding Success per Pair")
            com.rio.rostry.ui.analytics.BarChartView(
                data = listOf(
                    com.rio.rostry.ui.analytics.BarDatum("P1", 0.8f),
                    com.rio.rostry.ui.analytics.BarDatum("P2", 0.6f),
                    com.rio.rostry.ui.analytics.BarDatum("P3", 0.9f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Hatch Success by Batch")
            com.rio.rostry.ui.analytics.LineChartView(
                points = listOf(
                    com.rio.rostry.ui.analytics.LinePoint("B1", 60f),
                    com.rio.rostry.ui.analytics.LinePoint("B2", 72f),
                    com.rio.rostry.ui.analytics.LinePoint("B3", 81f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Mortality Trends")
            com.rio.rostry.ui.analytics.LineChartView(
                points = listOf(
                    com.rio.rostry.ui.analytics.LinePoint("W1", 3f),
                    com.rio.rostry.ui.analytics.LinePoint("W2", 2f),
                    com.rio.rostry.ui.analytics.LinePoint("W3", 1f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        } }
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Growth Variance & Trait Outcomes")
            com.rio.rostry.ui.analytics.PieChartView(
                slices = listOf(
                    com.rio.rostry.ui.analytics.PieSlice("Trait A", 40f, androidx.compose.ui.graphics.Color(0xFF64B5F6)),
                    com.rio.rostry.ui.analytics.PieSlice("Trait B", 35f, androidx.compose.ui.graphics.Color(0xFF81C784)),
                    com.rio.rostry.ui.analytics.PieSlice("Trait C", 25f, androidx.compose.ui.graphics.Color(0xFFFFB74D))
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenReports) { Text("Open Reports") }
                OutlinedButton(onClick = onOpenFeed) { Text("Open Feed") }
            }
        } }
    }
}

@Composable
private fun DocumentsTab() {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Documents & Certificates")
            Text("Upload, manage, and export enthusiast documents.")
        } }
    }
}
