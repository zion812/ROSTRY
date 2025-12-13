package com.rio.rostry.utils.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface FlowAnalyticsTracker {
    // Authentication flow events
    fun trackAuthFlowStart()
    fun trackAuthOtpSent(provider: String)
    fun trackAuthOtpVerified(provider: String)
    fun trackAuthOtpFailed(provider: String, reason: String)
    fun trackAuthCompleted(provider: String, userType: String)
    
    // Onboarding flow events
    fun trackOnboardingRoleSelected(role: String)
    fun trackOnboardingChecklistProgress(item: String, completed: Boolean)
    fun trackOnboardingCompleted()
    fun trackOnboardingFunnelStep(step: String, role: String)
    fun trackTimeToFirstValue(durationSeconds: Long, role: String, valueType: String)
    fun trackOnboardingDropOff(step: String, role: String, reason: String?)
    
    // Guest mode events
    fun trackGuestModeStarted(role: String)
    fun trackGuestModeUpgraded(role: String, durationSeconds: Long)
    fun trackGuestModeAbandoned(role: String, durationSeconds: Long)
    
    // Feature adoption events
    fun trackFeatureFirstUse(feature: String, daysSinceInstall: Int)
    fun trackFeatureAdoption(feature: String, adopted: Boolean, daysSinceInstall: Int)
    
    // Profile flow events
    fun trackProfileViewed()
    fun trackProfileEdited(field: String)
    fun trackProfileVerificationStarted(type: String)
    
    // Role upgrade flow events
    fun trackRoleUpgradeStarted(currentRole: String, targetRole: String)
    fun trackRoleUpgradeStarted(currentRole: com.rio.rostry.domain.model.UserType, targetRole: com.rio.rostry.domain.model.UserType)
    fun trackRoleUpgradePrerequisiteChecked(prerequisite: String, passed: Boolean)
    fun trackRoleUpgradePrerequisiteCheck(targetRole: String, passed: Boolean, failingKeys: Collection<String>)
    fun trackRoleUpgradePrerequisiteCheck(targetRole: com.rio.rostry.domain.model.UserType, passed: Boolean, failingKeys: Collection<String>)
    fun trackRoleUpgradeCompleted(newRole: String)
    fun trackRoleUpgradeCompleted(previousRole: com.rio.rostry.domain.model.UserType, newRole: com.rio.rostry.domain.model.UserType)
    fun trackRoleUpgradeFailed(role: String, reason: String)
    fun trackRoleUpgradeFixAction(field: String)
    
    // Farm monitoring flow events
    fun trackFarmMonitoringModuleOpened(module: String)
    fun trackFarmMonitoringRecordCreated(module: String, recordId: String)
    fun trackFarmMonitoringTaskCompleted(module: String, taskId: String)
    
    // Marketplace flow events
    fun trackMarketplaceBrowsed(category: String?)
    fun trackMarketplaceFiltered(filter: String, value: String?)
    fun trackMarketplaceProductViewed(productId: String)
    fun trackMarketplaceAddedToCart(productId: String)
    fun trackMarketplaceCheckoutStarted()
    
    // Transfer flow events
    fun trackTransferCreated(transferId: String, productId: String)
    fun trackTransferVerified(transferId: String)
    fun trackTransferCompleted(transferId: String)
    fun trackTransferDisputed(transferId: String, reason: String)
    
    // Breeding flow events
    fun trackBreedingPairCreated(pairId: String)
    fun trackBreedingEggsCollected(pairId: String, count: Int)
    fun trackBreedingHatched(batchId: String, count: Int)
    
    // Address management flow events
    fun trackAddressManagementOpened()
    fun trackAddressAdded()
    fun trackAddressUpdated()
    fun trackAddressDeleted()
    fun trackDefaultAddressSet()
    
    // User properties
    fun setUserRole(role: String)
    fun setUserVerificationStatus(status: String)
    fun setUserEngagementLevel(level: String)

    // Generic event tracking
    fun trackEvent(event: String, properties: Map<String, Any?> = emptyMap())
}

@Singleton
class FlowAnalyticsTrackerImpl @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : FlowAnalyticsTracker {

    private val scope = CoroutineScope(Dispatchers.IO)
    
    // Event batching queue for offline support
    private val eventQueue = mutableListOf<QueuedEvent>()
    
    private data class QueuedEvent(val event: String, val params: Bundle, val timestamp: Long)
    
    private fun log(event: String, params: Bundle.() -> Unit) {
        val bundle = Bundle().apply(params).apply {
            putLong("timestamp", System.currentTimeMillis())
        }
        
        // Queue for offline handling
        eventQueue.add(QueuedEvent(event, bundle, System.currentTimeMillis()))
        
        // Process queue in background
        scope.launch {
            processEventQueue()
        }
        
        // Log immediately if online
        try {
            firebaseAnalytics.logEvent(event, bundle)
            Timber.tag(TAG).d("Tracked event: $event")
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to log event: $event")
        }
    }

