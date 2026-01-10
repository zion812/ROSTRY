package com.rio.rostry.ui.enthusiast.digitalfarm

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.CoinLedgerDao
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.domain.model.*
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * ViewModel for the Digital Farm - Evolutionary Visuals feature.
 * 
 * Converts raw ProductEntity data into zone-based VisualBird groups
 * for efficient Canvas rendering.
 * 
 * Supports persona-specific "lenses" via DigitalFarmConfig:
 * - FARMER: Operational flock view with batch management
 * - ENTHUSIAST: Showroom + genetics lab with champion focus
 */
@HiltViewModel
class DigitalFarmViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val coinLedgerDao: CoinLedgerDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(DigitalFarmState(isLoading = true))
    val uiState: StateFlow<DigitalFarmState> = _uiState.asStateFlow()

    private val _farmStats = MutableStateFlow(FarmStats())
    val farmStats: StateFlow<FarmStats> = _farmStats.asStateFlow()

    private val _selectedBird = MutableStateFlow<VisualBird?>(null)
    val selectedBird: StateFlow<VisualBird?> = _selectedBird.asStateFlow()

    private val _tapResult = MutableSharedFlow<FarmTapResult>()
    val tapResult: SharedFlow<FarmTapResult> = _tapResult.asSharedFlow()
    
    // Persona-specific configuration (determines which zones/overlays/actions are visible)
    private val _config = MutableStateFlow(DigitalFarmConfig.ENTHUSIAST)
    val config: StateFlow<DigitalFarmConfig> = _config.asStateFlow()
    
    /**
     * Set the farm mode (called from UI based on user's role)
     */
    fun setMode(mode: DigitalFarmMode) {
        _config.value = when (mode) {
            DigitalFarmMode.FARMER -> DigitalFarmConfig.FARMER
            DigitalFarmMode.ENTHUSIAST -> DigitalFarmConfig.ENTHUSIAST
        }
    }
    
    /**
     * Set config based on user type (convenience method)
     */
    fun setConfigForRole(role: UserType) {
        _config.value = DigitalFarmConfig.forRole(role)
    }

    // Zone bounds for positioning (normalized 0-1 coordinates)
    private val nurseryZoneBounds = Pair(Offset(0.05f, 0.05f), Offset(0.45f, 0.35f))
    private val breedingZoneBounds = Pair(Offset(0.55f, 0.05f), Offset(0.95f, 0.35f))
    private val freeRangeZoneBounds = Pair(Offset(0.1f, 0.4f), Offset(0.9f, 0.65f))
    private val growOutZoneBounds = Pair(Offset(0.05f, 0.7f), Offset(0.45f, 0.9f))
    private val marketZoneBounds = Pair(Offset(0.55f, 0.7f), Offset(0.95f, 0.9f))
    
    // ==================== PHASE 5: VISUALIZATION STATE ====================
    
    // Current time of day (auto-updating)
    private val _timeOfDay = MutableStateFlow(TimeOfDay.fromCurrentTime())
    val timeOfDay: StateFlow<TimeOfDay> = _timeOfDay.asStateFlow()
    
    // Current weather (can be set manually or from external data source)
    private val _weather = MutableStateFlow(WeatherType.SUNNY)
    val weather: StateFlow<WeatherType> = _weather.asStateFlow()
    
    /**
     * Update weather - can be connected to real weather API
     */
    fun setWeather(weatherType: WeatherType) {
        _weather.value = weatherType
    }
    
    /**
     * Refresh time of day (call periodically or on resume)
     */
    fun refreshTimeOfDay() {
        _timeOfDay.value = TimeOfDay.fromCurrentTime()
    }

    init {
        loadFarmData()
    }

    fun loadFarmData() {
        viewModelScope.launch {
            try {
                val userId = currentUserProvider.userIdOrNull()
                if (userId == null) {
                    _uiState.value = DigitalFarmState(error = "User not authenticated")
                    return@launch
                }
                productRepository.getProductsBySeller(userId)
                    .catch { e ->
                        Timber.e(e, "Failed to load farm data")
                        _uiState.value = DigitalFarmState(error = e.message ?: "Failed to load")
                    }
                    .collect { resource ->
                        when (resource) {
                            is com.rio.rostry.utils.Resource.Success -> {
                                val products = resource.data ?: emptyList()
                                val farmState = groupProductsByLifecycle(products)
                                _uiState.value = farmState
                                _farmStats.value = calculateStats(products, farmState)
                            }
                            is com.rio.rostry.utils.Resource.Error -> {
                                _uiState.value = DigitalFarmState(error = resource.message ?: "Failed to load")
                            }
                            is com.rio.rostry.utils.Resource.Loading -> {
                                _uiState.value = DigitalFarmState(isLoading = true)
                            }
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error initializing farm data")
                _uiState.value = DigitalFarmState(error = e.message)
            }
        }
    }

    /**
     * Core grouping logic - THE SCALABILITY SECRET
     * Instead of rendering 500 individual birds, we group them into zones.
     */
    private fun groupProductsByLifecycle(products: List<ProductEntity>): DigitalFarmState {
        val now = System.currentTimeMillis()
        
        // Convert all products to VisualBirds with zone assignment
        val allBirds = products
            .filter { !it.isDeleted && it.category.lowercase() in listOf("poultry", "bird", "chicken", "fowl") }
            .map { entity -> 
                val zone = determineZone(entity)
                entity.toVisualBird(zone) 
            }

        // Group 1: Nurseries (chicks with mothers)
        val chicksWithMothers = allBirds.filter { it.zone == DigitalFarmZone.NURSERY }
        val motherIds = products.filter { it.motherId != null }.mapNotNull { it.motherId }.distinct()
        val nurseries = motherIds.mapNotNull { motherId ->
            val mother = allBirds.find { it.productId == motherId }
            val chicks = chicksWithMothers.filter { 
                products.find { p -> p.productId == it.productId }?.motherId == motherId 
            }
            if (mother != null && chicks.isNotEmpty()) {
                NurseryGroup(
                    mother = mother.copy(zone = DigitalFarmZone.NURSERY),
                    chicks = chicks.mapIndexed { i, chick ->
                        chick.copy(position = calculateChickPosition(i, chicks.size))
                    },
                    nestPosition = randomPositionInZone(nurseryZoneBounds, motherId.hashCode())
                )
            } else null
        }

        // Group 2: Breeding Units (rooster + hens grouped by batchId)
        val breedingProducts = products.filter { it.isBreedingUnit && it.batchId != null }
        val breedingBatchIds = breedingProducts.mapNotNull { it.batchId }.distinct()
        val breedingUnits = breedingBatchIds.map { batchId ->
            val batchBirds = allBirds.filter { 
                products.find { p -> p.productId == it.productId }?.batchId == batchId 
            }
            val rooster = batchBirds.find { it.gender?.lowercase() == "male" }
            val hens = batchBirds.filter { it.gender?.lowercase() == "female" }
            val representative = breedingProducts.find { it.batchId == batchId }
            
            BreedingUnit(
                unitId = batchId,
                rooster = rooster,
                hens = hens,
                eggsCollectedToday = representative?.eggsCollectedToday ?: 0,
                lastEggLogDate = representative?.lastEggLogDate,
                hutPosition = randomPositionInZone(breedingZoneBounds, batchId.hashCode())
            )
        }

        // Group 3: Ready Display (gold star birds)
        val readyBirds = allBirds.filter { it.zone == DigitalFarmZone.READY_DISPLAY }
            .map { it.copy(position = randomPositionInZone(breedingZoneBounds, it.productId.hashCode())) }

        // Group 4: Market Stand (listed for sale)
        val marketBirds = allBirds.filter { it.zone == DigitalFarmZone.MARKET_STAND }
            .map { it.copy(position = randomPositionInZone(marketZoneBounds, it.productId.hashCode())) }

        // Group 5: Free Range (growing birds 4-12 weeks)
        val freeRange = allBirds.filter { it.zone == DigitalFarmZone.FREE_RANGE }
            .map { it.copy(position = randomPositionInZone(freeRangeZoneBounds, it.productId.hashCode())) }

        // Group 6: Grow Out (3-6 month birds)
        val growOut = allBirds.filter { it.zone == DigitalFarmZone.GROW_OUT }
            .map { it.copy(position = randomPositionInZone(growOutZoneBounds, it.productId.hashCode())) }

        return DigitalFarmState(
            nurseries = nurseries,
            breedingUnits = breedingUnits,
            freeRange = freeRange,
            growOut = growOut,
            readyDisplay = readyBirds,
            marketReady = marketBirds,
            isLoading = false
        )
    }

    /**
     * Determines which zone a bird belongs to based on lifecycle stage
     */
    private fun determineZone(entity: ProductEntity): DigitalFarmZone {
        val ageWeeks = entity.ageWeeks ?: calculateAgeWeeks(entity.birthDate)
        
        return when {
            // Market listed
            entity.status == "available" || entity.status == "listed" -> DigitalFarmZone.MARKET_STAND
            
            // Ready for sale (gold star)
            entity.readyForSale || 
            (entity.weightGrams != null && entity.targetWeight != null && 
             entity.weightGrams >= entity.targetWeight) -> DigitalFarmZone.READY_DISPLAY
            
            // Breeding unit
            entity.isBreedingUnit -> DigitalFarmZone.BREEDING_UNIT
            
            // Nursery (chicks with mother)
            entity.motherId != null && ageWeeks < 4 -> DigitalFarmZone.NURSERY
            
            // Free range (4-12 weeks)
            ageWeeks in 4..12 -> DigitalFarmZone.FREE_RANGE
            
            // Grow out (3-6 months = 12-24 weeks)
            ageWeeks in 13..24 -> DigitalFarmZone.GROW_OUT
            
            // Default to free range for adults
            else -> DigitalFarmZone.FREE_RANGE
        }
    }

    /**
     * Deterministic position generation - bird always appears at same spot
     */
    private fun randomPositionInZone(bounds: Pair<Offset, Offset>, seed: Int): Offset {
        val random = Random(seed)
        val x = bounds.first.x + random.nextFloat() * (bounds.second.x - bounds.first.x)
        val y = bounds.first.y + random.nextFloat() * (bounds.second.y - bounds.first.y)
        return Offset(x, y)
    }

    /**
     * Calculate chick position orbiting around mother
     */
    private fun calculateChickPosition(index: Int, total: Int): Offset {
        val angle = (2 * Math.PI * index / total).toFloat()
        val radius = 0.05f
        return Offset(cos(angle) * radius, sin(angle) * radius)
    }

    private fun calculateAgeWeeks(birthDate: Long?): Int {
        if (birthDate == null) return 0
        val now = System.currentTimeMillis()
        val diff = now - birthDate
        return (diff / (7 * 24 * 60 * 60 * 1000L)).toInt()
    }

    private suspend fun calculateStats(products: List<ProductEntity>, farmState: DigitalFarmState): FarmStats {
        val totalEggsToday = farmState.breedingUnits.sumOf { it.eggsCollectedToday }
        val vaccinesDue = products.count { product ->
            // Check if vaccine is due today (simplified logic)
            product.vaccinationRecordsJson?.contains("due") == true
        }
        
        // Calculate batches (unique batchIds)
        val totalBatches = products.mapNotNull { it.batchId }.distinct().size
        
        // Calculate feed usage (~120g per bird per day for gamecock breeds)
        val feedUsageKg = farmState.totalBirds * 0.12f
        
        // Get coin balance from gamification system
        val userId = currentUserProvider.userIdOrNull()
        val coinBalance = if (userId != null) {
            try {
                coinLedgerDao.userCoinBalance(userId)
            } catch (e: Exception) {
                Timber.w(e, "Failed to fetch coin balance, using default")
                0
            }
        } else 0
        
        return FarmStats(
            totalBirds = farmState.totalBirds,
            totalEggsToday = totalEggsToday,
            birdsReadyForSale = farmState.readyDisplay.size,
            vaccinesDueToday = vaccinesDue,
            coins = coinBalance,
            totalBatches = totalBatches,
            feedUsageKg = feedUsageKg
        )
    }

    // User interactions
    fun onBirdTapped(bird: VisualBird) {
        _selectedBird.value = bird
        viewModelScope.launch {
            _tapResult.emit(FarmTapResult.BirdTapped(bird))
        }
    }

    fun onNurseryTapped(nursery: NurseryGroup) {
        viewModelScope.launch {
            _tapResult.emit(FarmTapResult.NurseryTapped(nursery))
        }
    }

    fun onBreedingHutTapped(unit: BreedingUnit) {
        viewModelScope.launch {
            _tapResult.emit(FarmTapResult.BreedingHutTapped(unit))
        }
    }

    fun onMarketStandTapped() {
        viewModelScope.launch {
            _tapResult.emit(FarmTapResult.MarketStandTapped(_uiState.value.marketReady))
        }
    }
    
    /**
     * Lite Mode: Zone tapped - opens zone bottom sheet with batch summary
     */
    fun onZoneTapped(zone: DigitalFarmZone) {
        viewModelScope.launch {
            val birdsInZone = when (zone) {
                DigitalFarmZone.FREE_RANGE -> _uiState.value.freeRange
                DigitalFarmZone.GROW_OUT -> _uiState.value.growOut
                DigitalFarmZone.READY_DISPLAY -> _uiState.value.readyDisplay
                DigitalFarmZone.MARKET_STAND -> _uiState.value.marketReady
                DigitalFarmZone.NURSERY -> _uiState.value.nurseries.flatMap { it.chicks + it.mother }
                DigitalFarmZone.BREEDING_UNIT -> _uiState.value.breedingUnits.flatMap { 
                    listOfNotNull(it.rooster) + it.hens 
                }
            }
            _tapResult.emit(FarmTapResult.ZoneTapped(zone, birdsInZone))
        }
    }

    fun clearSelection() {
        _selectedBird.value = null
    }

    fun logEggsForUnit(unitId: String, eggCount: Int) {
        viewModelScope.launch {
            // TODO: Update database and refresh
            Timber.d("Logging $eggCount eggs for breeding unit $unitId")
        }
    }

    // Extension to convert ProductEntity to VisualBird
    private fun ProductEntity.toVisualBird(zone: DigitalFarmZone): VisualBird {
        val ageWeeks = this.ageWeeks ?: calculateAgeWeeks(this.birthDate)
        
        val statusIndicator = when {
            this.readyForSale || 
            (this.weightGrams != null && this.targetWeight != null && 
             this.weightGrams >= this.targetWeight) -> BirdStatusIndicator.WEIGHT_READY
            this.healthStatus?.lowercase() == "sick" -> BirdStatusIndicator.SICK
            // Add more status checks as needed
            else -> BirdStatusIndicator.NONE
        }
        
        return VisualBird(
            productId = this.productId,
            name = this.name,
            breed = this.breed,
            gender = this.gender,
            ageWeeks = ageWeeks,
            weightGrams = this.weightGrams,
            color = this.color,
            zone = zone,
            statusIndicator = statusIndicator,
            isQuarantined = this.healthStatus?.lowercase() in listOf("quarantined", "sick", "isolated")
        )
    }
}
