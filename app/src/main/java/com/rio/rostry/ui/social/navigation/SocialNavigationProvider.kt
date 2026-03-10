package com.rio.rostry.ui.social.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rio.rostry.BuildConfig
import com.rio.rostry.core.navigation.NavigationProvider
import com.rio.rostry.ui.common.ErrorScreen
import com.rio.rostry.ui.community.EventDetailScreen
import com.rio.rostry.ui.community.ExpertProfileScreen
import com.rio.rostry.ui.community.GroupDetailScreen
import com.rio.rostry.ui.screens.PlaceholderScreen
import com.rio.rostry.ui.social.LiveBroadcastScreen
import com.rio.rostry.ui.social.discussion.DiscussionDetailScreen
import com.rio.rostry.ui.social.profile.SocialProfileScreen
import com.rio.rostry.ui.social.stories.StoryCreatorScreen
import com.rio.rostry.ui.social.stories.StoryViewerScreen
import java.net.URLEncoder

/**
 * Navigation provider for social feed feature.
 * Handles social feed, messaging, groups, discussions, live broadcasts, stories, and social profiles.
 * 
 * Migrated from AppNavHost - Task 6.1.5
 * Updated in Task 6.2 - Local route definitions added, Routes.kt dependency removed
 * Total routes: 12
 * 
 * Routes included:
 * - SOCIAL_FEED (social feed with optional postId)
 * - STORY_VIEWER (story viewer)
 * - LIVE (live broadcast)
 * - STORY_CREATOR (story creator)
 * - DISCUSSION_DETAIL (discussion detail)
 * - MESSAGES_THREAD (direct messages)
 * - MESSAGES_GROUP (group chat)
 * - GROUPS (groups screen - DEBUG only)
 * - COMMUNITY_HUB (community hub)
 * - GROUP_DETAILS (group detail)
 * - EVENT_DETAILS (event detail)
 * - EXPERT_PROFILE (expert profile)
 * - USER_PROFILE (social profile)
 * - LIVE_BROADCAST (live broadcast alternate route)
 * 
 * NOTE: This provider is in the app module because the screens haven't been migrated
 * to the feature:social-feed module yet. Once screens are migrated, this provider
 * should be moved to feature:social-feed module.
 */
class SocialNavigationProvider : NavigationProvider {
    override val featureId: String = "social"
    
    // Local route definitions (Task 6.2)
    companion object Routes {
        // Social routes
        const val SOCIAL_FEED = "social/feed"
        const val STORY_VIEWER = "social/story/viewer/{initialIndex}"
        const val STORY_CREATOR = "social/story/creator"
        const val DISCUSSION_DETAIL = "social/discussion/{postId}"
        const val LIVE = "social/live"
        const val LIVE_BROADCAST = "social/live"
        const val GROUPS = "social/groups"
        const val EVENTS = "social/events"
        const val EXPERT_BOOKING = "social/expert"
        const val LEADERBOARD = "social/leaderboard"
        const val MODERATION = "social/moderation"
        const val MODERATION_VERIFICATIONS = "moderation/verifications"
        
        // Messaging routes
        const val MESSAGES_THREAD = "messages/{threadId}"
        const val MESSAGES_GROUP = "group/{groupId}/chat"
        const val MESSAGES_OUTBOX = "messages/outbox"
        
        // Community Hub routes
        const val COMMUNITY_HUB = "community/hub"
        const val GROUP_DETAILS = "community/group/{groupId}"
        const val EVENT_DETAILS = "community/event/{eventId}"
        const val EXPERT_PROFILE = "community/expert/{expertId}"
        
        // User profile routes
        const val USER_PROFILE = "user/{userId}"
        const val PROFILE_EDIT = "user/edit"
        
        // Builder functions
        fun storyViewer(initialIndex: Int) = "social/story/viewer/$initialIndex"
        fun storyCreator() = STORY_CREATOR
        fun discussionDetail(postId: String) = "social/discussion/${URLEncoder.encode(postId, "UTF-8")}"
        fun messagesThread(threadId: String) = "messages/${URLEncoder.encode(threadId, "UTF-8")}"
        fun userProfile(userId: String) = "user/${URLEncoder.encode(userId, "UTF-8")}"
        fun createGroupRoute(groupId: String) = "community/group/$groupId"
        fun createEventRoute(eventId: String) = "community/event/$eventId"
        fun createExpertRoute(expertId: String) = "community/expert/$expertId"
        fun productDetails(id: String) = "product/${URLEncoder.encode(id, "UTF-8")}"
    }

