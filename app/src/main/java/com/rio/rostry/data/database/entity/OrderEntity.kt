package com.rio.rostry.data.database.entity

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
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val buyerId: String?, // Foreign key to UserEntity (buyer)
    val sellerId: String, // To know which seller this order is primarily for, if applicable or if direct from product.
                         // This might be more complex if an order spans multiple sellers.
                         // Consider if an order is always for one seller or if it aggregates items from many.
                         // For simplicity, let's assume an order is grouped by seller, or handled at a higher level.
    val totalAmount: Double,
    val status: String, // e.g., "pending_payment", "processing", "shipped", "delivered", "cancelled", "refunded"
    val shippingAddress: String,
    val paymentMethod: String? = null,
    val paymentStatus: String = "pending", // e.g., "pending", "paid", "failed", "refunded"
    val orderDate: Long = System.currentTimeMillis(),
    val expectedDeliveryDate: Long? = null,
    val actualDeliveryDate: Long? = null,
    val notes: String? = null, // e.g. delivery instructions
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
