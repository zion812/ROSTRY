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
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.android.libraries.places.api.Places
import com.rio.rostry.data.demo.DemoProductSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class RostryApp : Application(), Configuration.Provider {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
fun   hiltWorkerFactory(): HiltWorkerFactory
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
        }

        // Initialize Firebase explicitly
        FirebaseApp.initializeApp(this)

        // Initialize Firebase App Check with Debug Provider (for development)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            // Use debug provider for development - register the debug token in Firebase Console
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Timber.d("App Check Debug Provider initialized")
        }

        // Initialize Places SDK (New) with App Check support
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(this, BuildConfig.MAPS_API_KEY)
            Timber.d("Places SDK initialized")
        }
        // TODO: For production, replace DebugAppCheckProviderFactory with appropriate provider
        // (Play Integrity if publishing to Play Store, or Custom provider for other distribution)

        // Coil ImageLoader from DI for centralized config (memory/disk cache, RGB_565, OkHttp cache)
        val entryPoint = EntryPointAccessors.fromApplication(this, com.rio.rostry.di.AppEntryPoints::class.java)
        val imageLoader: ImageLoader = entryPoint.imageLoader()
        Coil.setImageLoader(imageLoader)
        // Attach MediaUploadManager outbox at startup (initializer has side-effects in init)
        entryPoint.mediaUploadInitializer()

        // Initialize WorkManager with our custom WorkerFactory configuration
        WorkManager.initialize(this, workManagerConfiguration)

        // Schedule periodic sync (every 6 hours, requires network)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

        // Schedule lifecycle milestone worker (daily)
        LifecycleWorker.schedule(this)

        // Schedule transfer timeout checker (every ~15 minutes)
        TransferTimeoutWorker.schedule(this)

        // Schedule moderation scanner (every 6 hours)
        ModerationWorker.schedule(this)

        // Schedule outgoing message sender (every 15 minutes, requires network)
        OutgoingMessageWorker.schedule(this)

        // Schedule analytics aggregation (daily) and reporting (weekly)
        AnalyticsAggregationWorker.schedule(this)
        ReportingWorker.schedule(this)

        // Schedule farm monitoring workers
        VaccinationReminderWorker.schedule(this)
        FarmPerformanceWorker.schedule(this)
        // Schedule quarantine overdue reminder
        com.rio.rostry.workers.QuarantineReminderWorker.schedule(this)
        // Schedule enthusiast KPIs aggregation (weekly)
        com.rio.rostry.workers.EnthusiastPerformanceWorker.schedule(this)

        // Schedule sync workers for offline-first reconciliation
        com.rio.rostry.workers.OutboxSyncWorker.schedule(this)
        com.rio.rostry.workers.PullSyncWorker.schedule(this)

        // Schedule General user order status polling (every 30 minutes)
        OrderStatusWorker.schedule(this)

        // Schedule prefetch under safe conditions (Wi‑Fi, charging)
        com.rio.rostry.workers.PrefetchWorker.schedule(this)

        // Schedule loveable-product background workers
        com.rio.rostry.workers.PersonalizationWorker.schedule(this)
        com.rio.rostry.workers.CommunityEngagementWorker.schedule(this)
        
        // Register connectivity listener for expedited sync on network reconnection
        setupConnectivityListener()
        
        // Seed demo products in debug builds only
        if (BuildConfig.DEBUG) {
            seedDemoData()
        }
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
