package com.rio.rostry.ui.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ModerationReportsDao
import com.rio.rostry.data.database.entity.ModerationReportEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.VerificationSubmission
import com.rio.rostry.notifications.VerificationNotificationService
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ModerationViewModel @Inject constructor(
    private val reportsDao: ModerationReportsDao,
    private val userRepository: UserRepository,
    private val verificationNotificationService: VerificationNotificationService,
    private val currentUserProvider: com.rio.rostry.session.CurrentUserProvider
) : ViewModel() {

    val openReports: StateFlow<List<ModerationReportEntity>> =
        reportsDao.streamByStatus("OPEN").stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtering state
    data class FilterState(
        val upgradeType: UpgradeType? = null,
        val role: UserType? = null,
        val status: VerificationStatus? = null,
        val dateRangeStart: Long? = null,
        val dateRangeEnd: Long? = null,
        val searchQuery: String? = null
    )
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pendingVerifications: StateFlow<List<VerificationSubmission>> = _filterState
        .flatMapLatest { filters ->
            val flow = if (filters.upgradeType != null) {
                userRepository.getVerificationsByUpgradeType(filters.upgradeType)
            } else if (filters.role != null || filters.status != null) {
                userRepository.getVerificationsByRoleAndStatus(filters.role, filters.status)
            } else {
                userRepository.streamPendingVerifications()
            }

            flow.map { res ->
                if (res is Resource.Success) {
                    var submissions = res.data ?: emptyList()
                    
                    // Apply client-side filters for date and search query
                    if (filters.dateRangeStart != null && filters.dateRangeEnd != null) {
                        submissions = submissions.filter { 
                            val time = it.submittedAt?.time ?: 0L
                            time in filters.dateRangeStart..filters.dateRangeEnd
                        }
                    }
                    
                    if (!filters.searchQuery.isNullOrBlank()) {
                        val query = filters.searchQuery.lowercase()
                        submissions = submissions.filter { 
                            it.userId.lowercase().contains(query) || 
                            it.submissionId.lowercase().contains(query)
                        }
                    }
                    submissions
                } else {
                    Timber.e("Error loading verifications: ${res.message}")
                    emptyList()
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    data class StatsCounters(val approved: Int = 0, val rejected: Int = 0)
    private val _statsCounters = MutableStateFlow(StatsCounters())

    data class VerificationStats(
        val pending: Int = 0,
        val approvedToday: Int = 0,
        val rejectedToday: Int = 0,
        val backlog: Int = 0,
        val byType: Map<UpgradeType, Int> = emptyMap()
    )

    val verificationStats: StateFlow<VerificationStats> = combine(
        pendingVerifications,
        _statsCounters
    ) { submissions, counters ->
        val byType = submissions.groupBy { it.upgradeType }.mapValues { it.value.size }
        VerificationStats(
            pending = submissions.count { it.currentStatus == VerificationStatus.PENDING },
            backlog = submissions.size,
            byType = byType,
            approvedToday = counters.approved,
            rejectedToday = counters.rejected
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, VerificationStats())

    fun refreshVerifications() {
        // No-op: Flows are reactive
    }

    fun setFilters(
        upgradeType: UpgradeType? = null,
        role: UserType? = null,
        status: VerificationStatus? = null,
        dateRangeStart: Long? = null,
        dateRangeEnd: Long? = null,
        searchQuery: String? = null
    ) {
        _filterState.value = FilterState(
            upgradeType = upgradeType,
            role = role,
            status = status,
            dateRangeStart = dateRangeStart,
            dateRangeEnd = dateRangeEnd,
            searchQuery = searchQuery
        )
    }

    fun clearFilters() {
        _filterState.value = FilterState()
    }

    fun updateStatus(reportId: String, status: String) {
        viewModelScope.launch {
            reportsDao.updateStatus(reportId, status, System.currentTimeMillis())
        }
    }

    fun approveVerification(submission: VerificationSubmission) {
        viewModelScope.launch {
            // 1. Update verification status in users collection
            val res = userRepository.updateVerificationStatus(submission.userId, VerificationStatus.VERIFIED)
            
            if (res is Resource.Success) {
                // Update submission status
                val adminId = currentUserProvider.userIdOrNull() ?: "admin"
                userRepository.updateVerificationSubmissionStatus(submission.userId, VerificationStatus.VERIFIED, adminId)

                // 2. If it's a role upgrade, update the user role
                if (submission.targetRole != null && submission.targetRole != submission.currentRole) {
                    userRepository.updateUserType(submission.userId, submission.targetRole)
                }
                
                // 3. Notify user
                runCatching { 
                    verificationNotificationService.notifyVerificationApproved(submission.userId, submission.targetRole ?: submission.currentRole) 
                }
                
                // 4. Update stats
                _statsCounters.value = _statsCounters.value.copy(
                    approved = _statsCounters.value.approved + 1
                )
            } else {
                Timber.e("approveVerification failed: ${res.message}")
            }
        }
    }

    fun rejectVerification(submission: VerificationSubmission, reason: String) {
        viewModelScope.launch {
            // 1. Update verification status in users collection
            val res = userRepository.updateVerificationStatus(submission.userId, VerificationStatus.REJECTED)
            
            if (res is Resource.Success) {
                // Update submission status
                val adminId = currentUserProvider.userIdOrNull() ?: "admin"
                userRepository.updateVerificationSubmissionStatus(submission.userId, VerificationStatus.REJECTED, adminId, reason)

                // 2. Notify user
                runCatching { 
                    verificationNotificationService.notifyVerificationRejected(submission.userId, reason) 
                }
                
                // 3. Update stats
                _statsCounters.value = _statsCounters.value.copy(
                    rejected = _statsCounters.value.rejected + 1
                )
            } else {
                Timber.e("rejectVerification failed: ${res.message}")
            }
        }
    }
}
