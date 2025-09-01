package com.rio.rostry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import com.rio.rostry.data.models.profile.KycStatus

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val location: String = "",
    @get:PropertyName("userType") // Use PropertyName to guide Firestore
    val userType: UserType = UserType.General,
    val language: String = "",
    val isVerified: Boolean = false,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    // Enhanced profile & trust fields
    val kycStatus: KycStatus = KycStatus.PENDING,
    val trustScore: Double = 0.0, // 0.0 - 5.0
    val farmCertification: Boolean = false,
    val languages: List<String> = emptyList(),
    val regionPrivacy: Boolean = true, // true: show region only, false: precise
    val verifiedTransfersCount: Int = 0,
    val profileBadges: List<String> = emptyList(),
    // Role-specific optional fields
    val farmName: String? = null,
    val farmRegistrationId: String? = null,
    val brandName: String? = null,
    val specialties: List<String> = emptyList(),
    val experienceYears: Int? = null
) {
    // Add a no-argument constructor for Firestore deserialization
    constructor() : this(
        uid = "",
        name = "",
        email = "",
        phone = null,
        location = "",
        userType = UserType.General,
        language = "",
        isVerified = false,
        profileImageUrl = null,
        bio = null,
        kycStatus = KycStatus.PENDING,
        trustScore = 0.0,
        farmCertification = false,
        languages = emptyList(),
        regionPrivacy = true,
        verifiedTransfersCount = 0,
        profileBadges = emptyList(),
        farmName = null,
        farmRegistrationId = null,
        brandName = null,
        specialties = emptyList(),
        experienceYears = null
    )
}
