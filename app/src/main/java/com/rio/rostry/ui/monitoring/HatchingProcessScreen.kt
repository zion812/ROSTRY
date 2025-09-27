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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun HatchingProcessScreen() {
    val batchName = remember { mutableStateOf("") }
    val sp = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Hatching Process", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = batchName.value, onValueChange = { batchName.value = it }, label = { Text("Batch name") })
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Create and monitor incubation batches.")
                Button(onClick = { /* TODO: create batch */ }) { Text("Start Batch") }
            }
        }
    }
}
