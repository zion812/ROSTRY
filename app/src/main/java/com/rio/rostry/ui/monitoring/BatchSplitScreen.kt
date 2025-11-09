package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.text.KeyboardOptions
import com.rio.rostry.R
import com.rio.rostry.ui.components.*
import com.rio.rostry.ui.monitoring.BatchSplitViewModel
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.utils.media.MediaUploadManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchSplitScreen(
    batchId: String,
    navController: NavController,
    viewModel: BatchSplitViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ui by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(batchId) {
        viewModel.loadBatch(batchId)
    }

    LaunchedEffect(ui.navigationEvent) {
        when (ui.navigationEvent) {
            is BatchSplitViewModel.NavigationEvent.NavigateToFarmMonitoring -> {
                // Navigate back to previous or monitoring screen as appropriate
                navController.popBackStack()
                viewModel.clearNavigationEvent()
            }
            null -> Unit
        }
    }

    LaunchedEffect(ui.error) {
        ui.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Batch") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (ui.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ui.batch?.let { BatchDetailsCard(it) }
                }

                items(ui.birds.size) { index ->
                    val bird = ui.birds[index]
                    BirdFormCard(
                        index = index,
                        bird = bird,
                        onUpdate = { updated -> viewModel.updateBirdForm(index, updated) }
                    )
                }

                item {
                    Button(
                        onClick = { viewModel.splitBatch() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !ui.isLoading
                    ) {
                        Text("Split Batch")
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchDetailsCard(batch: com.rio.rostry.data.database.entity.ProductEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Batch Details", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Name: ${batch.name}")
            Text("Count: ${batch.quantity ?: 0}")
            Text("Age: ${batch.ageWeeks ?: 0} weeks")
            Text("Breed: ${batch.breed ?: "-"}")
            Text("Status: ${batch.status ?: "-"}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirdFormCard(
    index: Int,
    bird: BatchSplitViewModel.BirdForm,
    onUpdate: (BatchSplitViewModel.BirdForm) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bird #${index + 1}", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bird.name,
                onValueChange = { onUpdate(bird.copy(name = it)) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = bird.gender ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Male", "Female").forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(gender) },
                            onClick = {
                                onUpdate(bird.copy(gender = gender))
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = (bird.weight?.toString() ?: ""),
                onValueChange = { onUpdate(bird.copy(weight = it.toDoubleOrNull())) },
                label = { Text("Weight (grams)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
