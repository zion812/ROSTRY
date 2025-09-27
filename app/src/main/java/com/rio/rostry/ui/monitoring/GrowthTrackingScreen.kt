package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.rio.rostry.ui.monitoring.vm.GrowthViewModel
import com.rio.rostry.ui.theme.LocalSpacing
import com.rio.rostry.ui.components.TwoLineChartView

@Composable
fun GrowthTrackingScreen(
    productId: String = ""
) {
    val vm: GrowthViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    val pid = remember { mutableStateOf(productId) }
    val sp = LocalSpacing.current

    // Observe initial productId if provided
    LaunchedEffect(productId) {
        if (productId.isNotBlank()) vm.observe(productId)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Growth Tracking", style = MaterialTheme.typography.titleLarge)
        // Expected vs Actual chart
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Expected vs Actual (g)")
                TwoLineChartView(points = state.points)
            }
        }
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                OutlinedTextField(value = pid.value, onValueChange = { pid.value = it; if (it.isNotBlank()) vm.observe(it) }, label = { Text("Product ID") })
                Text("Weekly records: ${state.records.size}")
                val weight = remember { mutableStateOf(0.0) }
                val height = remember { mutableStateOf(0.0) }
                OutlinedTextField(value = height.value.toString(), onValueChange = { runCatching { height.value = it.toDouble() } }, label = { Text("Height (cm)") })
                OutlinedTextField(value = weight.value.toString(), onValueChange = { runCatching { weight.value = it.toDouble() } }, label = { Text("Weight (g)") })
                Button(onClick = { if (pid.value.isNotBlank()) vm.recordToday(pid.value, weight.value, height.value) }) { Text("Record today") }
            }
        }
    }
}
