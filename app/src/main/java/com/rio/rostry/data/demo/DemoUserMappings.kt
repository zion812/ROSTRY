package com.rio.rostry.data.demo

import com.rio.rostry.data.database.entity.*
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import java.util.Locale

private const val ONE_DAY_MILLIS = 24L * 60 * 60 * 1000

fun DemoUserProfile.toUserEntity(now: Long = System.currentTimeMillis()): UserEntity = UserEntity(
    userId = id,
    phoneNumber = phoneNumber,
    email = email,
    fullName = fullName,
    address = location,
    profilePictureUrl = null,
    userType = role,
    verificationStatus = when (role) {
        UserType.GENERAL -> VerificationStatus.PENDING
        UserType.FARMER, UserType.ENTHUSIAST -> VerificationStatus.VERIFIED
    },
    createdAt = now - ONE_DAY_MILLIS * 7,
    updatedAt = now
)

fun DemoProduct.toProductEntity(
    owner: DemoUserProfile,
    index: Int,
    now: Long = System.currentTimeMillis()
): ProductEntity {
    val id = "demo-${owner.id}-product-${index + 1}"
    val timestamp = now - index * (60_000L)
    return ProductEntity(
        productId = id,
        sellerId = owner.id,
        name = name,
        description = description,
        category = category,
        price = priceInr.toDouble(),
        quantity = 1.0,
        unit = "unit",
        location = owner.location,
        status = "available",
        createdAt = timestamp,
        updatedAt = timestamp,
        lastModifiedAt = timestamp,
        imageUrls = emptyList(),
        dirty = false
    )
}

fun DemoTransaction.toTransferEntity(
    owner: DemoUserProfile,
    productId: String?,
    counterpartUserId: String,
    index: Int,
    now: Long = System.currentTimeMillis()
): TransferEntity {
    val id = "demo-${owner.id}-transfer-${index + 1}"
    val initiated = now - index * (90_000L)
    val completed = if (status.uppercase(Locale.ENGLISH) in setOf("COMPLETED", "SETTLED")) initiated + 45_000L else null
    return TransferEntity(
        transferId = id,
        productId = productId,
        fromUserId = owner.id,
        toUserId = counterpartUserId,
        amount = amountInr.toDouble(),
        currency = "INR",
        type = "PAYMENT",
        status = status,
        transactionReference = reference,
        notes = "Counterpart: $counterpart — $summary",
        initiatedAt = initiated,
        completedAt = completed,
        updatedAt = completed ?: initiated,
        lastModifiedAt = completed ?: initiated,
        dirty = false
    )
}

fun DemoSocialConnection.toPostEntity(
    owner: DemoUserProfile,
    index: Int,
    now: Long = System.currentTimeMillis()
): PostEntity {
    val id = "demo-${owner.id}-post-${index + 1}"
    val created = now - index * (75_000L)
    val textContent = "Connection • ${name} (${relation}) – ${note}"
    return PostEntity(
        postId = id,
        authorId = owner.id,
        type = "TEXT",
        text = textContent,
        mediaUrl = null,
        thumbnailUrl = null,
        productId = null,
        createdAt = created,
        updatedAt = created
    )
}

fun DemoSocialConnection.toCounterpartUser(now: Long = System.currentTimeMillis()): UserEntity {
    val slug = slugify(name)
    val id = "demo-conn-$slug"
    return UserEntity(
        userId = id,
        fullName = name,
        address = null,
        userType = UserType.GENERAL,
        createdAt = now - ONE_DAY_MILLIS,
        updatedAt = now - ONE_DAY_MILLIS / 2
    )
}

fun DemoTransaction.toCounterpartUser(now: Long = System.currentTimeMillis()): UserEntity {
    val slug = slugify(counterpart)
    val id = "demo-conn-$slug"
    return UserEntity(
        userId = id,
        fullName = counterpart,
        address = null,
        userType = UserType.GENERAL,
        createdAt = now - ONE_DAY_MILLIS,
        updatedAt = now - ONE_DAY_MILLIS / 2
    )
}

