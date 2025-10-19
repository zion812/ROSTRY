package com.rio.rostry.ui.enthusiast.breeding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.data.database.entity.EggCollectionEntity
import com.rio.rostry.data.database.entity.HatchingBatchEntity
import com.rio.rostry.data.database.entity.HatchingLogEntity
import com.rio.rostry.data.database.entity.MatingLogEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.EnthusiastBreedingRepository
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.launch
import com.rio.rostry.data.database.dao.BreedingPairDao
import com.rio.rostry.data.database.dao.MatingLogDao
import com.rio.rostry.data.database.dao.EggCollectionDao
import com.rio.rostry.data.database.dao.HatchingBatchDao
import com.rio.rostry.data.database.dao.HatchingLogDao
import com.rio.rostry.ui.analytics.BarChartView
import com.rio.rostry.ui.analytics.BarDatum
import com.rio.rostry.utils.Resource

@Composable
fun BreedingFlowScreen(
    onNavigateBack: () -> Unit,
    onOpenPairDetail: (String) -> Unit,
    onOpenBatchDetail: (String) -> Unit,
    vm: BreedingFlowViewModel = hiltViewModel()
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Pairs", "Mating", "Eggs", "Incubation", "Hatching")
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(vm.message) {
        vm.message.collect { msg -> if (!msg.isNullOrBlank()) snackbar.showSnackbar(msg) }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SnackbarHost(hostState = snackbar) { data -> Snackbar(snackbarData = data) }
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { idx, title ->
                Tab(selected = tabIndex == idx, onClick = { tabIndex = idx }, text = { Text(title) })
            }
        }

        when (tabIndex) {
            0 -> PairsTab(vm, onOpenPairDetail)
            1 -> MatingTab(vm)
            2 -> EggsTab(vm)
            3 -> IncubationTab(vm, onOpenBatchDetail)
            4 -> HatchingTab(vm)
        }
    }
}

@Composable
private fun PairsTab(vm: BreedingFlowViewModel, onOpenPairDetail: (String) -> Unit) {
    var showCreate by rememberSaveable { mutableStateOf(false) }
    var male by rememberSaveable { mutableStateOf("") }
    var female by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    val pairs by vm.pairs.collectAsState()
    ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Active Pairs")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pairs) { pair ->
                ElevatedCard(onClick = { onOpenPairDetail(pair.pairId) }) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${'$'}{pair.maleProductId.take(8)} × ${'$'}{pair.femaleProductId.take(8)}")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Eggs: ${'$'}{pair.eggsCollected}")
                            Text("Hatch: ${'$'}{(pair.hatchSuccessRate * 100).toInt()}%")
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { vm.openMatingDialogFor(pair.pairId) }, enabled = !vm.submitting.collectAsState().value) { Text("Log Mating") }
                            OutlinedButton(onClick = { vm.openEggDialogFor(pair.pairId) }, enabled = !vm.submitting.collectAsState().value) { Text("Collect Eggs") }
                            OutlinedButton(onClick = { vm.navigateToIncubationFromPair(pair.pairId) }) { Text("Start Incubation") }
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { showCreate = true }, enabled = !vm.submitting.collectAsState().value) { Text("Create Pair") }
        }
        if (showCreate) {
            AlertDialog(
                onDismissRequest = { showCreate = false },
                title = { Text("Create Pair") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = male, onValueChange = { male = it }, label = { Text("Male Product ID") })
                        OutlinedTextField(value = female, onValueChange = { female = it }, label = { Text("Female Product ID") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes (optional)") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            vm.createPair(male.trim(), female.trim(), notes.ifBlank { null })
                            showCreate = false
                        },
                        enabled = male.isNotBlank() && female.isNotBlank() && !vm.submitting.collectAsState().value
                    ) { Text("Create") }
                },
                dismissButton = { OutlinedButton(onClick = { showCreate = false }) { Text("Cancel") } }
            )
        }
    } }
}

