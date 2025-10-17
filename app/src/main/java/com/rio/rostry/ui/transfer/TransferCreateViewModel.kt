package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.QuarantineRecordDao
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.marketplace.validation.ProductValidator
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class TransferCreateViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val transferWorkflowRepository: TransferWorkflowRepository,
    private val productValidator: ProductValidator,
    private val quarantineDao: QuarantineRecordDao,
) : ViewModel() {

    enum class TransferType { SALE, GIFT, BREEDING_LOAN, OWNERSHIP_TRANSFER }

    data class UiState(
        val productId: String = "",
        val toUserId: String = "",
        val amount: String = "",
        val currency: String = "USD",
        val type: String = "PAYMENT",
        val notes: String = "",
        val loading: Boolean = false,
        val error: String? = null,
        val successTransferId: String? = null,
        val availableProducts: List<ProductEntity> = emptyList(),
        val searchResults: List<UserEntity> = emptyList(),
        val selectedProduct: ProductEntity? = null,
        val selectedRecipient: UserEntity? = null,
        val transferType: TransferType = TransferType.SALE,
        val validationErrors: Map<String, String> = emptyMap(),
        val showProductPicker: Boolean = false,
        val showRecipientPicker: Boolean = false,
        val confirmationStep: Boolean = false,
        val productStatus: String? = null,
        val validating: Boolean = false,
        val ownershipVerified: Boolean? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun update(field: String, value: String) {
        _state.value = when (field) {
            "productId" -> _state.value.copy(productId = value)
            "+toUserId" -> _state.value.copy(toUserId = value)
            "toUserId" -> _state.value.copy(toUserId = value)
            "amount" -> _state.value.copy(amount = value)
            "currency" -> _state.value.copy(currency = value)
            "type" -> _state.value.copy(type = value)
            "notes" -> _state.value.copy(notes = value)
            else -> _state.value
        }
    }

    init {
        loadUserProducts()
    }

    fun loadUserProducts() {
        val userId = currentUserProvider.userIdOrNull() ?: return
        viewModelScope.launch {
            // Placeholder - load user's products
            // In a real implementation, filter products by userId
            _state.value = _state.value.copy(availableProducts = emptyList())
        }
    }

    fun searchRecipients(query: String) {
        if (query.length < 2) {
            _state.value = _state.value.copy(searchResults = emptyList())
            return
        }
        viewModelScope.launch {
            // Placeholder - search for users
            // In a real implementation, this would query user repository
            _state.value = _state.value.copy(searchResults = emptyList())
        }
    }

    fun selectProduct(productId: String) {
        val product = _state.value.availableProducts.find { it.productId == productId }
        _state.value = _state.value.copy(
            selectedProduct = product,
            amount = product?.price?.toString() ?: "",
            productId = productId,
            showProductPicker = false,
            validating = true,
            error = null
        )
        // Check product eligibility asynchronously
        viewModelScope.launch {
            val (eligible, error) = checkProductEligibility(productId)
            val currentUserId = currentUserProvider.userIdOrNull()
            val selected = _state.value.availableProducts.find { it.productId == productId }
            var status: String? = null
            selected?.let { p ->
                val lifecycle = p.lifecycleStatus?.uppercase()
                status = when (lifecycle) {
                    "QUARANTINE" -> "quarantine"
                    "DECEASED" -> "deceased"
                    "TRANSFERRED" -> "transferred"
                    else -> null
                }
                if (status == null) {
                    // Fall back to quarantine check via validator
                    val inQuarantine = productValidator.checkQuarantineStatus(productId)
                    if (inQuarantine) status = "quarantine"
                }
                if (status == null) {
                    // Use validator to infer outdated farm data for traceable categories
                    val v = productValidator.validateWithTraceability(p)
                    if (!v.valid && v.reasons.any { it.contains("vaccination", true) || it.contains("health", true) || it.contains("growth", true) }) {
                        status = "outdated_farm_data"
                    }
                }
            }
            _state.value = _state.value.copy(
                validating = false,
                error = if (!eligible) error else null,
                productStatus = status,
                ownershipVerified = selected?.sellerId?.let { it == currentUserId }
            )
        }
    }

    /**
     * Checks product eligibility for transfer by validating lifecycle status, quarantine, and farm data freshness.
     */
    suspend fun checkProductEligibility(productId: String): Pair<Boolean, String?> {
        val product = _state.value.availableProducts.find { it.productId == productId }
            ?: return Pair(false, "Product not found")

        val lifecycle = product.lifecycleStatus?.uppercase()
        when (lifecycle) {
            "QUARANTINE" -> return Pair(false, "This product is in quarantine. Complete quarantine protocol in Farm Monitoring before transfer.")
            "DECEASED" -> return Pair(false, "This bird is marked as deceased. Cannot transfer.")
            "TRANSFERRED" -> return Pair(false, "Product already transferred")
        }

        // Check quarantine status
        val inQuarantine = productValidator.checkQuarantineStatus(productId)
        if (inQuarantine) return Pair(false, "This product is in quarantine. Complete quarantine protocol in Farm Monitoring before transfer.")

        // Check farm data freshness
        val validation = productValidator.validateWithTraceability(product)
        if (!validation.valid) {
            return Pair(false, validation.reasons.joinToString("; "))
        }

        return Pair(true, null)
    }

    fun selectRecipient(userId: String) {
        val user = _state.value.searchResults.find { it.userId == userId }
        _state.value = _state.value.copy(
            selectedRecipient = user,
            toUserId = userId,
            showRecipientPicker = false
        )
    }

    fun setTransferType(type: TransferType) {
        _state.value = _state.value.copy(transferType = type)
    }

    fun validateForm(): Map<String, String> {
        val s = _state.value
        return buildMap {
            if (s.selectedProduct == null) put("product", "Select a product")
            if (s.selectedRecipient == null) put("recipient", "Select a recipient")
            if (s.transferType == TransferType.SALE) {
                val amt = s.amount.toDoubleOrNull()
                if (amt == null) put("amount", "Enter a valid number")
                else if (amt <= 0.0) put("amount", "Amount must be greater than 0")
            }
            val senderId = currentUserProvider.userIdOrNull()
            if (!s.toUserId.isNullOrBlank() && senderId == s.toUserId) put("recipient", "Cannot transfer to yourself")

            // Add farm data validation
            if (s.selectedProduct != null) {
                val lifecycle = s.selectedProduct.lifecycleStatus?.uppercase()
                when (lifecycle) {
                    "QUARANTINE" -> put("product", "Cannot transfer products in quarantine")
                    "DECEASED" -> put("product", "Cannot transfer deceased birds")
                    "TRANSFERRED" -> put("product", "Product already transferred")
                }
                if (s.selectedProduct.familyTreeId != null) {
                    put("farmData", "Ensure farm data is up-to-date before transfer")
                }
            }
        }
    }

    fun openProductPicker() {
        _state.value = _state.value.copy(showProductPicker = true)
    }
    
    fun dismissProductPicker() {
        _state.value = _state.value.copy(showProductPicker = false)
    }
    
    fun openRecipientPicker() {
        _state.value = _state.value.copy(showRecipientPicker = true)
    }
    
    fun dismissRecipientPicker() {
        _state.value = _state.value.copy(showRecipientPicker = false)
    }
    
    fun enterConfirmation() {
        _state.value = _state.value.copy(confirmationStep = true)
    }
    
    fun exitConfirmation() {
        _state.value = _state.value.copy(confirmationStep = false)
    }

    fun proceedToConfirmation() {
        val errors = validateForm()
        if (errors.isNotEmpty()) {
            _state.value = _state.value.copy(validationErrors = errors)
            return
        }
        enterConfirmation()
        _state.value = _state.value.copy(validationErrors = emptyMap())
    }

    fun confirmAndSubmit() {
        val errors = validateForm()
        if (errors.isNotEmpty()) {
            _state.value = _state.value.copy(validationErrors = errors, error = "Please fix the highlighted fields")
            return
        }
        create()
    }

    fun create() {
        val s = _state.value
        val fromUserId = currentUserProvider.userIdOrNull()
        if (fromUserId == null) {
            _state.value = s.copy(error = "User not authenticated")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = s.copy(loading = true, error = null, successTransferId = null)

                // Perform comprehensive transfer eligibility validation
                if (s.selectedProduct != null) {
                    val eligibility = transferWorkflowRepository.validateTransferEligibility(s.productId, fromUserId, s.toUserId)
                    if (eligibility is Resource.Error) {
                        _state.value = _state.value.copy(loading = false, error = eligibility.message)
                        return@launch
                    }
                }

                val amt = s.amount.toDoubleOrNull() ?: 0.0
                val id = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val entity = TransferEntity(
                    transferId = id,
                    productId = s.productId.ifBlank { null },
                    fromUserId = fromUserId,
                    toUserId = s.toUserId.ifBlank { null },
                    orderId = null,
                    amount = amt,
                    currency = s.currency.ifBlank { "USD" },
                    type = s.transferType.name,
                    status = "PENDING",
                    transactionReference = null,
                    notes = s.notes.ifBlank { null },
                    initiatedAt = now,
                    updatedAt = now,
                    lastModifiedAt = now,
                    dirty = true
                )
                transferDao.upsert(entity)
                _state.value = _state.value.copy(loading = false, successTransferId = id)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}
