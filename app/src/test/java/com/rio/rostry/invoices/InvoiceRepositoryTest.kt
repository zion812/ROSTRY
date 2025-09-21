package com.rio.rostry.invoices

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.InvoiceDao
import com.rio.rostry.data.database.entity.InvoiceLineEntity
import com.rio.rostry.data.repository.InvoiceRepositoryImpl
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InvoiceRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var invoiceDao: InvoiceDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        invoiceDao = db.invoiceDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun gst_5_percent_calculation() = runBlocking {
        val repo = InvoiceRepositoryImpl(invoiceDao)
        val items = listOf(
            InvoiceLineEntity(lineId = "l1", invoiceId = "", description = "A", qty = 2.0, unitPrice = 50.0, lineTotal = 100.0),
            InvoiceLineEntity(lineId = "l2", invoiceId = "", description = "B", qty = 1.0, unitPrice = 20.0, lineTotal = 20.0)
        )
        val res = repo.generateInvoice("oInv", items) // default 5%
        assertTrue(res is Resource.Success)
        val inv = (res as Resource.Success).data
        requireNotNull(inv)
        assertEquals(120.0, inv.subtotal, 0.001)
        assertEquals(6.0, inv.gstAmount, 0.001)
        assertEquals(126.0, inv.total, 0.001)
    }
}
