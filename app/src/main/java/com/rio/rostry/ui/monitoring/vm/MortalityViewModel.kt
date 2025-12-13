package com.rio.rostry.ui.monitoring.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.monitoring.MortalityRepository
import com.rio.rostry.data.database.entity.MortalityRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MortalityViewModel @Inject constructor(
    private val repo: MortalityRepository,
    private val productDao: com.rio.rostry.data.database.dao.ProductDao,
    private val productRepository: com.rio.rostry.data.repository.ProductRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    data class UiState(
        val records: List<MortalityRecordEntity> = emptyList(),
        val products: List<com.rio.rostry.data.database.entity.ProductEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        loadProducts()
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            productRepository.getProductsBySeller(farmerId).collect { res ->
                if (res is com.rio.rostry.utils.Resource.Success) {
                    _ui.update { it.copy(products = res.data ?: emptyList()) }
                }
            }
        }
    }

    fun record(
        productId: String?,
        causeCategory: String,
        circumstances: String?,
        ageWeeks: Int?,
        disposalMethod: String?,
        quantity: Int = 1,
        financialImpactInr: Double?
    ) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val rec = MortalityRecordEntity(
                deathId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                causeCategory = causeCategory,
                circumstances = circumstances,
                ageWeeks = ageWeeks,
                disposalMethod = disposalMethod,
                quantity = quantity,
                financialImpactInr = financialImpactInr
            )
            repo.insert(rec)
            
            if (productId != null) {
                val product = productRepository.findById(productId)
                if (product != null) {
                    if (product.isBatch == true) {
                        productDao.decrementQuantity(productId, quantity, System.currentTimeMillis())
                    } else {
                        // Individual bird died - mark as DEAD
                        productDao.updateLifecycleStatus(productId, "DEAD", System.currentTimeMillis())
                        productDao.decrementQuantity(productId, quantity, System.currentTimeMillis())
                    }
                } else {
                    // Fallback if product not found in repo cache (unlikely)
                    productDao.decrementQuantity(productId, quantity, System.currentTimeMillis())
                }
            }
        }
    }
}
