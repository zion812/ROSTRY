package com.rio.rostry

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
class RostryApp : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
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

        // Schedule General user order status polling (every 30 minutes)
        OrderStatusWorker.schedule(this)

        // Schedule prefetch under safe conditions (Wi‑Fi, charging)
        com.rio.rostry.workers.PrefetchWorker.schedule(this)

        // Schedule loveable-product background workers
        com.rio.rostry.workers.PersonalizationWorker.schedule(this)
        com.rio.rostry.workers.CommunityEngagementWorker.schedule(this)
        
        // Seed demo products in debug builds only
        if (BuildConfig.DEBUG) {
            seedDemoData()
        }
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
