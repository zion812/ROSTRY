package com.rio.rostry.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
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
import android.net.TrafficStats
import java.util.Date

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) : BaseRepository(), UserRepository {

    private val usersCollection = firestore.collection("users")

    override fun getCurrentUser(): Flow<Resource<UserEntity?>> =
        flow<Resource<UserEntity?>> {
            TrafficStats.setThreadStatsTag(0xF00D) // Tag for user profile fetch
            try {
                val firebaseUser = firebaseAuth.currentUser
                val resolvedUserId = firebaseUser?.uid

                if (resolvedUserId != null) {
                    // 1. Emit local data immediately (Stale)
                    val localUser = userDao.getUserById(resolvedUserId).firstOrNull()
                    if (localUser != null) {
                        emit(Resource.Success<UserEntity?>(localUser))
                    }

                    // 2. Always fetch from Firestore (Revalidate)
                    if (firebaseUser != null) {
                        try {
                            val documentSnapshot = usersCollection.document(firebaseUser.uid).get().await()
                            val userEntity = documentSnapshot.toObject(UserEntity::class.java)
                            
                            if (userEntity != null) {
                                // Update local cache (triggers REPLACE)
                                userDao.upsertUser(userEntity)
                                // Emit fresh data
                                emit(Resource.Success<UserEntity?>(userEntity))
                            } else {
                                // No Firestore profile yet. Handle creation if needed.
                                if (firebaseUser.phoneNumber.isNullOrBlank()) {
                                    if (localUser == null) emit(Resource.Success<UserEntity?>(null))
                                } else {
                                    val newUser = UserEntity(
                                        userId = firebaseUser.uid,
                                        email = firebaseUser.email,
                                        phoneNumber = firebaseUser.phoneNumber
                                    )
                                    usersCollection.document(firebaseUser.uid).set(newUser).await()
                                    userDao.upsertUser(newUser)
                                    emit(Resource.Success<UserEntity?>(newUser))
                                }
                            }
                        } catch (e: Exception) {
                            if (e is kotlinx.coroutines.CancellationException) throw e
                            
                            // If we already emitted local data, suppress the error to keep the UI showing content
                            // Only throw/emit error if we have no data to show
                            if (localUser == null) {
                                if (e is com.google.firebase.firestore.FirebaseFirestoreException && 
                                    e.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                                     // Handle PERMISSION_DENIED (create logic) - duplicated from original but simplified for null case
                                     if (!firebaseUser.phoneNumber.isNullOrBlank()) {
                                        try {
                                            val newUser = UserEntity(
                                                userId = firebaseUser.uid,
                                                email = firebaseUser.email,
                                                phoneNumber = firebaseUser.phoneNumber
                                            )
                                            usersCollection.document(firebaseUser.uid).set(newUser).await()
                                            userDao.upsertUser(newUser)
                                            emit(Resource.Success<UserEntity?>(newUser))
                                        } catch (createError: Exception) {
                                            if (createError is kotlinx.coroutines.CancellationException) throw createError
                                            emit(Resource.Error<UserEntity?>("Failed to create user profile: ${createError.message}"))
                                        }
                                     } else {
                                         emit(Resource.Success<UserEntity?>(null))
                                     }
                                } else {
                                    throw e // Will be caught by outer flow catch
                                }
                            } else {
                                // Log the error but don't disrupt the user flow with an error state
                                android.util.Log.w("UserRepository", "Failed to refresh user profile from Firestore, using local cache: ${e.message}")
                            }
                        }
                    }
                } else {
                    emit(Resource.Success<UserEntity?>(null)) // No user logged in
                }
            } finally {
                TrafficStats.clearThreadStatsTag()
            }
        }
            .onStart { emit(Resource.Loading<UserEntity?>()) }
            .catch { e ->
                if (e is kotlinx.coroutines.CancellationException) throw e
                emit(Resource.Error<UserEntity?>("Failed to fetch user profile: ${e.message}"))
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
        return safeCall<Unit> {
            android.util.Log.e("UserRepositoryImpl", "updateUserProfile: safeCall started")
            usersCollection.document(userEntity.userId).set(userEntity).await()
            android.util.Log.e("UserRepositoryImpl", "updateUserProfile: Firestore update complete")
            userDao.upsertUser(userEntity) // Update local cache
            android.util.Log.e("UserRepositoryImpl", "updateUserProfile: Room update complete")
            Unit
        }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Update operation failed unexpectedly")
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

    /**
     * Updates the user's role type.
     * 
     * Production role upgrades require authentication. Offline updates are only permitted
     * for guest mode or test scenarios.
     */
    override suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit> = safeCall {
        // Enforce authentication for role upgrades
        if (firebaseAuth.currentUser == null) {
            throw IllegalStateException("Authentication required to update user role")
        }

        // Update Firestore
        val docRef = usersCollection.document(userId)
        val snapshot = docRef.get().await()
        val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
        val updated = current.copy(userType = newType.name, updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()

        // Update local Room database
        userDao.upsertUser(updated)
        
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update user type")

    override suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit> = safeCall {
        if (firebaseAuth.currentUser == null) {
            val current = userDao.getUserById(userId).firstOrNull() ?: UserEntity(userId = userId)
            val updated = current.copy(verificationStatus = status, updatedAt = System.currentTimeMillis())
            userDao.upsertUser(updated)
            Unit
        } else {
            val docRef = usersCollection.document(userId)
            val snapshot = docRef.get().await()
            val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
            val updated = current.copy(verificationStatus = status, updatedAt = System.currentTimeMillis())
            docRef.set(updated).await()
            userDao.upsertUser(updated)
            Unit
        }
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update verification status")

    override suspend fun updateVerificationSubmissionStatus(userId: String, status: VerificationStatus, reviewerId: String, rejectionReason: String?): Resource<Unit> = safeCall {
        val verificationsRef = firestore.collection("verifications").document(userId)
        val userRef = usersCollection.document(userId)

        firestore.runTransaction { transaction ->
            val verificationSnapshot = transaction.get(verificationsRef)
            if (!verificationSnapshot.exists()) {
                throw IllegalStateException("Verification document not found for user $userId")
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
        val updated = current.copy(verificationStatus = VerificationStatus.PENDING, updatedAt = System.currentTimeMillis())
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
            docRef.update(mapOf("phoneNumber" to phone, "updatedAt" to System.currentTimeMillis())).await()
            val local = userDao.getUserById(userId).firstOrNull()
            if (local != null) {
                userDao.upsertUser(local.copy(phoneNumber = phone, updatedAt = System.currentTimeMillis()))
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
            updatedAt = System.currentTimeMillis()
        )
        docRef.set(updated).await()
        userDao.upsertUser(updated)
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to update farm location verification")

    override fun streamPendingVerifications(): Flow<Resource<List<VerificationSubmission>>> =
        flow<Resource<List<VerificationSubmission>>> {
            // Query verifications collection directly for pending items
            val query = firestore.collection("verifications")
                .whereEqualTo("currentStatus", "PENDING")
                .orderBy("submittedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            
            val snap = query.get().await()
            val submissions = snap.toObjects(VerificationSubmission::class.java)
            emit(Resource.Success(submissions))
        }
            .onStart { emit(Resource.Loading()) }
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
        upgradeType: UpgradeType,
        currentRole: UserType,
        targetRole: UserType?
    ): Resource<Unit> = safeCall {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            throw IllegalStateException("User not authenticated")
        }
        
        // STRICT SECURITY: Always use the authenticated UID for the document path.
        val authUserId = currentUser.uid
        if (userId != authUserId) {
             android.util.Log.w("UserRepository", "submitKycVerification: userId param ($userId) does not match auth UID ($authUserId). Using auth UID.")
        }

        // Fetch current user details to snapshot location
        val userDoc = usersCollection.document(authUserId).get().await()
        val userEntity = userDoc.toObject(UserEntity::class.java)
        val locationSnapshot = if (userEntity?.farmLocationLat != null && userEntity.farmLocationLng != null) {
            mapOf("lat" to userEntity.farmLocationLat, "lng" to userEntity.farmLocationLng)
        } else null

        val batch = firestore.batch()
        val verificationsRef = firestore.collection("verifications").document(authUserId)
        val userRef = usersCollection.document(authUserId)
        
        // Create structured submission object
        // NOTE: We use null for submittedAt to allow @ServerTimestamp to work, 
        // effectively clearing client-side clock dependencies.
        val submission = VerificationSubmission(
            submissionId = submissionId,
            userId = authUserId,
            upgradeType = upgradeType,
            currentRole = currentRole,
            targetRole = targetRole,
            currentStatus = VerificationStatus.PENDING,
            documentUrls = documentUrls,
            imageUrls = imageUrls,
            documentTypes = docTypes,
            farmLocation = locationSnapshot,
            rejectionReason = null, // Clear previous rejection
            reviewedBy = null,
            reviewedAt = null,
            submittedAt = null // @ServerTimestamp will populate this
        )
        
        // 1. Write Verification Submission
        batch.set(verificationsRef, submission)
        
        // 2. Update User Profile Status atomically
        // Explicitly clear kycRejectionReason to remove "Rejected" UI state completely
        batch.update(userRef, mapOf(
            "verificationStatus" to VerificationStatus.PENDING,
            "kycRejectionReason" to null,
            "updatedAt" to FieldValue.serverTimestamp()
        ))
        
        batch.commit().await()
        
        // Update local cache
        val localUser = userDao.getUserById(authUserId).firstOrNull()
        if (localUser != null) {
            userDao.upsertUser(localUser.copy(
                verificationStatus = VerificationStatus.PENDING,
                kycRejectionReason = null
            ))
        }
        
        Unit
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to submit KYC verification")

    override fun getKycSubmissionStatus(userId: String): Flow<Resource<String?>> = callbackFlow {
        val docRef = firestore.collection("verifications").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error("Failed to listen to KYC status: ${error.message}"))
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val status = snapshot.getString("currentStatus") ?: snapshot.getString("status")
                trySend(Resource.Success(status))
            } else {
                trySend(Resource.Success(null))
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getVerificationDetails(userId: String): Flow<Resource<Map<String, Any>?>> = callbackFlow {
        val docRef = firestore.collection("verifications").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error("Failed to fetch verification details: ${error.message}"))
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(Resource.Success(snapshot.data))
            } else {
                trySend(Resource.Success(null))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getVerificationDetailsOnce(userId: String): Resource<Map<String, Any>?> = safeCall {
        val docRef = firestore.collection("verifications").document(userId)
        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            snapshot.data
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
        val docRef = firestore.collection("verifications").document(userId)
        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            snapshot.toObject(VerificationSubmission::class.java)
        } else {
            null
        }
    }.filter { it !is Resource.Loading<*> }.firstOrNull() ?: Resource.Error("Failed to get verification submission")
}
