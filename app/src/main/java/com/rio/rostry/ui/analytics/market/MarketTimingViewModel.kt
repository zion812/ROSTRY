package com.rio.rostry.ui.analytics.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.GrowthRepository
import com.rio.rostry.domain.service.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Calendar

@HiltViewModel
class MarketTimingViewModel @Inject constructor(
    private val priceTrendService: PriceTrendService,
    private val growthPredictionService: GrowthPredictionService,
    private val growthRepository: GrowthRepository
) : ViewModel() {

    private val _marketState = MutableStateFlow<MarketTimingState>(MarketTimingState.Loading)
    val marketState: StateFlow<MarketTimingState> = _marketState.asStateFlow()

    init {
        loadMarketInsights()
    }

    private fun loadMarketInsights() {
        viewModelScope.launch {
            // 1. Get Seasonal Context
            val seasonalContext = priceTrendService.getSeasonalContext()
            
            // 2. Get Best Time to Sell Prediction based on market ONLY
            val bestTimeMarket = priceTrendService.predictBestSellingTime()
            
            // 3. TODO: In a real app, we would fetch the user's actual batches here
            // For now, we'll simulate a batch nearing maturity to show the feature
            val sampleBatchPrediction = growthPredictionService.predictGrowthTrajectory(
                weights = listOf(42, 110, 240, 480, 850, 1250, 1600), // Simulating 6-7 weeks
                breed = "Broiler"
            )
            
            _marketState.value = MarketTimingState.Success(
                seasonalContext = seasonalContext,
                bestTimeToSell = bestTimeMarket,
                growthPrediction = sampleBatchPrediction
            )
        }
    }
}

sealed class MarketTimingState {
    object Loading : MarketTimingState()
    data class Success(
        val seasonalContext: SeasonalContext,
        val bestTimeToSell: BestTimeToSell,
        val growthPrediction: GrowthPrediction
    ) : MarketTimingState()
    data class Error(val message: String) : MarketTimingState()
}
