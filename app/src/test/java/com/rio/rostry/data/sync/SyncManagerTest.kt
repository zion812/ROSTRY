package com.rio.rostry.data.sync

import androidx.room.Room
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.SyncStateEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.utils.network.ConnectivityManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.stubbing.Answer

class SyncManagerTest {
    private lateinit var db: AppDatabase
    private lateinit var firestore: FirestoreService
    private lateinit var connectivity: ConnectivityManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        firestore = Mockito.mock(FirestoreService::class.java)
        connectivity = Mockito.mock(ConnectivityManager::class.java)
        `when`(connectivity.isOnline()).thenReturn(true)
        `when`(connectivity.isUnmetered()).thenReturn(true)

        // Return empty remote deltas
        runBlocking {
            `when`(firestore.fetchUpdatedProducts(Mockito.anyLong(), Mockito.anyInt())).thenReturn(emptyList())
            `when`(firestore.fetchUpdatedOrders(Mockito.anyLong(), Mockito.anyInt())).thenReturn(emptyList())
            `when`(firestore.fetchUpdatedTransfers(Mockito.anyLong(), Mockito.anyInt())).thenReturn(emptyList())
            `when`(firestore.fetchUpdatedTrackings(Mockito.anyLong(), Mockito.anyInt())).thenReturn(emptyList())
            `when`(firestore.fetchUpdatedChats(Mockito.anyLong(), Mockito.anyInt())).thenReturn(emptyList())

            // Echo sizes for pushes
            `when`(firestore.pushProducts(Mockito.anyList<ProductEntity>())).thenAnswer(Answer { (it.arguments[0] as List<*>).size })
            `when`(firestore.pushOrders(Mockito.anyList())).thenAnswer(Answer { (it.arguments[0] as List<*>).size })
            `when`(firestore.pushTransfers(Mockito.anyList())).thenAnswer(Answer { (it.arguments[0] as List<*>).size })
            `when`(firestore.pushTrackings(Mockito.anyList())).thenAnswer(Answer { (it.arguments[0] as List<*>).size })
            `when`(firestore.pushChats(Mockito.anyList())).thenAnswer(Answer { (it.arguments[0] as List<*>).size })
        }
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun syncAll_clearsDirtyProducts_and_updatesWatermarks() = runBlocking {
        val productDao = db.productDao()
        val stateDao = db.syncStateDao()

        // Insert a dirty product
        val p = ProductEntity(
            productId = "p1",
            sellerId = "s1",
            name = "name",
            description = "desc",
            category = "cat",
            price = 1.0,
            quantity = 1.0,
            unit = "kg",
            location = "loc",
            dirty = true,
            updatedAt = 1L,
            lastModifiedAt = 1L
        )
        productDao.insertProduct(p)
        stateDao.upsert(SyncStateEntity())

        val sync = SyncManager(
            userDao = db.userDao(),
            productDao = productDao,
            orderDao = db.orderDao(),
            productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(),
            transferDao = db.transferDao(),
            syncStateDao = stateDao,
            outboxDao = db.outboxDao(),
            firestoreService = firestore,
            connectivityManager = connectivity,
            gson = Gson(),
            breedingPairDao = Mockito.mock(com.rio.rostry.data.database.dao.BreedingPairDao::class.java),
            farmAlertDao = Mockito.mock(com.rio.rostry.data.database.dao.FarmAlertDao::class.java),
            farmerDashboardSnapshotDao = Mockito.mock(com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao::class.java),
            vaccinationRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.VaccinationRecordDao::class.java),
            growthRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.GrowthRecordDao::class.java),
            quarantineRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.QuarantineRecordDao::class.java),
            mortalityRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.MortalityRecordDao::class.java),
            hatchingBatchDao = Mockito.mock(com.rio.rostry.data.database.dao.HatchingBatchDao::class.java),
            hatchingLogDao = Mockito.mock(com.rio.rostry.data.database.dao.HatchingLogDao::class.java),
            firebaseAuth = Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        )

        val res = sync.syncAll()
        // Ensure product dirties are cleared
        val updated = productDao.findById("p1")
        assertEquals(false, updated?.dirty)

        // Ensure watermarks are bumped
        val state = stateDao.get()
        // lastProductSyncAt should be > 0
        assertEquals(true, (state?.lastProductSyncAt ?: 0L) > 0L)
    }

    @Test
    fun transfers_remoteTerminalOverrides_localPending() = runBlocking {
        val transferDao = db.transferDao()
        val stateDao = db.syncStateDao()
        stateDao.upsert(SyncStateEntity())

        // Local pending transfer marked dirty
        val local = TransferEntity(
            transferId = "t1",
            productId = "p1",
            fromUserId = "u1",
            toUserId = "u2",
            amount = 100.0,
            type = "PAYMENT",
            status = "PENDING",
            dirty = true,
            updatedAt = 1L,
            lastModifiedAt = 1L
        )
        transferDao.upsert(local)

        // Remote says COMPLETED (terminal)
        val remote = local.copy(status = "COMPLETED", dirty = false, updatedAt = 5L, lastModifiedAt = 5L)
        `when`(firestore.fetchUpdatedTransfers(Mockito.anyLong(), Mockito.anyInt())).thenReturn(listOf(remote))

        val sync = SyncManager(
            userDao = db.userDao(),
            productDao = db.productDao(),
            orderDao = db.orderDao(),
            productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(),
            transferDao = transferDao,
            syncStateDao = stateDao,
            outboxDao = db.outboxDao(),
            firestoreService = firestore,
            connectivityManager = connectivity,
            gson = Gson(),
            breedingPairDao = Mockito.mock(com.rio.rostry.data.database.dao.BreedingPairDao::class.java),
            farmAlertDao = Mockito.mock(com.rio.rostry.data.database.dao.FarmAlertDao::class.java),
            farmerDashboardSnapshotDao = Mockito.mock(com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao::class.java),
            vaccinationRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.VaccinationRecordDao::class.java),
            growthRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.GrowthRecordDao::class.java),
            quarantineRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.QuarantineRecordDao::class.java),
            mortalityRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.MortalityRecordDao::class.java),
            hatchingBatchDao = Mockito.mock(com.rio.rostry.data.database.dao.HatchingBatchDao::class.java),
            hatchingLogDao = Mockito.mock(com.rio.rostry.data.database.dao.HatchingLogDao::class.java),
            firebaseAuth = Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        )

        sync.syncAll()

        val merged = transferDao.getById("t1")
        // Expect terminal COMPLETED to be preserved after cleaning
        assertEquals("COMPLETED", merged?.status)
        assertEquals(false, merged?.dirty)
    }

    @Test
    fun products_lineageProtected_onPushAndLocalClean() = runBlocking {
        val productDao = db.productDao()
        val stateDao = db.syncStateDao()
        stateDao.upsert(SyncStateEntity())

        // Local modifies non-lineage fields and marks dirty, but has lineage set
        val local = ProductEntity(
            productId = "p2",
            sellerId = "s1",
            name = "old",
            description = "d",
            category = "c",
            price = 1.0,
            quantity = 1.0,
            unit = "kg",
            location = "loc",
            familyTreeId = "FAM-1",
            parentIdsJson = "[\"p0\"]",
            transferHistoryJson = "[\"t0\"]",
            dirty = true,
            updatedAt = 1L,
            lastModifiedAt = 1L
        )
        productDao.insertProduct(local)

        // Remote has lineage present (same or newer) which must be preserved
        val remote = local.copy(
            name = "remoteName",
            familyTreeId = "FAM-1-REMOTE",
            parentIdsJson = "[\"pX\"]",
            transferHistoryJson = "[\"tX\"]",
            dirty = false,
            updatedAt = 5L,
            lastModifiedAt = 5L
        )
        `when`(firestore.fetchUpdatedProducts(Mockito.anyLong(), Mockito.anyInt())).thenReturn(listOf(remote))

        val sync = SyncManager(
            userDao = db.userDao(),
            productDao = productDao,
            orderDao = db.orderDao(),
            productTrackingDao = db.productTrackingDao(),
            chatMessageDao = db.chatMessageDao(),
            transferDao = db.transferDao(),
            syncStateDao = stateDao,
            outboxDao = db.outboxDao(),
            firestoreService = firestore,
            connectivityManager = connectivity,
            gson = Gson(),
            breedingPairDao = Mockito.mock(com.rio.rostry.data.database.dao.BreedingPairDao::class.java),
            farmAlertDao = Mockito.mock(com.rio.rostry.data.database.dao.FarmAlertDao::class.java),
            farmerDashboardSnapshotDao = Mockito.mock(com.rio.rostry.data.database.dao.FarmerDashboardSnapshotDao::class.java),
            vaccinationRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.VaccinationRecordDao::class.java),
            growthRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.GrowthRecordDao::class.java),
            quarantineRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.QuarantineRecordDao::class.java),
            mortalityRecordDao = Mockito.mock(com.rio.rostry.data.database.dao.MortalityRecordDao::class.java),
            hatchingBatchDao = Mockito.mock(com.rio.rostry.data.database.dao.HatchingBatchDao::class.java),
            hatchingLogDao = Mockito.mock(com.rio.rostry.data.database.dao.HatchingLogDao::class.java),
            firebaseAuth = Mockito.mock(com.google.firebase.auth.FirebaseAuth::class.java)
        )

        sync.syncAll()

        val cleaned = productDao.findById("p2")
        // Expect lineage fields to reflect remote (protected), not overwritten by local dirty clean
        assertEquals("FAM-1-REMOTE", cleaned?.familyTreeId)
        assertEquals("[\"pX\"]", cleaned?.parentIdsJson)
        assertEquals("[\"tX\"]", cleaned?.transferHistoryJson)
        assertEquals(false, cleaned?.dirty)
    }
}
