package com.rio.rostry.data.database.mapper

import com.rio.rostry.core.model.*
import com.rio.rostry.data.database.entity.*

fun FarmAlertEntity.toDomain() = FarmAlert(
    alertId = alertId,
    farmerId = farmerId,
    alertType = alertType,
    severity = severity,
    message = message,
    actionRoute = actionRoute,
    isRead = isRead,
    createdAt = createdAt,
    expiresAt = expiresAt
)

fun FarmAlert.toEntity() = FarmAlertEntity(
    alertId = alertId,
    farmerId = farmerId,
    alertType = alertType,
    severity = severity,
    message = message,
    actionRoute = actionRoute,
    isRead = isRead,
    createdAt = createdAt,
    expiresAt = expiresAt
)

fun ListingDraftEntity.toDomain() = ListingDraft(
    draftId = draftId,
    farmerId = farmerId,
    step = step,
    formDataJson = formDataJson,
    createdAt = createdAt,
    updatedAt = updatedAt,
    expiresAt = expiresAt
)

fun ListingDraft.toEntity() = ListingDraftEntity(
    draftId = draftId,
    farmerId = farmerId,
    step = step,
    formDataJson = formDataJson,
    createdAt = createdAt,
    updatedAt = updatedAt,
    expiresAt = expiresAt
)

fun FarmerDashboardSnapshotEntity.toDomain() = DashboardSnapshot(
    snapshotId = snapshotId,
    farmerId = farmerId,
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
    productsReadyToListCount = productsReadyToListCount,
    avgFeedKg = avgFeedKg,
    medicationUsageCount = medicationUsageCount,
    dailyLogComplianceRate = dailyLogComplianceRate,
    actionSuggestions = actionSuggestions,
    transfersInitiatedCount = transfersInitiatedCount,
    transfersCompletedCount = transfersCompletedCount,
    complianceScore = complianceScore,
    onboardingCount = onboardingCount,
    dailyGoalsCompletedCount = dailyGoalsCompletedCount,
    analyticsInsightsCount = analyticsInsightsCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun DashboardSnapshot.toEntity() = FarmerDashboardSnapshotEntity(
    snapshotId = snapshotId,
    farmerId = farmerId,
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
    productsReadyToListCount = productsReadyToListCount,
    avgFeedKg = avgFeedKg,
    medicationUsageCount = medicationUsageCount,
    dailyLogComplianceRate = dailyLogComplianceRate,
    actionSuggestions = actionSuggestions,
    transfersInitiatedCount = transfersInitiatedCount,
    transfersCompletedCount = transfersCompletedCount,
    complianceScore = complianceScore,
    onboardingCount = onboardingCount,
    dailyGoalsCompletedCount = dailyGoalsCompletedCount,
    analyticsInsightsCount = analyticsInsightsCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun BreedingPairEntity.toDomain() = BreedingPair(
    pairId = pairId,
    farmerId = farmerId,
    maleProductId = maleProductId,
    femaleProductId = femaleProductId,
    pairedAt = pairedAt,
    status = status,
    hatchSuccessRate = hatchSuccessRate,
    eggsCollected = eggsCollected,
    hatchedEggs = hatchedEggs,
    separatedAt = separatedAt,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun BreedingPair.toEntity() = BreedingPairEntity(
    pairId = pairId,
    farmerId = farmerId,
    maleProductId = maleProductId,
    femaleProductId = femaleProductId,
    pairedAt = pairedAt,
    status = status,
    hatchSuccessRate = hatchSuccessRate,
    eggsCollected = eggsCollected,
    hatchedEggs = hatchedEggs,
    separatedAt = separatedAt,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
