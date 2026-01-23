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
import com.rio.rostry.ui.components.SyncState
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.ui.components.ConflictDetails
import com.rio.rostry.data.database.entity.OutboxEntity

@HiltViewModel
class TransferCreateViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val transferWorkflowRepository: TransferWorkflowRepository,
    private val productValidator: ProductValidator,
    private val quarantineDao: QuarantineRecordDao,
    private val connectivityManager: ConnectivityManager,
    private val syncManager: SyncManager,
    private val biosecurityRepository: com.rio.rostry.data.repository.BiosecurityRepository

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
        val ownershipVerified: Boolean? = null,
        val transferSyncState: SyncState? = null,
        val pendingSyncCount: Int = 0,
        val isOnline: Boolean = true,
        val failedTransferId: String? = null,
        val conflictDetails: ConflictDetails? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val onlineFlow: StateFlow<Boolean> = connectivityManager
        .observe()
        .map { it.isOnline }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

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
        val userId = currentUserProvider.userIdOrNull()
        if (userId != null) {
            viewModelScope.launch {
                transferDao.observeDirtyByUser(userId).collect { dirtyTransfers ->
                    _state.value = _state.value.copy(pendingSyncCount = dirtyTransfers.size)
                }
            }
        }
        viewModelScope.launch {
            onlineFlow.collect { online ->
                _state.value = _state.value.copy(isOnline = online)
            }
        }
        // Observe conflict events for transfers
        viewModelScope.launch {
            syncManager.conflictEvents.collect { ev ->
                if (ev.entityType == OutboxEntity.TYPE_TRANSFER) {
                    _state.value = _state.value.copy(
                        conflictDetails = ConflictDetails(
                            entityType = "transfer",
                            entityId = ev.entityId,
                            conflictFields = ev.conflictFields,
                            mergedAt = ev.mergedAt,
                            message = "Transfer was updated remotely. Your local changes were merged."
                        )
                    )
                }
            }
        }
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
                ownershipVerified = selected?.sellerId?.let { ownerId ->
                    // Use centralized RBAC check to allow Admins to manage transfers for others
                    // We need to fetch the full user profile to check the role
                    // Note: In a real app we might want to cache the current user role in the ViewModel
                    // but since verify is a one-time action, fetching here is acceptable.
                    // For now, assume currentUserId check is the baseline, and we optimistically allow if we can't fetch role quickly?
                    // No, better to be safe. We are in a coroutine.
                    // But we don't have easy access to UserType here without a call.
                    // Let's assume we can rely on standard ownership for immediate UI feedback, 
                    // AND checking admin status if it fails?
                    
                    // Actually, let's fetch the user. We are in viewModelScope.
                    // Using currentUserProvider gives ID. UserRepository has getCurrentUserSuspend.
                   
                   // Blocking fetch for role check
                   // Ideally retrieve this once in init or use a Flow, but for this specific action:
                   val currentUser = userRepository.getCurrentUserSuspend()
                   com.rio.rostry.domain.rbac.Rbac.canManageResource(
                       currentUser?.role,
                       currentUser?.userId,
                       ownerId
                   )
                } ?: false
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
            val id = UUID.randomUUID().toString()
            var localPersisted = false
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

                // Biosecurity Check
                val currentUser = userRepository.getCurrentUserSuspend()
                if (currentUser?.farmLocationLat != null && currentUser.farmLocationLng != null) {
                    val status = biosecurityRepository.checkLocation(currentUser.farmLocationLat, currentUser.farmLocationLng)
                    if (status is com.rio.rostry.data.repository.BiosecurityStatus.Blocked) {
                        val zoneNames = status.zones.joinToString { it.reason }
                        _state.value = s.copy(loading = false, error = "Transfer Blocked: You are in a Red Zone ($zoneNames)")
                        return@launch
                    }
                }

                val amt = s.amount.toDoubleOrNull() ?: 0.0
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
                localPersisted = true
                _state.value = _state.value.copy(
                    loading = false,
                    successTransferId = id,
                    transferSyncState = SyncState.PENDING
                )
                if (!connectivityManager.isOnline()) {
                    _state.value = _state.value.copy(error = "Transfer queued. Will sync when online.")
                } else {
                    _state.value = _state.value.copy(error = "Transfer initiated successfully")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message, failedTransferId = if (localPersisted) id else null)
            }
        }
    }

    fun retryFailedTransfer(transferId: String) {
        viewModelScope.launch {
            val result = transferWorkflowRepository.retryFailedTransfer(transferId)
            if (result is Resource.Success) {
                _state.value = _state.value.copy(error = null, failedTransferId = null)
            } else {
                _state.value = _state.value.copy(error = result.message)
            }
        }
    }

    fun dismissConflict() {
        _state.value = _state.value.copy(conflictDetails = null)
    }

    fun viewConflictDetails(entityId: String) {
        // Placeholder: In a real app, navigate to a details screen or show a dialog
        _state.value = _state.value.copy(conflictDetails = null)
    }
}
