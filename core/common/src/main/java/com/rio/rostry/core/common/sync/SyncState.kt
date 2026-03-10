package com.rio.rostry.core.common.sync

/**
 * Represents the synchronization state of a local entity.
 * Moved from app module to core:common so that core:database repositories
 * can reference it without creating a circular dependency.
 */
enum class SyncState {
    PENDING,
    SYNCED,
    CONFLICT
}

/**
 * Determines a [SyncState] from common entity fields.
 */
fun getSyncState(dirty: Boolean, syncedAt: Long?, updatedAt: Long): SyncState {
    return when {
        dirty && syncedAt == null -> SyncState.PENDING
        !dirty && syncedAt != null -> SyncState.SYNCED
        updatedAt > (syncedAt ?: 0L) && dirty -> SyncState.CONFLICT
        else -> SyncState.SYNCED
    }
}
