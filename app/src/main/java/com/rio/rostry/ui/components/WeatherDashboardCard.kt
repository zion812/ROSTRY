package com.rio.rostry.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.WeatherData
import com.rio.rostry.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData = _weatherData.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    init {
        loadWeather()
    }
    
    fun loadWeather() {
        viewModelScope.launch {
            _isLoading.value = true
            val weather = weatherRepository.getCurrentWeather()
            _weatherData.value = weather
            _isLoading.value = false
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            val weather = weatherRepository.refreshWeather()
            _weatherData.value = weather
            _isLoading.value = false
        }
    }
}

/**
 * Weather dashboard card for farmer home screen.
 * Shows current temperature, humidity, conditions, and heat stress alerts.
 */
@Composable
fun WeatherDashboardCard(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weather by viewModel.weatherData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Determine gradient based on temperature/conditions
    val gradient = remember(weather) {
        when {
            weather?.isHeatStress == true -> Brush.linearGradient(
                colors = listOf(Color(0xFFFF6B6B), Color(0xFFFFA94D))
            )
            (weather?.temperature ?: 28.0) >= 30 -> Brush.linearGradient(
                colors = listOf(Color(0xFFFF9800), Color(0xFFFFEB3B))
            )
            (weather?.temperature ?: 28.0) <= 18 -> Brush.linearGradient(
                colors = listOf(Color(0xFF42A5F5), Color(0xFF26C6DA))
            )
            else -> Brush.linearGradient(
                colors = listOf(Color(0xFF4FC3F7), Color(0xFF81C784))
            )
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Current Weather",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Spacer(Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = weather?.let { 
                                    String.format("%.0f", it.temperature) 
                                } ?: "--",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "°C",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        // Refresh button
                        IconButton(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .size(32.dp)
                                .alpha(if (isLoading) 0.5f else 1f)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                "Refresh",
                                tint = Color.White
                            )
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Weather icon
                        WeatherIcon(
                            weatherCode = weather?.weatherCode,
                            temperature = weather?.temperature ?: 28.0,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Additional info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Humidity
                    weather?.humidity?.let { humidity ->
                        WeatherInfoChip(
                            icon = Icons.Default.WaterDrop,
                            value = "$humidity%",
                            label = "Humidity"
                        )
                    }
                    
                    // Condition
                    WeatherInfoChip(
                        icon = Icons.Default.Thermostat,
                        value = getTemperatureLabel(weather?.temperature ?: 28.0),
                        label = "Condition"
                    )
                }
                
                // Heat stress alert
                if (weather?.isHeatStress == true) {
                    Spacer(Modifier.height(12.dp))
                    HeatStressAlert()
                }
            }
        }
    }
}

@Composable
private fun WeatherIcon(
    weatherCode: Int?,
    temperature: Double,
    modifier: Modifier = Modifier
) {
    val icon = when {
        weatherCode == null -> Icons.Default.WbSunny
        weatherCode == 0 -> Icons.Default.WbSunny  // Clear sky
        weatherCode in 1..3 -> Icons.Default.WbCloudy  // Partly cloudy
        weatherCode in 45..48 -> Icons.Default.Cloud  // Fog
        weatherCode in 51..67 || weatherCode in 80..82 -> Icons.Default.WaterDrop  // Rain
        weatherCode in 71..77 || weatherCode in 85..86 -> Icons.Default.AcUnit  // Snow
        else -> Icons.Default.Cloud
    }
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun WeatherInfoChip(
    icon: ImageVector,
    value: String,
    label: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Column {
            Text(
                value,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun HeatStressAlert() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(12.dp)
            .alpha(alpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Warning,
            "Heat Stress Alert",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                "Heat Stress Alert",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Ensure birds have access to cool water and shade",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp
            )
        }
    }
}

private fun getTemperatureLabel(temp: Double): String = when {
    temp >= 35 -> "Very Hot"
    temp >= 32 -> "Hot"
    temp >= 28 -> "Warm"
    temp >= 22 -> "Pleasant"
    temp >= 18 -> "Cool"
    else -> "Cold"
}

/**
 * Compact weather display for headers/toolbars.
 */
@Composable
fun WeatherCompactDisplay(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weather by viewModel.weatherData.collectAsState()
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        weather?.let { data ->
            Icon(
                if (data.isHeatStress) Icons.Default.Warning else Icons.Default.Thermostat,
                null,
                tint = if (data.isHeatStress) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                String.format("%.0f°C", data.temperature),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (data.isHeatStress) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
