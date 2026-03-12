package com.rio.rostry.domain.commerce.repository

import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.InvoiceLineEntity
import com.rio.rostry.core.common.Result

/**
 * Domain interface for invoice operations.
 * Migrated from app module as part of Phase 1 repository migration.
 */
interface InvoiceRepository {
    suspend fun generateInvoice(orderId: String, items: List<InvoiceLineEntity>, gstPercent: Double = 5.0): Result<InvoiceEntity>
    suspend fun getInvoiceByOrder(orderId: String): Result<Pair<InvoiceEntity, List<InvoiceLineEntity>>>
    suspend fun getAllInvoicesAdmin(): Result<List<InvoiceEntity>>
}
