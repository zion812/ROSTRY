package com.rio.rostry.auth

import android.content.Context
import androidx.biometric.BiometricManager
import com.rio.rostry.util.BiometricAuthUtil
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BiometricAuthUtilTest {

    private lateinit var context: Context
    private lateinit var biometricManager: BiometricManager

    @Before
    fun setUp() {
        context = mockk()
        biometricManager = mockk()

        mockkStatic(BiometricManager::class)
        every { BiometricManager.from(context) } returns biometricManager
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `isBiometricAuthAvailable should return true when biometric auth is available`() {
        every { biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_SUCCESS

        assertTrue(BiometricAuthUtil.isBiometricAuthAvailable(context))
    }

    @Test
    fun `isBiometricAuthAvailable should return false when biometric auth is not available`() {
        every { biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE

        assertFalse(BiometricAuthUtil.isBiometricAuthAvailable(context))
    }
}