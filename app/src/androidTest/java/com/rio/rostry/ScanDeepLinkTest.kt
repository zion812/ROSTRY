package com.rio.rostry

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.rio.rostry.MainActivity
import org.hamcrest.CoreMatchers.anyOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText

@RunWith(AndroidJUnit4::class)
@MediumTest
class ScanDeepLinkTest {

    @get:Rule
    val cameraPerm: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Test
    fun deepLink_opensScannerOrFallback() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("rostry://scan?context=family_tree"))
        intent.setClass(appContext, MainActivity::class.java)

        ActivityScenario.launch<MainActivity>(intent).use {
            // Accept either scanning UI or permission/fallback text depending on feature flag and device state
            onView(
                anyOf(
                    withText("Scan QR"),
                    withText("Camera permission needed to scan QR codes."),
                    withText("Product ID or rostry://product/{id}")
                )
            ).check(matches(isDisplayed()))
        }
    }
}
