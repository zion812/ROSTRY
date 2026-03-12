package com.rio.rostry.core.common.analytics

/**
 * Domain interface for enthusiast-specific analytics tracking.
 *
 * Tracks breeding lifecycle events, transfer verifications,
 * family tree interactions, and competition RSVPs specific
 * to the Enthusiast persona.
 */
interface EnthusiastAnalyticsTracker {

    fun trackFetcherCardTap(cardName: String, userId: String? = null)
    fun trackPairCreate(pairId: String, maleProductId: String, femaleProductId: String, userId: String? = null)
    fun trackMatingLogAdd(pairId: String, userId: String? = null)
    fun trackEggCollect(pairId: String, count: Int, userId: String? = null)
    fun trackIncubationStart(batchId: String, eggsCount: Int, userId: String? = null)
    fun trackHatchLogAdd(batchId: String, eventType: String, userId: String? = null)
    fun trackFamilyTreeNodeOpen(productId: String, depth: Int, userId: String? = null)
    fun trackTransferVerifyStep(transferId: String, step: String, userId: String? = null)
    fun trackDisputeOpen(transferId: String, reason: String, userId: String? = null)
    fun trackEventRsvp(eventId: String, status: String, userId: String? = null)

    // Breeding lifecycle funnel
    fun trackBreedingLifecycleStart(pairId: String, userId: String? = null)
    fun trackBreedingLifecycleStep(pairId: String, step: String, userId: String? = null)
    fun trackBreedingLifecycleComplete(pairId: String, userId: String? = null)

    // Transfer verification funnel
    fun trackTransferVerificationStart(transferId: String, userId: String? = null)
    fun trackTransferVerificationStepFunnel(transferId: String, step: String, userId: String? = null)
    fun trackTransferVerificationComplete(transferId: String, userId: String? = null)
}
