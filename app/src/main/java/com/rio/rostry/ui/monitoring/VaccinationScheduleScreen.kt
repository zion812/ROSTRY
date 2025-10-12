package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
    onListProduct: (String) -> Unit = {}
) {
    val vm: VaccinationViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    val productIdInput = remember { mutableStateOf("") }
    val vaccineTypeInput = remember { mutableStateOf("") }
    val showSchedulePicker = remember { mutableStateOf(false) }
    val showAdminPickerFor = remember { mutableStateOf<String?>(null) } // vaccinationId
    val selectedTab = rememberSaveable { mutableStateOf(0) } // 0=Due, 1=Overdue
    val showCompleteDialogFor = remember { mutableStateOf<com.rio.rostry.data.database.entity.TaskEntity?>(null) }
    val completeVaccineType = remember { mutableStateOf("") }
    val completeBatchCode = remember { mutableStateOf("") }
    val showCompleteDatePicker = remember { mutableStateOf(false) }
    val completeWhen = remember { mutableStateOf<Long?>(null) }
    val dateFmt = remember { SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Vaccination Schedule", style = MaterialTheme.typography.titleLarge)

        // Tabs: Due / Overdue counts
        val dueCount = state.dueTasks.size
        val overdueCount = state.overdueTasks.size
        TabRow(selectedTabIndex = selectedTab.value) {
            Tab(selected = selectedTab.value == 0, onClick = { selectedTab.value = 0 }, text = { Text("Due ($dueCount)") })
            Tab(selected = selectedTab.value == 1, onClick = { selectedTab.value = 1 }, text = { Text("Overdue ($overdueCount)") })
        }

        // Tab content lists
        val tasks = if (selectedTab.value == 0) state.dueTasks else state.overdueTasks
        if (tasks.isNotEmpty()) {
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (selectedTab.value == 0) "Due Today" else "Overdue", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks, key = { it.taskId }) { t ->
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(t.title)
                                    Text("Product: ${t.productId ?: "-"}")
                                    Text("Due: ${dateFmt.format(Date(t.dueAt))}")
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(onClick = { showCompleteDialogFor.value = t }) { Text("Complete") }
                                        OutlinedButton(onClick = { vm.snoozeTask(t.taskId, 24) }) { Text("Snooze 24h") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Product selector
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = productIdInput.value,
                onValueChange = { productIdInput.value = it },
                label = { Text("Product ID") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { if (productIdInput.value.isNotBlank()) vm.observe(productIdInput.value) }) {
                Text("Load")
            }
        }

        if (state.productId.isNotBlank()) {
            ElevatedCard {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Product: ${state.productId}")

                    // Suggestions
                    state.nextDueAt?.let { due ->
                        Text("Next due suggestion: ${dateFmt.format(Date(due))}")
                    }

                    // Actions
                    Text("Schedule Vaccination", style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    if (showSchedulePicker.value) {
                        val initial = state.nextDueAt ?: System.currentTimeMillis()
                        val dpState = rememberDatePickerState(initialSelectedDateMillis = initial)
                        DatePickerDialog(
                            onDismissRequest = { showSchedulePicker.value = false },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val day = dpState.selectedDateMillis ?: initial
                                        val noon = day + 12L*60*60*1000 // default to noon
                                        vm.schedule(state.productId, vaccineTypeInput.value.trim(), noon)
                                        showSchedulePicker.value = false
                                    },
                                    enabled = vaccineTypeInput.value.isNotBlank() && dpState.selectedDateMillis != null
                                ) { Text("Schedule") }
                            },
                            dismissButton = { OutlinedButton(onClick = { showSchedulePicker.value = false }) { Text("Cancel") } }
                        ) {
                            DatePicker(state = dpState)
                        }
                    }

                    Divider()
                    Text("Records", style = MaterialTheme.typography.titleMedium)

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.records, key = { it.vaccinationId }) { rec ->
                            val overdue = rec.administeredAt == null && rec.scheduledAt < System.currentTimeMillis()
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${rec.vaccineType}")
                                        if (overdue) {
                                            BadgedBox(badge = { Badge { Text("OVERDUE") } }) { }
                                        }
                                    }
                                    Text("Scheduled: ${dateFmt.format(Date(rec.scheduledAt))}")
                                    Text("Administered: ${rec.administeredAt?.let { dateFmt.format(Date(it)) } ?: "-"}")
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { showAdminPickerFor.value = rec.vaccinationId }) {
                                            Text("Pick Administered Date")
                                        }
                                        Button(onClick = { showAdminPickerFor.value = rec.vaccinationId }) {
                                            Text("Mark Administered")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Administered date picker dialog (per-record)
                    val adminFor = showAdminPickerFor.value
                    if (adminFor != null) {
                        val initial = System.currentTimeMillis()
                        val dpState = rememberDatePickerState(initialSelectedDateMillis = initial)
                        DatePickerDialog(
                            onDismissRequest = { showAdminPickerFor.value = null },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val whenMs = dpState.selectedDateMillis ?: initial
                                        vm.markAdministered(adminFor, whenMs + 12L*60*60*1000) // default noon
                                        showAdminPickerFor.value = null
                                    },
                                    enabled = dpState.selectedDateMillis != null
                                ) { Text("Confirm") }
                            },
                            dismissButton = { OutlinedButton(onClick = { showAdminPickerFor.value = null }) { Text("Cancel") } }
                        ) {
                            DatePicker(state = dpState)
                        }
                    }

                    // Complete Vaccination Task dialog
                    val taskToComplete = showCompleteDialogFor.value
                    if (taskToComplete != null) {
                        ElevatedCard {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Complete Vaccination", style = MaterialTheme.typography.titleMedium)
                                OutlinedTextField(value = completeVaccineType.value, onValueChange = { completeVaccineType.value = it }, label = { Text("Vaccine Type") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(value = completeBatchCode.value, onValueChange = { completeBatchCode.value = it }, label = { Text("Batch Code (optional)") }, modifier = Modifier.fillMaxWidth())
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { showCompleteDatePicker.value = true }) { Text(completeWhen.value?.let { "When: ${dateFmt.format(Date(it))}" } ?: "Pick Administered Time") }
                                    Button(
                                        onClick = {
                                            val whenMs = completeWhen.value ?: System.currentTimeMillis()
                                            vm.completeVaccinationTask(taskToComplete, completeVaccineType.value.trim(), completeBatchCode.value.ifBlank { null }, whenMs)
                                            showCompleteDialogFor.value = null
                                            completeVaccineType.value = ""
                                            completeBatchCode.value = ""
                                            completeWhen.value = null
                                        },
                                        enabled = completeVaccineType.value.isNotBlank()
                                    ) { Text("Save & Complete") }
                                    OutlinedButton(onClick = { showCompleteDialogFor.value = null }) { Text("Cancel") }
                                }
                            }
                        }
                        if (showCompleteDatePicker.value) {
                            val initial = System.currentTimeMillis()
                            val dpState = rememberDatePickerState(initialSelectedDateMillis = initial)
                            DatePickerDialog(
                                onDismissRequest = { showCompleteDatePicker.value = false },
                                confirmButton = {
                                    Button(onClick = {
                                        val whenMs = (dpState.selectedDateMillis ?: initial) + 12L*60*60*1000
                                        completeWhen.value = whenMs
                                        showCompleteDatePicker.value = false
                                    }, enabled = dpState.selectedDateMillis != null) { Text("Set") }
                                },
                                dismissButton = { OutlinedButton(onClick = { showCompleteDatePicker.value = false }) { Text("Cancel") } }
                            ) {
                                DatePicker(state = dpState)
                            }
                        }
                    }

                    // Listing CTA
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onListProduct(state.productId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                            Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("List with Vaccination Proof")
                        }
                    }
                }
            }
        }
    }
}
