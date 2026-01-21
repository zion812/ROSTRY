package com.rio.rostry.ui.publicaccess

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Lock
import com.rio.rostry.domain.repository.PublicBirdView
@Composable
fun PublicBirdLookupScreen(
    viewModel: PublicBirdLookupViewModel = hiltViewModel(),
    onBirdClick: (String) -> Unit = {} // If we want to navigate to details (if allowed)
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val colors = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        
        Text(
            text = "Public Bird Lookup",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Search Bar
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChanged,
            label = { Text("Enter 6-Digit Bird Code") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = viewModel::lookup) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when (val state = uiState) {
            is LookupUiState.Loading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
            }
            is LookupUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is LookupUiState.Success -> {
                when (val view = state.data) {
                    is PublicBirdView.Full -> {
                        // Using a simple card summary for now, or re-use AssetCard if available and compatible
                        // For flexibility, let's render a basic public card here
                        Card(
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(text = view.data.name.ifBlank { "Unnamed Bird" }, style = MaterialTheme.typography.titleLarge)
                                Text(text = "Breed: ${view.data.breed ?: "Unknown"}")
                                Text(text = "Code: ${view.data.birdCode}")
                                Text(text = "Status: Publicly Verified", color = colors.primary)
                            }
                        }
                    }
                    is PublicBirdView.Restricted -> {
                        Card(
                             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                             modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = "Private", tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Bird Exists", style = MaterialTheme.typography.titleMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Code: ${view.birdCode}")
                                Text(text = view.message, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            is LookupUiState.Idle -> {
                Text(text = "Enter a bird code to check its validity or view details.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
