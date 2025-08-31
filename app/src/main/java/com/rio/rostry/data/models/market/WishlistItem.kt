package com.rio.rostry.data.models.market

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val listingId: String = ""
)
