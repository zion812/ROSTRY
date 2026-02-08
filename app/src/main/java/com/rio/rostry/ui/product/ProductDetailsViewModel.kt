package com.rio.rostry.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.ai.RecommendationEngine
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.CartRepository
import com.rio.rostry.data.repository.WishlistRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.rio.rostry.utils.QrUtils
import com.rio.rostry.utils.QrStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import com.rio.rostry.data.repository.TraceabilityRepository

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val recommendationEngine: RecommendationEngine,
    private val currentUserProvider: CurrentUserProvider,
    private val analytics: GeneralAnalyticsTracker,
    @ApplicationContext private val appContext: android.content.Context,
    private val messagingRepository: com.rio.rostry.data.repository.social.MessagingRepository,
    private val auctionRepository: com.rio.rostry.data.repository.AuctionRepository,
    private val userRepository: com.rio.rostry.data.repository.UserRepository,
    private val traceabilityRepository: TraceabilityRepository
) : ViewModel() {

    private val _productId = MutableStateFlow<String?>(null)
    
    private val _product = MutableStateFlow<ProductEntity?>(null)
    private val _similarProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val _frequentlyBoughtTogether = MutableStateFlow<List<ProductEntity>>(emptyList())
    private val _isInWishlist = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _cartMessage = MutableStateFlow<String?>(null)
    private val _auctionId = MutableStateFlow<String?>(null)
    private val _seller = MutableStateFlow<com.rio.rostry.data.database.entity.UserEntity?>(null)
    private val _lineage = MutableStateFlow<com.rio.rostry.data.database.entity.FamilyTreeEntity?>(null)
    private val _qrSaved = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val _navigateToChat = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val _navigateToCart = MutableSharedFlow<Unit>(extraBufferCapacity = 1) // NEW

    val qrSaved: SharedFlow<String> = _qrSaved.asSharedFlow()
    val navigateToChat: SharedFlow<String> = _navigateToChat.asSharedFlow()
    val navigateToCart: SharedFlow<Unit> = _navigateToCart.asSharedFlow() // NEW

    val uiState: StateFlow<ProductDetailsUiState> = combine(
        _product,
        _similarProducts,
        _frequentlyBoughtTogether,
        _isInWishlist,
        _error,
        _isLoading,
        _cartMessage,
        _auctionId,
        _seller,
        _lineage
    ) { values ->
        val product = values[0] as ProductEntity?
        val similar = values[1] as List<ProductEntity>
        val fbt = values[2] as List<ProductEntity>
        val wishlist = values[3] as Boolean
        val error = values[4] as String?
        val loading = values[5] as Boolean
        val cartMsg = values[6] as String?
        val auctionId = values[7] as String?
        val seller = values[8] as com.rio.rostry.data.database.entity.UserEntity?
        val lineage = values[9] as com.rio.rostry.data.database.entity.FamilyTreeEntity?
        
        ProductDetailsUiState(
            product = product,
            similarProducts = similar,
            frequentlyBoughtTogether = fbt,
            isInWishlist = wishlist,
            error = error,
            isLoading = loading,
            cartMessage = cartMsg,
            auctionId = auctionId,
            seller = seller,
            lineage = lineage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductDetailsUiState()
    )

    fun loadProduct(productId: String) {
        if (_productId.value == productId) return
        _productId.value = productId
        _isLoading.value = true

        viewModelScope.launch {
            val resource = productRepository.getAllProducts().first { it !is Resource.Loading }
            when (resource) {
                is Resource.Success -> {
                    val product = resource.data?.firstOrNull { it.productId == productId }
                    _product.value = product
                    _isLoading.value = false
                    
                    if (product != null) {
                        // Track product view
                        analytics.productViewTracked(productId, 0)
                        
                        // Load recommendations
                        loadSimilarProducts(productId)
                        loadFrequentlyBoughtTogether(productId)
                        
                        // Check wishlist status
                        checkWishlistStatus(productId)
                        
                        // Check for active auction
                        checkForAuction(productId)

                        // Load Seller Profile (for Verification Badge)
                        loadSellerProfile(product.sellerId)

                        // Load Lineage (for Traceability)
                        if (product.familyTreeId != null) {
                            loadLineage(product.familyTreeId!!)
                        }
                    } else {
                        _error.value = "Product not found"
                    }
                }
                is Resource.Error -> {
                    _error.value = resource.message
                    _isLoading.value = false
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    private fun loadSimilarProducts(productId: String) {
        viewModelScope.launch {
            try {
                val recommendations = recommendationEngine.similarProducts(productId, limit = 5)
                productRepository.getAllProducts().first().let { resource ->
                    if (resource is Resource.Success) {
                        val productIds = recommendations.map { it.id }.toSet()
                        _similarProducts.value = resource.data?.filter { it.productId in productIds } ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                // Silent fail for recommendations
            }
        }
    }

    private fun loadFrequentlyBoughtTogether(productId: String) {
        viewModelScope.launch {
            try {
                val recommendations = recommendationEngine.frequentlyBoughtTogether(productId, limit = 3)
                productRepository.getAllProducts().first().let { resource ->
                    if (resource is Resource.Success) {
                        val productIds = recommendations.map { it.id }.toSet()
                        _frequentlyBoughtTogether.value = resource.data?.filter { it.productId in productIds } ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                // Silent fail for recommendations
            }
        }
    }

    private fun checkForAuction(productId: String) {
        viewModelScope.launch {
            val result = auctionRepository.getAuctionByProductId(productId)
            if (result is Resource.Success && result.data != null) {
                _auctionId.value = result.data.auctionId
            } else {
                _auctionId.value = null
            }
        }
    }

    private fun checkWishlistStatus(productId: String) {
        val userId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            wishlistRepository.observe(userId).collect { wishlistItems ->
                _isInWishlist.value = wishlistItems.any { it.productId == productId }
            }
        }
    }

    fun toggleWishlist() {
        val product = _product.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            _error.value = "Please sign in to save items"
            return
        }

        viewModelScope.launch {
            val result = if (_isInWishlist.value) {
                wishlistRepository.remove(userId, product.productId)
            } else {
                wishlistRepository.add(userId, product.productId)
            }

            when (result) {
                is Resource.Error -> _error.value = result.message
                is Resource.Success -> {
                    analytics.wishlistToggled(product.productId, !_isInWishlist.value)
                    _cartMessage.value = if (_isInWishlist.value) "Removed from wishlist" else "Added to wishlist"
                }
                else -> {}
            }
        }
    }

    fun addToCart(quantity: Double) {
        val product = _product.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            _error.value = "Please sign in to add items to cart"
            return
        }

        viewModelScope.launch {
            val result = cartRepository.addOrUpdateItem(
                userId = userId,
                productId = product.productId,
                quantity = quantity,
                buyerLat = null,
                buyerLon = null
            )
            when (result) {
                is Resource.Success -> {
                    _cartMessage.value = "Added to cart"
                    analytics.productViewTracked(product.productId, 0)
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                else -> {}
            }
        }
    }

    fun buyNow(quantity: Double) {
        val product = _product.value ?: return
        analytics.buyNowClicked(product.productId)
        addToCart(quantity)
        // In a real app, navigate to checkout here
        _cartMessage.value = "Proceeding to checkout..."
        _navigateToCart.tryEmit(Unit) // NEW
    }

    fun startChatWithSeller() {
        val product = _product.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            _error.value = "Please sign in to chat"
            return
        }
        val sellerId = product.sellerId
        if (userId == sellerId) {
            _error.value = "You cannot chat with yourself"
            return
        }

        viewModelScope.launch {
            try {
                // Check for existing thread
                val threads = messagingRepository.streamUserThreadsWithMetadata(userId).first()
                val existingThread = threads.find { thread ->
                    val participants = thread.metadata?.participantIds ?: emptyList()
                    participants.contains(userId) && participants.contains(sellerId) && 
                    thread.metadata?.context?.type == "PRODUCT_INQUIRY" &&
                    thread.metadata?.context?.relatedEntityId == product.productId
                }

                if (existingThread != null) {
                    _navigateToChat.emit(existingThread.threadId)
                } else {
                    // Create new thread
                    val context = com.rio.rostry.data.repository.social.MessagingRepository.ThreadContext(
                        type = "PRODUCT_INQUIRY",
                        relatedEntityId = product.productId,
                        topic = "Inquiry about ${product.name}"
                    )
                    val threadId = messagingRepository.createThreadWithContext(userId, sellerId, context)
                    _navigateToChat.emit(threadId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to start chat: ${e.message}"
            }
        }
    }

    fun generateAndStoreProductQr() {
        val product = _product.value ?: return
        viewModelScope.launch {
            try {
                val bmp = QrUtils.productQrBitmap(product.productId, product.birdCode, size = 1024)
                val uri = QrStorage.saveProductQr(appContext, product.productId, bmp)
                if (uri != null) {
                    val now = System.currentTimeMillis()
                    val result = productRepository.updateQrCodeUrl(product.productId, uri.toString(), now)
                    if (result is Resource.Success) {
                        _cartMessage.value = "Product QR saved"
                        _qrSaved.tryEmit(uri.toString())
                    } else {
                        _error.value = result.message ?: "Failed to update QR code in database"
                    }
                } else {
                    _error.value = "Failed to save QR"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to generate QR"
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _cartMessage.value = null
    }

    fun submitOffer(price: Double, quantity: Double) {
        val product = _product.value ?: return
        val userId = currentUserProvider.userIdOrNull() ?: run {
            _error.value = "Please sign in to make an offer"
            return
        }
        val sellerId = product.sellerId
        if (userId == sellerId) {
            _error.value = "You cannot make an offer to yourself"
            return
        }

        viewModelScope.launch {
            try {
                // Check for existing thread
                val threads = messagingRepository.streamUserThreadsWithMetadata(userId).first()
                val existingThread = threads.find { thread ->
                    val participants = thread.metadata?.participantIds ?: emptyList()
                    participants.contains(userId) && participants.contains(sellerId) && 
                    thread.metadata?.context?.type == "PRODUCT_INQUIRY" &&
                    thread.metadata?.context?.relatedEntityId == product.productId
                }

                val threadId = if (existingThread != null) {
                    existingThread.threadId
                } else {
                    // Create new thread
                    val context = com.rio.rostry.data.repository.social.MessagingRepository.ThreadContext(
                        type = "PRODUCT_INQUIRY",
                        relatedEntityId = product.productId,
                        topic = "Inquiry about ${product.name}"
                    )
                    messagingRepository.createThreadWithContext(userId, sellerId, context)
                }
                
                messagingRepository.sendOffer(threadId, userId, sellerId, price, quantity, product.unit)
                _cartMessage.value = "Offer sent!"
                _navigateToChat.emit(threadId)
            } catch (e: Exception) {
                _error.value = "Failed to send offer: ${e.message}"
            }
        }
    }

    private fun loadSellerProfile(sellerId: String) {
        viewModelScope.launch {
            val result = userRepository.getUserById(sellerId).first()
            if (result is Resource.Success) {
                _seller.value = result.data
            }
        }
    }

    private fun loadLineage(familyTreeId: String) {
        viewModelScope.launch {
            val result = traceabilityRepository.getFamilyTree(familyTreeId)
            if (result is Resource.Success) {
                _lineage.value = result.data
            }
        }
    }

    data class ProductDetailsUiState(
        val product: ProductEntity? = null,
        val similarProducts: List<ProductEntity> = emptyList(),
        val frequentlyBoughtTogether: List<ProductEntity> = emptyList(),
        val isInWishlist: Boolean = false,
        val error: String? = null,
        val isLoading: Boolean = true,
        val cartMessage: String? = null,
        val auctionId: String? = null,
        val seller: com.rio.rostry.data.database.entity.UserEntity? = null,
        val lineage: com.rio.rostry.data.database.entity.FamilyTreeEntity? = null
    )
}