package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MortalityTrackingScreen() {
    val cause = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mortality Tracking")
        OutlinedTextField(value = cause.value, onValueChange = { cause.value = it }, label = { Text("Cause (e.g., illness, predator)") })
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Log mortality events and review trends here.")
            OutlinedButton(onClick = { /* filter */ }) { Text("Filters") }
            Button(onClick = { /* record */ }) { Text("Record Mortality") }
        } }
    }
}
