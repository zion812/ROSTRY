package com.rio.rostry.domain.farm.repository

data class WeatherData(
    val temperature: Double,
    val humidity: Int?,
    val weatherCode: Int?,
    val isHeatStress: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        val FALLBACK = WeatherData(
            temperature = 28.0,
            humidity = null,
            weatherCode = null,
            isHeatStress = false
        )
    }
}

interface WeatherRepository {
    suspend fun getCurrentWeather(): WeatherData
    suspend fun getWeatherForLocation(latitude: Double, longitude: Double): WeatherData
    suspend fun refreshWeather(): WeatherData
}
