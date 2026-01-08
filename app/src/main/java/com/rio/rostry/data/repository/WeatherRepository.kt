package com.rio.rostry.data.repository

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Weather data from Open-Meteo API
 */
data class WeatherData(
    val temperature: Double,    // Celsius
    val humidity: Int?,         // Percentage
    val weatherCode: Int?,      // WMO weather code
    val isHeatStress: Boolean,  // Temperature >= 32°C
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

/**
 * Repository for fetching weather data from Open-Meteo API.
 * 
 * Open-Meteo is a free, open-source weather API that requires no API key.
 * Free tier allows ~10,000 calls per day.
 * 
 * API docs: https://open-meteo.com/en/docs
 */
@Singleton
class WeatherRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    // Cache weather data for 30 minutes to reduce API calls
    private var cachedWeather: WeatherData? = null
    private var lastFetchTime: Long = 0
    private val CACHE_DURATION_MS = 30 * 60 * 1000L // 30 minutes
    
    /**
     * Get current weather for the user's location.
     * Falls back to default data if location or API is unavailable.
     */
    suspend fun getCurrentWeather(): WeatherData = withContext(Dispatchers.IO) {
        try {
            // Return cached data if fresh
            cachedWeather?.let { cached ->
                if (System.currentTimeMillis() - lastFetchTime < CACHE_DURATION_MS) {
                    Timber.d("Returning cached weather: ${cached.temperature}°C")
                    return@withContext cached
                }
            }
            
            // Get user location
            val location = getLastLocation()
            if (location == null) {
                Timber.w("Location unavailable, using fallback weather")
                return@withContext WeatherData.FALLBACK
            }
            
            // Fetch from Open-Meteo API
            val weather = fetchWeatherFromApi(location.latitude, location.longitude)
            cachedWeather = weather
            lastFetchTime = System.currentTimeMillis()
            
            Timber.d("Fetched weather: ${weather.temperature}°C, heat stress: ${weather.isHeatStress}")
            weather
        } catch (e: Exception) {
            Timber.e(e, "Error fetching weather")
            cachedWeather ?: WeatherData.FALLBACK
        }
    }
    
    /**
     * Get weather for a specific location (used for farm locations).
     */
    suspend fun getWeatherForLocation(latitude: Double, longitude: Double): WeatherData = 
        withContext(Dispatchers.IO) {
            try {
                fetchWeatherFromApi(latitude, longitude)
            } catch (e: Exception) {
                Timber.e(e, "Error fetching weather for location ($latitude, $longitude)")
                WeatherData.FALLBACK
            }
        }
    
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        try {
            val cancellationTokenSource = CancellationTokenSource()
            
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                cont.resume(location)
            }.addOnFailureListener { e ->
                Timber.w(e, "Failed to get location")
                cont.resume(null)
            }
            
            cont.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } catch (e: SecurityException) {
            Timber.w(e, "Location permission not granted")
            cont.resume(null)
        }
    }
    
    /**
     * Fetch weather from Open-Meteo API.
     * 
     * Example request:
     * https://api.open-meteo.com/v1/forecast?latitude=17.38&longitude=78.46&current=temperature_2m,relative_humidity_2m,weather_code
     */
    private fun fetchWeatherFromApi(latitude: Double, longitude: Double): WeatherData {
        val url = URL(
            "https://api.open-meteo.com/v1/forecast?" +
            "latitude=$latitude&longitude=$longitude" +
            "&current=temperature_2m,relative_humidity_2m,weather_code"
        )
        
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        
        try {
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Timber.w("Open-Meteo API returned $responseCode")
                return WeatherData.FALLBACK
            }
            
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            return parseWeatherResponse(response)
        } finally {
            connection.disconnect()
        }
    }
    
    private fun parseWeatherResponse(jsonString: String): WeatherData {
        val json = JSONObject(jsonString)
        val current = json.getJSONObject("current")
        
        val temperature = current.optDouble("temperature_2m", 28.0)
        val humidity = if (current.has("relative_humidity_2m")) {
            current.getInt("relative_humidity_2m")
        } else null
        val weatherCode = if (current.has("weather_code")) {
            current.getInt("weather_code")
        } else null
        
        return WeatherData(
            temperature = temperature,
            humidity = humidity,
            weatherCode = weatherCode,
            isHeatStress = temperature >= 32.0
        )
    }
    
    /**
     * Force refresh weather (ignores cache).
     */
    suspend fun refreshWeather(): WeatherData {
        lastFetchTime = 0 // Clear cache
        return getCurrentWeather()
    }
}
