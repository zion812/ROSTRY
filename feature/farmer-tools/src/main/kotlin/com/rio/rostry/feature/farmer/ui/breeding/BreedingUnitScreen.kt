package com.rio.rostry.ui.farmer.breeding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.BreedingPairEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingUnitScreen(
    onBack: () -> Unit,
    viewModel: BreedingUnitViewModel = hiltViewModel()
) {
    val breedingPairs by viewModel.breedingPairs.collectAsState()
    val error by viewModel.error.collectAsState()
    var showEggDialogForPairId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breeding Units") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // Potentially add new pair logic or navigation here
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (error != null) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = error ?: "", color = MaterialTheme.colorScheme.onErrorContainer)
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (breedingPairs.isEmpty()) {
                    item {
                        Text(
                            text = "No active breeding pairs found.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                items(breedingPairs) { pair ->
                    BreedingUnitCard(
                        pair = pair,
                        onCollectEggs = { showEggDialogForPairId = pair.pairId }
                    )
                }
            }
        }
    }

    if (showEggDialogForPairId != null) {
        EggCollectionDialog(
            pairId = showEggDialogForPairId!!,
            onDismiss = { showEggDialogForPairId = null },
            onSubmit = { count, grade, weight ->
                viewModel.collectEggs(showEggDialogForPairId!!, count, grade, weight)
                showEggDialogForPairId = null
            }
        )
    }
}

@Composable
fun BreedingUnitCard(
    pair: BreedingPairEntity,
    onCollectEggs: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pair #${pair.pairId.take(4)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Badge { Text(pair.status) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Male: ${pair.maleProductId}", style = MaterialTheme.typography.bodyMedium)
            Text("Female: ${pair.femaleProductId}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Eggs: ${pair.eggsCollected}")
                Text("Hatch Rate: ${(pair.hatchSuccessRate * 100).toInt()}%")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCollectEggs,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Egg, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Log Egg Collection")
            }
        }
    }
}
