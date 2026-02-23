package com.rio.rostry.domain.model.media

data class MediaFilter(
    val assetId: String? = null,
    val ageGroups: Set<AgeGroup> = emptySet(),
    val sourceTypes: Set<SourceType> = emptySet(),
    val mediaTypes: Set<MediaType> = emptySet(),
    val dateRange: DateRange? = null
)

data class DateRange(
    val startDate: Long,
    val endDate: Long
)

data class PaginationState(
    val pageSize: Int = 20,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false
)
