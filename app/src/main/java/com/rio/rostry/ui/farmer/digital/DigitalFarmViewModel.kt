package com.rio.rostry.ui.farmer.digital

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.*
import com.rio.rostry.session.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

/**
 * Enhanced DigitalFarmViewModel for the 2.5D Digital Farm.
 *
 * Implements rendering rules:
 * - Rule 1: age < 4w + motherId -> NURSERY, orbit mother
 * - Rule 2: isBreedingBatch -> BREEDING_UNIT, group in hut
 * - Rule 3: readyForSale + male -> READY_DISPLAY, in cage
 * - Rule 4: listed -> MARKET_STAND
 * - Default: FREE_RANGE
 */
@HiltViewModel
class EnhancedDigitalFarmViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val farmAssetRepository: FarmAssetRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _farmState = MutableStateFlow(DigitalFarmState(isLoading = true))
    val farmState: StateFlow<DigitalFarmState> = _farmState

    private val _farmStats = MutableStateFlow(FarmStats())
    val farmStats: StateFlow<FarmStats> = _farmStats

    init {
        loadFarmData()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun loadFarmData() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull()
            if (userId == null) {
                _farmState.value = DigitalFarmState(isLoading = false, error = "Not logged in")
                return@launch
            }

            try {
                combine(
                    productRepository.getProductsBySeller(userId),
                    farmAssetRepository.getAssetsByFarmer(userId)
                ) { productsRes, assetsRes ->
                    val products = productsRes.data ?: emptyList()
                    val assets = assetsRes.data ?: emptyList()

                    // Map products to VisualBirds with rendering rules
                    val nurseries = mutableListOf<NurseryGroup>()
                    val breedingUnits = mutableListOf<BreedingUnit>()
                    val freeRange = mutableListOf<VisualBird>()
                    val growOut = mutableListOf<VisualBird>()
                    val readyDisplay = mutableListOf<VisualBird>()
                    val marketReady = mutableListOf<VisualBird>()

                    // Group breeding batches
                    val batchGroups = products.filter { it.isBreedingUnit == true && it.batchId != null }
                        .groupBy { it.batchId }

                    batchGroups.forEach { (batchId, birds) ->
                        if (batchId != null) {
                            val rooster = birds.find { it.gender?.equals("male", true) == true }
                            val hens = birds.filter { it.gender?.equals("female", true) == true }
                            val eggsToday = birds.sumOf { it.eggsCollectedToday ?: 0 }
                            val hutPos = IsometricGrid.getZoneCenter(DigitalFarmZone.BREEDING_UNIT)

                            breedingUnits.add(BreedingUnit(
                                unitId = batchId,
                                rooster = rooster?.toVisualBird(DigitalFarmZone.BREEDING_UNIT, hutPos),
                                hens = hens.map { it.toVisualBird(DigitalFarmZone.BREEDING_UNIT, hutPos) },
                                eggsCollectedToday = eggsToday,
                                lastEggLogDate = null, // Could be derived from egg collection records
                                hutPosition = hutPos
                            ))
                        }
                    }

                    // Process remaining products
                    val batchBirdIds = batchGroups.values.flatten().map { it.productId }.toSet()
                    val nonBatchBirds = products.filterNot { it.productId in batchBirdIds }

                    // Group mothers with chicks for Nursery
                    val mothers = nonBatchBirds.filter { bird ->
                        nonBatchBirds.any { it.motherId == bird.productId }
                    }
                    val chicks = nonBatchBirds.filter { it.motherId != null && (it.ageWeeks ?: 0) < 4 }

                    mothers.forEach { mother ->
                        val motherChicks = chicks.filter { it.motherId == mother.productId }
                        if (motherChicks.isNotEmpty()) {
                            val nestPos = IsometricGrid.getRandomPositionInZone(DigitalFarmZone.NURSERY)
                            nurseries.add(NurseryGroup(
                                mother = mother.toVisualBird(DigitalFarmZone.NURSERY, nestPos),
                                chicks = motherChicks.map { it.toVisualBird(DigitalFarmZone.NURSERY, nestPos) },
                                nestPosition = nestPos
                            ))
                        }
                    }

                    val chicksWithMothersIds = chicks.map { it.productId }.toSet()
                    val mothersIds = mothers.map { it.productId }.toSet()

                    // Remaining birds
                    val remainingBirds = nonBatchBirds.filterNot {
                        it.productId in chicksWithMothersIds || it.productId in mothersIds
                    }

                    remainingBirds.forEach { bird ->
                        when {
                            // Rule 3: Ready for sale male -> READY_DISPLAY (cage)
                            bird.readyForSale == true && bird.gender?.equals("male", true) == true -> {
                                val pos = IsometricGrid.getRandomPositionInZone(DigitalFarmZone.READY_DISPLAY)
                                readyDisplay.add(bird.toVisualBird(DigitalFarmZone.READY_DISPLAY, pos))
                            }
                            // Rule 4: Listed -> MARKET_STAND
                            bird.lifecycleStatus?.equals("LISTED", true) == true -> {
                                val pos = IsometricGrid.getRandomPositionInZone(DigitalFarmZone.MARKET_STAND)
                                marketReady.add(bird.toVisualBird(DigitalFarmZone.MARKET_STAND, pos))
                            }
                            // Default: FREE_RANGE based on age
                            (bird.ageWeeks ?: 0) in 4..12 -> {
                                val pos = IsometricGrid.getRandomPositionInZone(DigitalFarmZone.FREE_RANGE)
                                freeRange.add(bird.toVisualBird(DigitalFarmZone.FREE_RANGE, pos))
                            }
                            // Grow-out for older birds
                            (bird.ageWeeks ?: 0) > 12 -> {
                                val pos = IsometricGrid.getRandomPositionInZone(DigitalFarmZone.GROW_OUT)
                                growOut.add(bird.toVisualBird(DigitalFarmZone.GROW_OUT, pos))
                            }
                            else -> {
                                // Young birds without mother -> Free Range
                                val pos = IsometricGrid.getRandomPositionInZone(DigitalFarmZone.FREE_RANGE)
                                freeRange.add(bird.toVisualBird(DigitalFarmZone.FREE_RANGE, pos))
                            }
                        }
                    }

                    // Update stats
                    val totalBirds = nurseries.sumOf { it.chicks.size + 1 } +
                            breedingUnits.sumOf { (if (it.rooster != null) 1 else 0) + it.hens.size } +
                            freeRange.size + growOut.size + readyDisplay.size + marketReady.size
                    val eggsToday = breedingUnits.sumOf { it.eggsCollectedToday }

                    _farmStats.value = FarmStats(
                        totalBirds = totalBirds,
                        totalEggsToday = eggsToday,
                        birdsReadyForSale = readyDisplay.size,
                        vaccinesDueToday = 0, // Could be calculated from vaccination records
                        coins = 0
                    )

                    DigitalFarmState(
                        nurseries = nurseries,
                        breedingUnits = breedingUnits,
                        freeRange = freeRange,
                        growOut = growOut,
                        readyDisplay = readyDisplay,
                        marketReady = marketReady,
                        isLoading = false
                    )
                }.collect { state ->
                    _farmState.value = state
                }
            } catch (e: Exception) {
                _farmState.value = DigitalFarmState(isLoading = false, error = e.message)
            }
        }
    }

    /**
     * List a bird for sale (drag to market stand).
     */
    fun onListedForSale(birdId: String) {
        viewModelScope.launch {
            productRepository.getProductById(birdId).collect { resource ->
                resource.data?.let { product ->
                    val updated = product.copy(lifecycleStatus = "LISTED")
                    productRepository.updateProduct(updated)
                }
            }
        }
    }

    /**
     * Map ProductEntity to VisualBird with zone and position.
     */
    private fun ProductEntity.toVisualBird(zone: DigitalFarmZone, position: Offset): VisualBird {
        val statusIndicator = when {
            (this.weightGrams ?: 0.0) > 2000 && this.gender?.equals("male", true) == true -> BirdStatusIndicator.WEIGHT_READY
            this.healthStatus?.lowercase() != "ok" -> BirdStatusIndicator.SICK
            else -> BirdStatusIndicator.NONE
        }

        return VisualBird(
            productId = this.productId,
            name = this.name,
            breed = this.breed,
            gender = this.gender,
            ageWeeks = this.ageWeeks ?: 0,
            weightGrams = this.weightGrams,
            color = this.color,
            zone = zone,
            statusIndicator = statusIndicator,
            position = position,
            isSelected = false,
            currentAnimationTarget = position,
            isDragging = false,
            motherId = this.motherId,
            batchId = this.batchId,
            isReadyForSale = this.readyForSale == true,
            isListed = this.lifecycleStatus?.equals("LISTED", true) == true
        )
    }
}
