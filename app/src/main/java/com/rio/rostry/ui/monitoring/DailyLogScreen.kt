package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.monitoring.vm.DailyLogViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLogScreen(
    onNavigateBack: () -> Unit,
    productId: String?
) {
    val vm: DailyLogViewModel = hiltViewModel()
    LaunchedEffect(productId) { productId?.let { vm.loadForProduct(it) } }
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Log") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (productId == null) {
                Text("Select a bird/batch to log")
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.products) { p ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(p.name)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { vm.loadForProduct(p.productId) }) { Text("Log") }
                        }
                    }
                }
            } else {
                Text("Logging for product: $productId")
                // Header: device time and author for current or today's existing log
                val current = state.currentLog
                val todayLog = state.recentLogs.firstOrNull { it.logDate == (current?.logDate ?: it.logDate) }
                val deviceTs = todayLog?.deviceTimestamp ?: current?.deviceTimestamp
                val author = todayLog?.author ?: current?.author
                deviceTs?.let {
                    val formatted = remember(it) { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(it)) }
                    Text("Device time: $formatted • Author: ${author ?: "unknown"}")
                }
                val weightInput = remember { mutableStateOf("") }
                val feedInput = remember { mutableStateOf("") }
                val notesInput = remember { mutableStateOf("") }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightInput.value,
                        onValueChange = { weightInput.value = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Weight (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(onClick = {
                        weightInput.value.toDoubleOrNull()?.let { vm.updateWeight(it) }
                    }) { Text("Set") }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = feedInput.value,
                        onValueChange = { feedInput.value = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Feed (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(onClick = {
                        feedInput.value.toDoubleOrNull()?.let { vm.updateFeed(it) }
                    }) { Text("Set") }
                }
                OutlinedTextField(
                    value = notesInput.value,
                    onValueChange = {
                        notesInput.value = it
                        vm.setNotes(it)
                    },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val activity = listOf("LOW", "NORMAL", "HIGH")
                    activity.forEach { level ->
                        FilterChip(selected = state.currentLog?.activityLevel == level, onClick = { vm.setActivityLevel(level) }, label = { Text(level) })
                    }
                }
                // Quick-pick medication chips
                Text("Medication")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val meds = listOf("Vitamins", "Antibiotic", "Dewormer")
                    meds.forEach { m ->
                        val selected = (state.currentLog?.medicationJson ?: "").contains(m)
                        AssistChip(onClick = { vm.toggleMedication(m) }, label = { Text(m) }, leadingIcon = null)
                    }
                }
                // Quick-pick symptoms chips
                Text("Symptoms")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sy = listOf("Lethargy", "Cough", "Diarrhea")
                    sy.forEach { s ->
                        AssistChip(onClick = { vm.toggleSymptom(s) }, label = { Text(s) }, leadingIcon = null)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        // Stub photo picker: replace with real picker if available
                        vm.addPhoto("stub://photo")
                    }) { Text("Add Photo") }
                    val alreadyLogged = state.hasToday
                    if (alreadyLogged) {
                        // Show timestamp/author of the existing log
                        val tLog = todayLog
                        val ts = tLog?.deviceTimestamp
                        val formatted = ts?.let { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(it)) } ?: "-"
                        Text("Logged Today • $formatted • by ${tLog?.author ?: "unknown"}", modifier = Modifier.weight(1f))
                    } else {
                        Button(onClick = { vm.save() }, enabled = true, modifier = Modifier.weight(1f)) {
                            Text("Log Today")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Recent logs")
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(state.recentLogs) { l ->
                        Text("• ${java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(l.logDate))}  W=${l.weightGrams ?: "-"}g  F=${l.feedKg ?: "-"}kg  ${l.activityLevel ?: ""}  by ${l.author ?: "unknown"}")
                    }
                }
            }
        }
    }
}