    override fun trackEvent(event: String, properties: Map<String, Any?>) {
        log(event) {
            properties.forEach { (k, v) ->
                when (v) {
                    null -> { /* skip */ }
                    is String -> putString(k, v)
                    is Boolean -> putBoolean(k, v)
                    is Int -> putInt(k, v)
                    is Long -> putLong(k, v)
                    is Double -> putDouble(k, v)
                    is Float -> putDouble(k, v.toDouble())
                    else -> putString(k, v.toString())
                }
            }
        }
    }
    
    private suspend fun processEventQueue() {
        // In a real implementation, this would check connectivity and retry failed events
        // For now, just clear processed events
        if (eventQueue.size > 100) { // Prevent memory issues
            eventQueue.removeAll { System.currentTimeMillis() - it.timestamp > 24 * 60 * 60 * 1000 } // Remove old events
        }
    }
    
    private fun setUserProperty(key: String, value: String) {
        try {
            firebaseAnalytics.setUserProperty(key, value)
            Timber.tag(TAG).d("Set user property: $key = $value")
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to set user property: $key")
        }
    }

    // Authentication flow events
    override fun trackAuthFlowStart() = log("flow_auth_start") {}

    override fun trackAuthOtpSent(provider: String) = log("flow_auth_otp_sent") {
        putString("provider", provider)
    }

    override fun trackAuthOtpVerified(provider: String) = log("flow_auth_otp_verified") {
        putString("provider", provider)
    }

    override fun trackAuthOtpFailed(provider: String, reason: String) = log("flow_auth_otp_failed") {
        putString("provider", provider)
        putString("reason", reason)
    }

    override fun trackAuthCompleted(provider: String, userType: String) = log("flow_auth_completed") {
        putString("provider", provider)
        putString("user_type", userType)
    }

    // Onboarding flow events
    override fun trackOnboardingRoleSelected(role: String) = log("flow_onboarding_role_selected") {
        putString("role", role)
    }

    override fun trackOnboardingChecklistProgress(item: String, completed: Boolean) = log("flow_onboarding_checklist_progress") {
        putString("item", item)
        putBoolean("completed", completed)
    }

    override fun trackOnboardingCompleted() = log("flow_onboarding_completed") {}

    override fun trackOnboardingFunnelStep(step: String, role: String) = log("flow_onboarding_funnel_step") {
        putString("step", step)
        putString("role", role)
    }

    override fun trackTimeToFirstValue(durationSeconds: Long, role: String, valueType: String) = log("flow_time_to_first_value") {
        putLong("duration_seconds", durationSeconds)
        putString("role", role)
        putString("value_type", valueType)
    }

    override fun trackOnboardingDropOff(step: String, role: String, reason: String?) = log("flow_onboarding_drop_off") {
        putString("step", step)
        putString("role", role)
        reason?.let { putString("reason", it) }
    }

    // Guest mode events
    override fun trackGuestModeStarted(role: String) = log("flow_guest_mode_started") {
        putString("role", role)
    }

    override fun trackGuestModeUpgraded(role: String, durationSeconds: Long) = log("flow_guest_mode_upgraded") {
        putString("role", role)
        putLong("duration_seconds", durationSeconds)
    }

    override fun trackGuestModeAbandoned(role: String, durationSeconds: Long) = log("flow_guest_mode_abandoned") {
        putString("role", role)
        putLong("duration_seconds", durationSeconds)
    }

    // Feature adoption events
    override fun trackFeatureFirstUse(feature: String, daysSinceInstall: Int) = log("flow_feature_first_use") {
        putString("feature", feature)
        putInt("days_since_install", daysSinceInstall)
    }

    override fun trackFeatureAdoption(feature: String, adopted: Boolean, daysSinceInstall: Int) {
        log("flow_feature_adoption") {
            putString("feature", feature)
            putBoolean("adopted", adopted)
            putInt("days_since_install", daysSinceInstall)
        }
        setUserProperty("feature_${feature}_adopted", adopted.toString())
    }

    // Profile flow events
    override fun trackProfileViewed() = log("flow_profile_viewed") {}

    override fun trackProfileEdited(field: String) = log("flow_profile_edited") {
        putString("field", field)
    }

    override fun trackProfileVerificationStarted(type: String) = log("flow_profile_verification_started") {
        putString("type", type)
    }

    // Role upgrade flow events
    override fun trackRoleUpgradeStarted(currentRole: String, targetRole: String) = log("flow_role_upgrade_started") {
        putString("current_role", currentRole)
        putString("target_role", targetRole)
    }

    override fun trackRoleUpgradeStarted(currentRole: com.rio.rostry.domain.model.UserType, targetRole: com.rio.rostry.domain.model.UserType) =
        trackRoleUpgradeStarted(currentRole.name, targetRole.name)

