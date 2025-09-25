package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.rio.rostry.ui.monitoring.vm.GrowthViewModel

@Composable
fun GrowthTrackingScreen(
    productId: String = ""
) {
    val vm: GrowthViewModel = hiltViewModel()
    val state by vm.ui.collectAsState()
    val pid = remember { mutableStateOf(productId) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Growth Tracking")
        ElevatedCard {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = pid.value, onValueChange = { pid.value = it; if (it.isNotBlank()) vm.observe(it) }, label = { Text("Product ID") })
                Text("Weekly records: ${state.records.size}")
                val weight = remember { mutableStateOf(0.0) }
                val height = remember { mutableStateOf(0.0) }
                OutlinedTextField(value = weight.value.toString(), onValueChange = { runCatching { weight.value = it.toDouble() } }, label = { Text("Weight (g)") })
                OutlinedTextField(value = height.value.toString(), onValueChange = { runCatching { height.value = it.toDouble() } }, label = { Text("Height (cm)") })
                Button(onClick = { if (pid.value.isNotBlank()) vm.recordToday(pid.value, weight.value, height.value) }) { Text("Record today") }
            }
        }
    }
}
