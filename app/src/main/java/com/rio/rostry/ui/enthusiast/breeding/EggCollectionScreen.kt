package com.rio.rostry.ui.enthusiast.breeding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggCollectionScreen(
    onBack: () -> Unit,
    initialPairId: String? = null,
    vm: EggCollectionViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(initialPairId) {
        initialPairId?.let { vm.init(it) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Log Egg Collection") }) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.pairId,
                onValueChange = { vm.init(it) },
                label = { Text("Pair ID") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.eggsCollected,
                onValueChange = vm::setEggs,
                label = { Text("Eggs Collected") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.qualityGrade,
                onValueChange = vm::setGrade,
                label = { Text("Quality Grade (A/B/C)") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.weight,
                onValueChange = vm::setWeight,
                label = { Text("Avg Weight (g) - optional") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = vm::setNotes,
                label = { Text("Notes - optional") }
            )
            state.error?.let { Text(it) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.save() }, enabled = !state.saving) { Text("Save") }
            if (state.saved) {
                Text("Saved ✓")
            }
        }
    }
}
