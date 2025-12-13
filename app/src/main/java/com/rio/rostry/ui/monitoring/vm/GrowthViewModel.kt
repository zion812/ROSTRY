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
    private val analyticsRepository: com.rio.rostry.data.repository.analytics.AnalyticsRepository,
    private val productRepository: com.rio.rostry.data.repository.ProductRepository,
    private val mediaUploadManager: com.rio.rostry.utils.media.MediaUploadManager,
    @dagger.hilt.android.qualifiers.ApplicationContext private val appContext: android.content.Context
): ViewModel() {

    data class UiState(
        val records: List<GrowthRecordEntity> = emptyList(),
        val products: List<com.rio.rostry.data.database.entity.ProductEntity> = emptyList()
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        loadProducts()
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

    fun observe(productId: String) {
        viewModelScope.launch {
            repo.observe(productId).collect { list ->
                _ui.update { it.copy(records = list) }
            }
        }
    }

    fun recordToday(productId: String, weightGrams: Double?, heightCm: Double?, photoUri: String? = null) {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            
            // Calculate week from birth date
            val product = productRepository.findById(productId)
            val birthDate = product?.birthDate
            val now = System.currentTimeMillis()
            val week = if (birthDate != null) {
                com.rio.rostry.utils.LifecycleRules.calculateAgeInWeeks(birthDate, now)
            } else {
                0
            }

            // Handle photo upload
            var photoUrl: String? = null
            if (photoUri != null) {
                runCatching {
                    val input = appContext.contentResolver.openInputStream(android.net.Uri.parse(photoUri))
                        ?: throw IllegalStateException("Cannot open input stream")
                    val temp = java.io.File.createTempFile("growth_", ".jpg", appContext.cacheDir)
                    temp.outputStream().use { out -> input.copyTo(out) }
                    input.close()
                    val compressed = com.rio.rostry.utils.images.ImageCompressor.compressForUpload(appContext, temp, lowBandwidth = true)
                    
                    // Enqueue upload
                    val remotePath = "growth/${productId}/${UUID.randomUUID()}.jpg"
                    val ctxJson = com.google.gson.Gson().toJson(mapOf(
                        "type" to "growth_record",
                        "productId" to productId,
                        "at" to now
                    ))
                    mediaUploadManager.enqueueToOutbox(localPath = compressed.absolutePath, remotePath = remotePath, contextJson = ctxJson)
                    photoUrl = compressed.absolutePath // Use local path initially, will be updated by sync
                }.onFailure {
                    // Log error or handle failure
                }
            }

            val record = GrowthRecordEntity(
                recordId = UUID.randomUUID().toString(),
                productId = productId,
                farmerId = farmerId,
                week = week,
                weightGrams = weightGrams,
                heightCm = heightCm,
                photoUrl = photoUrl
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
