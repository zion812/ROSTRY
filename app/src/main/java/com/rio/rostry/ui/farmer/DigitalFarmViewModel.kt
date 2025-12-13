package com.rio.rostry.ui.farmer

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import kotlin.math.abs

sealed interface DigitalFarmUiState {
    object Loading : DigitalFarmUiState
    data class Success(
        val birds: List<VisualBird>,
        val isVerified: Boolean
    ) : DigitalFarmUiState
    object Error : DigitalFarmUiState
    object Empty : DigitalFarmUiState
}

@HiltViewModel
class DigitalFarmViewModel @Inject constructor(
    productRepository: ProductRepository,
    userRepository: UserRepository
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DigitalFarmUiState> = userRepository.getCurrentUser()
        .flatMapLatest { userResource ->
            val user = userResource.data
            if (user == null || user.userId.isBlank()) {
                val loading = com.rio.rostry.utils.Resource.Loading<List<ProductEntity>>()
                flowOf(Pair(loading, false))
            } else {
                productRepository.getProductsBySeller(user.userId).map { productsResource ->
                    Pair(productsResource, user.verificationStatus == VerificationStatus.VERIFIED)
                }
            }
        }.map { (productsResource, isVerified) ->
            when {
                productsResource is com.rio.rostry.utils.Resource.Loading -> DigitalFarmUiState.Loading
                productsResource is com.rio.rostry.utils.Resource.Error -> DigitalFarmUiState.Error
                else -> {
                    val products = productsResource.data ?: emptyList()
                    if (products.isEmpty()) {
                        DigitalFarmUiState.Empty
                    } else {
                        val visualBirds = products
                            .filter {
                                val status = it.lifecycleStatus ?: "ACTIVE"
                                status == "ACTIVE" || status == "QUARANTINE"
                            }
                            .map { it.toVisualBird() }
                        DigitalFarmUiState.Success(visualBirds, isVerified)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DigitalFarmUiState.Loading
        )

    private fun ProductEntity.toVisualBird(): VisualBird {
        // Deterministic positioning based on ID hash
        val hash = this.productId.hashCode()
        val rawX = (abs(hash * 31) % 1000) / 1000f
        val rawY = (abs(hash * 17) % 1000) / 1000f

        // Zone Logic
        // Coop: Top-Left (x: 0.0-0.5, y: 0.0-0.5)
        // Quarantine: Top-Right (x: 0.5-1.0, y: 0.0-0.5)
        // Free Range: Bottom Half (x: 0.0-1.0, y: 0.5-1.0)
        
        // Force Quarantine zone if status is QUARANTINE
        val effectiveLocation = if (this.lifecycleStatus == "QUARANTINE") "Quarantine" else this.location

        val (finalX, finalY) = when (effectiveLocation) {
            "Coop" -> Pair(rawX * 0.5f, rawY * 0.5f)
            "Quarantine" -> Pair(0.5f + (rawX * 0.5f), rawY * 0.5f)
            "Free Range" -> Pair(rawX, 0.5f + (rawY * 0.5f))
            else -> Pair(rawX, 0.5f + (rawY * 0.5f)) // Default to Free Range
        }

        val stage = when {
            (this.ageWeeks ?: 0) < 5 -> BirdStage.CHICK
            (this.ageWeeks ?: 0) < 20 -> BirdStage.JUVENILE
            this.breedingStatus == "active" -> BirdStage.BREEDER // Simplified check
            else -> BirdStage.ADULT
        }

        val sizeRadius = when (stage) {
            BirdStage.CHICK -> 10f
            BirdStage.JUVENILE -> 15f
            BirdStage.ADULT -> 20f
            BirdStage.BREEDER -> 25f
        }

        val birdColor = parseColor(this.color)

        val status = when {
            (this.weightGrams ?: 0.0) > 2000 -> BirdHealthStatus.READY_TO_SELL
            (this.ageWeeks ?: 0) > 0 && this.vaccinationRecordsJson.isNullOrEmpty() -> BirdHealthStatus.VACCINE_DUE
            else -> BirdHealthStatus.NORMAL
        }

        return VisualBird(
            id = this.productId,
            color = birdColor,
            sizeRadius = sizeRadius,
            x = finalX,
            y = finalY,
            label = this.birdCode ?: this.name.take(4),
            stage = stage,
            details = "${this.name}\nAge: ${this.ageWeeks ?: 0} weeks\nWeight: ${this.weightGrams ?: 0}g\nLoc: ${this.location}\nHealth: ${this.healthStatus ?: "OK"}",
            location = this.location,
            isSick = (this.healthStatus ?: "OK").lowercase() != "ok",
            status = status,
            isNew = (System.currentTimeMillis() - this.createdAt) < 24 * 60 * 60 * 1000 // New if < 24 hours
        )
    }

    private fun parseColor(colorStr: String?): Color {
        if (colorStr.isNullOrBlank()) return Color.White
        return try {
            // Try parsing hex
            if (colorStr.startsWith("#")) {
                Color(android.graphics.Color.parseColor(colorStr))
            } else {
                // Fallback for named colors or unknown formats
                when (colorStr.lowercase()) {
                    "black" -> Color.Black
                    "white" -> Color.White
                    "brown" -> Color(0xFF795548)
                    "yellow" -> Color.Yellow
                    "red" -> Color.Red
                    else -> Color.White
                }
            }
        } catch (e: Exception) {
            Color.White
        }
    }
}
