package com.rio.rostry.ui.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.demo.DemoDataCoordinator
import com.rio.rostry.demo.DemoModeManager
import com.rio.rostry.demo.DemoNetworkController
import com.rio.rostry.demo.DemoWorkController
import com.rio.rostry.demo.FeedbackManager
import com.rio.rostry.demo.GuidedTourManager
import com.rio.rostry.demo.LocationSimulation
import com.rio.rostry.domain.model.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoToolsViewModel @Inject constructor(
    private val demoMode: DemoModeManager,
    private val seeder: DemoDataCoordinator,
    private val network: DemoNetworkController,
    private val work: DemoWorkController,
    private val location: LocationSimulation,
    private val tour: GuidedTourManager,
    private val feedback: FeedbackManager
) : ViewModel() {

    data class UiState(
        val enabled: Boolean = false,
        val paymentsFlag: Boolean = true,
        val socialFlag: Boolean = true,
        val marketplaceFlag: Boolean = true,
        val transfersFlag: Boolean = true,
        val monitoringFlag: Boolean = true,
        val analyticsFlag: Boolean = true,
        val locationFlag: Boolean = true,
        val offlineFlag: Boolean = true,
        val tourActive: Boolean = false,
        val currentTourStepIndex: Int = 0,
        val lastFeedbackMessage: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            demoMode.enabled.collect { on -> _ui.value = _ui.value.copy(enabled = on) }
        }
        viewModelScope.launch {
            demoMode.flags.collect { f ->
                _ui.value = _ui.value.copy(
                    paymentsFlag = f.payments,
                    socialFlag = f.social,
                    marketplaceFlag = f.marketplace,
                    transfersFlag = f.transfers,
                    monitoringFlag = f.monitoring,
                    analyticsFlag = f.analytics,
                    locationFlag = f.location,
                    offlineFlag = f.offline
                )
            }
        }
        viewModelScope.launch {
            tour.active.collect { active -> _ui.value = _ui.value.copy(tourActive = active) }
        }
        viewModelScope.launch {
            tour.currentIndex.collect { idx -> _ui.value = _ui.value.copy(currentTourStepIndex = idx) }
        }
    }

    fun setEnabled(enabled: Boolean) { demoMode.setEnabled(enabled) }

    fun updateFlag(name: String, value: Boolean) {
        demoMode.updateFlags {
            when (name) {
                "payments" -> copy(payments = value)
                "social" -> copy(social = value)
                "marketplace" -> copy(marketplace = value)
                "transfers" -> copy(transfers = value)
                "monitoring" -> copy(monitoring = value)
                "analytics" -> copy(analytics = value)
                "location" -> copy(location = value)
                "offline" -> copy(offline = value)
                else -> this
            }
        }
    }

    fun seedAll() { seeder.seedAllAsync() }
    fun seedRole(role: UserType) { seeder.seedByRoleAsync(role) }
    fun resetSession() { seeder.resetSession() }

    fun goOffline() { network.goOffline() }
    fun goWifi() { network.goOnlineWifi() }

    fun simulate2G() { network.simulateQuality(com.rio.rostry.utils.network.NetworkQualityMonitor.Quality.G2) }
    fun simulate3G() { network.simulateQuality(com.rio.rostry.utils.network.NetworkQualityMonitor.Quality.G3) }
    fun simulate4G() { network.simulateQuality(com.rio.rostry.utils.network.NetworkQualityMonitor.Quality.G4) }
    fun simulate5G() { network.simulateQuality(com.rio.rostry.utils.network.NetworkQualityMonitor.Quality.G5) }
    fun clearNetwork() { network.clearOverrides() }

    // Offline testing hooks
    fun runSyncNow() { work.runSyncNow() }
    fun runOutgoingNow(requireNetwork: Boolean) { work.runOutgoingNow(requireNetwork) }

    fun startTour() { tour.startDefaultTour() }
    fun tourNext() { tour.next() }
    fun tourPrev() { tour.prev() }
    fun stopTour() { tour.stop() }

    fun sendFeedback(type: String, message: String) {
        feedback.report(type, message)
        _ui.value = _ui.value.copy(lastFeedbackMessage = message)
    }

    // Expose location route as a simple flow string (lat,lng)
    val routeFlow = location.mockRoute()
}
