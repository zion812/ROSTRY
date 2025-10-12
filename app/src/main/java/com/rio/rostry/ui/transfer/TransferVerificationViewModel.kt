package com.rio.rostry.ui.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import androidx.exifinterface.media.ExifInterface
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.dao.DisputeDao
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import android.net.Uri
import com.google.gson.Gson
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.VerificationUtils
import com.rio.rostry.utils.analytics.EnthusiastAnalyticsTracker
import com.rio.rostry.utils.images.ImageCompressor
import com.rio.rostry.utils.media.MediaUploadManager
import com.rio.rostry.utils.network.ConnectivityManager
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.entity.UploadTaskEntity
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import com.rio.rostry.workers.MediaUploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@HiltViewModel
class TransferVerificationViewModel @Inject constructor(
    private val transferDao: TransferDao,
    private val verificationDao: TransferVerificationDao,
    private val auditLogDao: AuditLogDao,
    private val disputeDao: DisputeDao,
    @ApplicationContext private val appContext: Context,
    private val gson: Gson,
    private val workflow: TransferWorkflowRepository,
    private val uploadManager: MediaUploadManager,
    private val analytics: EnthusiastAnalyticsTracker,
    private val currentUserProvider: CurrentUserProvider,
    private val uploadTaskDao: UploadTaskDao,
    private val connectivityManager: ConnectivityManager,
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val transfer: TransferEntity? = null,
        val verifications: List<TransferVerificationEntity> = emptyList(),
        val auditLogs: List<AuditLogEntity> = emptyList(),
        val disputes: List<com.rio.rostry.data.database.entity.DisputeEntity> = emptyList(),
        val error: String? = null,
        val success: String? = null,
        val tempBeforeUrl: String = "",
        val tempAfterUrl: String = "",
        val tempBeforeExifJson: String? = null,
        val tempAfterExifJson: String? = null,
        val tempBuyerPhotoExifJson: String? = null,
        val tempLat: String = "",
        val tempLng: String = "",
        val tempSignatureRef: String = "",
        val tempIdentityDocType: String = "",
        val tempIdentityDocRef: String = "",
        val tempIdentityDocNumber: String = "",
        val tempGpsExplanation: String = "",
        val submitting: Boolean = false,
        val uploadProgress: Map<String, Int> = emptyMap(), // remotePath -> percent
        val uploadedBeforeUrl: String? = null,
        val uploadedAfterUrl: String? = null,
        val uploadedIdentityRef: String? = null,
        val trustScore: Int? = null,
        val trustBreakdown: Map<String, Int> = emptyMap(),
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    // Prevent duplicate analytics events per transfer in a session
    private val startedTransfers = mutableSetOf<String>()
    private val completedTransfers = mutableSetOf<String>()

    // Typed temp updates to avoid key mismatches
    sealed class TempUpdate {
        data class BeforePhoto(val uri: String): TempUpdate()
        data class AfterPhoto(val uri: String): TempUpdate()
        data class Gps(val lat: String?, val lng: String?, val explanation: String? = null): TempUpdate()
        data class Signature(val ref: String): TempUpdate()
        data class IdentityType(val type: String): TempUpdate()
        data class IdentityRef(val ref: String): TempUpdate()
        data class IdentityNumber(val number: String): TempUpdate()
    }

    // Compose a simple breakdown matching TransferWorkflowRepository scoring
    private fun computeBreakdown(
        verifications: List<TransferVerificationEntity>,
        disputes: List<com.rio.rostry.data.database.entity.DisputeEntity>
    ): Map<String, Int> {
        var seller = if (verifications.any { it.step == "SELLER_INIT" && it.status == "APPROVED" }) 10 else 0
        var buyer = if (verifications.any { it.step == "BUYER_VERIFY" && it.status == "APPROVED" }) 15 else 0
        var platform = if (verifications.any { it.step == "PLATFORM_REVIEW" && it.status == "APPROVED" }) 15 else 0
        val disputesPenalty = -(disputes.size * 5)
        return linkedMapOf(
            "Seller evidence" to seller,
            "Buyer verification" to buyer,
            "Platform review" to platform,
            "Disputes" to disputesPenalty,
        )
    }

    fun load(transferId: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true, error = null)
                val tFlow = transferDao.getTransferById(transferId)
                val vFlow = verificationDao.streamByTransfer(transferId)
                val aFlow = auditLogDao.streamByRef(transferId)
                val dFlow = disputeDao.streamByTransfer(transferId)
                combine(tFlow, vFlow, aFlow, dFlow) { t, v, a, d -> arrayOf(t, v, a, d) }.collect { arr ->
                    val t = arr[0] as TransferEntity?
                    val v = arr[1] as List<TransferVerificationEntity>
                    val a = arr[2] as List<AuditLogEntity>
                    val d = arr[3] as List<com.rio.rostry.data.database.entity.DisputeEntity>
                    // Update base state
                    _state.value = _state.value.copy(loading = false, transfer = t, verifications = v, auditLogs = a, disputes = d)
                    // Compute trust score and breakdown
                    val id = t?.transferId
                    if (!id.isNullOrBlank()) {
                        viewModelScope.launch {
                            when (val res = workflow.computeTrustScore(id)) {
                                is Resource.Success -> {
                                    val score = res.data
                                    val breakdown = computeBreakdown(v, d)
                                    _state.value = _state.value.copy(trustScore = score, trustBreakdown = breakdown)
                                }
                                is Resource.Error -> {
                                    _state.value = _state.value.copy(error = res.message)
                                }
                                else -> Unit
                            }
                        }
                    }
                    // Funnel start analytics once per transfer
                    val tid = t?.transferId
                    if (!tid.isNullOrBlank() && startedTransfers.add(tid)) {
                        val userId = currentUserProvider.userIdOrNull()
                        analytics.trackTransferVerificationStart(tid, userId)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
        // Also begin observing persisted upload progress for this transfer
        viewModelScope.launch {
            uploadTaskDao.observeByContext(transferId).collect { tasks ->
                val map = tasks.associate { it.remotePath to it.progress }
                _state.value = _state.value.copy(uploadProgress = map)
            }
        }

        // Live progress from MediaUploadManager events, and persist on success
        viewModelScope.launch {
            uploadManager.events.collectLatest { ev ->
                when (ev) {
                    is MediaUploadManager.UploadEvent.Progress -> updateProgress(ev.remotePath, ev.percent)
                    is MediaUploadManager.UploadEvent.Success -> onUploadSuccess(ev.remotePath)
                    is MediaUploadManager.UploadEvent.Failed -> _state.value = _state.value.copy(error = ev.error)
                    else -> Unit
                }
            }
        }
    }

    fun updateTemp(update: TempUpdate) {
        _state.value = when (update) {
            is TempUpdate.BeforePhoto -> _state.value.copy(tempBeforeUrl = update.uri)
            is TempUpdate.AfterPhoto -> _state.value.copy(tempAfterUrl = update.uri)
            is TempUpdate.Gps -> _state.value.copy(
                tempLat = update.lat ?: _state.value.tempLat,
                tempLng = update.lng ?: _state.value.tempLng,
                tempGpsExplanation = update.explanation ?: _state.value.tempGpsExplanation
            )
            is TempUpdate.Signature -> _state.value.copy(tempSignatureRef = update.ref)
            is TempUpdate.IdentityType -> _state.value.copy(tempIdentityDocType = update.type)
            is TempUpdate.IdentityRef -> _state.value.copy(tempIdentityDocRef = update.ref)
            is TempUpdate.IdentityNumber -> _state.value.copy(tempIdentityDocNumber = update.number)
        }
    }

    // Backward-compat shim (will be removed): delegate to typed
    fun updateTemp(field: String, value: String) {
        when (field) {
            "before" -> updateTemp(TempUpdate.BeforePhoto(value))
            "after" -> updateTemp(TempUpdate.AfterPhoto(value))
            "lat" -> updateTemp(TempUpdate.Gps(lat = value, lng = null))
            "lng" -> updateTemp(TempUpdate.Gps(lat = null, lng = value))
            "sig" -> updateTemp(TempUpdate.Signature(value))
            "gps_explanation" -> updateTemp(TempUpdate.Gps(lat = null, lng = null, explanation = value))
            "identityDocType" -> updateTemp(TempUpdate.IdentityType(value))
            "identityDocRef" -> updateTemp(TempUpdate.IdentityRef(value))
            "identityDocNumber" -> updateTemp(TempUpdate.IdentityNumber(value))
        }
    }

    // Public helper to compress+upload identity image and store remote ref in state
    fun uploadIdentity(localUriString: String) {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            startUpload("identity", localUriString)
        }
    }

    fun submitSellerInit() {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            // With shared pipeline, uploads are started via startUpload; we wait for both to succeed
            _state.value = _state.value.copy(submitting = true, error = null)
            // Defer actual repository write to onUploadSuccess when both images are uploaded
            _state.value = _state.value.copy(submitting = false)
        }
    }

    // Start a shared-pipeline upload for a given kind: "before" | "after" | "identity"
    fun startUpload(kind: String, localUri: String) {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            val uri = Uri.parse(localUri)
            val inputFile = copyUriToCache(uri)
            val compressed = ImageCompressor.compressForUpload(appContext, inputFile, lowBandwidth = !connectivityManager.isOnWifi())
            val remotePath = "transfers/${transferId}/${kind}_${System.currentTimeMillis()}.jpg"
            val ctx = gson.toJson(mapOf("transferId" to transferId, "type" to kind))
            // Extract EXIF from original local URI and stash in state for persistence
            when (kind.lowercase()) {
                "before" -> _state.value = _state.value.copy(tempBeforeExifJson = exifJsonFor(localUri))
                "after" -> _state.value = _state.value.copy(tempAfterExifJson = exifJsonFor(localUri))
                "identity" -> _state.value = _state.value.copy(tempBuyerPhotoExifJson = exifJsonFor(localUri))
            }
            uploadManager.enqueueToOutbox(
                localPath = compressed.absolutePath,
                remotePath = remotePath,
                contextJson = ctx
            )
        }
    }

    private fun onUploadSuccess(remotePath: String) {
        val transferId = _state.value.transfer?.transferId ?: return
        val kind = when {
            remotePath.contains("before_") -> "before"
            remotePath.contains("after_") -> "after"
            remotePath.contains("identity_") || remotePath.contains("id/") || remotePath.contains("identity") -> "identity"
            else -> "unknown"
        }
        when (kind) {
            "before" -> {
                _state.value = _state.value.copy(uploadedBeforeUrl = remotePath)
                maybePersistSellerEvidence(transferId)
            }
            "after" -> {
                _state.value = _state.value.copy(uploadedAfterUrl = remotePath)
                maybePersistSellerEvidence(transferId)
            }
            "identity" -> {
                viewModelScope.launch {
                    when (val res = workflow.buyerVerify(
                        transferId = transferId,
                        buyerPhotoUrl = remotePath,
                        buyerGpsLat = null,
                        buyerGpsLng = null,
                        identityDocType = _state.value.tempIdentityDocType.takeIf { it.isNotBlank() } ?: "GOVT_ID",
                        identityDocRef = remotePath,
                        identityDocNumber = _state.value.tempIdentityDocNumber.takeIf { it.isNotBlank() },
                        buyerPhotoMetaJson = _state.value.tempBuyerPhotoExifJson,
                        gpsExplanation = _state.value.tempGpsExplanation.takeIf { it.isNotBlank() }
                    )) {
                        is Resource.Success -> {
                            _state.value = _state.value.copy(uploadedIdentityRef = remotePath, success = "Identity uploaded", tempBuyerPhotoExifJson = null)
                            analytics.trackTransferVerifyStep(transferId, "IDENTITY")
                        }
                        is Resource.Error -> _state.value = _state.value.copy(error = res.message)
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    private fun maybePersistSellerEvidence(transferId: String) {
        val before = _state.value.uploadedBeforeUrl
        val after = _state.value.uploadedAfterUrl
        if (before.isNullOrBlank() || after.isNullOrBlank()) return
        viewModelScope.launch {
            val beforeMeta = _state.value.tempBeforeExifJson
            val afterMeta = _state.value.tempAfterExifJson
            when (val res = workflow.appendSellerEvidence(transferId, before, after, beforeMeta, afterMeta)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(success = "Photos submitted", uploadedBeforeUrl = null, uploadedAfterUrl = null, tempBeforeExifJson = null, tempAfterExifJson = null)
                    analytics.trackTransferVerifyStep(transferId, "SELLER_INIT")
                }
                is Resource.Error -> _state.value = _state.value.copy(error = res.message)
                else -> Unit
            }
        }
    }

    fun submitGpsConfirm() {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            val lat = _state.value.tempLat.toDoubleOrNull()
            val lng = _state.value.tempLng.toDoubleOrNull()
            if (lat == null || lng == null) {
                _state.value = _state.value.copy(error = "Please provide valid GPS coordinates")
                return@launch
            }
            _state.value = _state.value.copy(submitting = true, error = null)
            // Buyer verify via repository (includes GPS radius rule)
            // Pass identity doc fields from state if present
            when (val res = workflow.buyerVerify(
                transferId = transferId,
                buyerPhotoUrl = null,
                buyerGpsLat = lat,
                buyerGpsLng = lng,
                identityDocType = _state.value.tempIdentityDocType.takeIf { it.isNotBlank() },
                identityDocRef = _state.value.tempIdentityDocRef.takeIf { it.isNotBlank() },
                identityDocNumber = _state.value.tempIdentityDocNumber.takeIf { it.isNotBlank() },
                buyerPhotoMetaJson = null,
                gpsExplanation = _state.value.tempGpsExplanation.takeIf { it.isNotBlank() }
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        submitting = false,
                        success = "GPS submitted",
                        tempLat = "",
                        tempLng = "",
                        tempIdentityDocType = "",
                        tempIdentityDocRef = "",
                        tempIdentityDocNumber = "",
                    )
                    analytics.trackTransferVerifyStep(transferId, "GPS_CONFIRM")
                }
                is Resource.Error -> _state.value = _state.value.copy(submitting = false, error = res.message)
                else -> _state.value = _state.value.copy(submitting = false)
            }
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
            when (val res = workflow.buyerVerify(
                transferId = transferId,
                buyerPhotoUrl = null,
                buyerGpsLat = null,
                buyerGpsLng = null,
                identityDocType = _state.value.tempIdentityDocType.takeIf { it.isNotBlank() } ?: "DIGITAL_SIG",
                identityDocRef = _state.value.tempIdentityDocRef.takeIf { it.isNotBlank() } ?: _state.value.tempSignatureRef,
                identityDocNumber = _state.value.tempIdentityDocNumber.takeIf { it.isNotBlank() }
            )) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        submitting = false,
                        success = "Signature submitted",
                        tempSignatureRef = "",
                        tempIdentityDocType = "",
                        tempIdentityDocRef = "",
                        tempIdentityDocNumber = "",
                    )
                    analytics.trackTransferVerifyStep(transferId, "SIGNATURE")
                }
                is Resource.Error -> _state.value = _state.value.copy(submitting = false, error = res.message)
                else -> _state.value = _state.value.copy(submitting = false)
            }
        }
    }

    fun raiseDispute(reason: String) {
        val transferId = _state.value.transfer?.transferId ?: return
        val userId = currentUserProvider.userIdOrNull()
        if (userId.isNullOrBlank()) {
            _state.value = _state.value.copy(error = "User not authenticated")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(submitting = true, error = null)
            when (val res = workflow.raiseDispute(transferId, userId, reason)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        submitting = false,
                        success = "Dispute opened: ${res.data}"
                    )
                    analytics.trackTransferVerifyStep(transferId, "DISPUTE_RAISED")
                }
                is Resource.Error -> _state.value = _state.value.copy(submitting = false, error = res.message)
                else -> _state.value = _state.value.copy(submitting = false)
            }
        }
    }

    fun resolveDispute(disputeId: String, notes: String, resolved: Boolean) {
        viewModelScope.launch {
            _state.value = _state.value.copy(submitting = true, error = null)
            when (val res = workflow.resolveDispute(disputeId, notes, resolved)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        submitting = false,
                        success = if (resolved) "Dispute resolved" else "Dispute rejected"
                    )
                    analytics.trackTransferVerifyStep(_state.value.transfer?.transferId ?: "", "DISPUTE_RESOLVED")
                }
                is Resource.Error -> _state.value = _state.value.copy(submitting = false, error = res.message)
                else -> _state.value = _state.value.copy(submitting = false)
            }
        }
    }

    fun platformReview(approved: Boolean, notes: String) {
        val transferId = _state.value.transfer?.transferId ?: return
        viewModelScope.launch {
            _state.value = _state.value.copy(submitting = true, error = null)
            val actor = currentUserProvider.userIdOrNull()
            when (val res = workflow.platformReview(transferId, approved, notes.ifBlank { null }, actor)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        submitting = false,
                        success = if (approved) "Approved" else "Reviewed"
                    )
                    analytics.trackTransferVerifyStep(_state.value.transfer?.transferId ?: "", "PLATFORM_REVIEW")
                    // Funnel complete (once per transfer)
                    val id = _state.value.transfer?.transferId
                    val userId = currentUserProvider.userIdOrNull()
                    if (!id.isNullOrBlank() && completedTransfers.add(id)) {
                        analytics.trackTransferVerificationComplete(id, userId)
                    }
                    // Refresh view
                    _state.value.transfer?.transferId?.let { load(it) }
                }
                is Resource.Error -> _state.value = _state.value.copy(submitting = false, error = res.message)
                else -> _state.value = _state.value.copy(submitting = false)
            }
        }
    }

    fun refresh() {
        _state.value.transfer?.transferId?.let { load(it) }
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
        } catch (_: Exception) { null }
    }

    private fun persistPhotosIfNeeded(before: String, after: String): Pair<String, String> {
        fun persistOne(uriStr: String): String {
            return try {
                if (!uriStr.contains("/cache/")) return uriStr
                val uri = Uri.parse(uriStr)
                val imagesDir = java.io.File(appContext.filesDir, "images").apply { mkdirs() }
                val ext = if (uriStr.endsWith(".png")) "png" else "jpg"
                val outFile = java.io.File(imagesDir, "${'$'}{System.currentTimeMillis()}.$ext")
                appContext.contentResolver.openInputStream(uri)?.use { input ->
                    java.io.FileOutputStream(outFile).use { output -> input.copyTo(output) }
                }
                androidx.core.content.FileProvider.getUriForFile(appContext, "${'$'}{appContext.packageName}.fileprovider", outFile).toString()
            } catch (e: Exception) {
                uriStr
            }
        }
        return persistOne(before) to persistOne(after)
    }

    private suspend fun uploadAndAwaitRemote(remotePath: String, localUriString: String): String? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(localUriString)
            val inputFile = copyUriToCache(uri)
            val compressed = ImageCompressor.compressForUpload(appContext, inputFile, lowBandwidth = false)
            // Persist task to outbox and schedule worker; progress observed from DAO
            val task = UploadTaskEntity(
                taskId = UUID.randomUUID().toString(),
                localPath = compressed.absolutePath,
                remotePath = remotePath,
                status = "QUEUED",
                progress = 0,
                retries = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                error = null,
                contextJson = gson.toJson(mapOf(
                    "transferId" to (_state.value.transfer?.transferId ?: ""),
                    "type" to "VERIFY"
                ))
            )
            uploadTaskDao.upsert(task)
            scheduleUploadWorker()
            // Return remotePath placeholder; backend or resolver can turn this into a URL once uploaded
            remotePath
        } catch (e: Exception) {
            null
        }
    }

    private fun updateProgress(remotePath: String, percent: Int) {
        _state.value = _state.value.copy(uploadProgress = _state.value.uploadProgress.toMutableMap().also { it[remotePath] = percent })
    }

    private fun scheduleUploadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val req = OneTimeWorkRequestBuilder<MediaUploadWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(appContext).enqueue(req)
    }

    private suspend fun copyUriToCache(uri: Uri): File = withContext(Dispatchers.IO) {
        val dir = File(appContext.cacheDir, "upload").apply { mkdirs() }
        val out = File(dir, "${'$'}{System.currentTimeMillis()}.jpg")
        appContext.contentResolver.openInputStream(uri)?.use { input ->
            java.io.FileOutputStream(out).use { output -> input.copyTo(output) }
        }
        out
    }

    fun consumeSuccess() {
        _state.value = _state.value.copy(success = null)
    }

    // --- Epic 7 helpers ---
    fun checkGpsProximity(sellerLat: Double, sellerLng: Double, buyerLat: Double, buyerLng: Double): Boolean {
        return try {
            VerificationUtils.withinRadius(sellerLat, sellerLng, buyerLat, buyerLng, 100.0)
        } catch (_: Exception) { false }
    }

    /**
     * Convenience wrapper to trigger the shared upload pipeline and set temp fields.
     * step: "before" | "after" | "identity".
     */
    fun uploadVerificationPhoto(step: String, photoUri: Uri) {
        when (step.lowercase()) {
            "before" -> updateTemp(TempUpdate.BeforePhoto(photoUri.toString()))
            "after" -> updateTemp(TempUpdate.AfterPhoto(photoUri.toString()))
            "identity" -> updateTemp(TempUpdate.IdentityRef(photoUri.toString()))
        }
        startUpload(step, photoUri.toString())
    }
}

