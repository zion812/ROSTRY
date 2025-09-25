package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FarmMonitoringScreen(
    onOpenGrowth: () -> Unit,
    onOpenVaccination: () -> Unit,
    onOpenBreeding: () -> Unit,
    onOpenQuarantine: () -> Unit,
    onOpenMortality: () -> Unit,
    onOpenHatching: () -> Unit,
    onOpenPerformance: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Farm Monitoring Dashboard")
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onOpenGrowth) { Text("Growth Monitoring") }
                Button(onClick = onOpenVaccination) { Text("Vaccination Schedule") }
                Button(onClick = onOpenBreeding) { Text("Breeding Management") }
                Button(onClick = onOpenQuarantine) { Text("Quarantine Management") }
                Button(onClick = onOpenMortality) { Text("Mortality Tracking") }
                Button(onClick = onOpenHatching) { Text("Hatching Process") }
                Button(onClick = onOpenPerformance) { Text("Farm Performance") }
            }
        }
    }
}
