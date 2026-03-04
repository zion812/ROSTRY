package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * DAO for referential integrity checks.
 *
 * Provides raw SQL query capabilities for checking foreign key constraints
 * and entity relationships across tables. Used by ReferentialIntegrityChecker.
 *
 * Requirements: 4.3, 4.6, 4.7
 */
@Dao
interface ReferentialIntegrityDao {

    /**
     * Execute a raw query that returns a count (e.g., counting orphaned records).
     */
    @RawQuery
    suspend fun queryCount(query: SupportSQLiteQuery): Int
}