@Composable
private fun MatingTab(vm: BreedingFlowViewModel) {
    var show by rememberSaveable { mutableStateOf(false) }
    var pairId by rememberSaveable { mutableStateOf("") }
    var observed by rememberSaveable { mutableStateOf("") }
    var conditions by rememberSaveable { mutableStateOf("") }
    val pairs by vm.pairs.collectAsState()
    val matingsByPair by vm.matingsByPair.collectAsState()
    ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Mating (last 2 weeks)")
        // Simple bar chart of matings per week across all pairs
        val chartData = vm.matingsPerWeekChart.collectAsState().value
        BarChartView(data = chartData, modifier = Modifier.fillMaxWidth())
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pairs) { p ->
                val logs = matingsByPair[p.pairId].orEmpty()
                ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pair ${'$'}{p.pairId.take(6)} — ${'$'}{logs.size} events")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { pairId = p.pairId; show = true }, enabled = !vm.submitting.collectAsState().value) { Text("Log Mating") }
                    }
                } }
            }
        }
        if (show) {
            AlertDialog(
                onDismissRequest = { show = false },
                title = { Text("Log Mating") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = pairId, onValueChange = { pairId = it }, label = { Text("Pair ID") })
                        OutlinedTextField(value = observed, onValueChange = { observed = it }, label = { Text("Observed Behavior") })
                        OutlinedTextField(value = conditions, onValueChange = { conditions = it }, label = { Text("Conditions") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (pairId.isBlank()) { vm.postMessage("Select a pair"); return@Button }
                        vm.logMating(pairId.trim(), observed.ifBlank { null }, conditions.ifBlank { null })
                        show = false
                    }, enabled = !vm.submitting.collectAsState().value) { Text("Save") }
                },
                dismissButton = { OutlinedButton(onClick = { show = false }) { Text("Cancel") } }
            )
        }
    } }
}

@Composable
private fun EggsTab(vm: BreedingFlowViewModel) {
    var show by rememberSaveable { mutableStateOf(false) }
    var pairId by rememberSaveable { mutableStateOf("") }
    var count by rememberSaveable { mutableStateOf("0") }
    var grade by rememberSaveable { mutableStateOf("A") }
    var weight by rememberSaveable { mutableStateOf("") }
    val pairs by vm.pairs.collectAsState()
    val eggsByPair by vm.eggCollectionsByPair.collectAsState()
    ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Egg Collections")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pairs) { p ->
                val cols = eggsByPair[p.pairId].orEmpty()
                val total = cols.sumOf { it.eggsCollected }
                ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Pair ${'$'}{p.pairId.take(6)} — Total: ${'$'}total")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { pairId = p.pairId; show = true }, enabled = !vm.submitting.collectAsState().value) { Text("Collect Eggs") }
                        if (cols.isNotEmpty()) {
                            OutlinedButton(onClick = { /* CTA to start incubation from latest collection */
                                val latest = cols.maxByOrNull { it.collectedAt }
                                latest?.let { /* show a lighter dialog to confirm start on Incubation tab */ }
                                show = true
                            }) { Text("Start Incubation…") }
                        }
                    }
                } }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { show = true }, enabled = !vm.submitting.collectAsState().value) { Text("Collect Eggs") }
        }
        if (show) {
            AlertDialog(
                onDismissRequest = { show = false },
                title = { Text("Collect Eggs") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = pairId, onValueChange = { pairId = it }, label = { Text("Pair ID") })
                        OutlinedTextField(value = count, onValueChange = { count = it.filter { ch -> ch.isDigit() } }, label = { Text("Count") })
                        OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Grade") })
                        OutlinedTextField(value = weight, onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Weight (g)") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val cnt = count.toIntOrNull() ?: 0
                        if (pairId.isBlank() || cnt <= 0) { vm.postMessage("Provide pair and count > 0"); return@Button }
                        vm.collectEggs(pairId.trim(), cnt, grade.ifBlank { "A" }, weight.toDoubleOrNull())
                        show = false
                    }, enabled = !vm.submitting.collectAsState().value) { Text("Save") }
                },
                dismissButton = { OutlinedButton(onClick = { show = false }) { Text("Cancel") } }
            )
        }
    } }
}

