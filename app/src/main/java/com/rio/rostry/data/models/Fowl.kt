package com.rio.rostry.data.models

import com.google.firebase.firestore.DocumentId

data class Fowl(
    @DocumentId
    val fowlId: String = "",
    val ownerUserId: String = "",
    val name: String = "",
    val breed: String = "",
    val birthDate: Long = 0L,
    val parentIds: List<String> = emptyList(),
    val healthRecords: List<HealthRecord> = emptyList(),
    val status: String = "growing",
    val photoUrl: String = "",
    val lineageNotes: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)