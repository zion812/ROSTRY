package com.rio.rostry.data.local.db

import androidx.room.TypeConverter

class OutboxTypeConverters {
    @TypeConverter
    fun fromNullableInt(value: Int?): Int = value ?: 0

    @TypeConverter
    fun toNullableInt(value: Int): Int? = value
}
