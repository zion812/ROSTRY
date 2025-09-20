package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a transfer of poultry between owners/farms
 */
@Entity(tableName = "transfer_records")
data class TransferRecord(
    @PrimaryKey
    val id: String,
    val poultryId: String,
    val fromOwnerId: String,
    val toOwnerId: String,
    val transferDate: Date,
    val transferReason: String, // SALE, GIFT, BREEDING, etc.
    val notes: String? = null,
    val qrCode: String? = null, // QR code linking to this record
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)