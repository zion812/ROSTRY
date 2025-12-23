package com.rio.rostry.ui.farmer.verification

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.FarmVerificationRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmVerificationViewModel @Inject constructor(
    private val verificationRepository: FarmVerificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmVerificationUiState())
    val uiState: StateFlow<FarmVerificationUiState> = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FarmVerificationUiState()
    )

    init {
        loadVerificationStatus()
    }

    private fun loadVerificationStatus() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUserSuspend()
            if (user != null) {
                verificationRepository.getVerificationStatus(user.userId).collect { verification ->
                     val status = if (verification != null) {
                         verification.status
                     } else {
                         user.verificationStatus
                     }
                     
                     _uiState.update { it.copy(
                         currentStatus = status,
                         rejectionReason = verification?.rejectionReason ?: user.verificationRejectionReason
                     ) }
                }
            }
        }
    }

    fun onAddressChanged(field: String, value: String) {
        _uiState.update { 
            val newAddress = it.address.toMutableMap()
            newAddress[field] = value
            it.copy(address = newAddress) 
        }
    }

    fun onLocationSelected(lat: Double, lng: Double, accuracy: Float?) {
        _uiState.update { 
            it.copy(
                location = mapOf("lat" to lat, "lng" to lng),
                gpsAccuracy = accuracy
            )
        }
    }

    fun onDocumentAdded(uri: Uri) {
         _uiState.update { 
            val newDocs = it.documentUris.toMutableList()
            newDocs.add(uri.toString())
            it.copy(documentUris = newDocs) 
        }
    }

    fun onDocumentRemoved(uriString: String) {
        _uiState.update { 
            val newDocs = it.documentUris.toMutableList()
            newDocs.remove(uriString)
            it.copy(documentUris = newDocs) 
        }
    }

    fun submitVerification() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = userRepository.getCurrentUserSuspend()
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not found") }
                return@launch
            }

            // Validation
            if (!validateForm()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val result = verificationRepository.submitVerification(
                farmerId = user.userId,
                farmAddress = _uiState.value.address,
                location = _uiState.value.location,
                documentUris = _uiState.value.documentUris,
                gpsAccuracy = _uiState.value.gpsAccuracy
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSubmitted = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Submission failed") }
            }
        }
    }

    private fun validateForm(): Boolean {
        val s = _uiState.value
        val validAddress = s.address["line1"]?.isNotBlank() == true && s.address["city"]?.isNotBlank() == true
        val validLocation = s.location["lat"] != null
        val validDocs = s.documentUris.isNotEmpty()
        
        if (!validAddress || !validLocation || !validDocs) {
             _uiState.update { it.copy(error = "Please fill all required fields, select location, and upload at least one document.") }
             return false
        }
        return true
    }
}

data class FarmVerificationUiState(
    val currentStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val address: Map<String, String> = mapOf(
        "line1" to "", "line2" to "", "city" to "", "state" to "", "postalCode" to "", "country" to ""
    ),
    val location: Map<String, Double?> = mapOf("lat" to null, "lng" to null),
    val gpsAccuracy: Float? = null,
    val documentUris: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    val rejectionReason: String? = null
)
