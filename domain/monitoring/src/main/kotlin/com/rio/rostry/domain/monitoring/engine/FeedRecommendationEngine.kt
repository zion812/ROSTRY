package com.rio.rostry.domain.monitoring.engine

import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.model.FeedRecommendation
import com.rio.rostry.domain.model.FeedType
import com.rio.rostry.domain.model.LifecycleStage

interface FeedRecommendationEngine {
    fun calculateRecommendation(
        products: List<ProductEntity>,
        availableInventoryKg: Double? = null
    ): FeedRecommendation?
    
    fun calculateForStage(
        stage: LifecycleStage,
        birdCount: Int,
        breedName: String? = null,
        availableInventoryKg: Double? = null
    ): FeedRecommendation
    
    fun getFeedEmoji(feedType: FeedType): String
    
    fun formatAmount(kg: Double): String
}
