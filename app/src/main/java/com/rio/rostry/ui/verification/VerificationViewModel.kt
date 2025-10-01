package com.rio.rostry.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.media.MediaUploadManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val mediaUploadManager: MediaUploadManager
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val user: UserEntity? = null,
        val message: String? = null,
        val error: String? = null,
        val uploadProgress: Map<String, Int> = emptyMap(),
        val uploadedDocuments: List<String> = emptyList(),
        val uploadedImages: List<String> = emptyList(),
        val uploadError: String? = null
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
                verificationStatus = VerificationStatus.PENDING,
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
                verificationStatus = VerificationStatus.PENDING,
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
        val remotePath = "kyc/$userId/documents/${UUID.randomUUID()}_$documentType"
        val task = MediaUploadManager.UploadTask(
            localPath = localUri,
            remotePath = remotePath,
            compress = true
        )
        mediaUploadManager.enqueue(task)
    }

    fun uploadImage(localUri: String, imageType: String) {
        val userId = _ui.value.user?.userId ?: return
        val remotePath = "kyc/$userId/images/${UUID.randomUUID()}_$imageType"
        val task = MediaUploadManager.UploadTask(
            localPath = localUri,
            remotePath = remotePath,
            compress = true,
            sizeLimitBytes = 1_500_000L
        )
        mediaUploadManager.enqueue(task)
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
                        
                        if (event.remotePath.contains("/documents/")) {
                            documents.add(event.remotePath)
                        } else if (event.remotePath.contains("/images/")) {
                            images.add(event.remotePath)
                        }
                        
                        _ui.value = _ui.value.copy(
                            uploadProgress = progress,
                            uploadedDocuments = documents,
                            uploadedImages = images
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

    fun submitKycWithDocuments() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            
            // Fetch the latest user data to preserve recently entered location/KYC level
            val latestUserResource = userRepository.getCurrentUser().first()
            val current = when (latestUserResource) {
                is Resource.Success -> latestUserResource.data
                else -> {
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        error = "Failed to retrieve latest user data"
                    )
                    return@launch
                }
            }
            
            if (current == null) {
                _ui.value = _ui.value.copy(isLoading = false, error = "User not found")
                return@launch
            }
            
            // Serialize document lists to JSON
            val documentUrlsJson = JSONArray(_ui.value.uploadedDocuments).toString()
            val imageUrlsJson = JSONArray(_ui.value.uploadedImages).toString()
            
            // Create document types map (placeholder - could be enhanced)
            val docTypesMap = JSONObject()
            _ui.value.uploadedDocuments.forEach { url ->
                val type = url.substringAfterLast("_").substringBefore(".")
                docTypesMap.put(url, type)
            }
            
            val updated = current.copy(
                kycDocumentUrls = documentUrlsJson,
                kycImageUrls = imageUrlsJson,
                kycDocumentTypes = docTypesMap.toString(),
                kycUploadStatus = "UPLOADED",
                kycUploadedAt = System.currentTimeMillis(),
                verificationStatus = VerificationStatus.PENDING,
                updatedAt = System.currentTimeMillis()
            )
            
            val res = userRepository.updateUserProfile(updated)
            _ui.value = if (res is Resource.Error) {
                _ui.value.copy(isLoading = false, error = res.message)
            } else {
                _ui.value.copy(isLoading = false, message = "KYC with documents submitted for verification")
            }
        }
    }
}
