package com.rio.rostry.di

import com.rio.rostry.ui.general.analytics.GeneralAnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

object TestMarketModule {

    fun provideAnalytics(): GeneralAnalyticsTracker = object : GeneralAnalyticsTracker {
        override fun navTabClick(tabRoute: String) {}
        override fun createEntryOpen() {}
        override fun marketFilterApply(filterKey: String, filterValue: String?) {}
        override fun offlineBannerSeen(context: String) {}
        override fun trendingSectionViewed() {}
        override fun trendingProductClicked(productId: String) {}
        override fun recommendationClicked(productId: String, reason: String) {}
        override fun wishlistToggled(productId: String, added: Boolean) {}
        override fun buyNowClicked(productId: String) {}
        override fun productViewTracked(productId: String, durationSeconds: Int) {}
        override fun savedForLater(productId: String) {}
        override fun cartRecommendationsShown(productIds: List<String>) {}
        override fun trendingSectionClicked(postId: String) {}
        override fun hashtagClicked(hashtag: String) {}
        override fun postViewTracked(postId: String, durationSeconds: Int) {}
        override fun doubleTapLike(postId: String) {}
        override fun postSaved(postId: String) {}
        override fun engagementMetricsViewed(postId: String) {}
        override fun personalizedFeedShown(userId: String, itemCount: Int) {}
        override fun personalizationAccuracy(userId: String, clicked: Boolean) {}
    }
}
