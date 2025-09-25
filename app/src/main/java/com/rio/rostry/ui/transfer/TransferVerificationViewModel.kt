package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import androidx.exifinterface.media.ExifInterface
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import android.net.Uri
import com.google.gson.Gson

@HiltViewModel
class TransferVerificationViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val auditLogDao: AuditLogDao,
    @ApplicationContext private val appContext: Context,
    private val gson: Gson,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val transfer: TransferEntity? = null,
        val verifications: List<TransferVerificationEntity> = emptyList(),
        val error: String? = null,
        val success: String? = null,
        val tempBeforeUrl: String = "",
        val tempAfterUrl: String = "",
        val tempLat: String = "",
        val tempLng: String = "",
        val tempSignatureRef: String = "",
        val submitting: Boolean = false,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load(transferId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true, error = null)
                val tFlow = transferDao.getTransferById(transferId)
                val vFlow = verificationDao.streamByTransfer(transferId)
                combine(tFlow, vFlow) { t, v -> t to v }.collect { (t, v) ->
                    _state.value = _state.value.copy(loading = false, transfer = t, verifications = v)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun updateTemp(field: String, value: String) {
        _state.value = when (field) {
            "before" -> _state.value.copy(tempBeforeUrl = value)
            "after" -> _state.value.copy(tempAfterUrl = value)
            "lat" -> _state.value.copy(tempLat = value)
            "lng" -> _state.value.copy(tempLng = value)
            "sig" -> _state.value.copy(tempSignatureRef = value)
            else -> _state.value
        }
    }

    fun submitSellerInit() {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            if (_state.value.tempBeforeUrl.isBlank() || _state.value.tempAfterUrl.isBlank()) {
                _state.value = _state.value.copy(error = "Please attach both before and after photos")
                return@launch
            }
            _state.value = _state.value.copy(submitting = true, error = null)
            val (beforeUri, afterUri) = persistPhotosIfNeeded(_state.value.tempBeforeUrl, _state.value.tempAfterUrl)
            _state.value = _state.value.copy(tempBeforeUrl = beforeUri, tempAfterUrl = afterUri)
            val beforeMeta = exifJsonFor(_state.value.tempBeforeUrl)
            val afterMeta = exifJsonFor(_state.value.tempAfterUrl)
            val entity = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "SELLER_INIT",
                status = "PENDING",
                photoBeforeUrl = _state.value.tempBeforeUrl.ifBlank { null },
                photoAfterUrl = _state.value.tempAfterUrl.ifBlank { null },
                photoBeforeMetaJson = beforeMeta,
                photoAfterMetaJson = afterMeta,
            )
            verificationDao.upsert(entity)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = transferId,
                    action = "SELLER_INIT_SUBMIT",
                    actorUserId = null,
                    detailsJson = null
                )
            )
            _state.value = _state.value.copy(
                submitting = false,
                success = "Photos submitted",
                tempBeforeUrl = "",
                tempAfterUrl = "",
            )
        }
    }

    fun submitGpsConfirm() {
        val transferId = _state.value.transfer?.transferId ?: return
        val lat = _state.value.tempLat.toDoubleOrNull()
        val lng = _state.value.tempLng.toDoubleOrNull()
        viewModelScope.launch {
            if (lat == null || lng == null) {
                _state.value = _state.value.copy(error = "Please provide valid GPS coordinates")
                return@launch
            }
            _state.value = _state.value.copy(submitting = true, error = null)
            val entity = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "GPS_CONFIRM",
                status = "PENDING",
                gpsLat = lat,
                gpsLng = lng,
            )
            verificationDao.upsert(entity)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = transferId,
                    action = "GPS_CONFIRM_SUBMIT",
                    actorUserId = null,
                    detailsJson = null
                )
            )
            _state.value = _state.value.copy(
                submitting = false,
                success = "GPS submitted",
                tempLat = "",
                tempLng = "",
            )
        }
    }

    fun submitSignature() {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            if (_state.value.tempSignatureRef.isBlank()) {
                _state.value = _state.value.copy(error = "Please provide a signature before submitting")
                return@launch
            }
            _state.value = _state.value.copy(submitting = true, error = null)
            val entity = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "SIGNATURE",
                status = "PENDING",
                identityDocType = "DIGITAL_SIG",
                identityDocRef = _state.value.tempSignatureRef.ifBlank { null }
            )
            verificationDao.upsert(entity)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = transferId,
                    action = "SIGNATURE_SUBMIT",
                    actorUserId = null,
                    detailsJson = null
                )
            )
            _state.value = _state.value.copy(
                submitting = false,
                success = "Signature submitted",
                tempSignatureRef = "",
            )
        }
    }

    fun submitPlatformReview(notes: String, approved: Boolean) {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(submitting = true, error = null)
            val entity = TransferVerificationEntity(
                verificationId = UUID.randomUUID().toString(),
                transferId = transferId,
                step = "PLATFORM_REVIEW",
                status = if (approved) "APPROVED" else "REJECTED",
                notes = notes.ifBlank { null }
            )
            verificationDao.upsert(entity)
            auditLogDao.insert(
                AuditLogEntity(
                    logId = UUID.randomUUID().toString(),
                    type = "VERIFICATION",
                    refId = transferId,
                    action = if (approved) "PLATFORM_APPROVE" else "PLATFORM_REJECT",
                    actorUserId = null,
                    detailsJson = null
                )
            )
            _state.value = _state.value.copy(submitting = false, success = if (approved) "Approved" else "Rejected")
        }
    }

    private fun exifJsonFor(uriString: String): String? {
        if (uriString.isBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                val map = mutableMapOf<String, Any?>()
                map[ExifInterface.TAG_DATETIME] = exif.getAttribute(ExifInterface.TAG_DATETIME)
                map[ExifInterface.TAG_ORIENTATION] = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                map["gpsLat"] = exif.latLong?.getOrNull(0)
                map["gpsLng"] = exif.latLong?.getOrNull(1)
                gson.toJson(map)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun persistPhotosIfNeeded(before: String, after: String): Pair<String, String> {
        fun persistOne(uriStr: String): String {
            return try {
                if (!uriStr.contains("/cache/")) return uriStr
                val uri = Uri.parse(uriStr)
                val imagesDir = java.io.File(appContext.filesDir, "images").apply { mkdirs() }
                val ext = if (uriStr.endsWith(".png")) "png" else "jpg"
                val outFile = java.io.File(imagesDir, "${System.currentTimeMillis()}.$ext")
                appContext.contentResolver.openInputStream(uri)?.use { input ->
                    java.io.FileOutputStream(outFile).use { output -> input.copyTo(output) }
                }
                androidx.core.content.FileProvider.getUriForFile(appContext, "${appContext.packageName}.fileprovider", outFile).toString()
            } catch (e: Exception) {
                uriStr
            }
        }
        return persistOne(before) to persistOne(after)
    }

    fun consumeSuccess() {
        _state.value = _state.value.copy(success = null)
    }
}
