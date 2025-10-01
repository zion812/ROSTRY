package com.rio.rostry.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.OrderEntity
import com.rio.rostry.marketplace.payment.PaymentGateway
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class AdvancedOrderServiceTest {
    private lateinit var db: AppDatabase
    private lateinit var service: AdvancedOrderService
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var paymentGateway: PaymentGateway

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        paymentRepository = Mockito.mock(PaymentRepository::class.java)
        paymentGateway = Mockito.mock(PaymentGateway::class.java)
        // Stub suspend initialize()
        runBlocking { `when`(paymentGateway.initialize()).thenReturn(Result.success(Unit)) }
        service = AdvancedOrderService(db.orderDao(), paymentRepository, paymentGateway)
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun order_stateTransitions_flow() = runBlocking {
        val order = OrderEntity(
            orderId = "o1",
            buyerId = "b1",
            sellerId = "s1",
            status = "NEW",
            totalAmount = 100.0,
            shippingAddress = "addr-1",
            createdAt = 0L,
            updatedAt = 0L,
            lastModifiedAt = 0L,
            dirty = false
        )

        // place -> CONFIRMED -> PROCESSING -> OUT_FOR_DELIVERY -> DELIVERED
        val place = service.placeOrder(order)
        assertTrue(place is Resource.Success)
        var stored = db.orderDao().findById("o1")
        assertEquals("PLACED", stored?.status)

        val confirm = service.confirmOrder("o1")
        assertTrue(confirm is Resource.Success)
        stored = db.orderDao().findById("o1")
        assertEquals("CONFIRMED", stored?.status)

        val processing = service.startPreparing("o1")
        assertTrue(processing is Resource.Success)
        stored = db.orderDao().findById("o1")
        assertEquals("PROCESSING", stored?.status)

        val shipped = service.shipOrder("o1")
        assertTrue(shipped is Resource.Success)
        stored = db.orderDao().findById("o1")
        assertEquals("OUT_FOR_DELIVERY", stored?.status)

        val delivered = service.deliverOrder("o1")
        assertTrue(delivered is Resource.Success)
        stored = db.orderDao().findById("o1")
        assertEquals("DELIVERED", stored?.status)
    }

    @Test
    fun cancel_fromVariousPreTerminalStates() = runBlocking {
        val order = OrderEntity(
            orderId = "o2",
            buyerId = "b1",
            sellerId = "s1",
            status = "NEW",
            totalAmount = 50.0,
            shippingAddress = "addr-2",
            createdAt = 0L,
            updatedAt = 0L,
            lastModifiedAt = 0L,
            dirty = false
        )
        service.placeOrder(order)
        service.confirmOrder("o2")
        val cancel = service.cancelOrder("o2")
        assertTrue(cancel is Resource.Success)
        val stored = db.orderDao().findById("o2")
        assertEquals("CANCELLED", stored?.status)
    }
}
