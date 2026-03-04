package com.rio.rostry.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "thread_metadata",
    indices = [
        Index("contextType"),
        Index("lastMessageAt"),
        Index("createdAt")
    ]
)
data class ThreadMetadataEntity(
    @PrimaryKey val threadId: String,
    val title: String?,
    val contextType: String?, // PRODUCT_INQUIRY, EXPERT_CONSULT, BREEDING_DISCUSSION, GENERAL
    val relatedEntityId: String?,
    val topic: String?,
    val participantIds: String, // Comma-separated list
    val lastMessageAt: Long,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "community_recommendations",
    indices = [
        Index("userId"),
        Index("type"),
        Index("score"),
        Index("expiresAt")
    ]
)
data class CommunityRecommendationEntity(
    @PrimaryKey val recommendationId: String,
    val userId: String,
    val type: String, // MENTOR, CONNECTION, GROUP, EVENT, EXPERT, POST
    val targetId: String,
    val score: Double,
    val reason: String?,
    val createdAt: Long,
    val expiresAt: Long,
    val dismissed: Boolean = false
)

@Entity(
    tableName = "user_interests",
    indices = [
        Index(value = ["userId", "category", "value"], unique = true)
    ]
)
data class UserInterestEntity(
    @PrimaryKey val interestId: String,
    val userId: String,
    val category: String, // BREED_TYPE, TOPIC, REGION, PRODUCT_CATEGORY
    val value: String,
    val weight: Double,
    val updatedAt: Long
)

@Entity(tableName = "expert_profiles")
data class ExpertProfileEntity(
    @PrimaryKey val userId: String,
    val specialties: String, // Comma-separated: breeding, health, nutrition, etc.
    val bio: String?,
    val rating: Double,
    val totalConsultations: Int,
    val availableForBooking: Boolean,
    val hourlyRate: Double?,
    val updatedAt: Long
)
