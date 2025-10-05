package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.QuarantineRepository
import com.rio.rostry.data.repository.monitoring.FarmAlertRepository
import com.rio.rostry.data.database.entity.QuarantineRecordEntity
import com.rio.rostry.data.database.entity.FarmAlertEntity
import com.rio.rostry.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class QuarantineViewModel @Inject constructor(
    private val repo: QuarantineRepository,
    private val farmAlertRepository: FarmAlertRepository,
    private val farmAlertDao: com.rio.rostry.data.database.dao.FarmAlertDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    @ApplicationContext private val appContext: android.content.Context
) : ViewModel() {

    data class UiState(
        val productId: String = "",
        val active: List<QuarantineRecordEntity> = emptyList(),
        val history: List<QuarantineRecordEntity> = emptyList(),
        val canDischarge: Map<String, Boolean> = emptyMap(),
        val nextUpdateDue: Map<String, Long> = emptyMap(),
        val isOverdue: Map<String, Boolean> = emptyMap()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun observe(productId: String) {
        _ui.update { it.copy(productId = productId) }
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                val active = list.filter { r -> r.status == "ACTIVE" }
                val history = list.filter { r -> r.status != "ACTIVE" }
                
                // Compute 12-hour policy metrics using lastUpdatedAt and updatesCount
                val now = System.currentTimeMillis()
                val twelveHours = TimeUnit.HOURS.toMillis(12)
                
                val canDischarge = active.associate { rec ->
                    // Can discharge if: has at least 2 updates AND last update was within 12 hours
                    val timeSinceUpdate = now - rec.lastUpdatedAt
                    val can = (rec.updatesCount >= 2) && (timeSinceUpdate <= twelveHours)
                    rec.quarantineId to can
                }
                
                val nextUpdateDue = active.associate { rec ->
                    rec.quarantineId to (rec.lastUpdatedAt + twelveHours)
                }
                
                val isOverdue = active.associate { rec ->
                    val nextDue = rec.lastUpdatedAt + twelveHours
                    rec.quarantineId to (now > nextDue)
                }
                
                _ui.update { 
                    it.copy(
                        active = active,
                        history = history,
                        canDischarge = canDischarge,
                        nextUpdateDue = nextUpdateDue,
                        isOverdue = isOverdue
                    )
                }
            }
        }
    }

    fun start(productId: String, reason: String, protocol: String?) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val rec = QuarantineRecordEntity(
                quarantineId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                reason = reason,
                protocol = protocol
            )
            repo.insert(rec)
        }
    }

    fun updateQuarantine(
        quarantineId: String,
        notes: String?,
        medication: String?,
        status: String?
    ) {
        viewModelScope.launch {
            val active = _ui.value.active.find { it.quarantineId == quarantineId } ?: return@launch
            
            val now = System.currentTimeMillis()
            val updated = active.copy(
                vetNotes = notes,
                medicationScheduleJson = medication,
                lastUpdatedAt = now,
                updatesCount = active.updatesCount + 1,
                updatedAt = now,
                dirty = true
            )
            
            repo.update(updated)
            
            // Clear any overdue alerts for this quarantine
            // Mark existing unread alerts as resolved
        }
    }

    fun dischargeQuarantine(quarantineId: String): Result<Unit> {
        viewModelScope.launch {
            val active = _ui.value.active.find { it.quarantineId == quarantineId } ?: return@launch
            val now = System.currentTimeMillis()
            val twelveHours = TimeUnit.HOURS.toMillis(12)
            
            // Recompute canDischarge to ensure policy is enforced
            val timeSinceUpdate = now - active.lastUpdatedAt
            val canDischarge = (active.updatesCount >= 2) && (timeSinceUpdate <= twelveHours)
            
            if (!canDischarge) {
                // Policy violation - cannot discharge
                return@launch
            }
            
            val updated = active.copy(
                status = "RECOVERED",
                endedAt = now,
                updatedAt = now,
                dirty = true
            )
            repo.update(updated)
        }
        
        return Result.success(Unit)
    }

    fun checkOverdueUpdates() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val now = System.currentTimeMillis()
            val twelveHoursAgo = now - TimeUnit.HOURS.toMillis(12)
            
            _ui.value.active.forEach { rec ->
                if (rec.lastUpdatedAt < twelveHoursAgo) {
                    // Check for existing unread alert for this quarantine in last 12h to avoid duplicates
                    val existingAlerts = farmAlertDao.getByFarmer(farmerId, limit = 100)
                    val hasRecentAlert = existingAlerts.any { alert ->
                        alert.alertType == "QUARANTINE_UPDATE" &&
                        alert.message.contains(rec.quarantineId) &&
                        !alert.isRead &&
                        (now - alert.createdAt) < TimeUnit.HOURS.toMillis(12)
                    }
                    
                    if (!hasRecentAlert) {
                        // Insert alert with correct farmerId
                        val alert = FarmAlertEntity(
                            alertId = UUID.randomUUID().toString(),
                            farmerId = farmerId,
                            alertType = "QUARANTINE_UPDATE",
                            severity = "URGENT",
                            message = "Quarantine update overdue for ${rec.productId} (ID: ${rec.quarantineId})",
                            actionRoute = Routes.MONITORING_QUARANTINE,
                            createdAt = now,
                            dirty = true
                        )
                        farmAlertRepository.insert(alert)
                        
                        // Trigger notification
                        com.rio.rostry.utils.notif.FarmNotifier.notifyQuarantineOverdue(appContext, rec.productId)
                    }
                }
            }
        }
    }

    fun complete(rec: QuarantineRecordEntity, status: String = "RECOVERED") {
        viewModelScope.launch {
            repo.update(rec.copy(status = status, endedAt = System.currentTimeMillis()))
        }
    }
}
