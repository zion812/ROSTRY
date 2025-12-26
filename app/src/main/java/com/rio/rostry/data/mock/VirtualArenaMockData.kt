package com.rio.rostry.data.mock

import com.rio.rostry.data.database.entity.CompetitionEntryEntity
import com.rio.rostry.domain.model.CompetitionStatus

object VirtualArenaMockData {
    fun getMockCompetitions(): List<CompetitionEntryEntity> {
        val now = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L
        
        return listOf(
            CompetitionEntryEntity(
                competitionId = "comp_001",
                title = "National Rooster Show 2024",
                description = "The biggest online rooster show of the year. Vote for the best structure, color, and health.",
                startTime = now - (2 * day),
                endTime = now + (5 * day),
                region = "National",
                status = CompetitionStatus.LIVE,
                entryFee = 10.0,
                prizePool = "$1000 + Gold Badge",
                participantCount = 124,
                participantsPreviewJson = "[\"https://placehold.co/100x100/png?text=Bird1\", \"https://placehold.co/100x100/png?text=Bird2\", \"https://placehold.co/100x100/png?text=Bird3\"]"
            ),
            CompetitionEntryEntity(
                competitionId = "comp_002",
                title = "Regional Beauty Contest: East",
                description = "Regional showcase for East Coast breeders.",
                startTime = now + (3 * day),
                endTime = now + (10 * day),
                region = "East Region",
                status = CompetitionStatus.UPCOMING,
                participantCount = 45,
                prizePool = "$200 + Silver Badge"
            ),
             CompetitionEntryEntity(
                competitionId = "comp_003",
                title = "Best in Show: Kelso Special",
                description = "Specialized competition for Kelso breed only.",
                startTime = now - (10 * day),
                endTime = now - (1 * day),
                region = "Global",
                status = CompetitionStatus.COMPLETED,
                participantCount = 89,
                prizePool = "$500"
            )
        )
    }
}
