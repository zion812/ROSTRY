package com.rio.rostry.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.database.dao.VerificationRequestDao
import com.rio.rostry.data.database.entity.VerificationRequestEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ViewModel for Admin Verification Dashboard.
 * 
 * Provides functionality to:
 * - Load pending verification requests from Firestore
 * - Approve or reject requests with reason
 * - Update user's verification status
 */
@HiltViewModel
class AdminVerificationViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val verificationRequestDao: VerificationRequestDao,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    companion object {
        private const val COLLECTION_NAME = "verification_requests"
        
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

    init {
        loadPendingRequests()
    }

    fun loadPendingRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("status", VerificationRequestEntity.STATUS_PENDING)
                    .get()
                    .await()

                val requests = snapshot.documents.mapNotNull { doc ->
                    try {
                        VerificationRequest(
                            userId = doc.getString("userId") ?: return@mapNotNull null,
                            requestId = doc.getString("requestId") ?: doc.id,
                            govtIdUrl = doc.getString("govtIdUrl"),
                            farmPhotoUrl = doc.getString("farmPhotoUrl"),
                            submittedAt = doc.getLong("submittedAt") ?: 0L,
                            userName = null // Will be fetched separately if needed
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                
                _pendingRequests.value = requests
            } catch (e: Exception) {
                _toastEvent.emit("Failed to load requests: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectRequest(request: VerificationRequest) {
        _selectedRequest.value = request
    }

    fun clearSelection() {
        _selectedRequest.value = null
    }

    fun approveRequest(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Update Firestore
                firestore.collection(COLLECTION_NAME)
                    .document(userId)
                    .update(
                        mapOf(
                            "status" to VerificationRequestEntity.STATUS_APPROVED,
                            "reviewedAt" to System.currentTimeMillis()
                        )
                    )
                    .await()
                
                // Update user entity (Firestore + Local)
                userRepository.updateVerificationStatus(userId, VerificationStatus.VERIFIED)
                
                // Update local request entity if exists
                val localRequest = verificationRequestDao.getLatestByUserId(userId)
                if (localRequest != null) {
                    verificationRequestDao.approve(localRequest.requestId)
                }
                
                // Remove from list
                _pendingRequests.value = _pendingRequests.value.filter { it.userId != userId }
                _selectedRequest.value = null
                
                _toastEvent.emit("User verified successfully!")
                
                // TODO: Send FCM notification to user
                
            } catch (e: Exception) {
                _toastEvent.emit("Failed to approve: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectRequest(userId: String, reason: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Update Firestore
                firestore.collection(COLLECTION_NAME)
                    .document(userId)
                    .update(
                        mapOf(
                            "status" to VerificationRequestEntity.STATUS_REJECTED,
                            "rejectionReason" to reason,
                            "reviewedAt" to System.currentTimeMillis()
                        )
                    )
                    .await()
                
                // Update user entity (stays REJECTED but updated in both Firestore and Local)
                userRepository.updateVerificationStatus(userId, VerificationStatus.REJECTED)
                
                // Update local request entity if exists
                val localRequest = verificationRequestDao.getLatestByUserId(userId)
                if (localRequest != null) {
                    verificationRequestDao.reject(localRequest.requestId, reason)
                }
                
                // Remove from list
                _pendingRequests.value = _pendingRequests.value.filter { it.userId != userId }
                _selectedRequest.value = null
                
                _toastEvent.emit("Request rejected: $reason")
                
                // TODO: Send FCM notification to user
                
            } catch (e: Exception) {
                _toastEvent.emit("Failed to reject: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Data class for verification request display.
 */
data class VerificationRequest(
    val userId: String,
    val requestId: String,
    val govtIdUrl: String?,
    val farmPhotoUrl: String?,
    val submittedAt: Long,
    val userName: String? = null
)
