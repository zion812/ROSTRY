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
    private val listingDraftRepository: com.rio.rostry.data.repository.monitoring.ListingDraftRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    @ApplicationContext private val appContext: Context,
    // Farm monitoring repositories for prefill
    private val productRepository: com.rio.rostry.data.repository.ProductRepository,
    private val growthRepository: com.rio.rostry.data.repository.monitoring.GrowthRepository,
    private val vaccinationRepository: com.rio.rostry.data.repository.monitoring.VaccinationRepository,
    private val quarantineRepository: com.rio.rostry.data.repository.monitoring.QuarantineRepository,
    private val breedingRepository: com.rio.rostry.data.repository.monitoring.BreedingRepository,
    private val analyticsRepository: com.rio.rostry.data.repository.analytics.AnalyticsRepository,
    private val productValidator: com.rio.rostry.marketplace.validation.ProductValidator
) : ViewModel() {

    private var prefillProductId: String? = null

    init {
        loadDraft()
    }

    private fun loadDraft() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val draft = listingDraftRepository.getDraft(farmerId) ?: return@launch
            
            // Deserialize formDataJson to ProductCreationState
            try {
                val gson = com.google.gson.Gson()
                val wizardState = gson.fromJson(draft.formDataJson, ProductCreationState::class.java)
                _ui.value = _ui.value.copy(wizardState = wizardState)
            } catch (e: Exception) {
                // Log error but don't crash
            }
        }
    }

    // Debounced autosave for wizard state mutating calls
    private var draftDebounceJob: kotlinx.coroutines.Job? = null
    private fun debounceSaveDraft() {
        draftDebounceJob?.cancel()
        draftDebounceJob = viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            saveDraft()
        }
    }

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
        val heightCm: Double? = null,
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

    private fun saveDraft() {
        viewModelScope.launch {
            val farmerId = firebaseAuth.currentUser?.uid ?: return@launch
            val wizardState = _ui.value.wizardState
            
            try {
                val gson = com.google.gson.Gson()
                val formDataJson = gson.toJson(wizardState)
                
                val draft = com.rio.rostry.data.database.entity.ListingDraftEntity(
                    draftId = "${farmerId}_current",
                    farmerId = farmerId,
                    step = wizardState.currentStep.name,
                    formDataJson = formDataJson,
                    updatedAt = System.currentTimeMillis(),
                    expiresAt = System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(30)
                )
                
                listingDraftRepository.saveDraft(draft)
            } catch (e: Exception) {
                // Log but don't interrupt user flow
            }
        }
    }

    /**
     * Load farm monitoring data to prefill the listing wizard when coming from farm screens
     * Comment 1: Add guardrails for null/blank productId
     */
    fun loadPrefillData(productId: String?) {
        // Comment 1: Validate productId and show user-facing error if invalid
        if (productId.isNullOrBlank()) {
            _ui.value = _ui.value.copy(
                error = "Invalid product selection. Please list manually or try again."
            )
            return
        }
        
        prefillProductId = productId
        viewModelScope.launch {
            try {
                // Track analytics: prefill initiated
                val farmerId = firebaseAuth.currentUser?.uid
                if (farmerId != null) {
                    analyticsRepository.trackFarmToMarketplacePrefillInitiated(farmerId, productId)
                }
                
                // Check if product is in active quarantine (precompute for synchronous validation)
                val inQuarantine = quarantineRepository.observe(productId).first()
                    .any { it.status == "ACTIVE" }
                
                quarantineCheckPassed = !inQuarantine // Store for synchronous validation
                
                if (inQuarantine) {
                    _ui.value = _ui.value.copy(
                        error = "Cannot list products currently in quarantine. Complete quarantine protocol first."
                    )
                    return@launch
                }
                
                // Fetch product and farm monitoring data - unwrap Resource
                // Comment 13: Don't return on Loading - wait for Success/Error
                _ui.value = _ui.value.copy(isSubmitting = true) // Show loading
                
                val productResource = productRepository.getProductById(productId)
                    .first { it !is Resource.Loading } // Wait until not loading
                    
                val product = when (productResource) {
                    is Resource.Success -> productResource.data
                    is Resource.Error -> {
                        _ui.value = _ui.value.copy(
                            isSubmitting = false,
                            error = productResource.message ?: "Product not found"
                        )
                        return@launch
                    }
                    is Resource.Loading -> null // Shouldn't happen due to first{} filter
                }
                
                if (product == null) {
                    _ui.value = _ui.value.copy(
                        isSubmitting = false,
                        error = "Product not found. Please try again or list manually."
                    )
                    return@launch
                }
                
                val latestGrowth = growthRepository.observe(productId).first().lastOrNull()
                val vaccinations = vaccinationRepository.observe(productId).first()
                
                // Calculate age group from birth date
                val ageGroup = if (product.birthDate != null) {
                    val ageInDays = ((System.currentTimeMillis() - product.birthDate) / (24 * 60 * 60 * 1000)).toInt()
                    when {
                        ageInDays <= 30 -> AgeGroup.Chick
                        ageInDays <= 90 -> AgeGroup.Grower
                        ageInDays <= 365 -> AgeGroup.Adult
                        else -> AgeGroup.Senior
                    }
                } else AgeGroup.Grower
                
                // Map product category - only use existing enum values (Meat, Adoption)
                val category = when {
                    product.category?.startsWith("ADOPTION") == true -> Category.Adoption
                    else -> Category.Meat // Default to Meat for MEAT, EGGS, BREEDING, etc.
                }
                
                // Format vaccination summary
                val vaccinationSummary = vaccinations
                    .filter { it.administeredAt != null }
                    .joinToString(", ") { 
                        "${it.vaccineType} (${formatDate(it.administeredAt!!)})" 
                    }
                
                // Prefill wizard state
                val prefilled = _ui.value.wizardState.copy(
                    basicInfo = _ui.value.wizardState.basicInfo.copy(
                        title = product.name,
                        price = product.price.toString(),
                        ageGroup = ageGroup,
                        category = category,
                        traceability = if (product.familyTreeId != null) Traceability.Traceable else Traceability.NonTraceable
                    ),
                    detailsInfo = _ui.value.wizardState.detailsInfo.copy(
                        birthDateMillis = product.birthDate,
                        weightText = (latestGrowth?.weightGrams ?: product.weightGrams)?.toString() ?: "",
                        heightCm = latestGrowth?.heightCm,
                        vaccination = vaccinationSummary,
                        genderText = product.gender ?: "",
                        colorPattern = product.color ?: "",
                        latitude = product.latitude,
                        longitude = product.longitude
                    ),
                    mediaInfo = _ui.value.wizardState.mediaInfo.copy(
                        photoUris = product.imageUrls ?: emptyList()
                    )
                )
                
                _ui.value = _ui.value.copy(wizardState = prefilled)
                
                // Track analytics: prefill success
                if (farmerId == null) return@launch
                val fieldsCount = listOf(
                    prefilled.basicInfo.title.isNotBlank(),
                    prefilled.basicInfo.price.isNotBlank(),
                    prefilled.detailsInfo.birthDateMillis != null,
                    prefilled.detailsInfo.weightText.isNotBlank(),
                    prefilled.detailsInfo.vaccination.isNotBlank(),
                    prefilled.mediaInfo.photoUris.isNotEmpty()
                ).count { it }
                analyticsRepository.trackFarmToMarketplacePrefillSuccess(farmerId, productId, fieldsCount)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    error = "Failed to load farm data: ${e.message}"
                )
            }
        }
    }
    
    private fun formatDate(millis: Long): String {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date(millis))
    }
    
    /**
     * Load breeding pair context to enhance listing with breeding performance stats
     */
    fun loadBreedingContext(pairId: String?) {
        if (pairId == null) return
        
        viewModelScope.launch {
            try {
                val pair = breedingRepository.getById(pairId)
                if (pair == null || pair.status != "ACTIVE") {
                    return@launch
                }
                
                // Format breeding performance summary
                val pairedDate = formatDate(pair.pairedAt)
                val successRate = (pair.hatchSuccessRate * 100).toInt()
                val breedingSummary = "Proven breeding pair: ${pair.eggsCollected} eggs collected. " +
                    "Hatch success ${successRate}%. Paired on $pairedDate."
                
                // Update wizard state with breeding info
                val currentState = _ui.value.wizardState
                val updatedDetailsInfo = currentState.detailsInfo.copy(
                    breedingHistory = if (currentState.detailsInfo.breedingHistory.isBlank()) {
                        breedingSummary
                    } else {
                        currentState.detailsInfo.breedingHistory + "\n\n" + breedingSummary
                    }
                )
                
                // Optionally prepend [Proven Pair] to title for visibility
                val updatedBasicInfo = currentState.basicInfo.copy(
                    title = if (!currentState.basicInfo.title.startsWith("[Proven Pair]")) {
                        "[Proven Pair] ${currentState.basicInfo.title}"
                    } else {
                        currentState.basicInfo.title
                    }
                )
                
                _ui.value = _ui.value.copy(
                    wizardState = currentState.copy(
                        basicInfo = updatedBasicInfo,
                        detailsInfo = updatedDetailsInfo
                    )
                )
            } catch (e: Exception) {
                // Don't block prefill flow; breeding context is optional enhancement
                timber.log.Timber.w(e, "Failed to load breeding context for pairId: $pairId")
            }
        }
    }

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
        saveDraft()
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
        saveDraft()
    }

    fun updateBasicInfo(transform: (BasicInfoState) -> BasicInfoState) {
        val current = _ui.value.wizardState
        _ui.value = _ui.value.copy(
            wizardState = current.copy(basicInfo = transform(current.basicInfo))
        )
        debounceSaveDraft()
    }

    fun updateDetails(transform: (DetailsInfoState) -> DetailsInfoState) {
        val current = _ui.value.wizardState
        _ui.value = _ui.value.copy(
            wizardState = current.copy(detailsInfo = transform(current.detailsInfo))
        )
        debounceSaveDraft()
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
    
    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }

    private var quarantineCheckPassed = true // Precomputed during loadPrefillData
    
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
            WizardStep.DETAILS -> buildMap {
                val category = when (state.basicInfo.category) {
                    Category.Meat -> com.rio.rostry.marketplace.model.ProductCategory.Meat
                    Category.Adoption -> if (state.basicInfo.traceability == Traceability.Traceable) com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable else com.rio.rostry.marketplace.model.ProductCategory.AdoptionNonTraceable
                }
                val ageGroup = when (state.basicInfo.ageGroup) {
                    AgeGroup.Chick -> com.rio.rostry.marketplace.model.AgeGroup.CHICK_0_5_WEEKS
                    AgeGroup.Grower -> com.rio.rostry.marketplace.model.AgeGroup.YOUNG_5_20_WEEKS
                    AgeGroup.Adult -> com.rio.rostry.marketplace.model.AgeGroup.ADULT_20_52_WEEKS
                    AgeGroup.Senior -> com.rio.rostry.marketplace.model.AgeGroup.BREEDER_12_MONTHS_PLUS
                }
                val input = com.rio.rostry.marketplace.form.DynamicListingValidator.Input(
                    category = category,
                    ageGroup = ageGroup,
                    isTraceable = state.basicInfo.traceability == Traceability.Traceable,
                    title = state.basicInfo.title,
                    birthDateMillis = state.detailsInfo.birthDateMillis,
                    birthPlace = state.detailsInfo.birthPlace.ifBlank { null },
                    vaccinationRecords = state.detailsInfo.vaccination.ifBlank { null },
                    healthRecordDateMillis = state.detailsInfo.healthRecordDateMillis,
                    parentInfo = state.detailsInfo.parentInfo.ifBlank { null },
                    weightGrams = state.detailsInfo.weightText.toDoubleOrNull(),
                    healthRecords = state.detailsInfo.healthUri.ifBlank { null },
                    gender = state.detailsInfo.genderText.ifBlank { null },
                    sizeCm = state.detailsInfo.heightCm,
                    colorPattern = state.detailsInfo.colorPattern.ifBlank { null },
                    specialCharacteristics = state.detailsInfo.specialChars.ifBlank { null },
                    breedingHistory = state.detailsInfo.breedingHistory.ifBlank { null },
                    provenPairsDoc = state.detailsInfo.provenPairs.ifBlank { null },
                    geneticTraits = state.detailsInfo.geneticTraits.ifBlank { null },
                    awards = state.detailsInfo.awards.ifBlank { null },
                    lineageDoc = state.detailsInfo.lineageDoc.ifBlank { null },
                    photosCount = state.mediaInfo.photoUris.size,
                    videosCount = state.mediaInfo.videoUris.size,
                    audioCount = state.mediaInfo.audioUris.size,
                    documentsCount = state.mediaInfo.documentUris.size,
                    price = state.basicInfo.price.toDoubleOrNull(),
                    startPrice = state.basicInfo.auctionStartPrice.toDoubleOrNull(),
                    latitude = state.detailsInfo.latitude,
                    longitude = state.detailsInfo.longitude
                )
                val result = com.rio.rostry.marketplace.form.DynamicListingValidator.validate(input)
                if (!result.valid) {
                    result.errors.forEachIndexed { index, err -> put("details.$index", err) }
                }
                // Non-blocking GPS hint when not required (non-traceable)
                val gpsMissing = state.detailsInfo.latitude == null || state.detailsInfo.longitude == null
                val isTraceableAdoption = category is com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable
                if (gpsMissing && !isTraceableAdoption) {
                    // Hint via transient error field; navigation is not blocked since not added to map
                    _ui.value = _ui.value.copy(error = "Tip: Adding your GPS location can improve discovery for your listing.")
                }
            }
            WizardStep.MEDIA -> emptyMap()
            WizardStep.REVIEW -> buildMap {
                // Check quarantine status (already precomputed synchronously)
                if (!quarantineCheckPassed) {
                    put("quarantine", "Cannot list products currently in quarantine. Complete quarantine protocol first.")
                }
                
                // If ageGroup == Chick: require vaccination
                if (state.basicInfo.ageGroup == AgeGroup.Chick && state.detailsInfo.vaccination.isBlank()) {
                    put("vaccination", "Vaccination records required for chicks before listing")
                }
                // If traceability == Traceable: require parentInfo or lineageDoc
                if (state.basicInfo.traceability == Traceability.Traceable) {
                    if (state.detailsInfo.parentInfo.isBlank() && state.detailsInfo.lineageDoc.isBlank()) {
                        put("traceability", "Parent info or lineage document required for traceable products")
                    }
                }

                // Run dynamic validator again on REVIEW as a final gate
                val category = when (state.basicInfo.category) {
                    Category.Meat -> com.rio.rostry.marketplace.model.ProductCategory.Meat
                    Category.Adoption -> if (state.basicInfo.traceability == Traceability.Traceable) com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable else com.rio.rostry.marketplace.model.ProductCategory.AdoptionNonTraceable
                }
                val ageGroup = when (state.basicInfo.ageGroup) {
                    AgeGroup.Chick -> com.rio.rostry.marketplace.model.AgeGroup.CHICK_0_5_WEEKS
                    AgeGroup.Grower -> com.rio.rostry.marketplace.model.AgeGroup.YOUNG_5_20_WEEKS
                    AgeGroup.Adult -> com.rio.rostry.marketplace.model.AgeGroup.ADULT_20_52_WEEKS
                    AgeGroup.Senior -> com.rio.rostry.marketplace.model.AgeGroup.BREEDER_12_MONTHS_PLUS
                }
                val input = com.rio.rostry.marketplace.form.DynamicListingValidator.Input(
                    category = category,
                    ageGroup = ageGroup,
                    isTraceable = state.basicInfo.traceability == Traceability.Traceable,
                    title = state.basicInfo.title,
                    birthDateMillis = state.detailsInfo.birthDateMillis,
                    birthPlace = state.detailsInfo.birthPlace.ifBlank { null },
                    vaccinationRecords = state.detailsInfo.vaccination.ifBlank { null },
                    healthRecordDateMillis = state.detailsInfo.healthRecordDateMillis,
                    parentInfo = state.detailsInfo.parentInfo.ifBlank { null },
                    weightGrams = state.detailsInfo.weightText.toDoubleOrNull(),
                    healthRecords = state.detailsInfo.healthUri.ifBlank { null },
                    gender = state.detailsInfo.genderText.ifBlank { null },
                    sizeCm = state.detailsInfo.heightCm,
                    colorPattern = state.detailsInfo.colorPattern.ifBlank { null },
                    specialCharacteristics = state.detailsInfo.specialChars.ifBlank { null },
                    breedingHistory = state.detailsInfo.breedingHistory.ifBlank { null },
                    provenPairsDoc = state.detailsInfo.provenPairs.ifBlank { null },
                    geneticTraits = state.detailsInfo.geneticTraits.ifBlank { null },
                    awards = state.detailsInfo.awards.ifBlank { null },
                    lineageDoc = state.detailsInfo.lineageDoc.ifBlank { null },
                    photosCount = state.mediaInfo.photoUris.size,
                    videosCount = state.mediaInfo.videoUris.size,
                    audioCount = state.mediaInfo.audioUris.size,
                    documentsCount = state.mediaInfo.documentUris.size,
                    price = state.basicInfo.price.toDoubleOrNull(),
                    startPrice = state.basicInfo.auctionStartPrice.toDoubleOrNull(),
                    latitude = state.detailsInfo.latitude,
                    longitude = state.detailsInfo.longitude
                )
                val result = com.rio.rostry.marketplace.form.DynamicListingValidator.validate(input)
                if (!result.valid) {
                    result.errors.forEachIndexed { index, err -> put("review.$index", err) }
                }
                // Non-blocking GPS hint at review step as well
                val gpsMissing = state.detailsInfo.latitude == null || state.detailsInfo.longitude == null
                val isTraceableAdoption = category is com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable
                if (gpsMissing && !isTraceableAdoption) {
                    _ui.value = _ui.value.copy(error = "Tip: Adding your GPS location can improve discovery for your listing.")
                }
            }
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
            
            // Comment 2: Re-check quarantine status at submit time
            if (!prefillProductId.isNullOrBlank()) {
                val inQuarantine = quarantineRepository.observe(prefillProductId!!).first()
                    .any { it.status == "ACTIVE" }
                if (inQuarantine) {
                    _ui.value = UiState(
                        isSubmitting = false,
                        error = "Cannot list products in quarantine. Complete quarantine protocol first."
                    )
                    return@launch
                }

                // Additional lifecycle validation: block DECEASED or TRANSFERRED
                val productRes = productRepository.getProductById(prefillProductId!!).first { it !is Resource.Loading }
                val p = (productRes as? Resource.Success)?.data
                if (p != null) {
                    val status = p.lifecycleStatus?.uppercase()
                    if (status == "DECEASED" || status == "TRANSFERRED") {
                        _ui.value = UiState(
                            isSubmitting = false,
                            error = "Cannot list products that are ${status?.lowercase()}."
                        )
                        return@launch
                    }
                }
            }
            
            try {
                val candidate = mapToEntity(currentUser, form)
                // Validate with traceability before any heavy work
                val validation = productValidator.validateWithTraceability(candidate)
                if (!validation.valid) {
                    _ui.value = UiState(
                        isSubmitting = false,
                        error = validation.reasons.joinToString(separator = "; ")
                    )
                    return@launch
                }

                val imageBytes = resolveAndCompress(form.photoUris)
                val baseProduct = if (imageBytes.isNotEmpty()) candidate.copy(imageUrls = emptyList()) else candidate
                when (val res = marketplace.createProduct(baseProduct, imageBytes)) {
                    is Resource.Success -> {
                        _ui.value = UiState(isSubmitting = false, successProductId = res.data)
                        // Delete draft on successful publish
                        listingDraftRepository.deleteDraft("${currentUser.userId}_current")
                        
                        // Track analytics: listing submitted
                        if (prefillProductId != null && res.data != null) {
                            analyticsRepository.trackFarmToMarketplaceListingSubmitted(
                                currentUser.userId,
                                prefillProductId!!,
                                res.data!!
                            )
                        }
                    }
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
