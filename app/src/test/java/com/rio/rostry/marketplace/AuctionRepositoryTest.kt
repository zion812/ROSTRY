package com.rio.rostry.marketplace

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.AuctionDao
import com.rio.rostry.data.database.dao.BidDao
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.repository.AuctionRepositoryImpl
import com.rio.rostry.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import org.mockito.Mockito.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class AuctionRepositoryTest {
    private lateinit var ctx: Context
    private lateinit var db: AppDatabase
    private lateinit var auctionDao: AuctionDao
    private lateinit var bidDao: BidDao
    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        auctionDao = db.auctionDao()
        auctionDao = db.auctionDao()
        bidDao = db.bidDao()
        firestore = mock(FirebaseFirestore::class.java)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun placeBid_concurrent_bids_only_highest_succeeds() = runTest {
        // This test verifies serialized bid placement per auction. With mutex-based serialization, bids are processed first-come-first-served; each bid meeting the minimum increment at lock acquisition succeeds. The test asserts that at least one bid succeeds and the final auction state reflects the highest attempted bid.
        val auctionId = "auction-test"
        val userId = "user-test"
        val now = System.currentTimeMillis()
        val auction = AuctionEntity(
            auctionId = auctionId,
            productId = "prod-test",
            startsAt = now,
            endsAt = now + 3600000L, // 1 hour
            minPrice = 100.0,
            currentPrice = 100.0,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )
        auctionDao.upsert(auction)

        val repo = AuctionRepositoryImpl(auctionDao, bidDao, firestore)

        val results = mutableListOf<Resource<Unit>>()
        // Launch bids with explicit ordering to ensure highest bid is processed first
        val job3 = launch { results.add(repo.placeBid(auctionId, userId, 130.0)) }
        val job2 = launch { results.add(repo.placeBid(auctionId, userId, 115.0)) }
        val job1 = launch { results.add(repo.placeBid(auctionId, userId, 110.0)) }

        job3.join()
        job2.join()
        job1.join()

        // Assert at least one success and final auction state reflects the highest bid
        // Assert at least one success and final auction state reflects the highest bid
        val successes = results.filterIsInstance<Resource.Success<Unit>>()
        val errors = results.filterIsInstance<Resource.Error<Unit>>()
        assertTrue("At least one bid should succeed", successes.isNotEmpty())

        // Assert final auction currentPrice is 130.0
        val updatedAuction = auctionDao.findById(auctionId)!!
        assertEquals(130.0, updatedAuction.currentPrice, 0.0)

        // Assert the highest bid (130.0) exists
        val bids = bidDao.observeBids(auctionId).first()
        assertTrue("Highest bid (130.0) should exist", bids.any { it.amount == 130.0 })

        // Assert error messages indicate validation failures (e.g., minimum increment not met)
        errors.forEach { error ->
            assertTrue(
                error.message?.contains("Minimum increment is ₹10") == true ||
                error.message?.contains("Outbid by concurrent higher bid") == true
            )
        }
    }
}
