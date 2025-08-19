package com.rio.rostry.data.models

data class HealthRecord(
    val type: String = "", // vaccination, treatment, etc.
    val date: Long = 0L,
    val notes: String = ""
)