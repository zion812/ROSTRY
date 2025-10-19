package com.rio.rostry.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.monitoring.FarmOnboardingRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.database.entity.FamilyTreeEntity
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
import com.rio.rostry.utils.validation.OnboardingValidator
import android.content.Context
import com.rio.rostry.utils.notif.FarmNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.rio.rostry.ui.navigation.Routes
import com.rio.rostry.data.sync.SyncManager
import com.rio.rostry.security.SecurityManager
import kotlinx.coroutines.delay

@HiltViewModel
class OnboardFarmBatchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val farmOnboardingRepository: FarmOnboardingRepository,
    private val mediaUploadManager: MediaUploadManager,
    private val familyTreeRepository: FamilyTreeRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val syncManager: SyncManager,
    private val securityManager: SecurityManager
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _refreshEvent = MutableSharedFlow<Unit>()
    val refreshEvent = _refreshEvent.asSharedFlow()

    enum class WizardStep { PATH_SELECTION, AGE_GROUP, BATCH_DETAILS, LINEAGE, PROOFS, REVIEW }

    data class BatchDetailsState(
        val batchName: String = "",
        val count: String = "",
        val hatchDate: Long? = null,
        val breed: String = "",
        val avgWeightGrams: String = "",
        val vaccinationRecords: String = "",
        val healthStatus: String = "OK"
    )

    data class LineageState(
        val maleParentId: String? = null,
        val femaleParentId: String? = null
    )

    data class MediaState(
        val photoUris: List<String> = emptyList(),
        val videoUris: List<String> = emptyList(),
        val documentUris: List<String> = emptyList()
    )

    data class WizardState(
        val currentStep: WizardStep = WizardStep.PATH_SELECTION,
        val isTraceable: Boolean? = null,
        val ageGroup: com.rio.rostry.utils.validation.OnboardingValidator.AgeGroup? = null,
        val batchDetails: BatchDetailsState = BatchDetailsState(),
        val lineage: LineageState = LineageState(),
        val media: MediaState = MediaState(),
        val validationErrors: Map<String, String> = emptyMap(),
        val saving: Boolean = false,
        val error: String? = null,
        val savedId: String? = null,
        val uploadProgress: Map<String, Int> = emptyMap(),
        val uploadStatus: Map<String, String> = emptyMap()
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

    fun updateIsTraceable(v: Boolean) { _state.value = _state.value.copy(isTraceable = v) }
    fun updateAgeGroup(v: com.rio.rostry.utils.validation.OnboardingValidator.AgeGroup) { _state.value = _state.value.copy(ageGroup = v) }
    fun updateBatchDetails(transform: (BatchDetailsState) -> BatchDetailsState) { _state.value = _state.value.copy(batchDetails = transform(_state.value.batchDetails)) }
    fun updateLineage(maleId: String?, femaleId: String?) { _state.value = _state.value.copy(lineage = LineageState(maleId, femaleId)) }
    fun updateMedia(transform: (MediaState) -> MediaState) { _state.value = _state.value.copy(media = transform(_state.value.media)) }

    fun setError(message: String?) {
        _state.value = _state.value.copy(error = message)
    }

    // Available parents
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
        updateLineage(maleId = p.productId, femaleId = _state.value.lineage.femaleParentId)
    }
    fun selectFemaleParent(p: com.rio.rostry.data.database.entity.ProductEntity) {
        updateLineage(maleId = _state.value.lineage.maleParentId, femaleId = p.productId)
    }

    fun applyScannedParent(productId: String, expectedGender: String) {
        viewModelScope.launch {
            productRepository.getProductById(productId).collect { res ->
                if (res is com.rio.rostry.utils.Resource.Success) {
                    val p = res.data ?: return@collect
                    if (p.sellerId != firebaseAuth.currentUser?.uid) {
                        _state.value = _state.value.copy(error = "You can only link your own products as parents")
                        securityManager.audit("LINEAGE_PARENT_REJECTED", mapOf("productId" to productId, "reason" to "ownership_mismatch"))
                        return@collect
                    }
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

    fun previousStep(onBack: () -> Unit) {
        val s = _state.value
        val prev = when (s.currentStep) {
            WizardStep.PATH_SELECTION -> null
            WizardStep.AGE_GROUP -> WizardStep.PATH_SELECTION
            WizardStep.BATCH_DETAILS -> WizardStep.AGE_GROUP
            WizardStep.LINEAGE -> WizardStep.BATCH_DETAILS
            WizardStep.PROOFS -> if (s.isTraceable == true) WizardStep.LINEAGE else WizardStep.BATCH_DETAILS
            WizardStep.REVIEW -> WizardStep.PROOFS
        }
        if (prev == null) onBack() else _state.value = s.copy(currentStep = prev, validationErrors = emptyMap())
    }

    fun nextStep() {
        val s = _state.value
        val errors = when (s.currentStep) {
            WizardStep.PATH_SELECTION -> com.rio.rostry.utils.validation.OnboardingValidator.validatePathSelection(s.isTraceable)
            WizardStep.AGE_GROUP -> com.rio.rostry.utils.validation.OnboardingValidator.validateAgeGroup(s.ageGroup)
            WizardStep.BATCH_DETAILS -> {
                val e = mutableMapOf<String, String>()
                if (s.batchDetails.batchName.isBlank()) e["batchName"] = "Batch name is required"
                val count = s.batchDetails.count.toIntOrNull() ?: 0
                if (count < 2) e["count"] = "Count must be at least 2"
                if (s.batchDetails.hatchDate == null) e["hatchDate"] = "Hatch date required"
                e
            }
            WizardStep.LINEAGE -> com.rio.rostry.utils.validation.OnboardingValidator.validateLineage(
                com.rio.rostry.utils.validation.OnboardingValidator.LineageState(s.lineage.maleParentId, s.lineage.femaleParentId),
                s.isTraceable == true
            )
            WizardStep.PROOFS -> com.rio.rostry.utils.validation.OnboardingValidator.validateMedia(
                com.rio.rostry.utils.validation.OnboardingValidator.MediaState(s.media.photoUris, s.media.videoUris, s.media.documentUris),
                s.isTraceable == true
            )
            WizardStep.REVIEW -> emptyMap()
        }
        if (errors.isNotEmpty()) { _state.value = s.copy(validationErrors = errors); return }
        val next = when (s.currentStep) {
            WizardStep.PATH_SELECTION -> WizardStep.AGE_GROUP
            WizardStep.AGE_GROUP -> WizardStep.BATCH_DETAILS
            WizardStep.BATCH_DETAILS -> if (s.isTraceable == true) WizardStep.LINEAGE else WizardStep.PROOFS
            WizardStep.LINEAGE -> WizardStep.PROOFS
            WizardStep.PROOFS -> WizardStep.REVIEW
            WizardStep.REVIEW -> WizardStep.REVIEW
        }
        _state.value = s.copy(currentStep = next, validationErrors = emptyMap())
    }

    fun save() {
        val uid = firebaseAuth.currentUser?.uid ?: run {
            _state.value = _state.value.copy(error = "Not signed in"); return
        }
        val s = _state.value
        val now = System.currentTimeMillis()
        val productId = "batch_${now}_${UUID.randomUUID()}"
        val count = s.batchDetails.count.toIntOrNull() ?: 0
        val entity = ProductEntity(
            productId = productId,
            sellerId = uid,
            name = s.batchDetails.batchName,
            description = s.batchDetails.breed.ifBlank { "" },
            category = "BATCH",
            price = 0.0,
            quantity = count.toDouble(),
            unit = "birds",
            location = "",
            isBatch = true,
            status = "private",
            stage = mapAgeGroupToStage(s.ageGroup ?: OnboardingValidator.AgeGroup.CHICK),
            lifecycleStatus = "ACTIVE",
            updatedAt = now,
            lastModifiedAt = now,
            dirty = true,
            birthDate = s.batchDetails.hatchDate,
            breed = s.batchDetails.breed,
            parentMaleId = s.lineage.maleParentId,
            parentFemaleId = s.lineage.femaleParentId
        )
        _state.value = s.copy(saving = true, error = null)
        viewModelScope.launch {
            when (val res = productRepository.addProduct(entity)) {
                is Resource.Success -> {
                    val newId = res.data ?: productId
                    // enqueue media uploads with correct id
                    s.media.photoUris.forEachIndexed { idx, uri -> mediaUploadManager.enqueueToOutbox(uri, "products/$newId/photos/$idx") }
                    s.media.documentUris.forEachIndexed { idx, uri -> mediaUploadManager.enqueueToOutbox(uri, "products/$newId/documents/$idx") }
                    farmOnboardingRepository.addProductToFarmMonitoring(newId, uid)
                    // Family tree linkages for batch to parents
                    if (!_state.value.lineage.maleParentId.isNullOrBlank()) {
                        val node = FamilyTreeEntity(
                            nodeId = "ft_${System.currentTimeMillis()}_${UUID.randomUUID()}",
                            productId = newId,
                            parentProductId = _state.value.lineage.maleParentId,
                            childProductId = newId,
                            relationType = "father"
                        )
                        familyTreeRepository.upsert(node)
                    }
                    if (!_state.value.lineage.femaleParentId.isNullOrBlank()) {
                        val node = FamilyTreeEntity(
                            nodeId = "ft_${System.currentTimeMillis()}_${UUID.randomUUID()}",
                            productId = newId,
                            parentProductId = _state.value.lineage.femaleParentId,
                            childProductId = newId,
                            relationType = "mother"
                        )
                        familyTreeRepository.upsert(node)
                    }
                    // Track upload progress and status, persist partial results incrementally
                    val allRemotePaths = mutableSetOf<String>()
                    s.media.photoUris.forEachIndexed { idx, _ -> allRemotePaths.add("products/$newId/photos/$idx") }
                    s.media.documentUris.forEachIndexed { idx, _ -> allRemotePaths.add("products/$newId/documents/$idx") }
                    val initialProgress = allRemotePaths.associateWith { 0 }
                    val initialStatus = allRemotePaths.associateWith { "PENDING" }
                    _state.value = _state.value.copy(uploadProgress = initialProgress, uploadStatus = initialStatus)
                    val photos = mutableSetOf<String>()
                    val docs = mutableSetOf<String>()
                    val uploadStartTime = System.currentTimeMillis()
                    viewModelScope.launch {
                        mediaUploadManager.events.collect { ev ->
                            when (ev) {
                                is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Progress -> {
                                    if (ev.remotePath in allRemotePaths) {
                                        _state.value = _state.value.copy(uploadProgress = _state.value.uploadProgress + (ev.remotePath to ev.percent))
                                    }
                                }
                                is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Success -> {
                                    val path = ev.remotePath
                                    if (path in allRemotePaths) {
                                        if (path.startsWith("products/$newId/photos/")) {
                                            photos += ev.downloadUrl
                                        } else if (path.startsWith("products/$newId/documents/")) {
                                            docs += ev.downloadUrl
                                        }
                                        _state.value = _state.value.copy(
                                            uploadProgress = _state.value.uploadProgress + (path to 100),
                                            uploadStatus = _state.value.uploadStatus + (path to "SUCCESS")
                                        )
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
                                    }
                                }
                                is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Failed -> {
                                    if (ev.remotePath in allRemotePaths) {
                                        _state.value = _state.value.copy(uploadStatus = _state.value.uploadStatus + (ev.remotePath to "FAILED"))
                                    }
                                }
                                is com.rio.rostry.utils.media.MediaUploadManager.UploadEvent.Queued -> {
                                    if (ev.remotePath in allRemotePaths) {
                                        _state.value = _state.value.copy(uploadStatus = _state.value.uploadStatus + (ev.remotePath to "UPLOADING"))
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                    // Audit if uploads exceed 120s threshold
                    viewModelScope.launch {
                        delay(120_000)
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - uploadStartTime >= 120_000 && _state.value.uploadStatus.values.any { it == "PENDING" || it == "UPLOADING" }) {
                            securityManager.audit("MEDIA_UPLOAD_TIMEOUT", mapOf("productId" to newId, "elapsedMs" to (currentTime - uploadStartTime)))
                        }
                    }
                    _state.value = _state.value.copy(saving = false, savedId = newId)
                    FarmNotifier.notifyBirdOnboarded(context, s.batchDetails.batchName, newId)
                    // Trigger a background sync so dashboards refresh promptly
                    runCatching { syncManager.syncAll() }
                    _navigationEvent.emit(Routes.Builders.dailyLog(newId))
                    _refreshEvent.emit(Unit)
                }
                is Resource.Error -> _state.value = _state.value.copy(saving = false, error = res.message)
                is Resource.Loading -> {}
            }
        }
    }

    private fun mapAgeGroupToStage(age: OnboardingValidator.AgeGroup): LifecycleStage = when (age) {
        OnboardingValidator.AgeGroup.CHICK -> LifecycleStage.CHICK
        OnboardingValidator.AgeGroup.JUVENILE -> LifecycleStage.JUVENILE
        OnboardingValidator.AgeGroup.ADULT -> LifecycleStage.ADULT
        OnboardingValidator.AgeGroup.BREEDER -> LifecycleStage.BREEDER
    }
}
