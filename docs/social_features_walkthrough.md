# Social Features Walkthrough

This document outlines the new social features implemented in the ROSTRY app, including the enhanced User Profile, Stories, and Twitter-like Discussions.

## 1. Social Profile

The User Profile has been significantly upgraded from a placeholder to a rich, Instagram-style profile.

### Features:
-   **Header**: Displays user avatar, name, bio, and stats (Posts, Followers, Following).
-   **Actions**: "Follow/Unfollow" and "Message" buttons for other users; "Edit Profile" for the current user.
-   **Content Tabs**:
    -   **Grid**: A 3-column grid of the user's posts.
    -   **Tagged**: A placeholder for posts the user is tagged in.
-   **Navigation**: Clicking a post in the grid opens the new Discussion Detail view.

### Verification:
1.  Navigate to the **Social Feed**.
2.  Click on any user's **Avatar** or **Name** on a post.
3.  Verify that the **Social Profile** screen opens.
4.  Check that the stats and bio are displayed.
5.  Verify that the "Follow" button toggles state (mock implementation).
6.  Click on a post thumbnail in the grid to open the post details.

## 2. Stories

A new Stories feature allows users to share ephemeral content.

### Features:
-   **Stories Row**: Located at the top of the Social Feed.
    -   **"Your Story"**: Button to create a new story.
    -   **Friend Stories**: List of active stories from followed users with a ring indicator.
-   **Story Viewer**: Immersive full-screen viewer.
    -   **Progress Bars**: Indicates duration and number of stories.
    -   **Navigation**: Tap left/right to navigate between stories; long-press to pause.
    -   **Auto-advance**: Automatically moves to the next story after 5 seconds.
-   **Story Creator**: Simple interface to pick an image and post it as a story.

### Verification:
1.  On the **Social Feed**, locate the **Stories Row** at the top.
2.  Click **"Your Story"** (or the Add icon).
3.  Select an image (mock or system picker).
4.  Click **"Share to Story"**.
5.  Verify that the story appears in the row.
6.  Click on a user's story bubble to open the **Story Viewer**.
7.  Test tap navigation and auto-advance.

## 3. Twitter-like Discussions

The feed and post details have been enhanced to support threaded conversations.

### Features:
-   **Feed Differentiation**:
    -   **Media Posts**: Standard card layout with large media.
    -   **Discussion Posts**: Text-focused layout, similar to tweets.
-   **Discussion Detail Screen**:
    -   **Main Post**: Displays the full content of the parent post.
    -   **Threaded Replies**: List of replies to the post.
    -   **Quick Reply**: Input bar at the bottom to easily add a reply.

### Verification:
1.  On the **Social Feed**, find a text-only post.
2.  Click on the post (not the profile) to open the **Discussion Detail** screen.
3.  Verify that the main post is at the top.
4.  Scroll through the replies.
5.  Type a reply in the bottom bar and click **"Reply"** (mock implementation).

## 4. Feed Enhancements

-   **Navigation**: All author avatars and names now link to the new **Social Profile**.
-   **Post Types**: The feed now intelligently renders posts based on their content (Media vs. Text).

## Files Modified
-   `SocialFeedScreen.kt`: Added StoriesRow, updated PostCard, added navigation callbacks.
-   `SocialProfileScreen.kt`: New profile UI.
-   `SocialProfileViewModel.kt`: Logic for profile data.
-   `StoryViewerScreen.kt`: New immersive story viewer.
-   `StoryCreatorScreen.kt`: New story creation UI.
-   `DiscussionDetailScreen.kt`: New threaded view.
-   `AppNavHost.kt`: Registered new routes and wired navigation.
-   `Routes.kt`: Added new route constants.
-   `SocialRepository.kt` & `SocialRepositoryImpl.kt`: Added `getUserPosts` for profile feed.

## 5. Code Cleanup & Optimization

-   **`AppNavHost.kt`**: Removed duplicate imports and redundant code blocks to improve maintainability.
-   **`SocialFeedScreen.kt`**: Optimized imports and ensured all UI components (Icons, Shapes) are correctly referenced.
