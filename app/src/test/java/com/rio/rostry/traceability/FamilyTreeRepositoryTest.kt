package com.rio.rostry.traceability

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.rio.rostry.data.database.AppDatabase
import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.repository.FamilyTreeRepositoryImpl
import com.rio.rostry.domain.rbac.RbacGuard
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class FamilyTreeRepositoryTest {
    private lateinit var ctx: Context
    private lateinit var db: AppDatabase
    private lateinit var familyTreeDao: FamilyTreeDao
    private lateinit var productDao: ProductDao
    private lateinit var auditLogDao: AuditLogDao

    @Before
    fun setup() {
        ctx = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        familyTreeDao = db.familyTreeDao()
        productDao = db.productDao()
        auditLogDao = db.auditLogDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun upsert_allows_cross_owner_parent_links() = runBlocking {
        val rbacGuard = mockk<RbacGuard>(relaxed = true)
        coEvery { rbacGuard.canEditLineage() } returns true
        
        val currentUserProvider = object : CurrentUserProvider {
            override fun userIdOrNull(): String? = "user-B"
            override fun isAuthenticated(): Boolean = true
        }
        val repo = FamilyTreeRepositoryImpl(
            familyTreeDao,
            rbacGuard,
            auditLogDao,
            currentUserProvider,
            productDao,
            Gson()
        )

        // Insert products
        val parentProduct = ProductEntity(
            productId = "prod-A",
            sellerId = "user-A",
            name = "Parent Bird",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        val childProduct = ProductEntity(
            productId = "prod-B",
            sellerId = "user-B",
            name = "Child Bird",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        productDao.insertProduct(parentProduct)
        productDao.insertProduct(childProduct)

        // Create family tree node linking child to parent
        val node = FamilyTreeEntity(
            nodeId = "node-1",
            productId = "prod-B",
            parentProductId = "prod-A",
            childProductId = null,
            relationType = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isDeleted = false,
            deletedAt = null
        )

        // Call upsert
        repo.upsert(node)

        // Assert upsert succeeds (no exception thrown)
        // Assert FamilyTreeEntity is persisted
        val persistedNode = familyTreeDao.findById("node-1")
        assertNotNull(persistedNode)
        assertEquals("prod-B", persistedNode?.productId)
        assertEquals("prod-A", persistedNode?.parentProductId)

        // Assert audit log entry is created
        val auditLogs = auditLogDao.getAll().filter { it.type == "LINEAGE_CROSS_OWNER_LINK" }
        assertEquals(1, auditLogs.size)
        val log = auditLogs.first()
        assertEquals("LINEAGE_CROSS_OWNER_LINK", log.type)
        assertEquals("node-1", log.refId)
        assertEquals("UPSERT", log.action)
        assertEquals("user-B", log.actorUserId)
        // Optionally check detailsJson contains the expected map
    }
}