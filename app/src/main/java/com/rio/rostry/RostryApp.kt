package com.rio.rostry

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Configuration
import com.rio.rostry.workers.SyncWorker
import com.rio.rostry.workers.LifecycleWorker
import com.rio.rostry.workers.TransferTimeoutWorker
import com.rio.rostry.workers.ModerationWorker
import com.rio.rostry.workers.OutgoingMessageWorker
import com.rio.rostry.workers.AnalyticsAggregationWorker
import com.rio.rostry.workers.ReportingWorker
import com.rio.rostry.workers.VaccinationReminderWorker
import com.rio.rostry.workers.FarmPerformanceWorker
import com.rio.rostry.workers.OrderStatusWorker
import com.rio.rostry.workers.LegacyProductMigrationWorker
import coil.Coil
import coil.ImageLoader
import coil.util.DebugLogger
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.appcheck.FirebaseAppCheck
import com.rio.rostry.data.session.UserSessionManager
import javax.inject.Inject
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.FirebaseAnalytics

import com.rio.rostry.security.RootDetection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class RostryApp : Application(), Configuration.Provider, coil.ImageLoaderFactory {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var appCheckProviderFactory: com.google.firebase.appcheck.AppCheckProviderFactory

    @Inject
    lateinit var databaseValidationHelper: com.rio.rostry.util.DatabaseValidationHelper
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun hiltWorkerFactory(): HiltWorkerFactory
    }

    override val workManagerConfiguration: Configuration
        get() {
            val entryPoint = EntryPointAccessors.fromApplication(
                this,
                WorkerFactoryEntryPoint::class.java
            )
            return Configuration.Builder()
                .setWorkerFactory(entryPoint.hiltWorkerFactory())
                .build()
        }
        
    override fun newImageLoader(): ImageLoader {
        val entryPoint = EntryPointAccessors.fromApplication(this, com.rio.rostry.di.AppEntryPoints::class.java)
        return entryPoint.imageLoader()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            // Enable StrictMode to surface bad practices in debug builds, but avoid noisy Google Play Services violations
            try {
                val threadPolicy = android.os.StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
                android.os.StrictMode.setThreadPolicy(threadPolicy)

                val vmBuilder = android.os.StrictMode.VmPolicy.Builder()
                    // Keep important VM checks but remove noisy NonSdkApiUsage detection
                    .detectActivityLeaks()
                    .detectFileUriExposure()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                // Do not enable detectNonSdkApiUsage() in debug builds to avoid overwhelming logs with Google Play Services internal API uses
                // Only enable it on targeted test builds or when specifically debugging for non-SDK API usage
                android.os.StrictMode.setVmPolicy(vmBuilder.build())

                // Add a short documentation comment about the StrictMode configuration
                Timber.d("StrictMode configured: ThreadPolicy=detectAll, VmPolicy=withoutNonSdkApiUsage (to avoid Google Play Services noise)")
            } catch (t: Throwable) {
                Timber.w(t, "Failed to enable StrictMode")
            }
        }

        // Initialize Firebase explicitly
        FirebaseApp.initializeApp(this)

        // Initialize Crashlytics explicitly with error handling
        try {
            val crashlytics = com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance()
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            Timber.d("Firebase Crashlytics initialized successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Firebase Crashlytics")
            // Don't crash the app if Crashlytics fails to initialize
        }

        // Disable Phone Auth app verification in debug builds to bypass reCAPTCHA/Play Integrity
        if (BuildConfig.PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING) {
            FirebaseAuth.getInstance().apply {
                firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
                useAppLanguage()
            }
            Timber.w("Phone Auth: App verification disabled for TESTING (configured via buildConfig)")
        } else {
            // Ensure app language is used for production too
            FirebaseAuth.getInstance().useAppLanguage()
        }

        // App Check installation
        applicationScope.launch(Dispatchers.IO) {
            try {
                val firebaseAppCheck = FirebaseAppCheck.getInstance()
                firebaseAppCheck.installAppCheckProviderFactory(appCheckProviderFactory)
                Timber.d("App Check Provider initialized: %s", appCheckProviderFactory.javaClass.simpleName)
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize App Check")
            }
        }

        // Places SDK initialization is handled lazily by PlacesModule.kt
        // if (!Places.isInitialized()) {
        //    Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        //    Timber.d("Places SDK initialized")
        // }

        // Coil ImageLoader from DI for centralized config (memory/disk cache, RGB_565, OkHttp cache)
        // using lazy init via ImageLoaderFactory interface
        val entryPoint = EntryPointAccessors.fromApplication(this, com.rio.rostry.di.AppEntryPoints::class.java)
        // Attach MediaUploadManager outbox at startup (initializer has side-effects in init)
        entryPoint.mediaUploadInitializer()

        // WorkManager is configured via Configuration.Provider; avoid explicit initialize to prevent duplicates.

        // Defer worker scheduling to background threads without arbitrary blocking delay
        applicationScope.launch {
            setupSessionBasedWorkers()
            Timber.d("Background workers scheduled")
        }
        
        // Register connectivity listener for expedited sync on network reconnection
        setupConnectivityListener()
        
        // Root detection - check in background and log results
        applicationScope.launch(Dispatchers.IO) {
            try {
                val rootResult = RootDetection.isDeviceRooted(this@RostryApp)
                com.rio.rostry.security.SecurityManager.processRootDetectionResult(
                    this@RostryApp,
                    rootResult.isRooted,
                    rootResult.detectionMethods
                )
            } catch (e: Exception) {
                Timber.e(e, "Root detection failed")
            }
        }
        
        // Seed demo products in debug builds only
        if (BuildConfig.DEBUG) {
            // Move debug operations off main thread for faster startup
            applicationScope.launch(Dispatchers.IO) {
                delay(2000) // Delay debug operations to not interfere with startup
                try {
                    val report = databaseValidationHelper.validateProductForeignKeys()
                    if (!report.isValid) {
                        Timber.w("Orphaned products found: ${report.invalidCount}, IDs: ${report.invalidIds.joinToString()}")
                        // Show toast on main thread
                        launch(Dispatchers.Main) {
                            android.widget.Toast.makeText(
                                this@RostryApp,
                                "Orphaned products detected: ${report.invalidCount}",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Timber.d("Database validation passed: no orphaned products")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to validate database on startup")
                }

            }
        }
    }

    private fun setupSessionBasedWorkers() {
        applicationScope.launch {
            userSessionManager.isUserLoggedIn.collect { isLoggedIn ->
                val workManager = WorkManager.getInstance(this@RostryApp)
                if (isLoggedIn) {
                    // ZERO-COST ARCHITECTURE: No more waiting for Cloud Functions claims!
                    // Firestore security rules now read userType directly from user documents.
                    Timber.i("User is logged in. Scheduling workers...")
                    // Fixed: Removed arbitrary delay. Reactive flow ensures readiness.
                    
                    scheduleAllWorkers(workManager)
                } else {
                    Timber.i("User is logged out, cancelling session workers.")
                    // Cancel only session-specific work
                    workManager.cancelAllWorkByTag("session_worker")
                    // Fallback for workers that might not have the tag yet or use unique names
                    workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
                    // Note: System-level workers (e.g. AnalyticsAggregation if system-wide) should remain.
                }
            }
        }
    }

    private fun scheduleAllWorkers(workManager: WorkManager) {
        // General constraints for standard workers
        val generalConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        // Strict constraints for heavy SyncWorker (WiFi only)
        val syncConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        // SyncWorker - Session Bound - 8 Hour Interval (Reduced from 4h for Quota Optimization)
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(8, TimeUnit.HOURS)
            .setConstraints(syncConstraints)
            .setBackoffCriteria(
                androidx.work.BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.MINUTES
            )
            .addTag("session_worker")
            .build()
        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        // Schedule others (Assumption: They should eventually be updated to include tags or be system wide)
        LifecycleWorker.schedule(this)
        TransferTimeoutWorker.schedule(this)
        ModerationWorker.schedule(this)
        // OutgoingMessageWorker removed - OutboxSyncWorker now handles all message delivery
        AnalyticsAggregationWorker.schedule(this) // System wide? Keeping it running or scheduling it.
        ReportingWorker.schedule(this)
        VaccinationReminderWorker.schedule(this)
        FarmPerformanceWorker.schedule(this)
        com.rio.rostry.workers.QuarantineReminderWorker.schedule(this)
        com.rio.rostry.workers.EnthusiastPerformanceWorker.schedule(this)
        com.rio.rostry.workers.OutboxSyncWorker.schedule(this)
        com.rio.rostry.workers.PullSyncWorker.schedule(this)
        OrderStatusWorker.schedule(this)
        com.rio.rostry.workers.PrefetchWorker.schedule(this)
        com.rio.rostry.workers.PersonalizationWorker.schedule(this)
        com.rio.rostry.workers.CommunityEngagementWorker.schedule(this)
        com.rio.rostry.workers.NotificationFlushWorker.schedule(this)
        com.rio.rostry.workers.StorageQuotaMonitorWorker.enqueuePeriodic(this)
        com.rio.rostry.workers.EvidenceOrderWorker.schedule(this) // Critical: Quote expiry, payment reminders, delivery confirmations
        
        // One-time migration: Legacy ProductEntity → New Farm Asset Architecture
        scheduleLegacyMigration(workManager)
    }
    
    private fun scheduleLegacyMigration(workManager: WorkManager) {
        val migrationRequest = OneTimeWorkRequestBuilder<LegacyProductMigrationWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("legacy_migration")
            .build()
        
        workManager.enqueueUniqueWork(
            LegacyProductMigrationWorker.WORK_NAME,
            androidx.work.ExistingWorkPolicy.KEEP, // Only run once
            migrationRequest
        )
        Timber.i("Scheduled LegacyProductMigrationWorker (one-time migration)")
    }
    
    private var isConnectivityCallbackRegistered = false

    /**
     * Registers a network callback to trigger expedited syncs on reconnection.
     * This callback is intended to live for the entire process lifetime.
     */
    private fun setupConnectivityListener() {
        if (isConnectivityCallbackRegistered) return
        
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        
        connectivityManager?.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Timber.d("Network available, triggering expedited sync")
                
                // Enqueue expedited sync to meet 5-minute SLA
                val request = OneTimeWorkRequestBuilder<com.rio.rostry.workers.OutboxSyncWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
                
                WorkManager.getInstance(this@RostryApp).enqueue(request)

                // Trigger immediate flush of batched notifications when connectivity is restored
                com.rio.rostry.workers.NotificationFlushWorker.scheduleImmediateFlush(this@RostryApp)
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                Timber.d("Network lost")
            }
        })
        isConnectivityCallbackRegistered = true
    }
    

}
