package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus

@Keep
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = "",
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
    // KYC document upload fields
    val kycDocumentUrls: String? = null, // JSON string containing list of uploaded document URLs
    val kycImageUrls: String? = null, // JSON string containing list of uploaded image URLs
    val kycDocumentTypes: String? = null, // JSON string mapping document URLs to their types
    val kycUploadStatus: String? = null, // PENDING, UPLOADED, VERIFIED, REJECTED
    val kycUploadedAt: Long? = null,
    val kycVerifiedAt: Long? = null,
    val kycRejectionReason: String? = null,
    // Common audit
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
