package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.database.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class FarmerCreateViewModel @Inject constructor(
    private val marketplace: ProductMarketplaceRepository,
    private val userRepository: UserRepository,
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
                val product = mapToEntity(currentUser.userId, form)
                when (val res = marketplace.createProduct(product)) {
                    is Resource.Success -> _ui.value = UiState(isSubmitting = false, successProductId = res.data)
                    is Resource.Error -> _ui.value = UiState(isSubmitting = false, error = res.message ?: "Failed to publish")
                    is Resource.Loading -> _ui.value = UiState(isSubmitting = true)
                }
            } catch (e: Exception) {
                _ui.value = UiState(isSubmitting = false, error = e.message ?: "Unexpected error")
            }
        }
    }

    private fun mapToEntity(sellerId: String, form: ListingForm): ProductEntity {
        val now = System.currentTimeMillis()
        val name = form.title.ifBlank { "Listing" }
        val price = when (form.priceType) {
            PriceType.Fixed -> form.price ?: 0.0
            PriceType.Auction -> form.auctionStartPrice ?: 0.0
        }
        val categoryText = when (form.category) {
            Category.Meat -> "Meat"
            Category.Adoption -> "Adoption"
        }
        val traceable = when (form.traceability) {
            Traceability.Traceable -> true
            Traceability.NonTraceable -> false
        }
        val birthDate: Long? = null // advanced: derive from age group if needed
        val status = "available"
        return ProductEntity(
            productId = "",
            sellerId = sellerId,
            name = name,
            description = "",
            category = categoryText,
            price = price,
            quantity = 1.0,
            unit = "piece",
            location = "",
            latitude = null,
            longitude = null,
            imageUrls = emptyList(),
            status = status,
            condition = if (traceable) "traceable" else null,
            harvestDate = null,
            expiryDate = null,
            birthDate = birthDate,
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

    private suspend fun getCurrentUserOrNull(): UserEntity? {
        return when (val res = userRepository.getCurrentUser().first()) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }
}
