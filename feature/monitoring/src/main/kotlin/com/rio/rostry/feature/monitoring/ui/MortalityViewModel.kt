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
    private val farmAssetRepository: com.rio.rostry.data.repository.FarmAssetRepository,
    private val activityLogRepository: com.rio.rostry.data.repository.FarmActivityLogRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    data class UiState(
        val records: List<MortalityRecordEntity> = emptyList(),
        val assets: List<com.rio.rostry.data.database.entity.FarmAssetEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        loadAssets()
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    private fun loadAssets() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            farmAssetRepository.getAssetsByFarmer(farmerId).collect { res ->
                if (res is com.rio.rostry.utils.Resource.Success) {
                    _ui.update { it.copy(assets = res.data ?: emptyList()) }
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
            
            // 1. Save Mortality Record
            val rec = MortalityRecordEntity(
                deathId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                causeCategory = causeCategory,
                circumstances = circumstances,
                ageWeeks = ageWeeks,
                disposalMethod = disposalMethod,
                quantity = quantity,
                financialImpactInr = financialImpactInr,
                occurredAt = System.currentTimeMillis()
            )
            repo.insert(rec)
            
            // 2. Update Farm Asset (Decrement or Status Change)
            var assetName: String? = null
            if (productId != null) {
                // Fetch asset to check type/current quantity
                // Since repo operations are Flow based, we might need a direct fetch or take(1)
                // Assuming we can get it from our local cache for simplicity or fetch again
                val asset = _ui.value.assets.find { it.assetId == productId }
                assetName = asset?.name
                
                if (asset != null) {
                    // Calculate new quantity
                    val newQuantity = (asset.quantity - quantity).coerceAtLeast(0.0)
                    
                    // If individual (not batch) and quantity is 0, mark as DEAD & ARCHIVED
                    if (newQuantity == 0.0) {
                        // For both Individual and Batch, if quantity is 0, it's effectively gone.
                        // Mark as ARCHIVED to hide from "Active" lists.
                        // Also update health status to DEAD for record keeping.
                        val updatedAsset = asset.copy(
                            quantity = newQuantity,
                            status = "ARCHIVED", 
                            healthStatus = "DEAD",
                            dirty = true,
                            updatedAt = System.currentTimeMillis()
                        )
                        farmAssetRepository.updateAsset(updatedAsset)
                    } else {
                        // Just update quantity if not depleted
                         farmAssetRepository.updateQuantity(productId, newQuantity)
                    }
                }
            }
            
            // 3. Log to Farm Activity Log (This triggers Calendar and Farm Log)
            try {
                activityLogRepository.logActivity(
                    farmerId = farmerId,
                    productId = productId,
                    activityType = "MORTALITY",
                    amount = financialImpactInr, // Financial impact as amount? Or maybe 0.
                    quantity = quantity.toDouble(),
                    category = "Health - Mortality",
                    description = "Mortality recorded: $quantity ${assetName ?: "Birds"}",
                    notes = "Cause: $causeCategory. ${circumstances ?: ""}"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
