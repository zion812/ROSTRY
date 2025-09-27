package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.LocalSpacing

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
    val sp = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Farm Monitoring Dashboard", style = MaterialTheme.typography.titleLarge)
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
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
