package com.rio.rostry.feature.farm.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FarmProfileScreen(
    onNavigateToEdit: () -> Unit,
    viewModel: FarmProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Farm Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            ProfileContent(
                uiState = uiState,
                onEditClick = onNavigateToEdit
            )
        }
    }
}

@Composable
private fun ProfileContent(
    uiState: FarmProfileUiState,
    onEditClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileStatRow(
            farmName = uiState.farmName,
            trustScore = uiState.trustScore,
            isVerified = uiState.isVerified
        )

        OutlinedButton(onClick = onEditClick) {
            Text("Edit Profile")
        }
    }
}

@Composable
private fun ProfileStatRow(
    farmName: String,
    trustScore: Int,
    isVerified: Boolean
) {
    Column {
        Text(
            text = farmName.ifEmpty { "Farm Name" },
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Trust Score: $trustScore")
            if (isVerified) {
                Text("✓ Verified", color = MaterialTheme.colorScheme.primary)
            }
        }
}
}

