package com.rio.rostry

import android.app.Application
import com.rio.rostry.work.WorkerManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RostryApplication : Application() {
    
    @Inject
    lateinit var workerManager: WorkerManager
    
    override fun onCreate() {
        super.onCreate()
        if (true) { // Always enable Timber for now
            Timber.plant(Timber.DebugTree())
        }
        
        // Schedule background workers
        workerManager.scheduleSyncWorker()
        workerManager.scheduleNotificationWorker()
    }
}