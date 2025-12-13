package com.rio.rostry.ui.verification

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.utils.storage.VerificationStoragePathBuilder
import com.rio.rostry.utils.validation.VerificationValidationService
import com.rio.rostry.notifications.VerificationNotificationService
import com.rio.rostry.security.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.rio.rostry.session.CurrentUserProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import org.json.JSONArray
import org.json.JSONObject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mediaUploadManager: MediaUploadManager,
    private val verificationValidationService: VerificationValidationService,
    private val verificationNotificationService: VerificationNotificationService,
    private val auditLogDao: AuditLogDao,
    @ApplicationContext private val appContext: Context,
    private val currentUserProvider: CurrentUserProvider,
    val placesClient: PlacesClient,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val user: UserEntity? = null,
        val message: String? = null,
        val error: String? = null,
        val uploadProgress: Map<String, Int> = emptyMap(),
        val uploadedDocuments: List<String> = emptyList(),
        val uploadedImages: List<String> = emptyList(),
        val uploadError: String? = null,
        val isSubmitting: Boolean = false,
        val exifWarnings: List<String> = emptyList(),
        val validationErrors: Map<String, String> = emptyMap(),
        val submissionSuccess: Boolean = false,
        val uploadedDocTypes: Map<String, String> = emptyMap(),
        val uploadedImageTypes: Map<String, String> = emptyMap(),
        // New state for location picker
        val selectedPlace: Place? = null,
        val showLocationPicker: Boolean = true,
        // New state for upgrade tracking
        val upgradeType: UpgradeType? = null,
        val currentRole: UserType? = null,
        val targetRole: UserType? = null
    )

    private val _ui = MutableStateFlow(restoreUiState() ?: UiState())
    val ui: StateFlow<UiState> = _ui

    init {
        refresh()
        observeUploadEvents()
        persistState()
    }

    private fun restoreUiState(): UiState? {
        if (!savedStateHandle.contains("uploaded_docs")) return null
        
        return UiState(
            uploadedDocuments = savedStateHandle.get<List<String>>("uploaded_docs") ?: emptyList(),
            uploadedImages = savedStateHandle.get<List<String>>("uploaded_images") ?: emptyList(),
            uploadedDocTypes = (savedStateHandle.get<java.io.Serializable>("doc_types") as? HashMap<String, String>)?.toMap() ?: emptyMap(),
            uploadedImageTypes = (savedStateHandle.get<java.io.Serializable>("img_types") as? HashMap<String, String>)?.toMap() ?: emptyMap(),
            selectedPlace = savedStateHandle.get<Place>("selected_place"),
            showLocationPicker = savedStateHandle.get<Boolean>("show_picker") ?: true,
            upgradeType = savedStateHandle.get<String>("upgrade_type")?.let { try { UpgradeType.valueOf(it) } catch(e: Exception) { null } },
            // Reset transient states
            isLoading = true,
            isSubmitting = false
        )
    }

    private fun persistState() {
        viewModelScope.launch {
            _ui.collectLatest { state ->
                savedStateHandle["uploaded_docs"] = ArrayList(state.uploadedDocuments)
                savedStateHandle["uploaded_images"] = ArrayList(state.uploadedImages)
                savedStateHandle["doc_types"] = HashMap(state.uploadedDocTypes)
                savedStateHandle["img_types"] = HashMap(state.uploadedImageTypes)
                savedStateHandle["selected_place"] = state.selectedPlace
                savedStateHandle["show_picker"] = state.showLocationPicker
                savedStateHandle["upgrade_type"] = state.upgradeType?.name
            }
        }
    }

    fun onPlaceSelected(place: Place) {
        _ui.value = _ui.value.copy(selectedPlace = place, showLocationPicker = false)
    }

    fun changeLocation() {
        _ui.value = _ui.value.copy(showLocationPicker = true)
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            userRepository.getCurrentUser().collectLatest { res ->
                when (res) {
                    is Resource.Success -> {
                        val user = res.data
                        if (user != null) {
                            determineUpgradeType(user)
                            // Fetch verification details separately
                            launch {
                                val submissionRes = userRepository.getVerificationSubmission(user.userId)
                                if (submissionRes is Resource.Success && submissionRes.data != null) {
                                    val submission = submissionRes.data
                                    // ONLY overwrite local draft if status is PENDING or VERIFIED
                                    // If UNVERIFIED/REJECTED, we assume user is fixing/applying, so keep local draft if exists.
                                    val shouldLoadRemote = user.verificationStatus == VerificationStatus.PENDING || 
                                                           user.verificationStatus == VerificationStatus.VERIFIED ||
                                                           (_ui.value.uploadedDocuments.isEmpty() && _ui.value.uploadedImages.isEmpty())

                                    if (shouldLoadRemote) {
                                        val imgTypes = submission.imageUrls.associateWith { "UNKNOWN" }.toMutableMap()
                                        
                                        _ui.value = _ui.value.copy(
                                            user = user,
                                            isLoading = false,
                                            uploadedDocuments = submission.documentUrls,
                                            uploadedImages = submission.imageUrls,
                                            uploadedDocTypes = submission.documentTypes,
                                            uploadedImageTypes = imgTypes
                                        )
                                    } else {
                                        // Keep local draft, just update user info
                                        _ui.value = _ui.value.copy(user = user, isLoading = false)
                                    }
                                } else {
                                    _ui.value = _ui.value.copy(user = user, isLoading = false)
                                }
                            }
                        } else {
                             _ui.value = UiState(user = null, isLoading = false)
                        }
                    }
                    is Resource.Error -> _ui.value = UiState(error = res.message, isLoading = false)
                    is Resource.Loading -> _ui.value = _ui.value.copy(isLoading = true)
                }
            }
        }
    }

    fun setUpgradeType(type: UpgradeType) {
        val targetRole = when (type) {
            UpgradeType.GENERAL_TO_FARMER -> UserType.FARMER
            UpgradeType.FARMER_TO_ENTHUSIAST -> UserType.ENTHUSIAST
            else -> null
        }
        _ui.value = _ui.value.copy(
            upgradeType = type,
            targetRole = targetRole
        )
    }

    private fun determineUpgradeType(user: UserEntity) {
        val upgradeType = when {
            user.role == UserType.GENERAL && user.verificationStatus == VerificationStatus.UNVERIFIED -> UpgradeType.GENERAL_TO_FARMER
            user.role == UserType.FARMER && (user.verificationStatus == VerificationStatus.UNVERIFIED || user.verificationStatus == VerificationStatus.PENDING || user.verificationStatus == VerificationStatus.REJECTED) -> UpgradeType.FARMER_VERIFICATION
            user.role == UserType.FARMER && user.verificationStatus == VerificationStatus.VERIFIED -> UpgradeType.FARMER_TO_ENTHUSIAST
            else -> null
        }
        
        val targetRole = when (upgradeType) {
            UpgradeType.GENERAL_TO_FARMER -> UserType.FARMER
            UpgradeType.FARMER_TO_ENTHUSIAST -> UserType.ENTHUSIAST
            else -> null
        }

        _ui.value = _ui.value.copy(
            upgradeType = upgradeType,
            currentRole = user.role,
            targetRole = targetRole
        )
    }

    fun submitFarmerLocation(lat: Double?, lng: Double?) {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val updated = current.copy(
                farmLocationLat = lat,
                farmLocationLng = lng,
                locationVerified = false,
                updatedAt = System.currentTimeMillis()
            )
            val res = userRepository.updateUserProfile(updated)
            _ui.value = if (res is Resource.Error) {
                _ui.value.copy(isLoading = false, error = res.message)
            } else {
                _ui.value.copy(isLoading = false, message = "Location submitted for verification")
            }
        }
    }

    fun submitEnthusiastKyc(level: Int?) {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val updated = current.copy(
                kycLevel = level,
                updatedAt = System.currentTimeMillis()
            )
            val res = userRepository.updateUserProfile(updated)
            _ui.value = if (res is Resource.Error) {
                _ui.value.copy(isLoading = false, error = res.message)
            } else {
                _ui.value.copy(isLoading = false, message = "KYC submitted for verification")
            }
        }
    }

    fun uploadDocument(localUri: String, documentType: String, upgradeType: UpgradeType) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            when (val vr = verificationValidationService.validateDocumentFile(localUri, appContext)) {
                is VerificationValidationService.ValidationResult.Error -> {
                    _ui.value = _ui.value.copy(uploadError = vr.message)
                    return@launch
                }
                else -> {}
            }
        }
        
        // Use organized path builder
        val uri = android.net.Uri.parse(localUri)
        val mimeType = appContext.contentResolver.getType(uri)
        val extension = when (mimeType) {
            "application/pdf" -> "pdf"
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            else -> localUri.substringAfterLast('.', "pdf")
        }
        val remotePath = VerificationStoragePathBuilder.buildDocumentPath(upgradeType, userId, documentType, extension)
        
        val ctx = JSONObject().apply { 
            put("docType", documentType)
            put("upgradeType", upgradeType.name)
        }.toString()
        mediaUploadManager.enqueueToOutbox(localPath = localUri, remotePath = remotePath, contextJson = ctx)
    }

    fun uploadImage(localUri: String, imageType: String, upgradeType: UpgradeType) {
        val userId = _ui.value.user?.userId ?: return
        viewModelScope.launch {
            val imgVr = verificationValidationService.validateImageFile(localUri, appContext)
            if (imgVr is VerificationValidationService.ValidationResult.Error) {
                _ui.value = _ui.value.copy(uploadError = imgVr.message)
                return@launch
            }
            // If farmer photos and location set, do EXIF validation as a warning-only
            val u = _ui.value.user
            val lat = u?.farmLocationLat
            val lng = u?.farmLocationLng
            if (lat != null && lng != null) {
                val exif = verificationValidationService.validateFarmPhotoLocation(localUri, lat, lng, appContext)
                if (!exif.isValid) {
                    val warns = _ui.value.exifWarnings.toMutableList()
                    exif.errorMessage?.let { warns.add(it) }
                    _ui.value = _ui.value.copy(exifWarnings = warns)
                }
            }
        }
        
        // Use organized path builder
        val uri = android.net.Uri.parse(localUri)
        val mimeType = appContext.contentResolver.getType(uri)
        val extension = when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> localUri.substringAfterLast('.', "jpg")
        }
        val remotePath = VerificationStoragePathBuilder.buildImagePath(upgradeType, userId, imageType, extension)
        
        mediaUploadManager.enqueueToOutbox(localPath = localUri, remotePath = remotePath)
    }

    private fun observeUploadEvents() {
        viewModelScope.launch {
            try {
                mediaUploadManager.events.collect { event ->
                    when (event) {
                        is MediaUploadManager.UploadEvent.Progress -> {
                            val progress = _ui.value.uploadProgress.toMutableMap()
                            progress[event.remotePath] = event.percent
                            _ui.value = _ui.value.copy(uploadProgress = progress)
                        }
                        is MediaUploadManager.UploadEvent.Success -> {
                            val progress = _ui.value.uploadProgress.toMutableMap()
                            progress.remove(event.remotePath)
                            
                            val documents = _ui.value.uploadedDocuments.toMutableList()
                            val images = _ui.value.uploadedImages.toMutableList()
                            val docTypes = _ui.value.uploadedDocTypes.toMutableMap()
                            val imgTypes = _ui.value.uploadedImageTypes.toMutableMap()
                            
                            if (event.remotePath.contains("/documents/")) {
                                if (!documents.contains(event.downloadUrl)) {
                                    documents.add(event.downloadUrl)
                                }
                                // Extract type from remotePath filename: {timestamp}_{TYPE}.xyz
                                val fileName = event.remotePath.substringAfterLast('/')
                                val docType = fileName.substringAfter('_').substringBeforeLast('.').uppercase()
                                docTypes[event.downloadUrl] = docType
                            } else if (event.remotePath.contains("/images/")) {
                                if (!images.contains(event.downloadUrl)) {
                                    images.add(event.downloadUrl)
                                }
                                val fileName = event.remotePath.substringAfterLast('/')
                                val imgType = fileName.substringAfter('_').substringBeforeLast('.').uppercase()
                                imgTypes[event.downloadUrl] = imgType
                            }
                            
                            _ui.value = _ui.value.copy(
                                uploadProgress = progress,
                                uploadedDocuments = documents,
                                uploadedImages = images,
                                uploadedDocTypes = docTypes,
                                uploadedImageTypes = imgTypes
                            )
                        }
                        is MediaUploadManager.UploadEvent.Failed -> {
                            val progress = _ui.value.uploadProgress.toMutableMap()
                            progress.remove(event.remotePath)
                            _ui.value = _ui.value.copy(
                                uploadProgress = progress,
                                uploadError = event.error
                            )
                        }
                        is MediaUploadManager.UploadEvent.Queued -> {
                            val progress = _ui.value.uploadProgress.toMutableMap()
                            progress[event.remotePath] = 0
                            _ui.value = _ui.value.copy(uploadProgress = progress)
                        }
                        is MediaUploadManager.UploadEvent.Retrying -> {
                            // Update UI to show retry attempt
                        }
                        is MediaUploadManager.UploadEvent.Cancelled -> {
                            val progress = _ui.value.uploadProgress.toMutableMap()
                            progress.remove(event.remotePath)
                            _ui.value = _ui.value.copy(uploadProgress = progress)
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore collection errors to keep VM alive
            }
        }
    }

    fun clearUploadError() {
        _ui.value = _ui.value.copy(uploadError = null)
    }

    fun removeUploadedFile(url: String, isDocument: Boolean) {
        val documents = _ui.value.uploadedDocuments.toMutableList()
        val images = _ui.value.uploadedImages.toMutableList()
        
        if (isDocument) {
            documents.remove(url)
        } else {
            images.remove(url)
        }
        
        _ui.value = _ui.value.copy(
            uploadedDocuments = documents,
            uploadedImages = images
        )
    }

    fun submitKycWithDocuments(upgradeType: UpgradeType, passedLat: Double? = null, passedLng: Double? = null) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = false, isSubmitting = true, submissionSuccess = false, error = null, message = null)

            // Fetch the latest user data to preserve recently entered location/KYC level
            val latestUserResource = userRepository.getCurrentUser().first()
            val current = when (latestUserResource) {
                is Resource.Success -> latestUserResource.data
                else -> {
                    _ui.value = _ui.value.copy(
                        isSubmitting = false,
                        error = "Failed to retrieve latest user data"
                    )
                    return@launch
                }
            }

            if (current == null) {
                _ui.value = _ui.value.copy(isSubmitting = false, error = "User not found")
                return@launch
            }

            // Check for resubmission if previously rejected
            val isResubmission = current.verificationStatus == VerificationStatus.REJECTED
            val previousRejectionReason = current.kycRejectionReason // Assuming this field exists in UserEntity

            // Duplicate prevention centralized here
            val statusResource = userRepository.getKycSubmissionStatus(current.userId).first()
            if (statusResource is Resource.Success && statusResource.data != null) {
                val status = statusResource.data
                if (status == "PENDING" || status == "APPROVED") {
                    _ui.value = _ui.value.copy(isSubmitting = false, error = "Verification already submitted. Please wait for review.")
                    return@launch
                }
            }

            // Get location from selected place if available
            val selectedPlace = _ui.value.selectedPlace
            val latFromPlace = selectedPlace?.latLng?.latitude
            val lngFromPlace = selectedPlace?.latLng?.longitude

            // Validate submission
            // Validate submission
            val effectiveLat = passedLat ?: latFromPlace ?: current.farmLocationLat
            val effectiveLng = passedLng ?: lngFromPlace ?: current.farmLocationLng
            
            // Check if submission is already in progress to prevent double clicks
            if (_ui.value.isSubmitting) return@launch

            val validation = if (upgradeType == UpgradeType.GENERAL_TO_FARMER || upgradeType == UpgradeType.FARMER_VERIFICATION) {
                verificationValidationService.validateFarmerSubmission(
                    effectiveLat, effectiveLng, 
                    _ui.value.uploadedImages, _ui.value.uploadedDocuments,
                    _ui.value.uploadedImageTypes, _ui.value.uploadedDocTypes,
                    upgradeType.requiredImages, upgradeType.requiredDocuments
                )
            } else {
                verificationValidationService.validateEnthusiastSubmission(
                    _ui.value.uploadedImages, _ui.value.uploadedDocuments, 
                    _ui.value.uploadedImageTypes, _ui.value.uploadedDocTypes,
                    upgradeType.requiredImages, upgradeType.requiredDocuments
                )
            }
            
            if (validation is VerificationValidationService.ValidationResult.Error) {
                _ui.value = _ui.value.copy(
                    isSubmitting = false, 
                    validationErrors = mapOf((validation.field ?: "general") to validation.message),
                    error = validation.message // Also show as general error
                )
                return@launch
            }

            // Serialize document lists to JSON
            val documentUrlsJson = JSONArray(_ui.value.uploadedDocuments).toString()
            val imageUrlsJson = JSONArray(_ui.value.uploadedImages).toString()

            // Create document types map from tracked types
            val docTypesMap = JSONObject(_ui.value.uploadedDocTypes)

            var updated = current.copy(
                // For farmers, persist location atomically with KYC submission if provided
                farmLocationLat = effectiveLat,
                farmLocationLng = effectiveLng,
                updatedAt = System.currentTimeMillis()
            )

            // If a place was selected, update the structured address fields
            selectedPlace?.let { place ->
                updated = updated.copy(
                    farmAddressLine1 = place.addressComponents?.asList()?.find { "street_number" in it.types }?.name?.let { streetNumber ->
                        place.addressComponents?.asList()?.find { "route" in it.types }?.name?.let { route ->
                            "$streetNumber $route"
                        }
                    } ?: place.addressComponents?.asList()?.find { "route" in it.types }?.name,
                    farmCity = place.getAddressComponent("locality"),
                    farmState = place.getAddressComponent("administrative_area_level_1"),
                    farmPostalCode = place.getAddressComponent("postal_code"),
                    farmCountry = place.getAddressComponent("country")
                )
            }

            val res = userRepository.updateUserProfile(updated)
            if (res is Resource.Error) {
                _ui.value = _ui.value.copy(isSubmitting = false, error = res.message)
            } else {
                // Send pending notification (non-blocking)
                runCatching { verificationNotificationService.notifyVerificationPending(current.userId ?: "") }

                // Create audit log for submission - do this before submission attempt
                val gson = Gson()
                val action = if (isResubmission) "KYC_RESUBMITTED" else "KYC_SUBMITTED"
                val details = mutableMapOf<String, Any?>(
                    "documentCount" to _ui.value.uploadedDocuments.size,
                    "imageCount" to _ui.value.uploadedImages.size,
                    "upgradeType" to upgradeType.name
                )
                if (isResubmission) {
                    details["previousRejectionReason"] = previousRejectionReason
                }
                auditLogDao.insert(
                    AuditLogEntity(
                        logId = UUID.randomUUID().toString(),
                        type = "VERIFICATION",
                        refId = current.userId,
                        action = action,
                        actorUserId = current.userId,
                        detailsJson = gson.toJson(details),
                        createdAt = System.currentTimeMillis()
                    )
                )

                val submissionId = UUID.randomUUID().toString()
                
                // Determine target role based on upgrade type
                val targetRole = when(upgradeType) {
                    UpgradeType.GENERAL_TO_FARMER -> UserType.FARMER
                    UpgradeType.FARMER_TO_ENTHUSIAST -> UserType.ENTHUSIAST
                    else -> null
                }
                
                val submissionResult = userRepository.submitKycVerification(
                    userId = current.userId, 
                    submissionId = submissionId, 
                    documentUrls = _ui.value.uploadedDocuments, 
                    imageUrls = _ui.value.uploadedImages, 
                    docTypes = _ui.value.uploadedDocTypes,
                    upgradeType = upgradeType,
                    currentRole = current.role,
                    targetRole = targetRole
                )

                // Handle the submission result - only set success after submitKycVerification succeeds
                when (submissionResult) {
                    is Resource.Error -> {
                        // Set a clear, user-facing error message that distinguishes network errors from permission problems
                        val userFacingError = when {
                            submissionResult.message?.contains("permission", ignoreCase = true) == true -> {
                                "Permission denied. Please contact support with code: PERMISSION_ERROR_${current.userId.takeLast(6)}"
                            }
                            submissionResult.message?.contains("network", ignoreCase = true) == true ||
                            submissionResult.message?.contains("timeout", ignoreCase = true) == true -> {
                                "Network error. Check your internet connection and try again."
                            }
                            else -> {
                                "Submission failed: ${submissionResult.message}. Please try again."
                            }
                        }
                        _ui.value = _ui.value.copy(
                            isSubmitting = false,
                            submissionSuccess = false,
                            error = userFacingError
                        )
                        SecurityManager.audit("KYC_SUBMIT", mapOf(
                            "userId" to current.userId,
                            "submissionId" to submissionId,
                            "documentCount" to _ui.value.uploadedDocuments.size,
                            "status" to "FAILED",
                            "error" to submissionResult.message
                        ))
                    }
                    is Resource.Success -> {
                        // Update local user status to PENDING
                        val pendingUser = current.copy(
                            verificationStatus = VerificationStatus.PENDING,
                            kycRejectionReason = null
                        )

                        _ui.value = _ui.value.copy(
                            user = pendingUser,
                            isSubmitting = false,
                            submissionSuccess = true,
                            message = "Submitted for verification",
                            error = null
                        )
                        SecurityManager.audit("KYC_SUBMIT", mapOf(
                            "userId" to current.userId,
                            "submissionId" to submissionId,
                            "documentCount" to _ui.value.uploadedDocuments.size,
                            "status" to "SUCCESS"
                        ))
                    }
                    is Resource.Loading -> {
                        // This shouldn't happen since submitKycVerification is a suspend function returning Resource
                        // but handle it gracefully anyway
                        _ui.value = _ui.value.copy(isSubmitting = true)
                    }
                }
            }
        }
    }

    // Helper function to extract address components
    private fun Place.getAddressComponent(type: String): String? {
        return addressComponents?.asList()?.find { it.types.contains(type) }?.name
    }

    // Retry submission if it failed previously
    fun retryKycSubmission() {
        if (!_ui.value.isSubmitting && _ui.value.error != null) {
            val type = _ui.value.upgradeType ?: UpgradeType.FARMER_VERIFICATION // Default fallback
            submitKycWithDocuments(type)
        }
    }

    // Update only farm location in profile without touching verificationStatus
    fun updateFarmLocation(lat: Double, lng: Double) {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            val updated = current.copy(
                farmLocationLat = lat,
                farmLocationLng = lng,
                updatedAt = System.currentTimeMillis()
            )
            userRepository.updateUserProfile(updated)
        }
    }

    fun handleRejection(reason: String) {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            val updated = current.copy(
                verificationStatus = VerificationStatus.REJECTED,
                kycRejectionReason = reason,
                updatedAt = System.currentTimeMillis()
            )
            val res = userRepository.updateUserProfile(updated)
            if (res is Resource.Success) {
                // Create audit log for rejection
                val gson = Gson()
                auditLogDao.insert(
                    AuditLogEntity(
                        logId = UUID.randomUUID().toString(),
                        type = "VERIFICATION",
                        refId = current.userId,
                        action = "KYC_REJECTED",
                        actorUserId = currentUserProvider.userIdOrNull(),
                        detailsJson = gson.toJson(mapOf("rejectionReason" to reason)),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun handleApproval() {
        val current = _ui.value.user ?: return
        viewModelScope.launch {
            val updated = current.copy(
                verificationStatus = VerificationStatus.VERIFIED,
                updatedAt = System.currentTimeMillis()
            )
            val res = userRepository.updateUserProfile(updated)
            if (res is Resource.Success) {
                // Create audit log for approval
                val gson = Gson()
                auditLogDao.insert(
                    AuditLogEntity(
                        logId = UUID.randomUUID().toString(),
                        type = "VERIFICATION",
                        refId = current.userId,
                        action = "KYC_APPROVED",
                        actorUserId = currentUserProvider.userIdOrNull(),
                        detailsJson = gson.toJson(emptyMap<String, Any>()),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
