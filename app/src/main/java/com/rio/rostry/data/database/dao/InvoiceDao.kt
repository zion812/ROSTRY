package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.InvoiceLineEntity

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInvoice(entity: InvoiceEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLines(lines: List<InvoiceLineEntity>)

    @Transaction
    suspend fun insertWithLines(entity: InvoiceEntity, lines: List<InvoiceLineEntity>) {
        insertInvoice(entity)
        insertLines(lines)
    }

    @Query("SELECT * FROM invoices WHERE orderId = :orderId LIMIT 1")
    suspend fun findByOrder(orderId: String): InvoiceEntity?

    @Query("SELECT * FROM invoice_lines WHERE invoiceId = :invoiceId")
    suspend fun linesForInvoice(invoiceId: String): List<InvoiceLineEntity>
}
