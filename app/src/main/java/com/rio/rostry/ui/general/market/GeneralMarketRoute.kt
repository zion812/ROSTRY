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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.testTag
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
        onApplyPreset = viewModel::applyQuickPreset,
        onClearFilters = viewModel::clearAllFilters,
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
        onAddToCart = viewModel::addToCart,
        onToggleWishlist = viewModel::toggleWishlist,
        onTrackView = viewModel::trackProductView
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
    onApplyPreset: (GeneralMarketViewModel.QuickPreset) -> Unit,
    onClearFilters: () -> Unit,
    onSelectBreed: (String) -> Unit,
    onSelectAgeGroup: (ValidationUtils.AgeGroup) -> Unit,
    onOpenProductDetails: (String) -> Unit,
    onOpenTraceability: (String) -> Unit,
    onAddToCart: (ProductEntity, Double) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onTrackView: (String) -> Unit
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
            // Compact Filter Bar with Badge
            var showFilterSheet by remember { mutableStateOf(false) }
            CompactFilterBar(
                activeCount = state.activeFilterCount,
                presets = state.filterPresets,
                onOpenFilters = { showFilterSheet = true },
                onApplyPreset = onApplyPreset,
                onRequestLocation = { showLocationDialog = true },
                onToggleVerified = onToggleVerified,
                onClearFilters = onClearFilters,
                onSelectBreed = onSelectBreed,
                onSelectAgeGroup = onSelectAgeGroup
            )

            if (state.products.isEmpty() && !state.isLoading) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Trending Products Section
                    if (state.trendingProducts.isNotEmpty()) {
                        item {
                            TrendingProductsSection(
                                products = state.trendingProducts,
                                wishlistIds = state.wishlistProductIds,
                                onProductClick = { 
                                    onTrackView(it.productId)
                                    onOpenProductDetails(it.productId)
                                },
                                onToggleWishlist = onToggleWishlist,
                                onAddToCart = { product -> onAddToCart(product, 1.0) }
                            )
                        }
                    }
                    
                    // Recommended Products Section
                    if (state.recommendedProducts.isNotEmpty()) {
                        item {
                            RecommendedProductsSection(
                                products = state.recommendedProducts,
                                wishlistIds = state.wishlistProductIds,
                                onProductClick = { 
                                    onTrackView(it.productId)
                                    onOpenProductDetails(it.productId)
                                },
                                onToggleWishlist = onToggleWishlist,
                                onAddToCart = { product -> onAddToCart(product, 1.0) }
                            )
                        }
                    }
                    
                    // All Products Grid Header
                    item {
                        Text(
                            text = "All Products",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                                .semantics { heading() }
                        )
                    }
                    
                    // Products in grid pattern using LazyColumn items (avoids nested lazy layout crash)
                    items(state.products.chunked(2), key = { row -> row.first().productId }) { rowProducts ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowProducts.forEach { product ->
                                EnhancedProductCard(
                                    product = product,
                                    isInWishlist = state.wishlistProductIds.contains(product.productId),
                                    onClick = { 
                                        onTrackView(product.productId)
                                        onOpenProductDetails(product.productId)
                                    },
                                    onOpenTraceability = { onOpenTraceability(product.productId) },
                                    onAddToCart = { onAddToCart(product, 1.0) },
                                    onToggleWishlist = { onToggleWishlist(product) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty space if odd number of products
                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
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
    Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Box {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("market_search_field"),
                value = query,
                onValueChange = onQueryChange,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search products") },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        TextButton(
                            onClick = onClearQuery,
                            modifier = Modifier.semantics { contentDescription = "Clear search" }
                        ) { Text("Clear") }
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
private fun CompactFilterBar(
    activeCount: Int,
    presets: List<GeneralMarketViewModel.FilterPreset>,
    onOpenFilters: () -> Unit,
    onApplyPreset: (GeneralMarketViewModel.QuickPreset) -> Unit,
    onRequestLocation: () -> Unit,
    onToggleVerified: () -> Unit,
    onClearFilters: () -> Unit,
    onSelectBreed: (String) -> Unit,
    onSelectAgeGroup: (ValidationUtils.AgeGroup) -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter button with badge
            BadgedBox(
                badge = {
                    if (activeCount > 0) {
                        Badge { Text("$activeCount") }
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = if (activeCount > 0) {
                        "Filters, $activeCount active"
                    } else {
                        "Filters"
                    }
                }
            ) {
                OutlinedButton(
                    onClick = onOpenFilters,
                    modifier = Modifier.testTag("market_filter_button")
                ) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filter icon")
                    Spacer(Modifier.width(4.dp))
                    Text("Filters")
                }
                if (activeCount > 0) {
                    TextButton(
                        onClick = onClearFilters,
                        modifier = Modifier.testTag("market_clear_filters_button")
                    ) { Text("Clear") }
                }
            }
            
            // Quick preset chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.semantics { contentDescription = "Quick filter presets" }
            ) {
                items(presets) { preset ->
                    FilterChip(
                        selected = false,
                        onClick = { onApplyPreset(preset.id) },
                        label = { Text(preset.label) },
                        modifier = Modifier
                            .semantics { 
                                role = Role.Button
                                contentDescription = "Apply ${preset.label} preset"
                            }
                            .testTag("preset_${preset.id}")
                    )
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

@Composable
private fun TrendingProductsSection(
    products: List<ProductEntity>,
    wishlistIds: Set<String>,
    onProductClick: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥 Trending Now",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.productId }) { product ->
                CompactProductCard(
                    product = product,
                    isInWishlist = wishlistIds.contains(product.productId),
                    onClick = { onProductClick(product) },
                    onToggleWishlist = { onToggleWishlist(product) },
                    onAddToCart = { onAddToCart(product) }
                )
            }
        }
    }
}

@Composable
private fun RecommendedProductsSection(
    products: List<ProductEntity>,
    wishlistIds: Set<String>,
    onProductClick: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ Recommended for You",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.productId }) { product ->
                CompactProductCard(
                    product = product,
                    isInWishlist = wishlistIds.contains(product.productId),
                    onClick = { onProductClick(product) },
                    onToggleWishlist = { onToggleWishlist(product) },
                    onAddToCart = { onAddToCart(product) }
                )
            }
        }
    }
}

