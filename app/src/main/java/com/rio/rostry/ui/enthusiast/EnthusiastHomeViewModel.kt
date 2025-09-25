package com.rio.rostry.ui.enthusiast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.repository.analytics.AnalyticsRepository
import com.rio.rostry.data.repository.analytics.EnthusiastDashboard
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class EnthusiastHomeViewModel @Inject constructor(
    analyticsRepository: AnalyticsRepository,
    transferDao: TransferDao,
    currentUserProvider: CurrentUserProvider,
) : ViewModel() {

    data class UiState(
        val dashboard: EnthusiastDashboard = EnthusiastDashboard(0.0, 0, 0.0, emptyList()),
        val pendingTransfersCount: Int = 0,
    )

    private val uid = currentUserProvider.userIdOrNull()

    private val dashboardFlow = (uid?.let { analyticsRepository.enthusiastDashboard(it) }
        ?: MutableStateFlow(EnthusiastDashboard(0.0, 0, 0.0, emptyList())))

    private val transfersPendingFlow = if (uid != null) {
        combine(transferDao.getTransfersFromUser(uid), transferDao.getTransfersToUser(uid)) { a, b ->
            (a + b).count { it.status.equals("PENDING", ignoreCase = true) }
        }
    } else MutableStateFlow(0)

    val ui: StateFlow<UiState> = combine(dashboardFlow, transfersPendingFlow) { d, p ->
        UiState(dashboard = d, pendingTransfersCount = p)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())
}
