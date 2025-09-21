package com.rio.rostry.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rio.rostry.data.base.BaseRepository
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : BaseRepository(), UserRepository {

    private val usersCollection = firestore.collection("users")

    override fun getCurrentUser(): Flow<Resource<UserEntity?>> = flow {
        emit(Resource.Loading())
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // Try to get from local DB first
            val localUser = userDao.getUserById(firebaseUser.uid).firstOrNull()
            if (localUser != null) {
                emit(Resource.Success(localUser))
            } else {
                // Fetch from Firestore if not in local DB
                try {
                    val documentSnapshot = usersCollection.document(firebaseUser.uid).get().await()
                    val userEntity = documentSnapshot.toObject(UserEntity::class.java)
                    if (userEntity != null) {
                        userDao.insertUser(userEntity) // Cache locally
                        emit(Resource.Success(userEntity))
                    } else {
                        // User exists in Auth but not in Firestore users collection (edge case)
                        // Create a basic profile or emit error
                        val newUser = UserEntity(
                            userId = firebaseUser.uid,
                            email = firebaseUser.email,
                            phoneNumber = firebaseUser.phoneNumber
                        )
                        usersCollection.document(firebaseUser.uid).set(newUser).await()
                        userDao.insertUser(newUser)
                        emit(Resource.Success(newUser))
                        // emit(Resource.Error("User profile not found in Firestore, created basic profile."))
                    }
                } catch (e: Exception) {
                    emit(Resource.Error("Failed to fetch user profile: ${e.message}"))
                }
            }
        } else {
            emit(Resource.Success(null)) // No user logged in
        }
    }

    override suspend fun refreshCurrentUser(userId: String): Resource<Unit> = safeCall<Unit> {
        // Fetch from Firestore and update local DB
        val documentSnapshot = usersCollection.document(userId).get().await()
        val userEntity = documentSnapshot.toObject(UserEntity::class.java)
        if (userEntity != null) {
            userDao.insertUser(userEntity)
            Resource.Success(Unit)
        } else {
            Resource.Error("User profile not found in Firestore during refresh.")
        }
    }.firstOrNull() ?: Resource.Error("Refresh operation failed unexpectedly")


    override suspend fun updateUserProfile(userEntity: UserEntity): Resource<Unit> = safeCall<Unit> {
        usersCollection.document(userEntity.userId).set(userEntity).await()
        userDao.insertUser(userEntity) // Update local cache
        Resource.Success(Unit)
    }.firstOrNull() ?: Resource.Error("Update operation failed unexpectedly")

    override fun getUserById(userId: String): Flow<Resource<UserEntity?>> = flow {
        emit(Resource.Loading())
        // Try local first
        val localUser = userDao.getUserById(userId).firstOrNull()
        if (localUser != null) {
            emit(Resource.Success(localUser))
        } else {
            // Fetch from Firestore if not in local DB
            try {
                val documentSnapshot = usersCollection.document(userId).get().await()
                val userEntity = documentSnapshot.toObject(UserEntity::class.java)
                if (userEntity != null) {
                    userDao.insertUser(userEntity) // Cache locally
                    emit(Resource.Success(userEntity))
                } else {
                    emit(Resource.Error("User with ID $userId not found in Firestore."))
                }
            } catch (e: Exception) {
                emit(Resource.Error("Failed to fetch user profile by ID: ${e.message}"))
            }
        }
    }

    override suspend fun updateUserType(userId: String, newType: UserType): Resource<Unit> = safeCall {
        val docRef = usersCollection.document(userId)
        val snapshot = docRef.get().await()
        val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
        val updated = current.copy(userType = newType, updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        userDao.insertUser(updated)
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to update user type")

    override suspend fun updateVerificationStatus(userId: String, status: VerificationStatus): Resource<Unit> = safeCall {
        val docRef = usersCollection.document(userId)
        val snapshot = docRef.get().await()
        val current = snapshot.toObject(UserEntity::class.java) ?: UserEntity(userId = userId)
        val updated = current.copy(verificationStatus = status, updatedAt = System.currentTimeMillis())
        docRef.set(updated).await()
        userDao.insertUser(updated)
        Unit
    }.firstOrNull() ?: Resource.Error("Failed to update verification status")

    // Implement other methods like signIn, signUp, signOut, etc.
    // Example:
    // override suspend fun signInWithEmail(email: String, password: String): Resource<UserEntity> {
    //     return safeCall {
    //         val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
    //         val firebaseUser = authResult.user
    //         if (firebaseUser != null) {
    //             val userDoc = usersCollection.document(firebaseUser.uid).get().await()
    //             val userEntity = userDoc.toObject(UserEntity::class.java)
    //                 ?: UserEntity(userId = firebaseUser.uid, email = firebaseUser.email) // Fallback or throw error
    //             userDao.insertUser(userEntity)
    //             userEntity
    //         } else {
    //             throw IllegalStateException("Firebase user is null after sign in")
    //         }
    //     }.firstOrNull() ?: Resource.Error("Sign in failed unexpectedly")
    // }
}
