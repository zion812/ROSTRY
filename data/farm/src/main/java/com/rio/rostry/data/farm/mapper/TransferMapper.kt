package com.rio.rostry.data.farm.mapper

import com.rio.rostry.core.model.Transfer
import com.rio.rostry.data.database.entity.TransferEntity

fun TransferEntity.toTransfer(): Transfer =
    Transfer(
        transferId = transferId,
        productId = productId,
        fromUserId = fromUserId,
        toUserId = toUserId,
        orderId = orderId,
        amount = amount,
        currency = currency,
        type = type,
        status = status,
        transferCode = transferCode,
        transferCodeExpiresAt = transferCodeExpiresAt,
        transferType = transferType,
        lineageSnapshotJson = lineageSnapshotJson,
        healthSnapshotJson = healthSnapshotJson,
        sellerPhotoUrl = sellerPhotoUrl,
        buyerPhotoUrl = buyerPhotoUrl,
        gpsLat = gpsLat,
        gpsLng = gpsLng,
        timeoutAt = timeoutAt,
        conditionsJson = conditionsJson,
        transactionReference = transactionReference,
        notes = notes,
        initiatedAt = initiatedAt,
        completedAt = completedAt,
        updatedAt = updatedAt,
        lastModifiedAt = lastModifiedAt,
        isDeleted = isDeleted,
        deletedAt = deletedAt,
        dirty = dirty
    )

fun Transfer.toEntity(): TransferEntity =
    TransferEntity(
        transferId = transferId,
        productId = productId,
        fromUserId = fromUserId,
        toUserId = toUserId,
        orderId = orderId,
        amount = amount,
        currency = currency,
        type = type,
        status = status,
        transferCode = transferCode,
        transferCodeExpiresAt = transferCodeExpiresAt,
        transferType = transferType ?: "STANDARD",
        lineageSnapshotJson = lineageSnapshotJson,
        healthSnapshotJson = healthSnapshotJson,
        sellerPhotoUrl = sellerPhotoUrl,
        buyerPhotoUrl = buyerPhotoUrl,
        gpsLat = gpsLat,
        gpsLng = gpsLng,
        timeoutAt = timeoutAt,
        conditionsJson = conditionsJson,
        transactionReference = transactionReference,
        notes = notes,
        initiatedAt = initiatedAt,
        completedAt = completedAt,
        updatedAt = updatedAt,
        lastModifiedAt = lastModifiedAt,
        isDeleted = isDeleted,
        deletedAt = deletedAt,
        dirty = dirty
    )
