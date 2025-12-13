package com.rio.rostry.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.ui.components.OnboardingChecklistCard
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.ui.onboarding.OnboardingChecklistViewModel

@Composable
fun HomeGeneralScreen(
    onProfile: () -> Unit,
    onBrowseMarketplace: () -> Unit,
    onSearchProducts: () -> Unit,
    onViewWishlist: () -> Unit,
    onMyOrders: () -> Unit,
    onProductDetails: (String) -> Unit,
    onUpgradeToFarmer: () -> Unit,
    onUpgradeToEnthusiast: () -> Unit,
    onJoinCommunity: () -> Unit,
    viewModel: GeneralHomeViewModel = hiltViewModel(),
    checklistViewModel: OnboardingChecklistViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val checklistState by checklistViewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HeroSection(uiState)
        }
        item {
            TopActions(
                onBrowse = {
                    viewModel.onBrowseMarketplaceClick()
                    onBrowseMarketplace()
                },
                onSearch = {
                    viewModel.onSearchProductsClick()
                    onSearchProducts()
                },
                onWishlist = {
                    viewModel.onViewWishlistClick()
                    onViewWishlist()
                },
                onOrders = {
                    viewModel.onMyOrdersClick()
                    onMyOrders()
                }
            )
        }
        item {
            RecommendedProducts(uiState.recommendedProducts) { productId ->
                viewModel.onProductClicked(productId)
                onProductDetails(productId)
            }
        }

        item {
            if (checklistState.isChecklistRelevant) {
                OnboardingChecklistCard(
                    items = checklistState.items,
                    completionPercentage = checklistState.completionPercentage,
                    onNavigate = { route ->
                        handleChecklistNavigation(route, onProfile, onBrowseMarketplace, onJoinCommunity)
                    },
                    onDismiss = { checklistViewModel.dismissChecklist() }
                )
            }
        }
        item {
            QuickLinks(
                onFarmer = {
                    viewModel.onUpgradeToFarmerClick()
                    onUpgradeToFarmer()
                },
                onEnthusiast = {
                    viewModel.onUpgradeToEnthusiastClick()
                    onUpgradeToEnthusiast()
                }
            )
        }
    }
}

@Composable
private fun HeroSection(uiState: GeneralHomeViewModel.UiState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Welcome, ${uiState.userName ?: "User"}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "${uiState.nearbyProductsCount} products nearby, ${uiState.newListingsToday} new listings today",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TopActions(
    onBrowse: () -> Unit,
    onSearch: () -> Unit,
    onWishlist: () -> Unit,
    onOrders: () -> Unit
) {
    Text(
        text = "What would you like to do today?",
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
        item { ActionCard("Browse Marketplace", Icons.Default.ShoppingCart, onBrowse) }
        item { ActionCard("Search Products", Icons.Default.Search, onSearch) }
        item { ActionCard("View Wishlist", Icons.Default.Favorite, onWishlist) }
        item { ActionCard("My Orders", Icons.Default.Receipt, onOrders) }
    }
}

@Composable
private fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable(onClick = onClick)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(32.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecommendedProducts(products: List<ProductEntity>, onProductClick: (String) -> Unit) {
    if (products.isEmpty()) return
    Text(
        text = "Recommended Products",
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
        items(products) { product ->
            ProductCard(product, onProductClick)
        }
    }
}

@Composable
private fun ProductCard(product: ProductEntity, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick(product.productId) }
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = product.imageUrls.firstOrNull() ?: "",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "5 km away", // Placeholder for distance calculation
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun QuickLinks(onFarmer: () -> Unit, onEnthusiast: () -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onFarmer) { Text("Upgrade to Farmer") }
        Button(onClick = onEnthusiast) { Text("Upgrade to Enthusiast") }
    }
}

private fun handleChecklistNavigation(
    route: String,
    onProfile: () -> Unit,
    onBrowseMarketplace: () -> Unit,
    onJoinCommunity: () -> Unit
) {
    when (route) {
        Routes.PROFILE -> onProfile()
        Routes.PRODUCT_MARKET -> onBrowseMarketplace()
        Routes.GROUPS -> onJoinCommunity()
        else -> {}
    }
}
