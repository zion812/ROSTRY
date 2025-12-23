package com.rio.rostry.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rio.rostry.data.database.dao.FarmVerificationDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.FarmVerificationEntity
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import com.google.gson.Gson
import java.util.Date

class FarmVerificationRepositoryImpl @Inject constructor(
    private val verificationDao: FarmVerificationDao,
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FarmVerificationRepository {

    override fun getVerificationStatus(farmerId: String): Flow<FarmVerificationEntity?> {
        return verificationDao.getLatestVerificationForFarmer(farmerId)
    }

    override suspend fun submitVerification(
        farmerId: String,
        farmAddress: Map<String, String>,
        location: Map<String, Double?>,
        documentUris: List<String>,
        gpsAccuracy: Float?
    ): Result<Unit> = runCatching {
        // 1. Upload documents
        val uploadedUrls = documentUris.map { uriString ->
            if (uriString.startsWith("http")) {
                uriString
            } else {
                val uri = Uri.parse(uriString)
                val ref = storage.reference.child("verifications/farm/$farmerId/${UUID.randomUUID()}")
                ref.putFile(uri).await()
                ref.downloadUrl.await().toString()
            }
        }

        // 2. Create Entity
        val verificationId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val entity = FarmVerificationEntity(
            verificationId = verificationId,
            farmerId = farmerId,
            farmLocationLat = location["lat"],
            farmLocationLng = location["lng"],
            farmAddressLine1 = farmAddress["line1"],
            farmAddressLine2 = farmAddress["line2"],
            farmCity = farmAddress["city"],
            farmState = farmAddress["state"],
            farmPostalCode = farmAddress["postalCode"],
            farmCountry = farmAddress["country"],
            verificationDocumentUrls = Gson().toJson(uploadedUrls),
            gpsAccuracy = gpsAccuracy,
            gpsTimestamp = now,
            status = VerificationStatus.PENDING,
            submittedAt = now,
            reviewedAt = null,
            reviewedBy = null,
            rejectionReason = null,
            notes = null,
            createdAt = now,
            updatedAt = now
        )

        // 3. Save to Room
        verificationDao.insertVerification(entity)

        // 4. Update User Status locally
        val user: com.rio.rostry.data.database.entity.UserEntity? = userDao.getUserById(farmerId).firstOrNull()
        if (user != null) {
            userDao.upsertUser(user.copy(
                verificationStatus = VerificationStatus.PENDING,
                verificationSubmittedAt = Date(now)
            ))
        }

        // 5. Sync to Firestore
        val docData = mapOf(
            "verificationId" to verificationId,
            "farmerId" to farmerId,
            "status" to "PENDING",
            "location" to mapOf("lat" to location["lat"], "lng" to location["lng"]),
            "address" to farmAddress,
            "documents" to uploadedUrls,
            "submittedAt" to now
        )
        firestore.collection("farm_verifications").document(verificationId).set(docData).await()
        firestore.collection("users").document(farmerId).update(
            mapOf(
                "verificationStatus" to "PENDING",
                "verificationSubmittedAt" to now
            )
        ).await()
    }

    override suspend fun getVerificationHistory(farmerId: String): List<FarmVerificationEntity> {
        return verificationDao.getAllVerificationsForFarmer(farmerId)
    }
}
