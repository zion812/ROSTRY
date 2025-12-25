package com.rio.rostry.utils

import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Centralized utility for time-based logic to ensure consistency across the app.
 * Replaces ad-hoc milliseconds calculations.
 */
object TimeUtils {

    const val ONE_DAY_MS = 24L * 60 * 60 * 1000

    fun now(): Long = System.currentTimeMillis()

    /**
     * Calculates a past timestamp based on days.
     * @param days Number of days in the past.
     * @return Timestamp in milliseconds.
     */
    fun daysAgo(days: Long): Long {
        return now() - (days * ONE_DAY_MS)
    }

    /**
     * Checks if a timestamp is within a certain number of days ago.
     * Use this for freshness checks.
     */
    fun isRecent(timestamp: Long, maxDays: Long): Boolean {
        val diff = now() - timestamp
        return diff >= 0 && diff <= (maxDays * ONE_DAY_MS)
    }

    /**
     * Calculates approximate birth date for an age group.
     * @param weeks The approximate age in weeks.
     */
    fun approximateBirthDate(weeks: Long): Long {
        return now() - (weeks * 7 * ONE_DAY_MS)
    }
    
    /**
     * Calculates approximate birth date for an age group in days.
     * @param days The approximate age in days.
     */
    fun approximateBirthDateDays(days: Long): Long {
        return daysAgo(days)
    }

    /**
     * Returns true if the timestamp is older than the threshold days.
     */
    fun isOlderThan(timestamp: Long, days: Long): Boolean {
        return (now() - timestamp) > (days * ONE_DAY_MS)
    }
}
