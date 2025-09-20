package com.rio.rostry.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.KycStatus
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.repository.UserRepository
import com.rio.rostry.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

/**
 * Implementation of AuthRepository using Firebase Authentication and local database
 */
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun sendPhoneVerificationCode(phoneNumber: String): Resource<Unit> {
        return try {
            // In a real implementation, we would implement phone verification
            // For now, we'll just return success
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send verification code")
            Resource.Error("Failed to send verification code: ${e.message}", e)
        }
    }

    override suspend fun verifyPhoneCode(phoneNumber: String, code: String): Resource<User> {
        return try {
            // In a real implementation, we would verify the code with Firebase
            // For now, we'll create a temporary user
            val user = userRepository.getUserByPhone(phoneNumber)
            
            if (user != null) {
                // Existing user - update last login
                val updatedUser = user.copy(updatedAt = Date())
                userRepository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                Resource.Success(updatedUser)
            } else {
                // New user - create with default values
                val newUser = User(
                    id = firebaseAuth.currentUser?.uid ?: phoneNumber,
                    phone = phoneNumber,
                    userType = UserType.GENERAL,
                    verificationStatus = VerificationStatus.VERIFIED,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                userRepository.insertUser(newUser)
                _currentUser.value = newUser
                Resource.Success(newUser)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to verify phone code")
            Resource.Error("Failed to verify phone code: ${e.message}", e)
        }
    }

    override suspend fun registerUser(user: User): Resource<User> {
        return try {
            userRepository.insertUser(user)
            _currentUser.value = user
            Resource.Success(user)
        } catch (e: Exception) {
            Timber.e(e, "Failed to register user")
            Resource.Error("Failed to register user: ${e.message}", e)
        }
    }

    override suspend fun login(phoneNumber: String): Resource<User> {
        return try {
            val user = userRepository.getUserByPhone(phoneNumber)
            if (user != null) {
                // Update last login time
                val updatedUser = user.copy(updatedAt = Date())
                userRepository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                Resource.Success(updatedUser)
            } else {
                Resource.Error("User not found", Exception("User with phone number $phoneNumber not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to login user")
            Resource.Error("Failed to login: ${e.message}", e)
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            _currentUser.value = null
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to logout")
            Resource.Error("Failed to logout: ${e.message}", e)
        }
    }

    override suspend fun upgradeUserRole(userId: String, newUserType: UserType): Resource<User> {
        return try {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                // Reset verification status when upgrading role
                val updatedUser = user.copy(
                    userType = newUserType,
                    verificationStatus = VerificationStatus.PENDING,
                    updatedAt = Date()
                )
                userRepository.updateUser(updatedUser)
                if (userId == _currentUser.value?.id) {
                    _currentUser.value = updatedUser
                }
                Resource.Success(updatedUser)
            } else {
                Resource.Error("User not found", Exception("User with ID $userId not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to upgrade user role")
            Resource.Error("Failed to upgrade user role: ${e.message}", e)
        }
    }

    override suspend fun updateProfile(user: User): Resource<User> {
        return try {
            userRepository.updateUser(user)
            if (user.id == _currentUser.value?.id) {
                _currentUser.value = user
            }
            Resource.Success(user)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update profile")
            Resource.Error("Failed to update profile: ${e.message}", e)
        }
    }

    override suspend fun verifyFarmer(userId: String, location: String): Resource<User> {
        return try {
            val user = userRepository.getUserById(userId)
            if (user != null && user.userType == UserType.FARMER) {
                val updatedUser = user.copy(
                    location = location,
                    verificationStatus = VerificationStatus.VERIFIED,
                    updatedAt = Date()
                )
                userRepository.updateUser(updatedUser)
                if (userId == _currentUser.value?.id) {
                    _currentUser.value = updatedUser
                }
                Resource.Success(updatedUser)
            } else {
                Resource.Error("User not found or not a farmer", Exception("User verification failed"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to verify farmer")
            Resource.Error("Failed to verify farmer: ${e.message}", e)
        }
    }

    override suspend fun verifyEnthusiast(userId: String, kycData: Map<String, String>): Resource<User> {
        return try {
            val user = userRepository.getUserById(userId)
            if (user != null && user.userType == UserType.ENTHUSIAST) {
                // In a real implementation, we would process the KYC data
                // For now, we'll just mark as verified
                val updatedUser = user.copy(
                    kycStatus = KycStatus.VERIFIED,
                    verificationStatus = VerificationStatus.VERIFIED,
                    updatedAt = Date()
                )
                userRepository.updateUser(updatedUser)
                if (userId == _currentUser.value?.id) {
                    _currentUser.value = updatedUser
                }
                Resource.Success(updatedUser)
            } else {
                Resource.Error("User not found or not an enthusiast", Exception("User verification failed"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to verify enthusiast")
            Resource.Error("Failed to verify enthusiast: ${e.message}", e)
        }
    }
}