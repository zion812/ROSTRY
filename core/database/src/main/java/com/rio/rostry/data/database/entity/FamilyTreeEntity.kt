package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "family_tree",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["parentProductId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["childProductId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["parentProductId"]),
        Index(value = ["childProductId"]),
        Index(value = ["assetId"]),
        Index(value = ["parentAssetId"]),
        Index(value = ["childAssetId"])
    ]
)
data class FamilyTreeEntity(
    @PrimaryKey val treeId: String,
    val productId: String?,
    val parentProductId: String?,
    val childProductId: String?,
    val assetId: String?,
    val parentAssetId: String?,
    val childAssetId: String?,
    val relationType: String, // e.g. PATERNAL, MATERNAL
    val confidence: Double = 1.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