// Marketplace mappings
fun DemoOrder.toOrderEntity(owner: DemoUserProfile, index: Int, now: Long): OrderEntity {
    val id = "demo-${owner.id}-order-${index + 1}"
    val timestamp = now - index * 60_000L
    return OrderEntity(
        orderId = id,
        buyerId = owner.id,
        sellerId = "", // To be set based on items
        status = status,
        totalAmount = totalAmount.toDouble(),
        orderDate = timestamp,
        createdAt = timestamp,
        updatedAt = timestamp,
        lastModifiedAt = timestamp,
        dirty = false
    )
}

fun DemoOrder.toOrderTrackingEvents(orderId: String, now: Long): List<OrderTrackingEventEntity> {
    return trackingEvents.mapIndexed { eventIndex, event ->
        OrderTrackingEventEntity(
            eventId = "$orderId-event-${eventIndex + 1}",
            orderId = orderId,
            status = event.status,
            hubId = null,
            note = event.location,
            timestamp = event.timestamp
        )
    }
}

fun DemoCartItem.toCartItemEntity(userId: String, now: Long): CartItemEntity {
    return CartItemEntity(
        id = "demo-$userId-cart-${productId}",
        userId = userId,
        productId = productId,
        quantity = quantity.toDouble(),
        addedAt = now
    )
}

fun DemoWishlistItem.toWishlistEntity(userId: String): WishlistEntity {
    return WishlistEntity(
        userId = userId,
        productId = productId,
        addedAt = addedAt
    )
}

fun DemoAuction.toAuctionEntity(owner: DemoUserProfile, index: Int, now: Long): AuctionEntity {
    val id = "demo-${owner.id}-auction-${index + 1}"
    return AuctionEntity(
        auctionId = id,
        productId = productId,
        startsAt = startTime,
        endsAt = endTime,
        minPrice = minPrice.toDouble(),
        currentPrice = 0.0,
        isActive = (now >= startTime && now <= endTime),
        createdAt = now - index * 60_000L,
        updatedAt = now - index * 60_000L
    )
}

fun DemoBid.toBidEntity(auctionId: String, userId: String, index: Int, now: Long): BidEntity {
    val id = "demo-$auctionId-$userId-bid-${index + 1}"
    return BidEntity(
        bidId = id,
        auctionId = auctionId,
        userId = userId,
        amount = amount.toDouble(),
        placedAt = timestamp
    )
}

fun DemoInvoice.toInvoiceEntity(orderId: String, now: Long): InvoiceEntity {
    val id = "demo-invoice-$orderId"
    return InvoiceEntity(
        invoiceId = id,
        orderId = orderId,
        subtotal = subtotal.toDouble(),
        gstPercent = 0.0,
        gstAmount = 0.0,
        total = total.toDouble(),
        createdAt = now
    )
}

fun DemoInvoice.toInvoiceLineEntities(invoiceId: String): List<InvoiceLineEntity> {
    // Assuming lines are not detailed in demo, return empty
    return emptyList()
}

fun DemoPayment.toPaymentEntity(orderId: String, userId: String, now: Long): PaymentEntity {
    val id = "demo-$userId-payment-$orderId"
    return PaymentEntity(
        paymentId = id,
        orderId = orderId,
        userId = userId,
        method = method,
        amount = amount.toDouble(),
        status = status,
        idempotencyKey = "demo-$userId-$orderId-$now"
    )
}

// Farm monitoring mappings
fun DemoDailyLog.toDailyLogEntity(farmerId: String, productId: String, index: Int, now: Long): DailyLogEntity {
    val id = "demo-$farmerId-dailylog-${index + 1}"
    return DailyLogEntity(
        logId = id,
        productId = productId,
        farmerId = farmerId,
        logDate = date,
        weightGrams = weight,
        feedKg = null,
        medicationJson = null,
        symptomsJson = null,
        activityLevel = null,
        photoUrls = null,
        notes = notes,
        temperature = temperature,
        humidity = humidity
    )
}

