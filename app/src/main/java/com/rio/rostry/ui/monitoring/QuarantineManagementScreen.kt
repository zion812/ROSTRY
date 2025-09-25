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
fun QuarantineManagementScreen() {
    val reason = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Quarantine Management")
        OutlinedTextField(value = reason.value, onValueChange = { reason.value = it }, label = { Text("Reason / Protocol") })
        ElevatedCard { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Start and manage quarantine protocols here.")
            OutlinedButton(onClick = { /* filter */ }) { Text("Filters") }
            Button(onClick = { /* start */ }) { Text("Start Quarantine") }
        } }
    }
}
