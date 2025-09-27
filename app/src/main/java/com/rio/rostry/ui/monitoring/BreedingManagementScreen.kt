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
fun BreedingManagementScreen() {
    val pair = remember { mutableStateOf("") }
    val sp = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Breeding Management", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = pair.value, onValueChange = { pair.value = it }, label = { Text("Breeding Pair ID") })
        ElevatedCard { Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
            Text("Manage breeding pairs and incubation batches here.")
            OutlinedButton(onClick = { /* filter */ }) { Text("Filters") }
            Button(onClick = { /* record */ }) { Text("Record Breeding Event") }
        } }
    }
}
