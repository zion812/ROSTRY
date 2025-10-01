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

    enum class WizardStep { BASICS, DETAILS, MEDIA, REVIEW }

    data class BasicInfoState(
        val category: Category = Category.Meat,
        val traceability: Traceability = Traceability.Traceable,
        val ageGroup: AgeGroup = AgeGroup.Grower,
        val title: String = "",
        val priceType: PriceType = PriceType.Fixed,
        val price: String = "",
        val auctionStartPrice: String = "",
        val availableFrom: String = "",
        val availableTo: String = ""
    )

    data class DetailsInfoState(
        val birthPlace: String = "",
        val birthDateMillis: Long? = null,
        val vaccination: String = "",
        val parentInfo: String = "",
        val weightText: String = "",
        val healthUri: String = "",
        val genderText: String = "",
        val sizeText: String = "",
        val colorPattern: String = "",
        val specialChars: String = "",
        val breedingHistory: String = "",
        val provenPairs: String = "",
        val geneticTraits: String = "",
        val awards: String = "",
        val lineageDoc: String = "",
        val healthRecordDateMillis: Long? = null,
        val latitude: Double? = null,
        val longitude: Double? = null
    )

    data class MediaInfoState(
        val photoUris: List<String> = emptyList(),
        val videoUris: List<String> = emptyList(),
        val audioUris: List<String> = emptyList(),
        val documentUris: List<String> = emptyList()
    )

    data class ProductCreationState(
        val currentStep: WizardStep = WizardStep.BASICS,
        val basicInfo: BasicInfoState = BasicInfoState(),
        val detailsInfo: DetailsInfoState = DetailsInfoState(),
        val mediaInfo: MediaInfoState = MediaInfoState(),
        val validationErrors: Map<String, String> = emptyMap(),
        val isSubmitting: Boolean = false,
        val submitSuccess: Boolean = false,
        val successProductId: String? = null,
        val error: String? = null
    )

    data class UiState(
        val wizardState: ProductCreationState = ProductCreationState(),
        val isSubmitting: Boolean = false,
        val successProductId: String? = null,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun nextStep() {
        val current = _ui.value.wizardState
        val errors = validateStep(current.currentStep)
        if (errors.isNotEmpty()) {
            _ui.value = _ui.value.copy(
                wizardState = current.copy(validationErrors = errors)
            )
            return
        }
        val next = when (current.currentStep) {
            WizardStep.BASICS -> WizardStep.DETAILS
            WizardStep.DETAILS -> WizardStep.MEDIA
            WizardStep.MEDIA -> WizardStep.REVIEW
            WizardStep.REVIEW -> WizardStep.REVIEW
        }
        _ui.value = _ui.value.copy(
            wizardState = current.copy(currentStep = next, validationErrors = emptyMap())
        )
    }

    fun previousStep() {
        val current = _ui.value.wizardState
        val prev = when (current.currentStep) {
            WizardStep.BASICS -> WizardStep.BASICS
            WizardStep.DETAILS -> WizardStep.BASICS
            WizardStep.MEDIA -> WizardStep.DETAILS
            WizardStep.REVIEW -> WizardStep.MEDIA
        }
        _ui.value = _ui.value.copy(
            wizardState = current.copy(currentStep = prev, validationErrors = emptyMap())
        )
    }

    fun updateBasicInfo(transform: (BasicInfoState) -> BasicInfoState) {
        val current = _ui.value.wizardState
        _ui.value = _ui.value.copy(
            wizardState = current.copy(basicInfo = transform(current.basicInfo))
        )
    }

    fun updateDetails(transform: (DetailsInfoState) -> DetailsInfoState) {
        val current = _ui.value.wizardState
        _ui.value = _ui.value.copy(
            wizardState = current.copy(detailsInfo = transform(current.detailsInfo))
        )
    }

    fun addMedia(type: String, uris: List<String>) {
        val current = _ui.value.wizardState.mediaInfo
        val updated = when (type) {
            "photo" -> current.copy(photoUris = uris.take(12))
            "video" -> current.copy(videoUris = uris.take(2))
            "audio" -> current.copy(audioUris = uris)
            "document" -> current.copy(documentUris = uris)
            else -> current
        }
        _ui.value = _ui.value.copy(
            wizardState = _ui.value.wizardState.copy(mediaInfo = updated)
        )
    }

    fun removeMedia(type: String, index: Int) {
        val current = _ui.value.wizardState.mediaInfo
        val updated = when (type) {
            "photo" -> current.copy(photoUris = current.photoUris.filterIndexed { i, _ -> i != index })
            "video" -> current.copy(videoUris = current.videoUris.filterIndexed { i, _ -> i != index })
            "audio" -> current.copy(audioUris = current.audioUris.filterIndexed { i, _ -> i != index })
            "document" -> current.copy(documentUris = current.documentUris.filterIndexed { i, _ -> i != index })
            else -> current
        }
        _ui.value = _ui.value.copy(
            wizardState = _ui.value.wizardState.copy(mediaInfo = updated)
        )
    }

    fun autoDetectLocation() {
        // Location detection logic would go here
        // For now, placeholder
    }

    fun validateStep(step: WizardStep): Map<String, String> {
        val state = _ui.value.wizardState
        return when (step) {
            WizardStep.BASICS -> buildMap {
                if (state.basicInfo.title.isBlank()) put("title", "Title is required")
                val price = state.basicInfo.price.toDoubleOrNull()
                if (state.basicInfo.priceType == PriceType.Fixed && price == null) {
                    put("price", "Enter valid price")
                }
                val auction = state.basicInfo.auctionStartPrice.toDoubleOrNull()
                if (state.basicInfo.priceType == PriceType.Auction && auction == null) {
                    put("auctionStartPrice", "Enter valid start price")
                }
                if (state.basicInfo.availableFrom.isBlank()) put("availableFrom", "Start date required")
                if (state.basicInfo.availableTo.isBlank()) put("availableTo", "End date required")
            }
            WizardStep.DETAILS -> emptyMap()
            WizardStep.MEDIA -> emptyMap()
            WizardStep.REVIEW -> emptyMap()
        }
    }

    fun submitWizardListing() {
        val state = _ui.value.wizardState
        val form = com.rio.rostry.ui.farmer.ListingForm(
            category = state.basicInfo.category,
            traceability = state.basicInfo.traceability,
            ageGroup = state.basicInfo.ageGroup,
            title = state.basicInfo.title,
            priceType = state.basicInfo.priceType,
            price = state.basicInfo.price.toDoubleOrNull(),
            auctionStartPrice = state.basicInfo.auctionStartPrice.toDoubleOrNull(),
            availableFrom = state.basicInfo.availableFrom,
            availableTo = state.basicInfo.availableTo,
            healthRecordUri = state.detailsInfo.healthUri.ifBlank { null },
            birthDateMillis = state.detailsInfo.birthDateMillis,
            birthPlace = state.detailsInfo.birthPlace.ifBlank { null },
            vaccinationRecords = state.detailsInfo.vaccination.ifBlank { null },
            parentInfo = state.detailsInfo.parentInfo.ifBlank { null },
            weightGrams = state.detailsInfo.weightText.toDoubleOrNull(),
            photoUris = state.mediaInfo.photoUris,
            videoUris = state.mediaInfo.videoUris,
            latitude = state.detailsInfo.latitude,
            longitude = state.detailsInfo.longitude
        )
        submitListing(form)
    }

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
