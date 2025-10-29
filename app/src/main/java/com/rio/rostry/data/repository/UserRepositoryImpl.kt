package com.rio.rostry.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.demo.DemoAccounts
import com.rio.rostry.data.demo.toUserEntity
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
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose

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
            val firebaseUser = firebaseAuth.currentUser
            val demoUserId = sessionManager.currentDemoUserId().firstOrNull()
            val resolvedUserId = firebaseUser?.uid ?: demoUserId

            if (resolvedUserId != null) {
                val localUser = userDao.getUserById(resolvedUserId).firstOrNull()
                if (localUser != null) {
                    emit(Resource.Success<UserEntity?>(localUser))
                } else if (firebaseUser != null) {
                    // Fetch from Firestore if not in local DB
                    val documentSnapshot = usersCollection.document(firebaseUser.uid).get().await()
                    val userEntity = documentSnapshot.toObject(UserEntity::class.java)
                    if (userEntity != null) {
                        userDao.insertUser(userEntity) // Cache locally
                        emit(Resource.Success<UserEntity?>(userEntity))
                    } else {
                        // No Firestore profile yet. Defer auto-create until phone is linked to satisfy security rules.
                        if (firebaseUser.phoneNumber.isNullOrBlank()) {
                            emit(Resource.Success<UserEntity?>(null))
                        } else {
                            val newUser = UserEntity(
                                userId = firebaseUser.uid,
                                email = firebaseUser.email,
                                phoneNumber = firebaseUser.phoneNumber
                            )
                            usersCollection.document(firebaseUser.uid).set(newUser).await()
                            userDao.insertUser(newUser)
                            emit(Resource.Success<UserEntity?>(newUser))
                        }
                    }
                } else {
                    emit(Resource.Success<UserEntity?>(null))
                }
            } else {
                emit(Resource.Success<UserEntity?>(null)) // No user logged in
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
            userDao.insertUser(userEntity)
            Unit
        } else {
            throw IllegalStateException("User profile not found in Firestore during refresh.")
        }
    }.firstOrNull() ?: Resource.Error("Refresh operation failed unexpectedly")


    override suspend fun updateUserProfile(userEntity: UserEntity): Resource<Unit> = safeCall<Unit> {
        usersCollection.document(userEntity.userId).set(userEntity).await()
        userDao.insertUser(userEntity) // Update local cache
        Unit
    }.firstOrNull() ?: Resource.Error("Update operation failed unexpectedly")

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
                    userDao.insertUser(userEntity) // Cache locally
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

    override suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit> = safeCall {
        if (firebaseAuth.currentUser == null) {
            val current = userDao.getUserById(userId).firstOrNull() ?: UserEntity(userId = userId)
            val updated = current.copy(userType = newType, updatedAt = System.currentTimeMillis())
            userDao.insertUser(updated)
            Unit
        } else {
            val docRef = usersCollection.document(userId)
            val snapshot = docRef.get().await()
            val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
            val updated = current.copy(userType = newType, updatedAt = System.currentTimeMillis())
            docRef.set(updated).await()
            userDao.insertUser(updated)
            Unit
        }
    }.firstOrNull() ?: Resource.Error("Failed to update user type")

    override suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit> = safeCall {
        if (firebaseAuth.currentUser == null) {
            val current = userDao.getUserById(userId).firstOrNull() ?: UserEntity(userId = userId)
            val updated = current.copy(verificationStatus = status, updatedAt = System.currentTimeMillis())
            userDao.insertUser(updated)
            Unit
        } else {
            val docRef = usersCollection.document(userId)
            val snapshot = docRef.get().await()
            val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
            val updated = current.copy(verificationStatus = status, updatedAt = System.currentTimeMillis())
            docRef.set(updated).await()
            userDao.insertUser(updated)
            Unit
        }
    }.firstOrNull() ?: Resource.Error("Failed to update verification status")

    override suspend fun uploadVerificationEvidence(userId: String, evidenceUrls: List<String>): Resource<Unit> = safeCall {
        // Store evidence under nested field to avoid Room schema changes
        val docRef = usersCollection.document(userId)
        val updateMap = mapOf(
            "verification" to mapOf(
                "evidence" to evidenceUrls,
                "updatedAt" to System.currentTimeMillis()
            )
        )
        docRef.set(updateMap, com.google.firebase.firestore.SetOptions.merge()).await()
        val submissionId = UUID.randomUUID().toString()
        val subResult = submitKycVerification(userId, submissionId, evidenceUrls, emptyList())
        if (subResult is Resource.Error) {
            throw Exception("Failed to submit KYC: ${subResult.message}")
        }
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to upload verification evidence")

    override suspend fun requestBreederVerification(userId: String, breedingProofUrls: List<String>): Resource<Unit> = safeCall {
        val docRef = usersCollection.document(userId)
        val updateMap = mapOf(
            "verification" to mapOf(
                "breederProof" to breedingProofUrls,
                "requestedAt" to System.currentTimeMillis(),
                "status" to VerificationStatus.PENDING.name
            )
        )
        docRef.set(updateMap, com.google.firebase.firestore.SetOptions.merge()).await()
        // Also reflect status in Room user row if present
        val current = userDao.getUserById(userId).firstOrNull() ?: UserEntity(userId = userId)
        val updated = current.copy(verificationStatus = VerificationStatus.PENDING, updatedAt = System.currentTimeMillis())
        userDao.insertUser(updated)
        val submissionId = UUID.randomUUID().toString()
        val subResult = submitKycVerification(userId, submissionId, emptyList(), breedingProofUrls)
        if (subResult is Resource.Error) {
            throw Exception("Failed to submit KYC: ${subResult.message}")
        }
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to request breeder verification")

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
                userDao.insertUser(local.copy(phoneNumber = phone, updatedAt = System.currentTimeMillis()))
            }
        }
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to refresh phone number")

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
        userDao.insertUser(updated)
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to update farm location verification")

    override suspend fun seedDemoUsers() {
        val entities = DemoAccounts.all.map { it.toUserEntity() }
        userDao.upsertUsers(entities)
    }

    override suspend fun upsertDemoUser(userEntity: UserEntity): Resource<Unit> = safeCall {
        userDao.upsertUsers(listOf(userEntity))
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to upsert demo user")

    override fun streamPendingVerifications(): Flow<Resource<List<UserEntity>>> =
        flow<Resource<List<UserEntity>>> {
            val query = usersCollection.whereEqualTo("verificationStatus", VerificationStatus.PENDING)
            val snap = query.get().await()
            val users = snap.toObjects(UserEntity::class.java)
            emit(Resource.Success(users))
        }
            .onStart { emit(Resource.Loading()) }
            .catch { e ->
                if (e is kotlinx.coroutines.CancellationException) throw e
                emit(Resource.Error("Failed to load pending verifications: ${e.message}"))
            }

    override suspend fun submitKycVerification(userId: String, submissionId: String, documentUrls: List<String>, imageUrls: List<String>): Resource<Unit> = safeCall {
        val verificationsCollection = firestore.collection("verifications")
        val data = mapOf(
            "submissionId" to submissionId,
            "status" to "PENDING",
            "documentUrls" to documentUrls,
            "imageUrls" to imageUrls,
            "submittedAt" to System.currentTimeMillis()
        )
        verificationsCollection.document(userId).set(data, SetOptions.merge()).await()
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to submit KYC verification")

    override fun getKycSubmissionStatus(userId: String): Flow<Resource<String?>> = callbackFlow {
        val docRef = firestore.collection("verifications").document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error("Failed to listen to KYC status: ${error.message}"))
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val status = snapshot.getString("status")
                trySend(Resource.Success(status))
            } else {
                trySend(Resource.Success(null))
            }
        }
        awaitClose { listener.remove() }
    }
}