@Composable
private fun IncubationTab(vm: BreedingFlowViewModel, onOpenBatchDetail: (String) -> Unit) {
    var show by rememberSaveable { mutableStateOf(false) }
    var collectionId by rememberSaveable { mutableStateOf("") }
    var expected by rememberSaveable { mutableStateOf("") }
    var temp by rememberSaveable { mutableStateOf("") }
    var hum by rememberSaveable { mutableStateOf("") }
    val batches by vm.incubationBatches.collectAsState()
    ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Active Batches")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(batches) { b ->
                val etaText = remember(b.expectedHatchAt) {
                    val now = System.currentTimeMillis()
                    val eta = (b.expectedHatchAt ?: 0L) - now
                    val hours = (eta / 3_600_000L).coerceAtLeast(0)
                    val mins = ((eta % 3_600_000L) / 60_000L).coerceAtLeast(0)
                    "ETA ${'$'}hours h ${'$'}mins m"
                }
                ElevatedCard(onClick = { onOpenBatchDetail(b.batchId) }) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(b.name ?: b.batchId.take(8))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Eggs set: ${'$'}{b.eggsCount ?: 0}")
                            Text(etaText)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { /* TODO: update conditions dialog */ }) { Text("Update Conditions") }
                            OutlinedButton(onClick = { /* TODO: candle eggs action */ }) { Text("Candle Eggs") }
                            OutlinedButton(onClick = { onOpenBatchDetail(b.batchId) }) { Text("View Logs") }
                        }
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { show = true }) { Text("Start Batch") }
        }
        if (show) {
            AlertDialog(
                onDismissRequest = { show = false },
                title = { Text("Start Incubation") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = collectionId, onValueChange = { collectionId = it }, label = { Text("Egg Collection ID") })
                        OutlinedTextField(value = expected, onValueChange = { expected = it.filter { ch -> ch.isDigit() } }, label = { Text("Expected Hatch At (epoch ms)") })
                        OutlinedTextField(value = temp, onValueChange = { temp = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Temperature C") })
                        OutlinedTextField(value = hum, onValueChange = { hum = it.filter { ch -> ch.isDigit() || ch == '.' } }, label = { Text("Humidity %") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val exp = expected.toLongOrNull() ?: 0L
                        if (collectionId.isBlank() || exp <= 0L) { vm.postMessage("Provide collectionId and expected time"); return@Button }
                        vm.startIncubation(collectionId.trim(), exp, temp.toDoubleOrNull(), hum.toDoubleOrNull())
                        show = false
                    }, enabled = !vm.submitting.collectAsState().value) { Text("Start") }
                },
                dismissButton = { OutlinedButton(onClick = { show = false }) { Text("Cancel") } }
            )
        }
    } }
}

@Composable
private fun HatchingTab(vm: BreedingFlowViewModel) {
    var show by rememberSaveable { mutableStateOf(false) }
    var batchId by rememberSaveable { mutableStateOf("") }
    var productId by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("HATCHED") }
    var notes by rememberSaveable { mutableStateOf("") }
    val batches by vm.incubationBatches.collectAsState()
    val logsByBatch by vm.hatchingLogsByBatch.collectAsState()
    ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Hatching Logs")
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(batches) { b ->
                val logs = logsByBatch[b.batchId].orEmpty()
                val hatched = logs.count { it.eventType.equals("HATCHED", true) }
                val eggsSet = b.eggsCount ?: hatched
                ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${'$'}{b.name ?: b.batchId.take(8)} — ${'$'}hatched/${'$'}eggsSet hatched")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { batchId = b.batchId; type = "HATCHED"; show = true }, enabled = !vm.submitting.collectAsState().value) { Text("Log HATCHED") }
                    }
                } }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { show = true }) { Text("Log Hatch") }
        }
        if (show) {
            AlertDialog(
                onDismissRequest = { show = false },
                title = { Text("Log Hatch Event") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = batchId, onValueChange = { batchId = it }, label = { Text("Batch ID") })
                        OutlinedTextField(value = productId, onValueChange = { productId = it }, label = { Text("Chick Product ID (optional)") })
                        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Event Type") })
                        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (batchId.isBlank()) { vm.postMessage("Provide batchId"); return@Button }
                        vm.logHatch(batchId.trim(), productId.ifBlank { null }, type.ifBlank { "HATCHED" }, notes.ifBlank { null })
                        show = false
                    }, enabled = !vm.submitting.collectAsState().value) { Text("Save") }
                },
                dismissButton = { OutlinedButton(onClick = { show = false }) { Text("Cancel") } }
            )
        }
    } }
}

