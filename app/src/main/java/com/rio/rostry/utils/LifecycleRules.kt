package com.rio.rostry.utils

object LifecycleRules {
    fun stageForWeek(week: Int): String = when {
        week < 0 -> "UNKNOWN"
        week < 5 -> "CHICK"
        week < 20 -> "GROWTH"
        week < 52 -> "ADULT"
        else -> "BREEDER"
    }

    // Returns a list of Pair(type, notes)
    fun eventsForWeek(week: Int): List<Pair<String, String>> {
        val stage = stageForWeek(week)
        val out = mutableListOf<Pair<String, String>>()
        when (stage) {
            "CHICK" -> {
                // Vaccination reminders typically during chick stage (example weeks)
                if (week in listOf(0, 2, 4)) out += "VACCINATION" to "Chick stage vaccination"
                out += "GROWTH_UPDATE" to "Weekly chick growth check"
            }
            "GROWTH" -> {
                out += "GROWTH_UPDATE" to "Weekly growth monitoring"
            }
            "ADULT" -> {
                out += "GROWTH_UPDATE" to "Adult development tracking"
            }
            "BREEDER" -> {
                out += "MILESTONE" to if (week == 52) "Breeder eligibility" else "Breeder period"
            }
        }
        return out
    }
}
