package com.rio.rostry.domain.model.media

data class MediaTag(
    val tagId: String,
    val tagType: TagType,
    val value: String
)

enum class TagType {
    ASSET_ID,
    AGE_GROUP,
    SOURCE_TYPE
}

enum class AgeGroup(val displayName: String) {
    CHICK("Chick"),
    JUVENILE("Juvenile"),
    ADULT("Adult"),
    SENIOR("Senior")
}

enum class SourceType(val displayName: String) {
    FARM_LOG("Farm Log"),
    HEALTH_RECORD("Health Record"),
    GENERAL_ASSET_PHOTO("General Photo")
}
