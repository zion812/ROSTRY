package com.rio.rostry.domain.validation

import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.domain.error.MediaError.ValidationError
import com.rio.rostry.domain.model.media.AgeGroup
import com.rio.rostry.domain.model.media.MediaTag
import com.rio.rostry.domain.model.media.SourceType
import com.rio.rostry.domain.model.media.TagType
import javax.inject.Inject

class TagValidator @Inject constructor(
    private val farmAssetDao: FarmAssetDao
) {
    suspend fun validateTags(tags: List<MediaTag>): Result<Unit> {
        for (tag in tags) {
            when (tag.tagType) {
                TagType.ASSET_ID -> {
                    val assetExists = farmAssetDao.findById(tag.value) != null
                    if (!assetExists) return Result.failure(ValidationError.InvalidAssetId)
                }
                TagType.AGE_GROUP -> {
                    if (runCatching { AgeGroup.valueOf(tag.value) }.isFailure) {
                        return Result.failure(ValidationError.InvalidAgeGroup)
                    }
                }
                TagType.SOURCE_TYPE -> {
                    if (runCatching { SourceType.valueOf(tag.value) }.isFailure) {
                        return Result.failure(ValidationError.InvalidSourceType)
                    }
                }
            }
        }
        return Result.success(Unit)
    }
}
