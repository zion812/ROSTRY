package com.rio.rostry.feature.breeding
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BreedingScreen(
    viewModel: BreedingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBreedingData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Breeding Management",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            BreedingContent(uiState)
        }
    }
}

@Composable
private fun BreedingContent(uiState: BreedingUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("Breeding Pairs", uiState.breedingPairs.toString())
        StatCard("Active Breeding", uiState.activeBreeding.toString())
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = value, style = MaterialTheme.typography.displaySmall)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
}
}

