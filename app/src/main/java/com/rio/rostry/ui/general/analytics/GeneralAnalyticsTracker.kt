package com.rio.rostry.ui.general.analytics

import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

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

@Singleton
class GeneralAnalyticsTrackerImpl @Inject constructor() : GeneralAnalyticsTracker {
    override fun navTabClick(tabRoute: String) {
        Timber.tag(TAG).d("nav_tab_click route=%s", tabRoute)
    }

    override fun createEntryOpen() {
        Timber.tag(TAG).d("create_entry_open")
    }

    override fun marketFilterApply(filterKey: String, filterValue: String?) {
        Timber.tag(TAG).d("market_filter_apply key=%s value=%s", filterKey, filterValue)
    }

    override fun offlineBannerSeen(context: String) {
        Timber.tag(TAG).d("offline_banner_seen context=%s", context)
    }

    // Marketplace analytics implementations
    override fun trendingSectionViewed() {
        Timber.tag(TAG).d("marketplace_trending_viewed")
    }

    override fun trendingProductClicked(productId: String) {
        Timber.tag(TAG).d("marketplace_trending_clicked productId=%s", productId)
    }

    override fun recommendationClicked(productId: String, reason: String) {
        Timber.tag(TAG).d("marketplace_recommendation_clicked productId=%s reason=%s", productId, reason)
    }

    override fun wishlistToggled(productId: String, added: Boolean) {
        Timber.tag(TAG).d("marketplace_wishlist_toggled productId=%s added=%s", productId, added)
    }

    override fun buyNowClicked(productId: String) {
        Timber.tag(TAG).d("marketplace_buy_now_clicked productId=%s", productId)
    }

    override fun productViewTracked(productId: String, durationSeconds: Int) {
        Timber.tag(TAG).d("marketplace_product_view productId=%s duration=%d", productId, durationSeconds)
    }

    override fun savedForLater(productId: String) {
        Timber.tag(TAG).d("marketplace_saved_for_later productId=%s", productId)
    }

    override fun cartRecommendationsShown(productIds: List<String>) {
        Timber.tag(TAG).d("marketplace_cart_recommendations_shown count=%d", productIds.size)
    }

    // Social analytics implementations
    override fun trendingSectionClicked(postId: String) {
        Timber.tag(TAG).d("social_trending_clicked postId=%s", postId)
    }

    override fun hashtagClicked(hashtag: String) {
        Timber.tag(TAG).d("social_hashtag_clicked hashtag=%s", hashtag)
    }

    override fun postViewTracked(postId: String, durationSeconds: Int) {
        Timber.tag(TAG).d("social_post_view postId=%s duration=%d", postId, durationSeconds)
    }

    override fun doubleTapLike(postId: String) {
        Timber.tag(TAG).d("social_double_tap_like postId=%s", postId)
    }

    override fun postSaved(postId: String) {
        Timber.tag(TAG).d("social_post_saved postId=%s", postId)
    }

    override fun engagementMetricsViewed(postId: String) {
        Timber.tag(TAG).d("social_engagement_viewed postId=%s", postId)
    }

    // Personalization analytics implementations
    override fun personalizedFeedShown(userId: String, itemCount: Int) {
        Timber.tag(TAG).d("personalization_feed_shown userId=%s count=%d", userId, itemCount)
    }

    override fun personalizationAccuracy(userId: String, clicked: Boolean) {
        Timber.tag(TAG).d("personalization_accuracy userId=%s clicked=%s", userId, clicked)
    }

    private companion object {
        private const val TAG = "GeneralAnalytics"
    }
}
