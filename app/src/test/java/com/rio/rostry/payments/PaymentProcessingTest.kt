package com.rio.rostry.payments

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.PaymentEntity
import com.rio.rostry.data.repository.PaymentRepositoryImpl
import com.rio.rostry.utils.Resource
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.data.database.entity.InvoiceEntity
import com.rio.rostry.data.database.entity.RefundEntity
import com.rio.rostry.utils.UpiUtils
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class PaymentProcessingTest {
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

    @After fun teardown() { db.close() }

    @Test
    fun upi_initiation_and_idempotency() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        val res1 = repo.initiateUpiPayment("order-1", "user-1", 199.0, "vpa@okbank", "Test User", "note")
        val res2 = repo.initiateUpiPayment("order-1", "user-1", 199.0, "vpa@okbank", "Test User", "note")
        assertTrue(res1 is Resource.Success && res2 is Resource.Success)
        val p1 = (res1 as Resource.Success).data!!
        val p2 = (res2 as Resource.Success).data!!
        assertEquals(p1.paymentId, p2.paymentId)
        assertEquals("UPI", p1.method)
        assertEquals("PENDING", p1.status)
        assertNotNull(p1.upiUri)
    }

    @Test
    fun cod_reservation_and_idempotency() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        val res1 = repo.codReservation("order-2", "user-2", 299.0)
        val res2 = repo.codReservation("order-2", "user-2", 299.0)
        assertTrue(res1 is Resource.Success && res2 is Resource.Success)
        val p = (res1 as Resource.Success).data!!
        assertEquals("COD", p.method)
        assertEquals("PENDING", p.status)
    }

    @Test
    fun card_wallet_initiation_and_idempotency() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        val res1 = repo.cardWalletDemo("order-3", "user-3", 399.0, idempotencyKey = "CARD-order-3-399.0")
        val res2 = repo.cardWalletDemo("order-3", "user-3", 399.0, idempotencyKey = "CARD-order-3-399.0")
        assertTrue(res1 is Resource.Success && res2 is Resource.Success)
        val p = (res1 as Resource.Success).data!!
        assertEquals("CARD", p.method)
        assertEquals("PENDING", p.status)
    }

    @Test
    fun mark_result_success_and_failure() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        val start = repo.cardWalletDemo("order-4", "user-4", 499.0, idempotencyKey = "CARD-order-4-499.0") as Resource.Success
        val ok = repo.markPaymentResult(start.data!!.idempotencyKey!!, success = true, providerRef = "prov-1")
        assertTrue(ok is Resource.Success)
        val updated: PaymentEntity = paymentDao.findByIdempotencyKey(start.data!!.idempotencyKey!!)!!
        assertEquals("SUCCESS", updated.status)
        val fail = repo.markPaymentResult("non-existent", success = false, providerRef = null)
        assertTrue(fail is Resource.Error)
    }

    @Test
    fun upi_uri_contains_expected_params_and_rejects_non_positive_amounts() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        val ok = repo.initiateUpiPayment("order-10", "user-10", 123.45, "vpa@okbank", "Test User", "note-1") as Resource.Success
        val entity = ok.data!!
        assertEquals("UPI", entity.method)
        assertTrue(entity.upiUri!!.contains("pa=vpa@okbank"))
        assertTrue(entity.upiUri!!.contains("pn=Test%20User"))
        assertTrue(entity.upiUri!!.contains("am=123.45"))
        assertTrue(entity.upiUri!!.contains("tn=note-1"))

        val zero = repo.initiateUpiPayment("order-11", "user-11", 0.0, "v@ok", "n", null)
        assertTrue(zero is Resource.Error)
        val neg = repo.initiateUpiPayment("order-12", "user-12", -1.0, "v@ok", "n", null)
        assertTrue(neg is Resource.Error)
    }

    @Test
    fun refunds_full_before_dispatch_and_partial_after_dispatch_with_accumulation() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        val now = System.currentTimeMillis()
        // Seed order/invoice
        val orderId = "o-100"
        val userId = "u-100"
        val pay = (repo.cardWalletDemo(orderId, userId, 1000.0, idempotencyKey = "CARD-$orderId-1000") as Resource.Success).data!!
        orderDao.insertOrder(
            OrderEntity(orderId = orderId, buyerId = userId, status = "CONFIRMED", createdAt = now, updatedAt = now)
        )
        invoiceDao.insertInvoice(
            InvoiceEntity(
                invoiceId = "inv-100",
                orderId = orderId,
                subtotal = 1000.0,
                gstPercent = 0.0,
                gstAmount = 0.0,
                total = 1000.0,
                createdAt = now
            )
        )
        // Full refund before OUT_FOR_DELIVERY
        val full = repo.refundPayment(pay.paymentId, reason = "cancel before ship")
        assertTrue(full is Resource.Success)
        val updated1 = paymentDao.findById(pay.paymentId)!!
        assertEquals("REFUNDED", updated1.status)
        val refundedSoFar1 = refundDao.totalRefundedForPayment(pay.paymentId)
        assertEquals(1000.0, refundedSoFar1, 0.01)

        // Create a new order after dispatch to test partial refunds with logistics deduction
        val order2 = "o-101"
        val pay2 = (repo.cardWalletDemo(order2, userId, 1000.0, idempotencyKey = "CARD-$order2-1000") as Resource.Success).data!!
        orderDao.insertOrder(OrderEntity(orderId = order2, buyerId = userId, status = "OUT_FOR_DELIVERY", createdAt = now, updatedAt = now))
        invoiceDao.insertInvoice(
            InvoiceEntity(
                invoiceId = "inv-101",
                orderId = order2,
                subtotal = 1000.0,
                gstPercent = 0.0,
                gstAmount = 0.0,
                total = 1000.0,
                createdAt = now
            )
        )
        val partial = repo.refundPayment(pay2.paymentId, reason = "reject after dispatch")
        assertTrue(partial is Resource.Success)
        val refundedSoFar2 = refundDao.totalRefundedForPayment(pay2.paymentId)
        assertEquals(950.0, refundedSoFar2, 0.01) // 1000 - logistics 50

        // Accumulating refunds should cap at invoice total and mark payment REFUNDED when fully refunded
        // Add another partial refund; since only 50 remains, it should refund the rest and mark REFUNDED
        val secondPartial = repo.refundPayment(pay2.paymentId, reason = "adjustment")
        assertTrue(secondPartial is Resource.Success)
        val updated2 = paymentDao.findById(pay2.paymentId)!!
        assertEquals("REFUNDED", updated2.status)
        val totalRefunded = refundDao.totalRefundedForPayment(pay2.paymentId)
        assertEquals(1000.0, totalRefunded, 0.01)

        // Already-refunded no-op
        val noop = repo.refundPayment(pay2.paymentId, reason = "duplicate")
        assertTrue(noop is Resource.Success)
    }

    @Test
    fun refund_errors_when_missing_entities() = runBlocking {
        val repo = PaymentRepositoryImpl(
            paymentDao,
            orderDao,
            invoiceDao,
            refundDao,
            com.rio.rostry.demo.DemoModeManager(ctx),
            com.rio.rostry.demo.MockPaymentSystem(paymentDao, invoiceDao)
        )
        // Missing payment
        val noPay = repo.refundPayment("missing", null)
        assertTrue(noPay is Resource.Error)
        // Seed payment but missing order
        val p = (repo.cardWalletDemo("ord-x", "usr-x", 100.0, idempotencyKey = "CARD-ord-x-100") as Resource.Success).data!!
        val noOrder = repo.refundPayment(p.paymentId, null)
        assertTrue(noOrder is Resource.Error)
        // Seed order but missing invoice
        val now = System.currentTimeMillis()
        orderDao.insertOrder(OrderEntity(orderId = "ord-x", buyerId = "usr-x", status = "PLACED", createdAt = now, updatedAt = now))
        val noInvoice = repo.refundPayment(p.paymentId, null)
        assertTrue(noInvoice is Resource.Error)
    }
}