    override fun trackRoleUpgradePrerequisiteChecked(prerequisite: String, passed: Boolean) = log("flow_role_upgrade_prerequisite") {
        putString("prerequisite", prerequisite)
        putBoolean("passed", passed)
    }

    override fun trackRoleUpgradePrerequisiteCheck(targetRole: String, passed: Boolean, failingKeys: Collection<String>) {
        // Summary event
        log("flow_role_upgrade_prereq_check") {
            putString("target_role", targetRole)
            putBoolean("passed", passed)
            putInt("failing_count", failingKeys.size)
        }
        // Log individual failing keys for detailed analytics
        failingKeys.forEach { key ->
            trackRoleUpgradePrerequisiteChecked(key, false)
        }
    }

    override fun trackRoleUpgradePrerequisiteCheck(targetRole: com.rio.rostry.domain.model.UserType, passed: Boolean, failingKeys: Collection<String>) =
        trackRoleUpgradePrerequisiteCheck(targetRole.name, passed, failingKeys)

    override fun trackRoleUpgradeCompleted(newRole: String) = log("flow_role_upgrade_completed") {
        putString("new_role", newRole)
    }

    override fun trackRoleUpgradeCompleted(previousRole: com.rio.rostry.domain.model.UserType, newRole: com.rio.rostry.domain.model.UserType) =
        trackRoleUpgradeCompleted(newRole.name)

    override fun trackRoleUpgradeFailed(role: String, reason: String) = log("flow_role_upgrade_failed") {
        putString("role", role)
        putString("reason", reason)
    }

    override fun trackRoleUpgradeFixAction(field: String) = log("flow_role_upgrade_fix_action") {
        putString("field", field)
    }

    // Farm monitoring flow events
    override fun trackFarmMonitoringModuleOpened(module: String) = log("flow_farm_monitoring_module_opened") {
        putString("module", module)
    }

    override fun trackFarmMonitoringRecordCreated(module: String, recordId: String) = log("flow_farm_monitoring_record_created") {
        putString("module", module)
        putString("record_id", recordId)
    }

    override fun trackFarmMonitoringTaskCompleted(module: String, taskId: String) = log("flow_farm_monitoring_task_completed") {
        putString("module", module)
        putString("task_id", taskId)
    }

    // Marketplace flow events
    override fun trackMarketplaceBrowsed(category: String?) = log("flow_marketplace_browsed") {
        category?.let { putString("category", it) }
    }

    override fun trackMarketplaceFiltered(filter: String, value: String?) = log("flow_marketplace_filtered") {
        putString("filter", filter)
        value?.let { putString("value", it) }
    }

    override fun trackMarketplaceProductViewed(productId: String) = log("flow_marketplace_product_viewed") {
        putString("product_id", productId)
    }

    override fun trackMarketplaceAddedToCart(productId: String) = log("flow_marketplace_added_to_cart") {
        putString("product_id", productId)
    }

    override fun trackMarketplaceCheckoutStarted() = log("flow_marketplace_checkout_started") {}

    // Transfer flow events
    override fun trackTransferCreated(transferId: String, productId: String) = log("flow_transfer_created") {
        putString("transfer_id", transferId)
        putString("product_id", productId)
    }

    override fun trackTransferVerified(transferId: String) = log("flow_transfer_verified") {
        putString("transfer_id", transferId)
    }

    override fun trackTransferCompleted(transferId: String) = log("flow_transfer_completed") {
        putString("transfer_id", transferId)
    }

    override fun trackTransferDisputed(transferId: String, reason: String) = log("flow_transfer_disputed") {
        putString("transfer_id", transferId)
        putString("reason", reason)
    }

    // Breeding flow events
    override fun trackBreedingPairCreated(pairId: String) = log("flow_breeding_pair_created") {
        putString("pair_id", pairId)
    }

    override fun trackBreedingEggsCollected(pairId: String, count: Int) = log("flow_breeding_eggs_collected") {
        putString("pair_id", pairId)
        putInt("count", count)
    }

    override fun trackBreedingHatched(batchId: String, count: Int) = log("flow_breeding_hatched") {
        putString("batch_id", batchId)
        putInt("count", count)
    }

    // Address management flow events
    override fun trackAddressManagementOpened() = log("flow_address_management_opened") {}

    override fun trackAddressAdded() = log("flow_address_added") {}

    override fun trackAddressUpdated() = log("flow_address_updated") {}

    override fun trackAddressDeleted() = log("flow_address_deleted") {}

    override fun trackDefaultAddressSet() = log("flow_default_address_set") {}

    // User properties
    override fun setUserRole(role: String) = setUserProperty("user_role", role)

    override fun setUserVerificationStatus(status: String) = setUserProperty("verification_status", status)

    override fun setUserEngagementLevel(level: String) = setUserProperty("engagement_level", level)

    private companion object {
        private const val TAG = "FlowAnalytics"
    }
}
