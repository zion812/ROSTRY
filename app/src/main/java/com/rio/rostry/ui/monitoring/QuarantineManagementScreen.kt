package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun QuarantineManagementScreen() {
    val reason = remember { mutableStateOf("") }
    val sp = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Quarantine Management", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = reason.value, onValueChange = { reason.value = it }, label = { Text("Reason / Protocol") })
        ElevatedCard { Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
            Text("Start and manage quarantine protocols here.")
            OutlinedButton(onClick = { /* filter */ }) { Text("Filters") }
            Button(onClick = { /* start */ }) { Text("Start Quarantine") }
        } }
    }
}
