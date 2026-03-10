package com.rio.rostry.ui.farmer.asset

import androidx.annotation.Keep

@Keep
data class TagGroup(
    val id: String = java.util.UUID.randomUUID().toString(),
    val prefix: String = "",
    val startNumber: Int = 1,
    val count: Int = 1,
    val gender: String = "Unknown", // MALE, FEMALE
    val color: String = "Red"
) {
    val rangeEnd: Int get() = startNumber + count - 1
    val label: String get() = "$count $gender ($color Tag $prefix$startNumber-$rangeEnd)"
    
    // Compatibility alias if needed, or just redundant
    val rangeStart: Int get() = startNumber
}
