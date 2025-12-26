package com.rio.rostry.data.monitoring

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUsageTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("firebase_usage_stats", Context.MODE_PRIVATE)
    
    companion object {
        // Spark Plan Limits (Daily)
        const val LIMIT_READS = 50_000
        const val LIMIT_WRITES = 20_000
        const val LIMIT_DELETES = 20_000
        
        // Safety Threshold (80%)
        const val THRESHOLD_READS = (LIMIT_READS * 0.8).toInt()
        
        private const val KEY_READS = "reads"
        private const val KEY_WRITES = "writes"
        private const val KEY_DELETES = "deletes"
        private const val KEY_DATE = "date"
    }

    @Synchronized
    fun trackReads(count: Int) {
        checkAndResetDate()
        val current = prefs.getInt(KEY_READS, 0)
        prefs.edit().putInt(KEY_READS, current + count).apply()
        checkThresholds()
    }

    @Synchronized
    fun trackWrites(count: Int) {
        checkAndResetDate()
        val current = prefs.getInt(KEY_WRITES, 0)
        prefs.edit().putInt(KEY_WRITES, current + count).apply()
    }
    
    fun getUsage(): UsageStats {
        checkAndResetDate()
        return UsageStats(
            reads = prefs.getInt(KEY_READS, 0),
            writes = prefs.getInt(KEY_WRITES, 0),
            deletes = prefs.getInt(KEY_DELETES, 0)
        )
    }

    fun isQuotaExceeded(): Boolean {
        val stats = getUsage()
        return stats.reads >= THRESHOLD_READS || stats.writes >= LIMIT_WRITES
    }

    private fun checkAndResetDate() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastDate = prefs.getString(KEY_DATE, "")
        
        if (lastDate != today) {
            Timber.i("Resetting Firebase usage stats for new day: $today")
            prefs.edit()
                .putString(KEY_DATE, today)
                .putInt(KEY_READS, 0)
                .putInt(KEY_WRITES, 0)
                .putInt(KEY_DELETES, 0)
                .apply()
        }
    }
    
    private fun checkThresholds() {
        val reads = prefs.getInt(KEY_READS, 0)
        if (reads > THRESHOLD_READS) {
            Timber.w("Firebase Read Quota at 80% ($reads/$LIMIT_READS). Consider disabling non-critical features.")
            // Trigger UI warning or feature toggle here if needed
            // e.g. FeatureFlags.disableCommunityFeed()
        }
    }
    
    data class UsageStats(
        val reads: Int,
        val writes: Int,
        val deletes: Int
    )
}
