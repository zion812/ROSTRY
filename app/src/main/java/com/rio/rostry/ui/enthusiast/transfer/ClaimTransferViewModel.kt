package com.rio.rostry.ui.enthusiast.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.domain.transfer.ClaimResult
import com.rio.rostry.domain.transfer.OwnershipTransferUseCase
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for ClaimTransferScreen.
 * Handles code validation, claim, and transfer execution.
 */
@HiltViewModel
class ClaimTransferViewModel @Inject constructor(
    private val transferUseCase: OwnershipTransferUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ClaimTransferUiState>(ClaimTransferUiState.EnterCode())
    val uiState: StateFlow<ClaimTransferUiState> = _uiState.asStateFlow()
    
    private val _codeInput = MutableStateFlow("")
    val codeInput: StateFlow<String> = _codeInput.asStateFlow()
    
    private var currentClaimResult: ClaimResult? = null
    
    fun updateCode(code: String) {
        _codeInput.value = code
        // Clear error when typing
        val current = _uiState.value
        if (current is ClaimTransferUiState.EnterCode && current.errorMessage != null) {
            _uiState.value = current.copy(errorMessage = null)
        }
    }
    
    fun claimTransfer() {
        val code = _codeInput.value
        if (code.length != 6) {
            _uiState.value = ClaimTransferUiState.EnterCode(errorMessage = "Please enter a 6-character code")
            return
        }
        
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = ClaimTransferUiState.EnterCode(errorMessage = "Please sign in first")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = ClaimTransferUiState.EnterCode(isLoading = true)
            
            when (val result = transferUseCase.claimTransfer(code, userId)) {
                is Resource.Success -> {
                    val data = result.data!!
                    currentClaimResult = data
                    _uiState.value = ClaimTransferUiState.Preview(
                        transferId = data.transferId,
                        birdId = data.birdId,
                        birdName = data.birdName,
                        birdBreed = data.birdBreed,
                        birdImageUrl = data.birdImageUrl
                    )
                }
                is Resource.Error -> {
                    _uiState.value = ClaimTransferUiState.EnterCode(
                        errorMessage = result.message ?: "Invalid code"
                    )
                }
                is Resource.Loading -> {
                    // Keep loading
                }
            }
        }
    }
    
    fun confirmClaim() {
        val claim = currentClaimResult ?: return
        
        viewModelScope.launch {
            val currentPreview = _uiState.value as? ClaimTransferUiState.Preview ?: return@launch
            _uiState.value = currentPreview.copy(isConfirming = true)
            
            when (val result = transferUseCase.executeTransfer(claim.transferId)) {
                is Resource.Success -> {
                    _uiState.value = ClaimTransferUiState.Success(
                        birdName = claim.birdName
                    )
                }
                is Resource.Error -> {
                    _uiState.value = ClaimTransferUiState.Error(
                        message = result.message ?: "Failed to complete transfer"
                    )
                }
                is Resource.Loading -> {
                    // Keep loading
                }
            }
        }
    }
    
    fun reset() {
        _codeInput.value = ""
        currentClaimResult = null
        _uiState.value = ClaimTransferUiState.EnterCode()
    }
}

sealed class ClaimTransferUiState {
    data class EnterCode(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : ClaimTransferUiState()
    
    data class Preview(
        val transferId: String,
        val birdId: String,
        val birdName: String,
        val birdBreed: String?,
        val birdImageUrl: String?,
        val isConfirming: Boolean = false
    ) : ClaimTransferUiState()
    
    data class Success(
        val birdName: String
    ) : ClaimTransferUiState()
    
    data class Error(
        val message: String
    ) : ClaimTransferUiState()
}
