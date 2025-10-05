package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.data.database.entity.GrowthRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GrowthViewModel @Inject constructor(
    private val repo: GrowthRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val analyticsRepository: com.rio.rostry.data.repository.analytics.AnalyticsRepository
): ViewModel() {

    data class UiState(
        val records: List<GrowthRecordEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    fun observe(productId: String) {
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    fun recordToday(productId: String, weightGrams: Double?, heightCm: Double?) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            // week calculation can be moved to repo if birth date available; default to 0 for now
            val record = GrowthRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                week = 0,
                weightGrams = weightGrams,
                heightCm = heightCm
            )
            repo.upsert(record)
        }
    }
    
    /**
     * Track analytics when farmer navigates to list product on marketplace
     */
    fun trackListOnMarketplaceClick(productId: String) {
        viewModelScope.launch {
            val userId = firebaseAuth.currentUser?.uid ?: return@launch
            analyticsRepository.trackFarmToMarketplaceListClicked(userId, productId, "growth")
        }
    }
}
