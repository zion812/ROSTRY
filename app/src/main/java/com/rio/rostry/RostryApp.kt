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
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.rio.rostry.data.session.UserSessionManager
import javax.inject.Inject
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.FirebaseAnalytics
import com.rio.rostry.data.demo.DemoProductSeeder
import com.rio.rostry.security.RootDetection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class RostryApp : Application(), Configuration.Provider {
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

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            // Enable StrictMode to surface hidden API and other bad practices in debug builds
            try {
                val threadPolicy = android.os.StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
                android.os.StrictMode.setThreadPolicy(threadPolicy)

                val vmBuilder = android.os.StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    vmBuilder.detectNonSdkApiUsage()
                }
                android.os.StrictMode.setVmPolicy(vmBuilder.build())
            } catch (t: Throwable) {
                Timber.w(t, "Failed to enable StrictMode")
            }
        }

        // Initialize Firebase explicitly
        FirebaseApp.initializeApp(this)

        // Disable Phone Auth app verification in debug builds to bypass reCAPTCHA/Play Integrity
        if (BuildConfig.DEBUG) {
            FirebaseAuth.getInstance()
                .firebaseAuthSettings
                .setAppVerificationDisabledForTesting(true)
            Timber.w("Phone Auth: App verification disabled for TESTING (debug build only)")
        }

        // App Check installation
        if (BuildConfig.DEBUG) {
            // IMPORTANT: Install Debug provider synchronously in debug builds so
            // early Firebase calls (e.g., PhoneAuth) have a valid App Check token.
            try {
                val firebaseAppCheck = FirebaseAppCheck.getInstance()
                firebaseAppCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
                Timber.i("App Check (debug) provider installed synchronously")
            } catch (e: Exception) {
                Timber.e(e, "Failed to install App Check debug provider")
            }
        } else {
            // Defer App Check setup on release to avoid startup cost
            applicationScope.launch(Dispatchers.IO) {
                try {
                    val firebaseAppCheck = FirebaseAppCheck.getInstance()
                    firebaseAppCheck.installAppCheckProviderFactory(appCheckProviderFactory)
                    Timber.d("App Check Provider initialized: %s", appCheckProviderFactory.javaClass.simpleName)
                    if (appCheckProviderFactory is PlayIntegrityAppCheckProviderFactory) {
                        Timber.i("App Check: Play Integrity is active (release build)")
                    } else {
                        Timber.w("App Check: Unexpected provider in release: %s", appCheckProviderFactory.javaClass.name)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Failed to initialize App Check")
                }
            }
        }

        // Defer Places SDK initialization - will be initialized lazily when first needed
        // This saves ~200ms on app startup
        Timber.d("Places SDK initialization deferred for startup optimization")

        // Coil ImageLoader from DI for centralized config (memory/disk cache, RGB_565, OkHttp cache)
        val entryPoint = EntryPointAccessors.fromApplication(this, com.rio.rostry.di.AppEntryPoints::class.java)
        val imageLoader: ImageLoader = entryPoint.imageLoader()
        Coil.setImageLoader(imageLoader)
        // Attach MediaUploadManager outbox at startup (initializer has side-effects in init)
        entryPoint.mediaUploadInitializer()

        // WorkManager is configured via Configuration.Provider; avoid explicit initialize to prevent duplicates.

        // Defer worker scheduling by 3 seconds to avoid blocking app startup
        applicationScope.launch {
            delay(3000)
            setupSessionBasedWorkers()
            Timber.d("Background workers scheduled after startup delay")
        }
        
        // Register connectivity listener for expedited sync on network reconnection
        setupConnectivityListener()
        
        // Root detection - check in background and log results
        applicationScope.launch(Dispatchers.IO) {
            try {
                val rootResult = RootDetection.isDeviceRooted(this@RostryApp)
                if (rootResult.isRooted) {
                    Timber.w("Root detected: ${rootResult.detectionMethods.joinToString(", ")}")
                    // Log to Firebase Analytics for monitoring
                    FirebaseAnalytics.getInstance(this@RostryApp).logEvent("root_detected", null)
                    
                    // Show non-blocking warning dialog on main thread
                    launch(Dispatchers.Main) {
                        // Dialog will be shown when activity is created
                        // Store root detection result for later display
                        getSharedPreferences("security_prefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("device_rooted", true)
                            .putString("root_methods", rootResult.detectionMethods.joinToString(", "))
                            .apply()
                    }
                } else {
                    Timber.d("Root detection: Device not rooted")
                }
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
                seedDemoData()
            }
        }
    }

    private fun setupSessionBasedWorkers() {
        applicationScope.launch {
            userSessionManager.isUserLoggedIn.collect { isLoggedIn ->
                val workManager = WorkManager.getInstance(this@RostryApp)
                if (isLoggedIn) {
                    Timber.i("User is logged in, scheduling background workers.")
                    scheduleAllWorkers(workManager)
                } else {
                    Timber.i("User is logged out, cancelling all background workers.")
                    workManager.cancelAllWork()
                }
            }
        }
    }

    private fun scheduleAllWorkers(workManager: WorkManager) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        LifecycleWorker.schedule(this)
        TransferTimeoutWorker.schedule(this)
        ModerationWorker.schedule(this)
        OutgoingMessageWorker.schedule(this)
        AnalyticsAggregationWorker.schedule(this)
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
    }
    
    private fun setupConnectivityListener() {
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
    }
    
    private fun seedDemoData() {
        applicationScope.launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    this@RostryApp, 
                    com.rio.rostry.di.AppEntryPoints::class.java
                )
                val seeder: DemoProductSeeder = entryPoint.demoProductSeeder()
                seeder.seedProducts()
                Timber.i("RostryApp: Demo data seeding initiated")
            } catch (e: Exception) {
                Timber.e(e, "RostryApp: Failed to seed demo data")
            }
        }
    }
}
