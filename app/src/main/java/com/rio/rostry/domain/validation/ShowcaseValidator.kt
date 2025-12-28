package com.rio.rostry.domain.validation

import com.rio.rostry.domain.model.UserType

/**
 * Validates showcase operations based on user role and current slot usage.
 * 
 * Farmers: Maximum 3 showcase birds (forces quality selection)
 * Enthusiasts: Unlimited showcase (brand building)
 */
object ShowcaseValidator {
    
    private const val FARMER_MAX_SLOTS = 3
    
    /**
     * Check if user can add another bird to showcase.
     */
    fun canAddToShowcase(role: UserType, currentShowcaseCount: Int): Boolean {
        return when (role) {
            UserType.FARMER -> currentShowcaseCount < FARMER_MAX_SLOTS
            UserType.ENTHUSIAST -> true // Unlimited
            UserType.GENERAL -> false // Cannot showcase
        }
    }
    
    /**
     * Get the maximum showcase slots for a role.
     */
    fun getMaxSlots(role: UserType): Int {
        return when (role) {
            UserType.FARMER -> FARMER_MAX_SLOTS
            UserType.ENTHUSIAST -> Int.MAX_VALUE
            UserType.GENERAL -> 0
        }
    }
    
    /**
     * Get remaining slots for a user.
     */
    fun getRemainingSlots(role: UserType, currentShowcaseCount: Int): Int {
        val max = getMaxSlots(role)
        return if (max == Int.MAX_VALUE) Int.MAX_VALUE else (max - currentShowcaseCount).coerceAtLeast(0)
    }
    
    /**
     * Get upgrade message when slots are exhausted.
     */
    fun getUpgradeMessage(role: UserType): String? {
        return when (role) {
            UserType.FARMER -> "Upgrade to Enthusiast for unlimited showcase birds"
            UserType.GENERAL -> "Become a verified Farmer to showcase your birds"
            UserType.ENTHUSIAST -> null
        }
    }
}
