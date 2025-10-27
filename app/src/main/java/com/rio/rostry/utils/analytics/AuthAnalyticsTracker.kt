package com.rio.rostry.utils.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

interface AuthAnalyticsTracker {
    fun trackAuthProviderSelected(provider: String)
    fun trackPhoneVerifyStart(isNewUser: Boolean)
    fun trackPhoneVerifySuccess(provider: String)
    fun trackPhoneVerifyFail(provider: String, reason: String)
    fun trackPhoneLinkCollision(provider: String)
    fun trackAuthCancel(provider: String)
    fun trackAuthComplete(provider: String, isNewUser: Boolean)
}

@Singleton
class AuthAnalyticsTrackerImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AuthAnalyticsTracker {

    private fun log(event: String, params: Bundle.() -> Unit) {
        val b = Bundle().apply(params)
        firebaseAnalytics.logEvent(event, b)
    }

    override fun trackAuthProviderSelected(provider: String) = log("auth_provider_selected") {
        putString("provider", provider)
        putLong("ts", System.currentTimeMillis())
    }

    override fun trackPhoneVerifyStart(isNewUser: Boolean) = log("auth_phone_verify_start") {
        putString("stage", "start")
        putString("user_type", if (isNewUser) "new" else "returning")
        putLong("ts", System.currentTimeMillis())
    }

    override fun trackPhoneVerifySuccess(provider: String) = log("auth_phone_verify_success") {
        putString("provider", provider)
        putLong("ts", System.currentTimeMillis())
    }

    override fun trackPhoneVerifyFail(provider: String, reason: String) = log("auth_phone_verify_fail") {
        putString("provider", provider)
        putString("reason", reason)
        putLong("ts", System.currentTimeMillis())
    }

    override fun trackPhoneLinkCollision(provider: String) = log("auth_phone_link_collision") {
        putString("provider", provider)
        putLong("ts", System.currentTimeMillis())
    }

    override fun trackAuthCancel(provider: String) = log("auth_cancel") {
        putString("provider", provider)
        putLong("ts", System.currentTimeMillis())
    }

    override fun trackAuthComplete(provider: String, isNewUser: Boolean) = log("auth_complete") {
        putString("provider", provider)
        putString("user_type", if (isNewUser) "new" else "returning")
        putLong("ts", System.currentTimeMillis())
    }
}
