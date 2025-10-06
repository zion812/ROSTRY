package com.rio.rostry.utils.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class EnthusiastAnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    private fun log(event: String, params: Bundle.() -> Unit) {
        val b = Bundle().apply(params)
        firebaseAnalytics.logEvent(event, b)
    }

    fun trackFetcherCardTap(cardName: String, userId: String? = null) = log("enthusiast_fetcher_card_tap") {
        putString("card_name", cardName)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackPairCreate(pairId: String, maleProductId: String, femaleProductId: String, userId: String? = null) = log("pair_create") {
        putString("pair_id", pairId)
        putString("male_id", maleProductId)
        putString("female_id", femaleProductId)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackMatingLogAdd(pairId: String, userId: String? = null) = log("mating_log_add") {
        putString("pair_id", pairId)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackEggCollect(pairId: String, count: Int, userId: String? = null) = log("egg_collect") {
        putString("pair_id", pairId)
        putInt("count", count)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackIncubationStart(batchId: String, eggsCount: Int, userId: String? = null) = log("incubation_start") {
        putString("batch_id", batchId)
        putInt("eggs_count", eggsCount)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackHatchLogAdd(batchId: String, eventType: String, userId: String? = null) = log("hatch_log_add") {
        putString("batch_id", batchId)
        putString("event_type", eventType)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackFamilyTreeNodeOpen(productId: String, depth: Int, userId: String? = null) = log("family_tree_node_open") {
        putString("product_id", productId)
        putInt("depth", depth)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackTransferVerifyStep(transferId: String, step: String, userId: String? = null) = log("transfer_verify_step") {
        putString("transfer_id", transferId)
        putString("step", step)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackDisputeOpen(transferId: String, reason: String, userId: String? = null) = log("dispute_open") {
        putString("transfer_id", transferId)
        putString("reason", reason)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackEventRsvp(eventId: String, status: String, userId: String? = null) = log("event_rsvp") {
        putString("event_id", eventId)
        putString("status", status)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    // Funnels
    fun trackBreedingLifecycleStart(pairId: String, userId: String? = null) = log("breeding_lifecycle_start") {
        putString("pair_id", pairId)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackBreedingLifecycleStep(pairId: String, step: String, userId: String? = null) = log("breeding_lifecycle_step") {
        putString("pair_id", pairId)
        putString("step", step)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackBreedingLifecycleComplete(pairId: String, userId: String? = null) = log("breeding_lifecycle_complete") {
        putString("pair_id", pairId)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackTransferVerificationStart(transferId: String, userId: String? = null) = log("transfer_verification_start") {
        putString("transfer_id", transferId)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackTransferVerificationStepFunnel(transferId: String, step: String, userId: String? = null) = log("transfer_verification_step") {
        putString("transfer_id", transferId)
        putString("step", step)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }

    fun trackTransferVerificationComplete(transferId: String, userId: String? = null) = log("transfer_verification_complete") {
        putString("transfer_id", transferId)
        userId?.let { putString("user_id", it) }
        putLong("ts", System.currentTimeMillis())
    }
}
