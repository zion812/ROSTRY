package com.rio.rostry.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.monitoring.DailyLogRepository
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
import com.rio.rostry.data.repository.monitoring.TaskRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.utils.validation.OnboardingValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import java.util.UUID
import javax.inject.Inject
import com.rio.rostry.domain.model.LifecycleStage

@HiltViewModel
class OnboardFarmBirdViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val farmOnboardingRepository: FarmOnboardingRepository,
    private val dailyLogRepository: DailyLogRepository,
    private val taskRepository: TaskRepository,
    private val mediaUploadManager: MediaUploadManager,
    private val familyTreeRepository: FamilyTreeRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    enum class WizardStep { PATH_SELECTION, AGE_GROUP, CORE_DETAILS, LINEAGE, PROOFS, REVIEW }

    data class CoreDetailsState(
        val name: String = "",
        val birthDate: Long? = null,
        val birthPlace: String = "",
        val gender: String = "Unknown",
        val weightGrams: String = "",
        val heightCm: String = "",
        val colors: String = "",
        val breed: String = "",
        val physicalId: String = "",
        val vaccinationRecords: String = "",
        val healthStatus: String = "OK",
        val breedingHistory: String = "",
        val awards: String = ""
    )

    data class LineageState(
        val maleParentId: String? = null,
        val femaleParentId: String? = null,
        val maleParentName: String? = null,
        val femaleParentName: String? = null
    )

    data class MediaState(
        val photoUris: List<String> = emptyList(),
        val videoUris: List<String> = emptyList(),
        val documentUris: List<String> = emptyList()
    )

    data class WizardState(
        val currentStep: WizardStep = WizardStep.PATH_SELECTION,
        val isTraceable: Boolean? = null,
        val ageGroup: OnboardingValidator.AgeGroup? = null,
        val coreDetails: CoreDetailsState = CoreDetailsState(),
        val lineage: LineageState = LineageState(),
        val media: MediaState = MediaState(),
        val validationErrors: Map<String, String> = emptyMap(),
        val saving: Boolean = false,
        val error: String? = null,
        val savedId: String? = null
    )

    private val _state = MutableStateFlow(WizardState())
    val state: StateFlow<WizardState> = _state

    private var isEnthusiast: Boolean = false
    fun setRole(role: String) {
        isEnthusiast = role.equals("enthusiast", ignoreCase = true)
        if (isEnthusiast && _state.value.isTraceable == null) {
            _state.value = _state.value.copy(isTraceable = true)
        }
    }

    // Available parents (ACTIVE, non-batch) separated by gender
    private val _availableMaleParents = MutableStateFlow<List<com.rio.rostry.data.database.entity.ProductEntity>>(emptyList())
    val availableMaleParents: StateFlow<List<com.rio.rostry.data.database.entity.ProductEntity>> = _availableMaleParents
    private val _availableFemaleParents = MutableStateFlow<List<com.rio.rostry.data.database.entity.ProductEntity>>(emptyList())
    val availableFemaleParents: StateFlow<List<com.rio.rostry.data.database.entity.ProductEntity>> = _availableFemaleParents

    fun loadAvailableParents() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            productRepository.getProductsBySeller(uid).collect { res ->
                if (res is com.rio.rostry.utils.Resource.Success) {
                    val list = res.data.orEmpty()
                    val active = list.filter { (it.lifecycleStatus ?: "ACTIVE") == "ACTIVE" && (it.isBatch != true) }
                    _availableMaleParents.value = active.filter { (it.gender ?: "").equals("male", true) }
                    _availableFemaleParents.value = active.filter { (it.gender ?: "").equals("female", true) }
                }
            }
        }
    }

    fun selectMaleParent(p: com.rio.rostry.data.database.entity.ProductEntity) {
        updateLineage(maleId = p.productId, femaleId = _state.value.lineage.femaleParentId, maleName = p.name)
    }

    fun selectFemaleParent(p: com.rio.rostry.data.database.entity.ProductEntity) {
        updateLineage(maleId = _state.value.lineage.maleParentId, femaleId = p.productId, femaleName = p.name)
    }

    fun applyScannedParent(productId: String, expectedGender: String) {
        viewModelScope.launch {
            productRepository.getProductById(productId).collect { res ->
                if (res is Resource.Success) {
                    val p = res.data ?: return@collect
                    val gender = (p.gender ?: "").lowercase()
                    if (expectedGender.equals("male", true) || gender == "male") {
                        selectMaleParent(p)
                    } else if (expectedGender.equals("female", true) || gender == "female") {
                        selectFemaleParent(p)
                    }
                }
            }
        }
    }

    fun updateIsTraceable(v: Boolean) { _state.value = _state.value.copy(isTraceable = v) }
    fun updateAgeGroup(v: OnboardingValidator.AgeGroup) { _state.value = _state.value.copy(ageGroup = v) }
    fun updateCoreDetails(transform: (CoreDetailsState) -> CoreDetailsState) {
        _state.value = _state.value.copy(coreDetails = transform(_state.value.coreDetails))
    }
    fun updateLineage(maleId: String?, femaleId: String?, maleName: String? = null, femaleName: String? = null) {
        _state.value = _state.value.copy(lineage = _state.value.lineage.copy(
            maleParentId = maleId, femaleParentId = femaleId, maleParentName = maleName, femaleParentName = femaleName
        ))
    }
    fun updateMedia(transform: (MediaState) -> MediaState) {
        _state.value = _state.value.copy(media = transform(_state.value.media))
    }

    fun previousStep(onBack: () -> Unit) {
        val s = _state.value
        val prev = when (s.currentStep) {
            WizardStep.PATH_SELECTION -> null
            WizardStep.AGE_GROUP -> WizardStep.PATH_SELECTION
            WizardStep.CORE_DETAILS -> WizardStep.AGE_GROUP
            WizardStep.LINEAGE -> WizardStep.CORE_DETAILS
            WizardStep.PROOFS -> if (s.isTraceable == true) WizardStep.LINEAGE else WizardStep.CORE_DETAILS
            WizardStep.REVIEW -> WizardStep.PROOFS
        }
        if (prev == null) onBack() else _state.value = s.copy(currentStep = prev, validationErrors = emptyMap())
    }

    fun nextStep() {
        val s = _state.value
        // Mappers to validator models
        fun toValidatorCore(d: CoreDetailsState) = OnboardingValidator.CoreDetailsState(
            name = d.name,
            birthDate = d.birthDate,
            birthPlace = d.birthPlace,
            gender = d.gender,
            weightGrams = d.weightGrams,
            heightCm = d.heightCm,
            colors = d.colors,
            breed = d.breed,
            physicalId = d.physicalId,
            vaccinationRecords = d.vaccinationRecords,
            healthStatus = d.healthStatus,
            breedingHistory = d.breedingHistory,
            awards = d.awards
        )
        fun toValidatorLineage(l: LineageState) = OnboardingValidator.LineageState(
            maleParentId = l.maleParentId,
            femaleParentId = l.femaleParentId
        )
        fun toValidatorMedia(m: MediaState) = OnboardingValidator.MediaState(
            photoUris = m.photoUris,
            videoUris = m.videoUris,
            documentUris = m.documentUris
        )
        val errors = when (s.currentStep) {
            WizardStep.PATH_SELECTION -> OnboardingValidator.validatePathSelection(s.isTraceable)
            WizardStep.AGE_GROUP -> OnboardingValidator.validateAgeGroup(s.ageGroup)
            WizardStep.CORE_DETAILS -> {
                if (s.ageGroup == null || s.isTraceable == null) mapOf("step" to "Select previous options") else
                    OnboardingValidator.validateCoreDetails(
                        details = toValidatorCore(s.coreDetails),
                        ageGroup = s.ageGroup,
                        isTraceable = s.isTraceable
                    )
            }
            WizardStep.LINEAGE -> OnboardingValidator.validateLineage(toValidatorLineage(s.lineage), s.isTraceable == true)
            WizardStep.PROOFS -> OnboardingValidator.validateMedia(toValidatorMedia(s.media), s.isTraceable == true)
            WizardStep.REVIEW -> emptyMap()
        }
        if (errors.isNotEmpty()) {
            _state.value = s.copy(validationErrors = errors)
            return
        }
        val next = when (s.currentStep) {
            WizardStep.PATH_SELECTION -> WizardStep.AGE_GROUP
            WizardStep.AGE_GROUP -> WizardStep.CORE_DETAILS
            WizardStep.CORE_DETAILS -> if (s.isTraceable == true) WizardStep.LINEAGE else WizardStep.PROOFS
            WizardStep.LINEAGE -> WizardStep.PROOFS
            WizardStep.PROOFS -> WizardStep.REVIEW
            WizardStep.REVIEW -> WizardStep.REVIEW
        }
        _state.value = s.copy(currentStep = next, validationErrors = emptyMap())
    }

    fun save() {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            _state.value = _state.value.copy(error = "Not signed in")
            return
        }
        val s = _state.value
        if (s.ageGroup == null) {
            _state.value = s.copy(error = "Age group missing")
            return
        }
        val now = System.currentTimeMillis()
        val tentativeId = "bird_${now}_${UUID.randomUUID()}"
        val entity = ProductEntity(
            productId = tentativeId,
            sellerId = uid,
            name = s.coreDetails.name,
            description = s.coreDetails.breed.ifBlank { "" },
            category = "BIRD",
            price = 0.0,
            quantity = 1.0,
            unit = "unit",
            location = "",
            isBatch = false,
            status = "private",
            stage = mapAgeGroupToStage(s.ageGroup),
            lifecycleStatus = "ACTIVE",
            updatedAt = now,
            lastModifiedAt = now,
            dirty = true,
            birthDate = s.coreDetails.birthDate,
            weightGrams = s.coreDetails.weightGrams.toDoubleOrNull(),
            heightCm = s.coreDetails.heightCm.toDoubleOrNull(),
            breed = s.coreDetails.breed,
            color = s.coreDetails.colors,
            parentMaleId = s.lineage.maleParentId,
            parentFemaleId = s.lineage.femaleParentId
        )
        _state.value = s.copy(saving = true, error = null)
        viewModelScope.launch {
            when (val res = productRepository.addProduct(entity)) {
                is Resource.Success -> {
                    val newId = res.data ?: tentativeId
                    // Enqueue uploads with correct id
                    s.media.photoUris.forEachIndexed { idx, uri -> mediaUploadManager.enqueueToOutbox(uri, "products/$newId/photos/$idx") }
                    s.media.documentUris.forEachIndexed { idx, uri -> mediaUploadManager.enqueueToOutbox(uri, "products/$newId/documents/$idx") }
                    // Initialize monitoring seeds
                    farmOnboardingRepository.addProductToFarmMonitoring(newId, uid)
                    // Family tree linkages
                    if (!s.lineage.maleParentId.isNullOrBlank()) {
                        val node = FamilyTreeEntity(
                            nodeId = "ft_${System.currentTimeMillis()}_${UUID.randomUUID()}",
                            productId = newId,
                            parentProductId = s.lineage.maleParentId,
                            childProductId = newId,
                            relationType = "father"
                        )
                        familyTreeRepository.upsert(node)
                    }
                    if (!s.lineage.femaleParentId.isNullOrBlank()) {
                        val node = FamilyTreeEntity(
                            nodeId = "ft_${System.currentTimeMillis()}_${UUID.randomUUID()}",
                            productId = newId,
                            parentProductId = s.lineage.femaleParentId,
                            childProductId = newId,
                            relationType = "mother"
                        )
                        familyTreeRepository.upsert(node)
                    }
                    // Persist media URLs as uploads succeed, filter and timeout
                    val photos = mutableSetOf<String>()
                    val docs = mutableSetOf<String>()
                    val expectedPhotos = s.media.photoUris.size
                    val expectedDocs = s.media.documentUris.size
                    viewModelScope.launch {
                        try {
                            withTimeout(60_000) {
                                mediaUploadManager.events.collect { ev ->
                                    when (ev) {
                                        is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Success -> {
                                            val path = ev.remotePath
                                            if (!path.contains("products/$newId/")) return@collect
                                            if (path.startsWith("products/$newId/photos/")) photos += ev.downloadUrl
                                            else if (path.startsWith("products/$newId/documents/")) docs += ev.downloadUrl
                                            val now2 = System.currentTimeMillis()
                                            productRepository.updateProduct(
                                                entity.copy(
                                                    productId = newId,
                                                    imageUrls = photos.toList(),
                                                    documentUrls = docs.toList(),
                                                    updatedAt = now2,
                                                    lastModifiedAt = now2,
                                                    dirty = true
                                                )
                                            )
                                            if (photos.size >= expectedPhotos && docs.size >= expectedDocs) cancel()
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        } catch (_: kotlinx.coroutines.TimeoutCancellationException) { /* best-effort */ }
                    }
                    _state.value = _state.value.copy(saving = false, savedId = newId)
                }
                is Resource.Error -> _state.value = _state.value.copy(saving = false, error = res.message)
                is Resource.Loading -> {}
            }
        }
    }
}

private fun mapAgeGroupToStage(age: OnboardingValidator.AgeGroup): LifecycleStage = when (age) {
    OnboardingValidator.AgeGroup.CHICK -> LifecycleStage.CHICK
    OnboardingValidator.AgeGroup.JUVENILE -> LifecycleStage.JUVENILE
    OnboardingValidator.AgeGroup.ADULT -> LifecycleStage.ADULT
    OnboardingValidator.AgeGroup.BREEDER -> LifecycleStage.BREEDER
}