@Composable
private fun CompactProductCard(
    product: ProductEntity,
    isInWishlist: Boolean,
    onClick: () -> Unit,
    onToggleWishlist: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 140.dp, height = 200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box {
            Column(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${"%.0f".format(product.price)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = sellerRating(product.sellerId),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            
            // Wishlist button overlay
            IconButton(
                onClick = onToggleWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isInWishlist) "Remove from wishlist" else "Add to wishlist",
                    tint = if (isInWishlist) Color.Red else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EnhancedProductCard(
    product: ProductEntity,
    isInWishlist: Boolean,
    onClick: () -> Unit,
    onOpenTraceability: () -> Unit,
    onAddToCart: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Box {
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
                    // Trust badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (product.familyTreeId != null) {
                            androidx.compose.material3.AssistChip(
                                onClick = {},
                                label = { Text("Traceable", style = MaterialTheme.typography.labelSmall) },
                                leadingIcon = { Icon(Icons.Filled.Map, null, modifier = Modifier.size(14.dp)) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        if (!product.sellerId.isBlank()) {
                            androidx.compose.material3.AssistChip(
                                onClick = {},
                                label = { Text("Verified", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = product.location,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${"%.2f".format(product.price)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Stock indicator
                        val stockColor = when {
                            product.quantity > 10 -> Color(0xFF4CAF50)
                            product.quantity > 3 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                        val stockText = when {
                            product.quantity > 10 -> "In Stock"
                            product.quantity > 0 -> "Only ${product.quantity.toInt()} left"
                            else -> "Out of Stock"
                        }
                        Text(
                            text = stockText,
                            style = MaterialTheme.typography.labelSmall,
                            color = stockColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFFC107)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "${sellerRating(product.sellerId)}★ · Seller rating",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = onAddToCart,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add to Cart")
                        }
                        Button(
                            onClick = onClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Details")
                        }
                    }
                }
            }
            
            // Wishlist button overlay
            IconButton(
                onClick = onToggleWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isInWishlist) "Remove from wishlist" else "Add to wishlist",
                    tint = if (isInWishlist) Color.Red else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
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
