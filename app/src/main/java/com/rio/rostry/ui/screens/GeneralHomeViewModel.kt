package com.rio.rostry.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
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
        val name = if (userRes is Resource.Success) userRes.data?.fullName else null

        val productsRes = productRepository.getAllProducts().first()
        val products = if (productsRes is Resource.Success) productsRes.data ?: emptyList() else emptyList()

        _uiState.value = UiState(
            userName = name,
            nearbyProductsCount = products.size,
            newListingsToday = products.size,
            recommendedProducts = products.take(10)
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
