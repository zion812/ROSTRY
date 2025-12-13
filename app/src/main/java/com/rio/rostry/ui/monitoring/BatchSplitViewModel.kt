package com.rio.rostry.ui.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
import com.rio.rostry.utils.BirdIdGenerator
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class BatchSplitViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val farmOnboardingRepository: FarmOnboardingRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val analyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    data class UiState(
        val batch: ProductEntity? = null,
        val birds: List<BirdForm> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val validationErrors: Map<String, String> = emptyMap(),
        val navigationEvent: NavigationEvent? = null
    )

    data class BirdForm(
        val name: String = "",
        val gender: String? = null,
        val weight: Double? = null,
        val photoUri: String? = null
    )

    sealed class NavigationEvent {
        object NavigateToFarmMonitoring : NavigationEvent()
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadBatch(batchId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = productRepository.getProductById(batchId).first()) {
                is Resource.Success -> {
                    val batch = result.data
                    if (batch != null && isEligibleForSplit(batch)) {
                        val initialBirds = List(batch.quantity?.toInt() ?: 0) { BirdForm() }
                        _uiState.update {
                            it.copy(
                                batch = batch,
                                birds = initialBirds,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                error = "Batch is not eligible for splitting. Must be at least 12 weeks old and active.",
                                isLoading = false
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            error = result.message ?: "Failed to load batch details",
                            isLoading = false
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun isEligibleForSplit(batch: ProductEntity): Boolean {
        val now = System.currentTimeMillis()
        val ageDays = batch.birthDate?.let { (now - it) / TimeUnit.DAYS.toMillis(1) } ?: 0
        return ageDays >= 84 && batch.status == "ACTIVE"
    }

    fun updateBirdForm(index: Int, birdForm: BirdForm) {
        val updatedBirds = _uiState.value.birds.toMutableList()
        if (index in updatedBirds.indices) {
            updatedBirds[index] = birdForm
            _uiState.update { it.copy(birds = updatedBirds) }
        }
    }

    fun splitBatch() {
        val batch = _uiState.value.batch ?: return
        val birds = _uiState.value.birds
        val farmerId = firebaseAuth.currentUser?.uid ?: return

        // Validate forms
        val validationErrors = mutableMapOf<String, String>()
        birds.forEachIndexed { index, bird ->
            if (bird.name.isBlank()) {
                validationErrors["bird_$index"] = "Name is required"
            }
            if (bird.weight == null || bird.weight <= 0) {
                validationErrors["bird_${index}_weight"] = "Valid weight is required"
            }
        }
        if (validationErrors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = validationErrors) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, validationErrors = emptyMap()) }

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val newProductIds = mutableListOf<String>()

                // Create individual products
                birds.forEach { bird ->
                    val productId = UUID.randomUUID().toString()
                    val color = batch.color?.ifBlank { null }
                    val breed = batch.breed?.ifBlank { null }
                    val birdCode = BirdIdGenerator.generate(color, breed, batch.sellerId, productId)
                    val colorTag = BirdIdGenerator.colorTag(color)
                    val individualProduct = ProductEntity(
                        productId = productId,
                        sellerId = batch.sellerId,
                        name = bird.name,
                        description = "Individual bird from batch ${batch.name}",
                        category = batch.category,
                        breed = batch.breed,
                        birthDate = batch.birthDate,
                        ageWeeks = batch.ageWeeks,
                        weightGrams = bird.weight,
                        heightCm = batch.heightCm,
                        gender = bird.gender,
                        quantity = 1.0,
                        price = (batch.price / (batch.quantity.takeIf { it != 0.0 } ?: 1.0)),
                        location = batch.location,
                        latitude = batch.latitude,
                        longitude = batch.longitude,
                        imageUrls = bird.photoUri?.let { listOf(it) } ?: emptyList(),
                        isBatch = false,
                        status = "private",
                        createdAt = now,
                        updatedAt = now,
                        lastModifiedAt = now,
                        dirty = true,
                        birdCode = birdCode,
                        colorTag = colorTag
                    )
                    productRepository.upsert(individualProduct)
                    newProductIds.add(productId)
                }

                // Update batch status
                val updatedBatch = batch.copy(
                    status = "SPLIT",
                    splitAt = now,
                    updatedAt = now,
                    lastModifiedAt = now,
                    splitIntoIds = (newProductIds.joinToString(",")),
                    dirty = true
                )
                productRepository.updateProduct(updatedBatch)

                // Generate monitoring records for each new bird
                newProductIds.forEach { productId ->
                    farmOnboardingRepository.addProductToFarmMonitoring(productId, farmerId)
                }

                // Track analytics
                analyticsTracker.trackEvent(
                    event = "batch_split_completed",
                    properties = mapOf(
                        "batch_id" to batch.productId,
                        "birds_created" to newProductIds.size,
                        "farmer_id" to farmerId,
                        "completion_time" to now
                    )
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationEvent = NavigationEvent.NavigateToFarmMonitoring
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to split batch: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearNavigationEvent() {
        _uiState.update { it.copy(navigationEvent = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
