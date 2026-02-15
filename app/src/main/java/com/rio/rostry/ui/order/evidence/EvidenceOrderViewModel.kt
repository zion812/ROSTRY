package com.rio.rostry.ui.order.evidence

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.OrderPaymentDao
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.data.repository.EvidenceOrderRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.*
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Evidence-Based Order System.
 * Handles quote creation, negotiation, payments, and delivery confirmation.
 */
@HiltViewModel
class EvidenceOrderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val evidenceOrderRepository: EvidenceOrderRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val paymentDao: OrderPaymentDao
) : ViewModel() {

    // Current user
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    // UI State
    private val _uiState = MutableStateFlow(EvidenceOrderUiState())
    val uiState: StateFlow<EvidenceOrderUiState> = _uiState.asStateFlow()

    // Active quotes for buyer/seller
    private val _buyerQuotes = MutableStateFlow<List<OrderQuoteEntity>>(emptyList())
    val buyerQuotes: StateFlow<List<OrderQuoteEntity>> = _buyerQuotes.asStateFlow()

    private val _sellerQuotes = MutableStateFlow<List<OrderQuoteEntity>>(emptyList())
    val sellerQuotes: StateFlow<List<OrderQuoteEntity>> = _sellerQuotes.asStateFlow()

    // Pending payments for buyer
    private val _pendingPayments = MutableStateFlow<List<OrderPaymentEntity>>(emptyList())
    val pendingPayments: StateFlow<List<OrderPaymentEntity>> = _pendingPayments.asStateFlow()

    // Payments awaiting verification for seller
    private val _paymentsToVerify = MutableStateFlow<List<OrderPaymentEntity>>(emptyList())
    val paymentsToVerify: StateFlow<List<OrderPaymentEntity>> = _paymentsToVerify.asStateFlow()

    // Current order's evidence
    private val _orderEvidence = MutableStateFlow<List<OrderEvidenceEntity>>(emptyList())
    val orderEvidence: StateFlow<List<OrderEvidenceEntity>> = _orderEvidence.asStateFlow()

    // Current order's audit trail
    private val _auditTrail = MutableStateFlow<List<OrderAuditLogEntity>>(emptyList())
    val auditTrail: StateFlow<List<OrderAuditLogEntity>> = _auditTrail.asStateFlow()

    // Active disputes
    private val _disputes = MutableStateFlow<List<OrderDisputeEntity>>(emptyList())
    val disputes: StateFlow<List<OrderDisputeEntity>> = _disputes.asStateFlow()

    // Loading & Error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val user = resource.data
                        _currentUserId.value = user?.userId
                        user?.userId?.let { userId ->
                            loadBuyerQuotes(userId)
                            loadSellerQuotes(userId)
                            loadUserDisputes(userId)
                        }
                    }
                    is Resource.Error -> {
                        _error.value = resource.message
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun loadBuyerQuotes(buyerId: String) {
        viewModelScope.launch {
            evidenceOrderRepository.getBuyerActiveQuotes(buyerId).collect {
                _buyerQuotes.value = it
            }
        }
    }

    private fun loadSellerQuotes(sellerId: String) {
        viewModelScope.launch {
            evidenceOrderRepository.getSellerActiveQuotes(sellerId).collect {
                _sellerQuotes.value = it
            }
        }
        viewModelScope.launch {
            evidenceOrderRepository.getPaymentsAwaitingVerification(sellerId).collect {
                _paymentsToVerify.value = it
            }
        }
    }

    private fun loadUserDisputes(userId: String) {
        viewModelScope.launch {
            evidenceOrderRepository.getUserActiveDisputes(userId).collect {
                _disputes.value = it
            }
        }
    }

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            evidenceOrderRepository.getOrderEvidence(orderId).collect {
                _orderEvidence.value = it
            }
        }
        viewModelScope.launch {
            evidenceOrderRepository.getOrderAuditTrail(orderId).collect {
                _auditTrail.value = it
            }
        }
    }

    // ==================== ENQUIRY CREATION ====================

    fun updateEnquiryForm(form: EnquiryFormState) {
        _uiState.update { it.copy(enquiryForm = form) }
    }

    fun createEnquiry(
        sellerId: String,
        productId: String,
        productName: String
    ) {
        val form = _uiState.value.enquiryForm
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = evidenceOrderRepository.createEnquiry(
                buyerId = userId,
                sellerId = sellerId,
                productId = productId,
                productName = productName,
                quantity = form.quantity,
                unit = form.unit,
                deliveryType = form.deliveryType,
                deliveryAddress = form.deliveryAddress,
                deliveryLatitude = form.deliveryLatitude,
                deliveryLongitude = form.deliveryLongitude,
                paymentPreference = form.paymentPreference,
                buyerNotes = form.notes
            )

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Enquiry sent successfully!"
                    _uiState.update { it.copy(
                        enquiryForm = EnquiryFormState(),
                        currentQuote = result.data
                    )}
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== QUOTE MANAGEMENT ====================

    fun updateQuoteForm(form: QuoteFormState) {
        _uiState.update { it.copy(quoteForm = form) }
    }

    fun sendQuote(quoteId: String) {
        val form = _uiState.value.quoteForm

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = evidenceOrderRepository.sendQuote(
                quoteId = quoteId,
                basePrice = form.basePrice,
                deliveryCharge = form.deliveryCharge,
                packingCharge = form.packingCharge,
                allowedPaymentTypes = form.allowedPaymentTypes,
                sellerNotes = form.notes,
                expiresInHours = 24
            )

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Quote sent to buyer!"
                    _uiState.update { it.copy(currentQuote = result.data) }
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun agreeToQuote(quoteId: String, isBuyer: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = if (isBuyer) {
                evidenceOrderRepository.buyerAgreeToQuote(quoteId)
            } else {
                evidenceOrderRepository.sellerAgreeToQuote(quoteId)
            }

            when (result) {
                is Resource.Success -> {
                    val quote = result.data
                    if (quote?.status == "LOCKED") {
                        _successMessage.value = "Agreement locked! Price is now final."
                    } else {
                        _successMessage.value = "You agreed to the quote. Waiting for other party."
                    }
                    _uiState.update { it.copy(currentQuote = result.data) }
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun counterOffer(originalQuoteId: String, newPrice: Double?, newDeliveryCharge: Double?, notes: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = evidenceOrderRepository.counterOffer(
                originalQuoteId = originalQuoteId,
                newPrice = newPrice,
                newDeliveryCharge = newDeliveryCharge,
                notes = notes
            )

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Counter offer sent!"
                    _uiState.update { it.copy(currentQuote = result.data) }
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== PAYMENT ====================

    fun submitPaymentProof(
        paymentId: String,
        imageUri: String,
        transactionRef: String?
    ) {
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // First upload the evidence
            val evidenceResult = evidenceOrderRepository.uploadEvidence(
                orderId = _uiState.value.currentQuote?.orderId ?: "",
                evidenceType = EvidenceType.PAYMENT_SCREENSHOT,
                uploadedBy = userId,
                uploadedByRole = "BUYER",
                imageUri = imageUri,
                videoUri = null,
                textContent = transactionRef,
                geoLatitude = null,
                geoLongitude = null
            )

            when (evidenceResult) {
                is Resource.Success -> {
                    val evidence = evidenceResult.data!!
                    val proofResult = evidenceOrderRepository.submitPaymentProof(
                        paymentId = paymentId,
                        evidenceId = evidence.evidenceId,
                        transactionRef = transactionRef
                    )

                    when (proofResult) {
                        is Resource.Success -> {
                            _successMessage.value = "Payment proof submitted! Waiting for seller verification."
                        }
                        is Resource.Error -> {
                            _error.value = proofResult.message
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _error.value = evidenceResult.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun verifyPayment(paymentId: String, approve: Boolean, rejectionReason: String? = null) {
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = if (approve) {
                evidenceOrderRepository.verifyPayment(paymentId, userId, null)
            } else {
                evidenceOrderRepository.rejectPayment(paymentId, rejectionReason ?: "Payment not received")
            }

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = if (approve) "Payment verified!" else "Payment rejected."
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== DELIVERY ====================

    fun generateDeliveryOtp(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = evidenceOrderRepository.generateDeliveryOtp(orderId)

            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(deliveryOtp = result.data) }
                    _successMessage.value = "OTP generated! Share this with the seller at delivery."
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun verifyDeliveryOtp(orderId: String, otp: String, lat: Double? = null, lng: Double? = null) {
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = evidenceOrderRepository.verifyDeliveryOtp(
                orderId = orderId,
                otp = otp,
                confirmedBy = userId,
                verifierLat = lat,
                verifierLng = lng
            )

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Delivery confirmed via OTP!"
                    _uiState.update { it.copy(deliveryOtp = null) }
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun confirmDeliveryWithPhoto(orderId: String, deliveryPhotoUri: String, buyerPhotoUri: String?) {
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Upload delivery photo
            val deliveryPhotoResult = evidenceOrderRepository.uploadEvidence(
                orderId = orderId,
                evidenceType = EvidenceType.DELIVERY_PHOTO,
                uploadedBy = userId,
                uploadedByRole = "SELLER",
                imageUri = deliveryPhotoUri,
                videoUri = null,
                textContent = null,
                geoLatitude = null,
                geoLongitude = null
            )

            when (deliveryPhotoResult) {
                is Resource.Success -> {
                    var buyerPhotoId: String? = null
                    
                    // Upload buyer photo if provided
                    if (buyerPhotoUri != null) {
                        val buyerPhotoResult = evidenceOrderRepository.uploadEvidence(
                            orderId = orderId,
                            evidenceType = EvidenceType.BUYER_CONFIRMATION_PHOTO,
                            uploadedBy = userId,
                            uploadedByRole = "BUYER",
                            imageUri = buyerPhotoUri,
                            videoUri = null,
                            textContent = null,
                            geoLatitude = null,
                            geoLongitude = null
                        )
                        if (buyerPhotoResult is Resource.Success) {
                            buyerPhotoId = buyerPhotoResult.data?.evidenceId
                        }
                    }

                    val confirmResult = evidenceOrderRepository.confirmDeliveryWithPhoto(
                        orderId = orderId,
                        deliveryPhotoId = deliveryPhotoResult.data!!.evidenceId,
                        buyerPhotoId = buyerPhotoId,
                        confirmedBy = userId
                    )

                    when (confirmResult) {
                        is Resource.Success -> {
                            _successMessage.value = "Delivery confirmed with photo!"
                        }
                        is Resource.Error -> {
                            _error.value = confirmResult.message
                        }
                        is Resource.Loading -> {}
                    }
                }
                is Resource.Error -> {
                    _error.value = deliveryPhotoResult.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun markBalanceCollected(orderId: String, cashReceiptUri: String?) {
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            var evidenceId: String? = null
            
            if (cashReceiptUri != null) {
                val receiptResult = evidenceOrderRepository.uploadEvidence(
                    orderId = orderId,
                    evidenceType = EvidenceType.CASH_RECEIPT,
                    uploadedBy = userId,
                    uploadedByRole = "SELLER",
                    imageUri = cashReceiptUri,
                    videoUri = null,
                    textContent = null,
                    geoLatitude = null,
                    geoLongitude = null
                )
                if (receiptResult is Resource.Success) {
                    evidenceId = receiptResult.data?.evidenceId
                }
            }

            val result = evidenceOrderRepository.markBalanceCollected(orderId, evidenceId)

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Balance collected! Order completed."
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== DISPUTES ====================

    fun raiseDispute(
        orderId: String,
        reason: DisputeReason,
        description: String,
        requestedResolution: String?,
        claimedAmount: Double?
    ) {
        val userId = _currentUserId.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = evidenceOrderRepository.raiseDispute(
                orderId = orderId,
                raisedBy = userId,
                raisedByRole = "BUYER", // Will be determined by checking order
                reason = reason,
                description = description,
                requestedResolution = requestedResolution,
                claimedAmount = claimedAmount,
                evidenceIds = null
            )

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Dispute raised successfully."
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== REVIEWS ====================

    fun submitReview(
        orderId: String,
        overallRating: Int,
        qualityRating: Int,
        deliveryRating: Int,
        communicationRating: Int,
        reviewText: String,
        wouldRecommend: Boolean?
    ) {
        val userId = _currentUserId.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            // Format content to include detailed ratings
            val detailedContent = StringBuilder().apply {
                if (reviewText.isNotBlank()) append(reviewText).append("\n\n")
                append("Quality: $qualityRating/5\n")
                append("Delivery: $deliveryRating/5\n")
                append("Communication: $communicationRating/5")
            }.toString()

            val result = evidenceOrderRepository.submitReview(
                orderId = orderId,
                reviewerId = userId,
                rating = overallRating,
                content = detailedContent,
                wouldRecommend = wouldRecommend
            )

            when (result) {
                is Resource.Success -> {
                    _successMessage.value = "Review submitted successfully!"
                }
                is Resource.Error -> {
                    _error.value = result.message
                }
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    // ==================== UTILS ====================

    fun loadPayment(paymentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val payment = paymentDao.findById(paymentId)
                _uiState.update { it.copy(currentPayment = payment) }
            } catch (e: Exception) {
                _error.value = "Could not load payment details"
            }
            _isLoading.value = false
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }

    fun setCurrentQuote(quote: OrderQuoteEntity?) {
        _uiState.update { it.copy(currentQuote = quote) }
    }
}

// ==================== UI STATE ====================

data class EvidenceOrderUiState(
    val enquiryForm: EnquiryFormState = EnquiryFormState(),
    val quoteForm: QuoteFormState = QuoteFormState(),
    val currentQuote: OrderQuoteEntity? = null,
    val currentPayment: OrderPaymentEntity? = null,
    val deliveryOtp: String? = null
)

data class EnquiryFormState(
    val quantity: Double = 1.0,
    val unit: String = "BIRDS",
    val deliveryType: OrderDeliveryType = OrderDeliveryType.SELLER_DELIVERY,
    val deliveryAddress: String? = null,
    val deliveryLatitude: Double? = null,
    val deliveryLongitude: Double? = null,
    val paymentPreference: OrderPaymentType = OrderPaymentType.COD,
    val notes: String? = null
)

data class QuoteFormState(
    val basePrice: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val packingCharge: Double = 0.0,
    val allowedPaymentTypes: List<OrderPaymentType> = listOf(
        OrderPaymentType.COD,
        OrderPaymentType.FULL_ADVANCE,
        OrderPaymentType.SPLIT_50_50
    ),
    val notes: String? = null
)
