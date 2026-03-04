package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class InvoiceEntity(
    @PrimaryKey val invoiceId: String,
    val orderId: String,
    val subtotal: Double,
    val gstPercent: Double,
    val gstAmount: Double,
    val total: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "invoice_lines",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["invoiceId"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId")]
)
data class InvoiceLineEntity(
    @PrimaryKey val lineId: String,
    val invoiceId: String,
    val description: String,
    val qty: Double,
    val unitPrice: Double,
    val lineTotal: Double
)
