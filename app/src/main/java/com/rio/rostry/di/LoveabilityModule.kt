package com.rio.rostry.di

import com.rio.rostry.ai.PersonalizationService
import com.rio.rostry.ai.RecommendationEngine
import com.rio.rostry.community.CommunityEngagementService
import com.rio.rostry.feedback.UserFeedbackSystem
import com.rio.rostry.gamification.AchievementSystem
import com.rio.rostry.insights.SmartInsightsEngine
import com.rio.rostry.notifications.IntelligentNotificationService
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Many of these services have @Inject constructors and @Singleton annotations and
// therefore don't require explicit @Provides bindings. We still expose a module
// to group loveable-product feature wiring and future bindings.
@Module
@InstallIn(SingletonComponent::class)
object LoveabilityModule {
    // Intentionally empty for now; DI will resolve via @Inject constructors.
    // Add @Provides bindings here in future if implementations change.
}
