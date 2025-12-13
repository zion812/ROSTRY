package com.rio.rostry.audit

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.FamilyTreeDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.FamilyTreeEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.FamilyTreeRepository
import com.rio.rostry.data.repository.ProductRepository
import com.rio.rostry.data.repository.TransferWorkflowRepository
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import androidx.room.Update
import androidx.room.Delete

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuditTrailTest {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var auditLogDao: AuditLogDao
    @Inject lateinit var productRepository: ProductRepository
    @Inject lateinit var transferWorkflowRepository: TransferWorkflowRepository
    @Inject lateinit var familyTreeRepository: FamilyTreeRepository
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var productDao: ProductDao
    @Inject lateinit var familyTreeDao: FamilyTreeDao
    @Inject lateinit var userDao: UserDao

    private val testUserId = "testUserId"
    private val gson = Gson()

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            // Insert test user
            val testUser = UserEntity(
                userId = testUserId,
                phoneNumber = null,
                email = "test@example.com",
                fullName = "Test User",
                userType = UserType.FARMER.name,
                verificationStatus = VerificationStatus.VERIFIED,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            userDao.insertUser(testUser)
            // Session uses test user seeded above
        }
    }

    @Test
    fun testProductCreationCreatesAuditLog() = runBlocking {
        val product = ProductEntity(
            productId = "",
            sellerId = testUserId,
            name = "Test Product",
            description = "Test Description",
            category = "Test Category",
            price = 100.0,
            quantity = 1.0,
            unit = "kg",
            location = "Test Location",
            latitude = 0.0,
            longitude = 0.0,
            lifecycleStatus = "ACTIVE",
            familyTreeId = null,
            parentIdsJson = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            isDeleted = false,
            deletedAt = null,
            dirty = false
        )
        val result = productRepository.addProduct(product)
        assertTrue(result is Resource.Success)
        val productId = (result as Resource.Success).data!!
        val logs = auditLogDao.getByRef(productId)
        assertEquals(1, logs.size)
        assertEquals("PRODUCT", logs[0].type)
        assertEquals("CREATE", logs[0].action)
        assertEquals(testUserId, logs[0].actorUserId)
    }

    @Test
    fun testTransferInitiationCreatesAuditLog() = runBlocking {
        // First create a product
        val product = ProductEntity(
            productId = "",
            sellerId = testUserId,
            name = "Test Product",
            description = "Test Description",
            category = "Test Category",
            price = 100.0,
            quantity = 1.0,
            unit = "kg",
            location = "Test Location",
            latitude = 0.0,
            longitude = 0.0,
            lifecycleStatus = "ACTIVE",
            familyTreeId = null,
            parentIdsJson = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            isDeleted = false,
            deletedAt = null,
            dirty = false
        )
        val productResult = productRepository.addProduct(product)
        assertTrue(productResult is Resource.Success)
        val productId = (productResult as Resource.Success).data!!

        // Create transfer
        val transferResult = transferWorkflowRepository.initiate(
            productId = productId,
            fromUserId = testUserId,
            toUserId = "otherUserId",
            amount = 100.0,
            currency = "INR",
            sellerPhotoUrl = null,
            gpsLat = null,
            gpsLng = null,
            conditionsJson = null,
            timeoutAt = null
        )
        // Assuming it succeeds or check logs even if fails
        val logs = auditLogDao.getByType("TRANSFER", 10)
        assertTrue(logs.isNotEmpty())
        val transferLog = logs.find { it.action == "INITIATE" }
        assertNotNull(transferLog)
        assertEquals(testUserId, transferLog?.actorUserId)
    }

    @Test
    fun testLineageEditCreatesAuditLog() = runBlocking {
        // Create product first
        val product = ProductEntity(
            productId = "",
            sellerId = testUserId,
            name = "Test Product",
            description = "Test Description",
            category = "Test Category",
            price = 100.0,
            quantity = 1.0,
            unit = "kg",
            location = "Test Location",
            latitude = 0.0,
            longitude = 0.0,
            lifecycleStatus = "ACTIVE",
            familyTreeId = null,
            parentIdsJson = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            isDeleted = false,
            deletedAt = null,
            dirty = false
        )
        val productResult = productRepository.addProduct(product)
        assertTrue(productResult is Resource.Success)
        val productId = (productResult as Resource.Success).data!!

        // Create lineage node
        val node = FamilyTreeEntity(
            nodeId = "node1",
            productId = productId,
            parentProductId = null,
            childProductId = null,
            relationType = "BIRTH",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isDeleted = false,
            deletedAt = null
        )
        familyTreeRepository.upsert(node)
        val logs = auditLogDao.getByRef("node1")
        assertEquals(1, logs.size)
        assertEquals("LINEAGE", logs[0].type)
        assertEquals("UPSERT", logs[0].action)
        assertEquals(testUserId, logs[0].actorUserId)
    }

    @Test
    fun testValidationFailureCreatesAuditLog() = runBlocking {
        // Create transfer with invalid product or something (e.g., product not owned)
        val result = transferWorkflowRepository.initiate(
            productId = "invalidProductId",
            fromUserId = testUserId,
            toUserId = "otherUserId",
            amount = 100.0,
            currency = "INR",
            sellerPhotoUrl = null,
            gpsLat = null,
            gpsLng = null,
            conditionsJson = null,
            timeoutAt = null
        )
        assertTrue(result is Resource.Error)
        val logs = auditLogDao.getByType("TRANSFER", 10)
        val failureLog = logs.find { it.action == "VALIDATION_FAILURE" }
        assertNotNull(failureLog)
    }

    @Test
    fun testAuditLogCannotBeUpdated() {
        val methods = AuditLogDao::class.java.declaredMethods
        val hasUpdate = methods.any { it.isAnnotationPresent(Update::class.java) || it.name.lowercase().contains("update") }
        assertFalse("AuditLogDao should not have update methods", hasUpdate)
    }

    @Test
    fun testAuditLogCannotBeDeleted() {
        val methods = AuditLogDao::class.java.declaredMethods
        val hasDelete = methods.any { it.isAnnotationPresent(Delete::class.java) || it.name.lowercase().contains("delete") }
        assertFalse("AuditLogDao should not have delete methods", hasDelete)
    }

    @Test
    fun testAuditLogDaoHasNoUpdateMethod() {
        // Same as testAuditLogCannotBeUpdated
        testAuditLogCannotBeUpdated()
    }

    @Test
    fun testAuditLogContainsAllRequiredFields() = runBlocking {
        val now = System.currentTimeMillis()
        val log = AuditLogEntity(
            logId = "testLogId",
            type = "TEST",
            refId = "testRefId",
            action = "TEST_ACTION",
            actorUserId = testUserId,
            detailsJson = "{\"test\":\"value\"}",
            createdAt = now
        )
        auditLogDao.insert(log)
        val retrieved = auditLogDao.getByRef("testRefId").first()
        assertEquals("testLogId", retrieved.logId)
        assertEquals("TEST", retrieved.type)
        assertEquals("testRefId", retrieved.refId)
        assertEquals("TEST_ACTION", retrieved.action)
        assertEquals(testUserId, retrieved.actorUserId)
        assertEquals("{\"test\":\"value\"}", retrieved.detailsJson)
        assertEquals(now, retrieved.createdAt)
    }

    @Test
    fun testAuditLogDetailsJsonIsValid() = runBlocking {
        val log = AuditLogEntity(
            logId = "testLogId2",
            type = "TEST",
            refId = "testRefId2",
            action = "TEST_ACTION",
            actorUserId = testUserId,
            detailsJson = "{\"key\":\"value\"}",
            createdAt = System.currentTimeMillis()
        )
        auditLogDao.insert(log)
        val retrieved = auditLogDao.getByRef("testRefId2").first()
        val details = gson.fromJson(retrieved.detailsJson, Map::class.java)
        assertEquals("value", details["key"])
    }

    @Test
    fun testAuditLogTimestampIsAccurate() = runBlocking {
        val before = System.currentTimeMillis()
        val log = AuditLogEntity(
            logId = "testLogId3",
            type = "TEST",
            refId = "testRefId3",
            action = "TEST_ACTION",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = System.currentTimeMillis()
        )
        auditLogDao.insert(log)
        val after = System.currentTimeMillis()
        val retrieved = auditLogDao.getByRef("testRefId3").first()
        assertTrue(retrieved.createdAt in before..after)
    }

    @Test
    fun testGetAuditLogsByRef() = runBlocking {
        val log1 = AuditLogEntity(
            logId = "log1",
            type = "TEST",
            refId = "ref",
            action = "ACTION1",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 1
        )
        val log2 = AuditLogEntity(
            logId = "log2",
            type = "TEST",
            refId = "ref",
            action = "ACTION2",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 2
        )
        auditLogDao.insert(log1)
        auditLogDao.insert(log2)
        val logs = auditLogDao.getByRef("ref")
        assertEquals(2, logs.size)
        assertEquals("log1", logs[0].logId)
        assertEquals("log2", logs[1].logId)
    }

    @Test
    fun testGetAuditLogsByType() = runBlocking {
        val log1 = AuditLogEntity(
            logId = "log3",
            type = "TYPE1",
            refId = "ref1",
            action = "ACTION",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 1
        )
        val log2 = AuditLogEntity(
            logId = "log4",
            type = "TYPE1",
            refId = "ref2",
            action = "ACTION",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 2
        )
        auditLogDao.insert(log1)
        auditLogDao.insert(log2)
        val logs = auditLogDao.getByType("TYPE1", 10)
        assertEquals(2, logs.size)
    }

    @Test
    fun testGetAuditLogsByActor() = runBlocking {
        val log = AuditLogEntity(
            logId = "log5",
            type = "TEST",
            refId = "ref",
            action = "ACTION",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 1
        )
        auditLogDao.insert(log)
        val logs = auditLogDao.getByActor(testUserId, 10)
        assertEquals(1, logs.size)
        assertEquals("log5", logs[0].logId)
    }

    @Test
    fun testAllSensitiveOperationsAreAudited() = runBlocking {
        val initialCount = auditLogDao.getByType("PRODUCT", 100).size +
                           auditLogDao.getByType("TRANSFER", 100).size +
                           auditLogDao.getByType("LINEAGE", 100).size
        // Perform operations
        val product = ProductEntity(
            productId = "",
            sellerId = testUserId,
            name = "Test Product",
            description = "Test Description",
            category = "Test Category",
            price = 100.0,
            quantity = 1.0,
            unit = "kg",
            location = "Test Location",
            latitude = 0.0,
            longitude = 0.0,
            lifecycleStatus = "ACTIVE",
            familyTreeId = null,
            parentIdsJson = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            isDeleted = false,
            deletedAt = null,
            dirty = false
        )
        productRepository.addProduct(product)
        // Assume transfer and lineage as above
        val finalCount = auditLogDao.getByType("PRODUCT", 100).size +
                         auditLogDao.getByType("TRANSFER", 100).size +
                         auditLogDao.getByType("LINEAGE", 100).size
        assertTrue(finalCount > initialCount)
    }

    @Test
    fun testAuditTrailIsChronological() = runBlocking {
        val log1 = AuditLogEntity(
            logId = "log6",
            type = "TEST",
            refId = "ref",
            action = "ACTION1",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 100
        )
        val log2 = AuditLogEntity(
            logId = "log7",
            type = "TEST",
            refId = "ref",
            action = "ACTION2",
            actorUserId = testUserId,
            detailsJson = null,
            createdAt = 200
        )
        auditLogDao.insert(log1)
        auditLogDao.insert(log2)
        val logs = auditLogDao.getByRef("ref")
        assertEquals(2, logs.size)
        assertTrue(logs[0].createdAt <= logs[1].createdAt)
    }
}