    override fun buildGraph(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.apply {
            // ============ Social Feed Routes ============
            
            // Social Feed Screen with optional postId
            composable(
                route = SOCIAL_FEED + "?postId={postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType; nullable = true; defaultValue = null }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "rostry://social/feed" },
                    navDeepLink { uriPattern = "rostry://social/feed?postId={postId}" }
                )
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")
                com.rio.rostry.ui.social.SocialFeedScreen(
                    onOpenThread = { threadId -> navController.navigate(Routes.discussionDetail(threadId)) },
                    onOpenGroups = {
                        if (BuildConfig.DEBUG) navController.navigate(GROUPS)
                        else navController.navigate(LEADERBOARD)
                    },
                    onOpenEvents = { navController.navigate(EVENTS) },
                    onOpenExpert = { navController.navigate(EXPERT_BOOKING) },
                    onOpenProfile = { userId -> navController.navigate(Routes.userProfile(userId)) },
                    onOpenStoryViewer = { index -> navController.navigate(Routes.storyViewer(index)) },
                    onOpenStoryCreator = { navController.navigate(Routes.storyCreator()) }
                )
            }
            
            // ============ Stories Routes ============
            
            // Story Viewer Screen
            composable(
                route = STORY_VIEWER,
                arguments = listOf(navArgument("initialIndex") { type = NavType.IntType })
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("initialIndex") ?: 0
                StoryViewerScreen(
                    initialIndex = index,
                    onFinished = { navController.popBackStack() }
                )
            }
            
            // Story Creator Screen
            composable(STORY_CREATOR) {
                StoryCreatorScreen(onBack = { navController.popBackStack() })
            }
            
            // ============ Live Broadcast Routes ============
            
            // Live Broadcast Screen (Social.LIVE)
            composable(LIVE) {
                com.rio.rostry.ui.social.LiveBroadcastScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Live Broadcast Screen (alternate route)
            composable(LIVE_BROADCAST) {
                if (BuildConfig.DEBUG) {
                    LiveBroadcastScreen(onBack = { navController.popBackStack() })
                } else {
                    PlaceholderScreen(title = "Live (Beta)")
                }
            }
            
            // ============ Discussion Routes ============
            
            // Discussion Detail Screen
            composable(
                route = DISCUSSION_DETAIL,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                DiscussionDetailScreen(
                    postId = postId,
                    onBack = { navController.popBackStack() },
                    onProfileClick = { userId -> navController.navigate(Routes.userProfile(userId)) }
                )
            }
            
            // ============ Messaging Routes ============
            
            // Direct Messages Thread Screen
            composable(
                route = MESSAGES_THREAD,
                arguments = listOf(navArgument("threadId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "rostry://messages/{threadId}" })
            ) { backStackEntry ->
                val threadId = backStackEntry.arguments?.getString("threadId") ?: ""
                if (threadId.isBlank()) {
                    android.util.Log.w("SocialNavigationProvider", "Invalid argument for route: $MESSAGES_THREAD")
                    ErrorScreen(message = "Invalid thread ID", onBack = { navController.popBackStack() })
                } else {
                    com.rio.rostry.ui.messaging.ThreadScreen(threadId = threadId, onBack = { navController.popBackStack() })
                }
            }
            
            // Group Chat Screen
            composable(
                route = MESSAGES_GROUP,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
                com.rio.rostry.ui.messaging.GroupChatScreen(groupId = groupId, onBack = { navController.popBackStack() })
            }
            
            // ============ Community Routes ============
            
            // Groups Screen (DEBUG only)
            if (BuildConfig.DEBUG) {
                composable(GROUPS) { 
                    com.rio.rostry.feature.enthusiast.ui.community.GroupsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    ) 
                }
            }
            
            // Community Hub Screen
            composable(COMMUNITY_HUB) {
                val sessionVm: com.rio.rostry.ui.session.SessionViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                val sessionState by sessionVm.uiState.collectAsState()
                val userType = sessionState.role ?: com.rio.rostry.domain.model.UserType.GENERAL
                com.rio.rostry.ui.community.CommunityHubScreen(
                    userType = userType,
                    onNavigateToThread = { threadId -> navController.navigate(Routes.messagesThread(threadId)) },
                    onNavigateToGroup = { groupId -> navController.navigate(Routes.createGroupRoute(groupId)) },
                    onNavigateToEvent = { eventId -> navController.navigate(Routes.createEventRoute(eventId)) },
                    onNavigateToExpert = { expertId -> navController.navigate(Routes.createExpertRoute(expertId)) },
                    onNavigateToPost = { postId -> navController.navigate(Routes.productDetails(postId)) }
                )
            }
            
            // Group Detail Screen
            composable(
                route = GROUP_DETAILS,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                GroupDetailScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Event Detail Screen
            composable(
                route = EVENT_DETAILS,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                EventDetailScreen(
                    eventId = eventId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Expert Profile Screen
            composable(
                route = EXPERT_PROFILE,
                arguments = listOf(navArgument("expertId") { type = NavType.StringType })
            ) { backStackEntry ->
                val expertId = backStackEntry.arguments?.getString("expertId") ?: return@composable
                ExpertProfileScreen(
                    expertId = expertId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // ============ Profile Routes ============
            
            // Social Profile Screen (User Profile)
            composable(
                route = USER_PROFILE,
                arguments = listOf(navArgument("userId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "rostry://user/{userId}" })
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("userId") ?: ""
                if (uid.isBlank()) {
                    android.util.Log.w("SocialNavigationProvider", "Invalid argument for route: $USER_PROFILE")
                    ErrorScreen(message = "Invalid user ID", onBack = { navController.popBackStack() })
                } else {
                    SocialProfileScreen(
                        userId = uid,
                        onBack = { navController.popBackStack() },
                        onPostClick = { postId -> navController.navigate(Routes.discussionDetail(postId)) },
                        onEditProfileClick = { navController.navigate(PROFILE_EDIT) }
                    )
                }
            }
        }
    }

    override fun getDeepLinks(): List<String> = listOf(
        "rostry://social/*",
        "rostry://messages/*",
        "rostry://user/*",
        "rostry://live",
        "https://rostry.app/social/*",
        "https://rostry.app/messages/*",
        "https://rostry.app/user/*"
    )
}
