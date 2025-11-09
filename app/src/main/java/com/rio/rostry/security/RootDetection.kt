package com.rio.rostry.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import timber.log.Timber
import java.io.File

/**
 * Root detection utility for identifying rooted/jailbroken devices.
 * 
 * This provides multiple detection methods to identify if a device is rooted.
 * Detection is used to display a security warning to users, not to block app usage.
 * 
 * @see SECURITY.md for security policy and root detection behavior
 */
object RootDetection {

    private val SU_BINARY_PATHS = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    private val ROOT_MANAGEMENT_APPS = listOf(
        "com.noshufou.android.su",
        "com.noshufou.android.su.elite",
        "eu.chainfire.supersu",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.koushikdutta.rommanager",
        "com.koushikdutta.rommanager.license",
        "com.dimonvideo.luckypatcher",
        "com.chelpus.lackypatch",
        "com.ramdroid.appquarantine",
        "com.ramdroid.appquarantinepro",
        "com.topjohnwu.magisk"
    )

    private val ROOT_FILES = listOf(
        "/system/app/Superuser.apk",
        "/system/app/SuperSU.apk",
        "/system/xbin/daemonsu",
        "/system/etc/init.d/99SuperSUDaemon",
        "/data/data/com.noshufou.android.su",
        "/dev/com.koushikdutta.superuser.daemon",
        "/system/app/SuperSU",
        "/system/xbin/ku.sud"
    )

    private val RW_SYSTEM_PATHS = listOf(
        "/system",
        "/system/bin",
        "/system/sbin",
        "/system/xbin",
        "/vendor/bin",
        "/sbin",
        "/etc"
    )

    /**
     * Comprehensive root detection check.
     * Combines multiple detection methods for higher accuracy.
     * 
     * @return RootDetectionResult containing detection status and details
     */
    fun isDeviceRooted(context: Context): RootDetectionResult {
        val checks = mutableListOf<String>()
        var isRooted = false

        // Check 1: SU binary exists
        if (checkSuBinary()) {
            checks.add("SU binary found")
            isRooted = true
        }

        // Check 2: Root management apps installed
        val rootApps = checkRootManagementApps(context)
        if (rootApps.isNotEmpty()) {
            checks.add("Root management apps: ${rootApps.joinToString()}")
            isRooted = true
        }

        // Check 3: Build tags contain test-keys
        if (checkBuildTags()) {
            checks.add("Build tags contain 'test-keys'")
            isRooted = true
        }

        // Check 4: Common root files exist
        if (checkRootFiles()) {
            checks.add("Common root files detected")
            isRooted = true
        }

        // Check 5: System paths are writable (dangerous check, only if other checks failed)
        if (!isRooted && checkRWPaths()) {
            checks.add("System paths are writable")
            isRooted = true
        }

        Timber.d("Root detection completed: rooted=$isRooted, checks=${checks.joinToString("; ")}")
        
        return RootDetectionResult(
            isRooted = isRooted,
            detectionMethods = checks,
            detectionTime = System.currentTimeMillis()
        )
    }

    /**
     * Check if SU binary exists in common locations.
     */
    fun checkSuBinary(): Boolean {
        return SU_BINARY_PATHS.any { path ->
            try {
                File(path).exists()
            } catch (e: Exception) {
                Timber.w(e, "Error checking SU binary at $path")
                false
            }
        }
    }

    /**
     * Check if common root management apps are installed.
     * 
     * @return List of detected root app package names
     */
    fun checkRootManagementApps(context: Context): List<String> {
        val pm = context.packageManager
        return ROOT_MANAGEMENT_APPS.filter { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            } catch (e: Exception) {
                Timber.w(e, "Error checking package $packageName")
                false
            }
        }
    }

    /**
     * Check if build tags contain 'test-keys' which indicates a custom ROM.
     */
    fun checkBuildTags(): Boolean {
        return try {
            val buildTags = Build.TAGS
            buildTags != null && buildTags.contains("test-keys")
        } catch (e: Exception) {
            Timber.w(e, "Error checking build tags")
            false
        }
    }

    /**
     * Check if common root-related files exist.
     */
    fun checkRootFiles(): Boolean {
        return ROOT_FILES.any { path ->
            try {
                File(path).exists()
            } catch (e: Exception) {
                Timber.w(e, "Error checking root file at $path")
                false
            }
        }
    }

    /**
     * Attempt to write to protected system paths.
     * This is a last-resort check and may cause issues, use cautiously.
     * 
     * @return true if any system path is writable
     */
    fun checkRWPaths(): Boolean {
        return RW_SYSTEM_PATHS.any { path ->
            try {
                val file = File(path)
                file.canWrite()
            } catch (e: Exception) {
                Timber.w(e, "Error checking RW path $path")
                false
            }
        }
    }
}

/**
 * Result of root detection checks.
 * 
 * @property isRooted Whether the device appears to be rooted
 * @property detectionMethods List of detection methods that triggered
 * @property detectionTime Timestamp when detection was performed
 */
data class RootDetectionResult(
    val isRooted: Boolean,
    val detectionMethods: List<String>,
    val detectionTime: Long
)
