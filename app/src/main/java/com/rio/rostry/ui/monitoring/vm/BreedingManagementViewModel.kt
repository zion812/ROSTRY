package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.entity.BreedingPairEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.monitoring.BreedingRepository
import com.rio.rostry.session.SessionManager
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
    private val productDao: ProductDao,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
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
            val maleProduct = productDao.findById(maleProductId)
            val femaleProduct = productDao.findById(femaleProductId)
            
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
            val maleProduct = productDao.getProductById(pair.maleProductId).first()
            val femaleProduct = productDao.getProductById(pair.femaleProductId).first()
            emit(BreedingPairWithProducts(pair, maleProduct, femaleProduct))
        } else {
            emit(null)
        }
    }
}
