package com.rio.rostry.utils

import android.util.Log

class AppLogger private constructor() {
    companion object {
        private const val TAG = "ROSTRY"
        private var INSTANCE: AppLogger? = null
        
        fun getInstance(): AppLogger {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppLogger().also { INSTANCE = it }
            }
        }
    }
    
    fun logInfo(message: String) {
        Log.i(TAG, message)
    }
    
    fun logWarning(message: String) {
        Log.w(TAG, message)
    }
    
    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
    
    fun logNetworkCall(url: String, durationMs: Long, success: Boolean) {
        Log.d(TAG, "Network call to $url took ${durationMs}ms, success: $success")
    }
    
    fun startPerformanceTrace(traceName: String) {
        // In a real implementation, this would start a performance trace
        logInfo("Starting performance trace: $traceName")
    }
    
    fun stopPerformanceTrace(traceName: String) {
        // In a real implementation, this would stop a performance trace
        logInfo("Stopping performance trace: $traceName")
    }
    
    fun recordNetworkPerformance(url: String, durationMs: Long, success: Boolean) {
        logNetworkCall(url, durationMs, success)
    }
}