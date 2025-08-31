package com.rio.rostry.data.models.market

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val listingId: String = "",
    val quantity: Int = 1
)
