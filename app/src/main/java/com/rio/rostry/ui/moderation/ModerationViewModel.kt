package com.rio.rostry.ui.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ModerationReportsDao
import com.rio.rostry.data.database.entity.ModerationReportEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.notifications.VerificationNotificationService
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val reportsDao: ModerationReportsDao,
    private val userRepository: UserRepository,
    private val verificationNotificationService: VerificationNotificationService,
) : ViewModel() {

    val openReports: StateFlow<List<ModerationReportEntity>> =
        reportsDao.streamByStatus("OPEN").stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    data class VerificationRequest(
        val userId: String,
        val userType: UserType?,
        val requestType: String, // FARMER_LOCATION or ENTHUSIAST_KYC
        val submittedAt: Long?,
        val status: VerificationStatus?,
        val documentUrls: List<String>,
        val imageUrls: List<String>,
        val farmLat: Double?,
        val farmLng: Double?,
        val kycLevel: Int?,
    )

    private val _pendingVerifications = MutableStateFlow<List<VerificationRequest>>(emptyList())
    val pendingVerifications: StateFlow<List<VerificationRequest>> = _pendingVerifications.asStateFlow()

    data class VerificationStats(
        val pending: Int = 0,
        val approvedToday: Int = 0,
        val rejectedToday: Int = 0,
        val backlog: Int = 0,
    )
    private val _verificationStats = MutableStateFlow(VerificationStats())
    val verificationStats: StateFlow<VerificationStats> = _verificationStats.asStateFlow()

    fun refreshVerifications() {
        viewModelScope.launch {
            userRepository.streamPendingVerifications().collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val users = res.data ?: emptyList()
                        val requests = users.map { u ->
                            val docUrls = runCatching { org.json.JSONArray(u.kycDocumentUrls ?: "[]") }
                                .getOrNull()?.let { arr -> (0 until arr.length()).map { i -> arr.getString(i) } } ?: emptyList()
                            val imgUrls = runCatching { org.json.JSONArray(u.kycImageUrls ?: "[]") }
                                .getOrNull()?.let { arr -> (0 until arr.length()).map { i -> arr.getString(i) } } ?: emptyList()
                            VerificationRequest(
                                userId = u.userId,
                                userType = u.userType,
                                requestType = if (u.userType?.name == "FARMER") "FARMER_LOCATION" else "ENTHUSIAST_KYC",
                                submittedAt = u.kycUploadedAt,
                                status = u.verificationStatus,
                                documentUrls = docUrls,
                                imageUrls = imgUrls,
                                farmLat = u.farmLocationLat,
                                farmLng = u.farmLocationLng,
                                kycLevel = u.kycLevel
                            )
                        }
                        _pendingVerifications.value = requests
                        _verificationStats.value = VerificationStats(
                            pending = requests.size,
                            approvedToday = _verificationStats.value.approvedToday,
                            rejectedToday = _verificationStats.value.rejectedToday,
                            backlog = requests.size
                        )
                    }
                    is Resource.Error -> {
                        _pendingVerifications.value = emptyList()
                        _verificationStats.value = VerificationStats(backlog = 0)
                        Timber.e("refreshVerifications error: ${res.message}")
                    }
                    else -> { /* Loading ignored */ }
                }
            }
        }
    }

    fun updateStatus(reportId: String, status: String) {
        viewModelScope.launch {
            reportsDao.updateStatus(reportId, status, System.currentTimeMillis())
        }
    }

    fun approveVerification(userId: String, userType: UserType?) {
        viewModelScope.launch {
            when (val res = userRepository.updateVerificationStatus(userId, VerificationStatus.VERIFIED)) {
                is Resource.Error -> Timber.e("approveVerification failed: ${res.message}")
                is Resource.Success -> {
                    runCatching { verificationNotificationService.notifyVerificationApproved(userId, userType) }
                    refreshVerifications()
                }
                else -> {}
            }
        }
    }

    fun rejectVerification(userId: String, reason: String) {
        viewModelScope.launch {
            when (val res = userRepository.updateVerificationStatus(userId, VerificationStatus.REJECTED)) {
                is Resource.Error -> Timber.e("rejectVerification failed: ${res.message}")
                is Resource.Success -> {
                    runCatching { verificationNotificationService.notifyVerificationRejected(userId, reason) }
                    refreshVerifications()
                }
                else -> {}
            }
        }
    }
}
