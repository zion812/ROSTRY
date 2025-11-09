package com.rio.rostry.ui.transfer

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import javax.inject.Inject
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.TransferVerificationDao
import com.rio.rostry.data.database.dao.AuditLogDao
import com.rio.rostry.data.database.dao.UploadTaskDao
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.UploadTaskEntity
import com.rio.rostry.di.CurrentUserHolder
import com.rio.rostry.session.RolePreferenceDataSource
import com.rio.rostry.domain.model.UserType

/**
 * Verification screen UI test rendering TransferVerificationScreen explicitly.
 * Expanded assertions with deterministic seeding via DAOs.
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
class TransferVerificationWorkflowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Inject lateinit var transferDao: TransferDao
    @Inject lateinit var verificationDao: TransferVerificationDao
    @Inject lateinit var auditLogDao: AuditLogDao
    @Inject lateinit var uploadTaskDao: UploadTaskDao
    @Inject lateinit var rolePrefs: RolePreferenceDataSource

    @Before
    fun setup() {
        hiltRule.inject()
        // Authenticate and set role
        CurrentUserHolder.userId = "test-user"
        rolePrefs.persist(UserType.GENERAL)
        // Seed a transfer with GPS for seller
        val now = System.currentTimeMillis()
        val t = TransferEntity(
            transferId = "t-ux",
            productId = "product-1",
            fromUserId = "seller-a",
            toUserId = "test-user",
            orderId = null,
            amount = 999.0,
            currency = "INR",
            type = "SALE",
            status = "PENDING",
            transactionReference = null,
            notes = null,
            initiatedAt = now,
            updatedAt = now,
            lastModifiedAt = now,
            gpsLat = 12.9716,
            gpsLng = 77.5946,
            dirty = false
        )
        kotlinx.coroutines.runBlocking { transferDao.upsert(t) }
        composeRule.setContent { TransferVerificationScreen(transferId = "t-ux") }
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("Transfer Verification: t-ux").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
    }

    @Test
    fun uploadsCard_shows_after_uploadTaskSeed() {
        // Seed an upload task to simulate queued before/after uploads
        val pathBefore = "transfers/t-ux/before_1.jpg"
        val pathAfter = "transfers/t-ux/after_1.jpg"
        kotlinx.coroutines.runBlocking { uploadTaskDao.upsert(
            UploadTaskEntity(
                taskId = "u1", localPath = "/tmp/a.jpg", remotePath = pathBefore,
                status = "QUEUED", progress = 0, retries = 0, createdAt = 0, updatedAt = 0, error = null, contextJson = null
            )
        ) }
        kotlinx.coroutines.runBlocking { uploadTaskDao.upsert(
            UploadTaskEntity(
                taskId = "u2", localPath = "/tmp/b.jpg", remotePath = pathAfter,
                status = "QUEUED", progress = 0, retries = 0, createdAt = 0, updatedAt = 0, error = null, contextJson = null
            )
        ) }
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("Uploads").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
        composeRule.onNodeWithText("Uploads").assertIsDisplayed()
    }

    @Test
    fun gps_within_and_outside_radius_flow() {
        // Within 100m
        composeRule.onNodeWithText("Latitude").performTextInput("12.97165")
        composeRule.onNodeWithText("Longitude").performTextInput("77.59460")
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("Within 100m").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
        composeRule.onNodeWithText("Submit GPS").performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("GPS submitted").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
        // Outside 100m requires explanation
        composeRule.onNodeWithText("Latitude").performTextInput("13.0000")
        composeRule.onNodeWithText("Longitude").performTextInput("77.0000")
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("Outside 100m").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
        // Button disabled until explanation field appears and filled
        composeRule.onNodeWithText("Explain why GPS is outside radius (required)").performTextInput("Road closure; had to verify from gate")
        composeRule.onNodeWithText("Submit GPS").performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("GPS submitted").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
    }

    @Test
    fun signature_submission_shows_success() {
        // Instead of drawing, set signature ref then submit
        composeRule.onNodeWithText("Signature Ref / Hash").performTextInput("sig-ref-123")
        composeRule.onNodeWithText("Submit Signature").performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("Signature submitted").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
    }

    @Test
    fun trust_score_attempts_to_render() {
        // When trust score is computed, a card titled Trust Score should appear
        // Allow some time for repository computation
        composeRule.waitUntil(timeoutMillis = 3_000) {
            try { composeRule.onNodeWithText("Trust Score").fetchSemanticsNode(); true } catch (_: Throwable) { false }
        }
        composeRule.onNodeWithText("Trust Score").assertIsDisplayed()
    }

    // Dispute admin UI test removed: Dispute types are not present in current project schema
}
