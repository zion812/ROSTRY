package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.common.Result
import java.io.File

/**
 * Domain interface for report generation.
 *
 * Generates CSV reports for user growth analytics and
 * commerce/order data exports.
 */
interface ReportRepository {

    /** Generate a user growth report as a CSV file. */
    suspend fun generateUserGrowthReport(): Result<File>

    /** Generate a commerce/order report as a CSV file. */
    suspend fun generateCommerceReport(): Result<File>
}
