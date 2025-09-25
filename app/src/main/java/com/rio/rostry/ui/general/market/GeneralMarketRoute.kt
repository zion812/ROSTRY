package com.rio.rostry.ui.general.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.ui.general.market.GeneralMarketViewModel.MarketFilters
import com.rio.rostry.ui.general.market.GeneralMarketViewModel.MarketUiState
import com.rio.rostry.utils.ValidationUtils
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun GeneralMarketRoute(
    onOpenProductDetails: (String) -> Unit,
    onOpenTraceability: (String) -> Unit,
    viewModel: GeneralMarketViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.lastCartMessage) {
        uiState.lastCartMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.acknowledgeCartMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearError()
        }
    }

    GeneralMarketScreen(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onQueryChange = viewModel::onQueryChange,
        onSuggestionSelected = {
            viewModel.onQueryChange(it)
            viewModel.clearSuggestions()
        },
        onClearQuery = {
            viewModel.onQueryChange("")
            viewModel.clearSuggestions()
        },
        onToggleNearby = {
            viewModel.updateFilters { current ->
                val toggled = current.copy(nearbyEnabled = !current.nearbyEnabled)
                if (toggled.nearbyEnabled && toggled.currentLocation == null) {
                    toggled.copy(nearbyEnabled = false)
                } else {
                    toggled
                }
            }
        },
        onSetLocation = viewModel::setLocation,
        onToggleVerified = {
            viewModel.updateFilters { it.copy(verifiedOnly = !it.verifiedOnly) }
        },
        onSelectBreed = { breed ->
            viewModel.updateFilters {
                val same = it.selectedBreed.equals(breed, ignoreCase = true)
                it.copy(selectedBreed = if (same) null else breed)
            }
        },
        onSelectAgeGroup = { group ->
            viewModel.updateFilters {
                val same = it.selectedAgeGroup == group
                it.copy(selectedAgeGroup = if (same) null else group)
            }
        },
        onOpenProductDetails = onOpenProductDetails,
        onOpenTraceability = onOpenTraceability,
        onAddToCart = viewModel::addToCart
    )
}

@Composable
private fun GeneralMarketScreen(
    state: MarketUiState,
    snackbarHostState: SnackbarHostState,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (String) -> Unit,
    onClearQuery: () -> Unit,
    onToggleNearby: () -> Unit,
    onSetLocation: (Double, Double) -> Unit,
    onToggleVerified: () -> Unit,
    onSelectBreed: (String) -> Unit,
    onSelectAgeGroup: (ValidationUtils.AgeGroup) -> Unit,
    onOpenProductDetails: (String) -> Unit,
    onOpenTraceability: (String) -> Unit,
    onAddToCart: (ProductEntity, Double) -> Unit
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    if (showLocationDialog) {
        LocationDialog(
            onConfirm = { lat, lon ->
                onSetLocation(lat, lon)
                showLocationDialog = false
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MarketTopBar(
                query = state.query,
                onQueryChange = {
                    showSuggestions = true
                    onQueryChange(it)
                },
                onClearQuery = {
                    showSuggestions = false
                    onClearQuery()
                },
                suggestions = state.suggestions,
                onSuggestionSelected = {
                    showSuggestions = false
                    onSuggestionSelected(it)
                },
                expanded = showSuggestions && state.suggestions.isNotEmpty()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            FilterRow(
                filters = state.filters,
                breeds = state.products.mapNotNull { it.breed }.distinct().sorted(),
                onToggleNearby = onToggleNearby,
                onRequestLocation = { showLocationDialog = true },
                onToggleVerified = onToggleVerified,
                onSelectBreed = onSelectBreed,
                onSelectAgeGroup = onSelectAgeGroup
            )

            if (state.products.isEmpty() && !state.isLoading) {
                EmptyState()
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.products, key = { it.productId }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onOpenProductDetails(product.productId) },
                            onOpenTraceability = { onOpenTraceability(product.productId) },
                            onAddToCart = { onAddToCart(product, 1.0) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    suggestions: List<String>,
    onSuggestionSelected: (String) -> Unit,
    expanded: Boolean
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Marketplace",
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Box {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = query,
                onValueChange = onQueryChange,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        TextButton(onClick = onClearQuery) { Text("Clear") }
                    }
                },
                placeholder = { Text("Search breeds, age, location, sellers") },
                singleLine = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {}
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = { onSuggestionSelected(suggestion) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun FilterRow(
    filters: MarketFilters,
    breeds: List<String>,
    onToggleNearby: () -> Unit,
    onRequestLocation: () -> Unit,
    onToggleVerified: () -> Unit,
    onSelectBreed: (String) -> Unit,
    onSelectAgeGroup: (ValidationUtils.AgeGroup) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = filters.nearbyEnabled,
                onClick = {
                    if (!filters.nearbyEnabled && filters.currentLocation == null) {
                        onRequestLocation()
                    } else {
                        onToggleNearby()
                    }
                },
                label = { Text("Nearby") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) }
            )
            FilterChip(
                selected = filters.verifiedOnly,
                onClick = onToggleVerified,
                label = { Text("Verified sellers") },
                leadingIcon = { Icon(Icons.Filled.FilterList, contentDescription = null) }
            )
            ValidationUtils.AgeGroup.values().forEach { group ->
                FilterChip(
                    selected = filters.selectedAgeGroup == group,
                    onClick = { onSelectAgeGroup(group) },
                    label = { Text(group.name.replace('_', ' ')) }
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    breeds.forEach { breed ->
                        val selected = filters.selectedBreed.equals(breed, ignoreCase = true)
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectBreed(breed) },
                            label = { Text(breed) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onOpenTraceability: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = product.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.location,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Seller rating · ${sellerRating(product.sellerId)}★",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onOpenTraceability) {
                        Icon(Icons.Filled.Map, contentDescription = null)
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Traceability")
                    }
                    Button(onClick = onAddToCart) {
                        Text("Add to cart")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No products found",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting filters or searching for other breeds",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun sellerRating(sellerId: String): String {
    if (sellerId.isBlank()) return "4.5"
    val normalized = (sellerId.hashCode().absoluteValue % 20) / 10.0
    val rating = 4.0 + normalized
    return "%.1f".format(rating.coerceIn(4.0, 5.0))
}

@Composable
private fun LocationDialog(
    onConfirm: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(latitude, longitude) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Set current location") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = latitude.toString(),
                    onValueChange = { value ->
                        latitude = value.toDoubleOrNull() ?: latitude
                    },
                    label = { Text("Latitude") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = longitude.toString(),
                    onValueChange = { value ->
                        longitude = value.toDoubleOrNull() ?: longitude
                    },
                    label = { Text("Longitude") }
                )
            }
        }
    )
}
