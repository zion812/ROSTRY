package com.rio.rostry.feature.general.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.commerce.repository.ProductRepository
import com.rio.rostry.domain.account.repository.UserRepository
import com.rio.rostry.core.common.analytics.FlowAnalyticsTracker

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class GeneralHomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val flowAnalyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    data class UiState(
        val userName: String? = null,
        val nearbyProductsCount: Int = 0,
        val newListingsToday: Int = 0,
        val recommendedProducts: List<ProductEntity> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadHomeData()
        }
    }

    private suspend fun loadHomeData() {
        val userRes = userRepository.getCurrentUser().first()
        val user = userRes.data
        val name = user?.fullName
        
        // Mock user location for 'Nearby' if not set (Center of Bangalore for demo)
        val userLat = user?.farmLocationLat ?: 12.9716
        val userLng = user?.farmLocationLng ?: 77.5946

        val productsRes = productRepository.getAllProducts().first()
        val products = if (productsRes is Resource.Success) productsRes.data ?: emptyList() else emptyList()

        // 1. New Listings Today
        // Use centralized TimeUtils for consistency
        val newToday = products.count { com.rio.rostry.utils.TimeUtils.isRecent(it.createdAt, 1L) }

        // 2. Nearby (within 50km)
        val nearbyCount = products.count { product ->
            val lat = product.latitude; val lng = product.longitude
            if (lat != null && lng != null) {
                com.rio.rostry.utils.LocationUtils.calculateDistance(userLat, userLng, lat, lng) <= 50.0
            } else false
        }

        // 3. Recommended: Sort by distance, then freshness
        val recommended = products.sortedBy { product ->
            val lat = product.latitude; val lng = product.longitude
            if (lat != null && lng != null) {
                com.rio.rostry.utils.LocationUtils.calculateDistance(userLat, userLng, lat, lng)
            } else Double.MAX_VALUE // Push unknown locations to end
        }.take(10)

        _uiState.value = UiState(
            userName = name,
            nearbyProductsCount = nearbyCount,
            newListingsToday = newToday,
            recommendedProducts = recommended
        )
    }

    fun onBrowseMarketplaceClick() {
        flowAnalyticsTracker.trackEvent("browse_marketplace_clicked")
    }

    fun onSearchProductsClick() {
        flowAnalyticsTracker.trackEvent("search_products_clicked")
    }

    fun onViewWishlistClick() {
        flowAnalyticsTracker.trackEvent("view_wishlist_clicked")
    }

    fun onMyOrdersClick() {
        flowAnalyticsTracker.trackEvent("my_orders_clicked")
    }

    fun onProductClicked(productId: String) {
        flowAnalyticsTracker.trackEvent("product_clicked", mapOf("product_id" to productId))
    }

    fun onUpgradeToFarmerClick() {
        flowAnalyticsTracker.trackEvent("upgrade_to_farmer_clicked")
    }

    fun onUpgradeToEnthusiastClick() {
        flowAnalyticsTracker.trackEvent("upgrade_to_enthusiast_clicked")
    }
}
