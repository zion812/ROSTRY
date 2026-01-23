package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.InvoiceLineEntity
import com.rio.rostry.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

interface InvoiceRepository {
    suspend fun generateInvoice(orderId: String, items: List<InvoiceLineEntity>, gstPercent: Double = 5.0): Resource<InvoiceEntity>
    suspend fun getInvoiceByOrder(orderId: String): Resource<Pair<InvoiceEntity, List<InvoiceLineEntity>>>
    suspend fun getAllInvoicesAdmin(): Resource<List<InvoiceEntity>>
}

@Singleton
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {

    override suspend fun generateInvoice(orderId: String, items: List<InvoiceLineEntity>, gstPercent: Double): Resource<InvoiceEntity> = try {
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
        // Rebind lines with the generated invoiceId if needed
        val normalized = items.map { it.copy(invoiceId = invoiceId) }
        invoiceDao.insertWithLines(entity, normalized)
        Resource.Success(entity)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to generate invoice")
    }

    override suspend fun getInvoiceByOrder(orderId: String): Resource<Pair<InvoiceEntity, List<InvoiceLineEntity>>> = try {
        val invoice = invoiceDao.findByOrder(orderId) ?: return Resource.Error("Invoice not found")
        val lines = invoiceDao.linesForInvoice(invoice.invoiceId)
        Resource.Success(invoice to lines)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch invoice")
    }

    override suspend fun getAllInvoicesAdmin(): Resource<List<InvoiceEntity>> = try {
        Resource.Success(invoiceDao.getAllInvoices())
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to fetch invoices")
    }
}
