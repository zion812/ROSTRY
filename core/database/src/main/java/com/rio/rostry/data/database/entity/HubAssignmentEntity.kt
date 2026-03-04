package com.rio.rostry.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for tracking hub assignments for products.
 * Supports location-based hub assignment for efficient logistics routing.
 */
@Entity(
    tableName = "hub_assignments",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["product_id"], unique = true),
        Index(value = ["hub_id"])
    ]
)
data class HubAssignmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "hub_id")
    val hubId: String,

    @ColumnInfo(name = "distance_km")
    val distanceKm: Double,

    @ColumnInfo(name = "assigned_at")
    val assignedAt: Long,

    @ColumnInfo(name = "seller_location_lat")
    val sellerLocationLat: Double,

    @ColumnInfo(name = "seller_location_lon")
    val sellerLocationLon: Double
)
