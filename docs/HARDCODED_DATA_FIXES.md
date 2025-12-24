# Hardcoded Data Fixes - Summary

## Date: 2025-12-24

## Problem
Several screens in the general user role were displaying fake/hardcoded data instead of real data from the database:

1. **Product Details Screen** - Showing fake review counts, view counts, and seller ratings based on hash values
2. **Social Profile Screen** - Showing hardcoded "12 Posts", "120 Followers", "45 Following"
3. **Messaging Thread Screen** - Showing raw UUID as thread title

---

## Changes Made

### 1. ProductDetailsScreen.kt
**Location:** `app/src/main/java/com/rio/rostry/ui/product/ProductDetailsScreen.kt`

**Before:**
- Line 428: `"(${(product.productId.hashCode().absoluteValue % 100 + 10)} reviews)"` - Fake review count
- Line 466: `"👁 ${(product.productId.hashCode().absoluteValue % 500 + 50)} people viewed this today"` - Fake view count
- Line 530: `"${sellerRating(product.sellerId)} (${(product.sellerId.hashCode().absoluteValue % 50 + 10)} ratings)"` - Fake seller rating
- Line 944-949: `sellerRating()` function generating fake ratings from 4.0-5.0

**After:**
- Reviews: Shows "(No reviews yet)" with muted styling
- View count: Removed fake view count (TODO comment added)
- Seller rating: Shows "New seller" instead of fake rating
- `sellerRating()` function returns "–" (em dash) indicating no rating yet

### 2. SocialProfileViewModel.kt
**Location:** `app/src/main/java/com/rio/rostry/ui/social/profile/SocialProfileViewModel.kt`

**Before:**
```kotlin
val followersCount = flowOf(120) // Mock
val followingCount = flowOf(45)  // Mock
val postsCount = flowOf(12)      // Mock
```

**After:**
- `postsCount`: Uses `socialRepository.getUserPostsCount(userId)` - real data from PostsDao
- `followersCount`: Uses `socialRepository.getFollowersCount(userId)` - real data from FollowsDao
- `followingCount`: Uses `socialRepository.getFollowingCount(userId)` - real data from FollowsDao

### 3. ThreadScreen.kt
**Location:** `app/src/main/java/com/rio/rostry/ui/messaging/ThreadScreen.kt`

**Before:**
```kotlin
Text(metadata?.title ?: "Thread $threadId")  // Shows raw UUID
```

**After:**
```kotlin
Text(metadata?.title ?: "New Conversation")  // User-friendly fallback
```

### 4. SocialRepository & DAOs
**Files Modified:**
- `app/src/main/java/com/rio/rostry/data/repository/social/RepositoriesSocial.kt`
- `app/src/main/java/com/rio/rostry/data/database/dao/SocialDaos.kt`

**Changes:**
- Added `getUserPostsCount()`, `getFollowersCount()`, `getFollowingCount()` to `SocialRepository` interface
- Added implementations in `SocialRepositoryImpl` using DAOs
- Added `countByAuthor()` query to `PostsDao`
- Injected `FollowsDao` into `SocialRepositoryImpl` for followers/following counts

---

## Result
All screens now show honest data from the database instead of fake numbers:
- New users see "0 followers", "0 following", "0 posts" - accurate and honest
- Products show "No reviews yet" instead of fake reviews
- Thread titles show "New Conversation" instead of raw UUIDs

---

## Future TODO Items
1. **Reviews System**: Implement a reviews table and real rating calculation
2. **Analytics System**: Implement product view tracking to show real view counts
3. **Seller Rating**: Calculate average rating from actual reviews
