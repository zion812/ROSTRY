package com.rio.rostry.data.sync

/**
 * Represents a sync conflict between local and remote data.
 */
data class SyncConflict(
    val entityType: String,
    val entityId: String,
    val conflictingFields: List<String>,
    val localTimestamp: Long,
    val remoteTimestamp: Long,
    val localVersion: Any? = null,
    val remoteVersion: Any? = null
)
