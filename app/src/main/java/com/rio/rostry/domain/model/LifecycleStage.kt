package com.rio.rostry.domain.model

data class LifecycleStage(
    val stage: String,
    val startWeek: Int,
    val endWeek: Int,
    val title: String,
    val description: String
) {
    companion object {
        val CHICK = LifecycleStage(
            stage = "CHICK",
            startWeek = 0,
            endWeek = 5,
            title = "Chick Stage",
            description = "0-5 weeks: Initial care and vaccination period"
        )
        
        val GROWTH = LifecycleStage(
            stage = "GROWTH",
            startWeek = 5,
            endWeek = 20,
            title = "Growth Stage",
            description = "5-20 weeks: Rapid development and monitoring period"
        )
        
        val ADULT = LifecycleStage(
            stage = "ADULT",
            startWeek = 20,
            endWeek = 52,
            title = "Adult Stage",
            description = "20-52 weeks: Full development with gender/color identification"
        )
        
        val BREEDER = LifecycleStage(
            stage = "BREEDER",
            startWeek = 52,
            endWeek = Int.MAX_VALUE,
            title = "Breeder Stage",
            description = "52+ weeks: Eligible for breeding"
        )
        
        fun getAllStages(): List<LifecycleStage> = listOf(CHICK, GROWTH, ADULT, BREEDER)
        
        fun getStageForWeek(week: Int): LifecycleStage {
            return getAllStages().firstOrNull { week >= it.startWeek && week < it.endWeek } ?: BREEDER
        }
    }
}