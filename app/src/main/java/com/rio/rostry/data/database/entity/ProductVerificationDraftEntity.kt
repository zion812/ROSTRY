package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for product verification drafts.
 * Multiple verifiers can create drafts for the same product.
 */
@Entity(
    tableName = "product_verification_drafts",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["verifierId"]),
        Index(value = ["status"])
    ]
)
data class ProductVerificationDraftEntity(
    @PrimaryKey val draftId: String,
    val productId: String,
    val verifierId: String,
    val fieldsJson: String, // JSON serialized Map<String, Any>
    val status: String, // PENDING, MERGED, DISCARDED
    val createdAt: Long,
    val mergedAt: Long? = null,
    val mergedInto: String? = null // ID of final verification record
)
