package com.rio.rostry.ui.enthusiast.transfer

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.domain.transfer.OwnershipTransferUseCase
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for TransferCodeScreen.
 * Initiates transfer and displays the generated code.
 */
@HiltViewModel
class TransferCodeViewModel @Inject constructor(
    private val transferUseCase: OwnershipTransferUseCase,
    private val productDao: ProductDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val productId: String = savedStateHandle["productId"] ?: ""
    
    private val _uiState = MutableStateFlow<TransferCodeUiState>(TransferCodeUiState.Loading)
    val uiState: StateFlow<TransferCodeUiState> = _uiState.asStateFlow()
    
    private var currentTransferId: String? = null
    
    init {
        if (productId.isNotBlank()) {
            initiateTransfer()
        } else {
            _uiState.value = TransferCodeUiState.Error("No bird selected")
        }
    }
    
    private fun initiateTransfer() {
        viewModelScope.launch {
            _uiState.value = TransferCodeUiState.Loading
            
            try {
                // Get bird details
                val bird = productDao.findById(productId)
                if (bird == null) {
                    _uiState.value = TransferCodeUiState.Error("Bird not found")
                    return@launch
                }
                
                // Initiate transfer
                when (val result = transferUseCase.initiateTransfer(productId, bird.sellerId)) {
                    is Resource.Success -> {
                        val data = result.data!!
                        currentTransferId = data.transferId
                        _uiState.value = TransferCodeUiState.Success(
                            transferId = data.transferId,
                            transferCode = data.transferCode,
                            birdName = data.birdName,
                            birdImageUrl = bird.imageUrls.firstOrNull(),
                            expiresAt = data.expiresAt
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = TransferCodeUiState.Error(result.message ?: "Failed to create transfer")
                    }
                    is Resource.Loading -> {
                        // Keep loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = TransferCodeUiState.Error(e.message ?: "An error occurred")
            }
        }
    }
    
    fun cancelTransfer() {
        viewModelScope.launch {
            val transferId = currentTransferId ?: return@launch
            val bird = productDao.findById(productId) ?: return@launch
            
            when (val result = transferUseCase.cancelTransfer(transferId, bird.sellerId)) {
                is Resource.Success -> {
                    _uiState.value = TransferCodeUiState.Cancelled
                }
                is Resource.Error -> {
                    // Show error but keep current state
                }
                is Resource.Loading -> {}
            }
        }
    }
    
    fun shareCode(context: Context) {
        val state = _uiState.value as? TransferCodeUiState.Success ?: return
        
        val shareText = """
            🐔 ROSTRY Bird Transfer
            
            Bird: ${state.birdName}
            Transfer Code: ${state.transferCode}
            
            Open ROSTRY app → Claim Transfer → Enter this code
        """.trimIndent()
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Transfer Code"))
    }
    
    fun showCopiedToast() {
        // In a real app, you'd use a SnackbarHostState or similar
    }
}

sealed class TransferCodeUiState {
    data object Loading : TransferCodeUiState()
    data class Success(
        val transferId: String,
        val transferCode: String,
        val birdName: String,
        val birdImageUrl: String?,
        val expiresAt: Long
    ) : TransferCodeUiState()
    data class Error(val message: String) : TransferCodeUiState()
    data object Cancelled : TransferCodeUiState()
}
