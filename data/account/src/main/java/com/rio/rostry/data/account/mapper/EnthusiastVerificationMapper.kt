package com.rio.rostry.data.account.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.core.model.EnthusiastVerification
import com.rio.rostry.core.model.VerificationStatus
import com.rio.rostry.data.database.entity.EnthusiastVerificationEntity

private val gson = Gson()

/**
 * Converts EnthusiastVerificationEntity to domain model.
 */
fun EnthusiastVerificationEntity.toEnthusiastVerification(): EnthusiastVerification {
    val specializationsType = object : TypeToken<List<String>>() {}.type
    val referenceContactsType = object : TypeToken<List<String>>() {}.type
    val documentUrlsType = object : TypeToken<List<String>>() {}.type
    val farmPhotosType = object : TypeToken<List<String>>() {}.type

    return EnthusiastVerification(
        verificationId = verificationId,
        userId = userId,
        experienceYears = experienceYears ?: 0,
        birdCount = birdCount ?: 0,
        specializations = gson.fromJson(specializations ?: "[]", specializationsType) ?: emptyList(),
        achievementsDescription = achievementsDescription.orEmpty(),
        referenceContacts = gson.fromJson(referenceContacts ?: "[]", referenceContactsType) ?: emptyList(),
        verificationDocumentUrls = gson.fromJson(verificationDocumentUrls, documentUrlsType) ?: emptyList(),
        profilePhotoUrl = profilePhotoUrl,
        farmPhotoUrls = gson.fromJson(farmPhotoUrls ?: "[]", farmPhotosType) ?: emptyList(),
        status = runCatching { VerificationStatus.valueOf(status.name) }.getOrDefault(VerificationStatus.PENDING),
        submittedAt = submittedAt ?: createdAt,
        reviewedAt = reviewedAt,
        reviewedBy = reviewedBy,
        rejectionReason = rejectionReason,
        adminNotes = adminNotes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Converts domain model to EnthusiastVerificationEntity.
 */
fun EnthusiastVerification.toEntity(): EnthusiastVerificationEntity {
    return EnthusiastVerificationEntity(
        verificationId = verificationId,
        userId = userId,
        experienceYears = experienceYears,
        birdCount = birdCount,
        specializations = gson.toJson(specializations),
        achievementsDescription = achievementsDescription,
        referenceContacts = gson.toJson(referenceContacts),
        verificationDocumentUrls = gson.toJson(verificationDocumentUrls),
        profilePhotoUrl = profilePhotoUrl,
        farmPhotoUrls = gson.toJson(farmPhotoUrls),
        status = com.rio.rostry.domain.model.VerificationStatus.valueOf(status.name),
        submittedAt = submittedAt,
        reviewedAt = reviewedAt,
        reviewedBy = reviewedBy,
        rejectionReason = rejectionReason,
        adminNotes = adminNotes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
