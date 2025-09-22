package com.rio.rostry.lifecycle

import com.rio.rostry.utils.LifecycleRules
import org.junit.Assert.assertEquals
import org.junit.Test

class LifecycleRulesTest {

    @Test
    fun stageBoundaries_areCorrect() {
        assertEquals("CHICK", LifecycleRules.stageForWeek(0))
        assertEquals("CHICK", LifecycleRules.stageForWeek(4))
        assertEquals("GROWTH", LifecycleRules.stageForWeek(5))
        assertEquals("GROWTH", LifecycleRules.stageForWeek(19))
        assertEquals("ADULT", LifecycleRules.stageForWeek(20))
        assertEquals("ADULT", LifecycleRules.stageForWeek(51))
        assertEquals("BREEDER", LifecycleRules.stageForWeek(52))
        assertEquals("BREEDER", LifecycleRules.stageForWeek(80))
    }

    @Test
    fun eventsForWeek_containsExpectedTypes() {
        // Week 0: chick with vaccination + growth update
        val w0 = LifecycleRules.eventsForWeek(0).map { it.first }.toSet()
        assert(w0.contains("VACCINATION"))
        assert(w0.contains("GROWTH_UPDATE"))

        // Week 6: growth phase weekly update
        val w6 = LifecycleRules.eventsForWeek(6).map { it.first }.toSet()
        assert(w6.contains("GROWTH_UPDATE"))

        // Week 25: adult development tracking
        val w25 = LifecycleRules.eventsForWeek(25).map { it.first }.toSet()
        assert(w25.contains("GROWTH_UPDATE"))

        // Week 52: breeder milestone
        val w52 = LifecycleRules.eventsForWeek(52)
        assertEquals("MILESTONE", w52.first().first)
    }
}
