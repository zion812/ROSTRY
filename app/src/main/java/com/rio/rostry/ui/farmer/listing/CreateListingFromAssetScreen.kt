package com.rio.rostry.ui.farmer.listing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.rio.rostry.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingFromAssetScreen(
    assetId: String,
    viewModel: CreateListingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onListingCreated: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Load asset on first composition
    LaunchedEffect(assetId) {
        viewModel.loadAsset(assetId)
    }

    // Handle success/failure events
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onListingCreated()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.list_item_for_sale)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.asset == null && !state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.asset_not_found))
            }
        } else {
            val asset = state.asset!!
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 // Asset Summary Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.source_asset, asset.name),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.available_quantity, asset.quantity.toInt(), asset.unit),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                OutlinedTextField(
                    value = state.listingTitle,
                    onValueChange = viewModel::onTitleChanged,
                    label = { Text(stringResource(R.string.listing_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.quantityToSell.toString(), // Simplified handling, could format better
                    onValueChange = viewModel::onQuantityChanged,
                    label = { Text(stringResource(R.string.quantity_to_sell)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = { Text(stringResource(R.string.max_quantity_hint, asset.quantity.toInt())) }
                )

                OutlinedTextField(
                    value = state.listingPrice.toString(),
                    onValueChange = viewModel::onPriceChanged,
                    label = { Text(stringResource(R.string.price_per_unit)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Text("₹", modifier = Modifier.padding(start = 12.dp)) }
                )
                
                OutlinedTextField(
                    value = state.listingDescription,
                    onValueChange = viewModel::onDescriptionChanged,
                    label = { Text(stringResource(R.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = viewModel::submitListing,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSubmitting
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Storefront, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.publish_listing))
                    }
                }
            }
        }
    }
}