fun DemoTask.toTaskEntity(farmerId: String, index: Int, now: Long): TaskEntity {
    val id = "demo-$farmerId-task-${index + 1}"
    val createdAt = now - index * 60_000L
    val completedAt = if (status == "completed") createdAt + 30_000L else null
    return TaskEntity(
        taskId = id,
        farmerId = farmerId,
        productId = productId,
        batchId = null,
        taskType = type,
        title = title,
        description = null,
        dueAt = dueDate,
        completedAt = completedAt,
        completedBy = null,
        priority = priority,
        recurrence = null,
        notes = null,
        createdAt = createdAt,
        updatedAt = completedAt ?: createdAt,
        dirty = false,
        syncedAt = null,
        snoozeUntil = null,
        metadata = null,
        mergedAt = null,
        mergeCount = 0
    )
}

fun DemoGrowthRecord.toGrowthRecordEntity(farmerId: String, productId: String, index: Int, now: Long): GrowthRecordEntity {
    val id = "demo-$farmerId-growth-${index + 1}"
    return GrowthRecordEntity(
        recordId = id,
        productId = productId,
        farmerId = farmerId,
        week = week,
        weightGrams = weight,
        heightCm = height,
        photoUrl = null,
        healthStatus = healthStatus,
        milestone = null
    )
}

fun DemoQuarantineRecord.toQuarantineRecordEntity(farmerId: String, productId: String, index: Int, now: Long): QuarantineRecordEntity {
    val id = "demo-$farmerId-quarantine-${index + 1}"
    return QuarantineRecordEntity(
        quarantineId = id,
        productId = productId,
        farmerId = farmerId,
        reason = reason,
        protocol = null,
        medicationScheduleJson = null,
        statusHistoryJson = null,
        vetNotes = null,
        startedAt = startDate,
        lastUpdatedAt = startDate,
        updatesCount = 0,
        endedAt = endDate,
        status = status,
        updatedAt = startDate,
        dirty = false,
        syncedAt = null
    )
}

fun DemoMortalityRecord.toMortalityRecordEntity(farmerId: String, productId: String, index: Int, now: Long): MortalityRecordEntity {
    val id = "demo-$farmerId-mortality-${index + 1}"
    return MortalityRecordEntity(
        deathId = id,
        productId = productId,
        farmerId = farmerId,
        causeCategory = cause,
        circumstances = null,
        ageWeeks = ageWeeks,
        disposalMethod = null,
        financialImpactInr = null,
        occurredAt = date
    )
}

fun DemoVaccinationRecord.toVaccinationRecordEntity(farmerId: String, productId: String, index: Int, now: Long): VaccinationRecordEntity {
    val id = "demo-$farmerId-vaccination-${index + 1}"
    return VaccinationRecordEntity(
        vaccinationId = id,
        productId = productId,
        farmerId = farmerId,
        vaccineType = vaccineType,
        supplier = null,
        batchCode = null,
        doseMl = null,
        scheduledAt = scheduledDate,
        administeredAt = administeredDate,
        efficacyNotes = null,
        costInr = null
    )
}

fun DemoHatchingBatch.toHatchingBatchEntity(farmerId: String, index: Int, now: Long): HatchingBatchEntity {
    val id = "demo-$farmerId-hatchingbatch-${index + 1}"
    return HatchingBatchEntity(
        batchId = id,
        name = name,
        farmerId = farmerId,
        startedAt = now - index * 60_000L,
        expectedHatchAt = expectedHatchDate,
        temperatureC = null,
        humidityPct = null,
        eggsCount = eggsCount,
        sourceCollectionId = null,
        notes = null,
        status = status
    )
}

fun DemoHatchingBatch.toHatchingLogEntities(batchId: String, farmerId: String, now: Long): List<HatchingLogEntity> {
    // Assuming some logs, but since not detailed, return empty or minimal
    return emptyList()
}

fun DemoBreedingPair.toBreedingPairEntity(farmerId: String, index: Int, now: Long): BreedingPairEntity {
    val id = "demo-$farmerId-breedingpair-${index + 1}"
    return BreedingPairEntity(
        pairId = id,
        farmerId = farmerId,
        maleProductId = maleProductId,
        femaleProductId = femaleProductId,
        pairedAt = now - index * 60_000L,
        status = status,
        eggsCollected = eggsCollected,
        hatchSuccessRate = 0.0,
        notes = null
    )
}

