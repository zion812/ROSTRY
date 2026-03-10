package com.rio.rostry.core.common.analytics

interface GeneralAnalyticsTracker {
    fun navTabClick(tabRoute: String)
    fun createEntryOpen()
    fun marketFilterApply(filterKey: String, filterValue: String?)
    fun offlineBannerSeen(context: String)
    
    // Marketplace analytics
    fun trendingSectionViewed()
    fun trendingProductClicked(productId: String)
    fun recommendationClicked(productId: String, reason: String)
    fun wishlistToggled(productId: String, added: Boolean)
    fun buyNowClicked(productId: String)
    fun productViewTracked(productId: String, durationSeconds: Int)
    fun savedForLater(productId: String)
    fun cartRecommendationsShown(productIds: List<String>)
    
    // Social analytics
    fun trendingSectionClicked(postId: String)
    fun hashtagClicked(hashtag: String)
    fun postViewTracked(postId: String, durationSeconds: Int)
    fun doubleTapLike(postId: String)
    fun postSaved(postId: String)
    fun engagementMetricsViewed(postId: String)
    
    // Personalization analytics
    fun personalizedFeedShown(userId: String, itemCount: Int)
    fun personalizationAccuracy(userId: String, clicked: Boolean)
}
