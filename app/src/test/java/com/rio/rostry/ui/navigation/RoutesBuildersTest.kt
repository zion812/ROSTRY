package com.rio.rostry.ui.navigation

import com.rio.rostry.ui.navigation.Routes
import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesBuildersTest {

    @Test
    fun `monitoringTasks with filter generates correct URL`() {
        val result = Routes.Builders.monitoringTasks("overdue")
        assertEquals("monitoring/tasks?filter=overdue", result)
    }

    @Test
    fun `monitoringTasks without filter returns base route`() {
        val result = Routes.Builders.monitoringTasks()
        assertEquals("monitoring/tasks", result)
    }

    @Test
    fun `monitoringVaccinationWithFilter generates correct URL`() {
        val result = Routes.Builders.monitoringVaccinationWithFilter("due")
        assertEquals("monitoring/vaccination?filter=due", result)
    }

    @Test
    fun `monitoringQuarantine with filter generates correct URL`() {
        val result = Routes.Builders.monitoringQuarantine("quarantine_12h")
        assertEquals("monitoring/quarantine?filter=quarantine_12h", result)
    }

    @Test
    fun `monitoringGrowthWithFilter generates correct URL`() {
        val result = Routes.Builders.monitoringGrowthWithFilter("ready_to_split")
        assertEquals("monitoring/growth?filter=ready_to_split", result)
    }

    @Test
    fun `monitoringDailyLog without filter returns base route`() {
        val result = Routes.Builders.monitoringDailyLog()
        assertEquals("monitoring/daily_log", result)
    }

    @Test
    fun `monitoringHatching without filter returns base route`() {
        val result = Routes.Builders.monitoringHatching()
        assertEquals("monitoring/hatching", result)
    }

    @Test
    fun `monitoringMortality without filter returns base route`() {
        val result = Routes.Builders.monitoringMortality()
        assertEquals("monitoring/mortality", result)
    }

    @Test
    fun `monitoringBreeding without filter returns base route`() {
        val result = Routes.Builders.monitoringBreeding()
        assertEquals("monitoring/breeding", result)
    }

    @Test
    fun `transfersWithFilter generates correct URL`() {
        val result = Routes.Builders.transfersWithFilter("ELIGIBLE")
        assertEquals("transfer/list?status=ELIGIBLE", result)
    }

    @Test
    fun `productsWithFilter generates correct URL`() {
        val result = Routes.Builders.productsWithFilter("ready_to_list")
        assertEquals("product/market?filter=ready_to_list", result)
    }
}