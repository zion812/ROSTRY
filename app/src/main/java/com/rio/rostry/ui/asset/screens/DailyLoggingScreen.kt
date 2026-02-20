package com.rio.rostry.ui.asset.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.asset.logging.DailyLoggingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLoggingScreen(
    assetId: String,
    viewModel: DailyLoggingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Daily Log (Asset $assetId)") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("Daily Logging Screen (Under Construction)")
        }
    }
}
