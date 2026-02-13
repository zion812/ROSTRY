package com.rio.rostry.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rio.rostry.data.database.dao.EnthusiastVerificationDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.EnthusiastVerificationEntity
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import com.google.gson.Gson
import java.util.Date

class EnthusiastVerificationRepositoryImpl @Inject constructor(
    private val verificationDao: EnthusiastVerificationDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore
) : EnthusiastVerificationRepository {

    override fun getVerificationStatus(userId: String): Flow<EnthusiastVerificationEntity?> {
        return verificationDao.getLatestVerificationForUser(userId)
    }

    override suspend fun submitVerification(
        userId: String,
        experienceYears: Int,
        birdCount: Int,
        specializations: List<String>,
        achievementsDescription: String,
        referenceContacts: List<String>,
        documentUris: List<String>,
        profilePhotoUri: String?,
        farmPhotoUris: List<String>
    ): Result<Unit> = runCatching {
        // 1. Upload documents - SKIPPED as per user request (No Storage)
        val uploadedDocUrls = emptyList<String>()
        val uploadedProfilePhoto: String? = null
        val uploadedFarmPhotos = emptyList<String>()

        // 2. Create Entity
        val verificationId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val entity = EnthusiastVerificationEntity(
            verificationId = verificationId,
            userId = userId,
            experienceYears = experienceYears,
            birdCount = birdCount,
            specializations = Gson().toJson(specializations),
            achievementsDescription = achievementsDescription,
            referenceContacts = Gson().toJson(referenceContacts),
            verificationDocumentUrls = Gson().toJson(uploadedDocUrls),
            profilePhotoUrl = uploadedProfilePhoto,
            farmPhotoUrls = Gson().toJson(uploadedFarmPhotos),
            status = VerificationStatus.PENDING,
            submittedAt = now,
            reviewedAt = null,
            reviewedBy = null,
            rejectionReason = null,
            adminNotes = null,
            createdAt = now,
            updatedAt = now
        )

        // 3. Save to Room
        verificationDao.insertVerification(entity)

        // 4. Update User Status locally
        val user = userDao.getUserById(userId).firstOrNull()
        if (user != null) {
            userDao.upsertUser(user.copy(
                verificationStatus = VerificationStatus.PENDING,
                verificationSubmittedAt = Date(now)
            ))
        }

        // 5. Sync to Firestore
        val docData = mapOf(
            "verificationId" to verificationId,
            "userId" to userId,
            "status" to "PENDING",
            "experienceYears" to experienceYears,
            "birdCount" to birdCount,
            "specializations" to specializations,
            "achievementsDescription" to achievementsDescription,
            "referenceContacts" to referenceContacts,
            "documents" to uploadedDocUrls,
            "profilePhotoUrl" to uploadedProfilePhoto,
            "farmPhotoUrls" to uploadedFarmPhotos,
            "submittedAt" to now
        )
        firestore.collection("enthusiast_verifications").document(verificationId).set(docData).await()
        firestore.collection("users").document(userId).update(
            mapOf(
                "verificationStatus" to "PENDING",
                "verificationSubmittedAt" to now,
                "pendingRoleUpgrade" to "ENTHUSIAST"
            )
        ).await()
    }

    override suspend fun getVerificationHistory(userId: String): List<EnthusiastVerificationEntity> {
        return verificationDao.getAllVerificationsForUser(userId)
    }

    override fun getPendingVerifications(): Flow<List<EnthusiastVerificationEntity>> {
        return verificationDao.getPendingVerifications()
    }

    override suspend fun reviewVerification(
        verificationId: String,
        approved: Boolean,
        reviewerId: String,
        rejectionReason: String?
    ): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        val status = if (approved) VerificationStatus.VERIFIED else VerificationStatus.REJECTED
        
        // 1. Update local DB
        verificationDao.updateVerificationStatus(
            verificationId = verificationId,
            status = status,
            reviewedAt = now,
            reviewedBy = reviewerId,
            reason = rejectionReason,
            updatedAt = now
        )
        
        // 2. Get verification to find userId
        val verification = verificationDao.getVerificationById(verificationId)
            ?: throw IllegalStateException("Verification not found")
        
        // 3. Update user role if approved
        if (approved) {
            val user = userDao.getUserById(verification.userId).firstOrNull()
                ?: throw IllegalStateException("User not found")
            userDao.upsertUser(user.copy(
                verificationStatus = VerificationStatus.VERIFIED,
                userType = com.rio.rostry.domain.model.UserType.ENTHUSIAST.name
            ))
        } else {
            val user = userDao.getUserById(verification.userId).firstOrNull()
            if (user != null) {
                userDao.upsertUser(user.copy(
                    verificationStatus = VerificationStatus.REJECTED
                ))
            }
        }
        
        // 4. Sync to Firestore
        val updateData = mutableMapOf<String, Any?>(
            "status" to status.name,
            "reviewedAt" to now,
            "reviewedBy" to reviewerId,
            "updatedAt" to now
        )
        if (rejectionReason != null) {
            updateData["rejectionReason"] = rejectionReason
        }
        firestore.collection("enthusiast_verifications").document(verificationId)
            .update(updateData).await()
        
        // 5. Update user role in Firestore if approved
        if (approved) {
            firestore.collection("users").document(verification.userId).update(
                mapOf(
                    "verificationStatus" to "VERIFIED",
                    "userType" to "ENTHUSIAST",
                    "verificationApprovedAt" to now,
                    "pendingRoleUpgrade" to null
                )
            ).await()
        } else {
            firestore.collection("users").document(verification.userId).update(
                mapOf(
                    "verificationStatus" to "REJECTED",
                    "pendingRoleUpgrade" to null
                )
            ).await()
        }
    }
    

}
