package com.rio.rostry.data.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["buyerId"],
            onDelete = ForeignKey.SET_NULL // Or CASCADE, depending on business logic if a user is deleted
        )
    ],
    indices = [Index(value = ["buyerId"])]
)
@Keep
data class OrderEntity(
    @PrimaryKey val orderId: String = "",
    val buyerId: String? = null,
    val sellerId: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "",
    val shippingAddress: String = "",
    val paymentMethod: String? = null,
    val paymentStatus: String = "pending",
    val orderDate: Long = 0L,
    val expectedDeliveryDate: Long? = null,
    val actualDeliveryDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val lastModifiedAt: Long = 0L,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val dirty: Boolean = false
)
