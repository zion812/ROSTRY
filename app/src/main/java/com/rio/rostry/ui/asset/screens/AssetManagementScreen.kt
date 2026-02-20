package com.rio.rostry.ui.asset.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.asset.management.AssetManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetManagementScreen(
    viewModel: AssetManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAssetDetail: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Management") },
                // navigationIcon = { ... }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Asset Management Dashboard (Under Construction)")
        }
    }
}
