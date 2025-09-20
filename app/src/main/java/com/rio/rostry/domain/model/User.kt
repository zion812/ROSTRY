package com.rio.rostry.domain.model

import java.util.Date

/**
 * User entity representing different types of users in the ROSTRY system
 * @param id Unique identifier for the user
 * @param phone Phone number (mandatory for all users)
 * @param email Email address (optional)
 * @param userType Type of user (GENERAL, FARMER, ENTHUSIAST)
 * @param verificationStatus Current verification status
 * @param name User's full name
 * @param address User's address
 * @param location User's location (required for farmers)
 * @param kycStatus KYC verification status (required for enthusiasts)
 * @param coins Number of coins in user's account
 * @param createdAt Account creation timestamp
 * @param updatedAt Last update timestamp
 */
data class User(
    val id: String,
    val phone: String,
    val email: String? = null,
    val userType: UserType = UserType.GENERAL,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val name: String? = null,
    val address: String? = null,
    val location: String? = null, // Required for farmers
    val kycStatus: KycStatus = KycStatus.NOT_SUBMITTED, // Required for enthusiasts
    val coins: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class UserType {
    GENERAL,
    FARMER,
    ENTHUSIAST
}

enum class VerificationStatus {
    PENDING,
    VERIFIED,
    REJECTED
}

enum class KycStatus {
    NOT_SUBMITTED,
    PENDING,
    VERIFIED,
    REJECTED
}