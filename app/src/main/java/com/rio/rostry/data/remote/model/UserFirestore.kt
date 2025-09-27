package com.rio.rostry.data.remote.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.data.database.entity.UserEntity

@IgnoreExtraProperties
data class UserFirestore(
    val userId: String? = null,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    val userType: String? = null,
    val verificationStatus: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val photoUrl: String? = null
) {
    // Explicit no-arg constructor for Firebase deserializer
    constructor(): this(null, null, null, null, null, null, null, null, null)
}

fun UserFirestore.toEntity(fallbackUserId: String? = null): UserEntity {
    val role = runCatching { UserType.valueOf((userType ?: "GENERAL").uppercase()) }
        .getOrElse { UserType.GENERAL }
    val vStatus = runCatching { VerificationStatus.valueOf((verificationStatus ?: "PENDING").uppercase()) }
        .getOrElse { VerificationStatus.PENDING }
    return UserEntity(
        userId = userId ?: fallbackUserId ?: "",
        fullName = fullName,
        phoneNumber = phoneNumber,
        email = email,
        userType = role,
        verificationStatus = vStatus,
        createdAt = createdAt ?: System.currentTimeMillis(),
        updatedAt = updatedAt ?: System.currentTimeMillis(),
        profilePictureUrl = photoUrl
    )
}
