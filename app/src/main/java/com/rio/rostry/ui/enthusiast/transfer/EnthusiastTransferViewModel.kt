package com.rio.rostry.ui.enthusiast.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.TransferEventType
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.TransferUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EnthusiastTransferUiState(
    val product: ProductEntity? = null,
    val selectedRecipient: UserEntity? = null,
    val discoveredUsers: List<UserEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isTransferring: Boolean = false,
    val errorMessage: String? = null,
    val transferSuccess: Boolean = false
)

@HiltViewModel
class EnthusiastTransferViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val transferRepository: TransferRepository,
    private val notificationService: IntelligentNotificationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnthusiastTransferUiState())
    val uiState: StateFlow<EnthusiastTransferUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collectLatest { res ->
                if (res is Resource.Success) {
                    currentUserId = res.data?.userId
                }
            }
        }
    }

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val product = productRepository.getById(productId)
            if (product != null) {
                _uiState.update { it.copy(product = product, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Product not found") }
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.update { it.copy(discoveredUsers = emptyList()) }
                return@launch
            }
            if (currentUserId == null) return@launch

            userRepository.searchUsersForTransfer(query, currentUserId!!).collectLatest { users ->
                _uiState.update { it.copy(discoveredUsers = users) }
            }
        }
    }

    fun selectRecipient(user: UserEntity) {
        _uiState.update { it.copy(selectedRecipient = user) }
    }

    fun initiateTransfer() {
        val product = uiState.value.product ?: return
        val recipient = uiState.value.selectedRecipient ?: return
        val senderId = currentUserId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isTransferring = true, errorMessage = null) }

            try {
                val gson = Gson()
                // Simple snapshot generation for Phase 2 requirement
                val lineageSnapshot = gson.toJson(mapOf(
                    "familyTreeId" to product.familyTreeId,
                    "sire" to product.parentMaleId,
                    "dam" to product.parentFemaleId
                ))
                
                val healthSnapshot = gson.toJson(mapOf(
                    "weightGrams" to product.weightGrams,
                    "condition" to product.condition,
                    "age" to product.ageWeeks
                ))

                val transferCode = TransferUtils.generateSecureCode()

                val transfer = transferRepository.initiateEnthusiastTransfer(
                    productId = product.productId,
                    fromUserId = senderId,
                    toUserId = recipient.userId,
                    lineageSnapshotJson = lineageSnapshot,
                    healthSnapshotJson = healthSnapshot,
                    transferCode = transferCode
                )

                // Lock records on the product to ensure data integrity during transfer
                productRepository.lockRecords(product.productId, System.currentTimeMillis())

                // Send Push Notification
                val message = "You've received an asset transfer proposition for ${product.name}."
                notificationService.notifyTransferEvent(
                    type = TransferEventType.ENTHUSIAST_TRANSFER_PROPOSED,
                    transferId = transfer.transferId,
                    title = "New Asset Transfer",
                    message = message
                )

                _uiState.update { it.copy(isTransferring = false, transferSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isTransferring = false, errorMessage = e.message ?: "Failed to initiate transfer") }
            }
        }
    }
}
