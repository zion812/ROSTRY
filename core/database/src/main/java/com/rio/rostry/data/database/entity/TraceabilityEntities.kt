package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeding_records",
    foreignKeys = [
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["parentId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["partnerId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["childId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("parentId"), Index("partnerId"), Index("childId")]
)
data class BreedingRecordEntity(
    @PrimaryKey val recordId: String,
    val parentId: String,
    val partnerId: String,
    val childId: String,
    val success: Boolean,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "traits")
data class TraitEntity(
    @PrimaryKey val traitId: String,
    val name: String,
    val description: String? = null
)

@Entity(
    tableName = "product_traits",
    primaryKeys = ["productId", "traitId"],
    foreignKeys = [
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TraitEntity::class, parentColumns = ["traitId"], childColumns = ["traitId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("traitId")]
)
data class ProductTraitCrossRef(
    val productId: String,
    val traitId: String
)

@Entity(
    tableName = "lifecycle_events",
    foreignKeys = [
        ForeignKey(entity = ProductEntity::class, parentColumns = ["productId"], childColumns = ["productId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("productId"), Index("week")]
)
data class LifecycleEventEntity(
    @PrimaryKey val eventId: String,
    val productId: String,
    val week: Int,
    val stage: String, // CHICK, GROWTH, ADULT, BREEDER
    val type: String, // VACCINATION, GROWTH_UPDATE, MILESTONE, ALERT
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
