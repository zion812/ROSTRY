package com.rio.rostry.coins

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.CoinLedgerDao
import com.rio.rostry.data.repository.CoinRepositoryImpl
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CoinRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var ledgerDao: CoinLedgerDao

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        ledgerDao = db.coinLedgerDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun purchase_use_refund_adjusts_balance() = runBlocking {
        val repo = CoinRepositoryImpl(ledgerDao)
        val user = "uCoins"
        assertEquals(0, repo.getBalance(user))
        assertTrue(repo.purchaseCoins(user, 10, null) is Resource.Success) // +10
        assertEquals(10, repo.getBalance(user))
        assertTrue(repo.useCoins(user, 3, "ref1") is Resource.Success)    // -3 => 7
        assertEquals(7, repo.getBalance(user))
        assertTrue(repo.refundCoins(user, 2, "ref1") is Resource.Success) // +2 => 9
        assertEquals(9, repo.getBalance(user))
    }
}
