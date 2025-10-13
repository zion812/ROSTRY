package com.rio.rostry

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDetailsQrSnackbarTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsQrSavedSnackbar_whenGenerateProductQrClicked() {
        // Arrange: launch via deep link to Product Details
        val productId = "demo_product"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("rostry://product/$productId")
        }
        composeRule.activityRule.scenario.onActivity { activity ->
            activity.startActivity(intent)
        }

        // Act: tap the Generate Product QR button if present
        // It resides inside ExportLineageCard which is visible when the product has a familyTreeId
        val generateBtn = composeRule.onNodeWithText("Generate Product QR", substring = false)
        generateBtn.assertIsDisplayed()
        generateBtn.performClick()

        // Assert: Snackbar with "QR saved" text is shown
        composeRule.onNodeWithText("QR saved", substring = false).assertIsDisplayed()
    }
}
