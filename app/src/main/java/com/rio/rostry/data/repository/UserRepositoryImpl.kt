package com.rio.rostry.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import com.rio.rostry.data.database.dao.RoleMigrationDao
import com.rio.rostry.data.database.entity.RoleMigrationEntity
import com.rio.rostry.workers.RoleUpgradeMigrationWorker
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.model.UpgradeType
import com.rio.rostry.domain.model.VerificationSubmission
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
import com.rio.rostry.data.migration.RoleUpgradeMigrationService
import com.rio.rostry.data.migration.MigrationResult
import androidx.work.WorkManager
import com.rio.rostry.workers.enqueueMigration
import kotlinx.coroutines.launch
import android.net.TrafficStats
import java.util.Date

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val roleMigrationDao: RoleMigrationDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager,
    private val migrationService: RoleUpgradeMigrationService,
    @ApplicationContext private val context: Context
) : BaseRepository(), UserRepository {

    private val usersCollection = firestore.collection("users")

    override fun getCurrentUser(): Flow<Resource<UserEntity?>> = callbackFlow {
        TrafficStats.setThreadStatsTag(0xF00D)
        val firebaseUser = firebaseAuth.currentUser
        val userId = firebaseUser?.uid

        if (userId == null) {
            trySend(Resource.Success(null))
            close()
            return@callbackFlow
        }

        // 1. Emit local data immediately (stale-while-revalidate)
        val localUser = userDao.getUserById(userId).firstOrNull()
        if (localUser != null) {
            trySend(Resource.Success(localUser))
        }

        // 2. Setup Real-time Listener
        val registration = usersCollection.document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                val isPermissionDenied = error.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED
                
                if (isPermissionDenied) {
                    android.util.Log.w("UserRepository", "Permission denied - token may be stale")
                    // Close listener to prevent infinite retries. Note: callbackFlow's close() 
                    // will trigger awaitClose { registration.remove() } but since registration 
                    // is being assigned after this lambda, we must be careful. 
                    // Actually, Room/Firestore might not have set registration yet.
                    // But in callbackFlow, close() is enough.
                    trySend(Resource.Error("Permission denied. Please refresh."))
                    close()
                } else if (localUser == null) {
                    trySend(Resource.Error("Failed to listen for user updates: ${error.message}"))
                } else {
                    android.util.Log.w("UserRepository", "Firestore listen error: ${error.message}")
                }
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val userEntity = snapshot.toObject(UserEntity::class.java)
                if (userEntity != null) {
                    // Update local cache
                    launch {
                        userDao.upsertUser(userEntity)
                    }
                    trySend(Resource.Success(userEntity))
                }
            } else if (snapshot != null && !snapshot.exists()) {
                // User document doesn't exist yet (e.g. new auth user)
                // Support both phone auth AND Google Sign-In (email-only) users
                val hasPhoneOrEmail = firebaseUser.phoneNumber != null || firebaseUser.email != null
                if (hasPhoneOrEmail) {
                    // Check if this is a known admin email
                    val isHardcodedAdmin = com.rio.rostry.ui.session.SessionViewModel.ADMIN_EMAILS.contains(firebaseUser.email)
                    
                    val initialRole = if (isHardcodedAdmin) UserType.ADMIN else UserType.GENERAL
                    
                    val newUser = UserEntity(
                        userId = userId,
                        email = firebaseUser.email,
                        phoneNumber = firebaseUser.phoneNumber,
                        fullName = firebaseUser.displayName,
                        profilePictureUrl = firebaseUser.photoUrl?.toString(),
                        userType = initialRole.name // Set role during creation
                    )
                    // Create it
                    // We use launch to do the async write
                    launch {
                        try {
                             usersCollection.document(userId).set(newUser).await()
                             userDao.upsertUser(newUser)
                             trySend(Resource.Success(newUser))
                        } catch (e: Exception) {
                            trySend(Resource.Error("Failed to create initial user: ${e.message}"))
                        }
                    }
                } else {
                    // No phone or email, maybe deleted?
                    trySend(Resource.Success(null))
                }
            }
        }

        awaitClose {
            registration.remove()
            TrafficStats.clearThreadStatsTag()
        }
    }

    override suspend fun refreshCurrentUser(userId: String): Resource<Unit> = safeCall<Unit> {
        // Fetch from Firestore and update local DB
        val documentSnapshot = usersCollection.document(userId).get().await()
        val userEntity = documentSnapshot.toObject(UserEntity::class.java)
        if (userEntity != null) {
            userDao.upsertUser(userEntity)
            Unit
        } else {
            throw IllegalStateException("User profile not found in Firestore during refresh.")
        }
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Refresh operation failed unexpectedly")


    override suspend fun updateUserProfile(userEntity: UserEntity): Resource<Unit> {
        android.util.Log.e("UserRepositoryImpl", "updateUserProfile called for ${userEntity.userId}")
        
        // Helper to perform the write
        suspend fun performWrite(): Resource<Unit> = safeCall<Unit> {
            android.util.Log.e("UserRepositoryImpl", "updateUserProfile: safeCall started")
            usersCollection.document(userEntity.userId).set(userEntity).await()
            android.util.Log.e("UserRepositoryImpl", "updateUserProfile: Firestore update complete")
            userDao.upsertUser(userEntity) // Update local cache
            android.util.Log.e("UserRepositoryImpl", "updateUserProfile: Room update complete")
            Unit
        }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Update operation failed unexpectedly")

        val result = performWrite()
        
        // If permission denied, try ONE token refresh and retry
        // This handles cases where the session might be stale or AppCheck context is missing
        if (result is Resource.Error && result.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
            android.util.Log.w("UserRepositoryImpl", "Permission denied for profile update. Attempting token refresh and retry...")
            try {
                // Force a token refresh to update the security context
                firebaseAuth.currentUser?.getIdToken(true)?.await()
                android.util.Log.d("UserRepositoryImpl", "Token refreshed successfully. Retrying profile update...")
                return performWrite() // Retry once
            } catch (e: Exception) {
                android.util.Log.e("UserRepositoryImpl", "Token refresh failed during retry: ${e.message}")
            }
        }
        
        return result
    }

    override fun getUserById(userId: String): Flow<Resource<UserEntity?>> =
        flow<Resource<UserEntity?>> {
            // Try local first
            val localUser = userDao.getUserById(userId).firstOrNull()
            if (localUser != null) {
                emit(Resource.Success<UserEntity?>(localUser))
            } else {
                // Fetch from Firestore if not in local DB
                val documentSnapshot = usersCollection.document(userId).get().await()
                val userEntity = documentSnapshot.toObject(UserEntity::class.java)
                if (userEntity != null) {
                    userDao.upsertUser(userEntity) // Cache locally
                    emit(Resource.Success<UserEntity?>(userEntity))
                } else {
                    emit(Resource.Error<UserEntity?>("User with ID $userId not found in Firestore."))
                }
            }
        }
            .onStart { emit(Resource.Loading<UserEntity?>()) }
            .catch { e ->
                if (e is kotlinx.coroutines.CancellationException) throw e
                emit(Resource.Error<UserEntity?>("Failed to fetch user profile by ID: ${e.message}"))
            }

    override fun searchUsers(query: String): Flow<List<UserEntity>> = userDao.searchUsers(query)

    override fun searchUsersForTransfer(query: String, currentUserId: String): Flow<List<UserEntity>> = userDao.searchUsersForTransfer(query, currentUserId)

    /**
     * Updates the user's role type.
     * 
     * Production role upgrades require authentication. Offline updates are only permitted
     * for guest mode or test scenarios.
     */
    override suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit> {
        // Helper to perform the role update
        suspend fun performUpdate(): Resource<Unit> = safeCall {
            // Enforce authentication for role upgrades
            if (firebaseAuth.currentUser == null) {
                throw IllegalStateException("Authentication required to update user role")
            }

            // Update Firestore
            val docRef = usersCollection.document(userId)
            val snapshot = docRef.get().await()
            val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
            val updated = current.copy(userType = newType.name, updatedAt = Date())
            docRef.set(updated).await()

            // Update local Room database
            userDao.upsertUser(updated)
            
            Unit
        }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update user type")

        val result = performUpdate()

        // Retry logic for PERMISSION_DENIED (stale token)
        if (result is Resource.Error && result.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true) {
            android.util.Log.w("UserRepositoryImpl", "Permission denied for role update. Attempting token refresh and retry...")
            try {
                firebaseAuth.currentUser?.getIdToken(true)?.await()
                android.util.Log.d("UserRepositoryImpl", "Token refreshed successfully. Retrying role update...")
                return performUpdate()
            } catch (e: Exception) {
                android.util.Log.e("UserRepositoryImpl", "Token refresh failed during retry: ${e.message}")
            }
        }

        return result
    }

    override suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit> = safeCall {
        if (firebaseAuth.currentUser == null) {
            val current = userDao.getUserById(userId).firstOrNull() ?: UserEntity(userId = userId)
            val updated = current.copy(verificationStatus = status, updatedAt = Date())
            userDao.upsertUser(updated)
            Unit
        } else {
            val docRef = usersCollection.document(userId)
            val snapshot = docRef.get().await()
            val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
            val updated = current.copy(verificationStatus = status, updatedAt = Date())
            docRef.set(updated).await()
            userDao.upsertUser(updated)
            Unit
        }
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update verification status")

    override suspend fun updateVerificationSubmissionStatus(submissionId: String, userId: String, status: VerificationStatus, reviewerId: String, rejectionReason: String?): Resource<Unit> = safeCall {
        val verificationsRef = firestore.collection("verifications").document(submissionId)
        val userRef = usersCollection.document(userId)

        firestore.runTransaction { transaction ->
            val verificationSnapshot = transaction.get(verificationsRef)
            if (!verificationSnapshot.exists()) {
                throw IllegalStateException("Verification document not found for submission $submissionId")
            }

            // 1. Update Verification Document
            val updates = mutableMapOf<String, Any>(
                "currentStatus" to status,
                "reviewedAt" to FieldValue.serverTimestamp(),
                "reviewedBy" to reviewerId
            )
            if (rejectionReason != null) {
                updates["rejectionReason"] = rejectionReason
            }
            transaction.update(verificationsRef, updates)

            // 2. Update User Document
            val userUpdates = mutableMapOf<String, Any?>(
                "verificationStatus" to status,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            // specific logic for VERIFIED status
            if (status == VerificationStatus.VERIFIED) {
                userUpdates["kycRejectionReason"] = null // Clear strict null
                
                // If there was a target role requested, grant it
                val targetRoleString = verificationSnapshot.getString("targetRole")
                if (!targetRoleString.isNullOrBlank()) {
                    userUpdates["userType"] = targetRoleString
                }
                userUpdates["kycVerifiedAt"] = System.currentTimeMillis() // or ServerTimestamp if field type changed to timestamp
            } 
            // specific logic for REJECTED status
            else if (status == VerificationStatus.REJECTED && rejectionReason != null) {
                userUpdates["kycRejectionReason"] = rejectionReason
            }

            transaction.update(userRef, userUpdates)
        }.await()
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update verification submission status")

    override suspend fun uploadVerificationEvidence(userId: String, evidenceUrls: List<String>): Resource<Unit> = safeCall {
        // Fetch current user to determine upgrade type
        val current = userDao.getUserById(userId).firstOrNull() 
            ?: throw IllegalStateException("User not found for verification evidence upload")

        val upgradeType = when {
            current.role == UserType.GENERAL && current.verificationStatus == VerificationStatus.UNVERIFIED -> UpgradeType.GENERAL_TO_FARMER
            current.role == UserType.FARMER && (current.verificationStatus == VerificationStatus.UNVERIFIED || current.verificationStatus == VerificationStatus.PENDING || current.verificationStatus == VerificationStatus.REJECTED) -> UpgradeType.FARMER_VERIFICATION
            current.role == UserType.FARMER && current.verificationStatus == VerificationStatus.VERIFIED -> UpgradeType.FARMER_TO_ENTHUSIAST
            else -> UpgradeType.GENERAL_TO_FARMER // Default fallback
        }

        // Only submit to verifications collection
        val submissionId = UUID.randomUUID().toString()
        val subResult = submitKycVerification(
            userId = userId, 
            submissionId = submissionId, 
            documentUrls = evidenceUrls, 
            imageUrls = emptyList(),
            upgradeType = upgradeType,
            currentRole = current.role
        )
        if (subResult is Resource.Error) {
            throw Exception("Failed to submit KYC: ${subResult.message}")
        }
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to upload verification evidence")

    override suspend fun requestBreederVerification(userId: String, breedingProofUrls: List<String>): Resource<Unit> = safeCall {
        val current = userDao.getUserById(userId).firstOrNull() ?: UserEntity(userId = userId)
        
        // Guard: Only FARMERs can request breeder verification
        if (current.role != UserType.FARMER) {
            throw IllegalStateException("Only farmers can request breeder verification")
        }

        // Also reflect status in Room user row if present
        val updated = current.copy(verificationStatus = VerificationStatus.PENDING, updatedAt = Date())
        userDao.upsertUser(updated)
        
        // Update status in Firestore users collection (lightweight flag only)
        usersCollection.document(userId).update("verificationStatus", VerificationStatus.PENDING).await()

        val submissionId = UUID.randomUUID().toString()
        val subResult = submitKycVerification(
            userId = userId, 
            submissionId = submissionId, 
            documentUrls = emptyList(), 
            imageUrls = breedingProofUrls, 
            docTypes = emptyMap(), 
            upgradeType = UpgradeType.FARMER_TO_ENTHUSIAST, 
            currentRole = current.role, 
            targetRole = UserType.ENTHUSIAST
        )
        if (subResult is Resource.Error) {
            throw Exception("Failed to submit KYC: ${subResult.message}")
        }
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to request breeder verification")

    override suspend fun refreshPhoneNumber(userId: String): Resource<Unit> = safeCall<Unit> {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null || firebaseUser.uid != userId) {
            throw IllegalStateException("User not authenticated")
        }
        val phone = firebaseUser.phoneNumber
        if (phone != null) {
            val docRef = usersCollection.document(userId)
            docRef.update(mapOf("phoneNumber" to phone, "updatedAt" to FieldValue.serverTimestamp())).await()
            val local = userDao.getUserById(userId).firstOrNull()
            if (local != null) {
                userDao.upsertUser(local.copy(phoneNumber = phone, updatedAt = Date()))
            }
        }
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to refresh phone number")

    override suspend fun updateFarmLocationVerification(userId: String, latitude: Double, longitude: Double): Resource<Unit> = safeCall {
        val docRef = usersCollection.document(userId)
        val snapshot = docRef.get().await()
        val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
        val updated = current.copy(
            farmLocationLat = latitude,
            farmLocationLng = longitude,
            locationVerified = true,
            updatedAt = Date()
        )
        docRef.set(updated).await()
        userDao.upsertUser(updated)
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update farm location verification")

    override fun streamPendingVerifications(): Flow<Resource<List<VerificationSubmission>>> = safeCall {
        // Query verifications collection directly for pending items
        val query = firestore.collection("verifications")
            .whereEqualTo("currentStatus", "PENDING")
            .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
        
        val snap = query.get().await()
        snap.toObjects(VerificationSubmission::class.java)
    }.filter { it !is Resource.Loading<*> }.onStart { emit(Resource.Loading()) }
     .catch { e ->
         if (e is kotlinx.coroutines.CancellationException) throw e
         emit(Resource.Error("Failed to load pending verifications: ${e.message}"))
     }

    override suspend fun submitKycVerification(
        userId: String, 
        submissionId: String, 
        documentUrls: List<String>, 
        imageUrls: List<String>, 
        docTypes: Map<String, String>,
        imageTypes: Map<String, String>,
        upgradeType: UpgradeType,
        currentRole: UserType,
        targetRole: UserType?,
        farmLocation: Map<String, Double>?,
        applicantPhone: String?
    ): Resource<Unit> = safeCall {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User not authenticated")
        }
        
        // FORCE TOKEN REFRESH: Ensure we have a fresh token before sensitive batch write
        // to prevent PERMISSION_DENIED due to expired or stale security context.
        try {
            currentUser.getIdToken(true).await()
            android.util.Log.d("UserRepository", "Token refreshed successfully before KYC submission")
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "Failed to refresh token before KYC submission: ${e.message}")
            // Continue anyway; the existing token might still be valid or Firestore might succeed anyway 
        }
        // STRICT SECURITY: Always use the authenticated UID for security validation.
        val authUserId = currentUser.uid
        if (userId != authUserId) {
             android.util.Log.w("UserRepository", "submitKycVerification: userId param ($userId) does not match auth UID ($authUserId). Using auth UID.")
        }

        // Generate a human-readable reference number for admin tracking
        // Format: KYC-YYYYMMDD-XXXXXX (where X is short UUID suffix)
        val dateStr = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(Date())
        val shortId = submissionId.takeLast(6).uppercase()
        val referenceNumber = "KYC-$dateStr-$shortId"
        
        // Fetch user info for denormalization (helps admin identify applicant)
        val localUser = userDao.getUserById(authUserId).firstOrNull()
        val finalApplicantName = localUser?.fullName ?: localUser?.phoneNumber ?: "Unknown"
        // Use passed phone if available, otherwise fallback to profile phone
        val finalApplicantPhone = if (!applicantPhone.isNullOrBlank()) applicantPhone else localUser?.phoneNumber
        val farmAddress = localUser?.farmAddressLine1?.let { addr ->
            listOfNotNull(addr, localUser.farmCity, localUser.farmState)
                .joinToString(", ")
        }

        val batch = firestore.batch()
        
        // IMPORTANT: Use submissionId as document ID for uniqueness and history tracking
        // This allows multiple submissions per user (resubmissions after rejection)
        val verificationsRef = firestore.collection("verifications").document(submissionId)
        val userRef = usersCollection.document(authUserId)
        
        // Create structured submission map to ensure only allowed fields are sent to Firestore
        // This avoids PERMISSION_DENIED errors caused by sending protected fields (like reviewedBy, rejectionReason) 
        // which are present in the data class but forbidden by the strict keys().hasOnly() rule in security rules.
        val submissionMap = hashMapOf<String, Any?>(
            "submissionId" to submissionId,
            "userId" to authUserId,
            "referenceNumber" to referenceNumber,
            "applicantName" to finalApplicantName,
            "applicantPhone" to finalApplicantPhone,
            "upgradeType" to upgradeType.name,
            "currentRole" to currentRole.name,
            "targetRole" to targetRole?.name,
            "currentStatus" to VerificationStatus.PENDING.name,
            "documentUrls" to documentUrls,
            "imageUrls" to imageUrls,
            "documentTypes" to docTypes,
            "imageTypes" to imageTypes,
            "farmLocation" to farmLocation,
            "farmAddress" to farmAddress,
            "additionalData" to emptyMap<String, Any>(),
            "submittedAt" to FieldValue.serverTimestamp()
        )
        
        // 1. Write Verification Submission with submissionId as document ID
        batch.set(verificationsRef, submissionMap)
        
        // 2. Update User Profile with reference to latest submission
        // Explicitly clear kycRejectionReason to remove "Rejected" UI state completely
        batch.update(userRef, mapOf(
            "verificationStatus" to VerificationStatus.PENDING.name,
            "kycRejectionReason" to null,
            "latestVerificationId" to submissionId,
            "latestVerificationRef" to referenceNumber,
            "updatedAt" to FieldValue.serverTimestamp()
        ))
        
        batch.commit().await()
        
        android.util.Log.i("UserRepository", "Verification submitted: $referenceNumber (ID: $submissionId)")
        
        // Update local cache with PENDING status and references to the submission
        val updatedLocal = localUser?.copy(
            verificationStatus = VerificationStatus.PENDING,
            kycRejectionReason = null,
            latestVerificationId = submissionId,
            latestVerificationRef = referenceNumber
        )
        if (updatedLocal != null) {
            userDao.upsertUser(updatedLocal)
        }
        
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to submit KYC verification")

    override fun getKycSubmissionStatus(userId: String): Flow<Resource<String?>> = flow {
        emit(Resource.Loading())
        try {
            // Optimization: Use get() instead of snapshot listener to save quota
            val query = firestore.collection("verifications")
                .whereEqualTo("userId", userId)
                .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)

            val snapshot = query.get().await()
            if (!snapshot.isEmpty) {
                val status = snapshot.documents.first().getString("currentStatus")
                    ?: snapshot.documents.first().getString("status")
                emit(Resource.Success(status))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch KYC status: ${e.message}"))
        }
    }

    override fun getVerificationDetails(userId: String): Flow<Resource<Map<String, Any>?>> = flow {
        emit(Resource.Loading())
        try {
            // Optimization: Use get() instead of snapshot listener to save quota
            val query = firestore.collection("verifications")
                .whereEqualTo("userId", userId)
                .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)

            val snapshot = query.get().await()
            if (!snapshot.isEmpty) {
                emit(Resource.Success(snapshot.documents.first().data))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to fetch verification details: ${e.message}"))
        }
    }

    override suspend fun getVerificationDetailsOnce(userId: String): Resource<Map<String, Any>?> = safeCall {
        // Query by userId field to get the latest submission
        val query = firestore.collection("verifications")
            .whereEqualTo("userId", userId)
            .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
        
        val snapshot = query.get().await()
        if (!snapshot.isEmpty) {
            snapshot.documents.first().data
        } else {
            null
        }
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to fetch verification details")

    override fun getVerificationsByUpgradeType(upgradeType: UpgradeType): Flow<Resource<List<VerificationSubmission>>> = 
        flow<Resource<List<VerificationSubmission>>> {
            val query = firestore.collection("verifications")
                .whereEqualTo("upgradeType", upgradeType.name)
                .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            
            val snap = query.get().await()
            val submissions = snap.toObjects(VerificationSubmission::class.java)
            emit(Resource.Success(submissions))
        }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            if (e is kotlinx.coroutines.CancellationException) throw e
            emit(Resource.Error("Failed to load verifications by type: ${e.message}"))
        }

    override fun getVerificationsByRoleAndStatus(role: UserType?, status: VerificationStatus?): Flow<Resource<List<VerificationSubmission>>> = 
        flow<Resource<List<VerificationSubmission>>> {
            var query: com.google.firebase.firestore.Query = firestore.collection("verifications")
            
            if (role != null) {
                query = query.whereEqualTo("currentRole", role.name)
            }
            
            if (status != null) {
                query = query.whereEqualTo("currentStatus", status.name)
            }
            
            query = query.orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            
            val snap = query.get().await()
            val submissions = snap.toObjects(VerificationSubmission::class.java)
            emit(Resource.Success(submissions))
        }
        .onStart { emit(Resource.Loading()) }
        .catch { e ->
            if (e is kotlinx.coroutines.CancellationException) throw e
            emit(Resource.Error("Failed to load verifications: ${e.message}"))
        }

    override suspend fun getVerificationSubmission(userId: String): Resource<VerificationSubmission?> = safeCall {
        // Query by userId field (document ID is now submissionId)
        // Get the most recent submission for this user
        val query = firestore.collection("verifications")
            .whereEqualTo("userId", userId)
            .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
        
        val snapshot = query.get().await()
        if (!snapshot.isEmpty) {
            snapshot.documents.first().toObject(VerificationSubmission::class.java)
        } else {
            null
        }
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to get verification submission")

    override suspend fun getCurrentUserSuspend(): UserEntity? {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            val userId = firebaseUser?.uid ?: return null

            // 1. Try local cache
            val localUser = userDao.getUserById(userId).firstOrNull()
            if (localUser != null) return localUser

            // 2. Fetch from Firestore if missing locally
            val snapshot = usersCollection.document(userId).get().await()
            if (snapshot.exists()) {
                val userEntity = snapshot.toObject(UserEntity::class.java)
                if (userEntity != null) {
                    userDao.upsertUser(userEntity) // Cache it
                    return userEntity
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun initiateRoleMigration(userId: String): Resource<Unit> {
        return try {
            val result = migrationService.initiateMigration(userId, migrateData = true)
            when (result) {
                is MigrationResult.Success -> {
                    val workManager = WorkManager.getInstance(context)
                    workManager.enqueueMigration(result.migrationId, userId)
                    Resource.Success(Unit)
                }
                is MigrationResult.Error -> Resource.Error(result.message)
                is MigrationResult.InsufficientQuota -> Resource.Error("Insufficient storage quota")
                is MigrationResult.AlreadyMigrating -> Resource.Error("Migration already in progress")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to initiate migration")
        }
    }

    override fun getRoleMigrationStatus(userId: String): Flow<Resource<RoleMigrationEntity?>> =
        roleMigrationDao.observeLatestForUser(userId)
            .map { Resource.Success(it) as Resource<RoleMigrationEntity?> }
            .onStart { emit(Resource.Loading<RoleMigrationEntity?>()) }
            .catch { e -> emit(Resource.Error<RoleMigrationEntity?>(e.message ?: "Error observing migration status")) }
    
    override suspend fun uploadProfileImage(userId: String, imageUri: android.net.Uri): Resource<String> = safeCall {
        val storage = com.google.firebase.storage.FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("profile_images/$userId/${System.currentTimeMillis()}.jpg")
        
        // Upload image to Firebase Storage
        storageRef.putFile(imageUri).await()
        
        // Get download URL
        val downloadUrl = storageRef.downloadUrl.await().toString()
        
        // Update user profile with new photo URL
        val docRef = usersCollection.document(userId)
        docRef.update(mapOf(
            "photoUrl" to downloadUrl,
            "profilePictureUrl" to downloadUrl,
            "updatedAt" to FieldValue.serverTimestamp()
        )).await()
        
        // Update local cache
        val localUser = userDao.getUserById(userId).firstOrNull()
        if (localUser != null) {
            userDao.upsertUser(localUser.copy(
                profilePictureUrl = downloadUrl,
                updatedAt = Date()
            ))
        }
        
        downloadUrl
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to upload profile image")

    override suspend fun deleteUser(userId: String): Resource<Unit> = safeCall {
        // 1. Delete from Firestore
        usersCollection.document(userId).delete().await()
        
        // 2. Delete from Local DB
        userDao.deleteUserById(userId)
        
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to delete user")

    override suspend fun getSystemUsers(limit: Int): Resource<List<UserEntity>> = safeCall {
        // Fetch users directly from Firestore for Admin visibility
        // This ensures we see all users, not just those cached locally
        val snapshot = usersCollection
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            
        val users = snapshot.toObjects(UserEntity::class.java)
        
        // Optionally update local cache with what we found
        if (users.isNotEmpty()) {
            userDao.upsertUsers(users)
        }
        
        users
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to fetch system users")

    override suspend fun suspendUser(userId: String, reason: String, durationMinutes: Long?): Resource<Unit> = safeCall {
        val updates = mutableMapOf<String, Any>(
            "isSuspended" to true,
            "suspensionReason" to reason,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (durationMinutes != null) {
            updates["suspensionEndsAt"] = System.currentTimeMillis() + (durationMinutes * 60 * 1000)
        }
        
        usersCollection.document(userId).update(updates).await()
        
        // Update local cache
        val localUser = userDao.getUserById(userId).firstOrNull()
        if (localUser != null) {
            val updatedUser = localUser.copy(
                isSuspended = true,
                suspensionReason = reason,
                suspensionEndsAt = updates["suspensionEndsAt"] as? Long,
                updatedAt = Date()
            )
            userDao.upsertUser(updatedUser)
        }
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to suspend user")

    override suspend fun unsuspendUser(userId: String): Resource<Unit> = safeCall {
        val updates = mapOf<String, Any?>(
            "isSuspended" to false,
            "suspensionReason" to null,
            "suspensionEndsAt" to null,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        
        usersCollection.document(userId).update(updates).await()
        
        // Update local cache
        val localUser = userDao.getUserById(userId).firstOrNull()
        if (localUser != null) {
            val updatedUser = localUser.copy(
                isSuspended = false,
                suspensionReason = null,
                suspensionEndsAt = null,
                updatedAt = Date()
            )
            userDao.upsertUser(updatedUser)
        }
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to unsuspend user")

    override suspend fun getAudienceSize(role: UserType?): Int {
        return if (role == null) {
            userDao.countAllUsers()
        } else {
            userDao.countUsersByRole(role.name)
        }
    }

    override fun getUsersStreamByRole(role: UserType): Flow<List<UserEntity>> {
        return userDao.getUsersByRole(role.name)
    }

    override suspend fun getNewUsersCount(since: Long): Int {
        return userDao.countUsersCreatedAfter(since)
    }

    override suspend fun getActiveUsersCount(since: Long): Int {
        return userDao.countUsersUpdatedAfter(since)
    }

    override suspend fun countAllUsers(): Int {
        return userDao.countAllUsers()
    }

    override suspend fun getPendingVerificationCount(): Int {
        return try {
            // Check both collections for accuracy, or prioritize Users collection as the source of truth for "Status"
            // Using users collection ensures we catch orphans
            val snapshot = usersCollection
                .whereEqualTo("verificationStatus", VerificationStatus.PENDING.name)
                .count()
                .get(com.google.firebase.firestore.AggregateSource.SERVER)
                .await()
            snapshot.count.toInt()
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getUsersByVerificationStatus(status: VerificationStatus): Resource<List<UserEntity>> = safeCall {
        val snapshot = usersCollection
            .whereEqualTo("verificationStatus", status.name)
            .get()
            .await()
            
        val users = snapshot.toObjects(UserEntity::class.java)
        if (users.isNotEmpty()) {
            userDao.upsertUsers(users)
        }
        users
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to fetch users by status")
}
