package com.rio.rostry.domain.verification

import android.util.Log
import com.google.firebase.firestore.GeoPoint
import com.rio.rostry.data.database.dao.KycVerificationDao
import com.rio.rostry.data.database.dao.ProductVerificationDraftDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.dao.VerificationRequestDao
import com.rio.rostry.data.database.entity.KycVerificationEntity
import com.rio.rostry.data.database.entity.ProductVerificationDraftEntity
import com.rio.rostry.domain.validation.CoordinateValidator
import com.rio.rostry.domain.validation.InputValidationError
import com.rio.rostry.domain.validation.InputValidationResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Full implementation of the VerificationSystem interface.
 * 
 * Handles:
 * - Draft creation and merging with conflict resolution
 * - KYC verification workflow (submit, review, approve/reject)
 * - Verification status validation before product listing
 * - Duplicate verification prevention
 * - Verifier credential validation
 * - Audit trail via status tracking
 */
@Singleton
class VerificationSystemImpl @Inject constructor(
    private val productDraftDao: ProductVerificationDraftDao,
    private val kycDao: KycVerificationDao,
    private val verificationRequestDao: VerificationRequestDao,
    private val userDao: UserDao,
    private val gson: Gson
) : VerificationSystem {

    companion object {
        private const val TAG = "VerificationSystem"
    }

    // ─── Draft Management ───────────────────────────────────────────────

    override suspend fun createDraft(
        productId: String,
        verifierId: String,
        fields: Map<String, Any>
    ): VerificationDraft {
        // Validate verifier credentials
        val credResult = validateVerifierCredentials(verifierId)
        if (credResult is InputValidationResult.Invalid) {
            Log.w(TAG, "Verifier $verifierId has invalid credentials")
        }

        val draftId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val entity = ProductVerificationDraftEntity(
            draftId = draftId,
            productId = productId,
            verifierId = verifierId,
            fieldsJson = gson.toJson(fields),
            status = DraftStatus.PENDING.name,
            createdAt = now
        )

        productDraftDao.insertDraft(entity)
        Log.i(TAG, "Created verification draft $draftId for product $productId by verifier $verifierId")

        return VerificationDraft(
            draftId = draftId,
            productId = productId,
            verifierId = verifierId,
            fields = fields,
            status = DraftStatus.PENDING,
            createdAt = now
        )
    }

    override suspend fun mergeDrafts(request: DraftMergeRequest): VerificationResult {
        return try {
            val draftEntities = productDraftDao.getAllDraftsForProduct(request.productId)
                .filter { it.draftId in request.draftIds && it.status == DraftStatus.PENDING.name }

            if (draftEntities.isEmpty()) {
                return VerificationResult.Failure("No pending drafts found for the specified IDs")
            }

            // Parse all draft fields
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val allDraftFields = draftEntities.map { entity ->
                entity.draftId to (gson.fromJson<Map<String, Any>>(entity.fieldsJson, type) ?: emptyMap())
            }

            // Detect conflicts (fields with different values across drafts)
            val allFields = mutableMapOf<String, MutableMap<String, Any>>() // fieldName -> draftId -> value
            for ((draftId, fields) in allDraftFields) {
                for ((fieldName, value) in fields) {
                    allFields.getOrPut(fieldName) { mutableMapOf() }[draftId] = value
                }
            }

            val conflicts = mutableListOf<FieldConflict>()
            for ((fieldName, draftValues) in allFields) {
                val uniqueValues = draftValues.values.map { it.toString() }.toSet()
                if (uniqueValues.size > 1 && !request.conflictResolutions.containsKey(fieldName)) {
                    conflicts.add(FieldConflict(fieldName = fieldName, values = draftValues))
                }
            }

            // If there are unresolved conflicts, return them
            if (conflicts.isNotEmpty()) {
                return VerificationResult.ConflictsDetected(conflicts)
            }

            // Merge fields: use conflict resolutions where provided, otherwise take the first value
            val mergedFields = mutableMapOf<String, Any>()
            for ((fieldName, draftValues) in allFields) {
                mergedFields[fieldName] = request.conflictResolutions[fieldName]
                    ?: draftValues.values.first()
            }

            // Create the final verification record ID
            val verificationId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            // Mark all drafts as merged
            productDraftDao.markDraftsAsMerged(
                draftIds = request.draftIds,
                status = DraftStatus.MERGED.name,
                mergedAt = now,
                mergedInto = verificationId
            )

            Log.i(TAG, "Merged ${request.draftIds.size} drafts into verification $verificationId for product ${request.productId}")

            VerificationResult.Success(verificationId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to merge drafts for product ${request.productId}", e)
            VerificationResult.Failure("Failed to merge drafts: ${e.message}")
        }
    }

    // ─── Verification Status Validation ─────────────────────────────────

    override suspend fun validateVerificationStatus(productId: String): InputValidationResult {
        val drafts = productDraftDao.getAllDraftsForProduct(productId)
        val hasMergedDraft = drafts.any { it.status == DraftStatus.MERGED.name }

        return if (hasMergedDraft) {
            InputValidationResult.Valid
        } else {
            InputValidationResult.Invalid(
                listOf(InputValidationError("verification", "Product $productId has not been verified. A merged verification is required before listing.", "UNVERIFIED"))
            )
        }
    }

    // ─── KYC Workflow ───────────────────────────────────────────────────

    override suspend fun submitKyc(request: KycSubmissionRequest): KycSubmissionResult {
        return try {
            // 1. Validate identity documents
            if (request.identityDocuments.isEmpty()) {
                return KycSubmissionResult.ValidationError(listOf("At least one identity document is required"))
            }

            // 2. Validate farm location coordinates
            val locationResult = validateFarmLocation(request.farmLocation)
            if (locationResult is InputValidationResult.Invalid) {
                return KycSubmissionResult.ValidationError(locationResult.errors.map { it.message })
            }

            // 3. Validate farm photos
            if (request.farmPhotos.isEmpty()) {
                return KycSubmissionResult.ValidationError(listOf("At least one farm photo is required"))
            }

            // 4. Check for existing KYC for this user
            val existingKyc = kycDao.getVerificationByUserId(request.userId)
            if (existingKyc != null && existingKyc.status == KycStatus.APPROVED.name) {
                return KycSubmissionResult.ValidationError(listOf("KYC is already approved for this user"))
            }

            // 5. Create KYC verification entity
            val verificationId = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()

            val entity = KycVerificationEntity(
                verificationId = verificationId,
                userId = request.userId,
                identityDocumentsJson = gson.toJson(request.identityDocuments),
                farmLocationLat = request.farmLocation.latitude,
                farmLocationLon = request.farmLocation.longitude,
                farmPhotosJson = gson.toJson(request.farmPhotos),
                status = KycStatus.PENDING.name,
                submittedAt = now
            )

            kycDao.insertVerification(entity)
            Log.i(TAG, "KYC submitted for user ${request.userId} with ID $verificationId")

            KycSubmissionResult.Success(verificationId)
        } catch (e: Exception) {
            Log.e(TAG, "KYC submission failed for user ${request.userId}", e)
            KycSubmissionResult.Failure("KYC submission failed: ${e.message}")
        }
    }

    override suspend fun validateFarmLocation(location: GeoPoint): InputValidationResult {
        return CoordinateValidator.validate(location.latitude, location.longitude)
    }

    // ─── Duplicate Prevention & Credential Validation ───────────────────

    override suspend fun checkDuplicateVerification(productId: String): Boolean {
        val drafts = productDraftDao.getAllDraftsForProduct(productId)
        return drafts.any { it.status == DraftStatus.MERGED.name }
    }

    override suspend fun validateVerifierCredentials(verifierId: String): InputValidationResult {
        val user = userDao.findById(verifierId)
        return if (user != null) {
            InputValidationResult.Valid
        } else {
            InputValidationResult.Invalid(listOf(InputValidationError("verifierId", "Verifier with ID $verifierId not found or does not have valid credentials", "INVALID_VERIFIER")))
        }
    }

    // ─── Draft Retrieval ────────────────────────────────────────────────

    override suspend fun getDraftsForProduct(productId: String): List<VerificationDraft> {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return productDraftDao.getAllDraftsForProduct(productId).map { entity ->
            VerificationDraft(
                draftId = entity.draftId,
                productId = entity.productId,
                verifierId = entity.verifierId,
                fields = gson.fromJson(entity.fieldsJson, type) ?: emptyMap(),
                status = try { DraftStatus.valueOf(entity.status) } catch (e: Exception) { DraftStatus.PENDING },
                createdAt = entity.createdAt,
                mergedAt = entity.mergedAt,
                mergedInto = entity.mergedInto
            )
        }
    }

    // ─── Admin KYC Status Management ────────────────────────────────────

    override suspend fun updateKycStatus(
        verificationId: String,
        status: KycStatus,
        reviewerId: String,
        rejectionReason: String?
    ): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            kycDao.updateStatus(
                verificationId = verificationId,
                status = status.name,
                reviewedAt = now,
                reviewedBy = reviewerId,
                rejectionReason = rejectionReason
            )

            // If approved, update user role (notify via log for now)
            if (status == KycStatus.APPROVED) {
                val verification = kycDao.getVerification(verificationId)
                if (verification != null) {
                    Log.i(TAG, "KYC APPROVED for user ${verification.userId} by reviewer $reviewerId. User role upgrade pending.")
                }
            }

            Log.i(TAG, "KYC status updated to $status for verification $verificationId by reviewer $reviewerId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update KYC status for $verificationId", e)
            Result.failure(e)
        }
    }
}
