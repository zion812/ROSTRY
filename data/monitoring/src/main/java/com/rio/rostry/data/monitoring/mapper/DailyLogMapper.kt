package com.rio.rostry.data.monitoring.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.core.model.DailyLog
import com.rio.rostry.data.database.entity.DailyLogEntity

private val gson = Gson()

/**
 * Converts DailyLogEntity to domain model.
 */
fun DailyLogEntity.toDailyLog(): DailyLog {
    val medication = medicationJson?.let {
        try {
            gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            null
        }
    }
    
    val symptoms = symptomsJson?.let {
        try {
            gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            null
        }
    }
    
    val photos = photoUrls?.let {
        try {
            gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            it.split(",").map { url -> url.trim() }.filter { url -> url.isNotBlank() }
        }
    }
    
    return DailyLog(
        logId = this.logId,
        productId = this.productId,
        farmerId = this.farmerId,
        logDate = this.logDate,
        weightGrams = this.weightGrams,
        feedKg = this.feedKg,
        medication = medication,
        symptoms = symptoms,
        activityLevel = this.activityLevel,
        photoUrls = photos,
        notes = this.notes,
        temperature = this.temperature,
        humidity = this.humidity,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * Converts domain model to DailyLogEntity.
 */
fun DailyLog.toEntity(): DailyLogEntity {
    return DailyLogEntity(
        logId = this.logId,
        productId = this.productId,
        farmerId = this.farmerId,
        logDate = this.logDate,
        weightGrams = this.weightGrams,
        feedKg = this.feedKg,
        medicationJson = this.medication?.let { gson.toJson(it) },
        symptomsJson = this.symptoms?.let { gson.toJson(it) },
        activityLevel = this.activityLevel,
        photoUrls = this.photoUrls?.let { gson.toJson(it) },
        notes = this.notes,
        temperature = this.temperature,
        humidity = this.humidity,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
