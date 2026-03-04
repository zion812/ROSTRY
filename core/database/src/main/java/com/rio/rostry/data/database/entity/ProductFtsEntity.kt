package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "products_fts")
@Fts4
data class ProductFtsEntity(
    val productId: String,
    val name: String,
    val description: String,
    val category: String,
    val breed: String?,
    val location: String,
    val condition: String?
)
