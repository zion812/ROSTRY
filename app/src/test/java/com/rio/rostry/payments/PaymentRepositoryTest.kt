package com.rio.rostry.payments

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.InvoiceLineEntity
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.repository.PaymentRepositoryImpl
import com.rio.rostry.utils.Resource
import com.rio.rostry.demo.DemoModeManager
import com.rio.rostry.demo.MockPaymentSystem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PaymentRepositoryTest {
    private lateinit var ctx: Context
    private lateinit var db: AppDatabase
    private lateinit var paymentDao: PaymentDao
    private lateinit var orderDao: OrderDao
    private lateinit var invoiceDao: InvoiceDao
    private lateinit var refundDao: RefundDao

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        paymentDao = db.paymentDao()
        orderDao = db.orderDao()
        invoiceDao = db.invoiceDao()
        refundDao = db.refundDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun idempotency_upi_initiate_returns_existing() = runBlocking {
        val demoMode = DemoModeManager(ctx)
        val mockPayments = MockPaymentSystem(paymentDao, invoiceDao)
        val repo = PaymentRepositoryImpl(paymentDao, orderDao, invoiceDao, refundDao, demoMode, mockPayments)
        val res1 = repo.initiateUpiPayment("o1", "u1", 100.0, "vpa@bank", "Name", "note")
        val res2 = repo.initiateUpiPayment("o1", "u1", 100.0, "vpa@bank", "Name", "note")
        assertTrue(res1 is Resource.Success && res2 is Resource.Success)
        val p1 = (res1 as Resource.Success).data
        val p2 = (res2 as Resource.Success).data
        requireNotNull(p1)
        requireNotNull(p2)
        assertEquals(p1.paymentId, p2.paymentId)
    }

    @Test
    fun refund_full_before_out_for_delivery() = runBlocking {
        val demoMode = DemoModeManager(ctx)
        val mockPayments = MockPaymentSystem(paymentDao, invoiceDao)
        val repo = PaymentRepositoryImpl(paymentDao, orderDao, invoiceDao, refundDao, demoMode, mockPayments)
        // Create order in PROCESSING (before OUT_FOR_DELIVERY)
        val order = OrderEntity(
            orderId = "o2", buyerId = "b1", sellerId = "s1", totalAmount = 100.0, status = "PROCESSING",
            shippingAddress = "addr"
        )
        orderDao.insertOrder(order)
        // Invoice total 105 (gst 5% of 100)
        val inv = InvoiceEntity(invoiceId = "inv2", orderId = "o2", subtotal = 100.0, gstPercent = 5.0, gstAmount = 5.0, total = 105.0)
        invoiceDao.insertInvoice(inv)
        // Payment success
        val p = PaymentEntity(paymentId = "pay2", orderId = "o2", userId = "b1", method = "CARD", amount = 105.0, status = "SUCCESS", idempotencyKey = "IDK2")
        paymentDao.insert(p)
        val result = repo.refundPayment("pay2", "test full")
        assertTrue(result is Resource.Success)
        val totalRefunded = refundDao.totalRefundedForPayment("pay2")
        assertEquals(105.0, totalRefunded, 0.01)
    }

    @Test
    fun refund_partial_on_or_after_out_for_delivery() = runBlocking {
        val demoMode = DemoModeManager(ctx)
        val mockPayments = MockPaymentSystem(paymentDao, invoiceDao)
        val repo = PaymentRepositoryImpl(paymentDao, orderDao, invoiceDao, refundDao, demoMode, mockPayments)
        // Create order OUT_FOR_DELIVERY
        val order = OrderEntity(
            orderId = "o3", buyerId = "b1", sellerId = "s1", totalAmount = 100.0, status = "OUT_FOR_DELIVERY",
            shippingAddress = "addr"
        )
        orderDao.insertOrder(order)
        // Invoice total 105
        val inv = InvoiceEntity(invoiceId = "inv3", orderId = "o3", subtotal = 100.0, gstPercent = 5.0, gstAmount = 5.0, total = 105.0)
        invoiceDao.insertInvoice(inv)
        // Payment success
        val p = PaymentEntity(paymentId = "pay3", orderId = "o3", userId = "b1", method = "CARD", amount = 105.0, status = "SUCCESS", idempotencyKey = "IDK3")
        paymentDao.insert(p)
        val result = repo.refundPayment("pay3", "test partial")
        assertTrue(result is Resource.Success)
        val totalRefunded = refundDao.totalRefundedForPayment("pay3")
        // Should be 105 - 50 logistics = 55
        assertEquals(55.0, totalRefunded, 0.01)
    }
}
