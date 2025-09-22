package com.rio.rostry.utils

import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlin.math.*

object VerificationUtils {
    // Haversine distance in meters
    fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun withinRadius(lat1: Double, lon1: Double, lat2: Double, lon2: Double, radiusMeters: Double = 100.0): Boolean {
        if (lat1.isNaN() || lon1.isNaN() || lat2.isNaN() || lon2.isNaN()) return false
        return distanceMeters(lat1, lon1, lat2, lon2) <= radiusMeters
    }

    data class PhotoMeta(
        val datetime: String?,
        val lat: Double?,
        val lon: Double?,
        val model: String?,
    )

    // Parse EXIF from a stream path; caller should provide InputStream
    fun parseExif(exif: ExifInterface): PhotoMeta {
        val datetime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL) ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
        val latLong = FloatArray(2)
        val hasLatLon = exif.getLatLong(latLong)
        val model = exif.getAttribute(ExifInterface.TAG_MODEL)
        return PhotoMeta(
            datetime = datetime,
            lat = if (hasLatLon) latLong[0].toDouble() else null,
            lon = if (hasLatLon) latLong[1].toDouble() else null,
            model = model
        )
    }
}
