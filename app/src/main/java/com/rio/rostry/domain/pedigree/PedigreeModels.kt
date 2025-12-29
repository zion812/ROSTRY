package com.rio.rostry.domain.pedigree

import com.rio.rostry.data.database.entity.ProductEntity

/**
 * Represents a bird in the pedigree tree.
 * Can be either a real ProductEntity or a "Guest Parent" from external source.
 */
data class PedigreeBird(
    val id: String,
    val name: String,
    val breed: String?,
    val color: String?,
    val imageUrl: String?,
    val birthDate: Long?,
    val isGuestParent: Boolean = false,
    val source: String? = null // For guest parents: "Purchased from X"
) {
    companion object {
        fun fromProduct(product: ProductEntity): PedigreeBird = PedigreeBird(
            id = product.productId,
            name = product.name,
            breed = product.breed,
            color = product.color,
            imageUrl = product.imageUrls.firstOrNull(),
            birthDate = product.birthDate,
            isGuestParent = false
        )
    }
}

/**
 * Recursive pedigree tree structure.
 * Each node can have a sire (father) and dam (mother) branch.
 */
data class PedigreeTree(
    val bird: PedigreeBird,
    val sire: PedigreeTree?,  // Father line (parentMaleId)
    val dam: PedigreeTree?,   // Mother line (parentFemaleId)
    val generation: Int = 0   // 0 = root, 1 = parents, 2 = grandparents, etc.
) {
    /**
     * Calculate pedigree completeness based on how many ancestors are documented.
     */
    val completeness: PedigreeCompleteness
        get() {
            val hasParents = sire != null || dam != null
            val hasGrandparents = (sire?.sire != null || sire?.dam != null) ||
                                   (dam?.sire != null || dam?.dam != null)
            val hasGreatGrandparents = listOfNotNull(
                sire?.sire?.sire, sire?.sire?.dam, sire?.dam?.sire, sire?.dam?.dam,
                dam?.sire?.sire, dam?.sire?.dam, dam?.dam?.sire, dam?.dam?.dam
            ).isNotEmpty()
            
            return when {
                hasGreatGrandparents -> PedigreeCompleteness.THREE_GEN
                hasGrandparents -> PedigreeCompleteness.TWO_GEN
                hasParents -> PedigreeCompleteness.ONE_GEN
                else -> PedigreeCompleteness.NONE
            }
        }
    
    /**
     * Count total documented ancestors in the tree.
     */
    fun countAncestors(): Int {
        val sireCount = sire?.let { 1 + it.countAncestors() } ?: 0
        val damCount = dam?.let { 1 + it.countAncestors() } ?: 0
        return sireCount + damCount
    }
}

/**
 * Indicates how complete a bird's pedigree documentation is.
 * Higher completeness = higher market value for Enthusiasts.
 */
enum class PedigreeCompleteness(val label: String, val generations: Int) {
    NONE("No Pedigree", 0),
    ONE_GEN("1-Gen Pedigree", 1),
    TWO_GEN("2-Gen Pedigree", 2),
    THREE_GEN("3-Gen Pedigree", 3);
    
    val badgeColor: Long
        get() = when (this) {
            NONE -> 0xFF9E9E9E       // Gray
            ONE_GEN -> 0xFFFF9800    // Orange
            TWO_GEN -> 0xFF4CAF50    // Green
            THREE_GEN -> 0xFFFFD700  // Gold
        }
}
