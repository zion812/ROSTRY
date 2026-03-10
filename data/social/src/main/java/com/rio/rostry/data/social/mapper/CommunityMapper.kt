package com.rio.rostry.data.social.mapper

import com.rio.rostry.core.model.Event
import com.rio.rostry.core.model.EventRsvp
import com.rio.rostry.core.model.Group
import com.rio.rostry.core.model.GroupMember
import com.rio.rostry.data.database.entity.EventEntity
import com.rio.rostry.data.database.entity.EventRsvpEntity
import com.rio.rostry.data.database.entity.GroupEntity
import com.rio.rostry.data.database.entity.GroupMemberEntity

/**
 * Converts GroupEntity to domain model.
 */
fun GroupEntity.toGroup(): Group {
    return Group(
        groupId = this.groupId,
        name = this.name,
        description = this.description,
        ownerId = this.ownerId,
        category = this.category,
        createdAt = this.createdAt
    )
}

/**
 * Converts domain model to GroupEntity.
 */
fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        groupId = this.groupId,
        name = this.name,
        description = this.description,
        ownerId = this.ownerId,
        category = this.category,
        createdAt = this.createdAt
    )
}

/**
 * Converts GroupMemberEntity to domain model.
 */
fun GroupMemberEntity.toGroupMember(): GroupMember {
    return GroupMember(
        membershipId = this.membershipId,
        groupId = this.groupId,
        userId = this.userId,
        role = this.role,
        joinedAt = this.joinedAt
    )
}

/**
 * Converts domain model to GroupMemberEntity.
 */
fun GroupMember.toEntity(): GroupMemberEntity {
    return GroupMemberEntity(
        membershipId = this.membershipId,
        groupId = this.groupId,
        userId = this.userId,
        role = this.role,
        joinedAt = this.joinedAt
    )
}

/**
 * Converts EventEntity to domain model.
 */
fun EventEntity.toEvent(): Event {
    return Event(
        eventId = this.eventId,
        groupId = this.groupId,
        title = this.title,
        description = this.description,
        location = this.location,
        startTime = this.startTime,
        endTime = this.endTime
    )
}

/**
 * Converts domain model to EventEntity.
 */
fun Event.toEntity(): EventEntity {
    return EventEntity(
        eventId = this.eventId,
        groupId = this.groupId,
        title = this.title,
        description = this.description,
        location = this.location,
        startTime = this.startTime,
        endTime = this.endTime
    )
}

/**
 * Converts EventRsvpEntity to domain model.
 */
fun EventRsvpEntity.toEventRsvp(): EventRsvp {
    return EventRsvp(
        id = this.id,
        eventId = this.eventId,
        userId = this.userId,
        status = this.status,
        updatedAt = this.updatedAt
    )
}

/**
 * Converts domain model to EventRsvpEntity.
 */
fun EventRsvp.toEntity(): EventRsvpEntity {
    return EventRsvpEntity(
        id = this.id,
        eventId = this.eventId,
        userId = this.userId,
        status = this.status,
        updatedAt = this.updatedAt
    )
}
