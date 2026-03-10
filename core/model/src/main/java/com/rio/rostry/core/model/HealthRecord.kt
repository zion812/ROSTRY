package com.rio.rostry.core.model

/**
 * Health record model for animal health tracking.
 */
data class HealthRecord(
    val id: String,
    val assetId: String,
    val farmerId: String,
    val recordType: HealthRecordType,
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val veterinarianId: String? = null,
    val cost: Double? = null,
    val attachments: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class HealthRecordType {
    VACCINATION,
    TREATMENT,
    CHECKUP,
    DIAGNOSIS,
    TEST_RESULT
}
