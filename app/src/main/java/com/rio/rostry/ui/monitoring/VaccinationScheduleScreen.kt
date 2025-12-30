package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.monitoring.vm.VaccinationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScheduleScreen(
    onListProduct: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val vm: VaccinationViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    val productIdInput = remember { mutableStateOf("") }
    val vaccineTypeInput = remember { mutableStateOf("") }
    val showSchedulePicker = remember { mutableStateOf(false) }
    val showAdminPickerFor = remember { mutableStateOf<String?>(null) } // vaccinationId
    val selectedTab = rememberSaveable { mutableStateOf(0) } // 0=Due, 1=Overdue
    val showCompleteDialogFor =
        remember { mutableStateOf<com.rio.rostry.data.database.entity.TaskEntity?>(null) }
    val completeVaccineType = remember { mutableStateOf("") }
    val completeBatchCode = remember { mutableStateOf("") }
    val showCompleteDatePicker = remember { mutableStateOf(false) }
    val completeWhen = remember { mutableStateOf<Long?>(null) }
    val applyToBatch = remember { mutableStateOf(false) }
    val showBirdSelectionSheet = remember { mutableStateOf(false) }
    val dateFmt = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }


    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Vaccination") },
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
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Tabs: Due / Overdue counts
            val dueCount = state.dueTasks.size
            val overdueCount = state.overdueTasks.size
            TabRow(selectedTabIndex = selectedTab.value) {
                Tab(
                    selected = selectedTab.value == 0,
                    onClick = { selectedTab.value = 0 },
                    text = { Text("Due ($dueCount)") })
                Tab(
                    selected = selectedTab.value == 1,
                    onClick = { selectedTab.value = 1 },
                    text = { Text("Overdue ($overdueCount)") })
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text("Vaccination Schedule", style = MaterialTheme.typography.titleLarge)
                }

                // Tab content lists
                val tasks = if (selectedTab.value == 0) state.dueTasks else state.overdueTasks
                if (tasks.isNotEmpty()) {
                    item {
                        ElevatedCard {
                            Column(
                                Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    if (selectedTab.value == 0) "Due Today" else "Overdue",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                    items(tasks, key = { it.taskId }) { t ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(t.title)
                                Text("Product: ${t.productId ?: "-"}")
                                Text("Due: ${dateFmt.format(Date(t.dueAt))}")
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(onClick = {
                                        showCompleteDialogFor.value = t
                                    }) { Text("Complete") }
                                    OutlinedButton(onClick = {
                                        vm.snoozeTask(
                                            t.taskId,
                                            24
                                        )
                                    }) { Text("Snooze 24h") }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        com.rio.rostry.ui.components.EmptyState(
                            title = if (selectedTab.value == 0) "No due vaccinations" else "No overdue vaccinations",
                            subtitle = "You're all caught up",
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                        )
                    }
                }

                item {
                    Divider()
                }

                // Product selector
                item {
                    OutlinedButton(
                        onClick = { showBirdSelectionSheet.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.productId.isNotBlank()) {
                            val p = state.products.find { it.productId == state.productId }
                            Text(text = "Selected: ${p?.name ?: state.productId}")
                        } else {
                            Text("Select Bird / Batch")
                        }
                    }
                }

                if (state.productId.isNotBlank()) {
                    item {
                        ElevatedCard {
                            Column(
                                Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Product: ${state.productId}")

                                // Suggestions
                                state.nextDueAt?.let { due ->
                                    Text("Next due suggestion: ${dateFmt.format(Date(due))}")
                                }

                                // Actions
                                Text("Schedule Vaccination", style = MaterialTheme.typography.titleMedium)
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = vaccineTypeInput.value,
                                        onValueChange = { vaccineTypeInput.value = it },
                                        label = { Text("Vaccine Type") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = { showSchedulePicker.value = true },
                                        enabled = vaccineTypeInput.value.isNotBlank()
                                    ) { Text("Pick Schedule Date") }
                                }
                                
                                // Quick Schedule Button - saves immediately with suggested date
                                if (state.nextDueAt != null) {
                                    Button(
                                        onClick = {
                                            vm.schedule(
                                                state.productId,
                                                vaccineTypeInput.value.trim(),
                                                state.nextDueAt ?: System.currentTimeMillis(),
                                                applyToChildren = false
                                            )
                                            vaccineTypeInput.value = ""
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = vaccineTypeInput.value.isNotBlank()
                                    ) { 
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                        Text("Save Schedule (Use Suggested Date)") 
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("Records", style = MaterialTheme.typography.titleMedium)
                    }

                    items(state.records, key = { it.vaccinationId }) { rec ->
                        val overdue =
                            rec.administeredAt == null && rec.scheduledAt < System.currentTimeMillis()
                        Card(Modifier.fillMaxWidth()) {
                            Column(
                                Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${rec.vaccineType}")
                                    if (overdue) {
                                        BadgedBox(badge = { Badge { Text("OVERDUE") } }) { }
                                    }
                                }
                                Text("Scheduled: ${dateFmt.format(Date(rec.scheduledAt))}")
                                Text(
                                    "Administered: ${
                                        rec.administeredAt?.let {
                                            dateFmt.format(
                                                Date(it)
                                            )
                                        } ?: "-"
                                    }")
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(onClick = {
                                        showAdminPickerFor.value = rec.vaccinationId
                                    }) {
                                        Text("Pick Administered Date")
                                    }
                                    Button(onClick = {
                                        showAdminPickerFor.value = rec.vaccinationId
                                    }) {
                                        Text("Mark Administered")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Listing CTA
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onListProduct(state.productId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = "Vaccination complete",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Icon(
                                    Icons.Filled.Storefront,
                                    contentDescription = "List product",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("List with Vaccination Proof")
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs (kept outside LazyColumn to overlay correctly)
    if (showSchedulePicker.value) {
        val initial = state.nextDueAt ?: System.currentTimeMillis()
        val dpState =
            rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { showSchedulePicker.value = false },
            confirmButton = {
                Button(
                    onClick = {
                        val day = dpState.selectedDateMillis ?: initial
                        val noon = day + 12L * 60 * 60 * 1000 // default to noon
                        vm.schedule(
                            state.productId,
                            vaccineTypeInput.value.trim(),
                            noon,
                            applyToChildren = applyToBatch.value
                        )
                        showSchedulePicker.value = false
                        applyToBatch.value = false
                    },
                    enabled = vaccineTypeInput.value.isNotBlank() && dpState.selectedDateMillis != null
                ) { Text("Schedule") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showSchedulePicker.value = false
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dpState)
            if (state.isBatch) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Checkbox(checked = applyToBatch.value, onCheckedChange = { applyToBatch.value = it })
                    Text("Apply to all items in batch")
                }
            }
        }
    }

    // Administered date picker dialog (per-record)
    val adminFor = showAdminPickerFor.value
    if (adminFor != null) {
        val initial = System.currentTimeMillis()
        val dpState =
            rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { showAdminPickerFor.value = null },
            confirmButton = {
                Button(
                    onClick = {
                        val whenMs = dpState.selectedDateMillis ?: initial
                        vm.markAdministered(
                            adminFor,
                            whenMs + 12L * 60 * 60 * 1000,
                            applyToChildren = applyToBatch.value
                        ) // default noon
                        showAdminPickerFor.value = null
                        applyToBatch.value = false
                    },
                    enabled = dpState.selectedDateMillis != null
                ) { Text("Confirm") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAdminPickerFor.value = null
                }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dpState)
            if (state.isBatch) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Checkbox(checked = applyToBatch.value, onCheckedChange = { applyToBatch.value = it })
                    Text("Apply to all items in batch")
                }
            }
        }
    }

    // Complete Vaccination Task dialog
    val taskToComplete = showCompleteDialogFor.value
    if (taskToComplete != null) {
        // ... (Dialog implementation remains same, just moved outside)
        // Since the dialog implementation is long, I'll keep it here but I need to make sure I don't lose it.
        // Wait, I can't easily "keep" it if I'm replacing the whole block.
        // I need to include the dialog code in the replacement.
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCompleteDialogFor.value = null },
            title = { Text("Complete Vaccination") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = completeVaccineType.value,
                        onValueChange = { completeVaccineType.value = it },
                        label = { Text("Vaccine Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = completeBatchCode.value,
                        onValueChange = { completeBatchCode.value = it },
                        label = { Text("Batch Code (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(onClick = {
                        showCompleteDatePicker.value = true
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(completeWhen.value?.let {
                            "When: ${dateFmt.format(Date(it))}"
                        } ?: "Pick Administered Time")
                    }
                    if (state.isBatch) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(checked = applyToBatch.value, onCheckedChange = { applyToBatch.value = it })
                            Text("Apply to all items in batch")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val whenMs = completeWhen.value ?: System.currentTimeMillis()
                        vm.completeVaccinationTask(
                            taskToComplete,
                            completeVaccineType.value.trim(),
                            completeBatchCode.value.ifBlank { null },
                            whenMs,
                            applyToChildren = applyToBatch.value
                        )
                        showCompleteDialogFor.value = null
                        applyToBatch.value = false
                        completeVaccineType.value = ""
                        completeBatchCode.value = ""
                        completeWhen.value = null
                    },
                    enabled = completeVaccineType.value.isNotBlank()
                ) { Text("Save & Complete") }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialogFor.value = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCompleteDatePicker.value) {
        val initial = System.currentTimeMillis()
        val dpState = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { showCompleteDatePicker.value = false },
            confirmButton = {
                Button(
                    onClick = {
                        val whenMs = (dpState.selectedDateMillis ?: initial) + 12L * 60 * 60 * 1000
                        completeWhen.value = whenMs
                        showCompleteDatePicker.value = false
                    },
                    enabled = dpState.selectedDateMillis != null
                ) { Text("Set") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCompleteDatePicker.value = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dpState)
        }
    }

    if (showBirdSelectionSheet.value) {
        com.rio.rostry.ui.components.BirdSelectionSheet(
            products = state.products,
            onDismiss = { showBirdSelectionSheet.value = false },
            onSelect = { product ->
                vm.observe(product.productId)
                showBirdSelectionSheet.value = false
            }
        )
    }
}
