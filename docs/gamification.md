Title: Gamification
Version: 1.0
Last Updated: 2025-10-16
Audience: Developers, Contributors

## Table of Contents
- [Data Models](#data-models)
- [Reward Mechanics](#reward-mechanics)
- [Progression System](#progression-system)
- [Implementation Guide](#implementation-guide)
- [UI Components](#ui-components)
- [Analytics & Monitoring](#analytics--monitoring)

```kotlin
@Singleton
class AchievementSystem @Inject constructor() {
    data class Achievement(
        val id: String,
        val name: String, 
        val description: String,
        val points: Int
    )
    
    suspend fun achievementsFor(userId: String): List<Achievement> = emptyList()
    suspend fun recordProgress(userId: String, action: String) {}
}
```

This class handles:
- Achievement discovery and unlocking logic
- Progress tracking against achievement criteria
- Point calculation and reward distribution
- Integration with notification systems for achievement unlocks

### Achievement Types and Categories

ROSTRY implements a comprehensive taxonomy of achievements organized by category:

**Farming Excellence**
- `first_flock`: Create your first poultry flock
- `vaccination_master`: Maintain 100% vaccination coverage for 30 days
- `yield_champion`: Achieve top 10% production yields in your region
- `breeding_expert`: Successfully hatch 100+ chicks

**Marketplace Success**
- `first_sale`: Complete your first marketplace transaction
- `trusted_seller`: Maintain 5-star rating with 50+ reviews
- `volume_trader`: Complete 100 marketplace transactions
- `premium_merchant`: List items in premium marketplace categories

**Community Engagement**
- `social_butterfly`: Join 5 different community groups
- `helpful_expert`: Provide 25 expert consultations
- `content_creator`: Publish 10 posts with engagement
- `mentor`: Guide 10 new farmers through onboarding

**Platform Mastery**
- `early_adopter`: Use app features within first week of joining
- `data_driven`: Generate 20 analytics reports
- `transfer_specialist`: Complete 50 secure fowl transfers
- `loyal_user`: Maintain active usage for 6 consecutive months

### Trigger Conditions and Unlock Criteria

Each achievement has specific trigger conditions that determine when it unlocks:

**Threshold-Based Triggers**
- Simple count achievements (e.g., "Complete 10 transfers")
- Time-based achievements (e.g., "Active for 30 consecutive days")
- Quality-based criteria (e.g., "Maintain 4.8+ star rating")

**Composite Triggers**
- Multi-step achievements requiring sequential actions
- Conditional achievements based on related activities
- Seasonal achievements tied to agricultural cycles

**Progressive Triggers**
- Tiered achievements (Bronze/Silver/Gold levels)
- Streak-based achievements (consecutive day requirements)
- Comparative achievements (top percentile performance)

### Achievement Tracking and Persistence

Achievement progress is tracked through the `UserProgressEntity`:

```kotlin
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val achievementId: String,
    val progress: Int,        // Current progress value
    val target: Int,          // Required target value
    val unlockedAt: Long?,    // Timestamp when unlocked (null if in progress)
    val updatedAt: Long       // Last progress update timestamp
)
```

The system maintains:
- Real-time progress updates as users perform actions
- Persistent storage of achievement states across app sessions
- Incremental progress tracking for long-term achievements
- Historical achievement data for analytics and user profiles

### Notification System for Unlocked Achievements

When achievements unlock, the system triggers multiple notification channels:

**In-App Notifications**
- Toast messages with achievement details and earned points
- Celebration animations with confetti effects
- Badge overlays on achievement icons
- Sound effects and haptic feedback

**Push Notifications**
- Optional push alerts for major milestone achievements
- Customizable notification preferences per achievement category
- Scheduled digest notifications for multiple unlocks

**UI Integration**
- Achievement unlocked dialogs with reward details
- Profile badge updates with new achievements
- Progress indicator animations for near-completion achievements

## Data Models

The gamification system relies on a robust data model that supports complex achievement tracking, reward management, and leaderboard calculations.

### GamificationEntities.kt: Entity Definitions

The core entities define the structure for all gamification data:

**Achievement Definition Entity**
```kotlin
@Entity(tableName = "achievements_def")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val name: String,
    val description: String?,
    val points: Int,           // Points awarded upon unlock
    val category: String?,     // Achievement category for organization
    val icon: String?          // Icon resource identifier
)
```

**User Progress Tracking**
```kotlin
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val achievementId: String,
    val progress: Int,
    val target: Int,
    val unlockedAt: Long?,
    val updatedAt: Long
)
```

**Badge System**
```kotlin
@Entity(tableName = "badges_def")
data class GamificationBadgeEntity(
    @PrimaryKey val badgeId: String,
    val name: String,
    val description: String?,
    val icon: String?
)
```

**Leaderboard Data**
```kotlin
@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey val id: String,
    val periodKey: String,     // Time period identifier (weekly, monthly, all-time)
    val userId: String,
    val score: Long,           // Calculated score for ranking
    val rank: Int              // Current rank in period
)
```

**Reward Definitions**
```kotlin
@Entity(tableName = "rewards_def")
data class RewardEntity(
    @PrimaryKey val rewardId: String,
    val name: String,
    val description: String?,
    val pointsRequired: Int    // Points needed to redeem
)
```

### GamificationDaos.kt: Database Access Patterns

The DAO layer provides efficient data access for gamification operations:

**Achievement Data Access**
```kotlin
@Dao
interface AchievementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<AchievementEntity>)

    @Query("SELECT * FROM achievements_def")
    fun all(): Flow<List<AchievementEntity>>
}
```

**Progress Tracking**
```kotlin
@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun forUser(userId: String): Flow<List<UserProgressEntity>>
}
```

**Leaderboard Operations**
```kotlin
@Dao
interface LeaderboardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<LeaderboardEntity>)

    @Query("SELECT * FROM leaderboard WHERE periodKey = :periodKey ORDER BY rank ASC")
    fun leaderboard(periodKey: String): Flow<List<LeaderboardEntity>>
}
```

### Achievement Entity Structure

Achievement entities are designed for flexibility and extensibility:

- **Unique Identification**: String-based IDs allow for readable, meaningful identifiers
- **Localization Support**: Name and description fields support internationalization
- **Categorization**: Category field enables filtering and organization
- **Visual Elements**: Icon field references drawable resources for UI display
- **Point System**: Points field determines achievement value and reward calculations

### User Progress Tracking

The progress tracking system handles complex achievement states:

- **Incremental Progress**: Progress field tracks partial completion
- **Target Definition**: Target field defines completion requirements
- **State Management**: unlockedAt field distinguishes completed from in-progress achievements
- **Audit Trail**: updatedAt field provides modification timestamps for data integrity

### Reward Entity and Redemption Tracking

Rewards are managed through a points-based redemption system:

- **Reward Catalog**: Centralized definitions of available rewards
- **Point Costs**: pointsRequired field determines redemption thresholds
- **Redemption History**: Separate tracking of redeemed rewards (future enhancement)
- **Dynamic Pricing**: Point requirements can be adjusted based on reward popularity

## Reward Mechanics

The reward system provides tangible value for user achievements, creating a compelling economy within the ROSTRY platform.

### Types of Rewards

ROSTRY implements multiple reward types to cater to different user motivations:

**Digital Badges**
- Visual recognition displayed on user profiles
- Category-specific badges (Farming, Marketplace, Community)
- Rarity tiers (Common, Rare, Epic, Legendary)
- Seasonal badges tied to agricultural cycles

**Platform Points**
- Accumulative points for leaderboard ranking
- Redemption currency for premium features
- Social status indicators
- Achievement multipliers for advanced users

**Feature Unlocks**
- Premium marketplace placement
- Advanced analytics features
- Priority customer support
- Exclusive community access
- Enhanced notification options

**Marketplace Benefits**
- Reduced transaction fees
- Featured listing opportunities
- Discount codes for platform services
- Priority in search results
- Bonus visibility in recommendations

### Reward Calculation and Distribution

Rewards are calculated through a multi-factor algorithm:

**Base Point System**
- Achievement-specific point values
- Difficulty multipliers (easy: 1x, medium: 2x, hard: 3x)
- Time-based bonuses for speed achievements
- Streak multipliers for consecutive accomplishments

**Dynamic Adjustments**
- Seasonal modifiers during peak farming periods
- Regional adjustments based on local competition
- User level multipliers for experienced farmers
- Community contribution bonuses

**Distribution Logic**
```kotlin
fun calculateReward(achievement: Achievement, userLevel: Int, streakCount: Int): Reward {
    val basePoints = achievement.points
    val levelMultiplier = 1.0 + (userLevel * 0.1)
    val streakBonus = min(streakCount * 0.05, 0.5) // Max 50% bonus
    
    val totalPoints = (basePoints * levelMultiplier * (1 + streakBonus)).toInt()
    return Reward(points = totalPoints, badges = determineBadges(achievement))
}
```

### Redemption Process

Users redeem rewards through a streamlined process:

**Reward Catalog Browsing**
- Categorized reward display
- Point balance visibility
- Redemption eligibility checking
- Preview of reward benefits

**Redemption Workflow**
1. User selects desired reward
2. System validates point balance and eligibility
3. Points are deducted from user account
4. Reward is immediately applied or queued for delivery
5. Confirmation notification with redemption details

**Reward Fulfillment**
- Instant digital rewards (badges, feature unlocks)
- Scheduled rewards (marketplace discounts)
- Physical rewards (shipping coordination required)
- Service rewards (support priority activation)

### Integration with Other Features

Rewards create value connections across the platform:

**Marketplace Integration**
- Achievement-based seller badges increase buyer trust
- Reward points can be used for transaction fee discounts
- Premium listings unlocked through achievement progression
- Featured placement rewards for top performers

**Premium Feature Access**
- Advanced analytics unlocked through point redemption
- Priority notifications for achievement hunters
- Exclusive community groups for high-level users
- Beta feature access for platform contributors

**Social Features**
- Leaderboard visibility bonuses for reward holders
- Special recognition in community discussions
- Mentor program access for experienced users
- Event hosting privileges for community leaders

## Progression System

The progression system provides long-term engagement through level advancement and milestone recognition.

### User Levels and Experience Points

ROSTRY implements a comprehensive leveling system:

**Level Structure**
- 50 progression levels from Novice (1) to Legend (50)
- Experience points required double approximately every 5 levels
- Level names reflect farming expertise progression

**Experience Point Sources**
- Achievement unlocks (primary source)
- Daily active usage bonuses
- Community engagement activities
- Marketplace transaction completion
- Feature exploration and adoption

**Point Calculation Formula**
```kotlin
fun calculateExperiencePoints(action: UserAction): Int {
    return when (action.type) {
        ACHIEVEMENT_UNLOCK -> action.achievement.points * 10
        DAILY_LOGIN -> 50 + (streakDays * 10)
        MARKETPLACE_TRANSACTION -> 100 + (transactionValue * 0.01).toInt()
        COMMUNITY_POST -> 25 + (engagementScore * 5)
        FEATURE_EXPLORATION -> 75
        else -> 0
    }
}
```

### Level-Up Mechanics

Level advancement triggers celebratory experiences:

**Level Thresholds**
- Levels 1-10: Focus on basic platform mastery
- Levels 11-25: Intermediate farming and marketplace skills
- Levels 26-40: Advanced features and community leadership
- Levels 41-50: Expert status and platform contribution

**Level-Up Process**
1. User accumulates experience points through activities
2. System monitors progress toward next level threshold
3. Upon reaching threshold, level advances immediately
4. Celebration sequence triggers with animations and notifications
5. New level benefits unlock automatically

**Level Benefits**
- Increased achievement point multipliers
- Access to higher-tier rewards
- Enhanced leaderboard visibility
- Priority in community features
- Exclusive badges and recognition

### Progression Milestones

Major milestones provide additional motivation:

**Bronze Milestones** (Levels 5, 10, 15)
- Basic platform proficiency recognition
- Access to standard reward catalog
- Community posting privileges

**Silver Milestones** (Levels 20, 25, 30)
- Intermediate expertise acknowledgment
- Premium feature previews
- Mentor program eligibility

**Gold Milestones** (Levels 35, 40, 45)
- Advanced user status
- Leadership opportunities
- Beta feature participation

**Legendary Milestones** (Level 50)
- Platform expert recognition
- Special legendary badge
- Advisory board invitations
- Featured user spotlights

### Visual Indicators and UI Components

Progression is made visible through comprehensive UI elements:

**Level Display Components**
- Circular progress rings showing experience toward next level
- Level number badges with color coding by tier
- Experience point counters with animated updates
- Level-up celebration overlays

**Progress Bars**
- Achievement completion progress bars
- Daily goal progress indicators
- Streak counters with visual flame icons
- Milestone progress trackers

**Visual Feedback**
- Color-coded level tiers (Bronze, Silver, Gold, Legendary)
- Animated level-up sequences with particle effects
- Progress notification badges on app icons
- Dashboard widgets showing progression status

## Implementation Guide

This section provides practical guidance for developers working with the gamification system.

### How to Add New Achievements

Adding new achievements requires coordination across multiple components:

**1. Define Achievement in Database**
```kotlin
val newAchievement = AchievementEntity(
    achievementId = "sustainable_farmer",
    name = "Sustainable Farmer",
    description = "Maintain eco-friendly practices for 90 days",
    points = 500,
    category = "Farming Excellence",
    icon = "ic_sustainability_badge"
)
```

**2. Implement Trigger Logic in AchievementSystem**
```kotlin
suspend fun checkSustainableFarmer(userId: String): Boolean {
    val sustainableActions = repository.getSustainableActionsLast90Days(userId)
    return sustainableActions.count() >= 90
}
```

**3. Register Achievement in System**
```kotlin
fun registerAchievement(achievement: AchievementEntity) {
    // Add to achievement definitions
    // Set up progress tracking
    // Configure trigger conditions
}
```

**4. Update UI Components**
- Add achievement to achievement list displays
- Include progress indicators where applicable
- Update celebration animations for new achievement types

### How to Integrate Gamification into Features

Integrating gamification requires systematic approach:

**Identify Gamification Opportunities**
- Analyze user workflows for achievement potential
- Determine appropriate reward triggers
- Consider progression impact on user engagement

**Implement Progress Tracking**
```kotlin
suspend fun recordUserAction(userId: String, actionType: String, metadata: Map<String, Any>) {
    achievementSystem.recordProgress(userId, actionType)
    
    // Update relevant progress entities
    val progress = UserProgressEntity(
        id = generateId(),
        userId = userId,
        achievementId = determineAchievementId(actionType),
        progress = calculateProgress(metadata),
        target = getAchievementTarget(actionType),
        unlockedAt = if (isComplete(metadata)) System.currentTimeMillis() else null,
        updatedAt = System.currentTimeMillis()
    )
    
    userProgressDao.upsert(progress)
}
```

**Add UI Feedback**
```kotlin
@Composable
fun AchievementNotification(achievement: Achievement, onDismiss: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(achievement.icon), contentDescription = null)
            Column {
                Text(achievement.name, style = MaterialTheme.typography.h6)
                Text(achievement.description)
                Text("+${achievement.points} points", color = MaterialTheme.colors.primary)
            }
        }
    }
}
```

### Repository Methods for Achievement Tracking

Repository classes provide clean interfaces for gamification data:

**AchievementRepository**
```kotlin
class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao,
    private val userProgressDao: UserProgressDao
) {
    fun getAchievementsForUser(userId: String): Flow<List<AchievementWithProgress>> {
        return combine(
            achievementDao.all(),
            userProgressDao.forUser(userId)
        ) { achievements, progress ->
            achievements.map { achievement ->
                val userProgress = progress.find { it.achievementId == achievement.achievementId }
                AchievementWithProgress(achievement, userProgress)
            }
        }
    }
    
    suspend fun updateProgress(userId: String, achievementId: String, newProgress: Int) {
        val existing = userProgressDao.getById(userId, achievementId)
        val updated = existing?.copy(
            progress = newProgress,
            updatedAt = System.currentTimeMillis(),
            unlockedAt = if (newProgress >= existing.target) System.currentTimeMillis() else null
        ) ?: UserProgressEntity(
            id = generateId(),
            userId = userId,
            achievementId = achievementId,
            progress = newProgress,
            target = getAchievementTarget(achievementId),
            unlockedAt = null,
            updatedAt = System.currentTimeMillis()
        )
        userProgressDao.upsert(updated)
    }
}
```

### ViewModel Patterns for Displaying Achievements

ViewModels handle the presentation logic for gamification features:

**AchievementViewModel**
```kotlin
class AchievementViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _achievements = MutableStateFlow<List<AchievementWithProgress>>(emptyList())
    val achievements: StateFlow<List<AchievementWithProgress>> = _achievements
    
    private val _userLevel = MutableStateFlow<UserLevel>(UserLevel(1, 0, 100))
    val userLevel: StateFlow<UserLevel> = _userLevel
    
    init {
        viewModelScope.launch {
            userRepository.currentUserId.collect { userId ->
                if (userId != null) {
                    loadAchievements(userId)
                    loadUserLevel(userId)
                }
            }
        }
    }
    
    private suspend fun loadAchievements(userId: String) {
        achievementRepository.getAchievementsForUser(userId)
            .collect { _achievements.value = it }
    }
    
    private suspend fun loadUserLevel(userId: String) {
        userRepository.getUserLevel(userId)
            .collect { _userLevel.value = it }
    }
    
    fun refreshAchievements() {
        viewModelScope.launch {
            userRepository.currentUserId.value?.let { loadAchievements(it) }
        }
    }
}
```

### Code Examples from Actual Implementation

**Achievement Trigger Integration**
```kotlin
// In TransferRepository
suspend fun completeTransfer(transferId: String): Result<Transfer> {
    val result = transferDao.completeTransfer(transferId)
    if (result.isSuccess) {
        // Trigger achievement check
        achievementSystem.recordProgress(currentUserId, "transfer_completed")
        
        // Check for milestone achievements
        checkTransferMilestones(currentUserId)
    }
    return result
}

private suspend fun checkTransferMilestones(userId: String) {
    val transferCount = transferDao.getCompletedCount(userId)
    
    when {
        transferCount >= 100 -> achievementSystem.unlockAchievement(userId, "century_club")
        transferCount >= 50 -> achievementSystem.unlockAchievement(userId, "transfer_specialist")
        transferCount >= 10 -> achievementSystem.unlockAchievement(userId, "getting_started")
    }
}
```

**Reward Redemption Flow**
```kotlin
// In RewardRepository
suspend fun redeemReward(userId: String, rewardId: String): Result<RewardRedemption> {
    val reward = rewardDao.getById(rewardId) ?: return Result.failure(RewardNotFoundException())
    val userPoints = userDao.getPoints(userId)
    
    if (userPoints < reward.pointsRequired) {
        return Result.failure(InsufficientPointsException())
    }
    
    // Deduct points
    userDao.updatePoints(userId, userPoints - reward.pointsRequired)
    
    // Record redemption
    val redemption = RewardRedemptionEntity(
        id = generateId(),
        userId = userId,
        rewardId = rewardId,
        redeemedAt = System.currentTimeMillis(),
        pointsSpent = reward.pointsRequired
    )
    redemptionDao.insert(redemption)
    
    // Apply reward effects
    applyRewardEffects(userId, reward)
    
    return Result.success(redemption.toDomain())
}
```

## UI Components

The gamification system includes specialized UI components for displaying achievements and progress.

### Achievement Display Components

Achievement displays provide visual recognition of accomplishments:

**AchievementCard Component**
```kotlin
@Composable
fun AchievementCard(
    achievement: AchievementWithProgress,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = if (achievement.isUnlocked) 8.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement icon with lock/unlock state
            Box {
                Icon(
                    painter = painterResource(achievement.icon),
                    contentDescription = achievement.name,
                    modifier = Modifier.size(48.dp)
                )
                if (!achievement.isUnlocked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_lock),
                        contentDescription = "Locked",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.White, CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
                if (achievement.isUnlocked) {
                    Text(
                        text = "Earned ${achievement.points} points",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                } else {
                    LinearProgressIndicator(
                        progress = achievement.progress.toFloat() / achievement.target,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${achievement.progress}/${achievement.target}",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }
    }
}
```

### Progress Indicators

Progress indicators show advancement toward goals:

**LevelProgressRing**
```kotlin
@Composable
fun LevelProgressRing(
    currentLevel: Int,
    currentXP: Int,
    nextLevelXP: Int,
    modifier: Modifier = Modifier
) {
    val progress = (currentXP.toFloat() / nextLevelXP).coerceIn(0f, 1f)
    
    Box(modifier = modifier.size(120.dp), contentAlignment = Alignment.Center) {
        // Background circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.LightGray,
                style = Stroke(width = 8f)
            )
        }
        
        // Progress arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = MaterialTheme.colors.primary,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }
        
        // Level text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentLevel.toString(),
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Level",
                style = MaterialTheme.typography.caption
            )
        }
    }
}
```

### Celebration Animations (Reference SuccessAnimations.kt)

Success animations provide celebratory feedback for achievements:

**AchievementUnlockAnimation**
```kotlin
@Composable
fun AchievementUnlockAnimation(
    achievement: Achievement,
    onAnimationComplete: () -> Unit
) {
    var animationState by remember { mutableStateOf(AnimationState.START) }
    
    LaunchedEffect(Unit) {
        // Sequence of animations
        delay(500) // Initial delay
        animationState = AnimationState.EXPANDING
        delay(1000)
        animationState = AnimationState.CONFETTI
        delay(2000)
        animationState = AnimationState.COMPLETE
        onAnimationComplete()
    }
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (animationState) {
            AnimationState.START -> {
                // Initial achievement icon
                Icon(
                    painter = painterResource(achievement.icon),
                    contentDescription = achievement.name,
                    modifier = Modifier.size(64.dp)
                )
            }
            AnimationState.EXPANDING -> {
                // Expanding glow effect
                Icon(
                    painter = painterResource(achievement.icon),
                    contentDescription = achievement.name,
                    modifier = Modifier
                        .size(128.dp)
                        .graphicsLayer {
                            scaleX = 2f
                            scaleY = 2f
                            alpha = 0.8f
                        }
                )
            }
            AnimationState.CONFETTI -> {
                // Full celebration with confetti
                ConfettiAnimation()
                AchievementCard(achievement = achievement)
            }
            AnimationState.COMPLETE -> {
                // Final state
                AchievementCard(achievement = achievement)
            }
        }
    }
}
```

### Leaderboard Views

Leaderboard displays show competitive rankings:

**LeaderboardScreen**
```kotlin
@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val currentUserRank by viewModel.currentUserRank.collectAsState()
    
    LazyColumn {
        item {
            // Current user rank highlight
            if (currentUserRank != null) {
                LeaderboardEntry(
                    entry = currentUserRank,
                    isCurrentUser = true,
                    modifier = Modifier.background(Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }
        
        items(leaderboard) { entry ->
            LeaderboardEntry(entry = entry, isCurrentUser = false)
        }
    }
}

@Composable
fun LeaderboardEntry(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number
        Text(
            text = "#${entry.rank}",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.width(60.dp)
        )
        
        // User avatar
        Image(
            painter = rememberImagePainter(entry.userAvatar),
            contentDescription = "User avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.userName,
                style = MaterialTheme.typography.body1,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "${entry.score} points",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }
        
        // Rank change indicator
        RankChangeIndicator(entry.rankChange)
    }
}
```

## Analytics & Monitoring

The gamification system includes comprehensive analytics to track effectiveness and user engagement.

### Tracking Achievement Completion Rates

Achievement analytics provide insights into user progression:

**Completion Rate Metrics**
- Overall achievement completion percentage
- Category-specific completion rates
- Time-to-completion for different achievement types
- Abandonment rates for complex achievements

**Analytics Implementation**
```kotlin
class GamificationAnalytics @Inject constructor(
    private val analyticsTracker: AnalyticsTracker
) {
    suspend fun trackAchievementUnlock(userId: String, achievementId: String) {
        analyticsTracker.logEvent("achievement_unlocked", mapOf(
            "user_id" to userId,
            "achievement_id" to achievementId,
            "timestamp" to System.currentTimeMillis(),
            "user_level" to getUserLevel(userId),
            "session_streak" to getCurrentStreak(userId)
        ))
    }
    
    suspend fun trackAchievementProgress(userId: String, achievementId: String, progress: Int, target: Int) {
        val completionRate = progress.toFloat() / target
        analyticsTracker.logEvent("achievement_progress", mapOf(
            "user_id" to userId,
            "achievement_id" to achievementId,
            "progress" to progress,
            "target" to target,
            "completion_rate" to completionRate,
            "days_since_start" to calculateDaysSinceStart(userId, achievementId)
        ))
    }
}
```

### User Engagement Metrics

Engagement tracking measures gamification system effectiveness:

**Engagement KPIs**
- Daily/Weekly/Monthly Active Users influenced by gamification
- Session length increases attributed to achievement features
- Feature adoption rates for gamified vs non-gamified features
- User retention rates at different achievement milestones

**Retention Analysis**
```kotlin
fun analyzeRetentionByAchievementLevel() {
    val retentionData = getUserRetentionData()
    
    retentionData.groupBy { user ->
        when {
            user.achievementsUnlocked < 5 -> "Beginner"
            user.achievementsUnlocked < 15 -> "Intermediate"
            user.achievementsUnlocked < 30 -> "Advanced"
            else -> "Expert"
        }
    }.forEach { (level, users) ->
        val avgRetention = users.map { it.daysRetained }.average()
        logRetentionMetric(level, avgRetention)
    }
}
```

### A/B Testing Gamification Features

A/B testing ensures gamification features are optimized:

**Testing Framework**
```kotlin
class GamificationABTest @Inject constructor(
    private val experimentManager: ExperimentManager
) {
    suspend fun getRewardMultiplierVariant(userId: String): Double {
        return when (experimentManager.getVariant(userId, "reward_multiplier_test")) {
            "control" -> 1.0
            "variant_a" -> 1.2  // 20% bonus
            "variant_b" -> 1.5  // 50% bonus
            else -> 1.0
        }
    }
    
    suspend fun trackExperimentOutcome(userId: String, achievementUnlocked: Boolean) {
        experimentManager.logMetric(
            userId = userId,
            experiment = "reward_multiplier_test",
            metric = "achievement_completion",
            value = if (achievementUnlocked) 1.0 else 0.0
        )
    }
}