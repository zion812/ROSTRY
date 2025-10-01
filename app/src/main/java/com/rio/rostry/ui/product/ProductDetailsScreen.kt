package com.rio.rostry.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun ProductDetailsScreen(
    productId: String,
    onOpenTraceability: () -> Unit,
    onNavigateToProduct: (String) -> Unit = {},
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.cartMessage) {
        uiState.cartMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.product != null) {
                ProductActionBar(
                    product = uiState.product!!,
                    isInWishlist = uiState.isInWishlist,
                    onToggleWishlist = { viewModel.toggleWishlist() },
                    onAddToCart = { viewModel.addToCart(1.0) },
                    onBuyNow = { viewModel.buyNow(1.0) }
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.product != null) {
            ProductDetailsContent(
                product = uiState.product!!,
                similarProducts = uiState.similarProducts,
                frequentlyBoughtTogether = uiState.frequentlyBoughtTogether,
                isInWishlist = uiState.isInWishlist,
                onOpenTraceability = onOpenTraceability,
                onNavigateToProduct = onNavigateToProduct,
                onToggleWishlist = { viewModel.toggleWishlist() },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(
    product: ProductEntity,
    similarProducts: List<ProductEntity>,
    frequentlyBoughtTogether: List<ProductEntity>,
    isInWishlist: Boolean,
    onOpenTraceability: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // Image Gallery
        item {
            ImageGallery(images = product.imageUrls.takeIf { it.isNotEmpty() } ?: listOf(""))
        }

        // Product Info
        item {
            ProductInfoSection(
                product = product,
                isInWishlist = isInWishlist,
                onToggleWishlist = onToggleWishlist
            )
        }

        // Seller Info
        item {
            SellerInfoCard(product = product)
        }

        // Description
        item {
            DescriptionSection(description = product.description)
        }

        // Key Attributes
        item {
            KeyAttributesSection(product = product)
        }

        // Traceability
        if (product.familyTreeId != null) {
            item {
                TraceabilityCard(onOpenTraceability = onOpenTraceability)
            }
        }

        // Similar Products
        if (similarProducts.isNotEmpty()) {
            item {
                SimilarProductsSection(
                    products = similarProducts,
                    onProductClick = onNavigateToProduct
                )
            }
        }

        // Frequently Bought Together
        if (frequentlyBoughtTogether.isNotEmpty()) {
            item {
                FrequentlyBoughtTogetherSection(
                    products = frequentlyBoughtTogether,
                    onProductClick = onNavigateToProduct
                )
            }
        }

        // Reviews Placeholder
        item {
            ReviewsPlaceholderSection()
        }

        // Q&A Placeholder
        item {
            QAPlaceholderSection()
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun ImageGallery(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
        }

        // Page Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
private fun ProductInfoSection(
    product: ProductEntity,
    isInWishlist: Boolean,
    onToggleWishlist: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Trust badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            if (product.familyTreeId != null) {
                AssistChip(
                    onClick = {},
                    label = { Text("✓ Traceable lineage") },
                    leadingIcon = { Icon(Icons.Filled.Verified, null, modifier = Modifier.size(16.dp)) }
                )
            }
            if (!product.sellerId.isBlank()) {
                AssistChip(
                    onClick = {},
                    label = { Text("✓ Verified seller") },
                    leadingIcon = { Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        // Title and wishlist
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onToggleWishlist) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = if (isInWishlist) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rating and reviews
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFFFC107)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${sellerRating(product.sellerId)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${(product.productId.hashCode().absoluteValue % 100 + 10)} reviews)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Price
        Text(
            text = "₹${"%.2f".format(product.price)}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stock status
        val stockColor = when {
            product.quantity > 10 -> Color(0xFF4CAF50)
            product.quantity > 3 -> Color(0xFFFF9800)
            else -> Color(0xFFF44336)
        }
        val stockText = when {
            product.quantity > 10 -> "✓ In Stock"
            product.quantity > 0 -> "⚠ Only ${product.quantity.toInt()} left - Order soon"
            else -> "✗ Out of Stock"
        }
        Text(
            text = stockText,
            style = MaterialTheme.typography.titleSmall,
            color = stockColor,
            fontWeight = FontWeight.Medium
        )

        // View count
        Text(
            text = "👁 ${(product.productId.hashCode().absoluteValue % 500 + 50)} people viewed this today",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SellerInfoCard(product: ProductEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Store,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Seller",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (product.sellerId.isNotBlank()) "Verified Seller" else "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${sellerRating(product.sellerId)} (${(product.sellerId.hashCode().absoluteValue % 50 + 10)} ratings)",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            TextButton(onClick = {}) {
                Text("View Profile")
            }
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = if (expanded) TextOverflow.Visible else TextOverflow.Ellipsis
            )
            if (description.length > 100) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Show less" else "Read more")
                }
            }
        }
    }
}

@Composable
private fun KeyAttributesSection(product: ProductEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Product Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            product.breed?.let { AttributeRow("Breed", it) }
            product.gender?.let { AttributeRow("Gender", it) }
            product.color?.let { AttributeRow("Color", it) }
            if (product.birthDate != null) {
                val ageInDays = ((System.currentTimeMillis() - product.birthDate) / (24 * 60 * 60 * 1000)).toInt()
                AttributeRow("Age", "$ageInDays days")
            }
            product.weightGrams?.let { AttributeRow("Weight", "${it}g") }
            AttributeRow("Category", product.category)
            AttributeRow("Location", product.location)
            AttributeRow("Unit", product.unit)
            AttributeRow("Quantity Available", product.quantity.toString())
        }
    }
}

@Composable
private fun AttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TraceabilityCard(onOpenTraceability: () -> Unit) {
    Card(
        onClick = onOpenTraceability,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🧬 View Family Tree",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Explore complete lineage and breeding history",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SimilarProductsSection(
    products: List<ProductEntity>,
    onProductClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Similar items you might like",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                RecommendationCard(product = product, onClick = { onProductClick(product.productId) })
            }
        }
    }
}

@Composable
private fun FrequentlyBoughtTogetherSection(
    products: List<ProductEntity>,
    onProductClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Frequently bought together",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                RecommendationCard(product = product, onClick = { onProductClick(product.productId) })
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 140.dp, height = 180.dp)
    ) {
        Column {
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹${"%.0f".format(product.price)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReviewsPlaceholderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Reviews and ratings will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QAPlaceholderSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Questions & Answers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Customer questions will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProductActionBar(
    product: ProductEntity,
    isInWishlist: Boolean,
    onToggleWishlist: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onToggleWishlist,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = if (isInWishlist) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
            
            OutlinedButton(
                onClick = onAddToCart,
                modifier = Modifier.weight(1f).height(48.dp),
                enabled = product.quantity > 0
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Cart")
            }
            
            Button(
                onClick = onBuyNow,
                modifier = Modifier.weight(1f).height(48.dp),
                enabled = product.quantity > 0
            ) {
                Text("Buy Now")
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
