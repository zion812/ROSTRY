package com.rio.rostry.`data`.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.FtsTableInfo
import androidx.room.util.TableInfo
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.rio.rostry.`data`.database.dao.AchievementDao
import com.rio.rostry.`data`.database.dao.AchievementDao_Impl
import com.rio.rostry.`data`.database.dao.AdminAuditDao
import com.rio.rostry.`data`.database.dao.AdminAuditDao_Impl
import com.rio.rostry.`data`.database.dao.AlertDao
import com.rio.rostry.`data`.database.dao.AlertDao_Impl
import com.rio.rostry.`data`.database.dao.AnalyticsDao
import com.rio.rostry.`data`.database.dao.AnalyticsDao_Impl
import com.rio.rostry.`data`.database.dao.ArenaParticipantDao
import com.rio.rostry.`data`.database.dao.ArenaParticipantDao_Impl
import com.rio.rostry.`data`.database.dao.AssetBatchOperationDao
import com.rio.rostry.`data`.database.dao.AssetBatchOperationDao_Impl
import com.rio.rostry.`data`.database.dao.AssetHealthRecordDao
import com.rio.rostry.`data`.database.dao.AssetHealthRecordDao_Impl
import com.rio.rostry.`data`.database.dao.AssetLifecycleEventDao
import com.rio.rostry.`data`.database.dao.AssetLifecycleEventDao_Impl
import com.rio.rostry.`data`.database.dao.AuctionDao
import com.rio.rostry.`data`.database.dao.AuctionDao_Impl
import com.rio.rostry.`data`.database.dao.AuditLogDao
import com.rio.rostry.`data`.database.dao.AuditLogDao_Impl
import com.rio.rostry.`data`.database.dao.BadgeDefDao
import com.rio.rostry.`data`.database.dao.BadgeDefDao_Impl
import com.rio.rostry.`data`.database.dao.BadgesDao
import com.rio.rostry.`data`.database.dao.BadgesDao_Impl
import com.rio.rostry.`data`.database.dao.BatchSummaryDao
import com.rio.rostry.`data`.database.dao.BatchSummaryDao_Impl
import com.rio.rostry.`data`.database.dao.BidDao
import com.rio.rostry.`data`.database.dao.BidDao_Impl
import com.rio.rostry.`data`.database.dao.BirdEventDao
import com.rio.rostry.`data`.database.dao.BirdEventDao_Impl
import com.rio.rostry.`data`.database.dao.BirdTraitRecordDao
import com.rio.rostry.`data`.database.dao.BirdTraitRecordDao_Impl
import com.rio.rostry.`data`.database.dao.BreedDao
import com.rio.rostry.`data`.database.dao.BreedDao_Impl
import com.rio.rostry.`data`.database.dao.BreedingPairDao
import com.rio.rostry.`data`.database.dao.BreedingPairDao_Impl
import com.rio.rostry.`data`.database.dao.BreedingPlanDao
import com.rio.rostry.`data`.database.dao.BreedingPlanDao_Impl
import com.rio.rostry.`data`.database.dao.BreedingRecordDao
import com.rio.rostry.`data`.database.dao.BreedingRecordDao_Impl
import com.rio.rostry.`data`.database.dao.CartDao
import com.rio.rostry.`data`.database.dao.CartDao_Impl
import com.rio.rostry.`data`.database.dao.ChatMessageDao
import com.rio.rostry.`data`.database.dao.ChatMessageDao_Impl
import com.rio.rostry.`data`.database.dao.CircuitBreakerMetricsDao
import com.rio.rostry.`data`.database.dao.CircuitBreakerMetricsDao_Impl
import com.rio.rostry.`data`.database.dao.ClutchDao
import com.rio.rostry.`data`.database.dao.ClutchDao_Impl
import com.rio.rostry.`data`.database.dao.CoinDao
import com.rio.rostry.`data`.database.dao.CoinDao_Impl
import com.rio.rostry.`data`.database.dao.CoinLedgerDao
import com.rio.rostry.`data`.database.dao.CoinLedgerDao_Impl
import com.rio.rostry.`data`.database.dao.CommentsDao
import com.rio.rostry.`data`.database.dao.CommentsDao_Impl
import com.rio.rostry.`data`.database.dao.CommunityRecommendationDao
import com.rio.rostry.`data`.database.dao.CommunityRecommendationDao_Impl
import com.rio.rostry.`data`.database.dao.ComplianceRuleDao
import com.rio.rostry.`data`.database.dao.ComplianceRuleDao_Impl
import com.rio.rostry.`data`.database.dao.ConfigurationCacheDao
import com.rio.rostry.`data`.database.dao.ConfigurationCacheDao_Impl
import com.rio.rostry.`data`.database.dao.DailyBirdLogDao
import com.rio.rostry.`data`.database.dao.DailyBirdLogDao_Impl
import com.rio.rostry.`data`.database.dao.DailyLogDao
import com.rio.rostry.`data`.database.dao.DailyLogDao_Impl
import com.rio.rostry.`data`.database.dao.DashboardCacheDao
import com.rio.rostry.`data`.database.dao.DashboardCacheDao_Impl
import com.rio.rostry.`data`.database.dao.DeliveryConfirmationDao
import com.rio.rostry.`data`.database.dao.DeliveryConfirmationDao_Impl
import com.rio.rostry.`data`.database.dao.DeliveryHubDao
import com.rio.rostry.`data`.database.dao.DeliveryHubDao_Impl
import com.rio.rostry.`data`.database.dao.DigitalTwinDao
import com.rio.rostry.`data`.database.dao.DigitalTwinDao_Impl
import com.rio.rostry.`data`.database.dao.DisputeDao
import com.rio.rostry.`data`.database.dao.DisputeDao_Impl
import com.rio.rostry.`data`.database.dao.EggCollectionDao
import com.rio.rostry.`data`.database.dao.EggCollectionDao_Impl
import com.rio.rostry.`data`.database.dao.EnthusiastDashboardSnapshotDao
import com.rio.rostry.`data`.database.dao.EnthusiastDashboardSnapshotDao_Impl
import com.rio.rostry.`data`.database.dao.EnthusiastVerificationDao
import com.rio.rostry.`data`.database.dao.EnthusiastVerificationDao_Impl
import com.rio.rostry.`data`.database.dao.ErrorLogDao
import com.rio.rostry.`data`.database.dao.ErrorLogDao_Impl
import com.rio.rostry.`data`.database.dao.EventRsvpsDao
import com.rio.rostry.`data`.database.dao.EventRsvpsDao_Impl
import com.rio.rostry.`data`.database.dao.EventsDao
import com.rio.rostry.`data`.database.dao.EventsDao_Impl
import com.rio.rostry.`data`.database.dao.ExpenseDao
import com.rio.rostry.`data`.database.dao.ExpenseDao_Impl
import com.rio.rostry.`data`.database.dao.ExpertBookingsDao
import com.rio.rostry.`data`.database.dao.ExpertBookingsDao_Impl
import com.rio.rostry.`data`.database.dao.ExpertProfileDao
import com.rio.rostry.`data`.database.dao.ExpertProfileDao_Impl
import com.rio.rostry.`data`.database.dao.FamilyTreeDao
import com.rio.rostry.`data`.database.dao.FamilyTreeDao_Impl
import com.rio.rostry.`data`.database.dao.FarmActivityLogDao
import com.rio.rostry.`data`.database.dao.FarmActivityLogDao_Impl
import com.rio.rostry.`data`.database.dao.FarmAlertDao
import com.rio.rostry.`data`.database.dao.FarmAlertDao_Impl
import com.rio.rostry.`data`.database.dao.FarmAssetDao
import com.rio.rostry.`data`.database.dao.FarmAssetDao_Impl
import com.rio.rostry.`data`.database.dao.FarmEventDao
import com.rio.rostry.`data`.database.dao.FarmEventDao_Impl
import com.rio.rostry.`data`.database.dao.FarmProfileDao
import com.rio.rostry.`data`.database.dao.FarmProfileDao_Impl
import com.rio.rostry.`data`.database.dao.FarmTimelineEventDao
import com.rio.rostry.`data`.database.dao.FarmTimelineEventDao_Impl
import com.rio.rostry.`data`.database.dao.FarmVerificationDao
import com.rio.rostry.`data`.database.dao.FarmVerificationDao_Impl
import com.rio.rostry.`data`.database.dao.FarmerDashboardSnapshotDao
import com.rio.rostry.`data`.database.dao.FarmerDashboardSnapshotDao_Impl
import com.rio.rostry.`data`.database.dao.FollowsDao
import com.rio.rostry.`data`.database.dao.FollowsDao_Impl
import com.rio.rostry.`data`.database.dao.GalleryFilterStateDao
import com.rio.rostry.`data`.database.dao.GalleryFilterStateDao_Impl
import com.rio.rostry.`data`.database.dao.GroupMembersDao
import com.rio.rostry.`data`.database.dao.GroupMembersDao_Impl
import com.rio.rostry.`data`.database.dao.GroupsDao
import com.rio.rostry.`data`.database.dao.GroupsDao_Impl
import com.rio.rostry.`data`.database.dao.GrowthRecordDao
import com.rio.rostry.`data`.database.dao.GrowthRecordDao_Impl
import com.rio.rostry.`data`.database.dao.HatchingBatchDao
import com.rio.rostry.`data`.database.dao.HatchingBatchDao_Impl
import com.rio.rostry.`data`.database.dao.HatchingLogDao
import com.rio.rostry.`data`.database.dao.HatchingLogDao_Impl
import com.rio.rostry.`data`.database.dao.HubAssignmentDao
import com.rio.rostry.`data`.database.dao.HubAssignmentDao_Impl
import com.rio.rostry.`data`.database.dao.InventoryItemDao
import com.rio.rostry.`data`.database.dao.InventoryItemDao_Impl
import com.rio.rostry.`data`.database.dao.InvoiceDao
import com.rio.rostry.`data`.database.dao.InvoiceDao_Impl
import com.rio.rostry.`data`.database.dao.LeaderboardDao
import com.rio.rostry.`data`.database.dao.LeaderboardDao_Impl
import com.rio.rostry.`data`.database.dao.LifecycleEventDao
import com.rio.rostry.`data`.database.dao.LifecycleEventDao_Impl
import com.rio.rostry.`data`.database.dao.LikesDao
import com.rio.rostry.`data`.database.dao.LikesDao_Impl
import com.rio.rostry.`data`.database.dao.ListingDraftDao
import com.rio.rostry.`data`.database.dao.ListingDraftDao_Impl
import com.rio.rostry.`data`.database.dao.MarketListingDao
import com.rio.rostry.`data`.database.dao.MarketListingDao_Impl
import com.rio.rostry.`data`.database.dao.MatingLogDao
import com.rio.rostry.`data`.database.dao.MatingLogDao_Impl
import com.rio.rostry.`data`.database.dao.MediaCacheMetadataDao
import com.rio.rostry.`data`.database.dao.MediaCacheMetadataDao_Impl
import com.rio.rostry.`data`.database.dao.MediaItemDao
import com.rio.rostry.`data`.database.dao.MediaItemDao_Impl
import com.rio.rostry.`data`.database.dao.MediaMetadataDao
import com.rio.rostry.`data`.database.dao.MediaMetadataDao_Impl
import com.rio.rostry.`data`.database.dao.MediaTagDao
import com.rio.rostry.`data`.database.dao.MediaTagDao_Impl
import com.rio.rostry.`data`.database.dao.MedicalEventDao
import com.rio.rostry.`data`.database.dao.MedicalEventDao_Impl
import com.rio.rostry.`data`.database.dao.ModerationBlocklistDao
import com.rio.rostry.`data`.database.dao.ModerationBlocklistDao_Impl
import com.rio.rostry.`data`.database.dao.ModerationReportsDao
import com.rio.rostry.`data`.database.dao.ModerationReportsDao_Impl
import com.rio.rostry.`data`.database.dao.MortalityRecordDao
import com.rio.rostry.`data`.database.dao.MortalityRecordDao_Impl
import com.rio.rostry.`data`.database.dao.NotificationDao
import com.rio.rostry.`data`.database.dao.NotificationDao_Impl
import com.rio.rostry.`data`.database.dao.OrderAuditLogDao
import com.rio.rostry.`data`.database.dao.OrderAuditLogDao_Impl
import com.rio.rostry.`data`.database.dao.OrderDao
import com.rio.rostry.`data`.database.dao.OrderDao_Impl
import com.rio.rostry.`data`.database.dao.OrderDisputeDao
import com.rio.rostry.`data`.database.dao.OrderDisputeDao_Impl
import com.rio.rostry.`data`.database.dao.OrderEvidenceDao
import com.rio.rostry.`data`.database.dao.OrderEvidenceDao_Impl
import com.rio.rostry.`data`.database.dao.OrderPaymentDao
import com.rio.rostry.`data`.database.dao.OrderPaymentDao_Impl
import com.rio.rostry.`data`.database.dao.OrderQuoteDao
import com.rio.rostry.`data`.database.dao.OrderQuoteDao_Impl
import com.rio.rostry.`data`.database.dao.OrderTrackingEventDao
import com.rio.rostry.`data`.database.dao.OrderTrackingEventDao_Impl
import com.rio.rostry.`data`.database.dao.OutboxDao
import com.rio.rostry.`data`.database.dao.OutboxDao_Impl
import com.rio.rostry.`data`.database.dao.OutgoingMessageDao
import com.rio.rostry.`data`.database.dao.OutgoingMessageDao_Impl
import com.rio.rostry.`data`.database.dao.PaymentDao
import com.rio.rostry.`data`.database.dao.PaymentDao_Impl
import com.rio.rostry.`data`.database.dao.PostsDao
import com.rio.rostry.`data`.database.dao.PostsDao_Impl
import com.rio.rostry.`data`.database.dao.ProductDao
import com.rio.rostry.`data`.database.dao.ProductDao_Impl
import com.rio.rostry.`data`.database.dao.ProductTrackingDao
import com.rio.rostry.`data`.database.dao.ProductTrackingDao_Impl
import com.rio.rostry.`data`.database.dao.ProductTraitDao
import com.rio.rostry.`data`.database.dao.ProductTraitDao_Impl
import com.rio.rostry.`data`.database.dao.ProfitabilityMetricsDao
import com.rio.rostry.`data`.database.dao.ProfitabilityMetricsDao_Impl
import com.rio.rostry.`data`.database.dao.QuarantineRecordDao
import com.rio.rostry.`data`.database.dao.QuarantineRecordDao_Impl
import com.rio.rostry.`data`.database.dao.RateLimitDao
import com.rio.rostry.`data`.database.dao.RateLimitDao_Impl
import com.rio.rostry.`data`.database.dao.ReferentialIntegrityDao
import com.rio.rostry.`data`.database.dao.ReferentialIntegrityDao_Impl
import com.rio.rostry.`data`.database.dao.RefundDao
import com.rio.rostry.`data`.database.dao.RefundDao_Impl
import com.rio.rostry.`data`.database.dao.ReportsDao
import com.rio.rostry.`data`.database.dao.ReportsDao_Impl
import com.rio.rostry.`data`.database.dao.ReputationDao
import com.rio.rostry.`data`.database.dao.ReputationDao_Impl
import com.rio.rostry.`data`.database.dao.ReviewDao
import com.rio.rostry.`data`.database.dao.ReviewDao_Impl
import com.rio.rostry.`data`.database.dao.RewardDefDao
import com.rio.rostry.`data`.database.dao.RewardDefDao_Impl
import com.rio.rostry.`data`.database.dao.RoleMigrationDao
import com.rio.rostry.`data`.database.dao.RoleMigrationDao_Impl
import com.rio.rostry.`data`.database.dao.RoleUpgradeRequestDao
import com.rio.rostry.`data`.database.dao.RoleUpgradeRequestDao_Impl
import com.rio.rostry.`data`.database.dao.ShowRecordDao
import com.rio.rostry.`data`.database.dao.ShowRecordDao_Impl
import com.rio.rostry.`data`.database.dao.StorageQuotaDao
import com.rio.rostry.`data`.database.dao.StorageQuotaDao_Impl
import com.rio.rostry.`data`.database.dao.StoriesDao
import com.rio.rostry.`data`.database.dao.StoriesDao_Impl
import com.rio.rostry.`data`.database.dao.SyncStateDao
import com.rio.rostry.`data`.database.dao.SyncStateDao_Impl
import com.rio.rostry.`data`.database.dao.TaskDao
import com.rio.rostry.`data`.database.dao.TaskDao_Impl
import com.rio.rostry.`data`.database.dao.TaskRecurrenceDao
import com.rio.rostry.`data`.database.dao.TaskRecurrenceDao_Impl
import com.rio.rostry.`data`.database.dao.ThreadMetadataDao
import com.rio.rostry.`data`.database.dao.ThreadMetadataDao_Impl
import com.rio.rostry.`data`.database.dao.TraitDao
import com.rio.rostry.`data`.database.dao.TraitDao_Impl
import com.rio.rostry.`data`.database.dao.TransactionDao
import com.rio.rostry.`data`.database.dao.TransactionDao_Impl
import com.rio.rostry.`data`.database.dao.TransferDao
import com.rio.rostry.`data`.database.dao.TransferDao_Impl
import com.rio.rostry.`data`.database.dao.TransferVerificationDao
import com.rio.rostry.`data`.database.dao.TransferVerificationDao_Impl
import com.rio.rostry.`data`.database.dao.UploadTaskDao
import com.rio.rostry.`data`.database.dao.UploadTaskDao_Impl
import com.rio.rostry.`data`.database.dao.UserDao
import com.rio.rostry.`data`.database.dao.UserDao_Impl
import com.rio.rostry.`data`.database.dao.UserInterestDao
import com.rio.rostry.`data`.database.dao.UserInterestDao_Impl
import com.rio.rostry.`data`.database.dao.UserProgressDao
import com.rio.rostry.`data`.database.dao.UserProgressDao_Impl
import com.rio.rostry.`data`.database.dao.VaccinationRecordDao
import com.rio.rostry.`data`.database.dao.VaccinationRecordDao_Impl
import com.rio.rostry.`data`.database.dao.VerificationDraftDao
import com.rio.rostry.`data`.database.dao.VerificationDraftDao_Impl
import com.rio.rostry.`data`.database.dao.VerificationRequestDao
import com.rio.rostry.`data`.database.dao.VerificationRequestDao_Impl
import com.rio.rostry.`data`.database.dao.VirtualArenaDao
import com.rio.rostry.`data`.database.dao.VirtualArenaDao_Impl
import com.rio.rostry.`data`.database.dao.WishlistDao
import com.rio.rostry.`data`.database.dao.WishlistDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass
import androidx.room.util.FtsTableInfo.Companion.read as ftsTableInfoRead
import androidx.room.util.TableInfo.Companion.read as tableInfoRead

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _userDao: Lazy<UserDao> = lazy {
    UserDao_Impl(this)
  }

  private val _productDao: Lazy<ProductDao> = lazy {
    ProductDao_Impl(this)
  }

  private val _orderDao: Lazy<OrderDao> = lazy {
    OrderDao_Impl(this)
  }

  private val _transferDao: Lazy<TransferDao> = lazy {
    TransferDao_Impl(this)
  }

  private val _coinDao: Lazy<CoinDao> = lazy {
    CoinDao_Impl(this)
  }

  private val _notificationDao: Lazy<NotificationDao> = lazy {
    NotificationDao_Impl(this)
  }

  private val _alertDao: Lazy<AlertDao> = lazy {
    AlertDao_Impl(this)
  }

  private val _transactionDao: Lazy<TransactionDao> = lazy {
    TransactionDao_Impl(this)
  }

  private val _productTrackingDao: Lazy<ProductTrackingDao> = lazy {
    ProductTrackingDao_Impl(this)
  }

  private val _familyTreeDao: Lazy<FamilyTreeDao> = lazy {
    FamilyTreeDao_Impl(this)
  }

  private val _chatMessageDao: Lazy<ChatMessageDao> = lazy {
    ChatMessageDao_Impl(this)
  }

  private val _syncStateDao: Lazy<SyncStateDao> = lazy {
    SyncStateDao_Impl(this)
  }

  private val _farmVerificationDao: Lazy<FarmVerificationDao> = lazy {
    FarmVerificationDao_Impl(this)
  }

  private val _enthusiastVerificationDao: Lazy<EnthusiastVerificationDao> = lazy {
    EnthusiastVerificationDao_Impl(this)
  }

  private val _auctionDao: Lazy<AuctionDao> = lazy {
    AuctionDao_Impl(this)
  }

  private val _bidDao: Lazy<BidDao> = lazy {
    BidDao_Impl(this)
  }

  private val _cartDao: Lazy<CartDao> = lazy {
    CartDao_Impl(this)
  }

  private val _wishlistDao: Lazy<WishlistDao> = lazy {
    WishlistDao_Impl(this)
  }

  private val _paymentDao: Lazy<PaymentDao> = lazy {
    PaymentDao_Impl(this)
  }

  private val _coinLedgerDao: Lazy<CoinLedgerDao> = lazy {
    CoinLedgerDao_Impl(this)
  }

  private val _deliveryHubDao: Lazy<DeliveryHubDao> = lazy {
    DeliveryHubDao_Impl(this)
  }

  private val _orderTrackingEventDao: Lazy<OrderTrackingEventDao> = lazy {
    OrderTrackingEventDao_Impl(this)
  }

  private val _invoiceDao: Lazy<InvoiceDao> = lazy {
    InvoiceDao_Impl(this)
  }

  private val _refundDao: Lazy<RefundDao> = lazy {
    RefundDao_Impl(this)
  }

  private val _breedingRecordDao: Lazy<BreedingRecordDao> = lazy {
    BreedingRecordDao_Impl(this)
  }

  private val _traitDao: Lazy<TraitDao> = lazy {
    TraitDao_Impl(this)
  }

  private val _productTraitDao: Lazy<ProductTraitDao> = lazy {
    ProductTraitDao_Impl(this)
  }

  private val _lifecycleEventDao: Lazy<LifecycleEventDao> = lazy {
    LifecycleEventDao_Impl(this)
  }

  private val _transferVerificationDao: Lazy<TransferVerificationDao> = lazy {
    TransferVerificationDao_Impl(this)
  }

  private val _disputeDao: Lazy<DisputeDao> = lazy {
    DisputeDao_Impl(this)
  }

  private val _auditLogDao: Lazy<AuditLogDao> = lazy {
    AuditLogDao_Impl(this)
  }

  private val _adminAuditDao: Lazy<AdminAuditDao> = lazy {
    AdminAuditDao_Impl(this)
  }

  private val _roleUpgradeRequestDao: Lazy<RoleUpgradeRequestDao> = lazy {
    RoleUpgradeRequestDao_Impl(this)
  }

  private val _farmAssetDao: Lazy<FarmAssetDao> = lazy {
    FarmAssetDao_Impl(this)
  }

  private val _inventoryItemDao: Lazy<InventoryItemDao> = lazy {
    InventoryItemDao_Impl(this)
  }

  private val _marketListingDao: Lazy<MarketListingDao> = lazy {
    MarketListingDao_Impl(this)
  }

  private val _expenseDao: Lazy<ExpenseDao> = lazy {
    ExpenseDao_Impl(this)
  }

  private val _growthRecordDao: Lazy<GrowthRecordDao> = lazy {
    GrowthRecordDao_Impl(this)
  }

  private val _quarantineRecordDao: Lazy<QuarantineRecordDao> = lazy {
    QuarantineRecordDao_Impl(this)
  }

  private val _mortalityRecordDao: Lazy<MortalityRecordDao> = lazy {
    MortalityRecordDao_Impl(this)
  }

  private val _vaccinationRecordDao: Lazy<VaccinationRecordDao> = lazy {
    VaccinationRecordDao_Impl(this)
  }

  private val _hatchingBatchDao: Lazy<HatchingBatchDao> = lazy {
    HatchingBatchDao_Impl(this)
  }

  private val _hatchingLogDao: Lazy<HatchingLogDao> = lazy {
    HatchingLogDao_Impl(this)
  }

  private val _postsDao: Lazy<PostsDao> = lazy {
    PostsDao_Impl(this)
  }

  private val _commentsDao: Lazy<CommentsDao> = lazy {
    CommentsDao_Impl(this)
  }

  private val _likesDao: Lazy<LikesDao> = lazy {
    LikesDao_Impl(this)
  }

  private val _followsDao: Lazy<FollowsDao> = lazy {
    FollowsDao_Impl(this)
  }

  private val _groupsDao: Lazy<GroupsDao> = lazy {
    GroupsDao_Impl(this)
  }

  private val _groupMembersDao: Lazy<GroupMembersDao> = lazy {
    GroupMembersDao_Impl(this)
  }

  private val _eventsDao: Lazy<EventsDao> = lazy {
    EventsDao_Impl(this)
  }

  private val _expertBookingsDao: Lazy<ExpertBookingsDao> = lazy {
    ExpertBookingsDao_Impl(this)
  }

  private val _moderationReportsDao: Lazy<ModerationReportsDao> = lazy {
    ModerationReportsDao_Impl(this)
  }

  private val _badgesDao: Lazy<BadgesDao> = lazy {
    BadgesDao_Impl(this)
  }

  private val _reputationDao: Lazy<ReputationDao> = lazy {
    ReputationDao_Impl(this)
  }

  private val _outgoingMessageDao: Lazy<OutgoingMessageDao> = lazy {
    OutgoingMessageDao_Impl(this)
  }

  private val _rateLimitDao: Lazy<RateLimitDao> = lazy {
    RateLimitDao_Impl(this)
  }

  private val _eventRsvpsDao: Lazy<EventRsvpsDao> = lazy {
    EventRsvpsDao_Impl(this)
  }

  private val _analyticsDao: Lazy<AnalyticsDao> = lazy {
    AnalyticsDao_Impl(this)
  }

  private val _reportsDao: Lazy<ReportsDao> = lazy {
    ReportsDao_Impl(this)
  }

  private val _storiesDao: Lazy<StoriesDao> = lazy {
    StoriesDao_Impl(this)
  }

  private val _reviewDao: Lazy<ReviewDao> = lazy {
    ReviewDao_Impl(this)
  }

  private val _orderEvidenceDao: Lazy<OrderEvidenceDao> = lazy {
    OrderEvidenceDao_Impl(this)
  }

  private val _orderQuoteDao: Lazy<OrderQuoteDao> = lazy {
    OrderQuoteDao_Impl(this)
  }

  private val _orderPaymentDao: Lazy<OrderPaymentDao> = lazy {
    OrderPaymentDao_Impl(this)
  }

  private val _deliveryConfirmationDao: Lazy<DeliveryConfirmationDao> = lazy {
    DeliveryConfirmationDao_Impl(this)
  }

  private val _orderDisputeDao: Lazy<OrderDisputeDao> = lazy {
    OrderDisputeDao_Impl(this)
  }

  private val _orderAuditLogDao: Lazy<OrderAuditLogDao> = lazy {
    OrderAuditLogDao_Impl(this)
  }

  private val _achievementDao: Lazy<AchievementDao> = lazy {
    AchievementDao_Impl(this)
  }

  private val _userProgressDao: Lazy<UserProgressDao> = lazy {
    UserProgressDao_Impl(this)
  }

  private val _badgeDefDao: Lazy<BadgeDefDao> = lazy {
    BadgeDefDao_Impl(this)
  }

  private val _leaderboardDao: Lazy<LeaderboardDao> = lazy {
    LeaderboardDao_Impl(this)
  }

  private val _rewardDefDao: Lazy<RewardDefDao> = lazy {
    RewardDefDao_Impl(this)
  }

  private val _threadMetadataDao: Lazy<ThreadMetadataDao> = lazy {
    ThreadMetadataDao_Impl(this)
  }

  private val _communityRecommendationDao: Lazy<CommunityRecommendationDao> = lazy {
    CommunityRecommendationDao_Impl(this)
  }

  private val _userInterestDao: Lazy<UserInterestDao> = lazy {
    UserInterestDao_Impl(this)
  }

  private val _expertProfileDao: Lazy<ExpertProfileDao> = lazy {
    ExpertProfileDao_Impl(this)
  }

  private val _outboxDao: Lazy<OutboxDao> = lazy {
    OutboxDao_Impl(this)
  }

  private val _breedingPairDao: Lazy<BreedingPairDao> = lazy {
    BreedingPairDao_Impl(this)
  }

  private val _farmAlertDao: Lazy<FarmAlertDao> = lazy {
    FarmAlertDao_Impl(this)
  }

  private val _listingDraftDao: Lazy<ListingDraftDao> = lazy {
    ListingDraftDao_Impl(this)
  }

  private val _farmerDashboardSnapshotDao: Lazy<FarmerDashboardSnapshotDao> = lazy {
    FarmerDashboardSnapshotDao_Impl(this)
  }

  private val _matingLogDao: Lazy<MatingLogDao> = lazy {
    MatingLogDao_Impl(this)
  }

  private val _eggCollectionDao: Lazy<EggCollectionDao> = lazy {
    EggCollectionDao_Impl(this)
  }

  private val _enthusiastDashboardSnapshotDao: Lazy<EnthusiastDashboardSnapshotDao> = lazy {
    EnthusiastDashboardSnapshotDao_Impl(this)
  }

  private val _uploadTaskDao: Lazy<UploadTaskDao> = lazy {
    UploadTaskDao_Impl(this)
  }

  private val _verificationDraftDao: Lazy<VerificationDraftDao> = lazy {
    VerificationDraftDao_Impl(this)
  }

  private val _roleMigrationDao: Lazy<RoleMigrationDao> = lazy {
    RoleMigrationDao_Impl(this)
  }

  private val _storageQuotaDao: Lazy<StorageQuotaDao> = lazy {
    StorageQuotaDao_Impl(this)
  }

  private val _dailyLogDao: Lazy<DailyLogDao> = lazy {
    DailyLogDao_Impl(this)
  }

  private val _taskDao: Lazy<TaskDao> = lazy {
    TaskDao_Impl(this)
  }

  private val _breedDao: Lazy<BreedDao> = lazy {
    BreedDao_Impl(this)
  }

  private val _dailyBirdLogDao: Lazy<DailyBirdLogDao> = lazy {
    DailyBirdLogDao_Impl(this)
  }

  private val _virtualArenaDao: Lazy<VirtualArenaDao> = lazy {
    VirtualArenaDao_Impl(this)
  }

  private val _batchSummaryDao: Lazy<BatchSummaryDao> = lazy {
    BatchSummaryDao_Impl(this)
  }

  private val _dashboardCacheDao: Lazy<DashboardCacheDao> = lazy {
    DashboardCacheDao_Impl(this)
  }

  private val _showRecordDao: Lazy<ShowRecordDao> = lazy {
    ShowRecordDao_Impl(this)
  }

  private val _verificationRequestDao: Lazy<VerificationRequestDao> = lazy {
    VerificationRequestDao_Impl(this)
  }

  private val _farmActivityLogDao: Lazy<FarmActivityLogDao> = lazy {
    FarmActivityLogDao_Impl(this)
  }

  private val _farmEventDao: Lazy<FarmEventDao> = lazy {
    FarmEventDao_Impl(this)
  }

  private val _farmProfileDao: Lazy<FarmProfileDao> = lazy {
    FarmProfileDao_Impl(this)
  }

  private val _farmTimelineEventDao: Lazy<FarmTimelineEventDao> = lazy {
    FarmTimelineEventDao_Impl(this)
  }

  private val _medicalEventDao: Lazy<MedicalEventDao> = lazy {
    MedicalEventDao_Impl(this)
  }

  private val _clutchDao: Lazy<ClutchDao> = lazy {
    ClutchDao_Impl(this)
  }

  private val _birdTraitRecordDao: Lazy<BirdTraitRecordDao> = lazy {
    BirdTraitRecordDao_Impl(this)
  }

  private val _breedingPlanDao: Lazy<BreedingPlanDao> = lazy {
    BreedingPlanDao_Impl(this)
  }

  private val _arenaParticipantDao: Lazy<ArenaParticipantDao> = lazy {
    ArenaParticipantDao_Impl(this)
  }

  private val _digitalTwinDao: Lazy<DigitalTwinDao> = lazy {
    DigitalTwinDao_Impl(this)
  }

  private val _birdEventDao: Lazy<BirdEventDao> = lazy {
    BirdEventDao_Impl(this)
  }

  private val _assetLifecycleEventDao: Lazy<AssetLifecycleEventDao> = lazy {
    AssetLifecycleEventDao_Impl(this)
  }

  private val _assetHealthRecordDao: Lazy<AssetHealthRecordDao> = lazy {
    AssetHealthRecordDao_Impl(this)
  }

  private val _taskRecurrenceDao: Lazy<TaskRecurrenceDao> = lazy {
    TaskRecurrenceDao_Impl(this)
  }

  private val _assetBatchOperationDao: Lazy<AssetBatchOperationDao> = lazy {
    AssetBatchOperationDao_Impl(this)
  }

  private val _complianceRuleDao: Lazy<ComplianceRuleDao> = lazy {
    ComplianceRuleDao_Impl(this)
  }

  private val _mediaItemDao: Lazy<MediaItemDao> = lazy {
    MediaItemDao_Impl(this)
  }

  private val _mediaTagDao: Lazy<MediaTagDao> = lazy {
    MediaTagDao_Impl(this)
  }

  private val _mediaCacheMetadataDao: Lazy<MediaCacheMetadataDao> = lazy {
    MediaCacheMetadataDao_Impl(this)
  }

  private val _galleryFilterStateDao: Lazy<GalleryFilterStateDao> = lazy {
    GalleryFilterStateDao_Impl(this)
  }

  private val _errorLogDao: Lazy<ErrorLogDao> = lazy {
    ErrorLogDao_Impl(this)
  }

  private val _configurationCacheDao: Lazy<ConfigurationCacheDao> = lazy {
    ConfigurationCacheDao_Impl(this)
  }

  private val _circuitBreakerMetricsDao: Lazy<CircuitBreakerMetricsDao> = lazy {
    CircuitBreakerMetricsDao_Impl(this)
  }

  private val _mediaMetadataDao: Lazy<MediaMetadataDao> = lazy {
    MediaMetadataDao_Impl(this)
  }

  private val _hubAssignmentDao: Lazy<HubAssignmentDao> = lazy {
    HubAssignmentDao_Impl(this)
  }

  private val _profitabilityMetricsDao: Lazy<ProfitabilityMetricsDao> = lazy {
    ProfitabilityMetricsDao_Impl(this)
  }

  private val _referentialIntegrityDao: Lazy<ReferentialIntegrityDao> = lazy {
    ReferentialIntegrityDao_Impl(this)
  }

  private val _moderationBlocklistDao: Lazy<ModerationBlocklistDao> = lazy {
    ModerationBlocklistDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(91,
        "616bc532eac80a4e1adfe8c6a05cc27d", "8c0582db04539a3f9ceed67a98278abc") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`userId` TEXT NOT NULL, `phoneNumber` TEXT, `email` TEXT, `fullName` TEXT, `address` TEXT, `bio` TEXT, `profilePictureUrl` TEXT, `userType` TEXT NOT NULL, `verificationStatus` TEXT NOT NULL, `farmAddressLine1` TEXT, `farmAddressLine2` TEXT, `farmCity` TEXT, `farmState` TEXT, `farmPostalCode` TEXT, `farmCountry` TEXT, `farmLocationLat` REAL, `farmLocationLng` REAL, `locationVerified` INTEGER, `kycLevel` INTEGER, `chickenCount` INTEGER, `farmerType` TEXT, `raisingSince` INTEGER, `favoriteBreed` TEXT, `kycVerifiedAt` INTEGER, `kycRejectionReason` TEXT, `verificationRejectionReason` TEXT, `latestVerificationId` TEXT, `latestVerificationRef` TEXT, `verificationSubmittedAt` INTEGER, `showcaseCount` INTEGER NOT NULL, `maxShowcaseSlots` INTEGER NOT NULL, `createdAt` INTEGER, `updatedAt` INTEGER, `customClaimsUpdatedAt` INTEGER, `isSuspended` INTEGER NOT NULL, `suspensionReason` TEXT, `suspensionEndsAt` INTEGER, `notificationsEnabled` INTEGER NOT NULL, `farmAlertsEnabled` INTEGER NOT NULL, `transferAlertsEnabled` INTEGER NOT NULL, `socialAlertsEnabled` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `products` (`productId` TEXT NOT NULL, `sellerId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `price` REAL NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `location` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `imageUrls` TEXT NOT NULL, `status` TEXT NOT NULL, `condition` TEXT, `harvestDate` INTEGER, `expiryDate` INTEGER, `birthDate` INTEGER, `vaccinationRecordsJson` TEXT, `weightGrams` REAL, `heightCm` REAL, `gender` TEXT, `color` TEXT, `breed` TEXT, `raisingPurpose` TEXT, `healthStatus` TEXT, `birdCode` TEXT, `colorTag` TEXT, `familyTreeId` TEXT, `sourceAssetId` TEXT, `parentIdsJson` TEXT, `breedingStatus` TEXT, `transferHistoryJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `lastModifiedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, `stage` TEXT, `lifecycleStatus` TEXT, `parentMaleId` TEXT, `parentFemaleId` TEXT, `ageWeeks` INTEGER, `lastStageTransitionAt` INTEGER, `breederEligibleAt` INTEGER, `isBatch` INTEGER, `batchId` TEXT, `splitAt` INTEGER, `splitIntoIds` TEXT, `documentUrls` TEXT NOT NULL, `qrCodeUrl` TEXT, `customStatus` TEXT, `debug` INTEGER NOT NULL, `deliveryOptions` TEXT NOT NULL, `deliveryCost` REAL, `leadTimeDays` INTEGER, `motherId` TEXT, `isBreedingUnit` INTEGER NOT NULL, `eggsCollectedToday` INTEGER NOT NULL, `lastEggLogDate` INTEGER, `readyForSale` INTEGER NOT NULL, `targetWeight` REAL, `isShowcased` INTEGER NOT NULL, `externalVideoUrl` TEXT, `recordsLockedAt` INTEGER, `autoLockAfterDays` INTEGER NOT NULL, `lineageHistoryJson` TEXT, `editCount` INTEGER NOT NULL, `lastEditedBy` TEXT, `adminFlagged` INTEGER NOT NULL, `moderationNote` TEXT, `metadataJson` TEXT NOT NULL, PRIMARY KEY(`productId`), FOREIGN KEY(`sellerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_sellerId` ON `products` (`sellerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_category` ON `products` (`category`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_status` ON `products` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_sourceAssetId` ON `products` (`sourceAssetId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_sellerId_lifecycleStatus` ON `products` (`sellerId`, `lifecycleStatus`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_sellerId_isBatch` ON `products` (`sellerId`, `isBatch`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_birthDate` ON `products` (`birthDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_updatedAt` ON `products` (`updatedAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_products_createdAt` ON `products` (`createdAt`)")
        connection.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `products_fts` USING FTS4(`productId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `breed` TEXT, `location` TEXT NOT NULL, `condition` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `orders` (`orderId` TEXT NOT NULL, `buyerId` TEXT, `sellerId` TEXT NOT NULL, `productId` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT, `totalAmount` REAL NOT NULL, `status` TEXT NOT NULL, `shippingAddress` TEXT NOT NULL, `paymentMethod` TEXT, `paymentStatus` TEXT NOT NULL, `orderDate` INTEGER NOT NULL, `expectedDeliveryDate` INTEGER, `actualDeliveryDate` INTEGER, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `lastModifiedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, `deliveryMethod` TEXT, `deliveryType` TEXT, `deliveryAddressJson` TEXT, `negotiationStatus` TEXT, `negotiatedPrice` REAL, `originalPrice` REAL, `cancellationReason` TEXT, `cancellationTime` INTEGER, `billImageUri` TEXT, `paymentSlipUri` TEXT, `otp` TEXT, `otpEntered` TEXT, `isVerified` INTEGER NOT NULL, PRIMARY KEY(`orderId`), FOREIGN KEY(`buyerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_orders_buyerId` ON `orders` (`buyerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_orders_sellerId` ON `orders` (`sellerId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_items` (`orderId` TEXT NOT NULL, `productId` TEXT NOT NULL, `quantity` REAL NOT NULL, `priceAtPurchase` REAL NOT NULL, `unitAtPurchase` TEXT NOT NULL, PRIMARY KEY(`orderId`, `productId`), FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE RESTRICT )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_items_orderId` ON `order_items` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_items_productId` ON `order_items` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transfers` (`transferId` TEXT NOT NULL, `productId` TEXT, `fromUserId` TEXT, `toUserId` TEXT, `orderId` TEXT, `amount` REAL NOT NULL, `currency` TEXT NOT NULL, `type` TEXT NOT NULL, `status` TEXT NOT NULL, `transactionReference` TEXT, `notes` TEXT, `gpsLat` REAL, `gpsLng` REAL, `sellerPhotoUrl` TEXT, `buyerPhotoUrl` TEXT, `timeoutAt` INTEGER, `conditionsJson` TEXT, `transferCode` TEXT, `lineageSnapshotJson` TEXT, `claimedAt` INTEGER, `transferType` TEXT NOT NULL, `growthSnapshotJson` TEXT, `healthSnapshotJson` TEXT, `transferCodeExpiresAt` INTEGER, `initiatedAt` INTEGER NOT NULL, `completedAt` INTEGER, `updatedAt` INTEGER NOT NULL, `lastModifiedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `mergedAt` INTEGER, `mergeCount` INTEGER NOT NULL, PRIMARY KEY(`transferId`), FOREIGN KEY(`fromUserId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`toUserId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfers_fromUserId` ON `transfers` (`fromUserId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfers_toUserId` ON `transfers` (`toUserId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfers_orderId` ON `transfers` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfers_productId` ON `transfers` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfers_syncedAt` ON `transfers` (`syncedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `coins` (`coinTransactionId` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `type` TEXT NOT NULL, `description` TEXT, `relatedTransferId` TEXT, `relatedOrderId` TEXT, `transactionDate` INTEGER NOT NULL, PRIMARY KEY(`coinTransactionId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`relatedTransferId`) REFERENCES `transfers`(`transferId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_coins_userId` ON `coins` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_coins_relatedTransferId` ON `coins` (`relatedTransferId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`notificationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `type` TEXT NOT NULL, `deepLinkUrl` TEXT, `isRead` INTEGER NOT NULL, `imageUrl` TEXT, `createdAt` INTEGER NOT NULL, `isBatched` INTEGER NOT NULL, `batchedAt` INTEGER, `displayedAt` INTEGER, `domain` TEXT, `userPreferenceEnabled` INTEGER NOT NULL, PRIMARY KEY(`notificationId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_userId` ON `notifications` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_isBatched` ON `notifications` (`isBatched`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_domain` ON `notifications` (`domain`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `alerts` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `title` TEXT NOT NULL, `message` TEXT NOT NULL, `severity` TEXT NOT NULL, `type` TEXT NOT NULL, `relatedId` TEXT, `createdAt` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `isDismissed` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `product_tracking` (`trackingId` TEXT NOT NULL, `productId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `status` TEXT NOT NULL, `metadataJson` TEXT, `timestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, PRIMARY KEY(`trackingId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`ownerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tracking_productId` ON `product_tracking` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tracking_ownerId` ON `product_tracking` (`ownerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_tracking_productId_timestamp` ON `product_tracking` (`productId`, `timestamp`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `family_tree` (`nodeId` TEXT NOT NULL, `productId` TEXT NOT NULL, `parentProductId` TEXT, `childProductId` TEXT, `relationType` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, PRIMARY KEY(`nodeId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`parentProductId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`childProductId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_family_tree_productId` ON `family_tree` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_family_tree_parentProductId` ON `family_tree` (`parentProductId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_family_tree_childProductId` ON `family_tree` (`childProductId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_family_tree_productId_parentProductId_childProductId` ON `family_tree` (`productId`, `parentProductId`, `childProductId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`messageId` TEXT NOT NULL, `senderId` TEXT NOT NULL, `receiverId` TEXT NOT NULL, `body` TEXT NOT NULL, `mediaUrl` TEXT, `sentAt` INTEGER NOT NULL, `deliveredAt` INTEGER, `readAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `lastModifiedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `deviceTimestamp` INTEGER NOT NULL, `type` TEXT NOT NULL, `metadata` TEXT, PRIMARY KEY(`messageId`), FOREIGN KEY(`senderId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`receiverId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_senderId` ON `chat_messages` (`senderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_receiverId` ON `chat_messages` (`receiverId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_senderId_receiverId` ON `chat_messages` (`senderId`, `receiverId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_syncedAt` ON `chat_messages` (`syncedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `sync_state` (`id` TEXT NOT NULL, `lastSyncAt` INTEGER NOT NULL, `lastUserSyncAt` INTEGER NOT NULL, `lastProductSyncAt` INTEGER NOT NULL, `lastOrderSyncAt` INTEGER NOT NULL, `lastTrackingSyncAt` INTEGER NOT NULL, `lastTransferSyncAt` INTEGER NOT NULL, `lastChatSyncAt` INTEGER NOT NULL, `lastBreedingSyncAt` INTEGER NOT NULL, `lastAlertSyncAt` INTEGER NOT NULL, `lastDashboardSyncAt` INTEGER NOT NULL, `lastVaccinationSyncAt` INTEGER NOT NULL, `lastGrowthSyncAt` INTEGER NOT NULL, `lastQuarantineSyncAt` INTEGER NOT NULL, `lastMortalitySyncAt` INTEGER NOT NULL, `lastHatchingSyncAt` INTEGER NOT NULL, `lastHatchingLogSyncAt` INTEGER NOT NULL, `lastEnthusiastBreedingSyncAt` INTEGER NOT NULL, `lastEnthusiastDashboardSyncAt` INTEGER NOT NULL, `lastDailyLogSyncAt` INTEGER NOT NULL, `lastBatchSummarySyncAt` INTEGER NOT NULL, `lastTaskSyncAt` INTEGER NOT NULL, `lastExpenseSyncAt` INTEGER NOT NULL, `lastProofSyncAt` INTEGER NOT NULL, `lastGeneticAnalysisSyncAt` INTEGER NOT NULL, `lastIoTDeviceSyncAt` INTEGER NOT NULL, `lastIoTDataSyncAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `auctions` (`auctionId` TEXT NOT NULL, `productId` TEXT NOT NULL, `sellerId` TEXT NOT NULL, `startsAt` INTEGER NOT NULL, `endsAt` INTEGER NOT NULL, `closedAt` INTEGER, `closedBy` TEXT, `minPrice` REAL NOT NULL, `currentPrice` REAL NOT NULL, `reservePrice` REAL, `buyNowPrice` REAL, `bidIncrement` REAL NOT NULL, `bidCount` INTEGER NOT NULL, `winnerId` TEXT, `isReserveMet` INTEGER NOT NULL, `extensionCount` INTEGER NOT NULL, `maxExtensions` INTEGER NOT NULL, `extensionMinutes` INTEGER NOT NULL, `status` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `viewCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`auctionId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_auctions_productId` ON `auctions` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_auctions_sellerId` ON `auctions` (`sellerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_auctions_status` ON `auctions` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_auctions_status_endsAt` ON `auctions` (`status`, `endsAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bids` (`bidId` TEXT NOT NULL, `auctionId` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `placedAt` INTEGER NOT NULL, `isAutoBid` INTEGER NOT NULL, `maxAmount` REAL, `isWinning` INTEGER NOT NULL, `wasOutbid` INTEGER NOT NULL, `outbidAt` INTEGER, `outbidNotified` INTEGER NOT NULL, `isRetracted` INTEGER NOT NULL, `retractedReason` TEXT, PRIMARY KEY(`bidId`), FOREIGN KEY(`auctionId`) REFERENCES `auctions`(`auctionId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_auctionId` ON `bids` (`auctionId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_userId` ON `bids` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bids_auctionId_amount` ON `bids` (`auctionId`, `amount`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cart_items` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `productId` TEXT NOT NULL, `quantity` REAL NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cart_items_userId` ON `cart_items` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_cart_items_productId` ON `cart_items` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `wishlist` (`userId` TEXT NOT NULL, `productId` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `productId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_wishlist_userId` ON `wishlist` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_wishlist_productId` ON `wishlist` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `payments` (`paymentId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `userId` TEXT NOT NULL, `method` TEXT NOT NULL, `amount` REAL NOT NULL, `currency` TEXT NOT NULL, `status` TEXT NOT NULL, `providerRef` TEXT, `upiUri` TEXT, `idempotencyKey` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`paymentId`), FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_orderId` ON `payments` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_payments_userId` ON `payments` (`userId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_payments_idempotencyKey` ON `payments` (`idempotencyKey`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `coin_ledger` (`entryId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `coins` INTEGER NOT NULL, `amountInInr` REAL NOT NULL, `refId` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`entryId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_coin_ledger_userId` ON `coin_ledger` (`userId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `delivery_hubs` (`hubId` TEXT NOT NULL, `name` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `address` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`hubId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_tracking_events` (`eventId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `status` TEXT NOT NULL, `hubId` TEXT, `note` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`eventId`), FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`hubId`) REFERENCES `delivery_hubs`(`hubId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_tracking_events_orderId` ON `order_tracking_events` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_tracking_events_hubId` ON `order_tracking_events` (`hubId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `invoices` (`invoiceId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `subtotal` REAL NOT NULL, `gstPercent` REAL NOT NULL, `gstAmount` REAL NOT NULL, `total` REAL NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`invoiceId`), FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_invoices_orderId` ON `invoices` (`orderId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `invoice_lines` (`lineId` TEXT NOT NULL, `invoiceId` TEXT NOT NULL, `description` TEXT NOT NULL, `qty` REAL NOT NULL, `unitPrice` REAL NOT NULL, `lineTotal` REAL NOT NULL, PRIMARY KEY(`lineId`), FOREIGN KEY(`invoiceId`) REFERENCES `invoices`(`invoiceId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_invoice_lines_invoiceId` ON `invoice_lines` (`invoiceId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `refunds` (`refundId` TEXT NOT NULL, `paymentId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `amount` REAL NOT NULL, `reason` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`refundId`), FOREIGN KEY(`paymentId`) REFERENCES `payments`(`paymentId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_refunds_paymentId` ON `refunds` (`paymentId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_refunds_orderId` ON `refunds` (`orderId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `breeding_records` (`recordId` TEXT NOT NULL, `parentId` TEXT NOT NULL, `partnerId` TEXT NOT NULL, `childId` TEXT NOT NULL, `success` INTEGER NOT NULL, `notes` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`recordId`), FOREIGN KEY(`parentId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`partnerId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`childId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_records_parentId` ON `breeding_records` (`parentId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_records_partnerId` ON `breeding_records` (`partnerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_records_childId` ON `breeding_records` (`childId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `traits` (`traitId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, PRIMARY KEY(`traitId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `product_traits` (`productId` TEXT NOT NULL, `traitId` TEXT NOT NULL, PRIMARY KEY(`productId`, `traitId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`traitId`) REFERENCES `traits`(`traitId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_product_traits_traitId` ON `product_traits` (`traitId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `lifecycle_events` (`eventId` TEXT NOT NULL, `productId` TEXT NOT NULL, `week` INTEGER NOT NULL, `stage` TEXT NOT NULL, `type` TEXT NOT NULL, `notes` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`eventId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_lifecycle_events_productId` ON `lifecycle_events` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_lifecycle_events_week` ON `lifecycle_events` (`week`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transfer_verifications` (`verificationId` TEXT NOT NULL, `transferId` TEXT NOT NULL, `step` TEXT NOT NULL, `status` TEXT NOT NULL, `photoBeforeUrl` TEXT, `photoAfterUrl` TEXT, `photoBeforeMetaJson` TEXT, `photoAfterMetaJson` TEXT, `gpsLat` REAL, `gpsLng` REAL, `identityDocType` TEXT, `identityDocRef` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`verificationId`), FOREIGN KEY(`transferId`) REFERENCES `transfers`(`transferId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfer_verifications_transferId` ON `transfer_verifications` (`transferId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transfer_verifications_status` ON `transfer_verifications` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `disputes` (`disputeId` TEXT NOT NULL, `transferId` TEXT NOT NULL, `reporterId` TEXT NOT NULL, `reportedUserId` TEXT NOT NULL, `reason` TEXT NOT NULL, `description` TEXT NOT NULL, `evidenceUrls` TEXT NOT NULL, `status` TEXT NOT NULL, `resolution` TEXT, `resolvedByAdminId` TEXT, `createdAt` INTEGER NOT NULL, `resolvedAt` INTEGER, PRIMARY KEY(`disputeId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `audit_logs` (`logId` TEXT NOT NULL, `type` TEXT NOT NULL, `refId` TEXT NOT NULL, `action` TEXT NOT NULL, `actorUserId` TEXT, `detailsJson` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`logId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_audit_logs_refId` ON `audit_logs` (`refId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_audit_logs_type` ON `audit_logs` (`type`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `admin_audit_logs` (`logId` TEXT NOT NULL, `adminId` TEXT NOT NULL, `adminName` TEXT, `actionType` TEXT NOT NULL, `targetId` TEXT, `targetType` TEXT, `details` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`logId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_admin_audit_logs_adminId` ON `admin_audit_logs` (`adminId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_admin_audit_logs_actionType` ON `admin_audit_logs` (`actionType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_admin_audit_logs_timestamp` ON `admin_audit_logs` (`timestamp`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_admin_audit_logs_targetId` ON `admin_audit_logs` (`targetId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `posts` (`postId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `type` TEXT NOT NULL, `text` TEXT, `mediaUrl` TEXT, `thumbnailUrl` TEXT, `productId` TEXT, `hashtags` TEXT, `mentions` TEXT, `parentPostId` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`postId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_authorId` ON `posts` (`authorId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_createdAt` ON `posts` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_posts_type` ON `posts` (`type`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`commentId` TEXT NOT NULL, `postId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`commentId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_postId` ON `comments` (`postId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_authorId` ON `comments` (`authorId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_createdAt` ON `comments` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `likes` (`likeId` TEXT NOT NULL, `postId` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`likeId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_likes_postId_userId` ON `likes` (`postId`, `userId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `follows` (`followId` TEXT NOT NULL, `followerId` TEXT NOT NULL, `followedId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`followId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_follows_followerId_followedId` ON `follows` (`followerId`, `followedId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `groups` (`groupId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `ownerId` TEXT NOT NULL, `category` TEXT, `isMarketplace` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`groupId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_groups_ownerId` ON `groups` (`ownerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_groups_name` ON `groups` (`name`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `group_members` (`membershipId` TEXT NOT NULL, `groupId` TEXT NOT NULL, `userId` TEXT NOT NULL, `role` TEXT NOT NULL, `joinedAt` INTEGER NOT NULL, PRIMARY KEY(`membershipId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_group_members_groupId_userId` ON `group_members` (`groupId`, `userId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `events` (`eventId` TEXT NOT NULL, `groupId` TEXT, `title` TEXT NOT NULL, `description` TEXT, `location` TEXT, `startTime` INTEGER NOT NULL, `endTime` INTEGER, PRIMARY KEY(`eventId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_events_groupId` ON `events` (`groupId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_events_startTime` ON `events` (`startTime`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `expert_bookings` (`bookingId` TEXT NOT NULL, `expertId` TEXT NOT NULL, `userId` TEXT NOT NULL, `topic` TEXT, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`bookingId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expert_bookings_expertId` ON `expert_bookings` (`expertId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expert_bookings_userId` ON `expert_bookings` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expert_bookings_startTime` ON `expert_bookings` (`startTime`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `moderation_reports` (`reportId` TEXT NOT NULL, `targetType` TEXT NOT NULL, `targetId` TEXT NOT NULL, `reporterId` TEXT NOT NULL, `reason` TEXT NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`reportId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_moderation_reports_targetType` ON `moderation_reports` (`targetType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_moderation_reports_targetId` ON `moderation_reports` (`targetId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_moderation_reports_status` ON `moderation_reports` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `badges` (`badgeId` TEXT NOT NULL, `userId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `awardedAt` INTEGER NOT NULL, PRIMARY KEY(`badgeId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_badges_userId` ON `badges` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_badges_awardedAt` ON `badges` (`awardedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `reputation` (`repId` TEXT NOT NULL, `userId` TEXT NOT NULL, `score` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`repId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_reputation_userId` ON `reputation` (`userId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `outgoing_messages` (`id` TEXT NOT NULL, `kind` TEXT NOT NULL, `threadOrGroupId` TEXT NOT NULL, `fromUserId` TEXT NOT NULL, `toUserId` TEXT, `bodyText` TEXT, `fileUri` TEXT, `fileName` TEXT, `status` TEXT NOT NULL, `priority` INTEGER NOT NULL, `retryCount` INTEGER NOT NULL, `maxRetries` INTEGER NOT NULL, `lastError` TEXT, `sentAt` INTEGER, `deliveredAt` INTEGER, `readAt` INTEGER, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outgoing_messages_status` ON `outgoing_messages` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outgoing_messages_createdAt` ON `outgoing_messages` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outgoing_messages_priority` ON `outgoing_messages` (`priority`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `rate_limits` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `action` TEXT NOT NULL, `lastAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_rate_limits_userId_action` ON `rate_limits` (`userId`, `action`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `event_rsvps` (`id` TEXT NOT NULL, `eventId` TEXT NOT NULL, `userId` TEXT NOT NULL, `status` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_event_rsvps_eventId_userId` ON `event_rsvps` (`eventId`, `userId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `analytics_daily` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `role` TEXT NOT NULL, `dateKey` TEXT NOT NULL, `salesRevenue` REAL NOT NULL, `ordersCount` INTEGER NOT NULL, `productViews` INTEGER NOT NULL, `likesCount` INTEGER NOT NULL, `commentsCount` INTEGER NOT NULL, `transfersCount` INTEGER NOT NULL, `breedingSuccessRate` REAL NOT NULL, `engagementScore` REAL NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_analytics_daily_userId_dateKey` ON `analytics_daily` (`userId`, `dateKey`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_analytics_daily_role` ON `analytics_daily` (`role`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `reports` (`reportId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `periodStart` INTEGER NOT NULL, `periodEnd` INTEGER NOT NULL, `format` TEXT NOT NULL, `uri` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`reportId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reports_userId` ON `reports` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reports_periodStart` ON `reports` (`periodStart`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reports_type` ON `reports` (`type`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `stories` (`storyId` TEXT NOT NULL, `authorId` TEXT NOT NULL, `mediaUrl` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL, PRIMARY KEY(`storyId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stories_authorId` ON `stories` (`authorId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_stories_expiresAt` ON `stories` (`expiresAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `growth_records` (`recordId` TEXT NOT NULL, `productId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `week` INTEGER NOT NULL, `weightGrams` REAL, `heightCm` REAL, `photoUrl` TEXT, `mediaItemsJson` TEXT, `healthStatus` TEXT, `milestone` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `correctionOf` TEXT, `editCount` INTEGER NOT NULL, `lastEditedBy` TEXT, `isBatchLevel` INTEGER NOT NULL, `sourceBatchId` TEXT, PRIMARY KEY(`recordId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_productId` ON `growth_records` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_week` ON `growth_records` (`week`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_farmerId` ON `growth_records` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_growth_records_createdAt` ON `growth_records` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `quarantine_records` (`quarantineId` TEXT NOT NULL, `productId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `reason` TEXT NOT NULL, `protocol` TEXT, `medicationScheduleJson` TEXT, `statusHistoryJson` TEXT, `vetNotes` TEXT, `startedAt` INTEGER NOT NULL, `lastUpdatedAt` INTEGER NOT NULL, `updatesCount` INTEGER NOT NULL, `endedAt` INTEGER, `status` TEXT NOT NULL, `healthScore` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`quarantineId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_productId` ON `quarantine_records` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_status` ON `quarantine_records` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_farmerId` ON `quarantine_records` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_quarantine_records_startedAt` ON `quarantine_records` (`startedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `mortality_records` (`deathId` TEXT NOT NULL, `productId` TEXT, `farmerId` TEXT NOT NULL, `causeCategory` TEXT NOT NULL, `circumstances` TEXT, `ageWeeks` INTEGER, `disposalMethod` TEXT, `quantity` INTEGER NOT NULL, `financialImpactInr` REAL, `photoUrls` TEXT, `mediaItemsJson` TEXT, `occurredAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `affectedProductIds` TEXT, `affectsAllChildren` INTEGER NOT NULL, PRIMARY KEY(`deathId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_productId` ON `mortality_records` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_causeCategory` ON `mortality_records` (`causeCategory`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_farmerId` ON `mortality_records` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mortality_records_occurredAt` ON `mortality_records` (`occurredAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `vaccination_records` (`vaccinationId` TEXT NOT NULL, `productId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `vaccineType` TEXT NOT NULL, `supplier` TEXT, `batchCode` TEXT, `doseMl` REAL, `scheduledAt` INTEGER NOT NULL, `administeredAt` INTEGER, `efficacyNotes` TEXT, `costInr` REAL, `photoUrls` TEXT, `mediaItemsJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `correctionOf` TEXT, `editCount` INTEGER NOT NULL, `lastEditedBy` TEXT, PRIMARY KEY(`vaccinationId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_productId` ON `vaccination_records` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_vaccineType` ON `vaccination_records` (`vaccineType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_scheduledAt` ON `vaccination_records` (`scheduledAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_vaccination_records_farmerId` ON `vaccination_records` (`farmerId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `hatching_batches` (`batchId` TEXT NOT NULL, `name` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `expectedHatchAt` INTEGER, `temperatureC` REAL, `humidityPct` REAL, `eggsCount` INTEGER, `sourceCollectionId` TEXT, `notes` TEXT, `status` TEXT NOT NULL, `hatchedAt` INTEGER, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`batchId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_batches_name` ON `hatching_batches` (`name`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_batches_farmerId` ON `hatching_batches` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_batches_expectedHatchAt` ON `hatching_batches` (`expectedHatchAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `hatching_logs` (`logId` TEXT NOT NULL, `batchId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `productId` TEXT, `eventType` TEXT NOT NULL, `qualityScore` INTEGER, `temperatureC` REAL, `humidityPct` REAL, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`logId`), FOREIGN KEY(`batchId`) REFERENCES `hatching_batches`(`batchId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_batchId` ON `hatching_logs` (`batchId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_productId` ON `hatching_logs` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_farmerId` ON `hatching_logs` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hatching_logs_createdAt` ON `hatching_logs` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `achievements_def` (`achievementId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `points` INTEGER NOT NULL, `category` TEXT, `icon` TEXT, PRIMARY KEY(`achievementId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `user_progress` (`id` TEXT NOT NULL, `userId` TEXT NOT NULL, `achievementId` TEXT NOT NULL, `progress` INTEGER NOT NULL, `target` INTEGER NOT NULL, `unlockedAt` INTEGER, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `badges_def` (`badgeId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `icon` TEXT, PRIMARY KEY(`badgeId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `leaderboard` (`id` TEXT NOT NULL, `periodKey` TEXT NOT NULL, `userId` TEXT NOT NULL, `score` INTEGER NOT NULL, `rank` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `rewards_def` (`rewardId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `pointsRequired` INTEGER NOT NULL, PRIMARY KEY(`rewardId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `thread_metadata` (`threadId` TEXT NOT NULL, `title` TEXT, `contextType` TEXT, `relatedEntityId` TEXT, `topic` TEXT, `participantIds` TEXT NOT NULL, `lastMessageAt` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`threadId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_thread_metadata_contextType` ON `thread_metadata` (`contextType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_thread_metadata_lastMessageAt` ON `thread_metadata` (`lastMessageAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_thread_metadata_createdAt` ON `thread_metadata` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `community_recommendations` (`recommendationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `type` TEXT NOT NULL, `targetId` TEXT NOT NULL, `score` REAL NOT NULL, `reason` TEXT, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL, `dismissed` INTEGER NOT NULL, PRIMARY KEY(`recommendationId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_userId` ON `community_recommendations` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_type` ON `community_recommendations` (`type`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_score` ON `community_recommendations` (`score`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_community_recommendations_expiresAt` ON `community_recommendations` (`expiresAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `user_interests` (`interestId` TEXT NOT NULL, `userId` TEXT NOT NULL, `category` TEXT NOT NULL, `value` TEXT NOT NULL, `weight` REAL NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`interestId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_interests_userId_category_value` ON `user_interests` (`userId`, `category`, `value`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `expert_profiles` (`userId` TEXT NOT NULL, `specialties` TEXT NOT NULL, `bio` TEXT, `rating` REAL NOT NULL, `totalConsultations` INTEGER NOT NULL, `availableForBooking` INTEGER NOT NULL, `hourlyRate` REAL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `outbox` (`outboxId` TEXT NOT NULL, `userId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `operation` TEXT NOT NULL, `payloadJson` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `retryCount` INTEGER NOT NULL, `lastAttemptAt` INTEGER, `status` TEXT NOT NULL, `priority` TEXT NOT NULL, `maxRetries` INTEGER NOT NULL, `contextJson` TEXT, PRIMARY KEY(`outboxId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_userId` ON `outbox` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_status` ON `outbox` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_createdAt` ON `outbox` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_priority` ON `outbox` (`priority`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_status_priority_createdAt` ON `outbox` (`status`, `priority`, `createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `breeding_pairs` (`pairId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `maleProductId` TEXT NOT NULL, `femaleProductId` TEXT NOT NULL, `pairedAt` INTEGER NOT NULL, `status` TEXT NOT NULL, `hatchSuccessRate` REAL NOT NULL, `eggsCollected` INTEGER NOT NULL, `hatchedEggs` INTEGER NOT NULL, `separatedAt` INTEGER, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`pairId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_maleProductId` ON `breeding_pairs` (`maleProductId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_femaleProductId` ON `breeding_pairs` (`femaleProductId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_farmerId` ON `breeding_pairs` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_pairs_status` ON `breeding_pairs` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_alerts` (`alertId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `alertType` TEXT NOT NULL, `severity` TEXT NOT NULL, `message` TEXT NOT NULL, `actionRoute` TEXT, `isRead` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`alertId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_alerts_farmerId` ON `farm_alerts` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_alerts_isRead` ON `farm_alerts` (`isRead`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_alerts_createdAt` ON `farm_alerts` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `listing_drafts` (`draftId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `step` TEXT NOT NULL, `formDataJson` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `expiresAt` INTEGER, PRIMARY KEY(`draftId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_listing_drafts_farmerId` ON `listing_drafts` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_listing_drafts_updatedAt` ON `listing_drafts` (`updatedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farmer_dashboard_snapshots` (`snapshotId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `weekStartAt` INTEGER NOT NULL, `weekEndAt` INTEGER NOT NULL, `revenueInr` REAL NOT NULL, `ordersCount` INTEGER NOT NULL, `hatchSuccessRate` REAL NOT NULL, `mortalityRate` REAL NOT NULL, `deathsCount` INTEGER NOT NULL, `vaccinationCompletionRate` REAL NOT NULL, `growthRecordsCount` INTEGER NOT NULL, `quarantineActiveCount` INTEGER NOT NULL, `productsReadyToListCount` INTEGER NOT NULL, `avgFeedKg` REAL, `medicationUsageCount` INTEGER, `dailyLogComplianceRate` REAL, `actionSuggestions` TEXT, `transfersInitiatedCount` INTEGER NOT NULL, `transfersCompletedCount` INTEGER NOT NULL, `complianceScore` REAL NOT NULL, `onboardingCount` INTEGER NOT NULL, `dailyGoalsCompletedCount` INTEGER NOT NULL, `analyticsInsightsCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`snapshotId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farmer_dashboard_snapshots_farmerId` ON `farmer_dashboard_snapshots` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farmer_dashboard_snapshots_weekStartAt` ON `farmer_dashboard_snapshots` (`weekStartAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `mating_logs` (`logId` TEXT NOT NULL, `pairId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `matedAt` INTEGER NOT NULL, `observedBehavior` TEXT, `environmentalConditions` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`logId`), FOREIGN KEY(`pairId`) REFERENCES `breeding_pairs`(`pairId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mating_logs_pairId` ON `mating_logs` (`pairId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mating_logs_farmerId` ON `mating_logs` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_mating_logs_matedAt` ON `mating_logs` (`matedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `egg_collections` (`collectionId` TEXT NOT NULL, `pairId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `eggsCollected` INTEGER NOT NULL, `collectedAt` INTEGER NOT NULL, `qualityGrade` TEXT NOT NULL, `weight` REAL, `notes` TEXT, `goodCount` INTEGER NOT NULL, `damagedCount` INTEGER NOT NULL, `brokenCount` INTEGER NOT NULL, `trayLayoutJson` TEXT, `setForHatching` INTEGER NOT NULL, `linkedBatchId` TEXT, `setForHatchingAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`collectionId`), FOREIGN KEY(`pairId`) REFERENCES `breeding_pairs`(`pairId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_egg_collections_pairId` ON `egg_collections` (`pairId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_egg_collections_farmerId` ON `egg_collections` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_egg_collections_collectedAt` ON `egg_collections` (`collectedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `enthusiast_dashboard_snapshots` (`snapshotId` TEXT NOT NULL, `userId` TEXT NOT NULL, `weekStartAt` INTEGER NOT NULL, `weekEndAt` INTEGER NOT NULL, `hatchRateLast30Days` REAL NOT NULL, `breederSuccessRate` REAL NOT NULL, `disputedTransfersCount` INTEGER NOT NULL, `topBloodlinesEngagement` TEXT, `activePairsCount` INTEGER NOT NULL, `eggsCollectedCount` INTEGER NOT NULL, `hatchingDueCount` INTEGER NOT NULL, `transfersPendingCount` INTEGER NOT NULL, `pairsToMateCount` INTEGER NOT NULL, `incubatingCount` INTEGER NOT NULL, `sickBirdsCount` INTEGER NOT NULL, `eggsCollectedToday` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`snapshotId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_enthusiast_dashboard_snapshots_userId` ON `enthusiast_dashboard_snapshots` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_enthusiast_dashboard_snapshots_weekStartAt` ON `enthusiast_dashboard_snapshots` (`weekStartAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `upload_tasks` (`taskId` TEXT NOT NULL, `localPath` TEXT NOT NULL, `remotePath` TEXT NOT NULL, `status` TEXT NOT NULL, `progress` INTEGER NOT NULL, `retries` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `error` TEXT, `contextJson` TEXT, PRIMARY KEY(`taskId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `daily_logs` (`logId` TEXT NOT NULL, `productId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `logDate` INTEGER NOT NULL, `weightGrams` REAL, `feedKg` REAL, `medicationJson` TEXT, `symptomsJson` TEXT, `activityLevel` TEXT, `photoUrls` TEXT, `notes` TEXT, `temperature` REAL, `humidity` REAL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `deviceTimestamp` INTEGER NOT NULL, `author` TEXT, `mergedAt` INTEGER, `mergeCount` INTEGER NOT NULL, `conflictResolved` INTEGER NOT NULL, `mediaItemsJson` TEXT, PRIMARY KEY(`logId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_productId` ON `daily_logs` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_farmerId` ON `daily_logs` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_logDate` ON `daily_logs` (`logDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_createdAt` ON `daily_logs` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_logs_mergedAt` ON `daily_logs` (`mergedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`taskId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `productId` TEXT, `batchId` TEXT, `taskType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `dueAt` INTEGER NOT NULL, `completedAt` INTEGER, `completedBy` TEXT, `priority` TEXT NOT NULL, `recurrence` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `snoozeUntil` INTEGER, `metadata` TEXT, `mergedAt` INTEGER, `mergeCount` INTEGER NOT NULL, PRIMARY KEY(`taskId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_farmerId` ON `tasks` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_productId` ON `tasks` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_taskType` ON `tasks` (`taskType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_dueAt` ON `tasks` (`dueAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_completedAt` ON `tasks` (`completedAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_mergedAt` ON `tasks` (`mergedAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_farmerId_completedAt_dueAt` ON `tasks` (`farmerId`, `completedAt`, `dueAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `breeds` (`breedId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `culinaryProfile` TEXT NOT NULL, `farmingDifficulty` TEXT NOT NULL, `imageUrl` TEXT, `tags` TEXT NOT NULL, PRIMARY KEY(`breedId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_verifications` (`verificationId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `farmLocationLat` REAL, `farmLocationLng` REAL, `farmAddressLine1` TEXT, `farmAddressLine2` TEXT, `farmCity` TEXT, `farmState` TEXT, `farmPostalCode` TEXT, `farmCountry` TEXT, `verificationDocumentUrls` TEXT NOT NULL, `gpsAccuracy` REAL, `gpsTimestamp` INTEGER, `status` TEXT NOT NULL, `submittedAt` INTEGER, `reviewedAt` INTEGER, `reviewedBy` TEXT, `rejectionReason` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`verificationId`), FOREIGN KEY(`farmerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_verifications_farmerId` ON `farm_verifications` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_verifications_status` ON `farm_verifications` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_assets` (`assetId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `name` TEXT NOT NULL, `assetType` TEXT NOT NULL, `category` TEXT NOT NULL, `status` TEXT NOT NULL, `isShowcase` INTEGER NOT NULL, `locationName` TEXT, `latitude` REAL, `longitude` REAL, `quantity` REAL NOT NULL, `initialQuantity` REAL NOT NULL, `unit` TEXT NOT NULL, `birthDate` INTEGER, `ageWeeks` INTEGER, `breed` TEXT, `gender` TEXT, `color` TEXT, `healthStatus` TEXT NOT NULL, `raisingPurpose` TEXT, `description` TEXT NOT NULL, `imageUrls` TEXT NOT NULL, `notes` TEXT, `lifecycleSubStage` TEXT, `parentIdsJson` TEXT, `batchId` TEXT, `origin` TEXT, `birdCode` TEXT, `acquisitionPrice` REAL, `acquisitionDate` INTEGER, `acquisitionSource` TEXT, `acquisitionSourceId` TEXT, `acquisitionNotes` TEXT, `estimatedValue` REAL, `lastVaccinationDate` INTEGER, `nextVaccinationDate` INTEGER, `weightGrams` REAL, `metadataJson` TEXT NOT NULL, `listedAt` INTEGER, `listingId` TEXT, `soldAt` INTEGER, `soldToUserId` TEXT, `soldPrice` REAL, `previousOwnerId` TEXT, `transferredAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, PRIMARY KEY(`assetId`), FOREIGN KEY(`farmerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_assets_farmerId` ON `farm_assets` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_assets_assetType` ON `farm_assets` (`assetType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_assets_status` ON `farm_assets` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_inventory` (`inventoryId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `sourceAssetId` TEXT, `sourceBatchId` TEXT, `name` TEXT NOT NULL, `sku` TEXT, `category` TEXT NOT NULL, `quantityAvailable` REAL NOT NULL, `quantityReserved` REAL NOT NULL, `unit` TEXT NOT NULL, `producedAt` INTEGER, `expiresAt` INTEGER, `qualityGrade` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`inventoryId`), FOREIGN KEY(`farmerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`sourceAssetId`) REFERENCES `farm_assets`(`assetId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_inventory_farmerId` ON `farm_inventory` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_inventory_sourceAssetId` ON `farm_inventory` (`sourceAssetId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_inventory_sku` ON `farm_inventory` (`sku`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `market_listings` (`listingId` TEXT NOT NULL, `sellerId` TEXT NOT NULL, `inventoryId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `price` REAL NOT NULL, `currency` TEXT NOT NULL, `priceUnit` TEXT NOT NULL, `category` TEXT NOT NULL, `tags` TEXT NOT NULL, `deliveryOptions` TEXT NOT NULL, `deliveryCost` REAL, `locationName` TEXT, `latitude` REAL, `longitude` REAL, `minOrderQuantity` REAL NOT NULL, `maxOrderQuantity` REAL, `imageUrls` TEXT NOT NULL, `status` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `viewsCount` INTEGER NOT NULL, `inquiriesCount` INTEGER NOT NULL, `leadTimeDays` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `expiresAt` INTEGER, `dirty` INTEGER NOT NULL, PRIMARY KEY(`listingId`), FOREIGN KEY(`sellerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`inventoryId`) REFERENCES `farm_inventory`(`inventoryId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_market_listings_sellerId` ON `market_listings` (`sellerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_market_listings_inventoryId` ON `market_listings` (`inventoryId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_market_listings_status` ON `market_listings` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_market_listings_category` ON `market_listings` (`category`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `reviews` (`reviewId` TEXT NOT NULL, `productId` TEXT, `sellerId` TEXT NOT NULL, `orderId` TEXT, `reviewerId` TEXT NOT NULL, `rating` INTEGER NOT NULL, `title` TEXT, `content` TEXT, `isVerifiedPurchase` INTEGER NOT NULL, `helpfulCount` INTEGER NOT NULL, `responseFromSeller` TEXT, `responseAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `adminFlagged` INTEGER NOT NULL, `moderationNote` TEXT, PRIMARY KEY(`reviewId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_productId` ON `reviews` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_sellerId` ON `reviews` (`sellerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_reviewerId` ON `reviews` (`reviewerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_reviews_createdAt` ON `reviews` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `review_helpful` (`reviewId` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`reviewId`, `userId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_review_helpful_reviewId` ON `review_helpful` (`reviewId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `rating_stats` (`statsId` TEXT NOT NULL, `sellerId` TEXT, `productId` TEXT, `averageRating` REAL NOT NULL, `totalReviews` INTEGER NOT NULL, `rating5Count` INTEGER NOT NULL, `rating4Count` INTEGER NOT NULL, `rating3Count` INTEGER NOT NULL, `rating2Count` INTEGER NOT NULL, `rating1Count` INTEGER NOT NULL, `verifiedPurchaseCount` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`statsId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_rating_stats_sellerId` ON `rating_stats` (`sellerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_rating_stats_productId` ON `rating_stats` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_evidence` (`evidenceId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `evidenceType` TEXT NOT NULL, `uploadedBy` TEXT NOT NULL, `uploadedByRole` TEXT NOT NULL, `imageUri` TEXT, `videoUri` TEXT, `textContent` TEXT, `geoLatitude` REAL, `geoLongitude` REAL, `geoAddress` TEXT, `isVerified` INTEGER NOT NULL, `verifiedBy` TEXT, `verifiedAt` INTEGER, `verificationNote` TEXT, `deviceTimestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`evidenceId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_evidence_orderId` ON `order_evidence` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_evidence_uploadedBy` ON `order_evidence` (`uploadedBy`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_evidence_evidenceType` ON `order_evidence` (`evidenceType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_evidence_createdAt` ON `order_evidence` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_quotes` (`quoteId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `buyerId` TEXT NOT NULL, `sellerId` TEXT NOT NULL, `productId` TEXT NOT NULL, `productName` TEXT NOT NULL, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `basePrice` REAL NOT NULL, `totalProductPrice` REAL NOT NULL, `deliveryCharge` REAL NOT NULL, `packingCharge` REAL NOT NULL, `platformFee` REAL NOT NULL, `discount` REAL NOT NULL, `finalTotal` REAL NOT NULL, `deliveryType` TEXT NOT NULL, `deliveryDistance` REAL, `deliveryAddress` TEXT, `deliveryLatitude` REAL, `deliveryLongitude` REAL, `pickupAddress` TEXT, `pickupLatitude` REAL, `pickupLongitude` REAL, `paymentType` TEXT NOT NULL, `advanceAmount` REAL, `balanceAmount` REAL, `status` TEXT NOT NULL, `buyerAgreedAt` INTEGER, `sellerAgreedAt` INTEGER, `lockedAt` INTEGER, `expiresAt` INTEGER, `version` INTEGER NOT NULL, `previousQuoteId` TEXT, `buyerNotes` TEXT, `sellerNotes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`quoteId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_quotes_orderId` ON `order_quotes` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_quotes_buyerId` ON `order_quotes` (`buyerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_quotes_sellerId` ON `order_quotes` (`sellerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_quotes_status` ON `order_quotes` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_payments` (`paymentId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `quoteId` TEXT NOT NULL, `payerId` TEXT NOT NULL, `receiverId` TEXT NOT NULL, `paymentPhase` TEXT NOT NULL, `amount` REAL NOT NULL, `currency` TEXT NOT NULL, `method` TEXT NOT NULL, `upiId` TEXT, `bankDetails` TEXT, `status` TEXT NOT NULL, `proofEvidenceId` TEXT, `transactionRef` TEXT, `verifiedAt` INTEGER, `verifiedBy` TEXT, `rejectionReason` TEXT, `refundedAmount` REAL, `refundedAt` INTEGER, `refundReason` TEXT, `dueAt` INTEGER NOT NULL, `expiredAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`paymentId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_payments_orderId` ON `order_payments` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_payments_payerId` ON `order_payments` (`payerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_payments_paymentPhase` ON `order_payments` (`paymentPhase`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_payments_status` ON `order_payments` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `delivery_confirmations` (`confirmationId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `buyerId` TEXT NOT NULL, `sellerId` TEXT NOT NULL, `deliveryOtp` TEXT NOT NULL, `otpGeneratedAt` INTEGER NOT NULL, `otpExpiresAt` INTEGER NOT NULL, `otpAttempts` INTEGER NOT NULL, `maxOtpAttempts` INTEGER NOT NULL, `status` TEXT NOT NULL, `confirmationMethod` TEXT, `deliveryPhotoEvidenceId` TEXT, `buyerConfirmationEvidenceId` TEXT, `gpsEvidenceId` TEXT, `confirmedAt` INTEGER, `confirmedBy` TEXT, `deliveryNotes` TEXT, `balanceCollected` INTEGER NOT NULL, `balanceCollectedAt` INTEGER, `balanceEvidenceId` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`confirmationId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_delivery_confirmations_orderId` ON `delivery_confirmations` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_delivery_confirmations_status` ON `delivery_confirmations` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_disputes` (`disputeId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `raisedBy` TEXT NOT NULL, `raisedByRole` TEXT NOT NULL, `againstUserId` TEXT NOT NULL, `reason` TEXT NOT NULL, `description` TEXT NOT NULL, `requestedResolution` TEXT, `claimedAmount` REAL, `evidenceIds` TEXT, `status` TEXT NOT NULL, `resolvedAt` INTEGER, `resolvedBy` TEXT, `resolutionType` TEXT, `resolutionNotes` TEXT, `refundedAmount` REAL, `lastResponseAt` INTEGER, `responseCount` INTEGER NOT NULL, `escalatedAt` INTEGER, `escalationReason` TEXT, `adminNotes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`disputeId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_disputes_orderId` ON `order_disputes` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_disputes_raisedBy` ON `order_disputes` (`raisedBy`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_disputes_status` ON `order_disputes` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `order_audit_logs` (`logId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `action` TEXT NOT NULL, `fromState` TEXT, `toState` TEXT, `performedBy` TEXT NOT NULL, `performedByRole` TEXT NOT NULL, `description` TEXT NOT NULL, `metadata` TEXT, `evidenceId` TEXT, `ipAddress` TEXT, `deviceInfo` TEXT, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`logId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_audit_logs_orderId` ON `order_audit_logs` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_audit_logs_performedBy` ON `order_audit_logs` (`performedBy`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_order_audit_logs_timestamp` ON `order_audit_logs` (`timestamp`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `verification_drafts` (`draftId` TEXT NOT NULL, `userId` TEXT NOT NULL, `upgradeType` TEXT, `farmLocationJson` TEXT, `uploadedImagesJson` TEXT, `uploadedDocsJson` TEXT, `uploadedImageTypesJson` TEXT, `uploadedDocTypesJson` TEXT, `uploadProgressJson` TEXT, `lastSavedAt` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `mergedAt` INTEGER, `mergedInto` TEXT, PRIMARY KEY(`draftId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_verification_drafts_userId` ON `verification_drafts` (`userId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `role_migrations` (`migrationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `fromRole` TEXT NOT NULL, `toRole` TEXT NOT NULL, `status` TEXT NOT NULL, `totalItems` INTEGER NOT NULL, `migratedItems` INTEGER NOT NULL, `currentPhase` TEXT, `currentEntity` TEXT, `startedAt` INTEGER, `completedAt` INTEGER, `pausedAt` INTEGER, `lastProgressAt` INTEGER, `errorMessage` TEXT, `retryCount` INTEGER NOT NULL, `maxRetries` INTEGER NOT NULL, `snapshotPath` TEXT, `metadataJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`migrationId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_role_migrations_userId` ON `role_migrations` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_role_migrations_status` ON `role_migrations` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_role_migrations_createdAt` ON `role_migrations` (`createdAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `storage_quota` (`userId` TEXT NOT NULL, `quotaBytes` INTEGER NOT NULL, `publicLimitBytes` INTEGER NOT NULL, `privateLimitBytes` INTEGER NOT NULL, `usedBytes` INTEGER NOT NULL, `publicUsedBytes` INTEGER NOT NULL, `privateUsedBytes` INTEGER NOT NULL, `imageBytes` INTEGER NOT NULL, `documentBytes` INTEGER NOT NULL, `dataBytes` INTEGER NOT NULL, `warningLevel` TEXT NOT NULL, `lastCalculatedAt` INTEGER NOT NULL, `lastSyncedAt` INTEGER, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_storage_quota_lastCalculatedAt` ON `storage_quota` (`lastCalculatedAt`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `daily_bird_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `birdId` TEXT NOT NULL, `date` INTEGER NOT NULL, `activityType` TEXT NOT NULL, `weight` REAL, `feedIntakeGrams` REAL, `notes` TEXT, `performanceRating` INTEGER, `performanceScoreJson` TEXT, `mediaUrlsJson` TEXT, `createdAt` INTEGER NOT NULL, FOREIGN KEY(`birdId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_bird_logs_birdId` ON `daily_bird_logs` (`birdId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_bird_logs_date` ON `daily_bird_logs` (`date`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `competitions` (`competitionId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `region` TEXT NOT NULL, `status` TEXT NOT NULL, `bannerUrl` TEXT, `entryFee` REAL, `prizePool` TEXT, `participantCount` INTEGER NOT NULL, `participantsPreviewJson` TEXT, `rulesJson` TEXT, `bracketsJson` TEXT, `leaderboardJson` TEXT, `galleryUrlsJson` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`competitionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `my_votes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `competitionId` TEXT NOT NULL, `participantId` TEXT NOT NULL, `votedAt` INTEGER NOT NULL, `points` INTEGER NOT NULL, `synced` INTEGER NOT NULL, `syncError` TEXT)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_my_votes_competitionId_participantId` ON `my_votes` (`competitionId`, `participantId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_my_votes_synced` ON `my_votes` (`synced`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `batch_summaries` (`batchId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `batchName` TEXT NOT NULL, `currentCount` INTEGER NOT NULL, `avgWeightGrams` REAL NOT NULL, `totalFeedKg` REAL NOT NULL, `fcr` REAL NOT NULL, `ageWeeks` INTEGER NOT NULL, `hatchDate` INTEGER, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`batchId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_batch_summaries_farmerId` ON `batch_summaries` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_batch_summaries_updatedAt` ON `batch_summaries` (`updatedAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_batch_summaries_dirty` ON `batch_summaries` (`dirty`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `dashboard_cache` (`cacheId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `totalBirds` INTEGER NOT NULL, `totalBatches` INTEGER NOT NULL, `pendingVaccines` INTEGER NOT NULL, `overdueVaccines` INTEGER NOT NULL, `avgFcr` REAL NOT NULL, `totalFeedKgThisMonth` REAL NOT NULL, `totalMortalityThisMonth` INTEGER NOT NULL, `estimatedHarvestDate` INTEGER, `daysUntilHarvest` INTEGER, `healthyCount` INTEGER NOT NULL, `quarantinedCount` INTEGER NOT NULL, `alertCount` INTEGER NOT NULL, `computedAt` INTEGER NOT NULL, `computationDurationMs` INTEGER NOT NULL, PRIMARY KEY(`cacheId`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_dashboard_cache_farmerId` ON `dashboard_cache` (`farmerId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `show_records` (`recordId` TEXT NOT NULL, `productId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `recordType` TEXT NOT NULL, `eventName` TEXT NOT NULL, `eventLocation` TEXT, `eventDate` INTEGER NOT NULL, `result` TEXT NOT NULL, `placement` INTEGER, `totalParticipants` INTEGER, `category` TEXT, `score` REAL, `opponentName` TEXT, `opponentOwnerName` TEXT, `judgesNotes` TEXT, `awards` TEXT, `photoUrls` TEXT NOT NULL, `isVerified` INTEGER NOT NULL, `verifiedBy` TEXT, `certificateUrl` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`recordId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_show_records_productId` ON `show_records` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_show_records_eventDate` ON `show_records` (`eventDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_show_records_recordType` ON `show_records` (`recordType`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `verification_requests` (`requestId` TEXT NOT NULL, `userId` TEXT NOT NULL, `govtIdUrl` TEXT, `farmPhotoUrl` TEXT, `status` TEXT NOT NULL, `rejectionReason` TEXT, `submittedAt` INTEGER, `reviewedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`requestId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_verification_requests_userId` ON `verification_requests` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_verification_requests_status` ON `verification_requests` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_activity_logs` (`activityId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `productId` TEXT, `activityType` TEXT NOT NULL, `amountInr` REAL, `quantity` REAL, `category` TEXT, `description` TEXT, `notes` TEXT, `photoUrls` TEXT, `mediaItemsJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`activityId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_activity_logs_farmerId` ON `farm_activity_logs` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_activity_logs_activityType` ON `farm_activity_logs` (`activityType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_activity_logs_createdAt` ON `farm_activity_logs` (`createdAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_activity_logs_productId` ON `farm_activity_logs` (`productId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_profiles` (`farmerId` TEXT NOT NULL, `farmName` TEXT NOT NULL, `farmBio` TEXT, `logoUrl` TEXT, `coverPhotoUrl` TEXT, `locationName` TEXT, `barangay` TEXT, `municipality` TEXT, `province` TEXT, `latitude` REAL, `longitude` REAL, `isVerified` INTEGER NOT NULL, `verifiedAt` INTEGER, `memberSince` INTEGER NOT NULL, `farmEstablished` INTEGER, `trustScore` INTEGER NOT NULL, `totalBirdsSold` INTEGER NOT NULL, `totalOrdersCompleted` INTEGER NOT NULL, `avgResponseTimeMinutes` INTEGER, `vaccinationRate` INTEGER, `returningBuyerRate` INTEGER, `badgesJson` TEXT NOT NULL, `whatsappNumber` TEXT, `isWhatsappEnabled` INTEGER NOT NULL, `isCallEnabled` INTEGER NOT NULL, `isPublic` INTEGER NOT NULL, `showLocation` INTEGER NOT NULL, `showSalesHistory` INTEGER NOT NULL, `showTimeline` INTEGER NOT NULL, `shareVaccinationLogs` INTEGER NOT NULL, `shareSanitationLogs` INTEGER NOT NULL, `shareFeedLogs` INTEGER NOT NULL, `shareWeightData` INTEGER NOT NULL, `shareSalesActivity` INTEGER NOT NULL, `shareMortalityData` INTEGER NOT NULL, `shareExpenseData` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, PRIMARY KEY(`farmerId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_profiles_isVerified` ON `farm_profiles` (`isVerified`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_profiles_province` ON `farm_profiles` (`province`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_profiles_trustScore` ON `farm_profiles` (`trustScore`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_timeline_events` (`eventId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `iconType` TEXT, `imageUrl` TEXT, `sourceType` TEXT, `sourceId` TEXT, `trustPointsEarned` INTEGER NOT NULL, `isPublic` INTEGER NOT NULL, `isMilestone` INTEGER NOT NULL, `eventDate` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`eventId`), FOREIGN KEY(`farmerId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_timeline_events_farmerId` ON `farm_timeline_events` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_timeline_events_eventType` ON `farm_timeline_events` (`eventType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_timeline_events_eventDate` ON `farm_timeline_events` (`eventDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_timeline_events_isPublic` ON `farm_timeline_events` (`isPublic`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `enthusiast_verifications` (`verificationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `experienceYears` INTEGER, `birdCount` INTEGER, `specializations` TEXT, `achievementsDescription` TEXT, `referenceContacts` TEXT, `verificationDocumentUrls` TEXT NOT NULL, `profilePhotoUrl` TEXT, `farmPhotoUrls` TEXT, `status` TEXT NOT NULL, `submittedAt` INTEGER, `reviewedAt` INTEGER, `reviewedBy` TEXT, `rejectionReason` TEXT, `adminNotes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`verificationId`), FOREIGN KEY(`userId`) REFERENCES `users`(`userId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_enthusiast_verifications_userId` ON `enthusiast_verifications` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_enthusiast_verifications_status` ON `enthusiast_verifications` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_events` (`eventId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `scheduledAt` INTEGER NOT NULL, `completedAt` INTEGER, `recurrence` TEXT NOT NULL, `productId` TEXT, `batchId` TEXT, `reminderBefore` INTEGER NOT NULL, `status` TEXT NOT NULL, `metadata` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`eventId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_events_farmerId` ON `farm_events` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_events_scheduledAt` ON `farm_events` (`scheduledAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_events_eventType` ON `farm_events` (`eventType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_farm_events_status` ON `farm_events` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `role_upgrade_requests` (`requestId` TEXT NOT NULL, `userId` TEXT NOT NULL, `currentRole` TEXT NOT NULL, `requestedRole` TEXT NOT NULL, `status` TEXT NOT NULL, `adminNotes` TEXT, `reviewedBy` TEXT, `reviewedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`requestId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`transactionId` TEXT NOT NULL, `orderId` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `currency` TEXT NOT NULL, `status` TEXT NOT NULL, `paymentMethod` TEXT NOT NULL, `gatewayReference` TEXT, `timestamp` INTEGER NOT NULL, `notes` TEXT, PRIMARY KEY(`transactionId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_orderId` ON `transactions` (`orderId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_userId` ON `transactions` (`userId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_timestamp` ON `transactions` (`timestamp`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_status` ON `transactions` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`expenseId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `assetId` TEXT, `category` TEXT NOT NULL, `amount` REAL NOT NULL, `description` TEXT, `expenseDate` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`expenseId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_farmerId` ON `expenses` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_assetId` ON `expenses` (`assetId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category` ON `expenses` (`category`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_expenseDate` ON `expenses` (`expenseDate`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `medical_events` (`eventId` TEXT NOT NULL, `birdId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `severity` TEXT NOT NULL, `eventDate` INTEGER NOT NULL, `resolvedDate` INTEGER, `diagnosis` TEXT, `symptoms` TEXT, `treatment` TEXT, `medication` TEXT, `dosage` TEXT, `treatmentDuration` TEXT, `status` TEXT NOT NULL, `outcome` TEXT, `treatedBy` TEXT, `vetVisit` INTEGER NOT NULL, `vetNotes` TEXT, `cost` REAL, `notes` TEXT, `mediaUrlsJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`eventId`), FOREIGN KEY(`birdId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_medical_events_birdId` ON `medical_events` (`birdId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_medical_events_farmerId` ON `medical_events` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_medical_events_eventType` ON `medical_events` (`eventType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_medical_events_eventDate` ON `medical_events` (`eventDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_medical_events_status` ON `medical_events` (`status`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `clutches` (`clutchId` TEXT NOT NULL, `breedingPairId` TEXT, `farmerId` TEXT NOT NULL, `sireId` TEXT, `damId` TEXT, `clutchName` TEXT, `clutchNumber` INTEGER, `eggsCollected` INTEGER NOT NULL, `collectionStartDate` INTEGER NOT NULL, `collectionEndDate` INTEGER, `setDate` INTEGER, `eggsSet` INTEGER NOT NULL, `incubatorId` TEXT, `incubationNotes` TEXT, `firstCandleDate` INTEGER, `firstCandleFertile` INTEGER NOT NULL, `firstCandleClear` INTEGER NOT NULL, `firstCandleEarlyDead` INTEGER NOT NULL, `secondCandleDate` INTEGER, `secondCandleAlive` INTEGER NOT NULL, `secondCandleDead` INTEGER NOT NULL, `expectedHatchDate` INTEGER, `actualHatchStartDate` INTEGER, `actualHatchEndDate` INTEGER, `chicksHatched` INTEGER NOT NULL, `chicksMale` INTEGER NOT NULL, `chicksFemale` INTEGER NOT NULL, `chicksUnsexed` INTEGER NOT NULL, `deadInShell` INTEGER NOT NULL, `pippedNotHatched` INTEGER NOT NULL, `averageChickWeight` REAL, `chickQualityScore` INTEGER, `qualityNotes` TEXT, `fertilityRate` REAL, `hatchabilityOfFertile` REAL, `hatchabilityOfSet` REAL, `status` TEXT NOT NULL, `offspringIdsJson` TEXT, `notes` TEXT, `mediaUrlsJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`clutchId`), FOREIGN KEY(`breedingPairId`) REFERENCES `breeding_pairs`(`pairId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_clutches_breedingPairId` ON `clutches` (`breedingPairId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_clutches_farmerId` ON `clutches` (`farmerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_clutches_status` ON `clutches` (`status`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_clutches_setDate` ON `clutches` (`setDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_clutches_expectedHatchDate` ON `clutches` (`expectedHatchDate`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bird_trait_records` (`recordId` TEXT NOT NULL, `productId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `traitCategory` TEXT NOT NULL, `traitName` TEXT NOT NULL, `traitValue` TEXT NOT NULL, `traitUnit` TEXT, `numericValue` REAL, `ageWeeks` INTEGER, `recordedAt` INTEGER NOT NULL, `measuredBy` TEXT, `notes` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`recordId`), FOREIGN KEY(`productId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_trait_records_productId` ON `bird_trait_records` (`productId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_trait_records_ownerId` ON `bird_trait_records` (`ownerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_trait_records_traitCategory` ON `bird_trait_records` (`traitCategory`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_trait_records_traitName` ON `bird_trait_records` (`traitName`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_trait_records_recordedAt` ON `bird_trait_records` (`recordedAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_trait_records_productId_traitName_ageWeeks` ON `bird_trait_records` (`productId`, `traitName`, `ageWeeks`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `breeding_plans` (`planId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `sireId` TEXT, `sireName` TEXT, `damId` TEXT, `damName` TEXT, `createdAt` INTEGER NOT NULL, `note` TEXT, `simulatedOffspringJson` TEXT NOT NULL, `status` TEXT NOT NULL, `priority` INTEGER NOT NULL, PRIMARY KEY(`planId`), FOREIGN KEY(`sireId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`damId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE SET NULL )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_plans_sireId` ON `breeding_plans` (`sireId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_plans_damId` ON `breeding_plans` (`damId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_breeding_plans_farmerId` ON `breeding_plans` (`farmerId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `arena_participants` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `competitionId` TEXT NOT NULL, `birdId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `birdName` TEXT NOT NULL, `birdImageUrl` TEXT, `breed` TEXT NOT NULL, `entryTime` INTEGER NOT NULL, `totalVotes` INTEGER NOT NULL, `averageScore` REAL NOT NULL, `rank` INTEGER NOT NULL, FOREIGN KEY(`birdId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`competitionId`) REFERENCES `competitions`(`competitionId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_arena_participants_birdId` ON `arena_participants` (`birdId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_arena_participants_competitionId` ON `arena_participants` (`competitionId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_arena_participants_ownerId` ON `arena_participants` (`ownerId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `digital_twins` (`twinId` TEXT NOT NULL, `birdId` TEXT NOT NULL, `registryId` TEXT, `ownerId` TEXT NOT NULL, `birdName` TEXT, `baseBreed` TEXT NOT NULL, `strainType` TEXT, `localStrainName` TEXT, `geneticPurityScore` INTEGER, `bodyType` TEXT, `boneDensityScore` INTEGER, `heightCm` REAL, `weightKg` REAL, `beakType` TEXT, `combType` TEXT, `skinColor` TEXT, `legColor` TEXT, `spurType` TEXT, `morphologyScore` INTEGER, `primaryBodyColor` INTEGER, `neckHackleColor` INTEGER, `wingHighlightColor` INTEGER, `tailColor` INTEGER, `tailIridescent` INTEGER NOT NULL, `plumagePattern` TEXT, `localColorCode` TEXT, `colorCategoryCode` TEXT, `lifecycleStage` TEXT NOT NULL, `ageDays` INTEGER, `maturityScore` INTEGER, `breedingStatus` TEXT NOT NULL, `gender` TEXT, `birthDate` INTEGER, `sireId` TEXT, `damId` TEXT, `generationDepth` INTEGER NOT NULL, `inbreedingCoefficient` REAL, `geneticsJson` TEXT, `geneticsScore` INTEGER, `vaccinationCount` INTEGER NOT NULL, `injuryCount` INTEGER NOT NULL, `staminaScore` INTEGER, `healthScore` INTEGER, `currentHealthStatus` TEXT NOT NULL, `aggressionIndex` INTEGER, `enduranceScore` INTEGER, `intelligenceScore` INTEGER, `totalFights` INTEGER NOT NULL, `fightWins` INTEGER NOT NULL, `performanceScore` INTEGER, `valuationScore` INTEGER, `verifiedStatus` INTEGER NOT NULL, `certificationLevel` TEXT NOT NULL, `estimatedValueInr` REAL, `totalShows` INTEGER NOT NULL, `showWins` INTEGER NOT NULL, `bestPlacement` INTEGER, `totalBreedingAttempts` INTEGER NOT NULL, `successfulBreedings` INTEGER NOT NULL, `totalOffspring` INTEGER NOT NULL, `appearanceJson` TEXT, `metadataJson` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, `isDeleted` INTEGER NOT NULL, `deletedAt` INTEGER, PRIMARY KEY(`twinId`), FOREIGN KEY(`birdId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_digital_twins_birdId` ON `digital_twins` (`birdId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_digital_twins_ownerId` ON `digital_twins` (`ownerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_digital_twins_baseBreed` ON `digital_twins` (`baseBreed`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_digital_twins_strainType` ON `digital_twins` (`strainType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_digital_twins_lifecycleStage` ON `digital_twins` (`lifecycleStage`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_digital_twins_certificationLevel` ON `digital_twins` (`certificationLevel`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_digital_twins_dirty` ON `digital_twins` (`dirty`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bird_events` (`eventId` TEXT NOT NULL, `birdId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `eventTitle` TEXT NOT NULL, `eventDescription` TEXT, `eventDate` INTEGER NOT NULL, `ageDaysAtEvent` INTEGER, `lifecycleStageAtEvent` TEXT, `numericValue` REAL, `numericValue2` REAL, `stringValue` TEXT, `dataJson` TEXT, `morphologyScoreDelta` INTEGER, `geneticsScoreDelta` INTEGER, `performanceScoreDelta` INTEGER, `healthScoreDelta` INTEGER, `marketScoreDelta` INTEGER, `recordedBy` TEXT, `isVerified` INTEGER NOT NULL, `verifiedBy` TEXT, `mediaUrlsJson` TEXT, `createdAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`eventId`), FOREIGN KEY(`birdId`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_birdId` ON `bird_events` (`birdId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_ownerId` ON `bird_events` (`ownerId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_eventType` ON `bird_events` (`eventType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_eventDate` ON `bird_events` (`eventDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_birdId_eventType` ON `bird_events` (`birdId`, `eventType`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_birdId_eventDate` ON `bird_events` (`birdId`, `eventDate`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bird_events_dirty` ON `bird_events` (`dirty`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `asset_lifecycle_events` (`eventId` TEXT NOT NULL, `assetId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `fromStage` TEXT, `toStage` TEXT, `eventData` TEXT NOT NULL, `triggeredBy` TEXT NOT NULL, `occurredAt` INTEGER NOT NULL, `recordedAt` INTEGER NOT NULL, `recordedBy` TEXT NOT NULL, `notes` TEXT, `mediaItemsJson` TEXT, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`eventId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `asset_health_records` (`recordId` TEXT NOT NULL, `assetId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `recordType` TEXT NOT NULL, `recordData` TEXT NOT NULL, `healthScore` INTEGER NOT NULL, `veterinarianId` TEXT, `veterinarianNotes` TEXT, `followUpRequired` INTEGER NOT NULL, `followUpDate` INTEGER, `costInr` REAL, `mediaItemsJson` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `dirty` INTEGER NOT NULL, `syncedAt` INTEGER, PRIMARY KEY(`recordId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `task_recurrences` (`recurrenceId` TEXT NOT NULL, `taskId` TEXT NOT NULL, `pattern` TEXT NOT NULL, `interval` INTEGER NOT NULL, `daysOfWeek` TEXT, `endDate` INTEGER, `maxOccurrences` INTEGER, `currentOccurrence` INTEGER NOT NULL, `lastGenerated` INTEGER, `nextDue` INTEGER, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`recurrenceId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `asset_batch_operations` (`operationId` TEXT NOT NULL, `farmerId` TEXT NOT NULL, `operationType` TEXT NOT NULL, `selectionCriteria` TEXT NOT NULL, `operationData` TEXT NOT NULL, `status` TEXT NOT NULL, `totalItems` INTEGER NOT NULL, `processedItems` INTEGER NOT NULL, `successfulItems` INTEGER NOT NULL, `failedItems` INTEGER NOT NULL, `errorLog` TEXT, `canRollback` INTEGER NOT NULL, `rollbackData` TEXT, `startedAt` INTEGER, `completedAt` INTEGER, `estimatedDuration` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`operationId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `compliance_rules` (`ruleId` TEXT NOT NULL, `jurisdiction` TEXT NOT NULL, `ruleType` TEXT NOT NULL, `assetTypes` TEXT NOT NULL, `ruleData` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `effectiveFrom` INTEGER NOT NULL, `effectiveUntil` INTEGER, `severity` TEXT NOT NULL, `description` TEXT NOT NULL, `reminderDays` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`ruleId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `media_items` (`mediaId` TEXT NOT NULL, `assetId` TEXT, `url` TEXT NOT NULL, `localPath` TEXT, `mediaType` TEXT NOT NULL, `dateAdded` INTEGER NOT NULL, `fileSize` INTEGER NOT NULL, `width` INTEGER, `height` INTEGER, `duration` INTEGER, `thumbnailUrl` TEXT, `uploadStatus` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `isCached` INTEGER NOT NULL, `lastAccessedAt` INTEGER, `dirty` INTEGER NOT NULL, PRIMARY KEY(`mediaId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_assetId` ON `media_items` (`assetId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_dateAdded` ON `media_items` (`dateAdded`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_uploadStatus` ON `media_items` (`uploadStatus`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_items_mediaType` ON `media_items` (`mediaType`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `media_tags` (`mediaId` TEXT NOT NULL, `tagId` TEXT NOT NULL, `tagType` TEXT NOT NULL, `value` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`mediaId`, `tagId`), FOREIGN KEY(`mediaId`) REFERENCES `media_items`(`mediaId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_tags_mediaId` ON `media_tags` (`mediaId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_tags_tagType_value` ON `media_tags` (`tagType`, `value`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `media_cache_metadata` (`mediaId` TEXT NOT NULL, `localPath` TEXT NOT NULL, `fileSize` INTEGER NOT NULL, `downloadedAt` INTEGER NOT NULL, `lastAccessedAt` INTEGER NOT NULL, `accessCount` INTEGER NOT NULL, PRIMARY KEY(`mediaId`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_cache_metadata_lastAccessedAt` ON `media_cache_metadata` (`lastAccessedAt`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_cache_metadata_fileSize` ON `media_cache_metadata` (`fileSize`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `gallery_filter_state` (`id` TEXT NOT NULL, `ageGroupsJson` TEXT NOT NULL, `sourceTypesJson` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `error_logs` (`id` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `user_id` TEXT, `operation_name` TEXT NOT NULL, `error_category` TEXT NOT NULL, `error_message` TEXT NOT NULL, `stack_trace` TEXT, `additional_data` TEXT, `reported` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_error_logs_timestamp` ON `error_logs` (`timestamp`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_error_logs_user_id` ON `error_logs` (`user_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_error_logs_error_category` ON `error_logs` (`error_category`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `configuration_cache` (`key` TEXT NOT NULL, `value` TEXT NOT NULL, `value_type` TEXT NOT NULL, `last_updated` INTEGER NOT NULL, `source` TEXT NOT NULL, PRIMARY KEY(`key`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_configuration_cache_last_updated` ON `configuration_cache` (`last_updated`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `circuit_breaker_metrics` (`service_name` TEXT NOT NULL, `state` TEXT NOT NULL, `failure_rate` REAL NOT NULL, `total_calls` INTEGER NOT NULL, `failed_calls` INTEGER NOT NULL, `last_state_change` INTEGER NOT NULL, `last_updated` INTEGER NOT NULL, PRIMARY KEY(`service_name`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `media_metadata` (`mediaId` TEXT NOT NULL, `originalUrl` TEXT NOT NULL, `thumbnailUrl` TEXT NOT NULL, `width` INTEGER NOT NULL, `height` INTEGER NOT NULL, `sizeBytes` INTEGER NOT NULL, `format` TEXT NOT NULL, `duration` INTEGER, `compressionQuality` INTEGER, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`mediaId`), FOREIGN KEY(`mediaId`) REFERENCES `media_items`(`mediaId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_media_metadata_mediaId` ON `media_metadata` (`mediaId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `hub_assignments` (`product_id` TEXT NOT NULL, `hub_id` TEXT NOT NULL, `distance_km` REAL NOT NULL, `assigned_at` INTEGER NOT NULL, `seller_location_lat` REAL NOT NULL, `seller_location_lon` REAL NOT NULL, PRIMARY KEY(`product_id`), FOREIGN KEY(`product_id`) REFERENCES `products`(`productId`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_hub_assignments_product_id` ON `hub_assignments` (`product_id`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_hub_assignments_hub_id` ON `hub_assignments` (`hub_id`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `profitability_metrics` (`id` TEXT NOT NULL, `entity_id` TEXT NOT NULL, `entity_type` TEXT NOT NULL, `period_start` INTEGER NOT NULL, `period_end` INTEGER NOT NULL, `revenue` REAL NOT NULL, `costs` REAL NOT NULL, `profit` REAL NOT NULL, `profit_margin` REAL NOT NULL, `order_count` INTEGER NOT NULL, `calculated_at` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_profitability_metrics_entity_id_entity_type` ON `profitability_metrics` (`entity_id`, `entity_type`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_profitability_metrics_period_start_period_end` ON `profitability_metrics` (`period_start`, `period_end`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `moderation_blocklist` (`term` TEXT NOT NULL, `type` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`term`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '616bc532eac80a4e1adfe8c6a05cc27d')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `products`")
        connection.execSQL("DROP TABLE IF EXISTS `products_fts`")
        connection.execSQL("DROP TABLE IF EXISTS `orders`")
        connection.execSQL("DROP TABLE IF EXISTS `order_items`")
        connection.execSQL("DROP TABLE IF EXISTS `transfers`")
        connection.execSQL("DROP TABLE IF EXISTS `coins`")
        connection.execSQL("DROP TABLE IF EXISTS `notifications`")
        connection.execSQL("DROP TABLE IF EXISTS `alerts`")
        connection.execSQL("DROP TABLE IF EXISTS `product_tracking`")
        connection.execSQL("DROP TABLE IF EXISTS `family_tree`")
        connection.execSQL("DROP TABLE IF EXISTS `chat_messages`")
        connection.execSQL("DROP TABLE IF EXISTS `sync_state`")
        connection.execSQL("DROP TABLE IF EXISTS `auctions`")
        connection.execSQL("DROP TABLE IF EXISTS `bids`")
        connection.execSQL("DROP TABLE IF EXISTS `cart_items`")
        connection.execSQL("DROP TABLE IF EXISTS `wishlist`")
        connection.execSQL("DROP TABLE IF EXISTS `payments`")
        connection.execSQL("DROP TABLE IF EXISTS `coin_ledger`")
        connection.execSQL("DROP TABLE IF EXISTS `delivery_hubs`")
        connection.execSQL("DROP TABLE IF EXISTS `order_tracking_events`")
        connection.execSQL("DROP TABLE IF EXISTS `invoices`")
        connection.execSQL("DROP TABLE IF EXISTS `invoice_lines`")
        connection.execSQL("DROP TABLE IF EXISTS `refunds`")
        connection.execSQL("DROP TABLE IF EXISTS `breeding_records`")
        connection.execSQL("DROP TABLE IF EXISTS `traits`")
        connection.execSQL("DROP TABLE IF EXISTS `product_traits`")
        connection.execSQL("DROP TABLE IF EXISTS `lifecycle_events`")
        connection.execSQL("DROP TABLE IF EXISTS `transfer_verifications`")
        connection.execSQL("DROP TABLE IF EXISTS `disputes`")
        connection.execSQL("DROP TABLE IF EXISTS `audit_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `admin_audit_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `posts`")
        connection.execSQL("DROP TABLE IF EXISTS `comments`")
        connection.execSQL("DROP TABLE IF EXISTS `likes`")
        connection.execSQL("DROP TABLE IF EXISTS `follows`")
        connection.execSQL("DROP TABLE IF EXISTS `groups`")
        connection.execSQL("DROP TABLE IF EXISTS `group_members`")
        connection.execSQL("DROP TABLE IF EXISTS `events`")
        connection.execSQL("DROP TABLE IF EXISTS `expert_bookings`")
        connection.execSQL("DROP TABLE IF EXISTS `moderation_reports`")
        connection.execSQL("DROP TABLE IF EXISTS `badges`")
        connection.execSQL("DROP TABLE IF EXISTS `reputation`")
        connection.execSQL("DROP TABLE IF EXISTS `outgoing_messages`")
        connection.execSQL("DROP TABLE IF EXISTS `rate_limits`")
        connection.execSQL("DROP TABLE IF EXISTS `event_rsvps`")
        connection.execSQL("DROP TABLE IF EXISTS `analytics_daily`")
        connection.execSQL("DROP TABLE IF EXISTS `reports`")
        connection.execSQL("DROP TABLE IF EXISTS `stories`")
        connection.execSQL("DROP TABLE IF EXISTS `growth_records`")
        connection.execSQL("DROP TABLE IF EXISTS `quarantine_records`")
        connection.execSQL("DROP TABLE IF EXISTS `mortality_records`")
        connection.execSQL("DROP TABLE IF EXISTS `vaccination_records`")
        connection.execSQL("DROP TABLE IF EXISTS `hatching_batches`")
        connection.execSQL("DROP TABLE IF EXISTS `hatching_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `achievements_def`")
        connection.execSQL("DROP TABLE IF EXISTS `user_progress`")
        connection.execSQL("DROP TABLE IF EXISTS `badges_def`")
        connection.execSQL("DROP TABLE IF EXISTS `leaderboard`")
        connection.execSQL("DROP TABLE IF EXISTS `rewards_def`")
        connection.execSQL("DROP TABLE IF EXISTS `thread_metadata`")
        connection.execSQL("DROP TABLE IF EXISTS `community_recommendations`")
        connection.execSQL("DROP TABLE IF EXISTS `user_interests`")
        connection.execSQL("DROP TABLE IF EXISTS `expert_profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `outbox`")
        connection.execSQL("DROP TABLE IF EXISTS `breeding_pairs`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_alerts`")
        connection.execSQL("DROP TABLE IF EXISTS `listing_drafts`")
        connection.execSQL("DROP TABLE IF EXISTS `farmer_dashboard_snapshots`")
        connection.execSQL("DROP TABLE IF EXISTS `mating_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `egg_collections`")
        connection.execSQL("DROP TABLE IF EXISTS `enthusiast_dashboard_snapshots`")
        connection.execSQL("DROP TABLE IF EXISTS `upload_tasks`")
        connection.execSQL("DROP TABLE IF EXISTS `daily_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `tasks`")
        connection.execSQL("DROP TABLE IF EXISTS `breeds`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_verifications`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_assets`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_inventory`")
        connection.execSQL("DROP TABLE IF EXISTS `market_listings`")
        connection.execSQL("DROP TABLE IF EXISTS `reviews`")
        connection.execSQL("DROP TABLE IF EXISTS `review_helpful`")
        connection.execSQL("DROP TABLE IF EXISTS `rating_stats`")
        connection.execSQL("DROP TABLE IF EXISTS `order_evidence`")
        connection.execSQL("DROP TABLE IF EXISTS `order_quotes`")
        connection.execSQL("DROP TABLE IF EXISTS `order_payments`")
        connection.execSQL("DROP TABLE IF EXISTS `delivery_confirmations`")
        connection.execSQL("DROP TABLE IF EXISTS `order_disputes`")
        connection.execSQL("DROP TABLE IF EXISTS `order_audit_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `verification_drafts`")
        connection.execSQL("DROP TABLE IF EXISTS `role_migrations`")
        connection.execSQL("DROP TABLE IF EXISTS `storage_quota`")
        connection.execSQL("DROP TABLE IF EXISTS `daily_bird_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `competitions`")
        connection.execSQL("DROP TABLE IF EXISTS `my_votes`")
        connection.execSQL("DROP TABLE IF EXISTS `batch_summaries`")
        connection.execSQL("DROP TABLE IF EXISTS `dashboard_cache`")
        connection.execSQL("DROP TABLE IF EXISTS `show_records`")
        connection.execSQL("DROP TABLE IF EXISTS `verification_requests`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_activity_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_profiles`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_timeline_events`")
        connection.execSQL("DROP TABLE IF EXISTS `enthusiast_verifications`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_events`")
        connection.execSQL("DROP TABLE IF EXISTS `role_upgrade_requests`")
        connection.execSQL("DROP TABLE IF EXISTS `transactions`")
        connection.execSQL("DROP TABLE IF EXISTS `expenses`")
        connection.execSQL("DROP TABLE IF EXISTS `medical_events`")
        connection.execSQL("DROP TABLE IF EXISTS `clutches`")
        connection.execSQL("DROP TABLE IF EXISTS `bird_trait_records`")
        connection.execSQL("DROP TABLE IF EXISTS `breeding_plans`")
        connection.execSQL("DROP TABLE IF EXISTS `arena_participants`")
        connection.execSQL("DROP TABLE IF EXISTS `digital_twins`")
        connection.execSQL("DROP TABLE IF EXISTS `bird_events`")
        connection.execSQL("DROP TABLE IF EXISTS `asset_lifecycle_events`")
        connection.execSQL("DROP TABLE IF EXISTS `asset_health_records`")
        connection.execSQL("DROP TABLE IF EXISTS `task_recurrences`")
        connection.execSQL("DROP TABLE IF EXISTS `asset_batch_operations`")
        connection.execSQL("DROP TABLE IF EXISTS `compliance_rules`")
        connection.execSQL("DROP TABLE IF EXISTS `media_items`")
        connection.execSQL("DROP TABLE IF EXISTS `media_tags`")
        connection.execSQL("DROP TABLE IF EXISTS `media_cache_metadata`")
        connection.execSQL("DROP TABLE IF EXISTS `gallery_filter_state`")
        connection.execSQL("DROP TABLE IF EXISTS `error_logs`")
        connection.execSQL("DROP TABLE IF EXISTS `configuration_cache`")
        connection.execSQL("DROP TABLE IF EXISTS `circuit_breaker_metrics`")
        connection.execSQL("DROP TABLE IF EXISTS `media_metadata`")
        connection.execSQL("DROP TABLE IF EXISTS `hub_assignments`")
        connection.execSQL("DROP TABLE IF EXISTS `profitability_metrics`")
        connection.execSQL("DROP TABLE IF EXISTS `moderation_blocklist`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("userId", TableInfo.Column("userId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("phoneNumber", TableInfo.Column("phoneNumber", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("email", TableInfo.Column("email", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("fullName", TableInfo.Column("fullName", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("address", TableInfo.Column("address", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("bio", TableInfo.Column("bio", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("profilePictureUrl", TableInfo.Column("profilePictureUrl", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("userType", TableInfo.Column("userType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("verificationStatus", TableInfo.Column("verificationStatus", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmAddressLine1", TableInfo.Column("farmAddressLine1", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmAddressLine2", TableInfo.Column("farmAddressLine2", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmCity", TableInfo.Column("farmCity", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmState", TableInfo.Column("farmState", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmPostalCode", TableInfo.Column("farmPostalCode", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmCountry", TableInfo.Column("farmCountry", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmLocationLat", TableInfo.Column("farmLocationLat", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmLocationLng", TableInfo.Column("farmLocationLng", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("locationVerified", TableInfo.Column("locationVerified", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("kycLevel", TableInfo.Column("kycLevel", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("chickenCount", TableInfo.Column("chickenCount", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmerType", TableInfo.Column("farmerType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("raisingSince", TableInfo.Column("raisingSince", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("favoriteBreed", TableInfo.Column("favoriteBreed", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("kycVerifiedAt", TableInfo.Column("kycVerifiedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("kycRejectionReason", TableInfo.Column("kycRejectionReason", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("verificationRejectionReason",
            TableInfo.Column("verificationRejectionReason", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("latestVerificationId", TableInfo.Column("latestVerificationId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("latestVerificationRef", TableInfo.Column("latestVerificationRef", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("verificationSubmittedAt", TableInfo.Column("verificationSubmittedAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("showcaseCount", TableInfo.Column("showcaseCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("maxShowcaseSlots", TableInfo.Column("maxShowcaseSlots", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("createdAt", TableInfo.Column("createdAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("customClaimsUpdatedAt", TableInfo.Column("customClaimsUpdatedAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("isSuspended", TableInfo.Column("isSuspended", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("suspensionReason", TableInfo.Column("suspensionReason", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("suspensionEndsAt", TableInfo.Column("suspensionEndsAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("notificationsEnabled", TableInfo.Column("notificationsEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("farmAlertsEnabled", TableInfo.Column("farmAlertsEnabled", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("transferAlertsEnabled", TableInfo.Column("transferAlertsEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("socialAlertsEnabled", TableInfo.Column("socialAlertsEnabled", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers,
            _indicesUsers)
        val _existingUsers: TableInfo = tableInfoRead(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(com.rio.rostry.data.database.entity.UserEntity).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsProducts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProducts.put("productId", TableInfo.Column("productId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("price", TableInfo.Column("price", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("quantity", TableInfo.Column("quantity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("unit", TableInfo.Column("unit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("location", TableInfo.Column("location", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("latitude", TableInfo.Column("latitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("longitude", TableInfo.Column("longitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("imageUrls", TableInfo.Column("imageUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("condition", TableInfo.Column("condition", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("harvestDate", TableInfo.Column("harvestDate", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("expiryDate", TableInfo.Column("expiryDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("birthDate", TableInfo.Column("birthDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("vaccinationRecordsJson", TableInfo.Column("vaccinationRecordsJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("weightGrams", TableInfo.Column("weightGrams", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("heightCm", TableInfo.Column("heightCm", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("gender", TableInfo.Column("gender", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("color", TableInfo.Column("color", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("breed", TableInfo.Column("breed", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("raisingPurpose", TableInfo.Column("raisingPurpose", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("healthStatus", TableInfo.Column("healthStatus", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("birdCode", TableInfo.Column("birdCode", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("colorTag", TableInfo.Column("colorTag", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("familyTreeId", TableInfo.Column("familyTreeId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("sourceAssetId", TableInfo.Column("sourceAssetId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("parentIdsJson", TableInfo.Column("parentIdsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("breedingStatus", TableInfo.Column("breedingStatus", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("transferHistoryJson", TableInfo.Column("transferHistoryJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("lastModifiedAt", TableInfo.Column("lastModifiedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("stage", TableInfo.Column("stage", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("lifecycleStatus", TableInfo.Column("lifecycleStatus", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("parentMaleId", TableInfo.Column("parentMaleId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("parentFemaleId", TableInfo.Column("parentFemaleId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("ageWeeks", TableInfo.Column("ageWeeks", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("lastStageTransitionAt", TableInfo.Column("lastStageTransitionAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("breederEligibleAt", TableInfo.Column("breederEligibleAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("isBatch", TableInfo.Column("isBatch", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("batchId", TableInfo.Column("batchId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("splitAt", TableInfo.Column("splitAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("splitIntoIds", TableInfo.Column("splitIntoIds", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("documentUrls", TableInfo.Column("documentUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("qrCodeUrl", TableInfo.Column("qrCodeUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("customStatus", TableInfo.Column("customStatus", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("debug", TableInfo.Column("debug", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("deliveryOptions", TableInfo.Column("deliveryOptions", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("deliveryCost", TableInfo.Column("deliveryCost", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("leadTimeDays", TableInfo.Column("leadTimeDays", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("motherId", TableInfo.Column("motherId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("isBreedingUnit", TableInfo.Column("isBreedingUnit", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("eggsCollectedToday", TableInfo.Column("eggsCollectedToday", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("lastEggLogDate", TableInfo.Column("lastEggLogDate", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("readyForSale", TableInfo.Column("readyForSale", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("targetWeight", TableInfo.Column("targetWeight", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("isShowcased", TableInfo.Column("isShowcased", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("externalVideoUrl", TableInfo.Column("externalVideoUrl", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("recordsLockedAt", TableInfo.Column("recordsLockedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("autoLockAfterDays", TableInfo.Column("autoLockAfterDays", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("lineageHistoryJson", TableInfo.Column("lineageHistoryJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("editCount", TableInfo.Column("editCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("lastEditedBy", TableInfo.Column("lastEditedBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("adminFlagged", TableInfo.Column("adminFlagged", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("moderationNote", TableInfo.Column("moderationNote", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("metadataJson", TableInfo.Column("metadataJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProducts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysProducts.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("sellerId"), listOf("userId")))
        val _indicesProducts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProducts.add(TableInfo.Index("index_products_sellerId", false, listOf("sellerId"),
            listOf("ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_category", false, listOf("category"),
            listOf("ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_status", false, listOf("status"),
            listOf("ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_sourceAssetId", false,
            listOf("sourceAssetId"), listOf("ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_sellerId_lifecycleStatus", false,
            listOf("sellerId", "lifecycleStatus"), listOf("ASC", "ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_sellerId_isBatch", false,
            listOf("sellerId", "isBatch"), listOf("ASC", "ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_birthDate", false, listOf("birthDate"),
            listOf("ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_updatedAt", false, listOf("updatedAt"),
            listOf("ASC")))
        _indicesProducts.add(TableInfo.Index("index_products_createdAt", false, listOf("createdAt"),
            listOf("ASC")))
        val _infoProducts: TableInfo = TableInfo("products", _columnsProducts, _foreignKeysProducts,
            _indicesProducts)
        val _existingProducts: TableInfo = tableInfoRead(connection, "products")
        if (!_infoProducts.equals(_existingProducts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |products(com.rio.rostry.data.database.entity.ProductEntity).
              | Expected:
              |""".trimMargin() + _infoProducts + """
              |
              | Found:
              |""".trimMargin() + _existingProducts)
        }
        val _columnsProductsFts: MutableSet<String> = mutableSetOf()
        _columnsProductsFts.add("productId")
        _columnsProductsFts.add("name")
        _columnsProductsFts.add("description")
        _columnsProductsFts.add("category")
        _columnsProductsFts.add("breed")
        _columnsProductsFts.add("location")
        _columnsProductsFts.add("condition")
        val _infoProductsFts: FtsTableInfo = FtsTableInfo("products_fts", _columnsProductsFts,
            "CREATE VIRTUAL TABLE IF NOT EXISTS `products_fts` USING FTS4(`productId` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `category` TEXT NOT NULL, `breed` TEXT, `location` TEXT NOT NULL, `condition` TEXT)")
        val _existingProductsFts: FtsTableInfo = ftsTableInfoRead(connection, "products_fts")
        if (!_infoProductsFts.equals(_existingProductsFts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |products_fts(com.rio.rostry.data.database.entity.ProductFtsEntity).
              | Expected:
              |""".trimMargin() + _infoProductsFts + """
              |
              | Found:
              |""".trimMargin() + _existingProductsFts)
        }
        val _columnsOrders: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrders.put("orderId", TableInfo.Column("orderId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("buyerId", TableInfo.Column("buyerId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("quantity", TableInfo.Column("quantity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("unit", TableInfo.Column("unit", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("totalAmount", TableInfo.Column("totalAmount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("shippingAddress", TableInfo.Column("shippingAddress", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("paymentMethod", TableInfo.Column("paymentMethod", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("paymentStatus", TableInfo.Column("paymentStatus", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("orderDate", TableInfo.Column("orderDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("expectedDeliveryDate", TableInfo.Column("expectedDeliveryDate",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("actualDeliveryDate", TableInfo.Column("actualDeliveryDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("lastModifiedAt", TableInfo.Column("lastModifiedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("deliveryMethod", TableInfo.Column("deliveryMethod", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("deliveryType", TableInfo.Column("deliveryType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("deliveryAddressJson", TableInfo.Column("deliveryAddressJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("negotiationStatus", TableInfo.Column("negotiationStatus", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("negotiatedPrice", TableInfo.Column("negotiatedPrice", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("originalPrice", TableInfo.Column("originalPrice", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("cancellationReason", TableInfo.Column("cancellationReason", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("cancellationTime", TableInfo.Column("cancellationTime", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("billImageUri", TableInfo.Column("billImageUri", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("paymentSlipUri", TableInfo.Column("paymentSlipUri", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("otp", TableInfo.Column("otp", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("otpEntered", TableInfo.Column("otpEntered", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrders.put("isVerified", TableInfo.Column("isVerified", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrders: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysOrders.add(TableInfo.ForeignKey("users", "SET NULL", "NO ACTION",
            listOf("buyerId"), listOf("userId")))
        val _indicesOrders: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrders.add(TableInfo.Index("index_orders_buyerId", false, listOf("buyerId"),
            listOf("ASC")))
        _indicesOrders.add(TableInfo.Index("index_orders_sellerId", false, listOf("sellerId"),
            listOf("ASC")))
        val _infoOrders: TableInfo = TableInfo("orders", _columnsOrders, _foreignKeysOrders,
            _indicesOrders)
        val _existingOrders: TableInfo = tableInfoRead(connection, "orders")
        if (!_infoOrders.equals(_existingOrders)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |orders(com.rio.rostry.data.database.entity.OrderEntity).
              | Expected:
              |""".trimMargin() + _infoOrders + """
              |
              | Found:
              |""".trimMargin() + _existingOrders)
        }
        val _columnsOrderItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderItems.put("orderId", TableInfo.Column("orderId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderItems.put("productId", TableInfo.Column("productId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderItems.put("quantity", TableInfo.Column("quantity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderItems.put("priceAtPurchase", TableInfo.Column("priceAtPurchase", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderItems.put("unitAtPurchase", TableInfo.Column("unitAtPurchase", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysOrderItems.add(TableInfo.ForeignKey("orders", "CASCADE", "NO ACTION",
            listOf("orderId"), listOf("orderId")))
        _foreignKeysOrderItems.add(TableInfo.ForeignKey("products", "RESTRICT", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesOrderItems: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderItems.add(TableInfo.Index("index_order_items_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesOrderItems.add(TableInfo.Index("index_order_items_productId", false,
            listOf("productId"), listOf("ASC")))
        val _infoOrderItems: TableInfo = TableInfo("order_items", _columnsOrderItems,
            _foreignKeysOrderItems, _indicesOrderItems)
        val _existingOrderItems: TableInfo = tableInfoRead(connection, "order_items")
        if (!_infoOrderItems.equals(_existingOrderItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_items(com.rio.rostry.data.database.entity.OrderItemEntity).
              | Expected:
              |""".trimMargin() + _infoOrderItems + """
              |
              | Found:
              |""".trimMargin() + _existingOrderItems)
        }
        val _columnsTransfers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransfers.put("transferId", TableInfo.Column("transferId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("fromUserId", TableInfo.Column("fromUserId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("toUserId", TableInfo.Column("toUserId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("orderId", TableInfo.Column("orderId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("transactionReference", TableInfo.Column("transactionReference",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("gpsLat", TableInfo.Column("gpsLat", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("gpsLng", TableInfo.Column("gpsLng", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("sellerPhotoUrl", TableInfo.Column("sellerPhotoUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("buyerPhotoUrl", TableInfo.Column("buyerPhotoUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("timeoutAt", TableInfo.Column("timeoutAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("conditionsJson", TableInfo.Column("conditionsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("transferCode", TableInfo.Column("transferCode", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("lineageSnapshotJson", TableInfo.Column("lineageSnapshotJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("claimedAt", TableInfo.Column("claimedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("transferType", TableInfo.Column("transferType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("growthSnapshotJson", TableInfo.Column("growthSnapshotJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("healthSnapshotJson", TableInfo.Column("healthSnapshotJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("transferCodeExpiresAt", TableInfo.Column("transferCodeExpiresAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("initiatedAt", TableInfo.Column("initiatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("lastModifiedAt", TableInfo.Column("lastModifiedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("mergedAt", TableInfo.Column("mergedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransfers.put("mergeCount", TableInfo.Column("mergeCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransfers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysTransfers.add(TableInfo.ForeignKey("users", "SET NULL", "NO ACTION",
            listOf("fromUserId"), listOf("userId")))
        _foreignKeysTransfers.add(TableInfo.ForeignKey("users", "SET NULL", "NO ACTION",
            listOf("toUserId"), listOf("userId")))
        _foreignKeysTransfers.add(TableInfo.ForeignKey("orders", "SET NULL", "NO ACTION",
            listOf("orderId"), listOf("orderId")))
        val _indicesTransfers: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTransfers.add(TableInfo.Index("index_transfers_fromUserId", false,
            listOf("fromUserId"), listOf("ASC")))
        _indicesTransfers.add(TableInfo.Index("index_transfers_toUserId", false, listOf("toUserId"),
            listOf("ASC")))
        _indicesTransfers.add(TableInfo.Index("index_transfers_orderId", false, listOf("orderId"),
            listOf("ASC")))
        _indicesTransfers.add(TableInfo.Index("index_transfers_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesTransfers.add(TableInfo.Index("index_transfers_syncedAt", false, listOf("syncedAt"),
            listOf("ASC")))
        val _infoTransfers: TableInfo = TableInfo("transfers", _columnsTransfers,
            _foreignKeysTransfers, _indicesTransfers)
        val _existingTransfers: TableInfo = tableInfoRead(connection, "transfers")
        if (!_infoTransfers.equals(_existingTransfers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transfers(com.rio.rostry.data.database.entity.TransferEntity).
              | Expected:
              |""".trimMargin() + _infoTransfers + """
              |
              | Found:
              |""".trimMargin() + _existingTransfers)
        }
        val _columnsCoins: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCoins.put("coinTransactionId", TableInfo.Column("coinTransactionId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("relatedTransferId", TableInfo.Column("relatedTransferId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("relatedOrderId", TableInfo.Column("relatedOrderId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCoins.put("transactionDate", TableInfo.Column("transactionDate", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCoins: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCoins.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("userId")))
        _foreignKeysCoins.add(TableInfo.ForeignKey("transfers", "SET NULL", "NO ACTION",
            listOf("relatedTransferId"), listOf("transferId")))
        val _indicesCoins: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCoins.add(TableInfo.Index("index_coins_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesCoins.add(TableInfo.Index("index_coins_relatedTransferId", false,
            listOf("relatedTransferId"), listOf("ASC")))
        val _infoCoins: TableInfo = TableInfo("coins", _columnsCoins, _foreignKeysCoins,
            _indicesCoins)
        val _existingCoins: TableInfo = tableInfoRead(connection, "coins")
        if (!_infoCoins.equals(_existingCoins)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |coins(com.rio.rostry.data.database.entity.CoinEntity).
              | Expected:
              |""".trimMargin() + _infoCoins + """
              |
              | Found:
              |""".trimMargin() + _existingCoins)
        }
        val _columnsNotifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotifications.put("notificationId", TableInfo.Column("notificationId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("deepLinkUrl", TableInfo.Column("deepLinkUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("isBatched", TableInfo.Column("isBatched", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("batchedAt", TableInfo.Column("batchedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("displayedAt", TableInfo.Column("displayedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("domain", TableInfo.Column("domain", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNotifications.put("userPreferenceEnabled", TableInfo.Column("userPreferenceEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysNotifications.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("userId")))
        val _indicesNotifications: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesNotifications.add(TableInfo.Index("index_notifications_userId", false,
            listOf("userId"), listOf("ASC")))
        _indicesNotifications.add(TableInfo.Index("index_notifications_isBatched", false,
            listOf("isBatched"), listOf("ASC")))
        _indicesNotifications.add(TableInfo.Index("index_notifications_domain", false,
            listOf("domain"), listOf("ASC")))
        val _infoNotifications: TableInfo = TableInfo("notifications", _columnsNotifications,
            _foreignKeysNotifications, _indicesNotifications)
        val _existingNotifications: TableInfo = tableInfoRead(connection, "notifications")
        if (!_infoNotifications.equals(_existingNotifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notifications(com.rio.rostry.data.database.entity.NotificationEntity).
              | Expected:
              |""".trimMargin() + _infoNotifications + """
              |
              | Found:
              |""".trimMargin() + _existingNotifications)
        }
        val _columnsAlerts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAlerts.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("severity", TableInfo.Column("severity", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("relatedId", TableInfo.Column("relatedId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAlerts.put("isDismissed", TableInfo.Column("isDismissed", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAlerts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAlerts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAlerts: TableInfo = TableInfo("alerts", _columnsAlerts, _foreignKeysAlerts,
            _indicesAlerts)
        val _existingAlerts: TableInfo = tableInfoRead(connection, "alerts")
        if (!_infoAlerts.equals(_existingAlerts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |alerts(com.rio.rostry.data.database.entity.AlertEntity).
              | Expected:
              |""".trimMargin() + _infoAlerts + """
              |
              | Found:
              |""".trimMargin() + _existingAlerts)
        }
        val _columnsProductTracking: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProductTracking.put("trackingId", TableInfo.Column("trackingId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("productId", TableInfo.Column("productId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("metadataJson", TableInfo.Column("metadataJson", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTracking.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProductTracking: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysProductTracking.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        _foreignKeysProductTracking.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("ownerId"), listOf("userId")))
        val _indicesProductTracking: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProductTracking.add(TableInfo.Index("index_product_tracking_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesProductTracking.add(TableInfo.Index("index_product_tracking_ownerId", false,
            listOf("ownerId"), listOf("ASC")))
        _indicesProductTracking.add(TableInfo.Index("index_product_tracking_productId_timestamp",
            false, listOf("productId", "timestamp"), listOf("ASC", "ASC")))
        val _infoProductTracking: TableInfo = TableInfo("product_tracking", _columnsProductTracking,
            _foreignKeysProductTracking, _indicesProductTracking)
        val _existingProductTracking: TableInfo = tableInfoRead(connection, "product_tracking")
        if (!_infoProductTracking.equals(_existingProductTracking)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |product_tracking(com.rio.rostry.data.database.entity.ProductTrackingEntity).
              | Expected:
              |""".trimMargin() + _infoProductTracking + """
              |
              | Found:
              |""".trimMargin() + _existingProductTracking)
        }
        val _columnsFamilyTree: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFamilyTree.put("nodeId", TableInfo.Column("nodeId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("parentProductId", TableInfo.Column("parentProductId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("childProductId", TableInfo.Column("childProductId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("relationType", TableInfo.Column("relationType", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFamilyTree.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFamilyTree: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysFamilyTree.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        _foreignKeysFamilyTree.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("parentProductId"), listOf("productId")))
        _foreignKeysFamilyTree.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("childProductId"), listOf("productId")))
        val _indicesFamilyTree: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFamilyTree.add(TableInfo.Index("index_family_tree_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesFamilyTree.add(TableInfo.Index("index_family_tree_parentProductId", false,
            listOf("parentProductId"), listOf("ASC")))
        _indicesFamilyTree.add(TableInfo.Index("index_family_tree_childProductId", false,
            listOf("childProductId"), listOf("ASC")))
        _indicesFamilyTree.add(TableInfo.Index("index_family_tree_productId_parentProductId_childProductId",
            true, listOf("productId", "parentProductId", "childProductId"), listOf("ASC", "ASC",
            "ASC")))
        val _infoFamilyTree: TableInfo = TableInfo("family_tree", _columnsFamilyTree,
            _foreignKeysFamilyTree, _indicesFamilyTree)
        val _existingFamilyTree: TableInfo = tableInfoRead(connection, "family_tree")
        if (!_infoFamilyTree.equals(_existingFamilyTree)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |family_tree(com.rio.rostry.data.database.entity.FamilyTreeEntity).
              | Expected:
              |""".trimMargin() + _infoFamilyTree + """
              |
              | Found:
              |""".trimMargin() + _existingFamilyTree)
        }
        val _columnsChatMessages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChatMessages.put("messageId", TableInfo.Column("messageId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("senderId", TableInfo.Column("senderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("receiverId", TableInfo.Column("receiverId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("body", TableInfo.Column("body", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("mediaUrl", TableInfo.Column("mediaUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("sentAt", TableInfo.Column("sentAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("deliveredAt", TableInfo.Column("deliveredAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("readAt", TableInfo.Column("readAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("lastModifiedAt", TableInfo.Column("lastModifiedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("deviceTimestamp", TableInfo.Column("deviceTimestamp", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("metadata", TableInfo.Column("metadata", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChatMessages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysChatMessages.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("senderId"), listOf("userId")))
        _foreignKeysChatMessages.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("receiverId"), listOf("userId")))
        val _indicesChatMessages: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesChatMessages.add(TableInfo.Index("index_chat_messages_senderId", false,
            listOf("senderId"), listOf("ASC")))
        _indicesChatMessages.add(TableInfo.Index("index_chat_messages_receiverId", false,
            listOf("receiverId"), listOf("ASC")))
        _indicesChatMessages.add(TableInfo.Index("index_chat_messages_senderId_receiverId", false,
            listOf("senderId", "receiverId"), listOf("ASC", "ASC")))
        _indicesChatMessages.add(TableInfo.Index("index_chat_messages_syncedAt", false,
            listOf("syncedAt"), listOf("ASC")))
        val _infoChatMessages: TableInfo = TableInfo("chat_messages", _columnsChatMessages,
            _foreignKeysChatMessages, _indicesChatMessages)
        val _existingChatMessages: TableInfo = tableInfoRead(connection, "chat_messages")
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |chat_messages(com.rio.rostry.data.database.entity.ChatMessageEntity).
              | Expected:
              |""".trimMargin() + _infoChatMessages + """
              |
              | Found:
              |""".trimMargin() + _existingChatMessages)
        }
        val _columnsSyncState: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSyncState.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastSyncAt", TableInfo.Column("lastSyncAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastUserSyncAt", TableInfo.Column("lastUserSyncAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastProductSyncAt", TableInfo.Column("lastProductSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastOrderSyncAt", TableInfo.Column("lastOrderSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastTrackingSyncAt", TableInfo.Column("lastTrackingSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastTransferSyncAt", TableInfo.Column("lastTransferSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastChatSyncAt", TableInfo.Column("lastChatSyncAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastBreedingSyncAt", TableInfo.Column("lastBreedingSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastAlertSyncAt", TableInfo.Column("lastAlertSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastDashboardSyncAt", TableInfo.Column("lastDashboardSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastVaccinationSyncAt", TableInfo.Column("lastVaccinationSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastGrowthSyncAt", TableInfo.Column("lastGrowthSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastQuarantineSyncAt", TableInfo.Column("lastQuarantineSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastMortalitySyncAt", TableInfo.Column("lastMortalitySyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastHatchingSyncAt", TableInfo.Column("lastHatchingSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastHatchingLogSyncAt", TableInfo.Column("lastHatchingLogSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastEnthusiastBreedingSyncAt",
            TableInfo.Column("lastEnthusiastBreedingSyncAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastEnthusiastDashboardSyncAt",
            TableInfo.Column("lastEnthusiastDashboardSyncAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastDailyLogSyncAt", TableInfo.Column("lastDailyLogSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastBatchSummarySyncAt", TableInfo.Column("lastBatchSummarySyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastTaskSyncAt", TableInfo.Column("lastTaskSyncAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastExpenseSyncAt", TableInfo.Column("lastExpenseSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastProofSyncAt", TableInfo.Column("lastProofSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastGeneticAnalysisSyncAt",
            TableInfo.Column("lastGeneticAnalysisSyncAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastIoTDeviceSyncAt", TableInfo.Column("lastIoTDeviceSyncAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSyncState.put("lastIoTDataSyncAt", TableInfo.Column("lastIoTDataSyncAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSyncState: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSyncState: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSyncState: TableInfo = TableInfo("sync_state", _columnsSyncState,
            _foreignKeysSyncState, _indicesSyncState)
        val _existingSyncState: TableInfo = tableInfoRead(connection, "sync_state")
        if (!_infoSyncState.equals(_existingSyncState)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |sync_state(com.rio.rostry.data.database.entity.SyncStateEntity).
              | Expected:
              |""".trimMargin() + _infoSyncState + """
              |
              | Found:
              |""".trimMargin() + _existingSyncState)
        }
        val _columnsAuctions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAuctions.put("auctionId", TableInfo.Column("auctionId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("startsAt", TableInfo.Column("startsAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("endsAt", TableInfo.Column("endsAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("closedAt", TableInfo.Column("closedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("closedBy", TableInfo.Column("closedBy", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("minPrice", TableInfo.Column("minPrice", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("currentPrice", TableInfo.Column("currentPrice", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("reservePrice", TableInfo.Column("reservePrice", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("buyNowPrice", TableInfo.Column("buyNowPrice", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("bidIncrement", TableInfo.Column("bidIncrement", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("bidCount", TableInfo.Column("bidCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("winnerId", TableInfo.Column("winnerId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("isReserveMet", TableInfo.Column("isReserveMet", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("extensionCount", TableInfo.Column("extensionCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("maxExtensions", TableInfo.Column("maxExtensions", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("extensionMinutes", TableInfo.Column("extensionMinutes", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("isActive", TableInfo.Column("isActive", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("viewCount", TableInfo.Column("viewCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuctions.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAuctions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysAuctions.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesAuctions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAuctions.add(TableInfo.Index("index_auctions_productId", false, listOf("productId"),
            listOf("ASC")))
        _indicesAuctions.add(TableInfo.Index("index_auctions_sellerId", false, listOf("sellerId"),
            listOf("ASC")))
        _indicesAuctions.add(TableInfo.Index("index_auctions_status", false, listOf("status"),
            listOf("ASC")))
        _indicesAuctions.add(TableInfo.Index("index_auctions_status_endsAt", false, listOf("status",
            "endsAt"), listOf("ASC", "ASC")))
        val _infoAuctions: TableInfo = TableInfo("auctions", _columnsAuctions, _foreignKeysAuctions,
            _indicesAuctions)
        val _existingAuctions: TableInfo = tableInfoRead(connection, "auctions")
        if (!_infoAuctions.equals(_existingAuctions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |auctions(com.rio.rostry.data.database.entity.AuctionEntity).
              | Expected:
              |""".trimMargin() + _infoAuctions + """
              |
              | Found:
              |""".trimMargin() + _existingAuctions)
        }
        val _columnsBids: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBids.put("bidId", TableInfo.Column("bidId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("auctionId", TableInfo.Column("auctionId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("placedAt", TableInfo.Column("placedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("isAutoBid", TableInfo.Column("isAutoBid", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("maxAmount", TableInfo.Column("maxAmount", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("isWinning", TableInfo.Column("isWinning", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("wasOutbid", TableInfo.Column("wasOutbid", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("outbidAt", TableInfo.Column("outbidAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("outbidNotified", TableInfo.Column("outbidNotified", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("isRetracted", TableInfo.Column("isRetracted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBids.put("retractedReason", TableInfo.Column("retractedReason", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBids: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBids.add(TableInfo.ForeignKey("auctions", "CASCADE", "NO ACTION",
            listOf("auctionId"), listOf("auctionId")))
        _foreignKeysBids.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", listOf("userId"),
            listOf("userId")))
        val _indicesBids: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBids.add(TableInfo.Index("index_bids_auctionId", false, listOf("auctionId"),
            listOf("ASC")))
        _indicesBids.add(TableInfo.Index("index_bids_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesBids.add(TableInfo.Index("index_bids_auctionId_amount", false, listOf("auctionId",
            "amount"), listOf("ASC", "ASC")))
        val _infoBids: TableInfo = TableInfo("bids", _columnsBids, _foreignKeysBids, _indicesBids)
        val _existingBids: TableInfo = tableInfoRead(connection, "bids")
        if (!_infoBids.equals(_existingBids)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bids(com.rio.rostry.data.database.entity.BidEntity).
              | Expected:
              |""".trimMargin() + _infoBids + """
              |
              | Found:
              |""".trimMargin() + _existingBids)
        }
        val _columnsCartItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCartItems.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCartItems.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCartItems.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCartItems.put("quantity", TableInfo.Column("quantity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCartItems.put("addedAt", TableInfo.Column("addedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCartItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCartItems.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("userId")))
        _foreignKeysCartItems.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesCartItems: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCartItems.add(TableInfo.Index("index_cart_items_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesCartItems.add(TableInfo.Index("index_cart_items_productId", false,
            listOf("productId"), listOf("ASC")))
        val _infoCartItems: TableInfo = TableInfo("cart_items", _columnsCartItems,
            _foreignKeysCartItems, _indicesCartItems)
        val _existingCartItems: TableInfo = tableInfoRead(connection, "cart_items")
        if (!_infoCartItems.equals(_existingCartItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cart_items(com.rio.rostry.data.database.entity.CartItemEntity).
              | Expected:
              |""".trimMargin() + _infoCartItems + """
              |
              | Found:
              |""".trimMargin() + _existingCartItems)
        }
        val _columnsWishlist: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWishlist.put("userId", TableInfo.Column("userId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWishlist.put("productId", TableInfo.Column("productId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWishlist.put("addedAt", TableInfo.Column("addedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWishlist: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysWishlist.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("userId")))
        _foreignKeysWishlist.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesWishlist: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesWishlist.add(TableInfo.Index("index_wishlist_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesWishlist.add(TableInfo.Index("index_wishlist_productId", false, listOf("productId"),
            listOf("ASC")))
        val _infoWishlist: TableInfo = TableInfo("wishlist", _columnsWishlist, _foreignKeysWishlist,
            _indicesWishlist)
        val _existingWishlist: TableInfo = tableInfoRead(connection, "wishlist")
        if (!_infoWishlist.equals(_existingWishlist)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |wishlist(com.rio.rostry.data.database.entity.WishlistEntity).
              | Expected:
              |""".trimMargin() + _infoWishlist + """
              |
              | Found:
              |""".trimMargin() + _existingWishlist)
        }
        val _columnsPayments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPayments.put("paymentId", TableInfo.Column("paymentId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("method", TableInfo.Column("method", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("providerRef", TableInfo.Column("providerRef", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("upiUri", TableInfo.Column("upiUri", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("idempotencyKey", TableInfo.Column("idempotencyKey", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPayments.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPayments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysPayments.add(TableInfo.ForeignKey("orders", "CASCADE", "NO ACTION",
            listOf("orderId"), listOf("orderId")))
        _foreignKeysPayments.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("userId")))
        val _indicesPayments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPayments.add(TableInfo.Index("index_payments_orderId", false, listOf("orderId"),
            listOf("ASC")))
        _indicesPayments.add(TableInfo.Index("index_payments_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesPayments.add(TableInfo.Index("index_payments_idempotencyKey", true,
            listOf("idempotencyKey"), listOf("ASC")))
        val _infoPayments: TableInfo = TableInfo("payments", _columnsPayments, _foreignKeysPayments,
            _indicesPayments)
        val _existingPayments: TableInfo = tableInfoRead(connection, "payments")
        if (!_infoPayments.equals(_existingPayments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |payments(com.rio.rostry.data.database.entity.PaymentEntity).
              | Expected:
              |""".trimMargin() + _infoPayments + """
              |
              | Found:
              |""".trimMargin() + _existingPayments)
        }
        val _columnsCoinLedger: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCoinLedger.put("entryId", TableInfo.Column("entryId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("coins", TableInfo.Column("coins", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("amountInInr", TableInfo.Column("amountInInr", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("refId", TableInfo.Column("refId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCoinLedger.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCoinLedger: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCoinLedger.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("userId"), listOf("userId")))
        val _indicesCoinLedger: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCoinLedger.add(TableInfo.Index("index_coin_ledger_userId", false, listOf("userId"),
            listOf("ASC")))
        val _infoCoinLedger: TableInfo = TableInfo("coin_ledger", _columnsCoinLedger,
            _foreignKeysCoinLedger, _indicesCoinLedger)
        val _existingCoinLedger: TableInfo = tableInfoRead(connection, "coin_ledger")
        if (!_infoCoinLedger.equals(_existingCoinLedger)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |coin_ledger(com.rio.rostry.data.database.entity.CoinLedgerEntity).
              | Expected:
              |""".trimMargin() + _infoCoinLedger + """
              |
              | Found:
              |""".trimMargin() + _existingCoinLedger)
        }
        val _columnsDeliveryHubs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDeliveryHubs.put("hubId", TableInfo.Column("hubId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryHubs.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryHubs.put("latitude", TableInfo.Column("latitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryHubs.put("longitude", TableInfo.Column("longitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryHubs.put("address", TableInfo.Column("address", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryHubs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryHubs.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDeliveryHubs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDeliveryHubs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDeliveryHubs: TableInfo = TableInfo("delivery_hubs", _columnsDeliveryHubs,
            _foreignKeysDeliveryHubs, _indicesDeliveryHubs)
        val _existingDeliveryHubs: TableInfo = tableInfoRead(connection, "delivery_hubs")
        if (!_infoDeliveryHubs.equals(_existingDeliveryHubs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |delivery_hubs(com.rio.rostry.data.database.entity.DeliveryHubEntity).
              | Expected:
              |""".trimMargin() + _infoDeliveryHubs + """
              |
              | Found:
              |""".trimMargin() + _existingDeliveryHubs)
        }
        val _columnsOrderTrackingEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderTrackingEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderTrackingEvents.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderTrackingEvents.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderTrackingEvents.put("hubId", TableInfo.Column("hubId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderTrackingEvents.put("note", TableInfo.Column("note", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderTrackingEvents.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderTrackingEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysOrderTrackingEvents.add(TableInfo.ForeignKey("orders", "CASCADE", "NO ACTION",
            listOf("orderId"), listOf("orderId")))
        _foreignKeysOrderTrackingEvents.add(TableInfo.ForeignKey("delivery_hubs", "SET NULL",
            "NO ACTION", listOf("hubId"), listOf("hubId")))
        val _indicesOrderTrackingEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderTrackingEvents.add(TableInfo.Index("index_order_tracking_events_orderId",
            false, listOf("orderId"), listOf("ASC")))
        _indicesOrderTrackingEvents.add(TableInfo.Index("index_order_tracking_events_hubId", false,
            listOf("hubId"), listOf("ASC")))
        val _infoOrderTrackingEvents: TableInfo = TableInfo("order_tracking_events",
            _columnsOrderTrackingEvents, _foreignKeysOrderTrackingEvents,
            _indicesOrderTrackingEvents)
        val _existingOrderTrackingEvents: TableInfo = tableInfoRead(connection,
            "order_tracking_events")
        if (!_infoOrderTrackingEvents.equals(_existingOrderTrackingEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_tracking_events(com.rio.rostry.data.database.entity.OrderTrackingEventEntity).
              | Expected:
              |""".trimMargin() + _infoOrderTrackingEvents + """
              |
              | Found:
              |""".trimMargin() + _existingOrderTrackingEvents)
        }
        val _columnsInvoices: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInvoices.put("invoiceId", TableInfo.Column("invoiceId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("subtotal", TableInfo.Column("subtotal", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("gstPercent", TableInfo.Column("gstPercent", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("gstAmount", TableInfo.Column("gstAmount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("total", TableInfo.Column("total", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoices.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInvoices: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysInvoices.add(TableInfo.ForeignKey("orders", "CASCADE", "NO ACTION",
            listOf("orderId"), listOf("orderId")))
        val _indicesInvoices: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInvoices.add(TableInfo.Index("index_invoices_orderId", false, listOf("orderId"),
            listOf("ASC")))
        val _infoInvoices: TableInfo = TableInfo("invoices", _columnsInvoices, _foreignKeysInvoices,
            _indicesInvoices)
        val _existingInvoices: TableInfo = tableInfoRead(connection, "invoices")
        if (!_infoInvoices.equals(_existingInvoices)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |invoices(com.rio.rostry.data.database.entity.InvoiceEntity).
              | Expected:
              |""".trimMargin() + _infoInvoices + """
              |
              | Found:
              |""".trimMargin() + _existingInvoices)
        }
        val _columnsInvoiceLines: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInvoiceLines.put("lineId", TableInfo.Column("lineId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("invoiceId", TableInfo.Column("invoiceId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("qty", TableInfo.Column("qty", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("unitPrice", TableInfo.Column("unitPrice", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInvoiceLines.put("lineTotal", TableInfo.Column("lineTotal", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInvoiceLines: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysInvoiceLines.add(TableInfo.ForeignKey("invoices", "CASCADE", "NO ACTION",
            listOf("invoiceId"), listOf("invoiceId")))
        val _indicesInvoiceLines: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesInvoiceLines.add(TableInfo.Index("index_invoice_lines_invoiceId", false,
            listOf("invoiceId"), listOf("ASC")))
        val _infoInvoiceLines: TableInfo = TableInfo("invoice_lines", _columnsInvoiceLines,
            _foreignKeysInvoiceLines, _indicesInvoiceLines)
        val _existingInvoiceLines: TableInfo = tableInfoRead(connection, "invoice_lines")
        if (!_infoInvoiceLines.equals(_existingInvoiceLines)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |invoice_lines(com.rio.rostry.data.database.entity.InvoiceLineEntity).
              | Expected:
              |""".trimMargin() + _infoInvoiceLines + """
              |
              | Found:
              |""".trimMargin() + _existingInvoiceLines)
        }
        val _columnsRefunds: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRefunds.put("refundId", TableInfo.Column("refundId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRefunds.put("paymentId", TableInfo.Column("paymentId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRefunds.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRefunds.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRefunds.put("reason", TableInfo.Column("reason", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRefunds.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRefunds: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysRefunds.add(TableInfo.ForeignKey("payments", "CASCADE", "NO ACTION",
            listOf("paymentId"), listOf("paymentId")))
        _foreignKeysRefunds.add(TableInfo.ForeignKey("orders", "CASCADE", "NO ACTION",
            listOf("orderId"), listOf("orderId")))
        val _indicesRefunds: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesRefunds.add(TableInfo.Index("index_refunds_paymentId", false, listOf("paymentId"),
            listOf("ASC")))
        _indicesRefunds.add(TableInfo.Index("index_refunds_orderId", false, listOf("orderId"),
            listOf("ASC")))
        val _infoRefunds: TableInfo = TableInfo("refunds", _columnsRefunds, _foreignKeysRefunds,
            _indicesRefunds)
        val _existingRefunds: TableInfo = tableInfoRead(connection, "refunds")
        if (!_infoRefunds.equals(_existingRefunds)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |refunds(com.rio.rostry.data.database.entity.RefundEntity).
              | Expected:
              |""".trimMargin() + _infoRefunds + """
              |
              | Found:
              |""".trimMargin() + _existingRefunds)
        }
        val _columnsBreedingRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBreedingRecords.put("recordId", TableInfo.Column("recordId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingRecords.put("parentId", TableInfo.Column("parentId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingRecords.put("partnerId", TableInfo.Column("partnerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingRecords.put("childId", TableInfo.Column("childId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingRecords.put("success", TableInfo.Column("success", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingRecords.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingRecords.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBreedingRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBreedingRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("parentId"), listOf("productId")))
        _foreignKeysBreedingRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("partnerId"), listOf("productId")))
        _foreignKeysBreedingRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("childId"), listOf("productId")))
        val _indicesBreedingRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBreedingRecords.add(TableInfo.Index("index_breeding_records_parentId", false,
            listOf("parentId"), listOf("ASC")))
        _indicesBreedingRecords.add(TableInfo.Index("index_breeding_records_partnerId", false,
            listOf("partnerId"), listOf("ASC")))
        _indicesBreedingRecords.add(TableInfo.Index("index_breeding_records_childId", false,
            listOf("childId"), listOf("ASC")))
        val _infoBreedingRecords: TableInfo = TableInfo("breeding_records", _columnsBreedingRecords,
            _foreignKeysBreedingRecords, _indicesBreedingRecords)
        val _existingBreedingRecords: TableInfo = tableInfoRead(connection, "breeding_records")
        if (!_infoBreedingRecords.equals(_existingBreedingRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |breeding_records(com.rio.rostry.data.database.entity.BreedingRecordEntity).
              | Expected:
              |""".trimMargin() + _infoBreedingRecords + """
              |
              | Found:
              |""".trimMargin() + _existingBreedingRecords)
        }
        val _columnsTraits: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTraits.put("traitId", TableInfo.Column("traitId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTraits.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTraits.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTraits: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTraits: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTraits: TableInfo = TableInfo("traits", _columnsTraits, _foreignKeysTraits,
            _indicesTraits)
        val _existingTraits: TableInfo = tableInfoRead(connection, "traits")
        if (!_infoTraits.equals(_existingTraits)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |traits(com.rio.rostry.data.database.entity.TraitEntity).
              | Expected:
              |""".trimMargin() + _infoTraits + """
              |
              | Found:
              |""".trimMargin() + _existingTraits)
        }
        val _columnsProductTraits: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProductTraits.put("productId", TableInfo.Column("productId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProductTraits.put("traitId", TableInfo.Column("traitId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProductTraits: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysProductTraits.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        _foreignKeysProductTraits.add(TableInfo.ForeignKey("traits", "CASCADE", "NO ACTION",
            listOf("traitId"), listOf("traitId")))
        val _indicesProductTraits: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProductTraits.add(TableInfo.Index("index_product_traits_traitId", false,
            listOf("traitId"), listOf("ASC")))
        val _infoProductTraits: TableInfo = TableInfo("product_traits", _columnsProductTraits,
            _foreignKeysProductTraits, _indicesProductTraits)
        val _existingProductTraits: TableInfo = tableInfoRead(connection, "product_traits")
        if (!_infoProductTraits.equals(_existingProductTraits)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |product_traits(com.rio.rostry.data.database.entity.ProductTraitCrossRef).
              | Expected:
              |""".trimMargin() + _infoProductTraits + """
              |
              | Found:
              |""".trimMargin() + _existingProductTraits)
        }
        val _columnsLifecycleEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLifecycleEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLifecycleEvents.put("productId", TableInfo.Column("productId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLifecycleEvents.put("week", TableInfo.Column("week", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLifecycleEvents.put("stage", TableInfo.Column("stage", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLifecycleEvents.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLifecycleEvents.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLifecycleEvents.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLifecycleEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysLifecycleEvents.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesLifecycleEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesLifecycleEvents.add(TableInfo.Index("index_lifecycle_events_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesLifecycleEvents.add(TableInfo.Index("index_lifecycle_events_week", false,
            listOf("week"), listOf("ASC")))
        val _infoLifecycleEvents: TableInfo = TableInfo("lifecycle_events", _columnsLifecycleEvents,
            _foreignKeysLifecycleEvents, _indicesLifecycleEvents)
        val _existingLifecycleEvents: TableInfo = tableInfoRead(connection, "lifecycle_events")
        if (!_infoLifecycleEvents.equals(_existingLifecycleEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |lifecycle_events(com.rio.rostry.data.database.entity.LifecycleEventEntity).
              | Expected:
              |""".trimMargin() + _infoLifecycleEvents + """
              |
              | Found:
              |""".trimMargin() + _existingLifecycleEvents)
        }
        val _columnsTransferVerifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransferVerifications.put("verificationId", TableInfo.Column("verificationId",
            "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("transferId", TableInfo.Column("transferId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("step", TableInfo.Column("step", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("status", TableInfo.Column("status", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("photoBeforeUrl", TableInfo.Column("photoBeforeUrl",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("photoAfterUrl", TableInfo.Column("photoAfterUrl", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("photoBeforeMetaJson",
            TableInfo.Column("photoBeforeMetaJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("photoAfterMetaJson",
            TableInfo.Column("photoAfterMetaJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("gpsLat", TableInfo.Column("gpsLat", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("gpsLng", TableInfo.Column("gpsLng", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("identityDocType", TableInfo.Column("identityDocType",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("identityDocRef", TableInfo.Column("identityDocRef",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransferVerifications.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransferVerifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysTransferVerifications.add(TableInfo.ForeignKey("transfers", "CASCADE",
            "NO ACTION", listOf("transferId"), listOf("transferId")))
        val _indicesTransferVerifications: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTransferVerifications.add(TableInfo.Index("index_transfer_verifications_transferId",
            false, listOf("transferId"), listOf("ASC")))
        _indicesTransferVerifications.add(TableInfo.Index("index_transfer_verifications_status",
            false, listOf("status"), listOf("ASC")))
        val _infoTransferVerifications: TableInfo = TableInfo("transfer_verifications",
            _columnsTransferVerifications, _foreignKeysTransferVerifications,
            _indicesTransferVerifications)
        val _existingTransferVerifications: TableInfo = tableInfoRead(connection,
            "transfer_verifications")
        if (!_infoTransferVerifications.equals(_existingTransferVerifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transfer_verifications(com.rio.rostry.data.database.entity.TransferVerificationEntity).
              | Expected:
              |""".trimMargin() + _infoTransferVerifications + """
              |
              | Found:
              |""".trimMargin() + _existingTransferVerifications)
        }
        val _columnsDisputes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDisputes.put("disputeId", TableInfo.Column("disputeId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("transferId", TableInfo.Column("transferId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("reporterId", TableInfo.Column("reporterId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("reportedUserId", TableInfo.Column("reportedUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("evidenceUrls", TableInfo.Column("evidenceUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("resolution", TableInfo.Column("resolution", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("resolvedByAdminId", TableInfo.Column("resolvedByAdminId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDisputes.put("resolvedAt", TableInfo.Column("resolvedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDisputes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDisputes: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDisputes: TableInfo = TableInfo("disputes", _columnsDisputes, _foreignKeysDisputes,
            _indicesDisputes)
        val _existingDisputes: TableInfo = tableInfoRead(connection, "disputes")
        if (!_infoDisputes.equals(_existingDisputes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |disputes(com.rio.rostry.data.database.entity.DisputeEntity).
              | Expected:
              |""".trimMargin() + _infoDisputes + """
              |
              | Found:
              |""".trimMargin() + _existingDisputes)
        }
        val _columnsAuditLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAuditLogs.put("logId", TableInfo.Column("logId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuditLogs.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuditLogs.put("refId", TableInfo.Column("refId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuditLogs.put("action", TableInfo.Column("action", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuditLogs.put("actorUserId", TableInfo.Column("actorUserId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuditLogs.put("detailsJson", TableInfo.Column("detailsJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAuditLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAuditLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAuditLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAuditLogs.add(TableInfo.Index("index_audit_logs_refId", false, listOf("refId"),
            listOf("ASC")))
        _indicesAuditLogs.add(TableInfo.Index("index_audit_logs_type", false, listOf("type"),
            listOf("ASC")))
        val _infoAuditLogs: TableInfo = TableInfo("audit_logs", _columnsAuditLogs,
            _foreignKeysAuditLogs, _indicesAuditLogs)
        val _existingAuditLogs: TableInfo = tableInfoRead(connection, "audit_logs")
        if (!_infoAuditLogs.equals(_existingAuditLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |audit_logs(com.rio.rostry.data.database.entity.AuditLogEntity).
              | Expected:
              |""".trimMargin() + _infoAuditLogs + """
              |
              | Found:
              |""".trimMargin() + _existingAuditLogs)
        }
        val _columnsAdminAuditLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAdminAuditLogs.put("logId", TableInfo.Column("logId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("adminId", TableInfo.Column("adminId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("adminName", TableInfo.Column("adminName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("actionType", TableInfo.Column("actionType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("targetId", TableInfo.Column("targetId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("targetType", TableInfo.Column("targetType", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("details", TableInfo.Column("details", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAdminAuditLogs.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAdminAuditLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAdminAuditLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAdminAuditLogs.add(TableInfo.Index("index_admin_audit_logs_adminId", false,
            listOf("adminId"), listOf("ASC")))
        _indicesAdminAuditLogs.add(TableInfo.Index("index_admin_audit_logs_actionType", false,
            listOf("actionType"), listOf("ASC")))
        _indicesAdminAuditLogs.add(TableInfo.Index("index_admin_audit_logs_timestamp", false,
            listOf("timestamp"), listOf("ASC")))
        _indicesAdminAuditLogs.add(TableInfo.Index("index_admin_audit_logs_targetId", false,
            listOf("targetId"), listOf("ASC")))
        val _infoAdminAuditLogs: TableInfo = TableInfo("admin_audit_logs", _columnsAdminAuditLogs,
            _foreignKeysAdminAuditLogs, _indicesAdminAuditLogs)
        val _existingAdminAuditLogs: TableInfo = tableInfoRead(connection, "admin_audit_logs")
        if (!_infoAdminAuditLogs.equals(_existingAdminAuditLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |admin_audit_logs(com.rio.rostry.data.database.entity.AdminAuditLogEntity).
              | Expected:
              |""".trimMargin() + _infoAdminAuditLogs + """
              |
              | Found:
              |""".trimMargin() + _existingAdminAuditLogs)
        }
        val _columnsPosts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPosts.put("postId", TableInfo.Column("postId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("authorId", TableInfo.Column("authorId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("text", TableInfo.Column("text", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("mediaUrl", TableInfo.Column("mediaUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("thumbnailUrl", TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("hashtags", TableInfo.Column("hashtags", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("mentions", TableInfo.Column("mentions", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("parentPostId", TableInfo.Column("parentPostId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsPosts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPosts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPosts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesPosts.add(TableInfo.Index("index_posts_authorId", false, listOf("authorId"),
            listOf("ASC")))
        _indicesPosts.add(TableInfo.Index("index_posts_createdAt", false, listOf("createdAt"),
            listOf("ASC")))
        _indicesPosts.add(TableInfo.Index("index_posts_type", false, listOf("type"), listOf("ASC")))
        val _infoPosts: TableInfo = TableInfo("posts", _columnsPosts, _foreignKeysPosts,
            _indicesPosts)
        val _existingPosts: TableInfo = tableInfoRead(connection, "posts")
        if (!_infoPosts.equals(_existingPosts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |posts(com.rio.rostry.data.database.entity.PostEntity).
              | Expected:
              |""".trimMargin() + _infoPosts + """
              |
              | Found:
              |""".trimMargin() + _existingPosts)
        }
        val _columnsComments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsComments.put("commentId", TableInfo.Column("commentId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("postId", TableInfo.Column("postId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("authorId", TableInfo.Column("authorId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("text", TableInfo.Column("text", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysComments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesComments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesComments.add(TableInfo.Index("index_comments_postId", false, listOf("postId"),
            listOf("ASC")))
        _indicesComments.add(TableInfo.Index("index_comments_authorId", false, listOf("authorId"),
            listOf("ASC")))
        _indicesComments.add(TableInfo.Index("index_comments_createdAt", false, listOf("createdAt"),
            listOf("ASC")))
        val _infoComments: TableInfo = TableInfo("comments", _columnsComments, _foreignKeysComments,
            _indicesComments)
        val _existingComments: TableInfo = tableInfoRead(connection, "comments")
        if (!_infoComments.equals(_existingComments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |comments(com.rio.rostry.data.database.entity.CommentEntity).
              | Expected:
              |""".trimMargin() + _infoComments + """
              |
              | Found:
              |""".trimMargin() + _existingComments)
        }
        val _columnsLikes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLikes.put("likeId", TableInfo.Column("likeId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLikes.put("postId", TableInfo.Column("postId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLikes.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLikes.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLikes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLikes: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesLikes.add(TableInfo.Index("index_likes_postId_userId", true, listOf("postId",
            "userId"), listOf("ASC", "ASC")))
        val _infoLikes: TableInfo = TableInfo("likes", _columnsLikes, _foreignKeysLikes,
            _indicesLikes)
        val _existingLikes: TableInfo = tableInfoRead(connection, "likes")
        if (!_infoLikes.equals(_existingLikes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |likes(com.rio.rostry.data.database.entity.LikeEntity).
              | Expected:
              |""".trimMargin() + _infoLikes + """
              |
              | Found:
              |""".trimMargin() + _existingLikes)
        }
        val _columnsFollows: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFollows.put("followId", TableInfo.Column("followId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("followerId", TableInfo.Column("followerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("followedId", TableInfo.Column("followedId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFollows.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFollows: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFollows: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFollows.add(TableInfo.Index("index_follows_followerId_followedId", true,
            listOf("followerId", "followedId"), listOf("ASC", "ASC")))
        val _infoFollows: TableInfo = TableInfo("follows", _columnsFollows, _foreignKeysFollows,
            _indicesFollows)
        val _existingFollows: TableInfo = tableInfoRead(connection, "follows")
        if (!_infoFollows.equals(_existingFollows)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |follows(com.rio.rostry.data.database.entity.FollowEntity).
              | Expected:
              |""".trimMargin() + _infoFollows + """
              |
              | Found:
              |""".trimMargin() + _existingFollows)
        }
        val _columnsGroups: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGroups.put("groupId", TableInfo.Column("groupId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroups.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroups.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroups.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroups.put("category", TableInfo.Column("category", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroups.put("isMarketplace", TableInfo.Column("isMarketplace", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGroups.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGroups: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesGroups: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesGroups.add(TableInfo.Index("index_groups_ownerId", false, listOf("ownerId"),
            listOf("ASC")))
        _indicesGroups.add(TableInfo.Index("index_groups_name", false, listOf("name"),
            listOf("ASC")))
        val _infoGroups: TableInfo = TableInfo("groups", _columnsGroups, _foreignKeysGroups,
            _indicesGroups)
        val _existingGroups: TableInfo = tableInfoRead(connection, "groups")
        if (!_infoGroups.equals(_existingGroups)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |groups(com.rio.rostry.data.database.entity.GroupEntity).
              | Expected:
              |""".trimMargin() + _infoGroups + """
              |
              | Found:
              |""".trimMargin() + _existingGroups)
        }
        val _columnsGroupMembers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGroupMembers.put("membershipId", TableInfo.Column("membershipId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMembers.put("groupId", TableInfo.Column("groupId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMembers.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMembers.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGroupMembers.put("joinedAt", TableInfo.Column("joinedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGroupMembers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesGroupMembers: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesGroupMembers.add(TableInfo.Index("index_group_members_groupId_userId", true,
            listOf("groupId", "userId"), listOf("ASC", "ASC")))
        val _infoGroupMembers: TableInfo = TableInfo("group_members", _columnsGroupMembers,
            _foreignKeysGroupMembers, _indicesGroupMembers)
        val _existingGroupMembers: TableInfo = tableInfoRead(connection, "group_members")
        if (!_infoGroupMembers.equals(_existingGroupMembers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |group_members(com.rio.rostry.data.database.entity.GroupMemberEntity).
              | Expected:
              |""".trimMargin() + _infoGroupMembers + """
              |
              | Found:
              |""".trimMargin() + _existingGroupMembers)
        }
        val _columnsEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEvents.put("groupId", TableInfo.Column("groupId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEvents.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEvents.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEvents.put("location", TableInfo.Column("location", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEvents.put("startTime", TableInfo.Column("startTime", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEvents.put("endTime", TableInfo.Column("endTime", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEvents.add(TableInfo.Index("index_events_groupId", false, listOf("groupId"),
            listOf("ASC")))
        _indicesEvents.add(TableInfo.Index("index_events_startTime", false, listOf("startTime"),
            listOf("ASC")))
        val _infoEvents: TableInfo = TableInfo("events", _columnsEvents, _foreignKeysEvents,
            _indicesEvents)
        val _existingEvents: TableInfo = tableInfoRead(connection, "events")
        if (!_infoEvents.equals(_existingEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |events(com.rio.rostry.data.database.entity.EventEntity).
              | Expected:
              |""".trimMargin() + _infoEvents + """
              |
              | Found:
              |""".trimMargin() + _existingEvents)
        }
        val _columnsExpertBookings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExpertBookings.put("bookingId", TableInfo.Column("bookingId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertBookings.put("expertId", TableInfo.Column("expertId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertBookings.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertBookings.put("topic", TableInfo.Column("topic", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertBookings.put("startTime", TableInfo.Column("startTime", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertBookings.put("endTime", TableInfo.Column("endTime", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertBookings.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExpertBookings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExpertBookings: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesExpertBookings.add(TableInfo.Index("index_expert_bookings_expertId", false,
            listOf("expertId"), listOf("ASC")))
        _indicesExpertBookings.add(TableInfo.Index("index_expert_bookings_userId", false,
            listOf("userId"), listOf("ASC")))
        _indicesExpertBookings.add(TableInfo.Index("index_expert_bookings_startTime", false,
            listOf("startTime"), listOf("ASC")))
        val _infoExpertBookings: TableInfo = TableInfo("expert_bookings", _columnsExpertBookings,
            _foreignKeysExpertBookings, _indicesExpertBookings)
        val _existingExpertBookings: TableInfo = tableInfoRead(connection, "expert_bookings")
        if (!_infoExpertBookings.equals(_existingExpertBookings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |expert_bookings(com.rio.rostry.data.database.entity.ExpertBookingEntity).
              | Expected:
              |""".trimMargin() + _infoExpertBookings + """
              |
              | Found:
              |""".trimMargin() + _existingExpertBookings)
        }
        val _columnsModerationReports: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsModerationReports.put("reportId", TableInfo.Column("reportId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("targetType", TableInfo.Column("targetType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("targetId", TableInfo.Column("targetId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("reporterId", TableInfo.Column("reporterId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationReports.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysModerationReports: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesModerationReports: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesModerationReports.add(TableInfo.Index("index_moderation_reports_targetType", false,
            listOf("targetType"), listOf("ASC")))
        _indicesModerationReports.add(TableInfo.Index("index_moderation_reports_targetId", false,
            listOf("targetId"), listOf("ASC")))
        _indicesModerationReports.add(TableInfo.Index("index_moderation_reports_status", false,
            listOf("status"), listOf("ASC")))
        val _infoModerationReports: TableInfo = TableInfo("moderation_reports",
            _columnsModerationReports, _foreignKeysModerationReports, _indicesModerationReports)
        val _existingModerationReports: TableInfo = tableInfoRead(connection, "moderation_reports")
        if (!_infoModerationReports.equals(_existingModerationReports)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |moderation_reports(com.rio.rostry.data.database.entity.ModerationReportEntity).
              | Expected:
              |""".trimMargin() + _infoModerationReports + """
              |
              | Found:
              |""".trimMargin() + _existingModerationReports)
        }
        val _columnsBadges: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBadges.put("badgeId", TableInfo.Column("badgeId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadges.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadges.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadges.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadges.put("awardedAt", TableInfo.Column("awardedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBadges: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBadges: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBadges.add(TableInfo.Index("index_badges_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesBadges.add(TableInfo.Index("index_badges_awardedAt", false, listOf("awardedAt"),
            listOf("ASC")))
        val _infoBadges: TableInfo = TableInfo("badges", _columnsBadges, _foreignKeysBadges,
            _indicesBadges)
        val _existingBadges: TableInfo = tableInfoRead(connection, "badges")
        if (!_infoBadges.equals(_existingBadges)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |badges(com.rio.rostry.data.database.entity.BadgeEntity).
              | Expected:
              |""".trimMargin() + _infoBadges + """
              |
              | Found:
              |""".trimMargin() + _existingBadges)
        }
        val _columnsReputation: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReputation.put("repId", TableInfo.Column("repId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReputation.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReputation.put("score", TableInfo.Column("score", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReputation.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReputation: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReputation: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesReputation.add(TableInfo.Index("index_reputation_userId", true, listOf("userId"),
            listOf("ASC")))
        val _infoReputation: TableInfo = TableInfo("reputation", _columnsReputation,
            _foreignKeysReputation, _indicesReputation)
        val _existingReputation: TableInfo = tableInfoRead(connection, "reputation")
        if (!_infoReputation.equals(_existingReputation)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |reputation(com.rio.rostry.data.database.entity.ReputationEntity).
              | Expected:
              |""".trimMargin() + _infoReputation + """
              |
              | Found:
              |""".trimMargin() + _existingReputation)
        }
        val _columnsOutgoingMessages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOutgoingMessages.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("kind", TableInfo.Column("kind", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("threadOrGroupId", TableInfo.Column("threadOrGroupId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("fromUserId", TableInfo.Column("fromUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("toUserId", TableInfo.Column("toUserId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("bodyText", TableInfo.Column("bodyText", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("fileUri", TableInfo.Column("fileUri", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("fileName", TableInfo.Column("fileName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("priority", TableInfo.Column("priority", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("retryCount", TableInfo.Column("retryCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("maxRetries", TableInfo.Column("maxRetries", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("lastError", TableInfo.Column("lastError", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("sentAt", TableInfo.Column("sentAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("deliveredAt", TableInfo.Column("deliveredAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("readAt", TableInfo.Column("readAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutgoingMessages.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOutgoingMessages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOutgoingMessages: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOutgoingMessages.add(TableInfo.Index("index_outgoing_messages_status", false,
            listOf("status"), listOf("ASC")))
        _indicesOutgoingMessages.add(TableInfo.Index("index_outgoing_messages_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        _indicesOutgoingMessages.add(TableInfo.Index("index_outgoing_messages_priority", false,
            listOf("priority"), listOf("ASC")))
        val _infoOutgoingMessages: TableInfo = TableInfo("outgoing_messages",
            _columnsOutgoingMessages, _foreignKeysOutgoingMessages, _indicesOutgoingMessages)
        val _existingOutgoingMessages: TableInfo = tableInfoRead(connection, "outgoing_messages")
        if (!_infoOutgoingMessages.equals(_existingOutgoingMessages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |outgoing_messages(com.rio.rostry.data.database.entity.OutgoingMessageEntity).
              | Expected:
              |""".trimMargin() + _infoOutgoingMessages + """
              |
              | Found:
              |""".trimMargin() + _existingOutgoingMessages)
        }
        val _columnsRateLimits: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRateLimits.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRateLimits.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRateLimits.put("action", TableInfo.Column("action", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRateLimits.put("lastAt", TableInfo.Column("lastAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRateLimits: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRateLimits: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesRateLimits.add(TableInfo.Index("index_rate_limits_userId_action", true,
            listOf("userId", "action"), listOf("ASC", "ASC")))
        val _infoRateLimits: TableInfo = TableInfo("rate_limits", _columnsRateLimits,
            _foreignKeysRateLimits, _indicesRateLimits)
        val _existingRateLimits: TableInfo = tableInfoRead(connection, "rate_limits")
        if (!_infoRateLimits.equals(_existingRateLimits)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |rate_limits(com.rio.rostry.data.database.entity.RateLimitEntity).
              | Expected:
              |""".trimMargin() + _infoRateLimits + """
              |
              | Found:
              |""".trimMargin() + _existingRateLimits)
        }
        val _columnsEventRsvps: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEventRsvps.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventRsvps.put("eventId", TableInfo.Column("eventId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventRsvps.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventRsvps.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEventRsvps.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEventRsvps: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEventRsvps: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEventRsvps.add(TableInfo.Index("index_event_rsvps_eventId_userId", true,
            listOf("eventId", "userId"), listOf("ASC", "ASC")))
        val _infoEventRsvps: TableInfo = TableInfo("event_rsvps", _columnsEventRsvps,
            _foreignKeysEventRsvps, _indicesEventRsvps)
        val _existingEventRsvps: TableInfo = tableInfoRead(connection, "event_rsvps")
        if (!_infoEventRsvps.equals(_existingEventRsvps)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |event_rsvps(com.rio.rostry.data.database.entity.EventRsvpEntity).
              | Expected:
              |""".trimMargin() + _infoEventRsvps + """
              |
              | Found:
              |""".trimMargin() + _existingEventRsvps)
        }
        val _columnsAnalyticsDaily: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAnalyticsDaily.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("dateKey", TableInfo.Column("dateKey", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("salesRevenue", TableInfo.Column("salesRevenue", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("ordersCount", TableInfo.Column("ordersCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("productViews", TableInfo.Column("productViews", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("likesCount", TableInfo.Column("likesCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("commentsCount", TableInfo.Column("commentsCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("transfersCount", TableInfo.Column("transfersCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("breedingSuccessRate", TableInfo.Column("breedingSuccessRate",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("engagementScore", TableInfo.Column("engagementScore", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAnalyticsDaily.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAnalyticsDaily: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAnalyticsDaily: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesAnalyticsDaily.add(TableInfo.Index("index_analytics_daily_userId_dateKey", true,
            listOf("userId", "dateKey"), listOf("ASC", "ASC")))
        _indicesAnalyticsDaily.add(TableInfo.Index("index_analytics_daily_role", false,
            listOf("role"), listOf("ASC")))
        val _infoAnalyticsDaily: TableInfo = TableInfo("analytics_daily", _columnsAnalyticsDaily,
            _foreignKeysAnalyticsDaily, _indicesAnalyticsDaily)
        val _existingAnalyticsDaily: TableInfo = tableInfoRead(connection, "analytics_daily")
        if (!_infoAnalyticsDaily.equals(_existingAnalyticsDaily)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |analytics_daily(com.rio.rostry.data.database.entity.AnalyticsDailyEntity).
              | Expected:
              |""".trimMargin() + _infoAnalyticsDaily + """
              |
              | Found:
              |""".trimMargin() + _existingAnalyticsDaily)
        }
        val _columnsReports: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReports.put("reportId", TableInfo.Column("reportId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("periodStart", TableInfo.Column("periodStart", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("periodEnd", TableInfo.Column("periodEnd", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("format", TableInfo.Column("format", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("uri", TableInfo.Column("uri", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReports.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReports: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReports: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesReports.add(TableInfo.Index("index_reports_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesReports.add(TableInfo.Index("index_reports_periodStart", false,
            listOf("periodStart"), listOf("ASC")))
        _indicesReports.add(TableInfo.Index("index_reports_type", false, listOf("type"),
            listOf("ASC")))
        val _infoReports: TableInfo = TableInfo("reports", _columnsReports, _foreignKeysReports,
            _indicesReports)
        val _existingReports: TableInfo = tableInfoRead(connection, "reports")
        if (!_infoReports.equals(_existingReports)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |reports(com.rio.rostry.data.database.entity.ReportEntity).
              | Expected:
              |""".trimMargin() + _infoReports + """
              |
              | Found:
              |""".trimMargin() + _existingReports)
        }
        val _columnsStories: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStories.put("storyId", TableInfo.Column("storyId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStories.put("authorId", TableInfo.Column("authorId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStories.put("mediaUrl", TableInfo.Column("mediaUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStories.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStories.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStories: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStories: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesStories.add(TableInfo.Index("index_stories_authorId", false, listOf("authorId"),
            listOf("ASC")))
        _indicesStories.add(TableInfo.Index("index_stories_expiresAt", false, listOf("expiresAt"),
            listOf("ASC")))
        val _infoStories: TableInfo = TableInfo("stories", _columnsStories, _foreignKeysStories,
            _indicesStories)
        val _existingStories: TableInfo = tableInfoRead(connection, "stories")
        if (!_infoStories.equals(_existingStories)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |stories(com.rio.rostry.data.database.entity.StoryEntity).
              | Expected:
              |""".trimMargin() + _infoStories + """
              |
              | Found:
              |""".trimMargin() + _existingStories)
        }
        var _result: RoomOpenDelegate.ValidationResult
        _result = onValidateSchema2(connection)
        if (!_result.isValid) {
          return _result
        }
        _result = onValidateSchema3(connection)
        if (!_result.isValid) {
          return _result
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }

      private fun onValidateSchema2(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsGrowthRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGrowthRecords.put("recordId", TableInfo.Column("recordId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("week", TableInfo.Column("week", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("weightGrams", TableInfo.Column("weightGrams", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("heightCm", TableInfo.Column("heightCm", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("photoUrl", TableInfo.Column("photoUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("mediaItemsJson", TableInfo.Column("mediaItemsJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("healthStatus", TableInfo.Column("healthStatus", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("milestone", TableInfo.Column("milestone", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("correctionOf", TableInfo.Column("correctionOf", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("editCount", TableInfo.Column("editCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("lastEditedBy", TableInfo.Column("lastEditedBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("isBatchLevel", TableInfo.Column("isBatchLevel", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGrowthRecords.put("sourceBatchId", TableInfo.Column("sourceBatchId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGrowthRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysGrowthRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesGrowthRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesGrowthRecords.add(TableInfo.Index("index_growth_records_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesGrowthRecords.add(TableInfo.Index("index_growth_records_week", false,
            listOf("week"), listOf("ASC")))
        _indicesGrowthRecords.add(TableInfo.Index("index_growth_records_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesGrowthRecords.add(TableInfo.Index("index_growth_records_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        val _infoGrowthRecords: TableInfo = TableInfo("growth_records", _columnsGrowthRecords,
            _foreignKeysGrowthRecords, _indicesGrowthRecords)
        val _existingGrowthRecords: TableInfo = tableInfoRead(connection, "growth_records")
        if (!_infoGrowthRecords.equals(_existingGrowthRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |growth_records(com.rio.rostry.data.database.entity.GrowthRecordEntity).
              | Expected:
              |""".trimMargin() + _infoGrowthRecords + """
              |
              | Found:
              |""".trimMargin() + _existingGrowthRecords)
        }
        val _columnsQuarantineRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsQuarantineRecords.put("quarantineId", TableInfo.Column("quarantineId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("productId", TableInfo.Column("productId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("protocol", TableInfo.Column("protocol", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("medicationScheduleJson",
            TableInfo.Column("medicationScheduleJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("statusHistoryJson", TableInfo.Column("statusHistoryJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("vetNotes", TableInfo.Column("vetNotes", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("startedAt", TableInfo.Column("startedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("lastUpdatedAt", TableInfo.Column("lastUpdatedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("updatesCount", TableInfo.Column("updatesCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("endedAt", TableInfo.Column("endedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("healthScore", TableInfo.Column("healthScore", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsQuarantineRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysQuarantineRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysQuarantineRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesQuarantineRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesQuarantineRecords.add(TableInfo.Index("index_quarantine_records_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesQuarantineRecords.add(TableInfo.Index("index_quarantine_records_status", false,
            listOf("status"), listOf("ASC")))
        _indicesQuarantineRecords.add(TableInfo.Index("index_quarantine_records_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesQuarantineRecords.add(TableInfo.Index("index_quarantine_records_startedAt", false,
            listOf("startedAt"), listOf("ASC")))
        val _infoQuarantineRecords: TableInfo = TableInfo("quarantine_records",
            _columnsQuarantineRecords, _foreignKeysQuarantineRecords, _indicesQuarantineRecords)
        val _existingQuarantineRecords: TableInfo = tableInfoRead(connection, "quarantine_records")
        if (!_infoQuarantineRecords.equals(_existingQuarantineRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |quarantine_records(com.rio.rostry.data.database.entity.QuarantineRecordEntity).
              | Expected:
              |""".trimMargin() + _infoQuarantineRecords + """
              |
              | Found:
              |""".trimMargin() + _existingQuarantineRecords)
        }
        val _columnsMortalityRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMortalityRecords.put("deathId", TableInfo.Column("deathId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("productId", TableInfo.Column("productId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("causeCategory", TableInfo.Column("causeCategory", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("circumstances", TableInfo.Column("circumstances", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("ageWeeks", TableInfo.Column("ageWeeks", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("disposalMethod", TableInfo.Column("disposalMethod", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("quantity", TableInfo.Column("quantity", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("financialImpactInr", TableInfo.Column("financialImpactInr",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("photoUrls", TableInfo.Column("photoUrls", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("mediaItemsJson", TableInfo.Column("mediaItemsJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("occurredAt", TableInfo.Column("occurredAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("affectedProductIds", TableInfo.Column("affectedProductIds",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMortalityRecords.put("affectsAllChildren", TableInfo.Column("affectsAllChildren",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMortalityRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMortalityRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMortalityRecords.add(TableInfo.Index("index_mortality_records_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesMortalityRecords.add(TableInfo.Index("index_mortality_records_causeCategory", false,
            listOf("causeCategory"), listOf("ASC")))
        _indicesMortalityRecords.add(TableInfo.Index("index_mortality_records_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesMortalityRecords.add(TableInfo.Index("index_mortality_records_occurredAt", false,
            listOf("occurredAt"), listOf("ASC")))
        val _infoMortalityRecords: TableInfo = TableInfo("mortality_records",
            _columnsMortalityRecords, _foreignKeysMortalityRecords, _indicesMortalityRecords)
        val _existingMortalityRecords: TableInfo = tableInfoRead(connection, "mortality_records")
        if (!_infoMortalityRecords.equals(_existingMortalityRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |mortality_records(com.rio.rostry.data.database.entity.MortalityRecordEntity).
              | Expected:
              |""".trimMargin() + _infoMortalityRecords + """
              |
              | Found:
              |""".trimMargin() + _existingMortalityRecords)
        }
        val _columnsVaccinationRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsVaccinationRecords.put("vaccinationId", TableInfo.Column("vaccinationId", "TEXT",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("productId", TableInfo.Column("productId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("vaccineType", TableInfo.Column("vaccineType", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("supplier", TableInfo.Column("supplier", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("batchCode", TableInfo.Column("batchCode", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("doseMl", TableInfo.Column("doseMl", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("scheduledAt", TableInfo.Column("scheduledAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("administeredAt", TableInfo.Column("administeredAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("efficacyNotes", TableInfo.Column("efficacyNotes", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("costInr", TableInfo.Column("costInr", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("photoUrls", TableInfo.Column("photoUrls", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("mediaItemsJson", TableInfo.Column("mediaItemsJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("correctionOf", TableInfo.Column("correctionOf", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("editCount", TableInfo.Column("editCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVaccinationRecords.put("lastEditedBy", TableInfo.Column("lastEditedBy", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVaccinationRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysVaccinationRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesVaccinationRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesVaccinationRecords.add(TableInfo.Index("index_vaccination_records_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesVaccinationRecords.add(TableInfo.Index("index_vaccination_records_vaccineType",
            false, listOf("vaccineType"), listOf("ASC")))
        _indicesVaccinationRecords.add(TableInfo.Index("index_vaccination_records_scheduledAt",
            false, listOf("scheduledAt"), listOf("ASC")))
        _indicesVaccinationRecords.add(TableInfo.Index("index_vaccination_records_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        val _infoVaccinationRecords: TableInfo = TableInfo("vaccination_records",
            _columnsVaccinationRecords, _foreignKeysVaccinationRecords, _indicesVaccinationRecords)
        val _existingVaccinationRecords: TableInfo = tableInfoRead(connection,
            "vaccination_records")
        if (!_infoVaccinationRecords.equals(_existingVaccinationRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |vaccination_records(com.rio.rostry.data.database.entity.VaccinationRecordEntity).
              | Expected:
              |""".trimMargin() + _infoVaccinationRecords + """
              |
              | Found:
              |""".trimMargin() + _existingVaccinationRecords)
        }
        val _columnsHatchingBatches: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHatchingBatches.put("batchId", TableInfo.Column("batchId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("startedAt", TableInfo.Column("startedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("expectedHatchAt", TableInfo.Column("expectedHatchAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("temperatureC", TableInfo.Column("temperatureC", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("humidityPct", TableInfo.Column("humidityPct", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("eggsCount", TableInfo.Column("eggsCount", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("sourceCollectionId", TableInfo.Column("sourceCollectionId",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("hatchedAt", TableInfo.Column("hatchedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingBatches.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHatchingBatches: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHatchingBatches: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesHatchingBatches.add(TableInfo.Index("index_hatching_batches_name", false,
            listOf("name"), listOf("ASC")))
        _indicesHatchingBatches.add(TableInfo.Index("index_hatching_batches_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesHatchingBatches.add(TableInfo.Index("index_hatching_batches_expectedHatchAt", false,
            listOf("expectedHatchAt"), listOf("ASC")))
        val _infoHatchingBatches: TableInfo = TableInfo("hatching_batches", _columnsHatchingBatches,
            _foreignKeysHatchingBatches, _indicesHatchingBatches)
        val _existingHatchingBatches: TableInfo = tableInfoRead(connection, "hatching_batches")
        if (!_infoHatchingBatches.equals(_existingHatchingBatches)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |hatching_batches(com.rio.rostry.data.database.entity.HatchingBatchEntity).
              | Expected:
              |""".trimMargin() + _infoHatchingBatches + """
              |
              | Found:
              |""".trimMargin() + _existingHatchingBatches)
        }
        val _columnsHatchingLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHatchingLogs.put("logId", TableInfo.Column("logId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("batchId", TableInfo.Column("batchId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("qualityScore", TableInfo.Column("qualityScore", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("temperatureC", TableInfo.Column("temperatureC", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("humidityPct", TableInfo.Column("humidityPct", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHatchingLogs.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHatchingLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysHatchingLogs.add(TableInfo.ForeignKey("hatching_batches", "CASCADE",
            "NO ACTION", listOf("batchId"), listOf("batchId")))
        _foreignKeysHatchingLogs.add(TableInfo.ForeignKey("products", "SET NULL", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesHatchingLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesHatchingLogs.add(TableInfo.Index("index_hatching_logs_batchId", false,
            listOf("batchId"), listOf("ASC")))
        _indicesHatchingLogs.add(TableInfo.Index("index_hatching_logs_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesHatchingLogs.add(TableInfo.Index("index_hatching_logs_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesHatchingLogs.add(TableInfo.Index("index_hatching_logs_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        val _infoHatchingLogs: TableInfo = TableInfo("hatching_logs", _columnsHatchingLogs,
            _foreignKeysHatchingLogs, _indicesHatchingLogs)
        val _existingHatchingLogs: TableInfo = tableInfoRead(connection, "hatching_logs")
        if (!_infoHatchingLogs.equals(_existingHatchingLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |hatching_logs(com.rio.rostry.data.database.entity.HatchingLogEntity).
              | Expected:
              |""".trimMargin() + _infoHatchingLogs + """
              |
              | Found:
              |""".trimMargin() + _existingHatchingLogs)
        }
        val _columnsAchievementsDef: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAchievementsDef.put("achievementId", TableInfo.Column("achievementId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAchievementsDef.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAchievementsDef.put("description", TableInfo.Column("description", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAchievementsDef.put("points", TableInfo.Column("points", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAchievementsDef.put("category", TableInfo.Column("category", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAchievementsDef.put("icon", TableInfo.Column("icon", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAchievementsDef: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAchievementsDef: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAchievementsDef: TableInfo = TableInfo("achievements_def", _columnsAchievementsDef,
            _foreignKeysAchievementsDef, _indicesAchievementsDef)
        val _existingAchievementsDef: TableInfo = tableInfoRead(connection, "achievements_def")
        if (!_infoAchievementsDef.equals(_existingAchievementsDef)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |achievements_def(com.rio.rostry.data.database.entity.AchievementEntity).
              | Expected:
              |""".trimMargin() + _infoAchievementsDef + """
              |
              | Found:
              |""".trimMargin() + _existingAchievementsDef)
        }
        val _columnsUserProgress: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUserProgress.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProgress.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProgress.put("achievementId", TableInfo.Column("achievementId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProgress.put("progress", TableInfo.Column("progress", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProgress.put("target", TableInfo.Column("target", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProgress.put("unlockedAt", TableInfo.Column("unlockedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserProgress.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserProgress: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUserProgress: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUserProgress: TableInfo = TableInfo("user_progress", _columnsUserProgress,
            _foreignKeysUserProgress, _indicesUserProgress)
        val _existingUserProgress: TableInfo = tableInfoRead(connection, "user_progress")
        if (!_infoUserProgress.equals(_existingUserProgress)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |user_progress(com.rio.rostry.data.database.entity.UserProgressEntity).
              | Expected:
              |""".trimMargin() + _infoUserProgress + """
              |
              | Found:
              |""".trimMargin() + _existingUserProgress)
        }
        val _columnsBadgesDef: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBadgesDef.put("badgeId", TableInfo.Column("badgeId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadgesDef.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadgesDef.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBadgesDef.put("icon", TableInfo.Column("icon", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBadgesDef: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBadgesDef: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBadgesDef: TableInfo = TableInfo("badges_def", _columnsBadgesDef,
            _foreignKeysBadgesDef, _indicesBadgesDef)
        val _existingBadgesDef: TableInfo = tableInfoRead(connection, "badges_def")
        if (!_infoBadgesDef.equals(_existingBadgesDef)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |badges_def(com.rio.rostry.data.database.entity.GamificationBadgeEntity).
              | Expected:
              |""".trimMargin() + _infoBadgesDef + """
              |
              | Found:
              |""".trimMargin() + _existingBadgesDef)
        }
        val _columnsLeaderboard: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLeaderboard.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLeaderboard.put("periodKey", TableInfo.Column("periodKey", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLeaderboard.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLeaderboard.put("score", TableInfo.Column("score", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsLeaderboard.put("rank", TableInfo.Column("rank", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLeaderboard: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLeaderboard: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoLeaderboard: TableInfo = TableInfo("leaderboard", _columnsLeaderboard,
            _foreignKeysLeaderboard, _indicesLeaderboard)
        val _existingLeaderboard: TableInfo = tableInfoRead(connection, "leaderboard")
        if (!_infoLeaderboard.equals(_existingLeaderboard)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |leaderboard(com.rio.rostry.data.database.entity.LeaderboardEntity).
              | Expected:
              |""".trimMargin() + _infoLeaderboard + """
              |
              | Found:
              |""".trimMargin() + _existingLeaderboard)
        }
        val _columnsRewardsDef: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRewardsDef.put("rewardId", TableInfo.Column("rewardId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRewardsDef.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRewardsDef.put("description", TableInfo.Column("description", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRewardsDef.put("pointsRequired", TableInfo.Column("pointsRequired", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRewardsDef: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRewardsDef: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRewardsDef: TableInfo = TableInfo("rewards_def", _columnsRewardsDef,
            _foreignKeysRewardsDef, _indicesRewardsDef)
        val _existingRewardsDef: TableInfo = tableInfoRead(connection, "rewards_def")
        if (!_infoRewardsDef.equals(_existingRewardsDef)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |rewards_def(com.rio.rostry.data.database.entity.RewardEntity).
              | Expected:
              |""".trimMargin() + _infoRewardsDef + """
              |
              | Found:
              |""".trimMargin() + _existingRewardsDef)
        }
        val _columnsThreadMetadata: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsThreadMetadata.put("threadId", TableInfo.Column("threadId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("title", TableInfo.Column("title", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("contextType", TableInfo.Column("contextType", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("relatedEntityId", TableInfo.Column("relatedEntityId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("topic", TableInfo.Column("topic", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("participantIds", TableInfo.Column("participantIds", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("lastMessageAt", TableInfo.Column("lastMessageAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsThreadMetadata.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysThreadMetadata: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesThreadMetadata: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesThreadMetadata.add(TableInfo.Index("index_thread_metadata_contextType", false,
            listOf("contextType"), listOf("ASC")))
        _indicesThreadMetadata.add(TableInfo.Index("index_thread_metadata_lastMessageAt", false,
            listOf("lastMessageAt"), listOf("ASC")))
        _indicesThreadMetadata.add(TableInfo.Index("index_thread_metadata_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        val _infoThreadMetadata: TableInfo = TableInfo("thread_metadata", _columnsThreadMetadata,
            _foreignKeysThreadMetadata, _indicesThreadMetadata)
        val _existingThreadMetadata: TableInfo = tableInfoRead(connection, "thread_metadata")
        if (!_infoThreadMetadata.equals(_existingThreadMetadata)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |thread_metadata(com.rio.rostry.data.database.entity.ThreadMetadataEntity).
              | Expected:
              |""".trimMargin() + _infoThreadMetadata + """
              |
              | Found:
              |""".trimMargin() + _existingThreadMetadata)
        }
        val _columnsCommunityRecommendations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCommunityRecommendations.put("recommendationId",
            TableInfo.Column("recommendationId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("userId", TableInfo.Column("userId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("targetId", TableInfo.Column("targetId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("score", TableInfo.Column("score", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("reason", TableInfo.Column("reason", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("createdAt", TableInfo.Column("createdAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCommunityRecommendations.put("dismissed", TableInfo.Column("dismissed", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCommunityRecommendations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCommunityRecommendations: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCommunityRecommendations.add(TableInfo.Index("index_community_recommendations_userId",
            false, listOf("userId"), listOf("ASC")))
        _indicesCommunityRecommendations.add(TableInfo.Index("index_community_recommendations_type",
            false, listOf("type"), listOf("ASC")))
        _indicesCommunityRecommendations.add(TableInfo.Index("index_community_recommendations_score",
            false, listOf("score"), listOf("ASC")))
        _indicesCommunityRecommendations.add(TableInfo.Index("index_community_recommendations_expiresAt",
            false, listOf("expiresAt"), listOf("ASC")))
        val _infoCommunityRecommendations: TableInfo = TableInfo("community_recommendations",
            _columnsCommunityRecommendations, _foreignKeysCommunityRecommendations,
            _indicesCommunityRecommendations)
        val _existingCommunityRecommendations: TableInfo = tableInfoRead(connection,
            "community_recommendations")
        if (!_infoCommunityRecommendations.equals(_existingCommunityRecommendations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |community_recommendations(com.rio.rostry.data.database.entity.CommunityRecommendationEntity).
              | Expected:
              |""".trimMargin() + _infoCommunityRecommendations + """
              |
              | Found:
              |""".trimMargin() + _existingCommunityRecommendations)
        }
        val _columnsUserInterests: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUserInterests.put("interestId", TableInfo.Column("interestId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUserInterests.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserInterests.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserInterests.put("value", TableInfo.Column("value", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserInterests.put("weight", TableInfo.Column("weight", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUserInterests.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUserInterests: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUserInterests: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesUserInterests.add(TableInfo.Index("index_user_interests_userId_category_value",
            true, listOf("userId", "category", "value"), listOf("ASC", "ASC", "ASC")))
        val _infoUserInterests: TableInfo = TableInfo("user_interests", _columnsUserInterests,
            _foreignKeysUserInterests, _indicesUserInterests)
        val _existingUserInterests: TableInfo = tableInfoRead(connection, "user_interests")
        if (!_infoUserInterests.equals(_existingUserInterests)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |user_interests(com.rio.rostry.data.database.entity.UserInterestEntity).
              | Expected:
              |""".trimMargin() + _infoUserInterests + """
              |
              | Found:
              |""".trimMargin() + _existingUserInterests)
        }
        val _columnsExpertProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExpertProfiles.put("userId", TableInfo.Column("userId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("specialties", TableInfo.Column("specialties", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("bio", TableInfo.Column("bio", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("rating", TableInfo.Column("rating", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("totalConsultations", TableInfo.Column("totalConsultations",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("availableForBooking", TableInfo.Column("availableForBooking",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("hourlyRate", TableInfo.Column("hourlyRate", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpertProfiles.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExpertProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExpertProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoExpertProfiles: TableInfo = TableInfo("expert_profiles", _columnsExpertProfiles,
            _foreignKeysExpertProfiles, _indicesExpertProfiles)
        val _existingExpertProfiles: TableInfo = tableInfoRead(connection, "expert_profiles")
        if (!_infoExpertProfiles.equals(_existingExpertProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |expert_profiles(com.rio.rostry.data.database.entity.ExpertProfileEntity).
              | Expected:
              |""".trimMargin() + _infoExpertProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingExpertProfiles)
        }
        val _columnsOutbox: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOutbox.put("outboxId", TableInfo.Column("outboxId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("entityType", TableInfo.Column("entityType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("entityId", TableInfo.Column("entityId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("operation", TableInfo.Column("operation", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("payloadJson", TableInfo.Column("payloadJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("retryCount", TableInfo.Column("retryCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("lastAttemptAt", TableInfo.Column("lastAttemptAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("priority", TableInfo.Column("priority", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("maxRetries", TableInfo.Column("maxRetries", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOutbox.put("contextJson", TableInfo.Column("contextJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOutbox: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOutbox: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOutbox.add(TableInfo.Index("index_outbox_userId", false, listOf("userId"),
            listOf("ASC")))
        _indicesOutbox.add(TableInfo.Index("index_outbox_status", false, listOf("status"),
            listOf("ASC")))
        _indicesOutbox.add(TableInfo.Index("index_outbox_createdAt", false, listOf("createdAt"),
            listOf("ASC")))
        _indicesOutbox.add(TableInfo.Index("index_outbox_priority", false, listOf("priority"),
            listOf("ASC")))
        _indicesOutbox.add(TableInfo.Index("index_outbox_status_priority_createdAt", false,
            listOf("status", "priority", "createdAt"), listOf("ASC", "ASC", "ASC")))
        val _infoOutbox: TableInfo = TableInfo("outbox", _columnsOutbox, _foreignKeysOutbox,
            _indicesOutbox)
        val _existingOutbox: TableInfo = tableInfoRead(connection, "outbox")
        if (!_infoOutbox.equals(_existingOutbox)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |outbox(com.rio.rostry.data.database.entity.OutboxEntity).
              | Expected:
              |""".trimMargin() + _infoOutbox + """
              |
              | Found:
              |""".trimMargin() + _existingOutbox)
        }
        val _columnsBreedingPairs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBreedingPairs.put("pairId", TableInfo.Column("pairId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("maleProductId", TableInfo.Column("maleProductId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("femaleProductId", TableInfo.Column("femaleProductId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("pairedAt", TableInfo.Column("pairedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("hatchSuccessRate", TableInfo.Column("hatchSuccessRate", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("eggsCollected", TableInfo.Column("eggsCollected", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("hatchedEggs", TableInfo.Column("hatchedEggs", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("separatedAt", TableInfo.Column("separatedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPairs.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBreedingPairs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBreedingPairs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBreedingPairs.add(TableInfo.Index("index_breeding_pairs_maleProductId", false,
            listOf("maleProductId"), listOf("ASC")))
        _indicesBreedingPairs.add(TableInfo.Index("index_breeding_pairs_femaleProductId", false,
            listOf("femaleProductId"), listOf("ASC")))
        _indicesBreedingPairs.add(TableInfo.Index("index_breeding_pairs_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesBreedingPairs.add(TableInfo.Index("index_breeding_pairs_status", false,
            listOf("status"), listOf("ASC")))
        val _infoBreedingPairs: TableInfo = TableInfo("breeding_pairs", _columnsBreedingPairs,
            _foreignKeysBreedingPairs, _indicesBreedingPairs)
        val _existingBreedingPairs: TableInfo = tableInfoRead(connection, "breeding_pairs")
        if (!_infoBreedingPairs.equals(_existingBreedingPairs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |breeding_pairs(com.rio.rostry.data.database.entity.BreedingPairEntity).
              | Expected:
              |""".trimMargin() + _infoBreedingPairs + """
              |
              | Found:
              |""".trimMargin() + _existingBreedingPairs)
        }
        val _columnsFarmAlerts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmAlerts.put("alertId", TableInfo.Column("alertId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("alertType", TableInfo.Column("alertType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("severity", TableInfo.Column("severity", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("message", TableInfo.Column("message", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("actionRoute", TableInfo.Column("actionRoute", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAlerts.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmAlerts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFarmAlerts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmAlerts.add(TableInfo.Index("index_farm_alerts_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmAlerts.add(TableInfo.Index("index_farm_alerts_isRead", false, listOf("isRead"),
            listOf("ASC")))
        _indicesFarmAlerts.add(TableInfo.Index("index_farm_alerts_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        val _infoFarmAlerts: TableInfo = TableInfo("farm_alerts", _columnsFarmAlerts,
            _foreignKeysFarmAlerts, _indicesFarmAlerts)
        val _existingFarmAlerts: TableInfo = tableInfoRead(connection, "farm_alerts")
        if (!_infoFarmAlerts.equals(_existingFarmAlerts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_alerts(com.rio.rostry.data.database.entity.FarmAlertEntity).
              | Expected:
              |""".trimMargin() + _infoFarmAlerts + """
              |
              | Found:
              |""".trimMargin() + _existingFarmAlerts)
        }
        val _columnsListingDrafts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsListingDrafts.put("draftId", TableInfo.Column("draftId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsListingDrafts.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsListingDrafts.put("step", TableInfo.Column("step", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsListingDrafts.put("formDataJson", TableInfo.Column("formDataJson", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListingDrafts.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListingDrafts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsListingDrafts.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysListingDrafts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesListingDrafts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesListingDrafts.add(TableInfo.Index("index_listing_drafts_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesListingDrafts.add(TableInfo.Index("index_listing_drafts_updatedAt", false,
            listOf("updatedAt"), listOf("ASC")))
        val _infoListingDrafts: TableInfo = TableInfo("listing_drafts", _columnsListingDrafts,
            _foreignKeysListingDrafts, _indicesListingDrafts)
        val _existingListingDrafts: TableInfo = tableInfoRead(connection, "listing_drafts")
        if (!_infoListingDrafts.equals(_existingListingDrafts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |listing_drafts(com.rio.rostry.data.database.entity.ListingDraftEntity).
              | Expected:
              |""".trimMargin() + _infoListingDrafts + """
              |
              | Found:
              |""".trimMargin() + _existingListingDrafts)
        }
        val _columnsFarmerDashboardSnapshots: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmerDashboardSnapshots.put("snapshotId", TableInfo.Column("snapshotId", "TEXT",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("farmerId", TableInfo.Column("farmerId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("weekStartAt", TableInfo.Column("weekStartAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("weekEndAt", TableInfo.Column("weekEndAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("revenueInr", TableInfo.Column("revenueInr", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("ordersCount", TableInfo.Column("ordersCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("hatchSuccessRate",
            TableInfo.Column("hatchSuccessRate", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("mortalityRate", TableInfo.Column("mortalityRate",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("deathsCount", TableInfo.Column("deathsCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("vaccinationCompletionRate",
            TableInfo.Column("vaccinationCompletionRate", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("growthRecordsCount",
            TableInfo.Column("growthRecordsCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("quarantineActiveCount",
            TableInfo.Column("quarantineActiveCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("productsReadyToListCount",
            TableInfo.Column("productsReadyToListCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("avgFeedKg", TableInfo.Column("avgFeedKg", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("medicationUsageCount",
            TableInfo.Column("medicationUsageCount", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("dailyLogComplianceRate",
            TableInfo.Column("dailyLogComplianceRate", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("actionSuggestions",
            TableInfo.Column("actionSuggestions", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("transfersInitiatedCount",
            TableInfo.Column("transfersInitiatedCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("transfersCompletedCount",
            TableInfo.Column("transfersCompletedCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("complianceScore", TableInfo.Column("complianceScore",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("onboardingCount", TableInfo.Column("onboardingCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("dailyGoalsCompletedCount",
            TableInfo.Column("dailyGoalsCompletedCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("analyticsInsightsCount",
            TableInfo.Column("analyticsInsightsCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("createdAt", TableInfo.Column("createdAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmerDashboardSnapshots.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmerDashboardSnapshots: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFarmerDashboardSnapshots: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmerDashboardSnapshots.add(TableInfo.Index("index_farmer_dashboard_snapshots_farmerId",
            false, listOf("farmerId"), listOf("ASC")))
        _indicesFarmerDashboardSnapshots.add(TableInfo.Index("index_farmer_dashboard_snapshots_weekStartAt",
            false, listOf("weekStartAt"), listOf("ASC")))
        val _infoFarmerDashboardSnapshots: TableInfo = TableInfo("farmer_dashboard_snapshots",
            _columnsFarmerDashboardSnapshots, _foreignKeysFarmerDashboardSnapshots,
            _indicesFarmerDashboardSnapshots)
        val _existingFarmerDashboardSnapshots: TableInfo = tableInfoRead(connection,
            "farmer_dashboard_snapshots")
        if (!_infoFarmerDashboardSnapshots.equals(_existingFarmerDashboardSnapshots)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farmer_dashboard_snapshots(com.rio.rostry.data.database.entity.FarmerDashboardSnapshotEntity).
              | Expected:
              |""".trimMargin() + _infoFarmerDashboardSnapshots + """
              |
              | Found:
              |""".trimMargin() + _existingFarmerDashboardSnapshots)
        }
        val _columnsMatingLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMatingLogs.put("logId", TableInfo.Column("logId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("pairId", TableInfo.Column("pairId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("matedAt", TableInfo.Column("matedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("observedBehavior", TableInfo.Column("observedBehavior", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("environmentalConditions",
            TableInfo.Column("environmentalConditions", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMatingLogs.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMatingLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysMatingLogs.add(TableInfo.ForeignKey("breeding_pairs", "CASCADE", "NO ACTION",
            listOf("pairId"), listOf("pairId")))
        val _indicesMatingLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMatingLogs.add(TableInfo.Index("index_mating_logs_pairId", false, listOf("pairId"),
            listOf("ASC")))
        _indicesMatingLogs.add(TableInfo.Index("index_mating_logs_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesMatingLogs.add(TableInfo.Index("index_mating_logs_matedAt", false,
            listOf("matedAt"), listOf("ASC")))
        val _infoMatingLogs: TableInfo = TableInfo("mating_logs", _columnsMatingLogs,
            _foreignKeysMatingLogs, _indicesMatingLogs)
        val _existingMatingLogs: TableInfo = tableInfoRead(connection, "mating_logs")
        if (!_infoMatingLogs.equals(_existingMatingLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |mating_logs(com.rio.rostry.data.database.entity.MatingLogEntity).
              | Expected:
              |""".trimMargin() + _infoMatingLogs + """
              |
              | Found:
              |""".trimMargin() + _existingMatingLogs)
        }
        val _columnsEggCollections: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEggCollections.put("collectionId", TableInfo.Column("collectionId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("pairId", TableInfo.Column("pairId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("eggsCollected", TableInfo.Column("eggsCollected", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("collectedAt", TableInfo.Column("collectedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("qualityGrade", TableInfo.Column("qualityGrade", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("weight", TableInfo.Column("weight", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("goodCount", TableInfo.Column("goodCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("damagedCount", TableInfo.Column("damagedCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("brokenCount", TableInfo.Column("brokenCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("trayLayoutJson", TableInfo.Column("trayLayoutJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("setForHatching", TableInfo.Column("setForHatching", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("linkedBatchId", TableInfo.Column("linkedBatchId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("setForHatchingAt", TableInfo.Column("setForHatchingAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEggCollections.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEggCollections: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysEggCollections.add(TableInfo.ForeignKey("breeding_pairs", "CASCADE",
            "NO ACTION", listOf("pairId"), listOf("pairId")))
        val _indicesEggCollections: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEggCollections.add(TableInfo.Index("index_egg_collections_pairId", false,
            listOf("pairId"), listOf("ASC")))
        _indicesEggCollections.add(TableInfo.Index("index_egg_collections_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesEggCollections.add(TableInfo.Index("index_egg_collections_collectedAt", false,
            listOf("collectedAt"), listOf("ASC")))
        val _infoEggCollections: TableInfo = TableInfo("egg_collections", _columnsEggCollections,
            _foreignKeysEggCollections, _indicesEggCollections)
        val _existingEggCollections: TableInfo = tableInfoRead(connection, "egg_collections")
        if (!_infoEggCollections.equals(_existingEggCollections)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |egg_collections(com.rio.rostry.data.database.entity.EggCollectionEntity).
              | Expected:
              |""".trimMargin() + _infoEggCollections + """
              |
              | Found:
              |""".trimMargin() + _existingEggCollections)
        }
        val _columnsEnthusiastDashboardSnapshots: MutableMap<String, TableInfo.Column> =
            mutableMapOf()
        _columnsEnthusiastDashboardSnapshots.put("snapshotId", TableInfo.Column("snapshotId",
            "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("userId", TableInfo.Column("userId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("weekStartAt", TableInfo.Column("weekStartAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("weekEndAt", TableInfo.Column("weekEndAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("hatchRateLast30Days",
            TableInfo.Column("hatchRateLast30Days", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("breederSuccessRate",
            TableInfo.Column("breederSuccessRate", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("disputedTransfersCount",
            TableInfo.Column("disputedTransfersCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("topBloodlinesEngagement",
            TableInfo.Column("topBloodlinesEngagement", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("activePairsCount",
            TableInfo.Column("activePairsCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("eggsCollectedCount",
            TableInfo.Column("eggsCollectedCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("hatchingDueCount",
            TableInfo.Column("hatchingDueCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("transfersPendingCount",
            TableInfo.Column("transfersPendingCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("pairsToMateCount",
            TableInfo.Column("pairsToMateCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("incubatingCount",
            TableInfo.Column("incubatingCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("sickBirdsCount",
            TableInfo.Column("sickBirdsCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("eggsCollectedToday",
            TableInfo.Column("eggsCollectedToday", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("createdAt", TableInfo.Column("createdAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("updatedAt", TableInfo.Column("updatedAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("dirty", TableInfo.Column("dirty", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastDashboardSnapshots.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEnthusiastDashboardSnapshots: MutableSet<TableInfo.ForeignKey> =
            mutableSetOf()
        val _indicesEnthusiastDashboardSnapshots: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEnthusiastDashboardSnapshots.add(TableInfo.Index("index_enthusiast_dashboard_snapshots_userId",
            false, listOf("userId"), listOf("ASC")))
        _indicesEnthusiastDashboardSnapshots.add(TableInfo.Index("index_enthusiast_dashboard_snapshots_weekStartAt",
            false, listOf("weekStartAt"), listOf("ASC")))
        val _infoEnthusiastDashboardSnapshots: TableInfo =
            TableInfo("enthusiast_dashboard_snapshots", _columnsEnthusiastDashboardSnapshots,
            _foreignKeysEnthusiastDashboardSnapshots, _indicesEnthusiastDashboardSnapshots)
        val _existingEnthusiastDashboardSnapshots: TableInfo = tableInfoRead(connection,
            "enthusiast_dashboard_snapshots")
        if (!_infoEnthusiastDashboardSnapshots.equals(_existingEnthusiastDashboardSnapshots)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |enthusiast_dashboard_snapshots(com.rio.rostry.data.database.entity.EnthusiastDashboardSnapshotEntity).
              | Expected:
              |""".trimMargin() + _infoEnthusiastDashboardSnapshots + """
              |
              | Found:
              |""".trimMargin() + _existingEnthusiastDashboardSnapshots)
        }
        val _columnsUploadTasks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUploadTasks.put("taskId", TableInfo.Column("taskId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("localPath", TableInfo.Column("localPath", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("remotePath", TableInfo.Column("remotePath", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("progress", TableInfo.Column("progress", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("retries", TableInfo.Column("retries", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("error", TableInfo.Column("error", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUploadTasks.put("contextJson", TableInfo.Column("contextJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUploadTasks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUploadTasks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUploadTasks: TableInfo = TableInfo("upload_tasks", _columnsUploadTasks,
            _foreignKeysUploadTasks, _indicesUploadTasks)
        val _existingUploadTasks: TableInfo = tableInfoRead(connection, "upload_tasks")
        if (!_infoUploadTasks.equals(_existingUploadTasks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |upload_tasks(com.rio.rostry.data.database.entity.UploadTaskEntity).
              | Expected:
              |""".trimMargin() + _infoUploadTasks + """
              |
              | Found:
              |""".trimMargin() + _existingUploadTasks)
        }
        val _columnsDailyLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDailyLogs.put("logId", TableInfo.Column("logId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("logDate", TableInfo.Column("logDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("weightGrams", TableInfo.Column("weightGrams", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("feedKg", TableInfo.Column("feedKg", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("medicationJson", TableInfo.Column("medicationJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("symptomsJson", TableInfo.Column("symptomsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("activityLevel", TableInfo.Column("activityLevel", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("photoUrls", TableInfo.Column("photoUrls", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("temperature", TableInfo.Column("temperature", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("humidity", TableInfo.Column("humidity", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("deviceTimestamp", TableInfo.Column("deviceTimestamp", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("author", TableInfo.Column("author", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("mergedAt", TableInfo.Column("mergedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("mergeCount", TableInfo.Column("mergeCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("conflictResolved", TableInfo.Column("conflictResolved", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyLogs.put("mediaItemsJson", TableInfo.Column("mediaItemsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDailyLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDailyLogs.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesDailyLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDailyLogs.add(TableInfo.Index("index_daily_logs_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesDailyLogs.add(TableInfo.Index("index_daily_logs_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesDailyLogs.add(TableInfo.Index("index_daily_logs_logDate", false, listOf("logDate"),
            listOf("ASC")))
        _indicesDailyLogs.add(TableInfo.Index("index_daily_logs_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        _indicesDailyLogs.add(TableInfo.Index("index_daily_logs_mergedAt", false,
            listOf("mergedAt"), listOf("ASC")))
        val _infoDailyLogs: TableInfo = TableInfo("daily_logs", _columnsDailyLogs,
            _foreignKeysDailyLogs, _indicesDailyLogs)
        val _existingDailyLogs: TableInfo = tableInfoRead(connection, "daily_logs")
        if (!_infoDailyLogs.equals(_existingDailyLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |daily_logs(com.rio.rostry.data.database.entity.DailyLogEntity).
              | Expected:
              |""".trimMargin() + _infoDailyLogs + """
              |
              | Found:
              |""".trimMargin() + _existingDailyLogs)
        }
        val _columnsTasks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTasks.put("taskId", TableInfo.Column("taskId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("batchId", TableInfo.Column("batchId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("taskType", TableInfo.Column("taskType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("dueAt", TableInfo.Column("dueAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("completedBy", TableInfo.Column("completedBy", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("priority", TableInfo.Column("priority", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("recurrence", TableInfo.Column("recurrence", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("snoozeUntil", TableInfo.Column("snoozeUntil", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("metadata", TableInfo.Column("metadata", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("mergedAt", TableInfo.Column("mergedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTasks.put("mergeCount", TableInfo.Column("mergeCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTasks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysTasks.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesTasks: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTasks.add(TableInfo.Index("index_tasks_farmerId", false, listOf("farmerId"),
            listOf("ASC")))
        _indicesTasks.add(TableInfo.Index("index_tasks_productId", false, listOf("productId"),
            listOf("ASC")))
        _indicesTasks.add(TableInfo.Index("index_tasks_taskType", false, listOf("taskType"),
            listOf("ASC")))
        _indicesTasks.add(TableInfo.Index("index_tasks_dueAt", false, listOf("dueAt"),
            listOf("ASC")))
        _indicesTasks.add(TableInfo.Index("index_tasks_completedAt", false, listOf("completedAt"),
            listOf("ASC")))
        _indicesTasks.add(TableInfo.Index("index_tasks_mergedAt", false, listOf("mergedAt"),
            listOf("ASC")))
        _indicesTasks.add(TableInfo.Index("index_tasks_farmerId_completedAt_dueAt", false,
            listOf("farmerId", "completedAt", "dueAt"), listOf("ASC", "ASC", "ASC")))
        val _infoTasks: TableInfo = TableInfo("tasks", _columnsTasks, _foreignKeysTasks,
            _indicesTasks)
        val _existingTasks: TableInfo = tableInfoRead(connection, "tasks")
        if (!_infoTasks.equals(_existingTasks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |tasks(com.rio.rostry.data.database.entity.TaskEntity).
              | Expected:
              |""".trimMargin() + _infoTasks + """
              |
              | Found:
              |""".trimMargin() + _existingTasks)
        }
        val _columnsBreeds: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBreeds.put("breedId", TableInfo.Column("breedId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreeds.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreeds.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreeds.put("culinaryProfile", TableInfo.Column("culinaryProfile", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreeds.put("farmingDifficulty", TableInfo.Column("farmingDifficulty", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreeds.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreeds.put("tags", TableInfo.Column("tags", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBreeds: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBreeds: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBreeds: TableInfo = TableInfo("breeds", _columnsBreeds, _foreignKeysBreeds,
            _indicesBreeds)
        val _existingBreeds: TableInfo = tableInfoRead(connection, "breeds")
        if (!_infoBreeds.equals(_existingBreeds)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |breeds(com.rio.rostry.data.database.entity.BreedEntity).
              | Expected:
              |""".trimMargin() + _infoBreeds + """
              |
              | Found:
              |""".trimMargin() + _existingBreeds)
        }
        val _columnsFarmVerifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmVerifications.put("verificationId", TableInfo.Column("verificationId", "TEXT",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmLocationLat", TableInfo.Column("farmLocationLat", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmLocationLng", TableInfo.Column("farmLocationLng", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmAddressLine1", TableInfo.Column("farmAddressLine1",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmAddressLine2", TableInfo.Column("farmAddressLine2",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmCity", TableInfo.Column("farmCity", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmState", TableInfo.Column("farmState", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmPostalCode", TableInfo.Column("farmPostalCode", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("farmCountry", TableInfo.Column("farmCountry", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("verificationDocumentUrls",
            TableInfo.Column("verificationDocumentUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("gpsAccuracy", TableInfo.Column("gpsAccuracy", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("gpsTimestamp", TableInfo.Column("gpsTimestamp", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("submittedAt", TableInfo.Column("submittedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("reviewedAt", TableInfo.Column("reviewedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("reviewedBy", TableInfo.Column("reviewedBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("rejectionReason", TableInfo.Column("rejectionReason", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmVerifications.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmVerifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysFarmVerifications.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("farmerId"), listOf("userId")))
        val _indicesFarmVerifications: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmVerifications.add(TableInfo.Index("index_farm_verifications_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmVerifications.add(TableInfo.Index("index_farm_verifications_status", false,
            listOf("status"), listOf("ASC")))
        val _infoFarmVerifications: TableInfo = TableInfo("farm_verifications",
            _columnsFarmVerifications, _foreignKeysFarmVerifications, _indicesFarmVerifications)
        val _existingFarmVerifications: TableInfo = tableInfoRead(connection, "farm_verifications")
        if (!_infoFarmVerifications.equals(_existingFarmVerifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_verifications(com.rio.rostry.data.database.entity.FarmVerificationEntity).
              | Expected:
              |""".trimMargin() + _infoFarmVerifications + """
              |
              | Found:
              |""".trimMargin() + _existingFarmVerifications)
        }
        val _columnsFarmAssets: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmAssets.put("assetId", TableInfo.Column("assetId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("assetType", TableInfo.Column("assetType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("isShowcase", TableInfo.Column("isShowcase", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("locationName", TableInfo.Column("locationName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("latitude", TableInfo.Column("latitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("longitude", TableInfo.Column("longitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("quantity", TableInfo.Column("quantity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("initialQuantity", TableInfo.Column("initialQuantity", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("unit", TableInfo.Column("unit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("birthDate", TableInfo.Column("birthDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("ageWeeks", TableInfo.Column("ageWeeks", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("breed", TableInfo.Column("breed", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("gender", TableInfo.Column("gender", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("color", TableInfo.Column("color", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("healthStatus", TableInfo.Column("healthStatus", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("raisingPurpose", TableInfo.Column("raisingPurpose", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("imageUrls", TableInfo.Column("imageUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("lifecycleSubStage", TableInfo.Column("lifecycleSubStage", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("parentIdsJson", TableInfo.Column("parentIdsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("batchId", TableInfo.Column("batchId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("origin", TableInfo.Column("origin", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("birdCode", TableInfo.Column("birdCode", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("acquisitionPrice", TableInfo.Column("acquisitionPrice", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("acquisitionDate", TableInfo.Column("acquisitionDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("acquisitionSource", TableInfo.Column("acquisitionSource", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("acquisitionSourceId", TableInfo.Column("acquisitionSourceId",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("acquisitionNotes", TableInfo.Column("acquisitionNotes", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("estimatedValue", TableInfo.Column("estimatedValue", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("lastVaccinationDate", TableInfo.Column("lastVaccinationDate",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("nextVaccinationDate", TableInfo.Column("nextVaccinationDate",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("weightGrams", TableInfo.Column("weightGrams", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("metadataJson", TableInfo.Column("metadataJson", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("listedAt", TableInfo.Column("listedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("listingId", TableInfo.Column("listingId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("soldAt", TableInfo.Column("soldAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("soldToUserId", TableInfo.Column("soldToUserId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("soldPrice", TableInfo.Column("soldPrice", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("previousOwnerId", TableInfo.Column("previousOwnerId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("transferredAt", TableInfo.Column("transferredAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmAssets.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmAssets: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysFarmAssets.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("farmerId"), listOf("userId")))
        val _indicesFarmAssets: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmAssets.add(TableInfo.Index("index_farm_assets_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmAssets.add(TableInfo.Index("index_farm_assets_assetType", false,
            listOf("assetType"), listOf("ASC")))
        _indicesFarmAssets.add(TableInfo.Index("index_farm_assets_status", false, listOf("status"),
            listOf("ASC")))
        val _infoFarmAssets: TableInfo = TableInfo("farm_assets", _columnsFarmAssets,
            _foreignKeysFarmAssets, _indicesFarmAssets)
        val _existingFarmAssets: TableInfo = tableInfoRead(connection, "farm_assets")
        if (!_infoFarmAssets.equals(_existingFarmAssets)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_assets(com.rio.rostry.data.database.entity.FarmAssetEntity).
              | Expected:
              |""".trimMargin() + _infoFarmAssets + """
              |
              | Found:
              |""".trimMargin() + _existingFarmAssets)
        }
        val _columnsFarmInventory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmInventory.put("inventoryId", TableInfo.Column("inventoryId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("sourceAssetId", TableInfo.Column("sourceAssetId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("sourceBatchId", TableInfo.Column("sourceBatchId", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("sku", TableInfo.Column("sku", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("quantityAvailable", TableInfo.Column("quantityAvailable", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("quantityReserved", TableInfo.Column("quantityReserved", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("unit", TableInfo.Column("unit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("producedAt", TableInfo.Column("producedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("qualityGrade", TableInfo.Column("qualityGrade", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmInventory.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmInventory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysFarmInventory.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("farmerId"), listOf("userId")))
        _foreignKeysFarmInventory.add(TableInfo.ForeignKey("farm_assets", "SET NULL", "NO ACTION",
            listOf("sourceAssetId"), listOf("assetId")))
        val _indicesFarmInventory: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmInventory.add(TableInfo.Index("index_farm_inventory_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmInventory.add(TableInfo.Index("index_farm_inventory_sourceAssetId", false,
            listOf("sourceAssetId"), listOf("ASC")))
        _indicesFarmInventory.add(TableInfo.Index("index_farm_inventory_sku", false, listOf("sku"),
            listOf("ASC")))
        val _infoFarmInventory: TableInfo = TableInfo("farm_inventory", _columnsFarmInventory,
            _foreignKeysFarmInventory, _indicesFarmInventory)
        val _existingFarmInventory: TableInfo = tableInfoRead(connection, "farm_inventory")
        if (!_infoFarmInventory.equals(_existingFarmInventory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_inventory(com.rio.rostry.data.database.entity.InventoryItemEntity).
              | Expected:
              |""".trimMargin() + _infoFarmInventory + """
              |
              | Found:
              |""".trimMargin() + _existingFarmInventory)
        }
        val _columnsMarketListings: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMarketListings.put("listingId", TableInfo.Column("listingId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("inventoryId", TableInfo.Column("inventoryId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("price", TableInfo.Column("price", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("priceUnit", TableInfo.Column("priceUnit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("tags", TableInfo.Column("tags", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("deliveryOptions", TableInfo.Column("deliveryOptions", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("deliveryCost", TableInfo.Column("deliveryCost", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("locationName", TableInfo.Column("locationName", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("latitude", TableInfo.Column("latitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("longitude", TableInfo.Column("longitude", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("minOrderQuantity", TableInfo.Column("minOrderQuantity", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("maxOrderQuantity", TableInfo.Column("maxOrderQuantity", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("imageUrls", TableInfo.Column("imageUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("isActive", TableInfo.Column("isActive", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("viewsCount", TableInfo.Column("viewsCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("inquiriesCount", TableInfo.Column("inquiriesCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("leadTimeDays", TableInfo.Column("leadTimeDays", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMarketListings.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMarketListings: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysMarketListings.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("sellerId"), listOf("userId")))
        _foreignKeysMarketListings.add(TableInfo.ForeignKey("farm_inventory", "CASCADE",
            "NO ACTION", listOf("inventoryId"), listOf("inventoryId")))
        val _indicesMarketListings: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMarketListings.add(TableInfo.Index("index_market_listings_sellerId", false,
            listOf("sellerId"), listOf("ASC")))
        _indicesMarketListings.add(TableInfo.Index("index_market_listings_inventoryId", false,
            listOf("inventoryId"), listOf("ASC")))
        _indicesMarketListings.add(TableInfo.Index("index_market_listings_status", false,
            listOf("status"), listOf("ASC")))
        _indicesMarketListings.add(TableInfo.Index("index_market_listings_category", false,
            listOf("category"), listOf("ASC")))
        val _infoMarketListings: TableInfo = TableInfo("market_listings", _columnsMarketListings,
            _foreignKeysMarketListings, _indicesMarketListings)
        val _existingMarketListings: TableInfo = tableInfoRead(connection, "market_listings")
        if (!_infoMarketListings.equals(_existingMarketListings)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |market_listings(com.rio.rostry.data.database.entity.MarketListingEntity).
              | Expected:
              |""".trimMargin() + _infoMarketListings + """
              |
              | Found:
              |""".trimMargin() + _existingMarketListings)
        }
        val _columnsReviews: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReviews.put("reviewId", TableInfo.Column("reviewId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("orderId", TableInfo.Column("orderId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("reviewerId", TableInfo.Column("reviewerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("rating", TableInfo.Column("rating", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("title", TableInfo.Column("title", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("content", TableInfo.Column("content", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("isVerifiedPurchase", TableInfo.Column("isVerifiedPurchase", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("helpfulCount", TableInfo.Column("helpfulCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("responseFromSeller", TableInfo.Column("responseFromSeller", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("responseAt", TableInfo.Column("responseAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("adminFlagged", TableInfo.Column("adminFlagged", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsReviews.put("moderationNote", TableInfo.Column("moderationNote", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReviews: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReviews: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesReviews.add(TableInfo.Index("index_reviews_productId", false, listOf("productId"),
            listOf("ASC")))
        _indicesReviews.add(TableInfo.Index("index_reviews_sellerId", false, listOf("sellerId"),
            listOf("ASC")))
        _indicesReviews.add(TableInfo.Index("index_reviews_reviewerId", false, listOf("reviewerId"),
            listOf("ASC")))
        _indicesReviews.add(TableInfo.Index("index_reviews_createdAt", false, listOf("createdAt"),
            listOf("ASC")))
        val _infoReviews: TableInfo = TableInfo("reviews", _columnsReviews, _foreignKeysReviews,
            _indicesReviews)
        val _existingReviews: TableInfo = tableInfoRead(connection, "reviews")
        if (!_infoReviews.equals(_existingReviews)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |reviews(com.rio.rostry.data.database.entity.ReviewEntity).
              | Expected:
              |""".trimMargin() + _infoReviews + """
              |
              | Found:
              |""".trimMargin() + _existingReviews)
        }
        val _columnsReviewHelpful: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsReviewHelpful.put("reviewId", TableInfo.Column("reviewId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviewHelpful.put("userId", TableInfo.Column("userId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsReviewHelpful.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysReviewHelpful: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesReviewHelpful: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesReviewHelpful.add(TableInfo.Index("index_review_helpful_reviewId", false,
            listOf("reviewId"), listOf("ASC")))
        val _infoReviewHelpful: TableInfo = TableInfo("review_helpful", _columnsReviewHelpful,
            _foreignKeysReviewHelpful, _indicesReviewHelpful)
        val _existingReviewHelpful: TableInfo = tableInfoRead(connection, "review_helpful")
        if (!_infoReviewHelpful.equals(_existingReviewHelpful)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |review_helpful(com.rio.rostry.data.database.entity.ReviewHelpfulEntity).
              | Expected:
              |""".trimMargin() + _infoReviewHelpful + """
              |
              | Found:
              |""".trimMargin() + _existingReviewHelpful)
        }
        val _columnsRatingStats: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRatingStats.put("statsId", TableInfo.Column("statsId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("sellerId", TableInfo.Column("sellerId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("averageRating", TableInfo.Column("averageRating", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("totalReviews", TableInfo.Column("totalReviews", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("rating5Count", TableInfo.Column("rating5Count", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("rating4Count", TableInfo.Column("rating4Count", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("rating3Count", TableInfo.Column("rating3Count", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("rating2Count", TableInfo.Column("rating2Count", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("rating1Count", TableInfo.Column("rating1Count", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("verifiedPurchaseCount", TableInfo.Column("verifiedPurchaseCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRatingStats.put("lastUpdated", TableInfo.Column("lastUpdated", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRatingStats: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRatingStats: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesRatingStats.add(TableInfo.Index("index_rating_stats_sellerId", false,
            listOf("sellerId"), listOf("ASC")))
        _indicesRatingStats.add(TableInfo.Index("index_rating_stats_productId", false,
            listOf("productId"), listOf("ASC")))
        val _infoRatingStats: TableInfo = TableInfo("rating_stats", _columnsRatingStats,
            _foreignKeysRatingStats, _indicesRatingStats)
        val _existingRatingStats: TableInfo = tableInfoRead(connection, "rating_stats")
        if (!_infoRatingStats.equals(_existingRatingStats)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |rating_stats(com.rio.rostry.data.database.entity.RatingStatsEntity).
              | Expected:
              |""".trimMargin() + _infoRatingStats + """
              |
              | Found:
              |""".trimMargin() + _existingRatingStats)
        }
        val _columnsOrderEvidence: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderEvidence.put("evidenceId", TableInfo.Column("evidenceId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("evidenceType", TableInfo.Column("evidenceType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("uploadedBy", TableInfo.Column("uploadedBy", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("uploadedByRole", TableInfo.Column("uploadedByRole", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("imageUri", TableInfo.Column("imageUri", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("videoUri", TableInfo.Column("videoUri", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("textContent", TableInfo.Column("textContent", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("geoLatitude", TableInfo.Column("geoLatitude", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("geoLongitude", TableInfo.Column("geoLongitude", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("geoAddress", TableInfo.Column("geoAddress", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("isVerified", TableInfo.Column("isVerified", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("verifiedBy", TableInfo.Column("verifiedBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("verifiedAt", TableInfo.Column("verifiedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("verificationNote", TableInfo.Column("verificationNote", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("deviceTimestamp", TableInfo.Column("deviceTimestamp", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderEvidence.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderEvidence: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOrderEvidence: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderEvidence.add(TableInfo.Index("index_order_evidence_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesOrderEvidence.add(TableInfo.Index("index_order_evidence_uploadedBy", false,
            listOf("uploadedBy"), listOf("ASC")))
        _indicesOrderEvidence.add(TableInfo.Index("index_order_evidence_evidenceType", false,
            listOf("evidenceType"), listOf("ASC")))
        _indicesOrderEvidence.add(TableInfo.Index("index_order_evidence_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        val _infoOrderEvidence: TableInfo = TableInfo("order_evidence", _columnsOrderEvidence,
            _foreignKeysOrderEvidence, _indicesOrderEvidence)
        val _existingOrderEvidence: TableInfo = tableInfoRead(connection, "order_evidence")
        if (!_infoOrderEvidence.equals(_existingOrderEvidence)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_evidence(com.rio.rostry.data.database.entity.OrderEvidenceEntity).
              | Expected:
              |""".trimMargin() + _infoOrderEvidence + """
              |
              | Found:
              |""".trimMargin() + _existingOrderEvidence)
        }
        val _columnsOrderQuotes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderQuotes.put("quoteId", TableInfo.Column("quoteId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("buyerId", TableInfo.Column("buyerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("productName", TableInfo.Column("productName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("quantity", TableInfo.Column("quantity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("unit", TableInfo.Column("unit", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("basePrice", TableInfo.Column("basePrice", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("totalProductPrice", TableInfo.Column("totalProductPrice", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("deliveryCharge", TableInfo.Column("deliveryCharge", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("packingCharge", TableInfo.Column("packingCharge", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("platformFee", TableInfo.Column("platformFee", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("discount", TableInfo.Column("discount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("finalTotal", TableInfo.Column("finalTotal", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("deliveryType", TableInfo.Column("deliveryType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("deliveryDistance", TableInfo.Column("deliveryDistance", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("deliveryAddress", TableInfo.Column("deliveryAddress", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("deliveryLatitude", TableInfo.Column("deliveryLatitude", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("deliveryLongitude", TableInfo.Column("deliveryLongitude", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("pickupAddress", TableInfo.Column("pickupAddress", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("pickupLatitude", TableInfo.Column("pickupLatitude", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("pickupLongitude", TableInfo.Column("pickupLongitude", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("paymentType", TableInfo.Column("paymentType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("advanceAmount", TableInfo.Column("advanceAmount", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("balanceAmount", TableInfo.Column("balanceAmount", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("buyerAgreedAt", TableInfo.Column("buyerAgreedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("sellerAgreedAt", TableInfo.Column("sellerAgreedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("lockedAt", TableInfo.Column("lockedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("expiresAt", TableInfo.Column("expiresAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("version", TableInfo.Column("version", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("previousQuoteId", TableInfo.Column("previousQuoteId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("buyerNotes", TableInfo.Column("buyerNotes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("sellerNotes", TableInfo.Column("sellerNotes", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderQuotes.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderQuotes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOrderQuotes: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderQuotes.add(TableInfo.Index("index_order_quotes_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesOrderQuotes.add(TableInfo.Index("index_order_quotes_buyerId", false,
            listOf("buyerId"), listOf("ASC")))
        _indicesOrderQuotes.add(TableInfo.Index("index_order_quotes_sellerId", false,
            listOf("sellerId"), listOf("ASC")))
        _indicesOrderQuotes.add(TableInfo.Index("index_order_quotes_status", false,
            listOf("status"), listOf("ASC")))
        val _infoOrderQuotes: TableInfo = TableInfo("order_quotes", _columnsOrderQuotes,
            _foreignKeysOrderQuotes, _indicesOrderQuotes)
        val _existingOrderQuotes: TableInfo = tableInfoRead(connection, "order_quotes")
        if (!_infoOrderQuotes.equals(_existingOrderQuotes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_quotes(com.rio.rostry.data.database.entity.OrderQuoteEntity).
              | Expected:
              |""".trimMargin() + _infoOrderQuotes + """
              |
              | Found:
              |""".trimMargin() + _existingOrderQuotes)
        }
        val _columnsOrderPayments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderPayments.put("paymentId", TableInfo.Column("paymentId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("quoteId", TableInfo.Column("quoteId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("payerId", TableInfo.Column("payerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("receiverId", TableInfo.Column("receiverId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("paymentPhase", TableInfo.Column("paymentPhase", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("method", TableInfo.Column("method", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("upiId", TableInfo.Column("upiId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("bankDetails", TableInfo.Column("bankDetails", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("proofEvidenceId", TableInfo.Column("proofEvidenceId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("transactionRef", TableInfo.Column("transactionRef", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("verifiedAt", TableInfo.Column("verifiedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("verifiedBy", TableInfo.Column("verifiedBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("rejectionReason", TableInfo.Column("rejectionReason", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("refundedAmount", TableInfo.Column("refundedAmount", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("refundedAt", TableInfo.Column("refundedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("refundReason", TableInfo.Column("refundReason", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("dueAt", TableInfo.Column("dueAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("expiredAt", TableInfo.Column("expiredAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderPayments.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderPayments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOrderPayments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderPayments.add(TableInfo.Index("index_order_payments_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesOrderPayments.add(TableInfo.Index("index_order_payments_payerId", false,
            listOf("payerId"), listOf("ASC")))
        _indicesOrderPayments.add(TableInfo.Index("index_order_payments_paymentPhase", false,
            listOf("paymentPhase"), listOf("ASC")))
        _indicesOrderPayments.add(TableInfo.Index("index_order_payments_status", false,
            listOf("status"), listOf("ASC")))
        val _infoOrderPayments: TableInfo = TableInfo("order_payments", _columnsOrderPayments,
            _foreignKeysOrderPayments, _indicesOrderPayments)
        val _existingOrderPayments: TableInfo = tableInfoRead(connection, "order_payments")
        if (!_infoOrderPayments.equals(_existingOrderPayments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_payments(com.rio.rostry.data.database.entity.OrderPaymentEntity).
              | Expected:
              |""".trimMargin() + _infoOrderPayments + """
              |
              | Found:
              |""".trimMargin() + _existingOrderPayments)
        }
        val _columnsDeliveryConfirmations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDeliveryConfirmations.put("confirmationId", TableInfo.Column("confirmationId",
            "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("buyerId", TableInfo.Column("buyerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("sellerId", TableInfo.Column("sellerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("deliveryOtp", TableInfo.Column("deliveryOtp", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("otpGeneratedAt", TableInfo.Column("otpGeneratedAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("otpExpiresAt", TableInfo.Column("otpExpiresAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("otpAttempts", TableInfo.Column("otpAttempts", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("maxOtpAttempts", TableInfo.Column("maxOtpAttempts",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("status", TableInfo.Column("status", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("confirmationMethod",
            TableInfo.Column("confirmationMethod", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("deliveryPhotoEvidenceId",
            TableInfo.Column("deliveryPhotoEvidenceId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("buyerConfirmationEvidenceId",
            TableInfo.Column("buyerConfirmationEvidenceId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("gpsEvidenceId", TableInfo.Column("gpsEvidenceId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("confirmedAt", TableInfo.Column("confirmedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("confirmedBy", TableInfo.Column("confirmedBy", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("deliveryNotes", TableInfo.Column("deliveryNotes", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("balanceCollected", TableInfo.Column("balanceCollected",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("balanceCollectedAt",
            TableInfo.Column("balanceCollectedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("balanceEvidenceId", TableInfo.Column("balanceEvidenceId",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("createdAt", TableInfo.Column("createdAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDeliveryConfirmations.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDeliveryConfirmations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDeliveryConfirmations: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDeliveryConfirmations.add(TableInfo.Index("index_delivery_confirmations_orderId",
            false, listOf("orderId"), listOf("ASC")))
        _indicesDeliveryConfirmations.add(TableInfo.Index("index_delivery_confirmations_status",
            false, listOf("status"), listOf("ASC")))
        val _infoDeliveryConfirmations: TableInfo = TableInfo("delivery_confirmations",
            _columnsDeliveryConfirmations, _foreignKeysDeliveryConfirmations,
            _indicesDeliveryConfirmations)
        val _existingDeliveryConfirmations: TableInfo = tableInfoRead(connection,
            "delivery_confirmations")
        if (!_infoDeliveryConfirmations.equals(_existingDeliveryConfirmations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |delivery_confirmations(com.rio.rostry.data.database.entity.DeliveryConfirmationEntity).
              | Expected:
              |""".trimMargin() + _infoDeliveryConfirmations + """
              |
              | Found:
              |""".trimMargin() + _existingDeliveryConfirmations)
        }
        val _columnsOrderDisputes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderDisputes.put("disputeId", TableInfo.Column("disputeId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("raisedBy", TableInfo.Column("raisedBy", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("raisedByRole", TableInfo.Column("raisedByRole", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("againstUserId", TableInfo.Column("againstUserId", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("reason", TableInfo.Column("reason", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("requestedResolution", TableInfo.Column("requestedResolution",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("claimedAmount", TableInfo.Column("claimedAmount", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("evidenceIds", TableInfo.Column("evidenceIds", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("resolvedAt", TableInfo.Column("resolvedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("resolvedBy", TableInfo.Column("resolvedBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("resolutionType", TableInfo.Column("resolutionType", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("resolutionNotes", TableInfo.Column("resolutionNotes", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("refundedAmount", TableInfo.Column("refundedAmount", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("lastResponseAt", TableInfo.Column("lastResponseAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("responseCount", TableInfo.Column("responseCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("escalatedAt", TableInfo.Column("escalatedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("escalationReason", TableInfo.Column("escalationReason", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("adminNotes", TableInfo.Column("adminNotes", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderDisputes.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderDisputes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOrderDisputes: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderDisputes.add(TableInfo.Index("index_order_disputes_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesOrderDisputes.add(TableInfo.Index("index_order_disputes_raisedBy", false,
            listOf("raisedBy"), listOf("ASC")))
        _indicesOrderDisputes.add(TableInfo.Index("index_order_disputes_status", false,
            listOf("status"), listOf("ASC")))
        val _infoOrderDisputes: TableInfo = TableInfo("order_disputes", _columnsOrderDisputes,
            _foreignKeysOrderDisputes, _indicesOrderDisputes)
        val _existingOrderDisputes: TableInfo = tableInfoRead(connection, "order_disputes")
        if (!_infoOrderDisputes.equals(_existingOrderDisputes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_disputes(com.rio.rostry.data.database.entity.OrderDisputeEntity).
              | Expected:
              |""".trimMargin() + _infoOrderDisputes + """
              |
              | Found:
              |""".trimMargin() + _existingOrderDisputes)
        }
        val _columnsOrderAuditLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsOrderAuditLogs.put("logId", TableInfo.Column("logId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("action", TableInfo.Column("action", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("fromState", TableInfo.Column("fromState", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("toState", TableInfo.Column("toState", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("performedBy", TableInfo.Column("performedBy", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("performedByRole", TableInfo.Column("performedByRole", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("metadata", TableInfo.Column("metadata", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("evidenceId", TableInfo.Column("evidenceId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("ipAddress", TableInfo.Column("ipAddress", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("deviceInfo", TableInfo.Column("deviceInfo", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsOrderAuditLogs.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysOrderAuditLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesOrderAuditLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesOrderAuditLogs.add(TableInfo.Index("index_order_audit_logs_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesOrderAuditLogs.add(TableInfo.Index("index_order_audit_logs_performedBy", false,
            listOf("performedBy"), listOf("ASC")))
        _indicesOrderAuditLogs.add(TableInfo.Index("index_order_audit_logs_timestamp", false,
            listOf("timestamp"), listOf("ASC")))
        val _infoOrderAuditLogs: TableInfo = TableInfo("order_audit_logs", _columnsOrderAuditLogs,
            _foreignKeysOrderAuditLogs, _indicesOrderAuditLogs)
        val _existingOrderAuditLogs: TableInfo = tableInfoRead(connection, "order_audit_logs")
        if (!_infoOrderAuditLogs.equals(_existingOrderAuditLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |order_audit_logs(com.rio.rostry.data.database.entity.OrderAuditLogEntity).
              | Expected:
              |""".trimMargin() + _infoOrderAuditLogs + """
              |
              | Found:
              |""".trimMargin() + _existingOrderAuditLogs)
        }
        val _columnsVerificationDrafts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsVerificationDrafts.put("draftId", TableInfo.Column("draftId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("upgradeType", TableInfo.Column("upgradeType", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("farmLocationJson", TableInfo.Column("farmLocationJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("uploadedImagesJson", TableInfo.Column("uploadedImagesJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("uploadedDocsJson", TableInfo.Column("uploadedDocsJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("uploadedImageTypesJson",
            TableInfo.Column("uploadedImageTypesJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("uploadedDocTypesJson",
            TableInfo.Column("uploadedDocTypesJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("uploadProgressJson", TableInfo.Column("uploadProgressJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("lastSavedAt", TableInfo.Column("lastSavedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("mergedAt", TableInfo.Column("mergedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationDrafts.put("mergedInto", TableInfo.Column("mergedInto", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVerificationDrafts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesVerificationDrafts: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesVerificationDrafts.add(TableInfo.Index("index_verification_drafts_userId", true,
            listOf("userId"), listOf("ASC")))
        val _infoVerificationDrafts: TableInfo = TableInfo("verification_drafts",
            _columnsVerificationDrafts, _foreignKeysVerificationDrafts, _indicesVerificationDrafts)
        val _existingVerificationDrafts: TableInfo = tableInfoRead(connection,
            "verification_drafts")
        if (!_infoVerificationDrafts.equals(_existingVerificationDrafts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |verification_drafts(com.rio.rostry.data.database.entity.VerificationDraftEntity).
              | Expected:
              |""".trimMargin() + _infoVerificationDrafts + """
              |
              | Found:
              |""".trimMargin() + _existingVerificationDrafts)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }

      private fun onValidateSchema3(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsRoleMigrations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRoleMigrations.put("migrationId", TableInfo.Column("migrationId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("fromRole", TableInfo.Column("fromRole", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("toRole", TableInfo.Column("toRole", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("totalItems", TableInfo.Column("totalItems", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("migratedItems", TableInfo.Column("migratedItems", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("currentPhase", TableInfo.Column("currentPhase", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("currentEntity", TableInfo.Column("currentEntity", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("startedAt", TableInfo.Column("startedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("pausedAt", TableInfo.Column("pausedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("lastProgressAt", TableInfo.Column("lastProgressAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("errorMessage", TableInfo.Column("errorMessage", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("retryCount", TableInfo.Column("retryCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("maxRetries", TableInfo.Column("maxRetries", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("snapshotPath", TableInfo.Column("snapshotPath", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("metadataJson", TableInfo.Column("metadataJson", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleMigrations.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRoleMigrations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRoleMigrations: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesRoleMigrations.add(TableInfo.Index("index_role_migrations_userId", false,
            listOf("userId"), listOf("ASC")))
        _indicesRoleMigrations.add(TableInfo.Index("index_role_migrations_status", false,
            listOf("status"), listOf("ASC")))
        _indicesRoleMigrations.add(TableInfo.Index("index_role_migrations_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        val _infoRoleMigrations: TableInfo = TableInfo("role_migrations", _columnsRoleMigrations,
            _foreignKeysRoleMigrations, _indicesRoleMigrations)
        val _existingRoleMigrations: TableInfo = tableInfoRead(connection, "role_migrations")
        if (!_infoRoleMigrations.equals(_existingRoleMigrations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |role_migrations(com.rio.rostry.data.database.entity.RoleMigrationEntity).
              | Expected:
              |""".trimMargin() + _infoRoleMigrations + """
              |
              | Found:
              |""".trimMargin() + _existingRoleMigrations)
        }
        val _columnsStorageQuota: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStorageQuota.put("userId", TableInfo.Column("userId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("quotaBytes", TableInfo.Column("quotaBytes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("publicLimitBytes", TableInfo.Column("publicLimitBytes", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("privateLimitBytes", TableInfo.Column("privateLimitBytes",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("usedBytes", TableInfo.Column("usedBytes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("publicUsedBytes", TableInfo.Column("publicUsedBytes", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("privateUsedBytes", TableInfo.Column("privateUsedBytes", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("imageBytes", TableInfo.Column("imageBytes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("documentBytes", TableInfo.Column("documentBytes", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("dataBytes", TableInfo.Column("dataBytes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("warningLevel", TableInfo.Column("warningLevel", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("lastCalculatedAt", TableInfo.Column("lastCalculatedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("lastSyncedAt", TableInfo.Column("lastSyncedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStorageQuota.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStorageQuota: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStorageQuota: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesStorageQuota.add(TableInfo.Index("index_storage_quota_lastCalculatedAt", false,
            listOf("lastCalculatedAt"), listOf("ASC")))
        val _infoStorageQuota: TableInfo = TableInfo("storage_quota", _columnsStorageQuota,
            _foreignKeysStorageQuota, _indicesStorageQuota)
        val _existingStorageQuota: TableInfo = tableInfoRead(connection, "storage_quota")
        if (!_infoStorageQuota.equals(_existingStorageQuota)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |storage_quota(com.rio.rostry.data.database.entity.StorageQuotaEntity).
              | Expected:
              |""".trimMargin() + _infoStorageQuota + """
              |
              | Found:
              |""".trimMargin() + _existingStorageQuota)
        }
        val _columnsDailyBirdLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDailyBirdLogs.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("birdId", TableInfo.Column("birdId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("activityType", TableInfo.Column("activityType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("weight", TableInfo.Column("weight", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("feedIntakeGrams", TableInfo.Column("feedIntakeGrams", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("performanceRating", TableInfo.Column("performanceRating",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("performanceScoreJson", TableInfo.Column("performanceScoreJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("mediaUrlsJson", TableInfo.Column("mediaUrlsJson", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyBirdLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDailyBirdLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDailyBirdLogs.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("birdId"), listOf("productId")))
        val _indicesDailyBirdLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDailyBirdLogs.add(TableInfo.Index("index_daily_bird_logs_birdId", false,
            listOf("birdId"), listOf("ASC")))
        _indicesDailyBirdLogs.add(TableInfo.Index("index_daily_bird_logs_date", false,
            listOf("date"), listOf("ASC")))
        val _infoDailyBirdLogs: TableInfo = TableInfo("daily_bird_logs", _columnsDailyBirdLogs,
            _foreignKeysDailyBirdLogs, _indicesDailyBirdLogs)
        val _existingDailyBirdLogs: TableInfo = tableInfoRead(connection, "daily_bird_logs")
        if (!_infoDailyBirdLogs.equals(_existingDailyBirdLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |daily_bird_logs(com.rio.rostry.data.database.entity.DailyBirdLogEntity).
              | Expected:
              |""".trimMargin() + _infoDailyBirdLogs + """
              |
              | Found:
              |""".trimMargin() + _existingDailyBirdLogs)
        }
        val _columnsCompetitions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCompetitions.put("competitionId", TableInfo.Column("competitionId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("startTime", TableInfo.Column("startTime", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("endTime", TableInfo.Column("endTime", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("region", TableInfo.Column("region", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("bannerUrl", TableInfo.Column("bannerUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("entryFee", TableInfo.Column("entryFee", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("prizePool", TableInfo.Column("prizePool", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("participantCount", TableInfo.Column("participantCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("participantsPreviewJson",
            TableInfo.Column("participantsPreviewJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("rulesJson", TableInfo.Column("rulesJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("bracketsJson", TableInfo.Column("bracketsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("leaderboardJson", TableInfo.Column("leaderboardJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("galleryUrlsJson", TableInfo.Column("galleryUrlsJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCompetitions.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCompetitions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCompetitions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCompetitions: TableInfo = TableInfo("competitions", _columnsCompetitions,
            _foreignKeysCompetitions, _indicesCompetitions)
        val _existingCompetitions: TableInfo = tableInfoRead(connection, "competitions")
        if (!_infoCompetitions.equals(_existingCompetitions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |competitions(com.rio.rostry.data.database.entity.CompetitionEntryEntity).
              | Expected:
              |""".trimMargin() + _infoCompetitions + """
              |
              | Found:
              |""".trimMargin() + _existingCompetitions)
        }
        val _columnsMyVotes: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMyVotes.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyVotes.put("competitionId", TableInfo.Column("competitionId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyVotes.put("participantId", TableInfo.Column("participantId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMyVotes.put("votedAt", TableInfo.Column("votedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyVotes.put("points", TableInfo.Column("points", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyVotes.put("synced", TableInfo.Column("synced", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMyVotes.put("syncError", TableInfo.Column("syncError", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMyVotes: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMyVotes: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMyVotes.add(TableInfo.Index("index_my_votes_competitionId_participantId", true,
            listOf("competitionId", "participantId"), listOf("ASC", "ASC")))
        _indicesMyVotes.add(TableInfo.Index("index_my_votes_synced", false, listOf("synced"),
            listOf("ASC")))
        val _infoMyVotes: TableInfo = TableInfo("my_votes", _columnsMyVotes, _foreignKeysMyVotes,
            _indicesMyVotes)
        val _existingMyVotes: TableInfo = tableInfoRead(connection, "my_votes")
        if (!_infoMyVotes.equals(_existingMyVotes)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |my_votes(com.rio.rostry.data.database.entity.MyVotesEntity).
              | Expected:
              |""".trimMargin() + _infoMyVotes + """
              |
              | Found:
              |""".trimMargin() + _existingMyVotes)
        }
        val _columnsBatchSummaries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBatchSummaries.put("batchId", TableInfo.Column("batchId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("batchName", TableInfo.Column("batchName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("currentCount", TableInfo.Column("currentCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("avgWeightGrams", TableInfo.Column("avgWeightGrams", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("totalFeedKg", TableInfo.Column("totalFeedKg", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("fcr", TableInfo.Column("fcr", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("ageWeeks", TableInfo.Column("ageWeeks", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("hatchDate", TableInfo.Column("hatchDate", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatchSummaries.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBatchSummaries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBatchSummaries: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBatchSummaries.add(TableInfo.Index("index_batch_summaries_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesBatchSummaries.add(TableInfo.Index("index_batch_summaries_updatedAt", false,
            listOf("updatedAt"), listOf("ASC")))
        _indicesBatchSummaries.add(TableInfo.Index("index_batch_summaries_dirty", false,
            listOf("dirty"), listOf("ASC")))
        val _infoBatchSummaries: TableInfo = TableInfo("batch_summaries", _columnsBatchSummaries,
            _foreignKeysBatchSummaries, _indicesBatchSummaries)
        val _existingBatchSummaries: TableInfo = tableInfoRead(connection, "batch_summaries")
        if (!_infoBatchSummaries.equals(_existingBatchSummaries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |batch_summaries(com.rio.rostry.data.database.entity.BatchSummaryEntity).
              | Expected:
              |""".trimMargin() + _infoBatchSummaries + """
              |
              | Found:
              |""".trimMargin() + _existingBatchSummaries)
        }
        val _columnsDashboardCache: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDashboardCache.put("cacheId", TableInfo.Column("cacheId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("totalBirds", TableInfo.Column("totalBirds", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("totalBatches", TableInfo.Column("totalBatches", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("pendingVaccines", TableInfo.Column("pendingVaccines", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("overdueVaccines", TableInfo.Column("overdueVaccines", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("avgFcr", TableInfo.Column("avgFcr", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("totalFeedKgThisMonth", TableInfo.Column("totalFeedKgThisMonth",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("totalMortalityThisMonth",
            TableInfo.Column("totalMortalityThisMonth", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("estimatedHarvestDate", TableInfo.Column("estimatedHarvestDate",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("daysUntilHarvest", TableInfo.Column("daysUntilHarvest",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("healthyCount", TableInfo.Column("healthyCount", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("quarantinedCount", TableInfo.Column("quarantinedCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("alertCount", TableInfo.Column("alertCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("computedAt", TableInfo.Column("computedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDashboardCache.put("computationDurationMs",
            TableInfo.Column("computationDurationMs", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDashboardCache: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDashboardCache: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDashboardCache.add(TableInfo.Index("index_dashboard_cache_farmerId", true,
            listOf("farmerId"), listOf("ASC")))
        val _infoDashboardCache: TableInfo = TableInfo("dashboard_cache", _columnsDashboardCache,
            _foreignKeysDashboardCache, _indicesDashboardCache)
        val _existingDashboardCache: TableInfo = tableInfoRead(connection, "dashboard_cache")
        if (!_infoDashboardCache.equals(_existingDashboardCache)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |dashboard_cache(com.rio.rostry.data.database.entity.DashboardCacheEntity).
              | Expected:
              |""".trimMargin() + _infoDashboardCache + """
              |
              | Found:
              |""".trimMargin() + _existingDashboardCache)
        }
        val _columnsShowRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsShowRecords.put("recordId", TableInfo.Column("recordId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("productId", TableInfo.Column("productId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("recordType", TableInfo.Column("recordType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("eventName", TableInfo.Column("eventName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("eventLocation", TableInfo.Column("eventLocation", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("eventDate", TableInfo.Column("eventDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("result", TableInfo.Column("result", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("placement", TableInfo.Column("placement", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("totalParticipants", TableInfo.Column("totalParticipants",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("category", TableInfo.Column("category", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("score", TableInfo.Column("score", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("opponentName", TableInfo.Column("opponentName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("opponentOwnerName", TableInfo.Column("opponentOwnerName", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("judgesNotes", TableInfo.Column("judgesNotes", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("awards", TableInfo.Column("awards", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("photoUrls", TableInfo.Column("photoUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("isVerified", TableInfo.Column("isVerified", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("verifiedBy", TableInfo.Column("verifiedBy", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("certificateUrl", TableInfo.Column("certificateUrl", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsShowRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysShowRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysShowRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesShowRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesShowRecords.add(TableInfo.Index("index_show_records_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesShowRecords.add(TableInfo.Index("index_show_records_eventDate", false,
            listOf("eventDate"), listOf("ASC")))
        _indicesShowRecords.add(TableInfo.Index("index_show_records_recordType", false,
            listOf("recordType"), listOf("ASC")))
        val _infoShowRecords: TableInfo = TableInfo("show_records", _columnsShowRecords,
            _foreignKeysShowRecords, _indicesShowRecords)
        val _existingShowRecords: TableInfo = tableInfoRead(connection, "show_records")
        if (!_infoShowRecords.equals(_existingShowRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |show_records(com.rio.rostry.data.database.entity.ShowRecordEntity).
              | Expected:
              |""".trimMargin() + _infoShowRecords + """
              |
              | Found:
              |""".trimMargin() + _existingShowRecords)
        }
        val _columnsVerificationRequests: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsVerificationRequests.put("requestId", TableInfo.Column("requestId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("govtIdUrl", TableInfo.Column("govtIdUrl", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("farmPhotoUrl", TableInfo.Column("farmPhotoUrl", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("rejectionReason", TableInfo.Column("rejectionReason",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("submittedAt", TableInfo.Column("submittedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("reviewedAt", TableInfo.Column("reviewedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsVerificationRequests.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysVerificationRequests: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesVerificationRequests: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesVerificationRequests.add(TableInfo.Index("index_verification_requests_userId",
            false, listOf("userId"), listOf("ASC")))
        _indicesVerificationRequests.add(TableInfo.Index("index_verification_requests_status",
            false, listOf("status"), listOf("ASC")))
        val _infoVerificationRequests: TableInfo = TableInfo("verification_requests",
            _columnsVerificationRequests, _foreignKeysVerificationRequests,
            _indicesVerificationRequests)
        val _existingVerificationRequests: TableInfo = tableInfoRead(connection,
            "verification_requests")
        if (!_infoVerificationRequests.equals(_existingVerificationRequests)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |verification_requests(com.rio.rostry.data.database.entity.VerificationRequestEntity).
              | Expected:
              |""".trimMargin() + _infoVerificationRequests + """
              |
              | Found:
              |""".trimMargin() + _existingVerificationRequests)
        }
        val _columnsFarmActivityLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmActivityLogs.put("activityId", TableInfo.Column("activityId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("productId", TableInfo.Column("productId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("activityType", TableInfo.Column("activityType", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("amountInr", TableInfo.Column("amountInr", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("quantity", TableInfo.Column("quantity", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("category", TableInfo.Column("category", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("description", TableInfo.Column("description", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("photoUrls", TableInfo.Column("photoUrls", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("mediaItemsJson", TableInfo.Column("mediaItemsJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmActivityLogs.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmActivityLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFarmActivityLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmActivityLogs.add(TableInfo.Index("index_farm_activity_logs_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmActivityLogs.add(TableInfo.Index("index_farm_activity_logs_activityType", false,
            listOf("activityType"), listOf("ASC")))
        _indicesFarmActivityLogs.add(TableInfo.Index("index_farm_activity_logs_createdAt", false,
            listOf("createdAt"), listOf("ASC")))
        _indicesFarmActivityLogs.add(TableInfo.Index("index_farm_activity_logs_productId", false,
            listOf("productId"), listOf("ASC")))
        val _infoFarmActivityLogs: TableInfo = TableInfo("farm_activity_logs",
            _columnsFarmActivityLogs, _foreignKeysFarmActivityLogs, _indicesFarmActivityLogs)
        val _existingFarmActivityLogs: TableInfo = tableInfoRead(connection, "farm_activity_logs")
        if (!_infoFarmActivityLogs.equals(_existingFarmActivityLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_activity_logs(com.rio.rostry.data.database.entity.FarmActivityLogEntity).
              | Expected:
              |""".trimMargin() + _infoFarmActivityLogs + """
              |
              | Found:
              |""".trimMargin() + _existingFarmActivityLogs)
        }
        val _columnsFarmProfiles: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmProfiles.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("farmName", TableInfo.Column("farmName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("farmBio", TableInfo.Column("farmBio", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("logoUrl", TableInfo.Column("logoUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("coverPhotoUrl", TableInfo.Column("coverPhotoUrl", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("locationName", TableInfo.Column("locationName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("barangay", TableInfo.Column("barangay", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("municipality", TableInfo.Column("municipality", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("province", TableInfo.Column("province", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("latitude", TableInfo.Column("latitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("longitude", TableInfo.Column("longitude", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("isVerified", TableInfo.Column("isVerified", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("verifiedAt", TableInfo.Column("verifiedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("memberSince", TableInfo.Column("memberSince", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("farmEstablished", TableInfo.Column("farmEstablished", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("trustScore", TableInfo.Column("trustScore", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("totalBirdsSold", TableInfo.Column("totalBirdsSold", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("totalOrdersCompleted", TableInfo.Column("totalOrdersCompleted",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("avgResponseTimeMinutes",
            TableInfo.Column("avgResponseTimeMinutes", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("vaccinationRate", TableInfo.Column("vaccinationRate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("returningBuyerRate", TableInfo.Column("returningBuyerRate",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("badgesJson", TableInfo.Column("badgesJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("whatsappNumber", TableInfo.Column("whatsappNumber", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("isWhatsappEnabled", TableInfo.Column("isWhatsappEnabled",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("isCallEnabled", TableInfo.Column("isCallEnabled", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("isPublic", TableInfo.Column("isPublic", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("showLocation", TableInfo.Column("showLocation", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("showSalesHistory", TableInfo.Column("showSalesHistory", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("showTimeline", TableInfo.Column("showTimeline", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareVaccinationLogs", TableInfo.Column("shareVaccinationLogs",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareSanitationLogs", TableInfo.Column("shareSanitationLogs",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareFeedLogs", TableInfo.Column("shareFeedLogs", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareWeightData", TableInfo.Column("shareWeightData", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareSalesActivity", TableInfo.Column("shareSalesActivity",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareMortalityData", TableInfo.Column("shareMortalityData",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("shareExpenseData", TableInfo.Column("shareExpenseData", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmProfiles.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmProfiles: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFarmProfiles: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmProfiles.add(TableInfo.Index("index_farm_profiles_isVerified", false,
            listOf("isVerified"), listOf("ASC")))
        _indicesFarmProfiles.add(TableInfo.Index("index_farm_profiles_province", false,
            listOf("province"), listOf("ASC")))
        _indicesFarmProfiles.add(TableInfo.Index("index_farm_profiles_trustScore", false,
            listOf("trustScore"), listOf("ASC")))
        val _infoFarmProfiles: TableInfo = TableInfo("farm_profiles", _columnsFarmProfiles,
            _foreignKeysFarmProfiles, _indicesFarmProfiles)
        val _existingFarmProfiles: TableInfo = tableInfoRead(connection, "farm_profiles")
        if (!_infoFarmProfiles.equals(_existingFarmProfiles)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_profiles(com.rio.rostry.data.database.entity.FarmProfileEntity).
              | Expected:
              |""".trimMargin() + _infoFarmProfiles + """
              |
              | Found:
              |""".trimMargin() + _existingFarmProfiles)
        }
        val _columnsFarmTimelineEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmTimelineEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("description", TableInfo.Column("description", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("iconType", TableInfo.Column("iconType", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("sourceType", TableInfo.Column("sourceType", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("sourceId", TableInfo.Column("sourceId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("trustPointsEarned", TableInfo.Column("trustPointsEarned",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("isPublic", TableInfo.Column("isPublic", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("isMilestone", TableInfo.Column("isMilestone", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("eventDate", TableInfo.Column("eventDate", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmTimelineEvents.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmTimelineEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysFarmTimelineEvents.add(TableInfo.ForeignKey("users", "CASCADE", "NO ACTION",
            listOf("farmerId"), listOf("userId")))
        val _indicesFarmTimelineEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmTimelineEvents.add(TableInfo.Index("index_farm_timeline_events_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmTimelineEvents.add(TableInfo.Index("index_farm_timeline_events_eventType",
            false, listOf("eventType"), listOf("ASC")))
        _indicesFarmTimelineEvents.add(TableInfo.Index("index_farm_timeline_events_eventDate",
            false, listOf("eventDate"), listOf("ASC")))
        _indicesFarmTimelineEvents.add(TableInfo.Index("index_farm_timeline_events_isPublic", false,
            listOf("isPublic"), listOf("ASC")))
        val _infoFarmTimelineEvents: TableInfo = TableInfo("farm_timeline_events",
            _columnsFarmTimelineEvents, _foreignKeysFarmTimelineEvents, _indicesFarmTimelineEvents)
        val _existingFarmTimelineEvents: TableInfo = tableInfoRead(connection,
            "farm_timeline_events")
        if (!_infoFarmTimelineEvents.equals(_existingFarmTimelineEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_timeline_events(com.rio.rostry.data.database.entity.FarmTimelineEventEntity).
              | Expected:
              |""".trimMargin() + _infoFarmTimelineEvents + """
              |
              | Found:
              |""".trimMargin() + _existingFarmTimelineEvents)
        }
        val _columnsEnthusiastVerifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEnthusiastVerifications.put("verificationId", TableInfo.Column("verificationId",
            "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("userId", TableInfo.Column("userId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("experienceYears", TableInfo.Column("experienceYears",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("birdCount", TableInfo.Column("birdCount", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("specializations", TableInfo.Column("specializations",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("achievementsDescription",
            TableInfo.Column("achievementsDescription", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("referenceContacts",
            TableInfo.Column("referenceContacts", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("verificationDocumentUrls",
            TableInfo.Column("verificationDocumentUrls", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("profilePhotoUrl", TableInfo.Column("profilePhotoUrl",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("farmPhotoUrls", TableInfo.Column("farmPhotoUrls",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("status", TableInfo.Column("status", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("submittedAt", TableInfo.Column("submittedAt",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("reviewedAt", TableInfo.Column("reviewedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("reviewedBy", TableInfo.Column("reviewedBy", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("rejectionReason", TableInfo.Column("rejectionReason",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("adminNotes", TableInfo.Column("adminNotes", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("createdAt", TableInfo.Column("createdAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEnthusiastVerifications.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEnthusiastVerifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysEnthusiastVerifications.add(TableInfo.ForeignKey("users", "CASCADE",
            "NO ACTION", listOf("userId"), listOf("userId")))
        val _indicesEnthusiastVerifications: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesEnthusiastVerifications.add(TableInfo.Index("index_enthusiast_verifications_userId",
            false, listOf("userId"), listOf("ASC")))
        _indicesEnthusiastVerifications.add(TableInfo.Index("index_enthusiast_verifications_status",
            false, listOf("status"), listOf("ASC")))
        val _infoEnthusiastVerifications: TableInfo = TableInfo("enthusiast_verifications",
            _columnsEnthusiastVerifications, _foreignKeysEnthusiastVerifications,
            _indicesEnthusiastVerifications)
        val _existingEnthusiastVerifications: TableInfo = tableInfoRead(connection,
            "enthusiast_verifications")
        if (!_infoEnthusiastVerifications.equals(_existingEnthusiastVerifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |enthusiast_verifications(com.rio.rostry.data.database.entity.EnthusiastVerificationEntity).
              | Expected:
              |""".trimMargin() + _infoEnthusiastVerifications + """
              |
              | Found:
              |""".trimMargin() + _existingEnthusiastVerifications)
        }
        val _columnsFarmEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("scheduledAt", TableInfo.Column("scheduledAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("completedAt", TableInfo.Column("completedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("recurrence", TableInfo.Column("recurrence", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("productId", TableInfo.Column("productId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("batchId", TableInfo.Column("batchId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("reminderBefore", TableInfo.Column("reminderBefore", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("metadata", TableInfo.Column("metadata", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmEvents.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFarmEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesFarmEvents.add(TableInfo.Index("index_farm_events_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesFarmEvents.add(TableInfo.Index("index_farm_events_scheduledAt", false,
            listOf("scheduledAt"), listOf("ASC")))
        _indicesFarmEvents.add(TableInfo.Index("index_farm_events_eventType", false,
            listOf("eventType"), listOf("ASC")))
        _indicesFarmEvents.add(TableInfo.Index("index_farm_events_status", false, listOf("status"),
            listOf("ASC")))
        val _infoFarmEvents: TableInfo = TableInfo("farm_events", _columnsFarmEvents,
            _foreignKeysFarmEvents, _indicesFarmEvents)
        val _existingFarmEvents: TableInfo = tableInfoRead(connection, "farm_events")
        if (!_infoFarmEvents.equals(_existingFarmEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_events(com.rio.rostry.data.database.entity.FarmEventEntity).
              | Expected:
              |""".trimMargin() + _infoFarmEvents + """
              |
              | Found:
              |""".trimMargin() + _existingFarmEvents)
        }
        val _columnsRoleUpgradeRequests: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRoleUpgradeRequests.put("requestId", TableInfo.Column("requestId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("currentRole", TableInfo.Column("currentRole", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("requestedRole", TableInfo.Column("requestedRole", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("adminNotes", TableInfo.Column("adminNotes", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("reviewedBy", TableInfo.Column("reviewedBy", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("reviewedAt", TableInfo.Column("reviewedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRoleUpgradeRequests.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRoleUpgradeRequests: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRoleUpgradeRequests: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRoleUpgradeRequests: TableInfo = TableInfo("role_upgrade_requests",
            _columnsRoleUpgradeRequests, _foreignKeysRoleUpgradeRequests,
            _indicesRoleUpgradeRequests)
        val _existingRoleUpgradeRequests: TableInfo = tableInfoRead(connection,
            "role_upgrade_requests")
        if (!_infoRoleUpgradeRequests.equals(_existingRoleUpgradeRequests)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |role_upgrade_requests(com.rio.rostry.data.database.entity.RoleUpgradeRequestEntity).
              | Expected:
              |""".trimMargin() + _infoRoleUpgradeRequests + """
              |
              | Found:
              |""".trimMargin() + _existingRoleUpgradeRequests)
        }
        val _columnsTransactions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTransactions.put("transactionId", TableInfo.Column("transactionId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("orderId", TableInfo.Column("orderId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("paymentMethod", TableInfo.Column("paymentMethod", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("gatewayReference", TableInfo.Column("gatewayReference", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTransactions.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTransactions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTransactions: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTransactions.add(TableInfo.Index("index_transactions_orderId", false,
            listOf("orderId"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_transactions_userId", false,
            listOf("userId"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_transactions_timestamp", false,
            listOf("timestamp"), listOf("ASC")))
        _indicesTransactions.add(TableInfo.Index("index_transactions_status", false,
            listOf("status"), listOf("ASC")))
        val _infoTransactions: TableInfo = TableInfo("transactions", _columnsTransactions,
            _foreignKeysTransactions, _indicesTransactions)
        val _existingTransactions: TableInfo = tableInfoRead(connection, "transactions")
        if (!_infoTransactions.equals(_existingTransactions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |transactions(com.rio.rostry.data.database.entity.TransactionEntity).
              | Expected:
              |""".trimMargin() + _infoTransactions + """
              |
              | Found:
              |""".trimMargin() + _existingTransactions)
        }
        val _columnsExpenses: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsExpenses.put("expenseId", TableInfo.Column("expenseId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("assetId", TableInfo.Column("assetId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("amount", TableInfo.Column("amount", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("expenseDate", TableInfo.Column("expenseDate", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsExpenses.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysExpenses: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesExpenses: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesExpenses.add(TableInfo.Index("index_expenses_farmerId", false, listOf("farmerId"),
            listOf("ASC")))
        _indicesExpenses.add(TableInfo.Index("index_expenses_assetId", false, listOf("assetId"),
            listOf("ASC")))
        _indicesExpenses.add(TableInfo.Index("index_expenses_category", false, listOf("category"),
            listOf("ASC")))
        _indicesExpenses.add(TableInfo.Index("index_expenses_expenseDate", false,
            listOf("expenseDate"), listOf("ASC")))
        val _infoExpenses: TableInfo = TableInfo("expenses", _columnsExpenses, _foreignKeysExpenses,
            _indicesExpenses)
        val _existingExpenses: TableInfo = tableInfoRead(connection, "expenses")
        if (!_infoExpenses.equals(_existingExpenses)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |expenses(com.rio.rostry.data.database.entity.ExpenseEntity).
              | Expected:
              |""".trimMargin() + _infoExpenses + """
              |
              | Found:
              |""".trimMargin() + _existingExpenses)
        }
        val _columnsMedicalEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMedicalEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("birdId", TableInfo.Column("birdId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("severity", TableInfo.Column("severity", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("eventDate", TableInfo.Column("eventDate", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("resolvedDate", TableInfo.Column("resolvedDate", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("diagnosis", TableInfo.Column("diagnosis", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("symptoms", TableInfo.Column("symptoms", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("treatment", TableInfo.Column("treatment", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("medication", TableInfo.Column("medication", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("dosage", TableInfo.Column("dosage", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("treatmentDuration", TableInfo.Column("treatmentDuration", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("outcome", TableInfo.Column("outcome", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("treatedBy", TableInfo.Column("treatedBy", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("vetVisit", TableInfo.Column("vetVisit", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("vetNotes", TableInfo.Column("vetNotes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("cost", TableInfo.Column("cost", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("mediaUrlsJson", TableInfo.Column("mediaUrlsJson", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMedicalEvents.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMedicalEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysMedicalEvents.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("birdId"), listOf("productId")))
        val _indicesMedicalEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMedicalEvents.add(TableInfo.Index("index_medical_events_birdId", false,
            listOf("birdId"), listOf("ASC")))
        _indicesMedicalEvents.add(TableInfo.Index("index_medical_events_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        _indicesMedicalEvents.add(TableInfo.Index("index_medical_events_eventType", false,
            listOf("eventType"), listOf("ASC")))
        _indicesMedicalEvents.add(TableInfo.Index("index_medical_events_eventDate", false,
            listOf("eventDate"), listOf("ASC")))
        _indicesMedicalEvents.add(TableInfo.Index("index_medical_events_status", false,
            listOf("status"), listOf("ASC")))
        val _infoMedicalEvents: TableInfo = TableInfo("medical_events", _columnsMedicalEvents,
            _foreignKeysMedicalEvents, _indicesMedicalEvents)
        val _existingMedicalEvents: TableInfo = tableInfoRead(connection, "medical_events")
        if (!_infoMedicalEvents.equals(_existingMedicalEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |medical_events(com.rio.rostry.data.database.entity.MedicalEventEntity).
              | Expected:
              |""".trimMargin() + _infoMedicalEvents + """
              |
              | Found:
              |""".trimMargin() + _existingMedicalEvents)
        }
        val _columnsClutches: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsClutches.put("clutchId", TableInfo.Column("clutchId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("breedingPairId", TableInfo.Column("breedingPairId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("sireId", TableInfo.Column("sireId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("damId", TableInfo.Column("damId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("clutchName", TableInfo.Column("clutchName", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("clutchNumber", TableInfo.Column("clutchNumber", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("eggsCollected", TableInfo.Column("eggsCollected", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("collectionStartDate", TableInfo.Column("collectionStartDate",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("collectionEndDate", TableInfo.Column("collectionEndDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("setDate", TableInfo.Column("setDate", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("eggsSet", TableInfo.Column("eggsSet", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("incubatorId", TableInfo.Column("incubatorId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("incubationNotes", TableInfo.Column("incubationNotes", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("firstCandleDate", TableInfo.Column("firstCandleDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("firstCandleFertile", TableInfo.Column("firstCandleFertile", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("firstCandleClear", TableInfo.Column("firstCandleClear", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("firstCandleEarlyDead", TableInfo.Column("firstCandleEarlyDead",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("secondCandleDate", TableInfo.Column("secondCandleDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("secondCandleAlive", TableInfo.Column("secondCandleAlive", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("secondCandleDead", TableInfo.Column("secondCandleDead", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("expectedHatchDate", TableInfo.Column("expectedHatchDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("actualHatchStartDate", TableInfo.Column("actualHatchStartDate",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("actualHatchEndDate", TableInfo.Column("actualHatchEndDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("chicksHatched", TableInfo.Column("chicksHatched", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("chicksMale", TableInfo.Column("chicksMale", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("chicksFemale", TableInfo.Column("chicksFemale", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("chicksUnsexed", TableInfo.Column("chicksUnsexed", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("deadInShell", TableInfo.Column("deadInShell", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("pippedNotHatched", TableInfo.Column("pippedNotHatched", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("averageChickWeight", TableInfo.Column("averageChickWeight", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("chickQualityScore", TableInfo.Column("chickQualityScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("qualityNotes", TableInfo.Column("qualityNotes", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("fertilityRate", TableInfo.Column("fertilityRate", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("hatchabilityOfFertile", TableInfo.Column("hatchabilityOfFertile",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("hatchabilityOfSet", TableInfo.Column("hatchabilityOfSet", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("offspringIdsJson", TableInfo.Column("offspringIdsJson", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("mediaUrlsJson", TableInfo.Column("mediaUrlsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsClutches.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysClutches: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysClutches.add(TableInfo.ForeignKey("breeding_pairs", "SET NULL", "NO ACTION",
            listOf("breedingPairId"), listOf("pairId")))
        val _indicesClutches: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesClutches.add(TableInfo.Index("index_clutches_breedingPairId", false,
            listOf("breedingPairId"), listOf("ASC")))
        _indicesClutches.add(TableInfo.Index("index_clutches_farmerId", false, listOf("farmerId"),
            listOf("ASC")))
        _indicesClutches.add(TableInfo.Index("index_clutches_status", false, listOf("status"),
            listOf("ASC")))
        _indicesClutches.add(TableInfo.Index("index_clutches_setDate", false, listOf("setDate"),
            listOf("ASC")))
        _indicesClutches.add(TableInfo.Index("index_clutches_expectedHatchDate", false,
            listOf("expectedHatchDate"), listOf("ASC")))
        val _infoClutches: TableInfo = TableInfo("clutches", _columnsClutches, _foreignKeysClutches,
            _indicesClutches)
        val _existingClutches: TableInfo = tableInfoRead(connection, "clutches")
        if (!_infoClutches.equals(_existingClutches)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |clutches(com.rio.rostry.data.database.entity.ClutchEntity).
              | Expected:
              |""".trimMargin() + _infoClutches + """
              |
              | Found:
              |""".trimMargin() + _existingClutches)
        }
        val _columnsBirdTraitRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBirdTraitRecords.put("recordId", TableInfo.Column("recordId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("productId", TableInfo.Column("productId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("traitCategory", TableInfo.Column("traitCategory", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("traitName", TableInfo.Column("traitName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("traitValue", TableInfo.Column("traitValue", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("traitUnit", TableInfo.Column("traitUnit", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("numericValue", TableInfo.Column("numericValue", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("ageWeeks", TableInfo.Column("ageWeeks", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("recordedAt", TableInfo.Column("recordedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("measuredBy", TableInfo.Column("measuredBy", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdTraitRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBirdTraitRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBirdTraitRecords.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("productId"), listOf("productId")))
        val _indicesBirdTraitRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBirdTraitRecords.add(TableInfo.Index("index_bird_trait_records_productId", false,
            listOf("productId"), listOf("ASC")))
        _indicesBirdTraitRecords.add(TableInfo.Index("index_bird_trait_records_ownerId", false,
            listOf("ownerId"), listOf("ASC")))
        _indicesBirdTraitRecords.add(TableInfo.Index("index_bird_trait_records_traitCategory",
            false, listOf("traitCategory"), listOf("ASC")))
        _indicesBirdTraitRecords.add(TableInfo.Index("index_bird_trait_records_traitName", false,
            listOf("traitName"), listOf("ASC")))
        _indicesBirdTraitRecords.add(TableInfo.Index("index_bird_trait_records_recordedAt", false,
            listOf("recordedAt"), listOf("ASC")))
        _indicesBirdTraitRecords.add(TableInfo.Index("index_bird_trait_records_productId_traitName_ageWeeks",
            false, listOf("productId", "traitName", "ageWeeks"), listOf("ASC", "ASC", "ASC")))
        val _infoBirdTraitRecords: TableInfo = TableInfo("bird_trait_records",
            _columnsBirdTraitRecords, _foreignKeysBirdTraitRecords, _indicesBirdTraitRecords)
        val _existingBirdTraitRecords: TableInfo = tableInfoRead(connection, "bird_trait_records")
        if (!_infoBirdTraitRecords.equals(_existingBirdTraitRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bird_trait_records(com.rio.rostry.data.database.entity.BirdTraitRecordEntity).
              | Expected:
              |""".trimMargin() + _infoBirdTraitRecords + """
              |
              | Found:
              |""".trimMargin() + _existingBirdTraitRecords)
        }
        val _columnsBreedingPlans: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBreedingPlans.put("planId", TableInfo.Column("planId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("sireId", TableInfo.Column("sireId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("sireName", TableInfo.Column("sireName", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("damId", TableInfo.Column("damId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("damName", TableInfo.Column("damName", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("note", TableInfo.Column("note", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("simulatedOffspringJson",
            TableInfo.Column("simulatedOffspringJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBreedingPlans.put("priority", TableInfo.Column("priority", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBreedingPlans: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBreedingPlans.add(TableInfo.ForeignKey("products", "SET NULL", "NO ACTION",
            listOf("sireId"), listOf("productId")))
        _foreignKeysBreedingPlans.add(TableInfo.ForeignKey("products", "SET NULL", "NO ACTION",
            listOf("damId"), listOf("productId")))
        val _indicesBreedingPlans: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBreedingPlans.add(TableInfo.Index("index_breeding_plans_sireId", false,
            listOf("sireId"), listOf("ASC")))
        _indicesBreedingPlans.add(TableInfo.Index("index_breeding_plans_damId", false,
            listOf("damId"), listOf("ASC")))
        _indicesBreedingPlans.add(TableInfo.Index("index_breeding_plans_farmerId", false,
            listOf("farmerId"), listOf("ASC")))
        val _infoBreedingPlans: TableInfo = TableInfo("breeding_plans", _columnsBreedingPlans,
            _foreignKeysBreedingPlans, _indicesBreedingPlans)
        val _existingBreedingPlans: TableInfo = tableInfoRead(connection, "breeding_plans")
        if (!_infoBreedingPlans.equals(_existingBreedingPlans)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |breeding_plans(com.rio.rostry.data.database.entity.BreedingPlanEntity).
              | Expected:
              |""".trimMargin() + _infoBreedingPlans + """
              |
              | Found:
              |""".trimMargin() + _existingBreedingPlans)
        }
        val _columnsArenaParticipants: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsArenaParticipants.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("competitionId", TableInfo.Column("competitionId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("birdId", TableInfo.Column("birdId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("birdName", TableInfo.Column("birdName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("birdImageUrl", TableInfo.Column("birdImageUrl", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("breed", TableInfo.Column("breed", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("entryTime", TableInfo.Column("entryTime", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("totalVotes", TableInfo.Column("totalVotes", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("averageScore", TableInfo.Column("averageScore", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsArenaParticipants.put("rank", TableInfo.Column("rank", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysArenaParticipants: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysArenaParticipants.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("birdId"), listOf("productId")))
        _foreignKeysArenaParticipants.add(TableInfo.ForeignKey("competitions", "CASCADE",
            "NO ACTION", listOf("competitionId"), listOf("competitionId")))
        val _indicesArenaParticipants: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesArenaParticipants.add(TableInfo.Index("index_arena_participants_birdId", false,
            listOf("birdId"), listOf("ASC")))
        _indicesArenaParticipants.add(TableInfo.Index("index_arena_participants_competitionId",
            false, listOf("competitionId"), listOf("ASC")))
        _indicesArenaParticipants.add(TableInfo.Index("index_arena_participants_ownerId", false,
            listOf("ownerId"), listOf("ASC")))
        val _infoArenaParticipants: TableInfo = TableInfo("arena_participants",
            _columnsArenaParticipants, _foreignKeysArenaParticipants, _indicesArenaParticipants)
        val _existingArenaParticipants: TableInfo = tableInfoRead(connection, "arena_participants")
        if (!_infoArenaParticipants.equals(_existingArenaParticipants)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |arena_participants(com.rio.rostry.data.database.entity.ArenaParticipantEntity).
              | Expected:
              |""".trimMargin() + _infoArenaParticipants + """
              |
              | Found:
              |""".trimMargin() + _existingArenaParticipants)
        }
        val _columnsDigitalTwins: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDigitalTwins.put("twinId", TableInfo.Column("twinId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("birdId", TableInfo.Column("birdId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("registryId", TableInfo.Column("registryId", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("birdName", TableInfo.Column("birdName", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("baseBreed", TableInfo.Column("baseBreed", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("strainType", TableInfo.Column("strainType", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("localStrainName", TableInfo.Column("localStrainName", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("geneticPurityScore", TableInfo.Column("geneticPurityScore",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("bodyType", TableInfo.Column("bodyType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("boneDensityScore", TableInfo.Column("boneDensityScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("heightCm", TableInfo.Column("heightCm", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("weightKg", TableInfo.Column("weightKg", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("beakType", TableInfo.Column("beakType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("combType", TableInfo.Column("combType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("skinColor", TableInfo.Column("skinColor", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("legColor", TableInfo.Column("legColor", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("spurType", TableInfo.Column("spurType", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("morphologyScore", TableInfo.Column("morphologyScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("primaryBodyColor", TableInfo.Column("primaryBodyColor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("neckHackleColor", TableInfo.Column("neckHackleColor", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("wingHighlightColor", TableInfo.Column("wingHighlightColor",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("tailColor", TableInfo.Column("tailColor", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("tailIridescent", TableInfo.Column("tailIridescent", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("plumagePattern", TableInfo.Column("plumagePattern", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("localColorCode", TableInfo.Column("localColorCode", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("colorCategoryCode", TableInfo.Column("colorCategoryCode", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("lifecycleStage", TableInfo.Column("lifecycleStage", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("ageDays", TableInfo.Column("ageDays", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("maturityScore", TableInfo.Column("maturityScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("breedingStatus", TableInfo.Column("breedingStatus", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("gender", TableInfo.Column("gender", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("birthDate", TableInfo.Column("birthDate", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("sireId", TableInfo.Column("sireId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("damId", TableInfo.Column("damId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("generationDepth", TableInfo.Column("generationDepth", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("inbreedingCoefficient", TableInfo.Column("inbreedingCoefficient",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("geneticsJson", TableInfo.Column("geneticsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("geneticsScore", TableInfo.Column("geneticsScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("vaccinationCount", TableInfo.Column("vaccinationCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("injuryCount", TableInfo.Column("injuryCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("staminaScore", TableInfo.Column("staminaScore", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("healthScore", TableInfo.Column("healthScore", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("currentHealthStatus", TableInfo.Column("currentHealthStatus",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("aggressionIndex", TableInfo.Column("aggressionIndex", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("enduranceScore", TableInfo.Column("enduranceScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("intelligenceScore", TableInfo.Column("intelligenceScore",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("totalFights", TableInfo.Column("totalFights", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("fightWins", TableInfo.Column("fightWins", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("performanceScore", TableInfo.Column("performanceScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("valuationScore", TableInfo.Column("valuationScore", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("verifiedStatus", TableInfo.Column("verifiedStatus", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("certificationLevel", TableInfo.Column("certificationLevel",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("estimatedValueInr", TableInfo.Column("estimatedValueInr", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("totalShows", TableInfo.Column("totalShows", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("showWins", TableInfo.Column("showWins", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("bestPlacement", TableInfo.Column("bestPlacement", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("totalBreedingAttempts", TableInfo.Column("totalBreedingAttempts",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("successfulBreedings", TableInfo.Column("successfulBreedings",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("totalOffspring", TableInfo.Column("totalOffspring", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("appearanceJson", TableInfo.Column("appearanceJson", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("metadataJson", TableInfo.Column("metadataJson", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("isDeleted", TableInfo.Column("isDeleted", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDigitalTwins.put("deletedAt", TableInfo.Column("deletedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDigitalTwins: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDigitalTwins.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("birdId"), listOf("productId")))
        val _indicesDigitalTwins: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_birdId", true,
            listOf("birdId"), listOf("ASC")))
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_ownerId", false,
            listOf("ownerId"), listOf("ASC")))
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_baseBreed", false,
            listOf("baseBreed"), listOf("ASC")))
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_strainType", false,
            listOf("strainType"), listOf("ASC")))
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_lifecycleStage", false,
            listOf("lifecycleStage"), listOf("ASC")))
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_certificationLevel", false,
            listOf("certificationLevel"), listOf("ASC")))
        _indicesDigitalTwins.add(TableInfo.Index("index_digital_twins_dirty", false,
            listOf("dirty"), listOf("ASC")))
        val _infoDigitalTwins: TableInfo = TableInfo("digital_twins", _columnsDigitalTwins,
            _foreignKeysDigitalTwins, _indicesDigitalTwins)
        val _existingDigitalTwins: TableInfo = tableInfoRead(connection, "digital_twins")
        if (!_infoDigitalTwins.equals(_existingDigitalTwins)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |digital_twins(com.rio.rostry.data.database.entity.DigitalTwinEntity).
              | Expected:
              |""".trimMargin() + _infoDigitalTwins + """
              |
              | Found:
              |""".trimMargin() + _existingDigitalTwins)
        }
        val _columnsBirdEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBirdEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("birdId", TableInfo.Column("birdId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("ownerId", TableInfo.Column("ownerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("eventTitle", TableInfo.Column("eventTitle", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("eventDescription", TableInfo.Column("eventDescription", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("eventDate", TableInfo.Column("eventDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("ageDaysAtEvent", TableInfo.Column("ageDaysAtEvent", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("lifecycleStageAtEvent", TableInfo.Column("lifecycleStageAtEvent",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("numericValue", TableInfo.Column("numericValue", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("numericValue2", TableInfo.Column("numericValue2", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("stringValue", TableInfo.Column("stringValue", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("dataJson", TableInfo.Column("dataJson", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("morphologyScoreDelta", TableInfo.Column("morphologyScoreDelta",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("geneticsScoreDelta", TableInfo.Column("geneticsScoreDelta",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("performanceScoreDelta", TableInfo.Column("performanceScoreDelta",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("healthScoreDelta", TableInfo.Column("healthScoreDelta", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("marketScoreDelta", TableInfo.Column("marketScoreDelta", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("recordedBy", TableInfo.Column("recordedBy", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("isVerified", TableInfo.Column("isVerified", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("verifiedBy", TableInfo.Column("verifiedBy", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("mediaUrlsJson", TableInfo.Column("mediaUrlsJson", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBirdEvents.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBirdEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBirdEvents.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("birdId"), listOf("productId")))
        val _indicesBirdEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_birdId", false, listOf("birdId"),
            listOf("ASC")))
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_ownerId", false,
            listOf("ownerId"), listOf("ASC")))
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_eventType", false,
            listOf("eventType"), listOf("ASC")))
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_eventDate", false,
            listOf("eventDate"), listOf("ASC")))
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_birdId_eventType", false,
            listOf("birdId", "eventType"), listOf("ASC", "ASC")))
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_birdId_eventDate", false,
            listOf("birdId", "eventDate"), listOf("ASC", "ASC")))
        _indicesBirdEvents.add(TableInfo.Index("index_bird_events_dirty", false, listOf("dirty"),
            listOf("ASC")))
        val _infoBirdEvents: TableInfo = TableInfo("bird_events", _columnsBirdEvents,
            _foreignKeysBirdEvents, _indicesBirdEvents)
        val _existingBirdEvents: TableInfo = tableInfoRead(connection, "bird_events")
        if (!_infoBirdEvents.equals(_existingBirdEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bird_events(com.rio.rostry.data.database.entity.BirdEventEntity).
              | Expected:
              |""".trimMargin() + _infoBirdEvents + """
              |
              | Found:
              |""".trimMargin() + _existingBirdEvents)
        }
        val _columnsAssetLifecycleEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAssetLifecycleEvents.put("eventId", TableInfo.Column("eventId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("assetId", TableInfo.Column("assetId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("fromStage", TableInfo.Column("fromStage", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("toStage", TableInfo.Column("toStage", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("eventData", TableInfo.Column("eventData", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("triggeredBy", TableInfo.Column("triggeredBy", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("occurredAt", TableInfo.Column("occurredAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("recordedAt", TableInfo.Column("recordedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("recordedBy", TableInfo.Column("recordedBy", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("notes", TableInfo.Column("notes", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("mediaItemsJson", TableInfo.Column("mediaItemsJson",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetLifecycleEvents.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAssetLifecycleEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAssetLifecycleEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAssetLifecycleEvents: TableInfo = TableInfo("asset_lifecycle_events",
            _columnsAssetLifecycleEvents, _foreignKeysAssetLifecycleEvents,
            _indicesAssetLifecycleEvents)
        val _existingAssetLifecycleEvents: TableInfo = tableInfoRead(connection,
            "asset_lifecycle_events")
        if (!_infoAssetLifecycleEvents.equals(_existingAssetLifecycleEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |asset_lifecycle_events(com.rio.rostry.data.database.entity.AssetLifecycleEventEntity).
              | Expected:
              |""".trimMargin() + _infoAssetLifecycleEvents + """
              |
              | Found:
              |""".trimMargin() + _existingAssetLifecycleEvents)
        }
        val _columnsAssetHealthRecords: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAssetHealthRecords.put("recordId", TableInfo.Column("recordId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("assetId", TableInfo.Column("assetId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("recordType", TableInfo.Column("recordType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("recordData", TableInfo.Column("recordData", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("healthScore", TableInfo.Column("healthScore", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("veterinarianId", TableInfo.Column("veterinarianId", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("veterinarianNotes", TableInfo.Column("veterinarianNotes",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("followUpRequired", TableInfo.Column("followUpRequired",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("followUpDate", TableInfo.Column("followUpDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("costInr", TableInfo.Column("costInr", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("mediaItemsJson", TableInfo.Column("mediaItemsJson", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetHealthRecords.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAssetHealthRecords: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAssetHealthRecords: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAssetHealthRecords: TableInfo = TableInfo("asset_health_records",
            _columnsAssetHealthRecords, _foreignKeysAssetHealthRecords, _indicesAssetHealthRecords)
        val _existingAssetHealthRecords: TableInfo = tableInfoRead(connection,
            "asset_health_records")
        if (!_infoAssetHealthRecords.equals(_existingAssetHealthRecords)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |asset_health_records(com.rio.rostry.data.database.entity.AssetHealthRecordEntity).
              | Expected:
              |""".trimMargin() + _infoAssetHealthRecords + """
              |
              | Found:
              |""".trimMargin() + _existingAssetHealthRecords)
        }
        val _columnsTaskRecurrences: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTaskRecurrences.put("recurrenceId", TableInfo.Column("recurrenceId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("taskId", TableInfo.Column("taskId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("pattern", TableInfo.Column("pattern", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("interval", TableInfo.Column("interval", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("daysOfWeek", TableInfo.Column("daysOfWeek", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("endDate", TableInfo.Column("endDate", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("maxOccurrences", TableInfo.Column("maxOccurrences", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("currentOccurrence", TableInfo.Column("currentOccurrence",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("lastGenerated", TableInfo.Column("lastGenerated", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("nextDue", TableInfo.Column("nextDue", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("isActive", TableInfo.Column("isActive", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTaskRecurrences.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTaskRecurrences: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTaskRecurrences: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoTaskRecurrences: TableInfo = TableInfo("task_recurrences", _columnsTaskRecurrences,
            _foreignKeysTaskRecurrences, _indicesTaskRecurrences)
        val _existingTaskRecurrences: TableInfo = tableInfoRead(connection, "task_recurrences")
        if (!_infoTaskRecurrences.equals(_existingTaskRecurrences)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |task_recurrences(com.rio.rostry.data.database.entity.TaskRecurrenceEntity).
              | Expected:
              |""".trimMargin() + _infoTaskRecurrences + """
              |
              | Found:
              |""".trimMargin() + _existingTaskRecurrences)
        }
        val _columnsAssetBatchOperations: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAssetBatchOperations.put("operationId", TableInfo.Column("operationId", "TEXT",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("farmerId", TableInfo.Column("farmerId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("operationType", TableInfo.Column("operationType", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("selectionCriteria", TableInfo.Column("selectionCriteria",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("operationData", TableInfo.Column("operationData", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("totalItems", TableInfo.Column("totalItems", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("processedItems", TableInfo.Column("processedItems",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("successfulItems", TableInfo.Column("successfulItems",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("failedItems", TableInfo.Column("failedItems", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("errorLog", TableInfo.Column("errorLog", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("canRollback", TableInfo.Column("canRollback", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("rollbackData", TableInfo.Column("rollbackData", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("startedAt", TableInfo.Column("startedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("completedAt", TableInfo.Column("completedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("estimatedDuration", TableInfo.Column("estimatedDuration",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAssetBatchOperations.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAssetBatchOperations: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAssetBatchOperations: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAssetBatchOperations: TableInfo = TableInfo("asset_batch_operations",
            _columnsAssetBatchOperations, _foreignKeysAssetBatchOperations,
            _indicesAssetBatchOperations)
        val _existingAssetBatchOperations: TableInfo = tableInfoRead(connection,
            "asset_batch_operations")
        if (!_infoAssetBatchOperations.equals(_existingAssetBatchOperations)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |asset_batch_operations(com.rio.rostry.data.database.entity.AssetBatchOperationEntity).
              | Expected:
              |""".trimMargin() + _infoAssetBatchOperations + """
              |
              | Found:
              |""".trimMargin() + _existingAssetBatchOperations)
        }
        val _columnsComplianceRules: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsComplianceRules.put("ruleId", TableInfo.Column("ruleId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("jurisdiction", TableInfo.Column("jurisdiction", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("ruleType", TableInfo.Column("ruleType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("assetTypes", TableInfo.Column("assetTypes", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("ruleData", TableInfo.Column("ruleData", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("isActive", TableInfo.Column("isActive", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("effectiveFrom", TableInfo.Column("effectiveFrom", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("effectiveUntil", TableInfo.Column("effectiveUntil", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("severity", TableInfo.Column("severity", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("reminderDays", TableInfo.Column("reminderDays", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsComplianceRules.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysComplianceRules: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesComplianceRules: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoComplianceRules: TableInfo = TableInfo("compliance_rules", _columnsComplianceRules,
            _foreignKeysComplianceRules, _indicesComplianceRules)
        val _existingComplianceRules: TableInfo = tableInfoRead(connection, "compliance_rules")
        if (!_infoComplianceRules.equals(_existingComplianceRules)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |compliance_rules(com.rio.rostry.data.database.entity.ComplianceRuleEntity).
              | Expected:
              |""".trimMargin() + _infoComplianceRules + """
              |
              | Found:
              |""".trimMargin() + _existingComplianceRules)
        }
        val _columnsMediaItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMediaItems.put("mediaId", TableInfo.Column("mediaId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("assetId", TableInfo.Column("assetId", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("url", TableInfo.Column("url", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("localPath", TableInfo.Column("localPath", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("mediaType", TableInfo.Column("mediaType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("dateAdded", TableInfo.Column("dateAdded", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("fileSize", TableInfo.Column("fileSize", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("width", TableInfo.Column("width", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("height", TableInfo.Column("height", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("duration", TableInfo.Column("duration", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("thumbnailUrl", TableInfo.Column("thumbnailUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("uploadStatus", TableInfo.Column("uploadStatus", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("isCached", TableInfo.Column("isCached", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("lastAccessedAt", TableInfo.Column("lastAccessedAt", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaItems.put("dirty", TableInfo.Column("dirty", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMediaItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMediaItems: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMediaItems.add(TableInfo.Index("index_media_items_assetId", false,
            listOf("assetId"), listOf("ASC")))
        _indicesMediaItems.add(TableInfo.Index("index_media_items_dateAdded", false,
            listOf("dateAdded"), listOf("ASC")))
        _indicesMediaItems.add(TableInfo.Index("index_media_items_uploadStatus", false,
            listOf("uploadStatus"), listOf("ASC")))
        _indicesMediaItems.add(TableInfo.Index("index_media_items_mediaType", false,
            listOf("mediaType"), listOf("ASC")))
        val _infoMediaItems: TableInfo = TableInfo("media_items", _columnsMediaItems,
            _foreignKeysMediaItems, _indicesMediaItems)
        val _existingMediaItems: TableInfo = tableInfoRead(connection, "media_items")
        if (!_infoMediaItems.equals(_existingMediaItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |media_items(com.rio.rostry.data.database.entity.MediaItemEntity).
              | Expected:
              |""".trimMargin() + _infoMediaItems + """
              |
              | Found:
              |""".trimMargin() + _existingMediaItems)
        }
        val _columnsMediaTags: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMediaTags.put("mediaId", TableInfo.Column("mediaId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaTags.put("tagId", TableInfo.Column("tagId", "TEXT", true, 2, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaTags.put("tagType", TableInfo.Column("tagType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaTags.put("value", TableInfo.Column("value", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaTags.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMediaTags: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysMediaTags.add(TableInfo.ForeignKey("media_items", "CASCADE", "NO ACTION",
            listOf("mediaId"), listOf("mediaId")))
        val _indicesMediaTags: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMediaTags.add(TableInfo.Index("index_media_tags_mediaId", false, listOf("mediaId"),
            listOf("ASC")))
        _indicesMediaTags.add(TableInfo.Index("index_media_tags_tagType_value", false,
            listOf("tagType", "value"), listOf("ASC", "ASC")))
        val _infoMediaTags: TableInfo = TableInfo("media_tags", _columnsMediaTags,
            _foreignKeysMediaTags, _indicesMediaTags)
        val _existingMediaTags: TableInfo = tableInfoRead(connection, "media_tags")
        if (!_infoMediaTags.equals(_existingMediaTags)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |media_tags(com.rio.rostry.data.database.entity.MediaTagEntity).
              | Expected:
              |""".trimMargin() + _infoMediaTags + """
              |
              | Found:
              |""".trimMargin() + _existingMediaTags)
        }
        val _columnsMediaCacheMetadata: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMediaCacheMetadata.put("mediaId", TableInfo.Column("mediaId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaCacheMetadata.put("localPath", TableInfo.Column("localPath", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaCacheMetadata.put("fileSize", TableInfo.Column("fileSize", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaCacheMetadata.put("downloadedAt", TableInfo.Column("downloadedAt", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaCacheMetadata.put("lastAccessedAt", TableInfo.Column("lastAccessedAt",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaCacheMetadata.put("accessCount", TableInfo.Column("accessCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMediaCacheMetadata: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesMediaCacheMetadata: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMediaCacheMetadata.add(TableInfo.Index("index_media_cache_metadata_lastAccessedAt",
            false, listOf("lastAccessedAt"), listOf("ASC")))
        _indicesMediaCacheMetadata.add(TableInfo.Index("index_media_cache_metadata_fileSize", false,
            listOf("fileSize"), listOf("ASC")))
        val _infoMediaCacheMetadata: TableInfo = TableInfo("media_cache_metadata",
            _columnsMediaCacheMetadata, _foreignKeysMediaCacheMetadata, _indicesMediaCacheMetadata)
        val _existingMediaCacheMetadata: TableInfo = tableInfoRead(connection,
            "media_cache_metadata")
        if (!_infoMediaCacheMetadata.equals(_existingMediaCacheMetadata)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |media_cache_metadata(com.rio.rostry.data.database.entity.MediaCacheMetadataEntity).
              | Expected:
              |""".trimMargin() + _infoMediaCacheMetadata + """
              |
              | Found:
              |""".trimMargin() + _existingMediaCacheMetadata)
        }
        val _columnsGalleryFilterState: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsGalleryFilterState.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsGalleryFilterState.put("ageGroupsJson", TableInfo.Column("ageGroupsJson", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGalleryFilterState.put("sourceTypesJson", TableInfo.Column("sourceTypesJson",
            "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsGalleryFilterState.put("updatedAt", TableInfo.Column("updatedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysGalleryFilterState: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesGalleryFilterState: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoGalleryFilterState: TableInfo = TableInfo("gallery_filter_state",
            _columnsGalleryFilterState, _foreignKeysGalleryFilterState, _indicesGalleryFilterState)
        val _existingGalleryFilterState: TableInfo = tableInfoRead(connection,
            "gallery_filter_state")
        if (!_infoGalleryFilterState.equals(_existingGalleryFilterState)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |gallery_filter_state(com.rio.rostry.data.database.entity.GalleryFilterStateEntity).
              | Expected:
              |""".trimMargin() + _infoGalleryFilterState + """
              |
              | Found:
              |""".trimMargin() + _existingGalleryFilterState)
        }
        val _columnsErrorLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsErrorLogs.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("user_id", TableInfo.Column("user_id", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("operation_name", TableInfo.Column("operation_name", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("error_category", TableInfo.Column("error_category", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("error_message", TableInfo.Column("error_message", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("stack_trace", TableInfo.Column("stack_trace", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("additional_data", TableInfo.Column("additional_data", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsErrorLogs.put("reported", TableInfo.Column("reported", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysErrorLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesErrorLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesErrorLogs.add(TableInfo.Index("index_error_logs_timestamp", false,
            listOf("timestamp"), listOf("ASC")))
        _indicesErrorLogs.add(TableInfo.Index("index_error_logs_user_id", false, listOf("user_id"),
            listOf("ASC")))
        _indicesErrorLogs.add(TableInfo.Index("index_error_logs_error_category", false,
            listOf("error_category"), listOf("ASC")))
        val _infoErrorLogs: TableInfo = TableInfo("error_logs", _columnsErrorLogs,
            _foreignKeysErrorLogs, _indicesErrorLogs)
        val _existingErrorLogs: TableInfo = tableInfoRead(connection, "error_logs")
        if (!_infoErrorLogs.equals(_existingErrorLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |error_logs(com.rio.rostry.data.database.entity.ErrorLogEntity).
              | Expected:
              |""".trimMargin() + _infoErrorLogs + """
              |
              | Found:
              |""".trimMargin() + _existingErrorLogs)
        }
        val _columnsConfigurationCache: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsConfigurationCache.put("key", TableInfo.Column("key", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConfigurationCache.put("value", TableInfo.Column("value", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsConfigurationCache.put("value_type", TableInfo.Column("value_type", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConfigurationCache.put("last_updated", TableInfo.Column("last_updated", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsConfigurationCache.put("source", TableInfo.Column("source", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysConfigurationCache: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesConfigurationCache: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesConfigurationCache.add(TableInfo.Index("index_configuration_cache_last_updated",
            false, listOf("last_updated"), listOf("ASC")))
        val _infoConfigurationCache: TableInfo = TableInfo("configuration_cache",
            _columnsConfigurationCache, _foreignKeysConfigurationCache, _indicesConfigurationCache)
        val _existingConfigurationCache: TableInfo = tableInfoRead(connection,
            "configuration_cache")
        if (!_infoConfigurationCache.equals(_existingConfigurationCache)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |configuration_cache(com.rio.rostry.data.database.entity.ConfigurationCacheEntity).
              | Expected:
              |""".trimMargin() + _infoConfigurationCache + """
              |
              | Found:
              |""".trimMargin() + _existingConfigurationCache)
        }
        val _columnsCircuitBreakerMetrics: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCircuitBreakerMetrics.put("service_name", TableInfo.Column("service_name", "TEXT",
            true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCircuitBreakerMetrics.put("state", TableInfo.Column("state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCircuitBreakerMetrics.put("failure_rate", TableInfo.Column("failure_rate", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCircuitBreakerMetrics.put("total_calls", TableInfo.Column("total_calls", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCircuitBreakerMetrics.put("failed_calls", TableInfo.Column("failed_calls",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCircuitBreakerMetrics.put("last_state_change", TableInfo.Column("last_state_change",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCircuitBreakerMetrics.put("last_updated", TableInfo.Column("last_updated",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCircuitBreakerMetrics: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCircuitBreakerMetrics: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCircuitBreakerMetrics: TableInfo = TableInfo("circuit_breaker_metrics",
            _columnsCircuitBreakerMetrics, _foreignKeysCircuitBreakerMetrics,
            _indicesCircuitBreakerMetrics)
        val _existingCircuitBreakerMetrics: TableInfo = tableInfoRead(connection,
            "circuit_breaker_metrics")
        if (!_infoCircuitBreakerMetrics.equals(_existingCircuitBreakerMetrics)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |circuit_breaker_metrics(com.rio.rostry.data.database.entity.CircuitBreakerMetricsEntity).
              | Expected:
              |""".trimMargin() + _infoCircuitBreakerMetrics + """
              |
              | Found:
              |""".trimMargin() + _existingCircuitBreakerMetrics)
        }
        val _columnsMediaMetadata: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsMediaMetadata.put("mediaId", TableInfo.Column("mediaId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("originalUrl", TableInfo.Column("originalUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("thumbnailUrl", TableInfo.Column("thumbnailUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("width", TableInfo.Column("width", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("height", TableInfo.Column("height", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("sizeBytes", TableInfo.Column("sizeBytes", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("format", TableInfo.Column("format", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("duration", TableInfo.Column("duration", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("compressionQuality", TableInfo.Column("compressionQuality",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsMediaMetadata.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysMediaMetadata: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysMediaMetadata.add(TableInfo.ForeignKey("media_items", "CASCADE", "NO ACTION",
            listOf("mediaId"), listOf("mediaId")))
        val _indicesMediaMetadata: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesMediaMetadata.add(TableInfo.Index("index_media_metadata_mediaId", false,
            listOf("mediaId"), listOf("ASC")))
        val _infoMediaMetadata: TableInfo = TableInfo("media_metadata", _columnsMediaMetadata,
            _foreignKeysMediaMetadata, _indicesMediaMetadata)
        val _existingMediaMetadata: TableInfo = tableInfoRead(connection, "media_metadata")
        if (!_infoMediaMetadata.equals(_existingMediaMetadata)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |media_metadata(com.rio.rostry.data.database.entity.MediaMetadataEntity).
              | Expected:
              |""".trimMargin() + _infoMediaMetadata + """
              |
              | Found:
              |""".trimMargin() + _existingMediaMetadata)
        }
        val _columnsHubAssignments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHubAssignments.put("product_id", TableInfo.Column("product_id", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHubAssignments.put("hub_id", TableInfo.Column("hub_id", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHubAssignments.put("distance_km", TableInfo.Column("distance_km", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHubAssignments.put("assigned_at", TableInfo.Column("assigned_at", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHubAssignments.put("seller_location_lat", TableInfo.Column("seller_location_lat",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHubAssignments.put("seller_location_lon", TableInfo.Column("seller_location_lon",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHubAssignments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysHubAssignments.add(TableInfo.ForeignKey("products", "CASCADE", "NO ACTION",
            listOf("product_id"), listOf("productId")))
        val _indicesHubAssignments: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesHubAssignments.add(TableInfo.Index("index_hub_assignments_product_id", true,
            listOf("product_id"), listOf("ASC")))
        _indicesHubAssignments.add(TableInfo.Index("index_hub_assignments_hub_id", false,
            listOf("hub_id"), listOf("ASC")))
        val _infoHubAssignments: TableInfo = TableInfo("hub_assignments", _columnsHubAssignments,
            _foreignKeysHubAssignments, _indicesHubAssignments)
        val _existingHubAssignments: TableInfo = tableInfoRead(connection, "hub_assignments")
        if (!_infoHubAssignments.equals(_existingHubAssignments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |hub_assignments(com.rio.rostry.data.database.entity.HubAssignmentEntity).
              | Expected:
              |""".trimMargin() + _infoHubAssignments + """
              |
              | Found:
              |""".trimMargin() + _existingHubAssignments)
        }
        val _columnsProfitabilityMetrics: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProfitabilityMetrics.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("entity_id", TableInfo.Column("entity_id", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("entity_type", TableInfo.Column("entity_type", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("period_start", TableInfo.Column("period_start", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("period_end", TableInfo.Column("period_end", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("revenue", TableInfo.Column("revenue", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("costs", TableInfo.Column("costs", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("profit", TableInfo.Column("profit", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("profit_margin", TableInfo.Column("profit_margin", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("order_count", TableInfo.Column("order_count", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProfitabilityMetrics.put("calculated_at", TableInfo.Column("calculated_at",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProfitabilityMetrics: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProfitabilityMetrics: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesProfitabilityMetrics.add(TableInfo.Index("index_profitability_metrics_entity_id_entity_type",
            false, listOf("entity_id", "entity_type"), listOf("ASC", "ASC")))
        _indicesProfitabilityMetrics.add(TableInfo.Index("index_profitability_metrics_period_start_period_end",
            false, listOf("period_start", "period_end"), listOf("ASC", "ASC")))
        val _infoProfitabilityMetrics: TableInfo = TableInfo("profitability_metrics",
            _columnsProfitabilityMetrics, _foreignKeysProfitabilityMetrics,
            _indicesProfitabilityMetrics)
        val _existingProfitabilityMetrics: TableInfo = tableInfoRead(connection,
            "profitability_metrics")
        if (!_infoProfitabilityMetrics.equals(_existingProfitabilityMetrics)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |profitability_metrics(com.rio.rostry.data.database.entity.ProfitabilityMetricsEntity).
              | Expected:
              |""".trimMargin() + _infoProfitabilityMetrics + """
              |
              | Found:
              |""".trimMargin() + _existingProfitabilityMetrics)
        }
        val _columnsModerationBlocklist: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsModerationBlocklist.put("term", TableInfo.Column("term", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationBlocklist.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsModerationBlocklist.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysModerationBlocklist: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesModerationBlocklist: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoModerationBlocklist: TableInfo = TableInfo("moderation_blocklist",
            _columnsModerationBlocklist, _foreignKeysModerationBlocklist,
            _indicesModerationBlocklist)
        val _existingModerationBlocklist: TableInfo = tableInfoRead(connection,
            "moderation_blocklist")
        if (!_infoModerationBlocklist.equals(_existingModerationBlocklist)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |moderation_blocklist(com.rio.rostry.data.database.entity.ModerationBlocklistEntity).
              | Expected:
              |""".trimMargin() + _infoModerationBlocklist + """
              |
              | Found:
              |""".trimMargin() + _existingModerationBlocklist)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    _shadowTablesMap.put("products_fts", "products_fts_content")
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "products",
        "products_fts", "orders", "order_items", "transfers", "coins", "notifications", "alerts",
        "product_tracking", "family_tree", "chat_messages", "sync_state", "auctions", "bids",
        "cart_items", "wishlist", "payments", "coin_ledger", "delivery_hubs",
        "order_tracking_events", "invoices", "invoice_lines", "refunds", "breeding_records",
        "traits", "product_traits", "lifecycle_events", "transfer_verifications", "disputes",
        "audit_logs", "admin_audit_logs", "posts", "comments", "likes", "follows", "groups",
        "group_members", "events", "expert_bookings", "moderation_reports", "badges", "reputation",
        "outgoing_messages", "rate_limits", "event_rsvps", "analytics_daily", "reports", "stories",
        "growth_records", "quarantine_records", "mortality_records", "vaccination_records",
        "hatching_batches", "hatching_logs", "achievements_def", "user_progress", "badges_def",
        "leaderboard", "rewards_def", "thread_metadata", "community_recommendations",
        "user_interests", "expert_profiles", "outbox", "breeding_pairs", "farm_alerts",
        "listing_drafts", "farmer_dashboard_snapshots", "mating_logs", "egg_collections",
        "enthusiast_dashboard_snapshots", "upload_tasks", "daily_logs", "tasks", "breeds",
        "farm_verifications", "farm_assets", "farm_inventory", "market_listings", "reviews",
        "review_helpful", "rating_stats", "order_evidence", "order_quotes", "order_payments",
        "delivery_confirmations", "order_disputes", "order_audit_logs", "verification_drafts",
        "role_migrations", "storage_quota", "daily_bird_logs", "competitions", "my_votes",
        "batch_summaries", "dashboard_cache", "show_records", "verification_requests",
        "farm_activity_logs", "farm_profiles", "farm_timeline_events", "enthusiast_verifications",
        "farm_events", "role_upgrade_requests", "transactions", "expenses", "medical_events",
        "clutches", "bird_trait_records", "breeding_plans", "arena_participants", "digital_twins",
        "bird_events", "asset_lifecycle_events", "asset_health_records", "task_recurrences",
        "asset_batch_operations", "compliance_rules", "media_items", "media_tags",
        "media_cache_metadata", "gallery_filter_state", "error_logs", "configuration_cache",
        "circuit_breaker_metrics", "media_metadata", "hub_assignments", "profitability_metrics",
        "moderation_blocklist")
  }

  public override fun clearAllTables() {
    super.performClear(true, "users", "products", "products_fts", "orders", "order_items",
        "transfers", "coins", "notifications", "alerts", "product_tracking", "family_tree",
        "chat_messages", "sync_state", "auctions", "bids", "cart_items", "wishlist", "payments",
        "coin_ledger", "delivery_hubs", "order_tracking_events", "invoices", "invoice_lines",
        "refunds", "breeding_records", "traits", "product_traits", "lifecycle_events",
        "transfer_verifications", "disputes", "audit_logs", "admin_audit_logs", "posts", "comments",
        "likes", "follows", "groups", "group_members", "events", "expert_bookings",
        "moderation_reports", "badges", "reputation", "outgoing_messages", "rate_limits",
        "event_rsvps", "analytics_daily", "reports", "stories", "growth_records",
        "quarantine_records", "mortality_records", "vaccination_records", "hatching_batches",
        "hatching_logs", "achievements_def", "user_progress", "badges_def", "leaderboard",
        "rewards_def", "thread_metadata", "community_recommendations", "user_interests",
        "expert_profiles", "outbox", "breeding_pairs", "farm_alerts", "listing_drafts",
        "farmer_dashboard_snapshots", "mating_logs", "egg_collections",
        "enthusiast_dashboard_snapshots", "upload_tasks", "daily_logs", "tasks", "breeds",
        "farm_verifications", "farm_assets", "farm_inventory", "market_listings", "reviews",
        "review_helpful", "rating_stats", "order_evidence", "order_quotes", "order_payments",
        "delivery_confirmations", "order_disputes", "order_audit_logs", "verification_drafts",
        "role_migrations", "storage_quota", "daily_bird_logs", "competitions", "my_votes",
        "batch_summaries", "dashboard_cache", "show_records", "verification_requests",
        "farm_activity_logs", "farm_profiles", "farm_timeline_events", "enthusiast_verifications",
        "farm_events", "role_upgrade_requests", "transactions", "expenses", "medical_events",
        "clutches", "bird_trait_records", "breeding_plans", "arena_participants", "digital_twins",
        "bird_events", "asset_lifecycle_events", "asset_health_records", "task_recurrences",
        "asset_batch_operations", "compliance_rules", "media_items", "media_tags",
        "media_cache_metadata", "gallery_filter_state", "error_logs", "configuration_cache",
        "circuit_breaker_metrics", "media_metadata", "hub_assignments", "profitability_metrics",
        "moderation_blocklist")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UserDao::class, UserDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProductDao::class, ProductDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderDao::class, OrderDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransferDao::class, TransferDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CoinDao::class, CoinDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(NotificationDao::class, NotificationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AlertDao::class, AlertDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransactionDao::class, TransactionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProductTrackingDao::class,
        ProductTrackingDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FamilyTreeDao::class, FamilyTreeDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChatMessageDao::class, ChatMessageDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SyncStateDao::class, SyncStateDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmVerificationDao::class,
        FarmVerificationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EnthusiastVerificationDao::class,
        EnthusiastVerificationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AuctionDao::class, AuctionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BidDao::class, BidDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CartDao::class, CartDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WishlistDao::class, WishlistDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PaymentDao::class, PaymentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CoinLedgerDao::class, CoinLedgerDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DeliveryHubDao::class, DeliveryHubDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderTrackingEventDao::class,
        OrderTrackingEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(InvoiceDao::class, InvoiceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RefundDao::class, RefundDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BreedingRecordDao::class, BreedingRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TraitDao::class, TraitDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProductTraitDao::class, ProductTraitDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(LifecycleEventDao::class, LifecycleEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TransferVerificationDao::class,
        TransferVerificationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DisputeDao::class, DisputeDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AuditLogDao::class, AuditLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AdminAuditDao::class, AdminAuditDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RoleUpgradeRequestDao::class,
        RoleUpgradeRequestDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmAssetDao::class, FarmAssetDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(InventoryItemDao::class, InventoryItemDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MarketListingDao::class, MarketListingDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExpenseDao::class, ExpenseDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GrowthRecordDao::class, GrowthRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(QuarantineRecordDao::class,
        QuarantineRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MortalityRecordDao::class,
        MortalityRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VaccinationRecordDao::class,
        VaccinationRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HatchingBatchDao::class, HatchingBatchDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HatchingLogDao::class, HatchingLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PostsDao::class, PostsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CommentsDao::class, CommentsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(LikesDao::class, LikesDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FollowsDao::class, FollowsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GroupsDao::class, GroupsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GroupMembersDao::class, GroupMembersDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EventsDao::class, EventsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExpertBookingsDao::class, ExpertBookingsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ModerationReportsDao::class,
        ModerationReportsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BadgesDao::class, BadgesDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReputationDao::class, ReputationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OutgoingMessageDao::class,
        OutgoingMessageDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RateLimitDao::class, RateLimitDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EventRsvpsDao::class, EventRsvpsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AnalyticsDao::class, AnalyticsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReportsDao::class, ReportsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(StoriesDao::class, StoriesDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReviewDao::class, ReviewDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderEvidenceDao::class, OrderEvidenceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderQuoteDao::class, OrderQuoteDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderPaymentDao::class, OrderPaymentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DeliveryConfirmationDao::class,
        DeliveryConfirmationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderDisputeDao::class, OrderDisputeDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OrderAuditLogDao::class, OrderAuditLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AchievementDao::class, AchievementDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserProgressDao::class, UserProgressDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BadgeDefDao::class, BadgeDefDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(LeaderboardDao::class, LeaderboardDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RewardDefDao::class, RewardDefDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ThreadMetadataDao::class, ThreadMetadataDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CommunityRecommendationDao::class,
        CommunityRecommendationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UserInterestDao::class, UserInterestDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ExpertProfileDao::class, ExpertProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(OutboxDao::class, OutboxDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BreedingPairDao::class, BreedingPairDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmAlertDao::class, FarmAlertDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ListingDraftDao::class, ListingDraftDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmerDashboardSnapshotDao::class,
        FarmerDashboardSnapshotDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MatingLogDao::class, MatingLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EggCollectionDao::class, EggCollectionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EnthusiastDashboardSnapshotDao::class,
        EnthusiastDashboardSnapshotDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(UploadTaskDao::class, UploadTaskDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VerificationDraftDao::class,
        VerificationDraftDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RoleMigrationDao::class, RoleMigrationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(StorageQuotaDao::class, StorageQuotaDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DailyLogDao::class, DailyLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TaskDao::class, TaskDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BreedDao::class, BreedDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DailyBirdLogDao::class, DailyBirdLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VirtualArenaDao::class, VirtualArenaDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BatchSummaryDao::class, BatchSummaryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DashboardCacheDao::class, DashboardCacheDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ShowRecordDao::class, ShowRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(VerificationRequestDao::class,
        VerificationRequestDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmActivityLogDao::class,
        FarmActivityLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmEventDao::class, FarmEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmProfileDao::class, FarmProfileDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmTimelineEventDao::class,
        FarmTimelineEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MedicalEventDao::class, MedicalEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ClutchDao::class, ClutchDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BirdTraitRecordDao::class,
        BirdTraitRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BreedingPlanDao::class, BreedingPlanDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ArenaParticipantDao::class,
        ArenaParticipantDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DigitalTwinDao::class, DigitalTwinDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BirdEventDao::class, BirdEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AssetLifecycleEventDao::class,
        AssetLifecycleEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AssetHealthRecordDao::class,
        AssetHealthRecordDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(TaskRecurrenceDao::class, TaskRecurrenceDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AssetBatchOperationDao::class,
        AssetBatchOperationDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ComplianceRuleDao::class, ComplianceRuleDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MediaItemDao::class, MediaItemDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MediaTagDao::class, MediaTagDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MediaCacheMetadataDao::class,
        MediaCacheMetadataDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(GalleryFilterStateDao::class,
        GalleryFilterStateDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ErrorLogDao::class, ErrorLogDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ConfigurationCacheDao::class,
        ConfigurationCacheDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(CircuitBreakerMetricsDao::class,
        CircuitBreakerMetricsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(MediaMetadataDao::class, MediaMetadataDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HubAssignmentDao::class, HubAssignmentDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ProfitabilityMetricsDao::class,
        ProfitabilityMetricsDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ReferentialIntegrityDao::class,
        ReferentialIntegrityDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ModerationBlocklistDao::class,
        ModerationBlocklistDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun userDao(): UserDao = _userDao.value

  public override fun productDao(): ProductDao = _productDao.value

  public override fun orderDao(): OrderDao = _orderDao.value

  public override fun transferDao(): TransferDao = _transferDao.value

  public override fun coinDao(): CoinDao = _coinDao.value

  public override fun notificationDao(): NotificationDao = _notificationDao.value

  public override fun alertDao(): AlertDao = _alertDao.value

  public override fun transactionDao(): TransactionDao = _transactionDao.value

  public override fun productTrackingDao(): ProductTrackingDao = _productTrackingDao.value

  public override fun familyTreeDao(): FamilyTreeDao = _familyTreeDao.value

  public override fun chatMessageDao(): ChatMessageDao = _chatMessageDao.value

  public override fun syncStateDao(): SyncStateDao = _syncStateDao.value

  public override fun farmVerificationDao(): FarmVerificationDao = _farmVerificationDao.value

  public override fun enthusiastVerificationDao(): EnthusiastVerificationDao =
      _enthusiastVerificationDao.value

  public override fun auctionDao(): AuctionDao = _auctionDao.value

  public override fun bidDao(): BidDao = _bidDao.value

  public override fun cartDao(): CartDao = _cartDao.value

  public override fun wishlistDao(): WishlistDao = _wishlistDao.value

  public override fun paymentDao(): PaymentDao = _paymentDao.value

  public override fun coinLedgerDao(): CoinLedgerDao = _coinLedgerDao.value

  public override fun deliveryHubDao(): DeliveryHubDao = _deliveryHubDao.value

  public override fun orderTrackingEventDao(): OrderTrackingEventDao = _orderTrackingEventDao.value

  public override fun invoiceDao(): InvoiceDao = _invoiceDao.value

  public override fun refundDao(): RefundDao = _refundDao.value

  public override fun breedingRecordDao(): BreedingRecordDao = _breedingRecordDao.value

  public override fun traitDao(): TraitDao = _traitDao.value

  public override fun productTraitDao(): ProductTraitDao = _productTraitDao.value

  public override fun lifecycleEventDao(): LifecycleEventDao = _lifecycleEventDao.value

  public override fun transferVerificationDao(): TransferVerificationDao =
      _transferVerificationDao.value

  public override fun disputeDao(): DisputeDao = _disputeDao.value

  public override fun auditLogDao(): AuditLogDao = _auditLogDao.value

  public override fun adminAuditDao(): AdminAuditDao = _adminAuditDao.value

  public override fun roleUpgradeRequestDao(): RoleUpgradeRequestDao = _roleUpgradeRequestDao.value

  public override fun farmAssetDao(): FarmAssetDao = _farmAssetDao.value

  public override fun inventoryItemDao(): InventoryItemDao = _inventoryItemDao.value

  public override fun marketListingDao(): MarketListingDao = _marketListingDao.value

  public override fun expenseDao(): ExpenseDao = _expenseDao.value

  public override fun growthRecordDao(): GrowthRecordDao = _growthRecordDao.value

  public override fun quarantineRecordDao(): QuarantineRecordDao = _quarantineRecordDao.value

  public override fun mortalityRecordDao(): MortalityRecordDao = _mortalityRecordDao.value

  public override fun vaccinationRecordDao(): VaccinationRecordDao = _vaccinationRecordDao.value

  public override fun hatchingBatchDao(): HatchingBatchDao = _hatchingBatchDao.value

  public override fun hatchingLogDao(): HatchingLogDao = _hatchingLogDao.value

  public override fun postsDao(): PostsDao = _postsDao.value

  public override fun commentsDao(): CommentsDao = _commentsDao.value

  public override fun likesDao(): LikesDao = _likesDao.value

  public override fun followsDao(): FollowsDao = _followsDao.value

  public override fun groupsDao(): GroupsDao = _groupsDao.value

  public override fun groupMembersDao(): GroupMembersDao = _groupMembersDao.value

  public override fun eventsDao(): EventsDao = _eventsDao.value

  public override fun expertBookingsDao(): ExpertBookingsDao = _expertBookingsDao.value

  public override fun moderationReportsDao(): ModerationReportsDao = _moderationReportsDao.value

  public override fun badgesDao(): BadgesDao = _badgesDao.value

  public override fun reputationDao(): ReputationDao = _reputationDao.value

  public override fun outgoingMessageDao(): OutgoingMessageDao = _outgoingMessageDao.value

  public override fun rateLimitDao(): RateLimitDao = _rateLimitDao.value

  public override fun eventRsvpsDao(): EventRsvpsDao = _eventRsvpsDao.value

  public override fun analyticsDao(): AnalyticsDao = _analyticsDao.value

  public override fun reportsDao(): ReportsDao = _reportsDao.value

  public override fun storiesDao(): StoriesDao = _storiesDao.value

  public override fun reviewDao(): ReviewDao = _reviewDao.value

  public override fun orderEvidenceDao(): OrderEvidenceDao = _orderEvidenceDao.value

  public override fun orderQuoteDao(): OrderQuoteDao = _orderQuoteDao.value

  public override fun orderPaymentDao(): OrderPaymentDao = _orderPaymentDao.value

  public override fun deliveryConfirmationDao(): DeliveryConfirmationDao =
      _deliveryConfirmationDao.value

  public override fun orderDisputeDao(): OrderDisputeDao = _orderDisputeDao.value

  public override fun orderAuditLogDao(): OrderAuditLogDao = _orderAuditLogDao.value

  public override fun achievementsDefDao(): AchievementDao = _achievementDao.value

  public override fun userProgressDao(): UserProgressDao = _userProgressDao.value

  public override fun badgesDefDao(): BadgeDefDao = _badgeDefDao.value

  public override fun leaderboardDao(): LeaderboardDao = _leaderboardDao.value

  public override fun rewardsDefDao(): RewardDefDao = _rewardDefDao.value

  public override fun threadMetadataDao(): ThreadMetadataDao = _threadMetadataDao.value

  public override fun communityRecommendationDao(): CommunityRecommendationDao =
      _communityRecommendationDao.value

  public override fun userInterestDao(): UserInterestDao = _userInterestDao.value

  public override fun expertProfileDao(): ExpertProfileDao = _expertProfileDao.value

  public override fun outboxDao(): OutboxDao = _outboxDao.value

  public override fun breedingPairDao(): BreedingPairDao = _breedingPairDao.value

  public override fun farmAlertDao(): FarmAlertDao = _farmAlertDao.value

  public override fun listingDraftDao(): ListingDraftDao = _listingDraftDao.value

  public override fun farmerDashboardSnapshotDao(): FarmerDashboardSnapshotDao =
      _farmerDashboardSnapshotDao.value

  public override fun matingLogDao(): MatingLogDao = _matingLogDao.value

  public override fun eggCollectionDao(): EggCollectionDao = _eggCollectionDao.value

  public override fun enthusiastDashboardSnapshotDao(): EnthusiastDashboardSnapshotDao =
      _enthusiastDashboardSnapshotDao.value

  public override fun uploadTaskDao(): UploadTaskDao = _uploadTaskDao.value

  public override fun verificationDraftDao(): VerificationDraftDao = _verificationDraftDao.value

  public override fun roleMigrationDao(): RoleMigrationDao = _roleMigrationDao.value

  public override fun storageQuotaDao(): StorageQuotaDao = _storageQuotaDao.value

  public override fun dailyLogDao(): DailyLogDao = _dailyLogDao.value

  public override fun taskDao(): TaskDao = _taskDao.value

  public override fun breedDao(): BreedDao = _breedDao.value

  public override fun dailyBirdLogDao(): DailyBirdLogDao = _dailyBirdLogDao.value

  public override fun virtualArenaDao(): VirtualArenaDao = _virtualArenaDao.value

  public override fun batchSummaryDao(): BatchSummaryDao = _batchSummaryDao.value

  public override fun dashboardCacheDao(): DashboardCacheDao = _dashboardCacheDao.value

  public override fun showRecordDao(): ShowRecordDao = _showRecordDao.value

  public override fun verificationRequestDao(): VerificationRequestDao =
      _verificationRequestDao.value

  public override fun farmActivityLogDao(): FarmActivityLogDao = _farmActivityLogDao.value

  public override fun farmEventDao(): FarmEventDao = _farmEventDao.value

  public override fun farmProfileDao(): FarmProfileDao = _farmProfileDao.value

  public override fun farmTimelineEventDao(): FarmTimelineEventDao = _farmTimelineEventDao.value

  public override fun medicalEventDao(): MedicalEventDao = _medicalEventDao.value

  public override fun clutchDao(): ClutchDao = _clutchDao.value

  public override fun birdTraitRecordDao(): BirdTraitRecordDao = _birdTraitRecordDao.value

  public override fun breedingPlanDao(): BreedingPlanDao = _breedingPlanDao.value

  public override fun arenaParticipantDao(): ArenaParticipantDao = _arenaParticipantDao.value

  public override fun digitalTwinDao(): DigitalTwinDao = _digitalTwinDao.value

  public override fun birdEventDao(): BirdEventDao = _birdEventDao.value

  public override fun assetLifecycleEventDao(): AssetLifecycleEventDao =
      _assetLifecycleEventDao.value

  public override fun assetHealthRecordDao(): AssetHealthRecordDao = _assetHealthRecordDao.value

  public override fun taskRecurrenceDao(): TaskRecurrenceDao = _taskRecurrenceDao.value

  public override fun assetBatchOperationDao(): AssetBatchOperationDao =
      _assetBatchOperationDao.value

  public override fun complianceRuleDao(): ComplianceRuleDao = _complianceRuleDao.value

  public override fun mediaItemDao(): MediaItemDao = _mediaItemDao.value

  public override fun mediaTagDao(): MediaTagDao = _mediaTagDao.value

  public override fun mediaCacheMetadataDao(): MediaCacheMetadataDao = _mediaCacheMetadataDao.value

  public override fun galleryFilterStateDao(): GalleryFilterStateDao = _galleryFilterStateDao.value

  public override fun errorLogDao(): ErrorLogDao = _errorLogDao.value

  public override fun configurationCacheDao(): ConfigurationCacheDao = _configurationCacheDao.value

  public override fun circuitBreakerMetricsDao(): CircuitBreakerMetricsDao =
      _circuitBreakerMetricsDao.value

  public override fun mediaMetadataDao(): MediaMetadataDao = _mediaMetadataDao.value

  public override fun hubAssignmentDao(): HubAssignmentDao = _hubAssignmentDao.value

  public override fun profitabilityMetricsDao(): ProfitabilityMetricsDao =
      _profitabilityMetricsDao.value

  public override fun referentialIntegrityDao(): ReferentialIntegrityDao =
      _referentialIntegrityDao.value

  public override fun moderationBlocklistDao(): ModerationBlocklistDao =
      _moderationBlocklistDao.value
}
