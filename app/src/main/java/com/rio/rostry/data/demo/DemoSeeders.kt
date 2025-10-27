package com.rio.rostry.data.demo

import com.rio.rostry.data.database.dao.*
import com.rio.rostry.data.database.entity.*
import com.rio.rostry.domain.model.UserType
import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import kotlin.random.Random
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

@Singleton
class DemoSeeders @Inject constructor(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val transferDao: TransferDao,
    private val postsDao: PostsDao,
    // Marketplace
    private val orderDao: OrderDao,
    private val cartDao: CartDao,
    private val wishlistDao: WishlistDao,
    private val auctionDao: AuctionDao,
    private val bidDao: BidDao,
    private val invoiceDao: InvoiceDao,
    private val orderTrackingEventDao: OrderTrackingEventDao,
    private val paymentDao: PaymentDao,
    // Farm monitoring
    private val dailyLogDao: DailyLogDao,
    private val taskDao: TaskDao,
    private val growthRecordDao: GrowthRecordDao,
    private val quarantineRecordDao: QuarantineRecordDao,
    private val mortalityRecordDao: MortalityRecordDao,
    private val vaccinationRecordDao: VaccinationRecordDao,
    private val hatchingBatchDao: HatchingBatchDao,
    private val breedingPairDao: BreedingPairDao,
    // Gamification
    private val userProgressDao: UserProgressDao,
    private val coinDao: CoinDao,
    private val coinLedgerDao: CoinLedgerDao,
    private val leaderboardDao: LeaderboardDao,
    // Social
    private val commentDao: CommentsDao,
    private val likeDao: LikesDao,
    private val followDao: FollowsDao,
    private val groupDao: GroupsDao,
    private val groupMemberDao: GroupMembersDao,
    private val eventDao: EventsDao,
    private val eventRsvpDao: EventRsvpsDao,
    private val chatMessageDao: ChatMessageDao,
    private val expertBookingDao: ExpertBookingsDao,
    // Traceability
    private val familyTreeDao: FamilyTreeDao,
    private val lifecycleEventDao: LifecycleEventDao,
    // Breeding
    private val matingLogDao: MatingLogDao,
    private val eggCollectionDao: EggCollectionDao,
    // Notifications
    private val notificationDao: NotificationDao,
    private val analyticsDao: AnalyticsDao,
    private val farmerDashboardSnapshotDao: FarmerDashboardSnapshotDao,
    private val reportsDao: ReportsDao
) {

    suspend fun seedProfile(profile: DemoUserProfile) {
        val now = System.currentTimeMillis()
        
        // 1. Seed user (existing)
        val ownerEntity = profile.toUserEntity(now)
        userDao.insertUser(ownerEntity)
        
        // Ensure counterpart users exist before products
        val counterpartSeller = UserEntity(userId = "demo-market-seller", fullName = "Demo Market Seller", createdAt = now - 86_400_000, updatedAt = now)
        val counterpartBuyer = UserEntity(userId = "demo-market-buyer", fullName = "Demo Market Buyer", createdAt = now - 86_400_000, updatedAt = now)
        userDao.upsertUsers(listOf(counterpartSeller, counterpartBuyer))
        
        // 2. Seed products (existing)
        val productEntities = profile.productListings.mapIndexed { index, demoProduct ->
            demoProduct.toProductEntity(owner = profile, index = index, now = now)
        }
        if (productEntities.isNotEmpty()) {
            productDao.insertProducts(productEntities)
        }

        // Build product resolvers (ids or names)
        val byId: Map<String, String> = productEntities.associate { it.productId to it.productId }
        val byName: Map<String, String> = productEntities.associate { it.name to it.productId }
        fun resolveProductId(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            return when {
                byId.containsKey(raw) -> raw
                byName.containsKey(raw) -> byName[raw]
                else -> null
            }
        }
        
        // 3. Seed counterpart users (existing)
        val counterpartUsers = mutableMapOf<String, UserEntity>()
        profile.transactions.forEach { txn ->
            val counterpart = txn.toCounterpartUser(now)
            counterpartUsers.putIfAbsent(counterpart.userId, counterpart)
        }
        profile.socialConnections.forEach { conn ->
            val counterpart = conn.toCounterpartUser(now)
            counterpartUsers.putIfAbsent(counterpart.userId, counterpart)
        }
        if (counterpartUsers.isNotEmpty()) {
            val safeUsers = counterpartUsers.values
                .filter { it.userId.isNotBlank() }
                .distinctBy { it.userId }
            if (safeUsers.isNotEmpty()) userDao.upsertUsers(safeUsers)
        }
        
        // 4. Seed transfers (existing)
        val transfers: List<TransferEntity> = profile.transactions.mapIndexed { index, demoTxn ->
            val productId = if (productEntities.isNotEmpty()) {
                productEntities[index % productEntities.size].productId
            } else {
                null
            }
            val counterpart = demoTxn.toCounterpartUser(now)
            val counterpartUserId = counterpartUsers[counterpart.userId]?.userId ?: counterpart.userId
            demoTxn.toTransferEntity(
                owner = profile,
                productId = productId,
                counterpartUserId = counterpartUserId,
                index = index,
                now = now
            )
        }
        transfers.forEach { transferDao.upsert(it) }
        
        // 5. Seed posts (existing) and retain IDs for FK integrity
        val seededPosts: List<PostEntity> = profile.socialConnections.mapIndexed { index, connection ->
            connection.toPostEntity(profile, index, now)
        }
        seededPosts.forEach { postsDao.upsert(it) }
        
        // 6. NEW: Seed marketplace commerce entities
        seedMarketplaceData(
            profile = profile,
            products = productEntities,
            now = now,
            resolveProductId = ::resolveProductId,
            counterpartBuyerId = counterpartBuyer.userId,
            counterpartSellerId = counterpartSeller.userId
        )
        
        // 7. NEW: Seed farm monitoring entities (for farmers)
        if (profile.role == UserType.FARMER) {
            seedFarmMonitoringData(profile, productEntities, now, ::resolveProductId)
        }
        
        // 8. NEW: Seed gamification entities (for enthusiasts)
        if (profile.role == UserType.ENTHUSIAST) {
            seedGamificationData(profile, now)
        }
        
        // 9. NEW: Seed community/social entities
        seedCommunityData(profile, seededPosts, now)
        
        // 10. NEW: Seed traceability entities
        seedTraceabilityData(profile, productEntities, now, ::resolveProductId)
        
        // 11. NEW: Seed breeding entities (for enthusiasts/farmers when present)
        if (profile.breedingPairs.isNotEmpty() || profile.matingLogs.isNotEmpty() || profile.eggCollections.isNotEmpty()) {
            seedBreedingData(profile, productEntities, now, ::resolveProductId)
        }
        
        // 12. NEW: Seed notifications
        seedNotifications(profile, now)

        // 13. NEW: Seed analytics daily data
        seedAnalyticsDailyData(profile, now)

        // 13b. NEW: Seed downloadable reports for demo
        seedReports(profile, now)

        // 14. NEW: Seed farmer dashboard snapshots (only for farmers)
        if (profile.role == UserType.FARMER) {
            seedFarmerDashboardSnapshots(profile, now)
        }
    }

    private suspend fun seedMarketplaceData(
        profile: DemoUserProfile,
        products: List<ProductEntity>,
        now: Long,
        resolveProductId: (String?) -> String?,
        counterpartBuyerId: String,
        counterpartSellerId: String
    ) {
        val productMap = products.associateBy { it.productId }
        // Pre-seed all unique sellerIds from products that don't match profile.id
        val uniqueSellerIds = products.map { it.sellerId }.distinct().filter { it != profile.id }
        val missingSellers = mutableListOf<UserEntity>()
        for (sellerId in uniqueSellerIds) {
            try {
                val exists = userDao.getUserById(sellerId).firstOrNull() != null
                if (!exists) {
                    val placeholder = UserEntity(
                        userId = sellerId,
                        fullName = "Placeholder Seller $sellerId",
                        email = null,
                        createdAt = now,
                        updatedAt = now
                    )
                    missingSellers.add(placeholder)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to check existence of sellerId: $sellerId")
            }
        }
        if (missingSellers.isNotEmpty()) {
            try {
                userDao.upsertUsers(missingSellers)
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert missing sellers: ${missingSellers.map { it.userId }}")
            }
        }

        // Orders
        profile.orders.forEachIndexed { index, order ->
            // Build order items with price and unit from product snapshot
            var tempOrderEntity = order.toOrderEntity(profile, index, now)
            val firstRaw = order.items.firstOrNull()?.productId
            val firstResolved = resolveProductId(firstRaw)
            val firstProductSeller = firstResolved?.let { productMap[it]?.sellerId }

            tempOrderEntity = if (profile.role == UserType.FARMER) {
                tempOrderEntity.copy(sellerId = profile.id, buyerId = counterpartBuyerId)
            } else {
                val seller = firstProductSeller ?: counterpartSellerId
                tempOrderEntity.copy(sellerId = seller, buyerId = profile.id)
            }

            val items = order.items.mapNotNull { item ->
                val resolvedId = resolveProductId(item.productId)
                val p = resolvedId?.let { productMap[it] }
                p?.let {
                    OrderItemEntity(
                        orderId = tempOrderEntity.orderId,
                        productId = it.productId,
                        quantity = item.quantity.toDouble(),
                        priceAtPurchase = it.price,
                        unitAtPurchase = it.unit
                    )
                }
            }

            // Ensure at least one resolved item, else skip this order entirely
            if (items.isEmpty()) {
                return@forEachIndexed
            }

            // Persist order and items
            orderDao.insertOrUpdate(tempOrderEntity)
            orderDao.insertOrderItems(items)

            // Persist tracking events
            val tracking = order.toOrderTrackingEvents(tempOrderEntity.orderId, now)
            tracking.forEach { event -> orderTrackingEventDao.insert(event) }

            // Seed payment attributed to buyer, using the exact orderId
            val payment = PaymentEntity(
                paymentId = "demo-${profile.id}-payment-${tempOrderEntity.orderId}",
                orderId = tempOrderEntity.orderId,
                userId = tempOrderEntity.buyerId ?: profile.id,
                method = "UPI",
                amount = order.totalAmount.toDouble(),
                status = "SUCCESS",
                idempotencyKey = "demo-${profile.id}-${tempOrderEntity.orderId}-${now}"
            )
            paymentDao.insert(payment)

            // Minimal invoice seeding for consistency
            val invoice = InvoiceEntity(
                invoiceId = "demo-invoice-${tempOrderEntity.orderId}",
                orderId = tempOrderEntity.orderId,
                subtotal = order.totalAmount.toDouble(),
                gstPercent = 0.0,
                gstAmount = 0.0,
                total = order.totalAmount.toDouble(),
                createdAt = now
            )
            invoiceDao.insertInvoice(invoice)
        }
        // Cart
        profile.cartItems.forEach { item ->
            val resolved = resolveProductId(item.productId) ?: return@forEach
            val entity = item.toCartItemEntity(profile.id, now).copy(productId = resolved)
            cartDao.upsert(entity)
        }
        // Wishlist
        profile.wishlistItems.forEach { item ->
            val resolved = resolveProductId(item.productId) ?: return@forEach
            val entity = item.toWishlistEntity(profile.id).copy(productId = resolved)
            wishlistDao.upsert(entity)
        }
        // Auctions
        profile.auctions.forEachIndexed { index, auction ->
            val resolved = resolveProductId(auction.productId) ?: return@forEachIndexed
            var entity = auction.toAuctionEntity(profile, index, now).copy(productId = resolved)
            auctionDao.upsert(entity)
            var maxBid = entity.minPrice
            auction.bids.forEachIndexed { bidIndex, bid ->
                val bidEntity = bid.toBidEntity(entity.auctionId, profile.id, bidIndex, now)
                bidDao.insert(bidEntity)
                if (bid.amount.toDouble() > maxBid) maxBid = bid.amount.toDouble()
            }
            entity = entity.copy(currentPrice = maxBid)
            auctionDao.update(entity)
        }
        // Invoices: skipped (schema-sensitive). Payments are inserted per order in the loop above using actual orderId.
    }

    private suspend fun seedFarmMonitoringData(
        profile: DemoUserProfile,
        products: List<ProductEntity>,
        now: Long,
        resolveProductId: (String?) -> String?
    ) {
        profile.dailyLogs.forEachIndexed { index, log ->
            val pid = resolveProductId(log.productId) ?: return@forEachIndexed
            val entity = log.toDailyLogEntity(profile.id, pid, index, now)
            dailyLogDao.upsert(entity)
        }
        profile.tasks.forEachIndexed { index, task ->
            val entity = task.toTaskEntity(profile.id, index, now)
            taskDao.upsert(entity)
        }
        profile.growthRecords.forEachIndexed { index, record ->
            val pid = resolveProductId(record.productId) ?: return@forEachIndexed
            val entity = record.toGrowthRecordEntity(profile.id, pid, index, now)
            growthRecordDao.upsert(entity)
        }
        profile.quarantineRecords.forEachIndexed { index, record ->
            val pid = resolveProductId(record.productId) ?: return@forEachIndexed
            val entity = record.toQuarantineRecordEntity(profile.id, pid, index, now)
            quarantineRecordDao.upsert(entity)
        }
        profile.mortalityRecords.forEachIndexed { index, record ->
            val pid = resolveProductId(record.productId) ?: return@forEachIndexed
            val entity = record.toMortalityRecordEntity(profile.id, pid, index, now)
            mortalityRecordDao.upsert(entity)
        }
        profile.vaccinationRecords.forEachIndexed { index, record ->
            val pid = resolveProductId(record.productId) ?: return@forEachIndexed
            val entity = record.toVaccinationRecordEntity(profile.id, pid, index, now)
            vaccinationRecordDao.upsert(entity)
        }
        profile.hatchingBatches.forEachIndexed { index, batch ->
            val entity = batch.toHatchingBatchEntity(profile.id, index, now)
            hatchingBatchDao.upsert(entity)
            // No hatching logs are seeded at this time.
        }
    }

    private suspend fun seedGamificationData(profile: DemoUserProfile, now: Long) {
        profile.achievements.forEachIndexed { index, achievement ->
            val entity = achievement.toUserProgressEntity(profile.id, index, now)
            userProgressDao.upsert(entity)
        }
        // Only ledger-style seeding to avoid multiple CoinEntity rows per user
        profile.coinTransactions.forEachIndexed { index, transaction ->
            val ledger = transaction.toCoinLedgerEntity(profile.id, index, now)
            coinLedgerDao.insert(ledger)
            val coin = transaction.toCoinEntity(profile.id, index, now)
            coinDao.insertCoinTransaction(coin)
        }
        profile.leaderboardEntries.forEachIndexed { index, entry ->
            val entity = entry.toLeaderboardEntity(profile.id, "week", index)
            leaderboardDao.upsertAll(listOf(entity))
        }
        // Monthly leaderboard pass
        profile.leaderboardEntries.forEachIndexed { index, entry ->
            val entity = entry.toLeaderboardEntity(profile.id, "month", index)
            leaderboardDao.upsertAll(listOf(entity))
        }
    }

    private suspend fun seedCommunityData(profile: DemoUserProfile, seededPosts: List<PostEntity>, now: Long) {
        val ensuredPosts: List<PostEntity> = if (seededPosts.isNotEmpty()) {
            seededPosts
        } else {
            val defaultPost = PostEntity(
                postId = "demo-${profile.id}-post-default",
                authorId = profile.id,
                type = "TEXT",
                text = profile.statusNote ?: "Hello ROSTRY!",
                mediaUrl = null,
                thumbnailUrl = null,
                productId = null,
                createdAt = now,
                updatedAt = now
            )
            postsDao.upsert(defaultPost)
            listOf(defaultPost)
        }
        val postIds = ensuredPosts.map { it.postId }
        profile.comments.forEachIndexed { index, comment ->
            val targetPostId = postIds[index % postIds.size]
            val entity = comment.toCommentEntity(targetPostId, profile.id, index, now)
            commentDao.upsert(entity)
        }
        profile.likes.forEachIndexed { index, like ->
            val targetPostId = postIds[index % postIds.size]
            val entity = like.toLikeEntity(targetPostId, profile.id, index, now)
            likeDao.upsert(entity)
        }
        profile.follows.forEachIndexed { index, follow ->
            val entity = follow.toFollowEntity(profile.id, index, now)
            followDao.upsert(entity)
        }
        val seededGroups: List<GroupEntity> = profile.groups.mapIndexed { index, group ->
            group.toGroupEntity(profile.id, index, now)
        }
        seededGroups.forEachIndexed { idx, group ->
            groupDao.upsert(group)
            val member = GroupMemberEntity(
                membershipId = "demo-${group.groupId}-member-${idx + 1}",
                groupId = group.groupId,
                userId = profile.id,
                role = "MEMBER",
                joinedAt = now
            )
            groupMemberDao.upsert(member)
        }
        // If no groups, create a default one for event FK integrity
        val groupForEventsId: String = if (seededGroups.isNotEmpty()) seededGroups.first().groupId else run {
            val defaultGroup = GroupEntity(
                groupId = "demo-${profile.id}-group-default",
                name = "Demo Group",
                description = "Autocreated",
                ownerId = profile.id,
                category = "general",
                createdAt = now
            )
            groupDao.upsert(defaultGroup)
            val member = GroupMemberEntity(
                membershipId = "demo-${defaultGroup.groupId}-member-1",
                groupId = defaultGroup.groupId,
                userId = profile.id,
                role = "MEMBER",
                joinedAt = now
            )
            groupMemberDao.upsert(member)
            defaultGroup.groupId
        }
        profile.events.forEachIndexed { index, event ->
            val groupId = seededGroups.getOrNull(index % maxOf(1, seededGroups.size))?.groupId ?: groupForEventsId
            val entity = event.toEventEntity(groupId, index, now)
            eventDao.upsert(entity)
            val rsvp = event.toEventRsvpEntity(entity.eventId, profile.id, now)
            eventRsvpDao.upsert(rsvp)
        }
        // Ensure receivers exist before inserting chat messages
        val receivers = profile.chatMessages.map { it.receiverId }
            .filter { it.isNotBlank() }
            .distinct()
            .map { rid ->
                UserEntity(userId = rid, fullName = rid, createdAt = now - 86_400_000, updatedAt = now)
            }
        if (receivers.isNotEmpty()) userDao.upsertUsers(receivers)
        profile.chatMessages.forEachIndexed { index, message ->
            val entity = message.toChatMessageEntity(profile.id, index, now)
            chatMessageDao.upsert(entity)
        }
        profile.expertBookings.forEachIndexed { index, booking ->
            val entity = booking.toExpertBookingEntity(profile.id, index, now)
            expertBookingDao.upsert(entity)
        }
    }

    private suspend fun seedTraceabilityData(
        profile: DemoUserProfile,
        products: List<ProductEntity>,
        now: Long,
        resolveProductId: (String?) -> String?
    ) {
        profile.familyTreeNodes.forEachIndexed { index, node ->
            val pid = resolveProductId(node.productId)
            val parentPid = resolveProductId(node.parentProductId)
            val entity = FamilyTreeEntity(
                nodeId = "demo-${profile.id}-familytree-${index + 1}",
                productId = pid ?: return@forEachIndexed,
                parentProductId = parentPid,
                childProductId = null,
                relationType = node.relationType,
                createdAt = now - index * 60_000L,
                updatedAt = now - index * 60_000L,
                isDeleted = false,
                deletedAt = null
            )
            familyTreeDao.upsert(entity)
        }
        profile.lifecycleEvents.forEachIndexed { index, event ->
            val pid = resolveProductId(event.productId) ?: return@forEachIndexed
            val entity = event.toLifecycleEventEntity(profile.id, index, now).copy(productId = pid)
            lifecycleEventDao.insert(entity)
        }
    }

    private suspend fun seedBreedingData(
        profile: DemoUserProfile,
        products: List<ProductEntity>,
        now: Long,
        resolveProductId: (String?) -> String?
    ) {
        // 1) Seed breeding pairs first (parents) and collect only successfully inserted pairIds
        val insertedPairIds = mutableListOf<String>()
        profile.breedingPairs.forEachIndexed { index, pair ->
            val male = resolveProductId(pair.maleProductId) ?: return@forEachIndexed
            val female = resolveProductId(pair.femaleProductId) ?: return@forEachIndexed
            val entity = pair.toBreedingPairEntity(profile.id, index, now).copy(
                maleProductId = male,
                femaleProductId = female
            )
            breedingPairDao.upsert(entity)
            insertedPairIds.add(entity.pairId)
        }
        // Map provided pairId strings to one of the actually inserted pairIds
        fun mapToInsertedPairId(raw: String?): String? {
            if (raw.isNullOrBlank()) return null
            if (insertedPairIds.contains(raw)) return raw
            if (insertedPairIds.isEmpty()) return null
            val idx = kotlin.math.abs(raw.hashCode()) % insertedPairIds.size
            return insertedPairIds[idx]
        }
        // 2) Seed mating logs using mapped pairIds (skip if no pairs were inserted)
        profile.matingLogs.forEachIndexed { index, log ->
            val mapped = mapToInsertedPairId(log.pairId) ?: return@forEachIndexed
            val entity = log.toMatingLogEntity(profile.id, index, now).copy(pairId = mapped)
            matingLogDao.upsert(entity)
        }
        // 3) Seed egg collections using mapped pairIds
        profile.eggCollections.forEachIndexed { index, collection ->
            val mapped = mapToInsertedPairId(collection.pairId) ?: return@forEachIndexed
            val entity = collection.toEggCollectionEntity(profile.id, index, now).copy(pairId = mapped)
            eggCollectionDao.upsert(entity)
        }
    }

    private suspend fun seedNotifications(profile: DemoUserProfile, now: Long) {
        profile.notifications.forEachIndexed { index, notification ->
            val entity = notification.toNotificationEntity(profile.id, index, now)
            notificationDao.insertNotification(entity)
        }
    }

    private suspend fun seedAnalyticsDailyData(profile: DemoUserProfile, now: Long) {
        val today = LocalDate.now()
        val totalLikes = profile.likes.size
        val totalComments = profile.comments.size
        val totalTransfers = profile.transactions.size
        val breedingSuccessRate = if (profile.role == UserType.ENTHUSIAST) {
            // Demo: approximate success rate in a reasonable band
            Random.nextDouble(0.65, 0.85)
        } else 0.0
        for (i in 0..29) {
            val date = today.minusDays(i.toLong())
            val dateKey = date.toString()
            // Demo orders have no timestamps; aggregate from all
            val salesRevenue = profile.orders.sumOf { it.totalAmount.toDouble() }
            val ordersCount = profile.orders.size
            val productViews = ordersCount * 15 + Random.nextInt(10, 31)
            val likesCount = (totalLikes / 30) + Random.nextInt(-2, 3).coerceAtLeast(0)
            val commentsCount = (totalComments / 30) + Random.nextInt(-2, 3).coerceAtLeast(0)
            val transfersCount = (totalTransfers / 30) + Random.nextInt(-2, 3).coerceAtLeast(0)
            val engagementScore = likesCount + commentsCount * 2.0
            val entity = AnalyticsDailyEntity(
                id = "demo-analytics-${profile.id}-${dateKey}",
                userId = profile.id,
                role = profile.role.name,
                dateKey = dateKey,
                salesRevenue = salesRevenue,
                ordersCount = ordersCount,
                productViews = productViews,
                likesCount = likesCount,
                commentsCount = commentsCount,
                transfersCount = transfersCount,
                breedingSuccessRate = breedingSuccessRate,
                engagementScore = engagementScore,
                createdAt = now
            )
            analyticsDao.upsertDaily(entity)
        }
    }

    private suspend fun seedFarmerDashboardSnapshots(profile: DemoUserProfile, now: Long) {
        val today = LocalDate.now()
        for (weekIndex in 0..3) {
            val weekStartDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(weekIndex.toLong())
            val weekEndDate = weekStartDate.plusDays(6)
            val weekStartAt = weekStartDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val weekEndAt = weekEndDate.atTime(23, 59, 59, 999999999).toInstant(ZoneOffset.UTC).toEpochMilli()
            // Demo: aggregate from all entries due to lack of per-week timestamps
            val revenueInr = profile.orders.sumOf { it.totalAmount.toDouble() }
            val ordersCount = profile.orders.size
            val totalEggs = profile.hatchingBatches.sumOf { it.eggsCount }
            val hatchSuccessRate = if (totalEggs > 0) Random.nextDouble(0.7, 0.9) else Random.nextDouble(0.7, 0.9)
            val deathsCount = profile.mortalityRecords.size
            val mortalityRate = if (deathsCount > 0) Random.nextDouble(0.02, 0.08) else 0.02
            val vaccinationCompletionRate = if (profile.vaccinationRecords.isNotEmpty()) Random.nextDouble(0.8, 0.95) else 0.8
            val growthRecordsCount = profile.growthRecords.size
            val quarantineActiveCount = profile.quarantineRecords.count { it.status.equals("ACTIVE", ignoreCase = true) }
            val dailyLogsOnWeek = profile.dailyLogs
            val avgFeedKg = if (dailyLogsOnWeek.isNotEmpty()) Random.nextDouble(2.5, 4.5) else Random.nextDouble(2.5, 4.5)
            val dailyLogComplianceRate = (dailyLogsOnWeek.size / 7.0).coerceIn(0.0, 1.0)
            val transfersInitiatedCount = profile.transactions.size
            val transfersCompletedCount = profile.transactions.count { it.status == "COMPLETED" }
            val snapshotId = "demo-snapshot-${profile.id}-week-${weekIndex}"
            val entity = FarmerDashboardSnapshotEntity(
                snapshotId = snapshotId,
                farmerId = profile.id,
                weekStartAt = weekStartAt,
                weekEndAt = weekEndAt,
                revenueInr = revenueInr,
                ordersCount = ordersCount,
                hatchSuccessRate = hatchSuccessRate,
                mortalityRate = mortalityRate,
                deathsCount = deathsCount,
                vaccinationCompletionRate = vaccinationCompletionRate,
                growthRecordsCount = growthRecordsCount,
                quarantineActiveCount = quarantineActiveCount,
                avgFeedKg = avgFeedKg,
                dailyLogComplianceRate = dailyLogComplianceRate,
                transfersInitiatedCount = transfersInitiatedCount,
                transfersCompletedCount = transfersCompletedCount,
                createdAt = now,
                dirty = false,
                syncedAt = null
            )
            farmerDashboardSnapshotDao.upsert(entity)
        }
    }

    private suspend fun seedReports(profile: DemoUserProfile, now: Long) {
        val today = LocalDate.now()
        // Weekly period: last Monday to Sunday
        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1)
        val weekEnd = weekStart.plusDays(6)
        val weekStartAt = weekStart.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val weekEndAt = weekEnd.atTime(23, 59, 59, 999_000_000).toInstant(ZoneOffset.UTC).toEpochMilli()
        val weekly = ReportEntity(
            reportId = "demo-${profile.id}-report-weekly",
            userId = profile.id,
            type = "WEEKLY",
            periodStart = weekStartAt,
            periodEnd = weekEndAt,
            format = "PDF",
            uri = null,
            createdAt = now
        )
        reportsDao.upsert(weekly)

        // Monthly period: previous calendar month
        val firstOfThisMonth = today.withDayOfMonth(1)
        val firstOfPrevMonth = firstOfThisMonth.minusMonths(1)
        val endOfPrevMonth = firstOfThisMonth.minusDays(1)
        val monthStartAt = firstOfPrevMonth.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val monthEndAt = endOfPrevMonth.atTime(23, 59, 59, 999_000_000).toInstant(ZoneOffset.UTC).toEpochMilli()
        val monthly = ReportEntity(
            reportId = "demo-${profile.id}-report-monthly",
            userId = profile.id,
            type = "MONTHLY",
            periodStart = monthStartAt,
            periodEnd = monthEndAt,
            format = "CSV",
            uri = null,
            createdAt = now
        )
        reportsDao.upsert(monthly)
    }
}
