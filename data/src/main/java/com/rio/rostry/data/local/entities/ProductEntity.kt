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
    // Location
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    // Media
    val images: List<String> = emptyList(),
    // Category
    val category: String? = null,
    // Age-specific
    val birthDate: Long? = null,
    val vaccinationRecords: List<String> = emptyList(),
    val weight: Double? = null,
    val height: Double? = null,
    val gender: String? = null,
    val color: String? = null,
    val breed: String? = null,
    // Traceability
    val familyTreeId: String? = null,
    val parentIds: List<String> = emptyList(),
    val breedingStatus: String? = null,
    val transferHistory: List<String> = emptyList(),
    // Seller verification
    val verifiedSeller: Boolean = false,
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
