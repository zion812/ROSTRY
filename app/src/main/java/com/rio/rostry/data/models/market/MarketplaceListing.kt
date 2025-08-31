package com.rio.rostry.data.models.market

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint

@Entity(tableName = "marketplace_listings")
data class MarketplaceListing(
    @PrimaryKey val id: String = "",
    val fowlId: String = "",
    val sellerId: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val location: GeoPoint? = null,
    val breed: String = "",
    val purpose: String = "", // e.g., breeding, meat, eggs
    val imageUrl: String = "",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val isSold: Boolean = false
)
