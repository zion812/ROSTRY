package com.rio.rostry.domain.auth

import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication and user management operations
 */
interface AuthRepository {
    /**
     * Get the current authenticated user
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Send phone verification code
     * @param phoneNumber The phone number to verify
     * @return Resource indicating success or failure
     */
    suspend fun sendPhoneVerificationCode(phoneNumber: String): Resource<Unit>

    /**
     * Verify phone number with code
     * @param phoneNumber The phone number to verify
     * @param code The verification code received by the user
     * @return Resource containing the authenticated user or error
     */
    suspend fun verifyPhoneCode(phoneNumber: String, code: String): Resource<User>

    /**
     * Register a new user
     * @param user The user to register
     * @return Resource containing the registered user or error
     */
    suspend fun registerUser(user: User): Resource<User>

    /**
     * Log in an existing user
     * @param phoneNumber The user's phone number
     * @return Resource containing the authenticated user or error
     */
    suspend fun login(phoneNumber: String): Resource<User>

    /**
     * Log out the current user
     */
    suspend fun logout(): Resource<Unit>

    /**
     * Upgrade user role
     * @param userId The ID of the user to upgrade
     * @param newUserType The new user type
     * @return Resource containing the updated user or error
     */
    suspend fun upgradeUserRole(userId: String, newUserType: UserType): Resource<User>

    /**
     * Update user profile
     * @param user The updated user information
     * @return Resource containing the updated user or error
     */
    suspend fun updateProfile(user: User): Resource<User>

    /**
     * Verify farmer with location
     * @param userId The ID of the user to verify
     * @param location The location for verification
     * @return Resource containing the updated user or error
     */
    suspend fun verifyFarmer(userId: String, location: String): Resource<User>

    /**
     * Verify enthusiast with KYC
     * @param userId The ID of the user to verify
     * @param kycData The KYC data for verification
     * @return Resource containing the updated user or error
     */
    suspend fun verifyEnthusiast(userId: String, kycData: Map<String, String>): Resource<User>
}