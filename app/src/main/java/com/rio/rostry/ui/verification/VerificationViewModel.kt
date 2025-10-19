package com.rio.rostry.ui.verification

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.media.MediaUploadManager
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
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init {
        refresh()
        observeUploadEvents()
    }

    fun refresh() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            userRepository.getCurrentUser().collectLatest { res ->
                when (res) {
                    is Resource.Success -> _ui.value = UiState(user = res.data, isLoading = false)
                    is Resource.Error -> _ui.value = UiState(error = res.message, isLoading = false)
                    is Resource.Loading -> _ui.value = _ui.value.copy(isLoading = true)
                }
            }
        }
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

    fun uploadDocument(localUri: String, documentType: String) {
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
        val remotePath = "kyc/$userId/documents/${UUID.randomUUID()}_$documentType"
        val ctx = JSONObject().apply { put("docType", documentType) }.toString()
        mediaUploadManager.enqueueToOutbox(localPath = localUri, remotePath = remotePath, contextJson = ctx)
    }

    fun uploadImage(localUri: String, imageType: String) {
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
        val remotePath = "kyc/$userId/images/${UUID.randomUUID()}_$imageType"
        mediaUploadManager.enqueueToOutbox(localPath = localUri, remotePath = remotePath)
    }

    private fun observeUploadEvents() {
        viewModelScope.launch {
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
                        
                        if (event.remotePath.contains("/documents/")) {
                            documents.add(event.downloadUrl)
                            // Extract type from remotePath suffix if present, e.g., _AADHAAR
                            val docType = event.remotePath.substringAfterLast('_').uppercase()
                            docTypes[event.downloadUrl] = docType
                        } else if (event.remotePath.contains("/images/")) {
                            images.add(event.downloadUrl)
                        }
                        
                        _ui.value = _ui.value.copy(
                            uploadProgress = progress,
                            uploadedDocuments = documents,
                            uploadedImages = images,
                            uploadedDocTypes = docTypes
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

    fun submitKycWithDocuments(passedLat: Double? = null, passedLng: Double? = null) {
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

            // Validate submission
            val effectiveLat = passedLat ?: current.farmLocationLat
            val effectiveLng = passedLng ?: current.farmLocationLng
            val validation = if (current.userType?.name == "FARMER") {
                verificationValidationService.validateFarmerSubmission(effectiveLat, effectiveLng, _ui.value.uploadedImages, _ui.value.uploadedDocuments)
            } else {
                verificationValidationService.validateEnthusiastSubmission(_ui.value.uploadedImages, _ui.value.uploadedDocuments, _ui.value.uploadedDocTypes)
            }
            if (validation is VerificationValidationService.ValidationResult.Error) {
                _ui.value = _ui.value.copy(isSubmitting = false, validationErrors = mapOf((validation.field ?: "general") to validation.message))
                return@launch
            }

            // Serialize document lists to JSON
            val documentUrlsJson = JSONArray(_ui.value.uploadedDocuments).toString()
            val imageUrlsJson = JSONArray(_ui.value.uploadedImages).toString()

            // Create document types map from tracked types
            val docTypesMap = JSONObject(_ui.value.uploadedDocTypes)

            val updated = current.copy(
                kycDocumentUrls = documentUrlsJson,
                kycImageUrls = imageUrlsJson,
                kycDocumentTypes = docTypesMap.toString(),
                kycUploadStatus = "UPLOADED",
                kycUploadedAt = System.currentTimeMillis(),
                // For farmers, persist location atomically with KYC submission if provided
                farmLocationLat = effectiveLat,
                farmLocationLng = effectiveLng,
                updatedAt = System.currentTimeMillis()
            )

            val res = userRepository.updateUserProfile(updated)
            if (res is Resource.Error) {
                _ui.value = _ui.value.copy(isSubmitting = false, error = res.message)
            } else {
                // Send pending notification (non-blocking)
                runCatching { verificationNotificationService.notifyVerificationPending(current.userId ?: "") }
                _ui.value = _ui.value.copy(isSubmitting = false, submissionSuccess = true, message = "Submitted for verification")

                // Create audit log for submission
                val gson = Gson()
                val action = if (isResubmission) "KYC_RESUBMITTED" else "KYC_SUBMITTED"
                val details = mutableMapOf<String, Any?>(
                    "documentCount" to _ui.value.uploadedDocuments.size,
                    "imageCount" to _ui.value.uploadedImages.size
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
                userRepository.submitKycVerification(current.userId, submissionId, _ui.value.uploadedDocuments, _ui.value.uploadedImages)
                SecurityManager.audit("KYC_SUBMIT", mapOf("userId" to current.userId, "submissionId" to submissionId, "documentCount" to _ui.value.uploadedDocuments.size))
            }
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
