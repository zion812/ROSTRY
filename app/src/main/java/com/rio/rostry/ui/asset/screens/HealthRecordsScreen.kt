package com.rio.rostry.ui.asset.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.asset.health.HealthRecordsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(
    assetId: String,
    viewModel: HealthRecordsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Health Records") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Health Records Screen (Under Construction)")
        }
    }
}