// Gamification mappings
fun DemoAchievement.toUserProgressEntity(userId: String, index: Int, now: Long): UserProgressEntity {
    val id = "demo-$userId-progress-${index + 1}"
    val unlocked = unlockedAt ?: now - index * 60_000L
    return UserProgressEntity(
        id = id,
        userId = userId,
        achievementId = achievementId,
        progress = points,
        target = points,
        unlockedAt = unlocked,
        updatedAt = unlocked
    )
}

fun DemoCoinTransaction.toCoinEntity(userId: String, index: Int, now: Long): CoinEntity {
    val id = "demo-$userId-coin-${index + 1}"
    return CoinEntity(
        coinTransactionId = id,
        userId = userId,
        amount = amount.toDouble(),
        type = type,
        description = description,
        relatedTransferId = null,
        relatedOrderId = null,
        transactionDate = now - index * 60_000L
    )
}

fun DemoCoinTransaction.toCoinLedgerEntity(userId: String, index: Int, now: Long): CoinLedgerEntity {
    val id = "demo-$userId-ledger-${index + 1}"
    return CoinLedgerEntity(
        entryId = id,
        userId = userId,
        type = type,
        coins = amount.toInt(),
        amountInInr = 0.0,
        refId = null,
        notes = description,
        createdAt = date
    )
}

fun DemoLeaderboardEntry.toLeaderboardEntity(userId: String, periodKey: String, index: Int): LeaderboardEntity {
    val id = "demo-$userId-leaderboard-${periodKey}-${index + 1}"
    return LeaderboardEntity(
        id = id,
        periodKey = periodKey,
        userId = userId,
        score = score.toLong(),
        rank = rank
    )
}

// Community/social mappings
fun DemoComment.toCommentEntity(postId: String, authorId: String, index: Int, now: Long): CommentEntity {
    val id = "demo-$authorId-comment-${index + 1}"
    val timestamp = now - index * 60_000L
    return CommentEntity(
        commentId = id,
        postId = postId,
        authorId = authorId,
        text = text,
        createdAt = timestamp
    )
}

fun DemoLike.toLikeEntity(postId: String, userId: String, index: Int, now: Long): LikeEntity {
    val id = "demo-$userId-like-${index + 1}"
    val timestamp = now - index * 60_000L
    return LikeEntity(
        likeId = id,
        postId = postId,
        userId = userId,
        createdAt = timestamp
    )
}

fun DemoFollow.toFollowEntity(followerId: String, index: Int, now: Long): FollowEntity {
    val id = "demo-$followerId-follow-${index + 1}"
    val timestamp = now - index * 60_000L
    return FollowEntity(
        followId = id,
        followerId = followerId,
        followedId = followedUserId,
        createdAt = timestamp
    )
}

fun DemoGroup.toGroupEntity(ownerId: String, index: Int, now: Long): GroupEntity {
    val id = "demo-$ownerId-group-${index + 1}"
    val timestamp = now - index * 60_000L
    return GroupEntity(
        groupId = id,
        name = name,
        description = description,
        ownerId = ownerId,
        category = category,
        createdAt = timestamp
    )
}

fun DemoGroup.toGroupMemberEntity(groupId: String, userId: String, index: Int, now: Long): GroupMemberEntity {
    val id = "demo-$groupId-member-${index + 1}"
    val timestamp = now - index * 60_000L
    return GroupMemberEntity(
        membershipId = id,
        groupId = groupId,
        userId = userId,
        role = "MEMBER",
        joinedAt = timestamp
    )
}

fun DemoEvent.toEventEntity(groupId: String, index: Int, now: Long): EventEntity {
    val id = "demo-$groupId-event-${index + 1}"
    return EventEntity(
        eventId = id,
        groupId = groupId,
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = null
    )
}

