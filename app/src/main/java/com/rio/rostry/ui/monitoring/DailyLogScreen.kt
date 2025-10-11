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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

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

                // Quick chips row
                val weightFocus = remember { FocusRequester() }
                val feedFocus = remember { FocusRequester() }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chips = listOf("Weight", "Feed", "Medication", "Symptoms", "Activity")
                    chips.forEach { label ->
                        FilterChip(selected = false, onClick = {
                            when (label) {
                                "Weight" -> weightFocus.requestFocus()
                                "Feed" -> feedFocus.requestFocus()
                                "Medication" -> { /* scroll to medication section */ }
                                "Symptoms" -> { /* scroll to symptoms section */ }
                                "Activity" -> { /* focus activity chips */ }
                            }
                        }, label = { Text(label) })
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightInput.value,
                        onValueChange = { weightInput.value = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Weight (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).focusRequester(weightFocus)
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
                        modifier = Modifier.weight(1f).focusRequester(feedFocus)
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

                // Inline weight trend chart (last 30 days)
                val chartLogs by vm.chartData.collectAsState()
                if (chartLogs.isNotEmpty()) {
                    Text("Weight trend (30 days)", fontWeight = FontWeight.SemiBold)
                    val maxW = chartLogs.maxOfOrNull { it.weightGrams ?: 0.0 } ?: 0.0
                    val minW = chartLogs.filter { it.weightGrams != null }.minOfOrNull { it.weightGrams!! } ?: 0.0
                    val points = chartLogs.mapIndexedNotNull { idx, l ->
                        val w = l.weightGrams ?: return@mapIndexedNotNull null
                        idx to w
                    }
                    if (points.size >= 2 && maxW > 0) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                            val range = (maxW - minW).takeIf { it > 0 } ?: 1.0
                            var prev: Offset? = null
                            points.forEachIndexed { i, (idx, w) ->
                                val x = idx * stepX
                                val yRatio = ((w - minW) / range).toFloat()
                                val y = size.height * (1f - yRatio)
                                val cur = Offset(x, y)
                                prev?.let { p ->
                                    drawLine(Color(0xFF1976D2), p, cur, strokeWidth = 4f)
                                }
                                prev = cur
                            }
                        }
                    }
                }
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
                    OutlinedButton(onClick = { vm.requestPhotoPick() }) { Text("Add Photo") }
                    val alreadyLogged = state.hasToday
                    if (alreadyLogged) {
                        // Show timestamp/author of the existing log
                        val tLog = todayLog
                        val ts = tLog?.deviceTimestamp
                        val formatted = ts?.let { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(it)) } ?: "-"
                        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Logged Today • $formatted • by ${tLog?.author ?: "unknown"}")
                            if ((tLog?.dirty == true)) {
                                Text("Offline", color = Color.Red)
                            } else {
                                Text("Synced ✓", color = Color(0xFF2E7D32))
                            }
                        }
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
