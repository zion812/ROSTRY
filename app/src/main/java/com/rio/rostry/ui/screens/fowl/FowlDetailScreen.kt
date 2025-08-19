package com.rio.rostry.ui.screens.fowl

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.navigation.AppRoutes
import com.rio.rostry.viewmodel.FowlDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FowlDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FowlDetailViewModel = viewModel()
) {
    val fowl by viewModel.fowl.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val scrollState = rememberScrollState()

    LaunchedEffect(error) {
        error?.let {
            // Handle error display
            // In a real app, you would show a snackbar or dialog
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fowl?.name ?: "Fowl Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            fowl?.let { fowlValue ->
                FloatingActionButton(
                    onClick = { 
                        navController.navigate(AppRoutes.addHealthRecord(fowlValue.fowlId))
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Health Record")
                }
            }
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error?.message ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refreshFowl() }) {
                        Text("Retry")
                    }
                }
            }
        } else if (fowl != null) {
            FowlDetailContent(
                fowl = fowl!!,
                scrollState = scrollState,
                modifier = modifier.padding(padding)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Fowl not found")
            }
        }
    }
}

@Composable
fun FowlDetailContent(
    fowl: Fowl,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Photo section
        if (fowl.photoUrl.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fowl Photo",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // Handle both local URIs and web URLs
                    AsyncImage(
                        model = fowl.photoUrl,
                        contentDescription = "Fowl photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Basic Information Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DetailItem("Name", fowl.name)
                DetailItem("Breed", fowl.breed)
                DetailItem("Birth Date", formatDate(fowl.birthDate))
                DetailItem("Status", fowl.status)
                DetailItem("Created", formatDate(fowl.createdAt))
                DetailItem("Last Updated", formatDate(fowl.updatedAt))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lineage Information Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Lineage Information",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DetailItem("Parents", if (fowl.parentIds.isEmpty()) "None" else fowl.parentIds.joinToString(", "))
                DetailItem("Lineage Notes", fowl.lineageNotes.ifEmpty { "None" })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Health Records Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Health Records",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                if (fowl.healthRecords.isEmpty()) {
                    Text(
                        text = "No health records yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    fowl.healthRecords.forEachIndexed { index, record ->
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailItem("Record #${index + 1}", "")
                        DetailItem("Type", record.type)
                        DetailItem("Date", formatDate(record.date))
                        DetailItem("Notes", record.notes.ifEmpty { "None" })
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown"
    }
}