package com.rio.rostry.ui.farmer

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.CompressionUtils
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.database.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class FarmerCreateViewModel @Inject constructor(
    private val marketplace: ProductMarketplaceRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    data class UiState(
        val isSubmitting: Boolean = false,
        val successProductId: String? = null,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun submitListing(form: ListingForm) {
        if (_ui.value.isSubmitting) return
        viewModelScope.launch {
            _ui.value = UiState(isSubmitting = true)
            // Fetch current user to populate sellerId and location
            val currentUser = getCurrentUserOrNull()
            if (currentUser == null) {
                _ui.value = UiState(isSubmitting = false, error = "Not authenticated")
                return@launch
            }
            try {
                val product = mapToEntity(currentUser, form)
                val imageBytes = resolveAndCompress(form.photoUris)
                val baseProduct = if (imageBytes.isNotEmpty()) product.copy(imageUrls = emptyList()) else product
                when (val res = marketplace.createProduct(baseProduct, imageBytes)) {
                    is Resource.Success -> _ui.value = UiState(isSubmitting = false, successProductId = res.data)
                    is Resource.Error -> _ui.value = UiState(isSubmitting = false, error = res.message ?: "Failed to publish")
                    is Resource.Loading -> _ui.value = UiState(isSubmitting = true)
                }
            } catch (e: Exception) {
                _ui.value = UiState(isSubmitting = false, error = e.message ?: "Unexpected error")
            }
        }
    }

    private fun mapToEntity(user: UserEntity, form: ListingForm): ProductEntity {
        val now = System.currentTimeMillis()
        val name = form.title.ifBlank { "Listing" }
        val price = when (form.priceType) {
            PriceType.Fixed -> form.price ?: 0.0
            PriceType.Auction -> form.auctionStartPrice ?: 0.0
        }
        val categoryText = when (form.category) {
            Category.Meat -> "MEAT"
            Category.Adoption -> when (form.traceability) {
                Traceability.Traceable -> "ADOPTION_TRACEABLE"
                Traceability.NonTraceable -> "ADOPTION_NON_TRACEABLE"
            }
        }
        val traceable = when (form.traceability) {
            Traceability.Traceable -> true
            Traceability.NonTraceable -> false
        }
        // Derive birthDate approximation by selected age group for validation
        val dayMs = 24L * 60 * 60 * 1000
        val birthDate: Long? = when (form.ageGroup) {
            AgeGroup.Chick -> now - 10L * dayMs
            AgeGroup.Grower -> now - 60L * dayMs
            AgeGroup.Adult -> now - 200L * dayMs
            AgeGroup.Senior -> now - 400L * dayMs
        }
        val status = "available"
        return ProductEntity(
            productId = "",
            sellerId = user.userId,
            name = name,
            description = "",
            category = categoryText,
            price = price,
            quantity = 1.0,
            unit = "piece",
            location = user.address ?: "",
            latitude = user.farmLocationLat,
            longitude = user.farmLocationLng,
            // Use placeholders only if UI hasn't provided any media; replaced when we attach imageBytes
            imageUrls = if (form.photoUris.isNotEmpty()) emptyList() else listOf("placeholder://image1", "placeholder://image2"),
            status = status,
            condition = if (traceable) "traceable" else null,
            harvestDate = null,
            expiryDate = null,
            birthDate = form.birthDateMillis ?: birthDate,
            vaccinationRecordsJson = form.healthRecordUri,
            weightGrams = null,
            heightCm = null,
            gender = null,
            color = null,
            breed = null,
            familyTreeId = null,
            parentIdsJson = null,
            breedingStatus = null,
            transferHistoryJson = null,
            createdAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            isDeleted = false,
            deletedAt = null,
            dirty = true
        )
    }

    private fun resolveAndCompress(uris: List<String>): List<ByteArray> {
        if (uris.isEmpty()) return emptyList()
        val cr = appContext.contentResolver
        val list = mutableListOf<ByteArray>()
        for (s in uris) {
            try {
                val uri = Uri.parse(s)
                cr.openInputStream(uri)?.use { input ->
                    val raw = input.readBytes()
                    val compressed = CompressionUtils.compressImage(raw)
                    list += compressed
                }
            } catch (_: Exception) {
                // skip bad URIs
            }
        }
        return list
    }

    private suspend fun getCurrentUserOrNull(): UserEntity? {
        return when (val res = userRepository.getCurrentUser().first()) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }
}
