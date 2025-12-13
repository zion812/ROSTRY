package com.rio.rostry.marketplace.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Product categorization for marketplace.
 * - MEAT: Independent products for consumption
 * - ADOPTION: Live birds for keeping/breeding
 *   - TRACEABLE: Requires family tree documentation
 *   - NON_TRACEABLE: Standalone listings without lineage
 */
@Serializable
sealed class ProductCategory {
    @Serializable
    @SerialName("MEAT")
    data object Meat : ProductCategory()

    @Serializable
    @SerialName("ADOPTION_TRACEABLE")
    data object AdoptionTraceable : ProductCategory()

    @Serializable
    @SerialName("ADOPTION_NON_TRACEABLE")
    data object AdoptionNonTraceable : ProductCategory()

    @Serializable
    @SerialName("STARTER_KIT")
    data object StarterKit : ProductCategory()

    companion object {
        fun fromString(value: String?): ProductCategory? = when (value?.uppercase()) {
            "MEAT" -> Meat
            "ADOPTION_TRACEABLE", "ADOPTION-TRACEABLE", "TRACEABLE" -> AdoptionTraceable
            "ADOPTION_NON_TRACEABLE", "ADOPTION-NON-TRACEABLE", "NON_TRACEABLE", "NON-TRACEABLE" -> AdoptionNonTraceable
            "STARTER_KIT", "STARTER-KIT", "KIT" -> StarterKit
            else -> null
        }

        fun toString(category: ProductCategory?): String? = when (category) {
            is Meat -> "MEAT"
            is AdoptionTraceable -> "ADOPTION_TRACEABLE"
            is AdoptionNonTraceable -> "ADOPTION_NON_TRACEABLE"
            is StarterKit -> "STARTER_KIT"
            null -> null
        }
    }
}
