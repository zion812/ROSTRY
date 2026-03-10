package com.rio.rostry.core.model

/**
 * Recommendation model for personalized and trending content.
 */
data class Recommendation(
    val id: String,
    val title: String,
    val score: Double,
    val kind: Kind,
    val reason: String? = null,
    val imageUrl: String? = null,
    val price: Double? = null,
    val sellerRating: Double? = null,
    val type: RecommendationType = RecommendationType.PERSONALIZED
) {
    enum class Kind { PRODUCT, POST, TIP }

    enum class RecommendationType {
        COLLABORATIVE, CONTENT_BASED, TRENDING, PERSONALIZED
    }
}
