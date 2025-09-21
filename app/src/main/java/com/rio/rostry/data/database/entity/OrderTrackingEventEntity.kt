package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_tracking_events",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DeliveryHubEntity::class,
            parentColumns = ["hubId"],
            childColumns = ["hubId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("orderId"), Index("hubId")]
)
data class OrderTrackingEventEntity(
    @PrimaryKey val eventId: String,
    val orderId: String,
    val status: String, // PLACED, CONFIRMED, PROCESSING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    val hubId: String? = null,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
