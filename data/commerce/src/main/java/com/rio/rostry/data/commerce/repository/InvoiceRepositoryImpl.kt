package com.rio.rostry.data.commerce.repository

import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.InvoiceLineEntity
import com.rio.rostry.domain.commerce.repository.InvoiceRepository
import com.rio.rostry.core.common.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {

    override suspend fun generateInvoice(orderId: String, items: List<InvoiceLineEntity>, gstPercent: Double): Result<InvoiceEntity> = try {
        require(items.isNotEmpty()) { "Invoice items required" }
        val subtotal = items.sumOf { it.lineTotal }
        val gstAmount = (subtotal * gstPercent) / 100.0
        val total = subtotal + gstAmount
        val invoiceId = java.util.UUID.randomUUID().toString()
        val entity = InvoiceEntity(
            invoiceId = invoiceId,
            orderId = orderId,
            subtotal = subtotal,
            gstPercent = gstPercent,
            gstAmount = gstAmount,
            total = total,
            createdAt = System.currentTimeMillis()
        )
        val normalized = items.map { it.copy(invoiceId = invoiceId) }
        invoiceDao.insertWithLines(entity, normalized)
        Result.Success(entity)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getInvoiceByOrder(orderId: String): Result<Pair<InvoiceEntity, List<InvoiceLineEntity>>> = try {
        val invoice = invoiceDao.findByOrder(orderId) ?: return Result.Error(Exception("Invoice not found"))
        val lines = invoiceDao.linesForInvoice(invoice.invoiceId)
        Result.Success(invoice to lines)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getAllInvoicesAdmin(): Result<List<InvoiceEntity>> = try {
        Result.Success(invoiceDao.getAllInvoices())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
