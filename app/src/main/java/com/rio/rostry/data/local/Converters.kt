package com.rio.rostry.data.local

import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromGeoPoint(value: String?): GeoPoint? {
        return value?.split(",")?.let { latLng ->
            if (latLng.size == 2) {
                GeoPoint(latLng[0].toDouble(), latLng[1].toDouble())
            } else {
                null
            }
        }
    }

    @TypeConverter
    fun geoPointToString(geoPoint: GeoPoint?): String? {
        return geoPoint?.let { "${it.latitude},${it.longitude}" }
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}
