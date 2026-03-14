package com.rio.rostry.domain.admin.repository

import com.rio.rostry.core.common.Result
import java.io.File

/**
 * Domain interface for report generation.
 *
 * Generates CSV reports for user growth analytics,
 * commerce/order data, inventory, financial, engagement,
 * and veterinary activity exports.
 */
interface ReportRepository {

    /** Generate a user growth report as a CSV file. */
    suspend fun generateUserGrowthReport(): Result<File>

    /** Generate a commerce/order report as a CSV file. */
    suspend fun generateCommerceReport(): Result<File>

    /** Generate an inventory status report as a CSV file. */
    suspend fun generateInventoryReport(): Result<File>

    /** Generate a financial report (revenue, expenses, profits) as a CSV file. */
    suspend fun generateFinancialReport(): Result<File>

    /** Generate a user engagement and activity report as a CSV file. */
    suspend fun generateEngagementReport(): Result<File>

    /** Generate a veterinary/health records report as a CSV file. */
    suspend fun generateVeterinaryReport(): Result<File>
}