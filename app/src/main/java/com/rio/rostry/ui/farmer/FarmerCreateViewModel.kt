package com.rio.rostry.ui.farmer

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.OutboxDao
import com.rio.rostry.data.database.entity.OutboxEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductMarketplaceRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.components.SyncState
import com.rio.rostry.utils.CompressionUtils
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.database.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.ui.components.ConflictDetails
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.rio.rostry.utils.BirdIdGenerator
import com.rio.rostry.data.repository.MarketListingRepository
import com.rio.rostry.data.repository.FarmAssetRepository
import com.rio.rostry.data.repository.InventoryRepository
import com.rio.rostry.data.database.entity.MarketListingEntity
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.data.database.entity.InventoryItemEntity

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
    private val productValidator: com.rio.rostry.marketplace.validation.ProductValidator,
    private val dailyLogRepository: com.rio.rostry.data.repository.monitoring.DailyLogRepository,
    private val auditLogDao: AuditLogDao,
    private val outboxDao: OutboxDao,
    private val gson: Gson,
    private val currentUserProvider: CurrentUserProvider,
    private val connectivityManager: com.rio.rostry.utils.network.ConnectivityManager,
    private val syncManager: SyncManager,
    private val rbacGuard: RbacGuard,
    private val marketListingRepository: MarketListingRepository,
    private val farmAssetRepository: FarmAssetRepository,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private var prefillProductId: String? = null

    init {
        loadDraft()
        val userId = currentUserProvider.userIdOrNull()
        if (userId != null) {
            viewModelScope.launch {
                outboxDao.observePendingCountByType(userId, OutboxEntity.TYPE_LISTING).collect { count ->
                    _ui.value = _ui.value.copy(pendingListingsCount = count)
                }
            }
        }
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
        val traceability: Traceability = Traceability.NonTraceable,
        val ageGroup: AgeGroup = AgeGroup.Chick,
        val title: String = "",
        val listForSale: Boolean = false,  // NEW: Toggle for marketplace listing
        val priceType: PriceType = PriceType.Fixed,
        val price: Double? = null,
        val auctionStartPrice: Double? = null,
        val availableFrom: String = "",
        val availableTo: String = "",
        val deliveryOptions: List<String> = emptyList(),
        val deliveryCost: Double? = null,
        val leadTimeDays: Int? = null,
        val quantity: Int = 1,
        val isBatch: Boolean = false
    )

    data class DetailsInfoState(
        val birthDateMillis: Long? = null,
        val birthPlace: String = "",
        val vaccination: String = "",
        val healthRecordDateMillis: Long? = null,
        val parentInfo: String = "",
        val weightText: String = "",
        val healthUri: String = "",
        val genderText: String = "",
        val heightCm: Double? = null,
        val colorPattern: String = "",
        val specialChars: String = "",
        val breedingHistory: String = "",
        val provenPairs: String = "",
        val geneticTraits: String = "",
        val awards: String = "",
        val lineageDoc: String = "",
        val sizeText: String = "",
        val latitude: Double? = null,
        val longitude: Double? = null,
        val breed: String = "",
        val deliveryOptions: List<String> = emptyList(),
        val deliveryCost: Double? = null,
        val leadTimeDays: Int? = null,
        // Weight validation fields (Phase 8)
        val expectedWeightRange: IntRange? = null,
        val weightValidationMessage: String? = null,
        val isWeightBelowExpected: Boolean = false,
        val isWeightAboveExpected: Boolean = false,
        val suggestedWeight: Int? = null,
        val ageInDays: Int? = null,
        val ageDescription: String? = null
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
        val error: String? = null,
        val validationStatus: Map<String, Boolean> = emptyMap(),
        val listingSyncState: SyncState? = null,
        val pendingListingsCount: Int = 0,
        val isOnline: Boolean = true,
        val conflictDetails: ConflictDetails? = null,
        val verificationRequired: Boolean = false
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    private val _navigationEvents = MutableSharedFlow<String>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val onlineFlow: StateFlow<Boolean> = connectivityManager
        .observe()
        .map { it.isOnline }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        viewModelScope.launch {
            onlineFlow.collect { online ->
                _ui.value = _ui.value.copy(isOnline = online)
            }
        }
        viewModelScope.launch {
            syncManager.conflictEvents.collect { ev ->
                if (ev.entityType == com.rio.rostry.data.database.entity.OutboxEntity.TYPE_LISTING) {
                    _ui.value = _ui.value.copy(
                        conflictDetails = ConflictDetails(
                            entityType = "listing",
                            entityId = ev.entityId,
                            conflictFields = ev.conflictFields,
                            mergedAt = ev.mergedAt,
                            message = "Listing was updated remotely. Your local changes were merged."
                        )
                    )
                }
            }
        }
    }


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
     * Load farm monitoring data to prefill the listing wizard when coming from farm screens.
     * When productId is null/blank, this function silently returns as manual listing is intended.
     * Only shows error when a valid productId is provided but the product cannot be loaded.
     */
    fun loadPrefillData(productId: String?) {
        // If no productId provided, user intends to create listing manually - don't show error
        if (productId.isNullOrBlank()) {
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
                        price = product.price,
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
                        breed = product.breed ?: "",
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
                    prefilled.basicInfo.price != null,
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
        if (current.currentStep == WizardStep.MEDIA && next == WizardStep.REVIEW) {
            // Pre-compute farm data freshness for REVIEW to avoid suspend calls in validation
            val sourceProductId = prefillProductId
            if (!sourceProductId.isNullOrBlank()) {
                viewModelScope.launch {
                    val fresh = checkFarmDataFreshness(sourceProductId)
                    val statusMap = buildMap {
                        put("notInQuarantine", quarantineCheckPassed)
                        put("recentVaccination", fresh["vaccination"] == true)
                        put("recentHealthLog", fresh["healthLog"] == true)
                        put("recentGrowthRecord", fresh["growth"] == true)
                    }
                    _ui.value = _ui.value.copy(
                        wizardState = current.copy(currentStep = next, validationErrors = emptyMap()),
                        validationStatus = statusMap
                    )
                    saveDraft()
                }
                return
            }
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
        val updatedDetails = transform(current.detailsInfo)
        
        // Calculate age and validate weight (Phase 8)
        val validatedDetails = validateWeightInput(updatedDetails, current.basicInfo.ageGroup)
        
        _ui.value = _ui.value.copy(
            wizardState = current.copy(detailsInfo = validatedDetails)
        )
        debounceSaveDraft()
    }
    
    /**
     * Validate weight input against lifecycle benchmarks.
     * Non-blocking - just populates warning fields.
     */
    private fun validateWeightInput(
        details: DetailsInfoState,
        ageGroup: AgeGroup
    ): DetailsInfoState {
        // Calculate age in days from birth date
        val ageInDays = com.rio.rostry.domain.model.PoultryWeightBenchmarks.calculateAgeInDays(details.birthDateMillis)
            ?: estimateAgeFromAgeGroup(ageGroup)
        
        val ageDescription = ageInDays?.let { 
            com.rio.rostry.domain.model.PoultryWeightBenchmarks.getAgeDescription(it) 
        }
        
        // Get expected weight range
        val expectedRange = ageInDays?.let { 
            com.rio.rostry.domain.model.PoultryWeightBenchmarks.getExpectedRangeForAge(it) 
        }
        
        // Get suggested weight
        val suggestedWeight = com.rio.rostry.domain.model.PoultryWeightBenchmarks.getSuggestedWeight(
            ageInDays, 
            details.genderText.ifBlank { null }
        )
        
        // Validate weight if provided
        val weightGrams = details.weightText.toIntOrNull()
        val validationResult = if (weightGrams != null && weightGrams > 0) {
            com.rio.rostry.domain.model.PoultryWeightBenchmarks.validateWeight(
                weightGrams,
                ageInDays,
                details.genderText.ifBlank { null }
            )
        } else {
            null
        }
        
        // Extract validation message and flags
        val (message, isBelow, isAbove) = when (validationResult) {
            is com.rio.rostry.domain.model.PoultryWeightBenchmarks.WeightValidationResult.BelowExpected -> 
                Triple(validationResult.message, true, false)
            is com.rio.rostry.domain.model.PoultryWeightBenchmarks.WeightValidationResult.AboveExpected -> 
                Triple(validationResult.message, false, true)
            else -> Triple(null, false, false)
        }
        
        return details.copy(
            ageInDays = ageInDays,
            ageDescription = ageDescription,
            expectedWeightRange = expectedRange,
            suggestedWeight = suggestedWeight,
            weightValidationMessage = message,
            isWeightBelowExpected = isBelow,
            isWeightAboveExpected = isAbove
        )
    }
    
    /**
     * Estimate age in days based on AgeGroup selection.
     */
    private fun estimateAgeFromAgeGroup(ageGroup: AgeGroup): Int? {
        return when (ageGroup) {
            AgeGroup.Chick -> 14    // ~2 weeks
            AgeGroup.Grower -> 56   // ~8 weeks
            AgeGroup.Adult -> 140   // ~20 weeks
            AgeGroup.Senior -> 365  // ~1 year
        }
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
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSubmitting = true)
            try {
                // 1. Check if user has a verified farm location profile
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    // We need to fetch the latest user data to be sure
                    val user = userRepository.getCurrentUserSuspend()
                    
                    if (user != null && user.farmLocationLat != null && user.farmLocationLng != null) {
                        // Priority: Use the verified/saved farm location
                        updateDetails { it.copy(
                            latitude = user.farmLocationLat, 
                            longitude = user.farmLocationLng
                        ) }
                        _ui.value = _ui.value.copy(isSubmitting = false)
                        return@launch
                    }
                }
                
                // 2. Fallback: If no saved location, we'd normally trigger device GPS here.
                // Since this is a ViewModel, we can't request permissions directly.
                // Ideally, we'd emit a side-effect to the UI to request location.
                // For now, prompt the user if no saved location is found.
                _ui.value = _ui.value.copy(
                    isSubmitting = false,
                    error = "No saved farm location found. Please verify your farm location in Profile or enter coordinates manually."
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isSubmitting = false, error = "Failed to detect location: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _ui.value = _ui.value.copy(error = null)
    }

    fun dismissConflict() {
        _ui.value = _ui.value.copy(conflictDetails = null)
    }

    fun viewConflictDetails(entityId: String) {
        _ui.value = _ui.value.copy(conflictDetails = null)
    }

    private var quarantineCheckPassed = true // Precomputed during loadPrefillData
    
    fun validateStep(step: WizardStep): Map<String, String> {
        val state = _ui.value.wizardState
        return when (step) {
            WizardStep.BASICS -> buildMap {
                if (state.basicInfo.title.isBlank()) put("title", "Title is required")
                
                // Only validate marketplace fields if listing for sale
                if (state.basicInfo.listForSale) {
                    if (state.basicInfo.priceType == PriceType.Fixed && state.basicInfo.price == null) {
                        put("price", "Enter valid price")
                    }
                    if (state.basicInfo.priceType == PriceType.Auction && state.basicInfo.auctionStartPrice == null) {
                        put("auctionStartPrice", "Enter valid start price")
                    }
                    if (state.basicInfo.availableFrom.isBlank()) put("availableFrom", "Start date required")
                    if (state.basicInfo.availableTo.isBlank()) put("availableTo", "End date required")
                }
                
                if (state.basicInfo.quantity < 1) {
                    put("quantity", "Quantity must be at least 1")
                }
                if (state.basicInfo.isBatch && state.basicInfo.quantity <= 1) {
                    put("quantity", "Batch size must be greater than 1")
                }
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
                    price = state.basicInfo.price,
                    startPrice = state.basicInfo.auctionStartPrice,
                    latitude = state.detailsInfo.latitude,
                    longitude = state.detailsInfo.longitude,
                    hasRecentVaccination = _ui.value.validationStatus["recentVaccination"] as? Boolean,
                    hasRecentHealthLog = _ui.value.validationStatus["recentHealthLog"] as? Boolean,
                    hasRecentGrowthRecord = _ui.value.validationStatus["recentGrowthRecord"] as? Boolean
                )
                val result = com.rio.rostry.marketplace.form.DynamicListingValidator.validate(input)
                if (!result.valid) {
                    result.errors.forEachIndexed { index: Int, err: String -> put("details.$index", err) }
                }
                // Non-blocking GPS hint when not required (non-traceable)
                val gpsMissing = state.detailsInfo.latitude == null || state.detailsInfo.longitude == null
                val isTraceableAdoptionDetails = category is com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable
                if (gpsMissing && !isTraceableAdoptionDetails) {
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

                // Add farm data freshness checks using precomputed state
                val isTraceableAdoptionReview = state.basicInfo.category == Category.Adoption && state.basicInfo.traceability == Traceability.Traceable
                if (isTraceableAdoptionReview) {
                    val vs = _ui.value.validationStatus
                    if (prefillProductId != null) {
                        if (vs["recentVaccination"] == false) {
                            put("vaccination", "No vaccination in last 30 days. Add vaccination record before listing.")
                        }
                        if (vs["recentHealthLog"] == false) {
                            put("healthLog", "No health log in last 7 days. Add daily health log before listing.")
                        }
                        if (vs["recentGrowthRecord"] == false) {
                            put("growth", "No growth record in last 14 days. Add growth monitoring before listing.")
                        }
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
                    price = state.basicInfo.price,
                    startPrice = state.basicInfo.auctionStartPrice,
                    latitude = state.detailsInfo.latitude,
                    longitude = state.detailsInfo.longitude,
                    hasRecentVaccination = _ui.value.validationStatus["recentVaccination"] as? Boolean,
                    hasRecentHealthLog = _ui.value.validationStatus["recentHealthLog"] as? Boolean,
                    hasRecentGrowthRecord = _ui.value.validationStatus["recentGrowthRecord"] as? Boolean
                )
                val result = com.rio.rostry.marketplace.form.DynamicListingValidator.validate(input)
                if (!result.valid) {
                    result.errors.forEachIndexed { index: Int, err: String -> put("review.$index", err) }
                }
                // Non-blocking GPS hint at review step as well
                val gpsMissing = state.detailsInfo.latitude == null || state.detailsInfo.longitude == null
                val isTraceableAdoptionReview2 = category is com.rio.rostry.marketplace.model.ProductCategory.AdoptionTraceable
                if (gpsMissing && !isTraceableAdoptionReview2) {
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
            price = state.basicInfo.price,
            auctionStartPrice = state.basicInfo.auctionStartPrice,
            availableFrom = state.basicInfo.availableFrom,
            availableTo = state.basicInfo.availableTo,
            healthRecordUri = state.detailsInfo.healthUri.ifBlank { null },
            birthDateMillis = state.detailsInfo.birthDateMillis,
            birthPlace = state.detailsInfo.birthPlace.ifBlank { null },
            vaccinationRecords = state.detailsInfo.vaccination.ifBlank { null },
            parentInfo = state.detailsInfo.parentInfo.ifBlank { null },
            photoUris = state.mediaInfo.photoUris,
            videoUris = state.mediaInfo.videoUris,
            latitude = state.detailsInfo.latitude,
            longitude = state.detailsInfo.longitude,
            deliveryOptions = state.detailsInfo.deliveryOptions,
            deliveryCost = state.detailsInfo.deliveryCost,
            leadTimeDays = state.detailsInfo.leadTimeDays,
            quantity = state.basicInfo.quantity,
            isBatch = state.basicInfo.isBatch
        )
        if (_ui.value.isSubmitting) return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isSubmitting = true)
            // Fetch current user to populate sellerId and location
            val currentUser = getCurrentUserOrNull()
            if (currentUser == null) {
                _ui.value = UiState(isSubmitting = false, error = "Not authenticated")
                return@launch
            }

            // Check verification status
            if (currentUser.verificationStatus != VerificationStatus.VERIFIED) {
                _ui.value = UiState(isSubmitting = false, error = "Complete KYC verification to list products. Go to Profile → Verification.", verificationRequired = true)
                return@launch
            }

            // Check RBAC permission
            if (!rbacGuard.canListProduct()) {
                _ui.value = UiState(isSubmitting = false, error = "You don't have permission to list products")
                return@launch
            }

            // Comment 2: Re-check quarantine status at submit time
            if (!prefillProductId.isNullOrBlank()) {
                val inQuarantine = quarantineRepository.observe(prefillProductId!!).first()
                    .any { it.status == "ACTIVE" }
                if (inQuarantine) {
                    _ui.value = UiState(
                        isSubmitting = false,
                        error = "Complete quarantine protocol and mark as RECOVERED before listing"
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
                            error = if (status == "DECEASED") "Cannot list deceased birds. Remove from active inventory." else "This bird has been transferred. Update ownership records."
                        )
                        return@launch
                    }
                }
            }
            
            try {
                val candidate = mapToEntity(currentUser, form)
                val outboxEntry = OutboxEntity(
                    outboxId = candidate.productId.ifBlank { UUID.randomUUID().toString() },
                    userId = currentUser.userId,
                    entityType = OutboxEntity.TYPE_LISTING,
                    entityId = candidate.productId,
                    operation = "CREATE",
                    payloadJson = gson.toJson(candidate),
                    createdAt = System.currentTimeMillis(),
                    priority = "HIGH"
                )
                outboxDao.insert(outboxEntry)
                // Validate with traceability before any heavy work
                val validation = productValidator.validateWithTraceability(candidate, sourceProductId = prefillProductId)
                if (!validation.valid) {
                    _ui.value = _ui.value.copy(
                        isSubmitting = false,
                        error = validation.reasons.joinToString(separator = "; ")
                    )
                    // Add audit log for validation failure
                    val auditLog = com.rio.rostry.data.database.entity.AuditLogEntity.createValidationFailureLog(
                        refId = prefillProductId ?: "NEW_LISTING",
                        action = "LISTING_BLOCKED",
                        actorUserId = currentUser.userId,
                        reasons = validation.reasons
                    )
                    auditLogDao.insert(auditLog)
                    return@launch
                }

                // Construct form from wizardState for image processing
                val form = with(_ui.value.wizardState) {
                    ListingForm(
                        category = basicInfo.category,
                        traceability = basicInfo.traceability,
                        title = basicInfo.title,
                        price = basicInfo.price,
                        auctionStartPrice = basicInfo.auctionStartPrice,
                        quantity = basicInfo.quantity,
                        ageGroup = basicInfo.ageGroup,
                        birthDateMillis = detailsInfo.birthDateMillis,
                        healthRecordUri = detailsInfo.healthUri,
                        photoUris = mediaInfo.photoUris,
                        videoUris = mediaInfo.videoUris,
                        deliveryOptions = basicInfo.deliveryOptions,
                        deliveryCost = basicInfo.deliveryCost,
                        leadTimeDays = basicInfo.leadTimeDays,
                        isBatch = basicInfo.isBatch,
                        priceType = basicInfo.priceType,
                        availableFrom = basicInfo.availableFrom,
                        availableTo = basicInfo.availableTo
                    )
                }
                val imageBytes = resolveAndCompress(form.photoUris)
                val baseProduct = if (imageBytes.isNotEmpty()) candidate.copy(imageUrls = emptyList()) else candidate
                when (val res = marketplace.createProduct(baseProduct, imageBytes)) {
                    is Resource.Success -> {
                        val listingId = res.data ?: candidate.productId
                        
                        // New Architecture: Create parallel entities
                        createNewDomainEntities(currentUser, form, listingId)
                        
                        _ui.value = _ui.value.copy(isSubmitting = false, successProductId = listingId, listingSyncState = if (connectivityManager.isOnline()) SyncState.SYNCED else SyncState.PENDING, error = if (connectivityManager.isOnline()) "Listing published successfully" else "Listing queued. Will publish when online.")
                        // Delete draft on successful publish
                        listingDraftRepository.deleteDraft("${currentUser.userId}_current")

                        // Track analytics: listing submitted
                        if (prefillProductId != null) {
                            analyticsRepository.trackFarmToMarketplaceListingSubmitted(
                                currentUser.userId,
                                prefillProductId!!,
                                listingId
                            )
                        }
                    }
                    is Resource.Error -> _ui.value = _ui.value.copy(isSubmitting = false, error = res.message ?: "Failed to publish")
                    is Resource.Loading -> _ui.value = _ui.value.copy(isSubmitting = true)
                }
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(isSubmitting = false, error = e.message ?: "Unexpected error")
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
        val birthDate: Long? = when (form.ageGroup) {
            AgeGroup.Chick -> com.rio.rostry.utils.TimeUtils.approximateBirthDateDays(10L)
            AgeGroup.Grower -> com.rio.rostry.utils.TimeUtils.approximateBirthDateDays(60L)
            AgeGroup.Adult -> com.rio.rostry.utils.TimeUtils.approximateBirthDateDays(200L)
            AgeGroup.Senior -> com.rio.rostry.utils.TimeUtils.approximateBirthDateDays(400L)
        }
        val status = "available"
        val color = _ui.value.wizardState.detailsInfo.colorPattern.ifBlank { null }
        val breed = _ui.value.wizardState.detailsInfo.breed.ifBlank { null }
        val productId = prefillProductId ?: UUID.randomUUID().toString()
        // productId must be non-empty for BirdIdGenerator to work correctly
        val birdCode = BirdIdGenerator.generate(color, breed, user.userId, productId)
        val colorTag = BirdIdGenerator.colorTag(color)
        return ProductEntity(
            productId = productId,
            sellerId = user.userId,
            name = name,
            description = "",
            category = categoryText,
            price = price,
            quantity = form.quantity.toDouble(),
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
            color = color,
            breed = breed,
            birdCode = birdCode,
            colorTag = colorTag,
            familyTreeId = if (traceable) _ui.value.wizardState.detailsInfo.lineageDoc.ifBlank { null } else null,
            parentIdsJson = _ui.value.wizardState.detailsInfo.parentInfo.ifBlank { null },
            breedingStatus = null,
            transferHistoryJson = null,
            createdAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            isDeleted = false,
            deletedAt = null,
            dirty = true,
            stage = null,
            lifecycleStatus = null,
            parentMaleId = null,
            parentFemaleId = null,
            ageWeeks = null,
            lastStageTransitionAt = null,
            breederEligibleAt = null,
            isBatch = form.isBatch,
            splitAt = null,
            splitIntoIds = null,
            documentUrls = emptyList(),
            qrCodeUrl = null,
            customStatus = null,
            debug = false,
            // Delivery & Logistics fields from the form
            deliveryOptions = form.deliveryOptions,
            deliveryCost = form.deliveryCost,
            leadTimeDays = form.leadTimeDays
        )
    }

    private fun resolveAndCompress(uris: List<String>): List<ByteArray> {
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

    private suspend fun createNewDomainEntities(user: UserEntity, form: ListingForm, listingId: String) {
        val now = System.currentTimeMillis()
        val farmerId = user.userId
        
        // 1. Determine Asset ID (Link to existing if prefilled, otherwise use the listing ID which is the source of truth)
        // CRITICAL FIX: Ensure Asset ID matches Product ID for un-prefilled listings to maintain link
        val assetId = prefillProductId ?: listingId
        
        // 2. Ensure FarmAsset exists in the new table
        val existingAssetRes = if (prefillProductId != null) {
            farmAssetRepository.getAssetById(assetId).first()
        } else null
        val existingAsset = existingAssetRes?.data

        if (existingAsset == null) {
            val breed = _ui.value.wizardState.detailsInfo.breed
            
            val newAsset = FarmAssetEntity(
                assetId = assetId,
                farmerId = farmerId,
                name = form.title,
                assetType = if (form.isBatch) "BATCH" else "ANIMAL",
                category = form.category.name,
                status = "ACTIVE",
                locationName = user.address ?: "Farm",
                quantity = form.quantity.toDouble(),
                birthDate = form.birthDateMillis,
                breed = breed,
                healthStatus = "OK",
                weightGrams = _ui.value.wizardState.detailsInfo.weightText.toDoubleOrNull(),
                isShowcase = false,
                color = _ui.value.wizardState.detailsInfo.colorPattern,
                metadataJson = "{}",
                createdAt = now,
                updatedAt = now,
                dirty = true
            )
            farmAssetRepository.addAsset(newAsset)
        }
        
        // 3. Create Inventory Item (The bridge)
        val inventoryId = UUID.randomUUID().toString()
        val inventoryItem = InventoryItemEntity(
            inventoryId = inventoryId,
            farmerId = farmerId,
            sourceAssetId = assetId,
            name = form.title,
            sku = "SKU-${assetId.take(5).uppercase()}",
            category = form.category.name,
            quantityAvailable = form.quantity.toDouble(),
            unit = "piece",
            createdAt = now,
            updatedAt = now,
            dirty = true
        )
        inventoryRepository.addInventory(inventoryItem)
        
        // 4. Create Market Listing (Commercial detail)
        val marketListing = MarketListingEntity(
            listingId = listingId,
            sellerId = farmerId,
            inventoryId = inventoryId,
            title = form.title,
            description = _ui.value.wizardState.detailsInfo.specialChars.ifBlank { "" },
            category = form.category.name,
            price = form.price ?: 0.0,
            currency = "INR",
            status = "PUBLISHED",
            isActive = true,
            latitude = form.latitude ?: user.farmLocationLat,
            longitude = form.longitude ?: user.farmLocationLng,
            deliveryOptions = form.deliveryOptions,
            deliveryCost = form.deliveryCost ?: 0.0,
            leadTimeDays = form.leadTimeDays ?: 0,
            viewsCount = 0,
            inquiriesCount = 0,
            createdAt = now,
            updatedAt = now,
            dirty = true
        )
        marketListingRepository.publishListing(marketListing)
        
        timber.log.Timber.d("New domain entities created for listing $listingId (Asset: $assetId, Inv: $inventoryId)")
    }

    private suspend fun getCurrentUserOrNull(): UserEntity? {
        return when (val res = userRepository.getCurrentUser().first()) {
            is com.rio.rostry.utils.Resource.Success -> res.data
            else -> null
        }
    }

    private suspend fun checkFarmDataFreshness(productId: String): Map<String, Boolean> {
        val vaccinationFresh = vaccinationRepository.observe(productId).first()
            .any { it.administeredAt != null && com.rio.rostry.utils.TimeUtils.isRecent(it.administeredAt!!, 30L) }
        val healthLogFresh = dailyLogRepository.observe(productId).first()
            .any { com.rio.rostry.utils.TimeUtils.isRecent(it.createdAt, 7L) }
        val growthFresh = growthRepository.observe(productId).first()
            .any { com.rio.rostry.utils.TimeUtils.isRecent(it.createdAt, 14L) }
        return mapOf(
            "vaccination" to vaccinationFresh,
            "healthLog" to healthLogFresh,
            "growth" to growthFresh
        )
    }

    fun navigateToVerification() {
        viewModelScope.launch {
            _navigationEvents.emit("verification")
        }
    }
}