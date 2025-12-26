package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rio.rostry.domain.model.UpgradeType

@Entity(
    tableName = "verification_drafts",
    indices = [Index(value = ["userId"], unique = true)]
)
data class VerificationDraftEntity(
    @PrimaryKey val draftId: String,
    val userId: String,
    val upgradeType: UpgradeType?,
    val farmLocationJson: String?,
    val uploadedImagesJson: String?,
    val uploadedDocsJson: String?,
    val uploadedImageTypesJson: String?,
    val uploadedDocTypesJson: String?,
    val uploadProgressJson: String?,
    val lastSavedAt: Long,
    val createdAt: Long,
    val updatedAt: Long
)
