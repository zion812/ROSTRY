package com.rio.rostry.ui.enthusiast.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.notifications.IntelligentNotificationService
import com.rio.rostry.notifications.TransferEventType
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.UUID
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransferResponseUiState(
    val transferEntity: TransferEntity? = null,
    val productEntity: ProductEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputCode: String = "",
    val codeError: String? = null,
    val isProcessing: Boolean = false,
    val isActionComplete: Boolean = false
)

@HiltViewModel
class TransferResponseViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val notificationService: IntelligentNotificationService,
    private val auditLogDao: AuditLogDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferResponseUiState())
    val uiState: StateFlow<TransferResponseUiState> = _uiState.asStateFlow()
    
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

    fun loadTransfer(transferId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            transferRepository.getById(transferId).collectLatest { transfer ->
                if (transfer != null) {
                    val product = transfer.productId?.let { productRepository.getById(it) }
                    _uiState.update { 
                        it.copy(
                            transferEntity = transfer,
                            productEntity = product,
                            isLoading = false
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Transfer not found.") }
                }
            }
        }
    }

    fun updateInputCode(code: String) {
        if (code.length <= 6 && code.all { it.isDigit() }) {
            _uiState.update { it.copy(inputCode = code, codeError = null) }
        }
    }

    fun acceptTransfer() {
        val transfer = uiState.value.transferEntity ?: return
        val product = uiState.value.productEntity ?: return
        val currentCode = uiState.value.inputCode
        val recipientId = currentUserId ?: return

        // 1. Verify we are the intended recipient
        if (transfer.toUserId != recipientId) {
            _uiState.update { it.copy(errorMessage = "You are not the intended recipient of this transfer.", codeError = null) }
            return
        }

        // 2. Timeout check
        val now = System.currentTimeMillis()
        val expiresAt = transfer.transferCodeExpiresAt ?: 0L
        if (now > expiresAt) {
            _uiState.update { it.copy(codeError = "Transfer code has expired. Please request a new one.") }
            handleTimeout(transfer)
            return
        }

        // 3. Verify Code
        if (currentCode != transfer.transferCode) {
            _uiState.update { it.copy(codeError = "Invalid security code. Please check with the sender.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, codeError = null, errorMessage = null) }
            try {
                // Execute Ownership Swap
                val transferRes = productRepository.transferOwnership(product.productId, recipientId)
                if (transferRes is Resource.Success) {
                    
                    // Mark transfer as Complete
                    transferRepository.upsert(transfer.copy(
                        status = "COMPLETED",
                        completedAt = System.currentTimeMillis(),
                        claimedAt = System.currentTimeMillis(),
                        dirty = true
                    ))

                    // Log Audit Event
                    auditLogDao.insert(
                        AuditLogEntity(
                            logId = UUID.randomUUID().toString(),
                            type = "TRANSFER",
                            refId = transfer.transferId,
                            action = "COMPLETE_ENTHUSIAST_TRANSFER",
                            actorUserId = recipientId,
                            detailsJson = "{\"productId\":\"${product.productId}\",\"fromUserId\":\"${transfer.fromUserId}\"}",
                            createdAt = System.currentTimeMillis()
                        )
                    )

                    // Notify Sender of success
                    notificationService.notifyTransferEvent(
                        type = TransferEventType.COMPLETED,
                        transferId = transfer.transferId,
                        title = "Transfer Complete",
                        message = "${product.name} has been successfully claimed!"
                    )

                    _uiState.update { it.copy(isProcessing = false, isActionComplete = true) }
                } else {
                    _uiState.update { it.copy(isProcessing = false, errorMessage = transferRes.message ?: "Failed to swap ownership") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false, errorMessage = e.message ?: "An unexpected error occurred.") }
            }
        }
    }

    fun denyTransfer() {
        val transfer = uiState.value.transferEntity ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            
            try {
                transferRepository.upsert(transfer.copy(
                    status = "CANCELLED",
                    dirty = true
                ))

                // Log Audit Event
                currentUserId?.let { userId ->
                    auditLogDao.insert(
                        AuditLogEntity(
                            logId = UUID.randomUUID().toString(),
                            type = "TRANSFER",
                            refId = transfer.transferId,
                            action = "DENY_ENTHUSIAST_TRANSFER",
                            actorUserId = userId,
                            detailsJson = null,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                }

                
                // Notify Sender of denial
                notificationService.notifyTransferEvent(
                    type = TransferEventType.CANCELLED,
                    transferId = transfer.transferId,
                    title = "Transfer Denied",
                    message = "The transfer request was declined by the recipient."
                )

                _uiState.update { it.copy(isProcessing = false, isActionComplete = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isProcessing = false, errorMessage = e.message ?: "Failed to deny transfer") }
            }
        }
    }

    private fun handleTimeout(transfer: TransferEntity) {
        viewModelScope.launch {
            transferRepository.upsert(transfer.copy(
                status = "TIMED_OUT",
                dirty = true
            ))
            
            notificationService.notifyTransferEvent(
                type = TransferEventType.TIMED_OUT,
                transferId = transfer.transferId,
                title = "Transfer Expired",
                message = "The 15-minute transfer window has expired."
            )
        }
    }
}
