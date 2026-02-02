package com.rio.rostry.ui.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpertProfileViewModel @Inject constructor() : ViewModel() {
    
    data class Review(val authorName: String, val rating: Float, val content: String)
    
    data class UiState(
        val isLoading: Boolean = true,
        val expertId: String = "",
        val name: String = "",
        val title: String = "",
        val bio: String = "",
        val specializations: List<String> = emptyList(),
        val yearsExperience: Int = 0,
        val consultationsCompleted: Int = 0,
        val rating: Float = 0f,
        val reviewCount: Int = 0,
        val hourlyRate: Double = 0.0,
        val isAvailableNow: Boolean = false,
        val reviews: List<Review> = emptyList(),
        val error: String? = null
    )
    
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    
    fun loadExpert(expertId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, expertId = expertId) }
            delay(500)
            
            _state.update { it.copy(
                isLoading = false,
                name = "Dr. Ramesh Veterinary",
                title = "Senior Poultry Veterinarian",
                bio = "20+ years of experience in poultry health, disease prevention, and farm management. Specialized in broiler and layer health optimization. Available for on-site visits and virtual consultations.",
                specializations = listOf("Disease Prevention", "Vaccination Protocols", "Biosecurity", "Nutrition Planning", "Hatchery Management"),
                yearsExperience = 22,
                consultationsCompleted = 1456,
                rating = 4.8f,
                reviewCount = 312,
                hourlyRate = 1500.0,
                isAvailableNow = true,
                reviews = listOf(
                    Review("Sharma Farms", 5f, "Excellent advice on vaccination schedule. Our mortality reduced by 40%!"),
                    Review("Green Valley Poultry", 5f, "Very knowledgeable and patient. Helped us set up biosecurity protocols."),
                    Review("Sunrise Hatchery", 4f, "Good consultation, provided detailed report after visit.")
                )
            ) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpertProfileScreen(
    expertId: String,
    viewModel: ExpertProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(expertId) {
        viewModel.loadExpert(expertId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expert Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Expert Header
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium, modifier = Modifier.size(80.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(state.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(state.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${state.rating}", fontWeight = FontWeight.Bold)
                                Text(" (${state.reviewCount} reviews)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (state.isAvailableNow) {
                                Spacer(Modifier.height(8.dp))
                                Surface(color = Color(0xFF4CAF50).copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
                                    Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Circle, null, Modifier.size(8.dp), tint = Color(0xFF4CAF50))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Available Now", style = MaterialTheme.typography.labelSmall, color = Color(0xFF4CAF50))
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Stats Row
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(Modifier.weight(1f), "${state.yearsExperience}+", "Years Exp", Color(0xFF2196F3))
                        StatCard(Modifier.weight(1f), "${state.consultationsCompleted}", "Consultations", Color(0xFF4CAF50))
                        StatCard(Modifier.weight(1f), "₹${state.hourlyRate.toInt()}", "Per Hour", Color(0xFFFF9800))
                    }
                }
                
                // Bio
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("About", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(state.bio, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                
                // Specializations
                item {
                    Text("Specializations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.specializations) { spec ->
                            SuggestionChip(onClick = { }, label = { Text(spec) })
                        }
                    }
                }
                
                // Reviews
                item {
                    Text("Reviews", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(state.reviews) { review ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(review.authorName, fontWeight = FontWeight.Medium)
                                Row {
                                    repeat(review.rating.toInt()) {
                                        Icon(Icons.Default.Star, null, Modifier.size(14.dp), tint = Color(0xFFFFC107))
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(review.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                
                // Book Button
                item {
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.VideoCall, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Book Consultation")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String, color: Color) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
