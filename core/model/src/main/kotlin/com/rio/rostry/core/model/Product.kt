package com.rio.rostry.core.model

/**
 * Domain model representing a product (bird) in the ROSTRY system.
 * 
 * This is the public API model used by feature modules. It is independent
 * of database implementation details and provides a clean, domain-focused
 * representation of a product.
 */
data class Product(
    val id: String,
    val name: String,
    val sellerId: String,
    val category: String,
    val price: Double,
    val quantity: Double = 1.0,
    val unit: String = "piece",
    val currency: String = "USD",
    val description: String?,
    val imageUrls: List<String>,
    
    // Bird-specific fields
    val breed: String?,
    val gender: String?,
    val birthDate: Long?,
    val ageWeeks: Int?,
    val colorTag: String?,
    val birdCode: String?,
    
    // Lifecycle
    val stage: String,
    val lifecycleStatus: String,
    val lastStageTransitionAt: Long?,
    
    // Location
    val latitude: Double?,
    val longitude: Double?,
    val location: String?,
    
    // Traceability
    val familyTreeId: String?,
    val parentMaleId: String?,
    val parentFemaleId: String?,
    val isTraceable: Boolean,
    
    // Verification
    val isVerified: Boolean,
    val verificationLevel: String?,
    
    // QR Code
    val qrCodeUrl: String?,
    
    // Metadata
    val metadata: Map<String, Any>?,
    
    // Record locking
    val recordsLockedAt: Long?,
    val autoLockAfterDays: Int,
    
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Checks if this product is eligible for transfer.
     */
    fun isEligibleForTransfer(): Boolean {
        return lifecycleStatus == "active" && recordsLockedAt == null
    }
    
    /**
     * Checks if records are currently locked.
     */
    fun areRecordsLocked(nowMillis: Long = System.currentTimeMillis()): Boolean {
        // Manual lock
        if (recordsLockedAt != null) return true
        
        // Time-based lock
        val lockThreshold = nowMillis - (autoLockAfterDays * 24L * 60 * 60 * 1000)
        return createdAt < lockThreshold
    }
    
    /**
     * Gets the age in days if birth date is available.
     */
    fun getAgeDays(nowMillis: Long = System.currentTimeMillis()): Int? {
        return birthDate?.let {
            ((nowMillis - it) / (24L * 60 * 60 * 1000)).toInt()
        }
    }
}
