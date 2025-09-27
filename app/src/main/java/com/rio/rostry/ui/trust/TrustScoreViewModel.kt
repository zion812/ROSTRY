package com.rio.rostry.ui.trust

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.DisputeDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrustScoreViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val disputeDao: DisputeDao,
    private val currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    data class Breakdown(
        val successRate: Double = 0.0,
        val authenticityChecks: Double = 0.0,
        val docsQuality: Double = 0.0,
        val disputesImpact: Double = 0.0,
        val responseTime: Double = 0.0
    )

    data class UiState(
        val loading: Boolean = true,
        val userId: String = "",
        val score: Double = 0.0,
        val breakdown: Breakdown = Breakdown(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(userIdArg: String?) {
        viewModelScope.launch {
            val userId = userIdArg ?: currentUserProvider.userIdOrNull() ?: "me"
            _state.value = _state.value.copy(loading = true, error = null, userId = userId)
            runCatching {
                val from = transferDao.getTransfersFromUser(userId).first()
                val to = transferDao.getTransfersToUser(userId).first()
                val all = (from + to).distinctBy { it.transferId }
                val completed = all.count { it.status.equals("COMPLETED", ignoreCase = true) }
                val total = all.size.coerceAtLeast(1)
                val success = completed.toDouble() / total

                // Authenticity: proportion of transfers having at least one verification step
                val hasChecks = all.count { t -> verificationDao.getByTransfer(t.transferId).isNotEmpty() }
                val authenticity = hasChecks.toDouble() / total

                // Docs quality: rough metric = average number of steps per transfer normalized by 3
                val avgSteps = all.map { t -> verificationDao.getByTransfer(t.transferId).size }.average()
                val docsQ = (avgSteps / 3.0).coerceIn(0.0, 1.0)

                // Disputes impact: penalize ratio of disputes to transfers
                val disputesOpen = all.sumOf { t -> disputeDao.getByTransfer(t.transferId).count { it.status == "OPEN" } }
                val disputePenalty = 1.0 - (disputesOpen.toDouble() / total).coerceIn(0.0, 1.0)

                // Response time: placeholder 0.8 until we wire SLA metrics
                val response = 0.8

                // Weighted score
                val score = (success * 0.4 + authenticity * 0.2 + docsQ * 0.2 + disputePenalty * 0.1 + response * 0.1) * 100.0

                _state.value = _state.value.copy(
                    loading = false,
                    score = score,
                    breakdown = Breakdown(
                        successRate = success * 100,
                        authenticityChecks = authenticity * 100,
                        docsQuality = docsQ * 100,
                        disputesImpact = disputePenalty * 100,
                        responseTime = response * 100
                    )
                )
            }.onFailure { e ->
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}