@HiltViewModel
class BreedingFlowViewModel @Inject constructor(
    private val repo: EnthusiastBreedingRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val breedingPairDao: BreedingPairDao,
    private val matingLogDao: MatingLogDao,
    private val eggCollectionDao: EggCollectionDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val hatchingLogDao: HatchingLogDao,
) : ViewModel() {
    private val uid = currentUserProvider.userIdOrNull()

    private val emptyPairs = MutableStateFlow<List<BreedingPairEntity>>(emptyList())
    val pairs: StateFlow<List<BreedingPairEntity>> = if (uid != null) {
        breedingPairDao.observeActive(uid).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    } else {
        emptyPairs
    }

    // Recent matings grouped by pair
    val matingsByPair: StateFlow<Map<String, List<MatingLogEntity>>> = pairs
        .flatMapLatest { list ->
            if (list.isEmpty()) MutableStateFlow(emptyMap()) else combine(
                list.map { p ->
                    matingLogDao.observeByPair(p.pairId).map { logs -> p.pairId to logs }
                }
            ) { arr -> arr.toMap() }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    // Eggs by pair
    val eggCollectionsByPair: StateFlow<Map<String, List<EggCollectionEntity>>> = pairs
        .flatMapLatest { list ->
            if (list.isEmpty()) MutableStateFlow(emptyMap()) else combine(
                list.map { p ->
                    eggCollectionDao.observeByPair(p.pairId).map { cols -> p.pairId to cols }
                }
            ) { arr -> arr.toMap() }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    val incubationBatches: StateFlow<List<HatchingBatchEntity>> = if (uid != null) {
        repo.observeIncubationTimers(uid).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    } else MutableStateFlow(emptyList())

    val hatchingLogsByBatch: StateFlow<Map<String, List<HatchingLogEntity>>> = incubationBatches
        .flatMapLatest { list ->
            if (list.isEmpty()) MutableStateFlow(emptyMap()) else combine(
                list.map { b ->
                    hatchingLogDao.observeForBatch(b.batchId).map { logs -> b.batchId to logs }
                }
            ) { arr -> arr.toMap() }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    // Derived chart: matings per week (last 8 entries grouped by iso week label)
    val matingsPerWeekChart: StateFlow<List<BarDatum>> = matingsByPair
        .map { byPair ->
            val all = byPair.values.flatten()
            if (all.isEmpty()) emptyList() else {
                val cal = java.util.Calendar.getInstance()
                val grouped = all.groupBy {
                    cal.timeInMillis = it.matedAt
                    val week = cal.get(java.util.Calendar.WEEK_OF_YEAR)
                    val year = cal.get(java.util.Calendar.YEAR)
                    "W${'$'}week/${'$'}year"
                }
                grouped.entries.sortedBy { it.key }.map { (k, v) -> BarDatum(k, v.size.toFloat()) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Actions (lightweight wrappers)
    fun createPair(maleProductId: String, femaleProductId: String, notes: String?) {
        val farmerId = uid ?: return
        if (maleProductId.isBlank() || femaleProductId.isBlank()) { postMessage("Both male and female are required"); return }
        if (maleProductId == femaleProductId) { postMessage("Male and female cannot be the same"); return }
        viewModelScope.launch {
            _submitting.value = true
            when (val res = repo.createPair(farmerId, maleProductId, femaleProductId, notes)) {
                is Resource.Success -> postMessage("Pair created")
                is Resource.Error -> postMessage(res.message ?: "Failed to create pair")
                is Resource.Loading -> {}
            }
            _submitting.value = false
        }
    }

    fun logMating(pairId: String, observed: String?, conditions: String?) {
        viewModelScope.launch {
            _submitting.value = true
            when (val res = repo.logMating(pairId, observed, conditions)) {
                is Resource.Success -> postMessage("Mating logged")
                is Resource.Error -> postMessage(res.message ?: "Failed to log mating")
                is Resource.Loading -> {}
            }
            _submitting.value = false
        }
    }

    fun collectEggs(pairId: String, count: Int, grade: String, weight: Double?) {
        if (count <= 0) { postMessage("Count must be > 0"); return }
        viewModelScope.launch {
            _submitting.value = true
            when (val res = repo.collectEggs(pairId, count, grade, weight)) {
                is Resource.Success -> postMessage("Eggs collected")
                is Resource.Error -> postMessage(res.message ?: "Failed to collect eggs")
                is Resource.Loading -> {}
            }
            _submitting.value = false
        }
    }

    fun startIncubation(collectionId: String, expectedAt: Long, temp: Double?, humidity: Double?) {
        if (collectionId.isBlank() || expectedAt <= 0L) { postMessage("Provide collection and expected hatch time"); return }
        viewModelScope.launch {
            _submitting.value = true
            when (val res = repo.startIncubation(collectionId, expectedAt, temp, humidity)) {
                is Resource.Success -> postMessage("Incubation started")
                is Resource.Error -> postMessage(res.message ?: "Failed to start incubation")
                is Resource.Loading -> {}
            }
            _submitting.value = false
        }
    }

    fun logHatch(batchId: String, productId: String?, eventType: String, notes: String?) {
        viewModelScope.launch {
            _submitting.value = true
            when (val res = repo.logHatch(batchId, productId, eventType, notes)) {
                is Resource.Success -> postMessage("Hatch event logged")
                is Resource.Error -> postMessage(res.message ?: "Failed to log hatch")
                is Resource.Loading -> {}
            }
            _submitting.value = false
        }
    }

    // UI helpers
    fun openMatingDialogFor(id: String) { /* handled in UI by setting state */ }
    fun openEggDialogFor(id: String) { /* handled in UI */ }
    fun navigateToIncubationFromPair(id: String) { /* handled by parent navigation; no-op here */ }
    fun postMessage(msg: String) { _message.value = msg }
}
