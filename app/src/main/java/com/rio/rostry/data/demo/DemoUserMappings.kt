package com.rio.rostry.data.demo

import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.UserEntity
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

private fun slugify(input: String): String = input
    .lowercase(Locale.ENGLISH)
    .replace("[^a-z0-9]+".toRegex(), "-")
    .trim('-')
