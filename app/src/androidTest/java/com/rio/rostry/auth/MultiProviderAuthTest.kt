package com.rio.rostry.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MultiProviderAuthTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    @Ignore("Requires Firebase Auth/Firestore emulators; implement environment hooks before enabling")
    fun newGoogleUser_requiresPhoneLinking() {
        // Arrange: Launch app with emulators, sign in with a fresh Google account stub
        // Act: Observe navigation/state
        // Assert: Phone verification UI is shown via global guard (no access to app content)
    }

    @Test
    @Ignore("Requires Firebase emulators")
    fun returningUserWithPhone_noPrompt() {
        // Arrange: Existing user with phone linked
        // Act: Sign in
        // Assert: App content appears; no phone verify UI
    }

    @Test
    @Ignore("Requires Firebase emulators")
    fun directPhoneSignup_noPrompt() {
        // Arrange: Sign in via phone provider
        // Assert: App content appears immediately
    }

    @Test
    @Ignore("Requires Firebase emulators")
    fun collisionAndNoNetwork_handledGracefully() {
        // Arrange: Simulate link collision and no-network
        // Assert: Error messages shown, cooldown respected
    }

    @Test
    @Ignore("Requires Firestore emulator with security rules")
    fun firestoreRules_blockUserCreateWithoutPhone() {
        // Arrange: New auth user without phone
        // Act: Trigger profile creation
        // Assert: Security denies create; repository defers creation safely
    }
}
