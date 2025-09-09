package com.rio.rostry.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val price: Long,
    val currency: String = "INR",
    val stock: Int,
    val farmerId: String,
    val createdAt: Long = System.currentTimeMillis()
)
