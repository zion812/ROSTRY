package com.rio.rostry.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rio.rostry.core.ErrorLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashlyticsErrorLogger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : ErrorLogger {
    override fun log(throwable: Throwable, message: String?) {
        message?.let { crashlytics.log(it) }
        crashlytics.recordException(throwable)
    }

    override fun setUserId(id: String) {
        crashlytics.setUserId(id)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}
