package com.rio.rostry.ui.market

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.models.market.MarketplaceListing
import com.rio.rostry.ui.market.cart.CartViewModel
import com.rio.rostry.ui.messaging.MessagingViewModel
import com.rio.rostry.ui.components.REmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    marketplaceViewModel: MarketplaceViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    messagingViewModel: MessagingViewModel = hiltViewModel(),
    onSellFowl: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                marketplaceViewModel.fetchListingsNearMe(10.0) // Default radius 10km
            }
        }
    )
    val uiState by marketplaceViewModel.uiState.collectAsState()
    val filters by marketplaceViewModel.filters.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onSellFowl) {
                Icon(Icons.Default.Add, contentDescription = "Sell a Fowl")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Marketplace") },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping Cart")
                    }
                    IconButton(onClick = onNavigateToWishlist) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist")
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Sort Options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(text = { Text("Price: Low to High") }, onClick = { 
                            marketplaceViewModel.onSortOrderChanged(SortOrder.PRICE_ASC)
                            showMenu = false 
                        })
                        DropdownMenuItem(text = { Text("Price: High to Low") }, onClick = { 
                            marketplaceViewModel.onSortOrderChanged(SortOrder.PRICE_DESC)
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text("Date: Newest First") }, onClick = { 
                            marketplaceViewModel.onSortOrderChanged(SortOrder.DATE_NEWEST)
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text("Date: Oldest First") }, onClick = { 
                            marketplaceViewModel.onSortOrderChanged(SortOrder.DATE_OLDEST)
                            showMenu = false
                        })
                    }

                    IconButton(onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            marketplaceViewModel.fetchListingsNearMe(10.0)
                        } else {
                            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Filter by location")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is MarketplaceUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MarketplaceUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is MarketplaceUiState.Success -> {
                if (state.listings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        REmptyState(
                            title = "No listings found",
                            subtitle = "Try adjusting filters or come back later.",
                            primaryCtaLabel = "Clear Filter",
                            onPrimaryCta = { marketplaceViewModel.fetchMarketplaceListings() }
                        )
                    }
                } else {
                    Column(modifier = Modifier.padding(paddingValues)) {
                        OutlinedTextField(
                            value = filters.searchQuery,
                            onValueChange = { marketplaceViewModel.onSearchQueryChanged(it) },
                            label = { Text("Search by breed or description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                            Button(onClick = { marketplaceViewModel.fetchMarketplaceListings() }) {
                                Text("Clear Filter")
                            }
                        }
                        MarketplaceList(
                            listings = state.listings,
                            onAddToCart = { cartViewModel.addToCart(it) },
                            onAddToWishlist = { cartViewModel.addToWishlist(it) },
                            onContactSeller = { sellerId ->
                                messagingViewModel.createConversation(sellerId) { conversationId ->
                                    conversationId?.let { onNavigateToChat(it) }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceList(
    listings: List<MarketplaceListing>,
    onAddToCart: (String) -> Unit,
    onAddToWishlist: (String) -> Unit,
    onContactSeller: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(listings) { listing ->
            MarketplaceListItem(
                listing = listing,
                onAddToCart = { onAddToCart(listing.id) },
                onAddToWishlist = { onAddToWishlist(listing.id) },
                onContactSeller = { onContactSeller(listing.sellerId) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MarketplaceListItem(
    listing: MarketplaceListing,
    onAddToCart: () -> Unit,
    onAddToWishlist: () -> Unit,
    onContactSeller: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = listing.imageUrl,
                contentDescription = "Fowl Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = listing.breed, style = MaterialTheme.typography.headlineSmall)
                Text(text = "$${listing.price}", style = MaterialTheme.typography.bodyLarge)
            Row {
                IconButton(onClick = onAddToCart) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Add to Cart")
                }
                IconButton(onClick = onAddToWishlist) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Add to Wishlist")
                }
                Button(onClick = onContactSeller) {
                    Text("Contact Seller")
                }
            }
                Text(text = listing.description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
