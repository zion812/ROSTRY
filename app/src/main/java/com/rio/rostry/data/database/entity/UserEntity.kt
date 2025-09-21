package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String, // Firebase UID or custom ID
    val phoneNumber: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val address: String? = null,
    val profilePictureUrl: String? = null,
    val userType: UserType = UserType.GENERAL,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    // Farmer-specific (optional)
    val farmLocationLat: Double? = null,
    val farmLocationLng: Double? = null,
    val locationVerified: Boolean? = null,
    // Enthusiast-specific (optional)
    val kycLevel: Int? = null,
    // Common audit
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
