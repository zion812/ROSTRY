package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String,
    val productId: String,
    val buyerId: String,
    val farmerId: String,
    val quantity: Int,
    val totalPrice: Double,
    val status: String, // pending, confirmed, shipped, delivered, cancelled
    val deliveryAddress: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)