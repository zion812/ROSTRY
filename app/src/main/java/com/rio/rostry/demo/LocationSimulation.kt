package com.rio.rostry.demo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationSimulation @Inject constructor() {
    data class Coordinate(val lat: Double, val lng: Double)

    // Andhra Pradesh sample waypoints (approximate)
    private val apWaypoints = listOf(
        Coordinate(16.5062, 80.6480), // Vijayawada
        Coordinate(16.3067, 80.4365), // Guntur
        Coordinate(17.6868, 83.2185), // Visakhapatnam
        Coordinate(16.9891, 82.2475), // Rajahmundry vicinity
        Coordinate(15.8281, 78.0373), // Kurnool
    )

    fun mockRoute(intervalMs: Long = 2000L): Flow<Coordinate> = flow {
        while (true) {
            for (c in apWaypoints) {
                emit(c)
                delay(intervalMs)
            }
        }
    }

    fun withinRadiusKm(center: Coordinate, point: Coordinate, radiusKm: Double): Boolean {
        val d = haversine(center.lat, center.lng, point.lat, point.lng)
        return d <= radiusKm
    }

    // Haversine distance in KM
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}
