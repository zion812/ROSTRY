package com.rio.rostry.ui.verification

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.FarmLocation
import com.rio.rostry.domain.model.LocationComponent
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.notifications.VerificationNotificationService
import com.rio.rostry.security.SecurityManager
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.ui.verification.state.VerificationFormState
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.utils.storage.VerificationStoragePathBuilder
import com.rio.rostry.utils.validation.VerificationValidationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import com.rio.rostry.domain.verification.VerificationRequirementProvider

import com.rio.rostry.domain.verification.VerificationRequirements
import com.rio.rostry.data.repository.VerificationDraftRepository
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mediaUploadManager: MediaUploadManager,
    private val verificationValidationService: VerificationValidationService,
    private val verificationNotificationService: VerificationNotificationService,
    private val auditLogDao: AuditLogDao,
    private val verificationRequirementProvider: VerificationRequirementProvider,
    @ApplicationContext private val appContext: Context,
    private val currentUserProvider: CurrentUserProvider,
    val placesClient: PlacesClient,
    private val savedStateHandle: SavedStateHandle,
    private val draftRepository: VerificationDraftRepository,
    private val uploadTaskDao: UploadTaskDao
) : ViewModel() {

    // Keep UiState compatible with View for now, but sourced from FormState
    data class UiState(
        val isLoading: Boolean = false,
        val user: UserEntity? = null,
        val message: String? = null,
        val error: String? = null,
        // Derived from FormState
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
        val selectedPlace: FarmLocation? = null,
        val showLocationPicker: Boolean = false,
        val upgradeType: UpgradeType? = null,
        val currentRole: UserType? = null,
        val targetRole: UserType? = null,
        // Dynamic Requirements
        val requiredDocuments: List<String> = emptyList(),
        val requiredImages: List<String> = emptyList(),
        val canEdit: Boolean = true,
        val lastSavedAt: Long? = null,
        // Contact information for farmer verification
        val contactPhone: String = "",
        val farmDescription: String = ""
    )

    private val gson = Gson()
    
    private val _requirements = MutableStateFlow(VerificationRequirements(emptyList(), emptyList()))
    private val _userData = MutableStateFlow<UserEntity?>(null)
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _lastSavedAt = MutableStateFlow<Long?>(null)

    private val _formState = savedStateHandle.getStateFlow("verification_form_state", VerificationFormState())

    private var autoSaveJob: Job? = null

    private fun updateFormState(update: (VerificationFormState) -> VerificationFormState) {
        val current = _formState.value
        val new = update(current)
        savedStateHandle["verification_form_state"] = new
    }

    val ui: StateFlow<UiState> = combine(
        _formState,
        _userData,
        _validationErrors,
        _requirements,
        _lastSavedAt
    ) { form: VerificationFormState, user: UserEntity?, validationErrors: Map<String, String>, reqs: VerificationRequirements, lastSaved: Long? ->
        UiState(
            isLoading = user == null,
            user = user,
            message = null,
            error = form.error,
            uploadProgress = form.uploadProgress,
            uploadedDocuments = form.uploadedDocuments,
            uploadedImages = form.uploadedImages,
            uploadError = form.uploadError,
            isSubmitting = form.isSubmitting,
            exifWarnings = form.exifWarnings,
            validationErrors = validationErrors,
            submissionSuccess = form.submissionSuccess,
            uploadedDocTypes = form.uploadedDocTypes,
            uploadedImageTypes = form.uploadedImageTypes,
            selectedPlace = form.farmLocation,
            showLocationPicker = form.showLocationPicker || (form.farmLocation == null && user?.farmLocationLat == null && user?.farmLocationLng == null),
            upgradeType = form.upgradeType,
            currentRole = user?.role,
            targetRole = calculateTargetRole(form.upgradeType),
            requiredDocuments = reqs.requiredDocuments,
            requiredImages = reqs.requiredImages,
            canEdit = user?.verificationStatus == VerificationStatus.UNVERIFIED || user?.verificationStatus == VerificationStatus.REJECTED,
            lastSavedAt = lastSaved,
            contactPhone = form.contactPhone,
            farmDescription = form.farmDescription
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState(isLoading = true)
    )

    init {
        // Reset transient flags that shouldn't persist across process death
        updateFormState { it.copy(isSubmitting = false) }
        
        // Restore pending uploads monitoring after process death
        _formState.value.uploadProgress.keys.forEach { remotePath ->
            mediaUploadManager.retrack(remotePath)
        }
        
        // Restore requirements immediately if type is known
        _formState.value.upgradeType?.let { updateRequirements(it) }

        loadDraft()
        refresh()
        observeUploadEvents()
        startAutoSave()
    }

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            _formState
                .debounce(2000L)
                .distinctUntilChanged()
                .collectLatest { state ->
                    val userId = currentUserProvider.userIdOrNull() ?: return@collectLatest
                    // Only save if there's meaningful data or if it's already a draft
                    if (state.farmLocation != null || state.uploadedImages.isNotEmpty() || state.uploadedDocuments.isNotEmpty()) {
                        draftRepository.saveDraft(userId, state)
                        _lastSavedAt.value = System.currentTimeMillis()
                    }
                }
        }
    }

    private fun loadDraft() {
        viewModelScope.launch {
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            val draft = draftRepository.loadDraft(userId)
            if (draft != null) {
                // Logic: Restore if current state is default and draft has data
                val current = _formState.value
                if (current.uploadedImages.isEmpty() && current.uploadedDocuments.isEmpty() && current.farmLocation == null) {
                    updateFormState { draft.copy(isSubmitting = false, submissionSuccess = false) }
                    
                    // Re-attach to any uploads mentioned in database or draft
                    val incompleteTasks = uploadTaskDao.getIncompleteByUser(userId)
                    val pathsToTrack = (draft.uploadProgress.keys + incompleteTasks.map { it.remotePath }).distinct()
                    pathsToTrack.forEach { remotePath ->
                        mediaUploadManager.retrack(remotePath)
                    }
                    
                    // Update requirements for restored type
                    draft.upgradeType?.let { updateRequirements(it) }
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            // Debounce user data to prevent rapid UI updates during profile changes
            userRepository.getCurrentUser()
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { resource ->
                    if (resource is Resource.Success) {
                        val user = resource.data
                        _userData.value = user
                        user?.let { determineUpgradeType(it) }
                    }
                }
        }
    }
    
    private fun updateRequirements(type: UpgradeType?) {
        if (type == null) {
            _requirements.value = VerificationRequirements(emptyList(), emptyList())
            return
        }
        viewModelScope.launch {
            val reqs = verificationRequirementProvider.getRequirements(type)
             _requirements.value = reqs
        }
    }

    fun onPlaceSelected(place: Place) {
        val location = mapPlaceToFarmLocation(place)
        updateFormState { it.copy(farmLocation = location, showLocationPicker = false) }
    }

    fun changeLocation() {
        updateFormState { it.copy(showLocationPicker = true) }
    }

    fun cancelLocationChange() {
        updateFormState { it.copy(showLocationPicker = false) }
    }

    /**
     * Accept a FarmLocation directly (from SimpleLocationPicker or other GPS-based sources).
     * This bypasses the need for Google Places API.
     */
    fun onFarmLocationSelected(location: FarmLocation) {
        updateFormState { it.copy(farmLocation = location, showLocationPicker = false) }
    }

    fun updateContactPhone(phone: String) {
        updateFormState { it.copy(contactPhone = phone) }
    }

    fun updateFarmDescription(description: String) {
        updateFormState { it.copy(farmDescription = description) }
    }

    fun setUpgradeType(type: UpgradeType) {
        val targetRole = calculateTargetRole(type)
        val formUpdate = { state: VerificationFormState ->
             state.copy(upgradeType = type)
        }
        updateFormState(formUpdate)
        updateRequirements(type)
    }

    // (Inside determineUpgradeType call updateRequirements)
    private fun determineUpgradeType(user: UserEntity) {
        val computedType = when {
            user.role == UserType.GENERAL && user.verificationStatus == VerificationStatus.UNVERIFIED -> UpgradeType.GENERAL_TO_FARMER
            user.role == UserType.FARMER && (user.verificationStatus != VerificationStatus.VERIFIED) -> UpgradeType.FARMER_VERIFICATION
            user.role == UserType.FARMER && user.verificationStatus == VerificationStatus.VERIFIED -> UpgradeType.FARMER_TO_ENTHUSIAST
            else -> null
        }
        computedType?.let { type ->
            if (_formState.value.upgradeType == null) {
                 updateFormState { it.copy(upgradeType = type) }
                 updateRequirements(type)
            } else {
                 // Even if already set, ensure requirements are loaded
                 updateRequirements(_formState.value.upgradeType)
            }
        }
    }
    
    // (Rest of file updates)


    private suspend fun cacheFileLocally(uriString: String): String? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val uri = android.net.Uri.parse(uriString)
            val extension = getFileExtension(uri) ?: "jpg"
            val file = java.io.File.createTempFile("upload_${UUID.randomUUID()}", ".$extension", appContext.cacheDir)
            
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                java.io.FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uploadDocument(localUri: String, documentType: String, upgradeType: UpgradeType) {
        viewModelScope.launch {
            val user = _userData.value ?: userRepository.getCurrentUser().firstOrNull()?.data ?: run {
                updateFormState { it.copy(uploadError = "User session not found") }
                return@launch
            }
            val userId = user.userId
            
             val vr = verificationValidationService.validateDocumentFile(localUri, appContext)
             if (vr is VerificationValidationService.ValidationResult.Error) {
                 updateFormState { it.copy(uploadError = vr.message) }
                 return@launch
             }

             val uri = android.net.Uri.parse(localUri)
             val extension = getFileExtension(uri)
             val remotePath = VerificationStoragePathBuilder.buildDocumentPath(upgradeType, userId, documentType, extension)
             
             updateFormState {
                 it.copy(uploadProgress = it.uploadProgress + (remotePath to 0))
             }

             // Cache file locally to ensure worker can access it even if permission is lost
             val cachedPath = cacheFileLocally(localUri)
             if (cachedPath == null) {
                 updateFormState { it.copy(uploadError = "Failed to process file for upload") }
                 return@launch
             }

             val ctx = JSONObject().apply { 
                 put("docType", documentType)
                 put("upgradeType", upgradeType.name)
             }.toString()
             // Pass the cached local path (file://) instead of content:// URI
             mediaUploadManager.enqueueToOutbox(localPath = "file://$cachedPath", remotePath = remotePath, contextJson = ctx)
        }
    }

    fun uploadImage(localUri: String, imageType: String, upgradeType: UpgradeType) {
        viewModelScope.launch {
            val user = _userData.value ?: userRepository.getCurrentUser().firstOrNull()?.data ?: run {
                 updateFormState { it.copy(uploadError = "User session not found") }
                 return@launch
            }
            val userId = user.userId

            val imgVr = verificationValidationService.validateImageFile(localUri, appContext)
            if (imgVr is VerificationValidationService.ValidationResult.Error) {
                updateFormState { it.copy(uploadError = imgVr.message) }
                return@launch
            }
            
            // Validation: Check GPS EXIF Data against User/Form location
            val userLat = user.farmLocationLat ?: _formState.value.farmLocation?.lat
            val userLng = user.farmLocationLng ?: _formState.value.farmLocation?.lng
            
            if (userLat != null && userLng != null) {
                val exif = verificationValidationService.validateFarmPhotoLocation(localUri, userLat, userLng, appContext)
                if (!exif.isValid) {
                     val newWarning = exif.errorMessage ?: "Location mismatch"
                     updateFormState { it.copy(exifWarnings = it.exifWarnings + newWarning) }
                }
            }

            val uri = android.net.Uri.parse(localUri)
            val extension = getFileExtension(uri)
            val remotePath = VerificationStoragePathBuilder.buildImagePath(upgradeType, userId, imageType, extension)
            
            updateFormState {
                 it.copy(uploadProgress = it.uploadProgress + (remotePath to 0))
            }
            
            // Cache file locally to ensure worker can access it even if permission is lost
            val cachedPath = cacheFileLocally(localUri)
            if (cachedPath == null) {
                 updateFormState { it.copy(uploadError = "Failed to process file for upload") }
                 return@launch
            }

            // Pass the cached local path (file://) instead of content:// URI
            mediaUploadManager.enqueueToOutbox(localPath = "file://$cachedPath", remotePath = remotePath)
        }
    }

    private fun observeUploadEvents() {
        viewModelScope.launch {
            mediaUploadManager.events.collect { event ->
                when (event) {
                    is MediaUploadManager.UploadEvent.Progress -> {
                        updateFormState { it.copy(uploadProgress = it.uploadProgress + (event.remotePath to event.percent)) }
                    }
                    is MediaUploadManager.UploadEvent.Success -> {
                        handleUploadSuccess(event.remotePath, event.downloadUrl)
                    }
                    is MediaUploadManager.UploadEvent.Failed -> {
                        updateFormState { it.copy(
                            uploadProgress = it.uploadProgress - event.remotePath,
                            uploadError = event.error
                        )}
                    }
                    is MediaUploadManager.UploadEvent.Queued -> {
                        updateFormState { it.copy(uploadProgress = it.uploadProgress + (event.remotePath to 0)) }
                    }
                    is MediaUploadManager.UploadEvent.Cancelled -> {
                        updateFormState { it.copy(uploadProgress = it.uploadProgress - event.remotePath) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleUploadSuccess(remotePath: String, downloadUrl: String) {
        updateFormState { current ->
            val newProgress = current.uploadProgress - remotePath
            // Best effort type extraction from path
            val fileName = remotePath.substringAfterLast('/')
            val type = fileName.substringAfter('_').substringBeforeLast('.').uppercase()

            if (remotePath.contains("/documents/")) {
                if (!current.uploadedDocuments.contains(downloadUrl)) {
                    current.copy(
                        uploadProgress = newProgress,
                        uploadedDocuments = current.uploadedDocuments + downloadUrl,
                        uploadedDocTypes = current.uploadedDocTypes + (downloadUrl to type)
                    )
                } else current.copy(uploadProgress = newProgress)
            } else {
                if (!current.uploadedImages.contains(downloadUrl)) {
                     current.copy(
                        uploadProgress = newProgress,
                        uploadedImages = current.uploadedImages + downloadUrl,
                        uploadedImageTypes = current.uploadedImageTypes + (downloadUrl to type)
                    )
                } else current.copy(uploadProgress = newProgress)
            }
        }
    }

    fun removeUploadedFile(url: String, isDocument: Boolean) {
        updateFormState { current ->
            if (isDocument) {
                current.copy(uploadedDocuments = current.uploadedDocuments - url, uploadedDocTypes = current.uploadedDocTypes - url)
            } else {
                current.copy(uploadedImages = current.uploadedImages - url, uploadedImageTypes = current.uploadedImageTypes - url)
            }
        }
    }

    fun submitKycWithDocuments(upgradeType: UpgradeType, enthusiastLevel: Int? = null) {
        // Guard against double-submission (e.g., rapid taps)
        if (_formState.value.isSubmitting) {
            return
        }
        
        viewModelScope.launch {
            updateFormState { it.copy(isSubmitting = true, error = null, submissionSuccess = false) }
            val user = _userData.value ?: run {
                updateFormState { it.copy(isSubmitting = false, error = "User not loaded") }
                return@launch
            }
            
            val form = _formState.value
            val lat = form.farmLocation?.lat ?: user.farmLocationLat
            val lng = form.farmLocation?.lng ?: user.farmLocationLng
            
            // Client-side Validation (Double check)
            val validationError = if (upgradeType == UpgradeType.GENERAL_TO_FARMER || upgradeType == UpgradeType.FARMER_VERIFICATION) {
                 verificationValidationService.validateFarmerSubmission(
                     lat, lng,
                     form.uploadedImages, form.uploadedDocuments,
                     form.uploadedImageTypes, form.uploadedDocTypes,
                     upgradeType.requiredImages, upgradeType.requiredDocuments
                 )
            } else {
                 verificationValidationService.validateEnthusiastSubmission(
                     form.uploadedImages, form.uploadedDocuments,
                     form.uploadedImageTypes, form.uploadedDocTypes,
                     upgradeType.requiredImages, upgradeType.requiredDocuments
                 )
            }

            if (validationError is VerificationValidationService.ValidationResult.Error) {
                val field = validationError.field ?: "general"
                _validationErrors.value = mapOf(field to validationError.message)
                updateFormState { it.copy(isSubmitting = false, error = validationError.message) }
                return@launch
            }

            // Perform Submission
            val submissionId = UUID.randomUUID().toString()
            val targetRole = calculateTargetRole(upgradeType)
            val updatedUser = user.copy(
                farmLocationLat = lat,
                farmLocationLng = lng,
                verificationStatus = VerificationStatus.PENDING,
                locationVerified = false,
                updatedAt = java.util.Date()
            ).let { u ->
                // Apply address updates if present in form
                form.farmLocation?.let { loc ->
                     u.copy(
                         farmAddressLine1 = loc.address ?: loc.name,
                         farmCity = loc.city,
                         farmState = loc.state,
                         farmPostalCode = loc.postalCode,
                         farmCountry = loc.country
                     )
                } ?: u
            }.let { u ->
                if (enthusiastLevel != null && upgradeType == UpgradeType.FARMER_TO_ENTHUSIAST) {
                    u.copy(kycLevel = enthusiastLevel)
                } else u
            }

            // 1. Update Profile - IMPORTANT: Check result to prevent proceeding on failure
            val profileResult = userRepository.updateUserProfile(updatedUser)
            if (profileResult is Resource.Error) {
                val errorMsg = profileResult.message ?: "Failed to update user profile"
                val displayMsg = if (errorMsg.contains("PERMISSION_DENIED", ignoreCase = true)) {
                    "Permission denied for profile update. Your session might be stale. Please try again or re-login."
                } else {
                    "Profile Update Error: $errorMsg"
                }
                updateFormState { it.copy(isSubmitting = false, error = displayMsg) }
                return@launch
            }
            
            // 2. Audit Log
             auditLogDao.insert(AuditLogEntity(
                logId = UUID.randomUUID().toString(),
                type = "VERIFICATION",
                refId = user.userId,
                action = "KYC_SUBMITTED",
                actorUserId = user.userId,
                detailsJson = gson.toJson(mapOf("upgradeType" to upgradeType.name, "submissionId" to submissionId)),
                createdAt = System.currentTimeMillis()
            ))

            // 3. Submit
            val locationMap = if (lat != null && lng != null) mapOf("lat" to lat, "lng" to lng) else null
            
            val result = userRepository.submitKycVerification(
                userId = user.userId,
                submissionId = submissionId,
                documentUrls = form.uploadedDocuments,
                imageUrls = form.uploadedImages,
                docTypes = form.uploadedDocTypes,
                imageTypes = form.uploadedImageTypes,
                upgradeType = upgradeType,
                currentRole = user.role,
                targetRole = targetRole,
                farmLocation = locationMap,
                applicantPhone = form.contactPhone
            )

            if (result is Resource.Success) {
                 updateFormState { it.copy(isSubmitting = false, submissionSuccess = true) }
                 
                 // Cleanup draft after success
                 val userId = currentUserProvider.userIdOrNull()
                 if (userId != null) {
                     draftRepository.deleteDraft(userId)
                 }
                 
                 refresh() // Refresh user to get PENDING status logic
                 // Notify
                 runCatching { verificationNotificationService.notifyVerificationPending(user.userId) }
            } else {
                 val errorMsg = result.message ?: "Verification submission failed"
                 val displayMsg = if (errorMsg.contains("PERMISSION_DENIED", ignoreCase = true)) {
                     "Permission denied. Your session may have expired or you don't have permission to write this submission. Please logout and login again."
                 } else {
                     errorMsg
                 }
                 updateFormState { it.copy(isSubmitting = false, error = displayMsg) }
            }
        }
    }
    
    // Internal Helpers

    private fun calculateTargetRole(type: UpgradeType?): UserType? {
        return when (type) {
            UpgradeType.GENERAL_TO_FARMER -> UserType.FARMER
            UpgradeType.FARMER_TO_ENTHUSIAST -> UserType.ENTHUSIAST
            else -> null
        }
    }

    private fun mapPlaceToFarmLocation(place: Place): FarmLocation {
        val lat = place.latLng?.latitude ?: 0.0
        val lng = place.latLng?.longitude ?: 0.0
        
        val components = place.addressComponents?.asList()?.map { 
            LocationComponent(it.name, it.types)
        } ?: emptyList()
        
        fun getComp(type: String) = components.find { it.types.contains(type) }?.name

        return FarmLocation(
            lat = lat,
            lng = lng,
            name = place.name,
            address = place.address,
            city = getComp("locality"),
            state = getComp("administrative_area_level_1"),
            postalCode = getComp("postal_code"),
            country = getComp("country"),
            addressComponents = components
        )
    }



    fun clearUploadError() {
        updateFormState { it.copy(uploadError = null) }
    }

    fun retryKycSubmission() {
        if (!_formState.value.isSubmitting && _formState.value.error != null) {
            val type = _formState.value.upgradeType ?: UpgradeType.FARMER_VERIFICATION
            submitKycWithDocuments(type)
        }
    }

    private fun getFileExtension(uri: android.net.Uri): String {
        val mimeType = appContext.contentResolver.getType(uri)
        return if (mimeType != null) {
            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
        } else {
            // Fallback: try parsing from name
            var name = ""
            try {
                appContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                     if (cursor.moveToFirst()) {
                         val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                         if (idx >= 0) name = cursor.getString(idx)
                     }
                }
            } catch (e: Exception) {}
            if (name.contains(".")) name.substringAfterLast('.') else "jpg"
        }
    }
}
