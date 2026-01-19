package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class BreedingPairWithProducts(
    val pair: BreedingPairEntity,
    val maleProduct: ProductEntity?,
    val femaleProduct: ProductEntity?
)

@HiltViewModel
class BreedingManagementViewModel @Inject constructor(
    private val breedingRepository: BreedingRepository,
    private val productRepository: ProductRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val compatibilityCalculator: com.rio.rostry.domain.breeding.BreedingCompatibilityCalculator
) : ViewModel() {

    private val farmerId = flow { firebaseAuth.currentUser?.uid?.let { emit(it) } }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val breedingPairs: StateFlow<List<BreedingPairEntity>> = farmerId
        .flatMapLatest { id ->
            breedingRepository.observeActive(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products: StateFlow<List<ProductEntity>> = _products.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            productRepository.getProductsBySeller(farmerId).collect { res ->
                if (res is Resource.Success) {
                    _products.value = res.data ?: emptyList()
                }
            }
        }
    }

    fun addPair(
        maleProductId: String,
        femaleProductId: String,
        pairedAt: Long,
        notes: String?
    ) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            _error.value = null

            // Basic validations
            if (maleProductId == femaleProductId) {
                _error.value = "Cannot pair the same product"
                return@launch
            }
            
            // Validate products exist
            val maleProduct = productRepository.findById(maleProductId)
            val femaleProduct = productRepository.findById(femaleProductId)
            
            if (maleProduct == null || femaleProduct == null) {
                _error.value = "Invalid product IDs"
                return@launch
            }

            if (maleProduct.lifecycleStatus != "ACTIVE" || femaleProduct.lifecycleStatus != "ACTIVE") {
                _error.value = "Both products must be ACTIVE"
                return@launch
            }

            // Prevent duplicate active pair with same members (order-insensitive)
            val activePairs = breedingRepository.observeActive(farmerId).first()
            val dup = activePairs.any { p ->
                (p.maleProductId == maleProductId && p.femaleProductId == femaleProductId) ||
                (p.maleProductId == femaleProductId && p.femaleProductId == maleProductId)
            }
            if (dup) {
                _error.value = "Pair already exists"
                return@launch
            }

            val pair = BreedingPairEntity(
                pairId = UUID.randomUUID().toString(),
                farmerId = farmerId,
                maleProductId = maleProductId,
                femaleProductId = femaleProductId,
                pairedAt = pairedAt,
                status = "ACTIVE",
                eggsCollected = 0,
                hatchSuccessRate = 0.0,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )

            breedingRepository.upsert(pair)

            // Promote birds to BREEDER stage
            val now = System.currentTimeMillis()
            productRepository.updateStage(maleProductId, com.rio.rostry.domain.model.LifecycleStage.BREEDER, now)
            productRepository.updateStage(femaleProductId, com.rio.rostry.domain.model.LifecycleStage.BREEDER, now)
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun retirePair(pairId: String) {
        viewModelScope.launch {
            val pair = breedingRepository.getById(pairId) ?: return@launch
            
            val updatedPair = pair.copy(
                status = "RETIRED",
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )

            breedingRepository.upsert(updatedPair)
        }
    }

    fun updatePair(
        pairId: String,
        eggsCollected: Int,
        hatchSuccessRate: Double,
        notes: String?
    ) {
        viewModelScope.launch {
            val pair = breedingRepository.getById(pairId) ?: return@launch
            
            val updatedPair = pair.copy(
                eggsCollected = eggsCollected,
                hatchSuccessRate = hatchSuccessRate,
                notes = notes,
                updatedAt = System.currentTimeMillis(),
                dirty = true
            )

            breedingRepository.upsert(updatedPair)
        }
    }

    fun getPairDetails(pairId: String): Flow<BreedingPairWithProducts?> = flow {
        val pair = breedingRepository.getById(pairId)
        if (pair != null) {
            val maleProduct = when (val maleResult = productRepository.getProductById(pair.maleProductId).first()) {
                is com.rio.rostry.utils.Resource.Success -> maleResult.data
                else -> null
            }
            val femaleProduct = when (val femaleResult = productRepository.getProductById(pair.femaleProductId).first()) {
                is com.rio.rostry.utils.Resource.Success -> femaleResult.data
                else -> null
            }
            emit(BreedingPairWithProducts(pair, maleProduct, femaleProduct))
        } else {
            emit(null)
        }
    }

    suspend fun checkCompatibility(maleId: String, femaleId: String): com.rio.rostry.domain.breeding.BreedingCompatibilityCalculator.CompatibilityResult? {
        val male = productRepository.findById(maleId) ?: return null
        val female = productRepository.findById(femaleId) ?: return null
        return compatibilityCalculator.calculateCompatibility(male, female)
    }
}
