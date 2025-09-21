package com.rio.rostry.utils

import kotlin.math.*

object ValidationUtils {
    enum class AgeGroup { DAY_OLD, CHICK, GROWER, ADULT }

    fun ageGroup(birthDateMillis: Long?, now: Long = System.currentTimeMillis()): AgeGroup? {
        birthDateMillis ?: return null
        val days = floor(((now - birthDateMillis).coerceAtLeast(0)) / (1000.0 * 60 * 60 * 24)).toInt()
        return when {
            days <= 7 -> AgeGroup.DAY_OLD
            days <= 35 -> AgeGroup.CHICK // up to 5 weeks
            days <= 140 -> AgeGroup.GROWER // up to 20 weeks
            else -> AgeGroup.ADULT
        }
    }

    fun requiresVaccinationProof(birthDateMillis: Long?, now: Long = System.currentTimeMillis()): Boolean {
        val group = ageGroup(birthDateMillis, now) ?: return false
        return group == AgeGroup.DAY_OLD || group == AgeGroup.CHICK
    }

    fun requiresFamilyTree(breedingStatus: String?): Boolean {
        return breedingStatus?.lowercase() in setOf("breeding", "breeder", "stud")
    }

    // Haversine distance in kilometers
    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun withinDeliveryRadius(productLat: Double?, productLon: Double?, buyerLat: Double?, buyerLon: Double?, maxKm: Double = 50.0): Boolean {
        if (productLat == null || productLon == null || buyerLat == null || buyerLon == null) return false
        return distanceKm(productLat, productLon, buyerLat, buyerLon) <= maxKm
    }
}
