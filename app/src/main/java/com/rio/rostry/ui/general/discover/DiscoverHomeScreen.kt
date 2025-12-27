package com.rio.rostry.ui.general.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.ui.components.CompactProductCard

/**
 * Discover Home Screen - Curated product discovery for general users.
 * Features: Near You, Festival Specials, High-Rated Farms
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverHomeScreen(
    onNavigateBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onSeeAllNearYou: () -> Unit,
    onSeeAllFestival: () -> Unit,
    onSeeAllHighRated: () -> Unit,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshDiscoverData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is DiscoverUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DiscoverUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(state.message)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshDiscoverData() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is DiscoverUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Near You Section
                    DiscoverSection(
                        title = "📍 Near You",
                        subtitle = "Birds from nearby farms",
                        products = state.nearYou,
                        onProductClick = onProductClick,
                        onSeeAll = onSeeAllNearYou,
                        emptyMessage = "No products near you yet"
                    )

                    Spacer(Modifier.height(16.dp))

                    // Festival Specials Section
                    DiscoverSection(
                        title = "🎉 Festival Specials",
                        subtitle = "Premium picks for the season",
                        products = state.festivalSpecials,
                        onProductClick = onProductClick,
                        onSeeAll = onSeeAllFestival,
                        accentColor = Color(0xFFFF6B35),
                        emptyMessage = "No festival specials right now"
                    )

                    Spacer(Modifier.height(16.dp))

                    // High-Rated Farms Section
                    DiscoverSection(
                        title = "⭐ High-Rated Farms",
                        subtitle = "Verified & trusted sellers",
                        products = state.highRatedFarms,
                        onProductClick = onProductClick,
                        onSeeAll = onSeeAllHighRated,
                        accentColor = Color(0xFFFFD700),
                        emptyMessage = "No verified farms yet"
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoverSection(
    title: String,
    subtitle: String,
    products: List<ProductEntity>,
    onProductClick: (String) -> Unit,
    onSeeAll: () -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    emptyMessage: String = "No products available"
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onSeeAll) {
                Text("See All", color = accentColor)
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = accentColor)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Products Row
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(emptyMessage, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    CompactProductCard(
                        product = product,
                        onClick = { onProductClick(product.productId) }
                    )
                }
            }
        }
    }
}
