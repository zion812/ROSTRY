package com.rio.rostry.ui.social.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.core.navigation.NavigationRoute

/**
 * Navigation routes for social feature.
 */
sealed class SocialRoute(override val route: String) : NavigationRoute {
    object Feed : SocialRoute("social/feed")
    object Groups : SocialRoute("social/groups")
    object Events : SocialRoute("social/events")
    object Expert : SocialRoute("social/expert")
    object Moderation : SocialRoute("social/moderation")
    object ModerationVerifications : SocialRoute("moderation/verifications")
    object Leaderboard : SocialRoute("social/leaderboard")
    object Live : SocialRoute("social/live")
    object StoryViewer : SocialRoute("social/story/viewer/{initialIndex}") {
        fun createRoute(initialIndex: String) = "social/story/viewer/$initialIndex"
    }
    object StoryCreator : SocialRoute("social/story/creator")
    object DiscussionDetail : SocialRoute("social/discussion/{postId}") {
        fun createRoute(postId: String) = "social/discussion/$postId"
    }
}

/**
 * Navigation routes for messaging feature.
 */
sealed class MessagingRoute(override val route: String) : NavigationRoute {
    object Thread : MessagingRoute("messages/{threadId}") {
        fun createRoute(threadId: String) = "messages/$threadId"
    }
    object Group : MessagingRoute("group/{groupId}/chat") {
        fun createRoute(groupId: String) = "group/$groupId/chat"
    }
    object Outbox : MessagingRoute("messages/outbox")
}

/**
 * Navigation provider for social feature.
 */
class SocialNavigationStubProvider : NavigationProvider {
    override val featureId: String = "social"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(SocialRoute.Feed.route) {
                // TODO: Connect to SocialFeedScreen
            }

            composable(SocialRoute.Groups.route) {
                // TODO: Connect to GroupsScreen
            }

            composable(SocialRoute.Events.route) {
                // TODO: Connect to EventsScreen
            }

            composable(SocialRoute.Expert.route) {
                // TODO: Connect to ExpertScreen
            }

            composable(SocialRoute.Moderation.route) {
                // TODO: Connect to ModerationScreen
            }

            composable(SocialRoute.ModerationVerifications.route) {
                // TODO: Connect to ModerationVerificationsScreen
            }

            composable(SocialRoute.Leaderboard.route) {
                // TODO: Connect to LeaderboardScreen
            }

            composable(SocialRoute.Live.route) {
                // TODO: Connect to LiveScreen
            }

            composable(SocialRoute.StoryViewer.route) { backStackEntry ->
                val initialIndex = backStackEntry.arguments?.getString("initialIndex") ?: "0"
                // TODO: Connect to StoryViewerScreen
            }

            composable(SocialRoute.StoryCreator.route) {
                // TODO: Connect to StoryCreatorScreen
            }

            composable(SocialRoute.DiscussionDetail.route) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                // TODO: Connect to DiscussionDetailScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://social",
        "https://rostry.app/social"
    )
}

/**
 * Navigation provider for messaging feature.
 */
class MessagingNavigationProvider : NavigationProvider {
    override val featureId: String = "messaging"

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            composable(MessagingRoute.Thread.route) { backStackEntry ->
                val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
                // TODO: Connect to ThreadScreen
            }

            composable(MessagingRoute.Group.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                // TODO: Connect to GroupChatScreen
            }

            composable(MessagingRoute.Outbox.route) {
                // TODO: Connect to OutboxScreen
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://messages",
        "https://rostry.app/messages"
    )
}

