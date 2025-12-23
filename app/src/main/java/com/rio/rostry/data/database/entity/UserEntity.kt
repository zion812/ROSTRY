package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.firebase.firestore.Exclude
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import java.util.Date

@Keep
@Entity(tableName = "users")
@TypeConverters(UserEntity.DateLongConverter::class)
data class UserEntity(
    @PrimaryKey val userId: String = "",
    val phoneNumber: String? = null,
    val email: String? = null,
    val fullName: String? = null,
    val address: String? = null,
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val userType: String = UserType.GENERAL.name,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    // Farmer-specific (optional)
    val farmAddressLine1: String? = null,
    val farmAddressLine2: String? = null,
    val farmCity: String? = null,
    val farmState: String? = null,
    val farmPostalCode: String? = null,
    val farmCountry: String? = null,
    val farmLocationLat: Double? = null,
    val farmLocationLng: Double? = null,
    val locationVerified: Boolean? = null,
    // Enthusiast-specific (optional)
    val kycLevel: Int? = null,
    val chickenCount: Int? = null,
    val farmerType: String? = null, // BACKYARD, HOBBYIST
    val raisingSince: Long? = null,
    val favoriteBreed: String? = null,

    
    val kycVerifiedAt: Long? = null,
    val kycRejectionReason: String? = null,
    val verificationRejectionReason: String? = null,
    val latestVerificationId: String? = null,
    val latestVerificationRef: String? = null,
    val verificationSubmittedAt: Date? = null,
    // Common audit
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val customClaimsUpdatedAt: Date? = null
) {
    @get:Exclude
    val role: UserType
        get() = try {
            UserType.valueOf(userType)
        } catch (e: Exception) {
            UserType.GENERAL
        }

    class DateLongConverter {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? = date?.time
    }
}
