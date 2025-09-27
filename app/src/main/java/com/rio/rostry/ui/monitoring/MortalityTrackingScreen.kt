package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.theme.LocalSpacing
import com.rio.rostry.ui.components.LineChartView
import com.rio.rostry.ui.components.PieChartView

@Composable
fun MortalityTrackingScreen(vm: MortalityViewModel = hiltViewModel()) {
    val sp = LocalSpacing.current
    val state by vm.ui.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Mortality Tracking", style = MaterialTheme.typography.titleLarge)

        // Range filters
        Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
            OutlinedButton(onClick = { vm.setDays(7) }) { Text("7d") }
            OutlinedButton(onClick = { vm.setDays(30) }) { Text("30d") }
            OutlinedButton(onClick = { vm.setDays(90) }) { Text("90d") }
        }

        if (state.isLoading) {
            Row { CircularProgressIndicator() }
        }
        state.error?.let { err ->
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }

        // Stats
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Last ${state.days} days", style = MaterialTheme.typography.titleMedium)
                Text("Total deaths: ${state.stats.total}")
                Text("Total cost: ${"%.2f".format(state.stats.totalCostInr)}")
                Text("Avg age (weeks): ${state.stats.avgAgeWeeks?.let { "%.1f".format(it) } ?: "-"}")
            }
        }

        // Trend (textual)
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Trend by day")
                LineChartView(data = state.trend)
                state.trend.forEach { dc ->
                    Text("${dc.day}: ${dc.count}")
                }
            }
        }

        // Distribution (textual with percentages)
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("By cause")
                PieChartView(data = state.distribution)
                val total = state.distribution.sumOf { it.count }
                state.distribution.forEach { cc ->
                    val pct = if (total > 0) (cc.count * 100.0 / total) else 0.0
                    Text("${cc.category}: ${cc.count} (${"%.1f".format(pct)}%)")
                }
            }
        }

        // Recent
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Recent entries")
                state.recent.take(5).forEach { r ->
                    Text("${r.causeCategory} • age=${r.ageWeeks ?: "-"} • cost=${r.financialImpactInr ?: 0.0}")
                }
            }
        }

        // Record form
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Record mortality")
                val causeOptions = listOf("ILLNESS", "PREDATOR", "ACCIDENT", "OTHER")
                val expanded = remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        readOnly = true,
                        value = state.newRecord.causeCategory,
                        onValueChange = {},
                        label = { Text("Cause Category") },
                        trailingIcon = { Text("\u25BE") },
                        modifier = Modifier
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        causeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    vm.updateNewRecord { it.copy(causeCategory = option) }
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }
                // Toggle dropdown when clicking the field or a button
                Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                    OutlinedButton(onClick = { expanded.value = true }) { Text("Choose Cause") }
                }
                OutlinedTextField(
                    value = state.newRecord.circumstances,
                    onValueChange = { s -> vm.updateNewRecord { it.copy(circumstances = s) } },
                    label = { Text("Circumstances") }
                )
                OutlinedTextField(
                    value = state.newRecord.ageWeeks,
                    onValueChange = { s -> vm.updateNewRecord { it.copy(ageWeeks = s) } },
                    label = { Text("Age (weeks)") }
                )
                OutlinedTextField(
                    value = state.newRecord.disposalMethod,
                    onValueChange = { s -> vm.updateNewRecord { it.copy(disposalMethod = s) } },
                    label = { Text("Disposal Method") }
                )
                OutlinedTextField(
                    value = state.newRecord.financialImpactInr,
                    onValueChange = { s -> vm.updateNewRecord { it.copy(financialImpactInr = s) } },
                    label = { Text("Financial Impact (INR)") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                    Button(onClick = { vm.recordMortality() }) { Text("Record Mortality") }
                    OutlinedButton(onClick = { vm.refresh() }) { Text("Refresh") }
                }
            }
        }

        Spacer(Modifier.height(sp.sm))
    }
}
