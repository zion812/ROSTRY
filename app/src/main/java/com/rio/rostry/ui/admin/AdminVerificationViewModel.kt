package com.rio.rostry.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Admin Verification Dashboard.
 * 
 * Provides functionality to:
 * - Load pending verification requests from UserRepository (verifications collection)
 * - Approve or reject requests
 */
@HiltViewModel
class AdminVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    companion object {
        // Predefined rejection reasons
        val REJECTION_REASONS = listOf(
            "Blurry or unclear photo",
            "Invalid government ID",
            "Farm photo does not show chickens/farm",
            "ID and selfie do not match",
            "Document appears to be edited",
            "Other (please contact support)"
        )
    }

    private val _pendingRequests = MutableStateFlow<List<VerificationRequest>>(emptyList())
    val pendingRequests: StateFlow<List<VerificationRequest>> = _pendingRequests.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _selectedRequest = MutableStateFlow<VerificationRequest?>(null)
    val selectedRequest: StateFlow<VerificationRequest?> = _selectedRequest.asStateFlow()

    private var streamJob: Job? = null



    private val _historyRequests = MutableStateFlow<List<VerificationRequest>>(emptyList())
    val historyRequests: StateFlow<List<VerificationRequest>> = _historyRequests.asStateFlow()

    private var historyJob: Job? = null

    init {
        loadPendingRequests()
        loadHistoryRequests()
    }

    fun loadPendingRequests() {
        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            userRepository.streamPendingVerifications().collect { resource ->
                when (resource) {
                    is Resource.Loading -> if (_pendingRequests.value.isEmpty()) _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        val submissions = resource.data ?: emptyList()
                        _pendingRequests.value = submissions.map { it.toUiModel() }
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _toastEvent.emit("Error loading requests: ${resource.message}")
                    }
                }
            }
        }
    }

    fun loadHistoryRequests() {
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            // Fetch both VERIFIED and REJECTED
            // Note: This naive approach fetches lists separately. For production, a combined query is better.
            // Using existing repository method for now.
            
            // 1. Fetch Verified
            userRepository.getVerificationsByRoleAndStatus(null, VerificationStatus.VERIFIED).collect { resVerified ->
                if (resVerified is Resource.Success) {
                     val verified = resVerified.data?.map { it.toUiModel() } ?: emptyList()
                     
                     // 2. Fetch Rejected (nested to keep it simple, or combine flows)
                     userRepository.getVerificationsByRoleAndStatus(null, VerificationStatus.REJECTED).collect { resRejected ->
                         if (resRejected is Resource.Success) {
                             val rejected = resRejected.data?.map { it.toUiModel() } ?: emptyList()
                             // Combine and Sort
                             _historyRequests.value = (verified + rejected).sortedByDescending { it.submittedAt }
                         }
                     }
                }
            }
        }
    }

    fun selectRequest(request: VerificationRequest) {
        _selectedRequest.value = request
    }

    fun clearSelection() {
        _selectedRequest.value = null
    }

    fun approveRequest(userId: String, requestId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val adminId = currentUserProvider.userIdOrNull() ?: "admin"
                val result = userRepository.updateVerificationSubmissionStatus(
                    submissionId = requestId,
                    userId = userId,
                    status = VerificationStatus.VERIFIED,
                    reviewerId = adminId,
                    rejectionReason = null
                )

                if (result is Resource.Success) {
                    _toastEvent.emit("User verified successfully!")
                    clearSelection()
                    loadHistoryRequests() // Refresh history
                } else {
                    _toastEvent.emit("Failed to verify: ${result.message}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error approving request")
                _toastEvent.emit("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectRequest(userId: String, requestId: String, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val adminId = currentUserProvider.userIdOrNull() ?: "admin"
                val result = userRepository.updateVerificationSubmissionStatus(
                    submissionId = requestId,
                    userId = userId,
                    status = VerificationStatus.REJECTED,
                    reviewerId = adminId,
                    rejectionReason = reason
                )

                if (result is Resource.Success) {
                    _toastEvent.emit("Request rejected: $reason")
                    clearSelection()
                    loadHistoryRequests() // Refresh history
                } else {
                    _toastEvent.emit("Failed to reject: ${result.message}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error rejecting request")
                _toastEvent.emit("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Mapper extension for cleanliness
    private fun com.rio.rostry.domain.model.VerificationSubmission.toUiModel(): VerificationRequest {
        return VerificationRequest(
            userId = this.userId,
            requestId = this.submissionId,
            govtIdUrl = this.documentUrls.firstOrNull(),
            farmPhotoUrl = this.imageUrls.firstOrNull(),
            submittedAt = this.submittedAt?.time ?: 0L,
            userName = this.applicantName ?: "User: ${this.userId.take(8)}",
            status = this.currentStatus, 
            rejectionReason = this.rejectionReason ?: this.rejectionReason
        )
    }
}

/**
 * Data class for verification request display in Admin UI.
 * Mapped from VerificationSubmission domain object.
 */
data class VerificationRequest(
    val userId: String,
    val requestId: String,
    val govtIdUrl: String?,
    val farmPhotoUrl: String?,
    val submittedAt: Long,
    val userName: String?,
    val status: VerificationStatus = VerificationStatus.PENDING,
    val rejectionReason: String? = null
)
