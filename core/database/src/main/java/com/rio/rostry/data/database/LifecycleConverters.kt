package com.rio.rostry.data.database

import androidx.room.TypeConverter
import com.rio.rostry.domain.model.LifecycleStage

class LifecycleConverters {
  @TypeConverter
  fun toStage(value: String?): LifecycleStage? = value?.let { runCatching { LifecycleStage.valueOf(it) }.getOrNull() }

  @TypeConverter
  fun fromStage(stage: LifecycleStage?): String? = stage?.name
}
