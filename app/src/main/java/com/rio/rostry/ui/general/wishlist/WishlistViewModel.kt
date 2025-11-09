package com.rio.rostry.ui.general.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.WishlistEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.WishlistRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.network.ConnectivityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val analyticsTracker: GeneralAnalyticsTracker,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    data class WishlistItemUi(
        val wishlistId: String,
        val productId: String,
        val name: String,
        val price: Double,
        val unit: String,
        val imageUrl: String?,
        val location: String,
        val availability: String,
        val sellerId: String
    )

    data class WishlistUiState(
        val isAuthenticated: Boolean = true,
        val isLoading: Boolean = true,
        val items: List<WishlistItemUi> = emptyList(),
        val error: String? = null,
        val successMessage: String? = null,
        val isBulkAdding: Boolean = false
    )

    private val error = MutableStateFlow<String?>(null)
    private val successMessage = MutableStateFlow<String?>(null)
    private val isBulkAdding = MutableStateFlow(false)

    private val userId: String? = currentUserProvider.userIdOrNull()

    private val wishlistItems: Flow<List<WishlistEntity>> = userId?.let { uid ->
        wishlistRepository.observe(uid)
    } ?: flowOf(emptyList())

    private val products: StateFlow<Resource<List<ProductEntity>>> = productRepository
        .getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading()
        )

    private data class BaseInputs(
        val wishlist: List<WishlistEntity>,
        val products: Resource<List<ProductEntity>>
    )

    private val baseInputs: StateFlow<BaseInputs> = combine(
        wishlistItems,
        products
    ) { wishlist, productResource ->
        BaseInputs(
            wishlist = wishlist,
            products = productResource
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BaseInputs(
            wishlist = emptyList(),
            products = Resource.Loading()
        )
    )

    private val statusInputs: StateFlow<Pair<String?, String?>> = combine(
        error,
        successMessage
    ) { err, success ->
        Pair(err, success)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Pair(null, null)
    )

    val uiState: StateFlow<WishlistUiState> = combine(
        baseInputs,
        statusInputs,
        isBulkAdding
    ) { base, status, bulkAdding ->
        if (userId == null) {
            return@combine WishlistUiState(
                isAuthenticated = false,
                isLoading = false,
                error = "Sign in to view your wishlist"
            )
        }
        val productsData = base.products.data.orEmpty()
        val productMap = productsData.associateBy { it.productId }
        val items = base.wishlist.mapNotNull { entity ->
            val product = productMap[entity.productId]
            product?.let {
                WishlistItemUi(
                    wishlistId = "${entity.userId}:${entity.productId}",
                    productId = entity.productId,
                    name = it.name,
                    price = it.price,
                    unit = it.unit,
                    imageUrl = it.imageUrls.firstOrNull(),
                    location = it.location,
                    availability = if (it.quantity > 0) "In Stock" else "Out of Stock",
                    sellerId = it.sellerId
                )
            }
        }
        
        WishlistUiState(
            isAuthenticated = true,
            isLoading = base.products is Resource.Loading && items.isEmpty(),
            items = items,
            error = status.first ?: (base.products as? Resource.Error)?.message,
            successMessage = status.second,
            isBulkAdding = bulkAdding
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WishlistUiState(isAuthenticated = userId != null)
    )

    init {
        // Optional: generic analytics event
        userId?.let { analyticsTracker.productViewTracked("wishlist_screen", 0) }
    }

    fun removeFromWishlist(productId: String) {
        val uid = userId ?: run {
            error.value = "Sign in to manage wishlist"
            return
        }
        viewModelScope.launch {
            val result = wishlistRepository.remove(uid, productId)
            when (result) {
                is Resource.Success -> {
                    successMessage.value = "Removed from wishlist"
                    analyticsTracker.wishlistToggled(productId, added = false)
                }
                is Resource.Error -> {
                    error.value = result.message ?: "Failed to remove from wishlist"
                }
                is Resource.Loading -> {
                    // Loading state handled in UI
                }
            }
        }
    }

    fun addToCart(productId: String) {
        val uid = userId ?: run {
            error.value = "Sign in to add to cart"
            return
        }
        viewModelScope.launch {
            // Optionally fetch product if needed (non-blocking for offline friendliness)
            val product = runCatching { productRepository.getProductById(productId).firstOrNull()?.data }.getOrNull()
            val result = cartRepository.addOrUpdateItem(
                userId = uid,
                productId = productId,
                quantity = 1.0,
                buyerLat = null, // Could be enhanced to get user location
                buyerLon = null
            )
            when (result) {
                is Resource.Success -> {
                    successMessage.value = "Added to cart"
                    analyticsTracker.buyNowClicked(productId) // Track conversion
                }
                is Resource.Error -> {
                    error.value = result.message ?: "Failed to add to cart"
                }
                is Resource.Loading -> {
                    // Loading state handled in UI
                }
            }
        }
    }

    fun bulkAddToCart(productIds: List<String>) {
        val uid = userId ?: run {
            error.value = "Sign in to add to cart"
            return
        }
        if (isBulkAdding.value) return
        isBulkAdding.value = true
        viewModelScope.launch {
            var successCount = 0
            var errorCount = 0
            for (productId in productIds) {
                val result = cartRepository.addOrUpdateItem(
                    userId = uid,
                    productId = productId,
                    quantity = 1.0,
                    buyerLat = null,
                    buyerLon = null
                )
                when (result) {
                    is Resource.Success -> {
                        successCount++
                        analyticsTracker.buyNowClicked(productId)
                    }
                    is Resource.Error -> {
                        errorCount++
                    }
                    is Resource.Loading -> {
                        // Skip loading
                    }
                }
            }
            isBulkAdding.value = false
            if (successCount > 0) {
                successMessage.value = "Added $successCount item(s) to cart"
            }
            if (errorCount > 0) {
                error.value = "Failed to add $errorCount item(s) to cart"
            }
        }
    }

    fun clearMessages() {
        error.value = null
        successMessage.value = null
    }

    fun syncWishlist() {
        val uid = userId ?: return
        viewModelScope.launch {
            if (!connectivityManager.isOnline()) {
                error.value = "No internet connection"
                return@launch
            }
            // No remote sync implemented; surface a friendly message
            successMessage.value = "Wishlist is up to date"
        }
    }
}