fun DemoEvent.toEventRsvpEntity(eventId: String, userId: String, now: Long): EventRsvpEntity {
    val id = "demo-$userId-rsvp-$eventId"
    return EventRsvpEntity(
        id = id,
        eventId = eventId,
        userId = userId,
        status = rsvpStatus,
        updatedAt = now
    )
}

fun DemoChatMessage.toChatMessageEntity(senderId: String, index: Int, now: Long): ChatMessageEntity {
    val id = "demo-$senderId-chat-${index + 1}"
    val timestamp = now - index * 60_000L
    return ChatMessageEntity(
        messageId = id,
        senderId = senderId,
        receiverId = receiverId,
        body = body,
        sentAt = timestamp
    )
}

fun DemoExpertBooking.toExpertBookingEntity(userId: String, index: Int, now: Long): ExpertBookingEntity {
    val id = "demo-$userId-booking-${index + 1}"
    val timestamp = now - index * 60_000L
    return ExpertBookingEntity(
        bookingId = id,
        userId = userId,
        expertId = expertId,
        topic = topic,
        startTime = startTime,
        endTime = startTime + 60_000L, // 1 minute
        status = status
    )
}

// Traceability mappings
fun DemoFamilyTreeNode.toFamilyTreeEntity(ownerId: String, index: Int, now: Long): FamilyTreeEntity {
    val id = "demo-$ownerId-familytree-${index + 1}"
    val timestamp = now - index * 60_000L
    return FamilyTreeEntity(
        nodeId = id,
        productId = productId,
        parentProductId = parentProductId,
        childProductId = null,
        relationType = relationType,
        createdAt = timestamp,
        updatedAt = timestamp,
        isDeleted = false,
        deletedAt = null
    )
}

fun DemoLifecycleEvent.toLifecycleEventEntity(ownerId: String, index: Int, now: Long): LifecycleEventEntity {
    val id = "demo-$ownerId-lifecycle-${index + 1}"
    val timestamp = now - index * 60_000L
    return LifecycleEventEntity(
        eventId = id,
        productId = productId,
        week = week,
        stage = stage,
        type = type,
        notes = notes,
        timestamp = timestamp
    )
}

// Breeding mappings
fun DemoMatingLog.toMatingLogEntity(farmerId: String, index: Int, now: Long): MatingLogEntity {
    val id = "demo-$farmerId-mating-${index + 1}"
    val timestamp = now - index * 60_000L
    return MatingLogEntity(
        logId = id,
        pairId = pairId,
        farmerId = farmerId,
        matedAt = matedAt,
        observedBehavior = observedBehavior,
        environmentalConditions = null,
        notes = notes,
        createdAt = timestamp,
        updatedAt = timestamp,
        dirty = false,
        syncedAt = null
    )
}

fun DemoEggCollection.toEggCollectionEntity(farmerId: String, index: Int, now: Long): EggCollectionEntity {
    val id = "demo-$farmerId-eggcollection-${index + 1}"
    val timestamp = now - index * 60_000L
    return EggCollectionEntity(
        collectionId = id,
        pairId = pairId,
        farmerId = farmerId,
        eggsCollected = eggsCollected,
        collectedAt = date,
        qualityGrade = qualityGrade,
        weight = null,
        notes = null,
        createdAt = timestamp,
        updatedAt = timestamp,
        dirty = false,
        syncedAt = null
    )
}

// Notification mapping
fun DemoNotification.toNotificationEntity(userId: String, index: Int, now: Long): NotificationEntity {
    val id = "demo-$userId-notification-${index + 1}"
    val timestamp = now - index * 60_000L
    return NotificationEntity(
        notificationId = id,
        userId = userId,
        title = title,
        message = message,
        type = type,
        deepLinkUrl = null,
        isRead = (index % 2 == 0),
        imageUrl = null,
        createdAt = timestamp,
        isBatched = false,
        batchedAt = null,
        displayedAt = null,
        domain = domain
    )
}

private fun slugify(input: String): String = input
    .lowercase(Locale.ENGLISH)
    .replace("[^a-z0-9]+".toRegex(), "-")
    .trim('-')