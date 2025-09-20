package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["farmerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["farmerId"]),
        Index(value = ["category"]),
        Index(value = ["name"])
    ]
)
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val unit: String, // kg, bunch, crate, etc.
    val farmerId: String,
    val imageUrl: String? = null,
    val category: String,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isDeleted: Boolean = false, // Soft delete flag
    val deletedAt: Date? = null // Soft delete timestamp
)