package com.rio.rostry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "fowl")
data class Fowl(
    @PrimaryKey val id: String,
    val userId: String = "",
    val name: String,
    val group: String? = null,
    val sireId: String? = null, // Father
    val damId: String? = null,  // Mother
    val imageUrl: String? = null,
    val qrCodeUrl: String? = null,
    val birthDate: Date,
    val status: String // e.g., Chick, Pullet, Cockerel, Hen, Rooster
)
