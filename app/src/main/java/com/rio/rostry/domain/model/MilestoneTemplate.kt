package com.rio.rostry.domain.model

data class MilestoneTemplate(
    val id: String,
    val stage: String,
    val weekNumber: Int,
    val title: String,
    val description: String,
    val isRequired: Boolean = true,
    val alertDaysBefore: Int = 0 // Days before milestone to send alert
)