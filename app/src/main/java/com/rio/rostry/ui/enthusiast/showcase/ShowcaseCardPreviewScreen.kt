package com.rio.rostry.ui.enthusiast.showcase

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.domain.pedigree.PedigreeCompleteness

/**
 * ShowcaseCardPreviewScreen - Full screen preview of a generated showcase card.
 * 
 * Features:
 * - Preview of the shareable card
 * - Share to social media
 * - Download to gallery
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseCardPreviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShowcaseCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Showcase Card", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF1a1a2e)
    ) { padding ->
        when (val state = uiState) {
            is ShowcaseCardUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFFFD700))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Generating showcase card...",
                            color = Color.White
                        )
                    }
                }
            }
            is ShowcaseCardUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            null,
                            Modifier.size(48.dp),
                            tint = Color.Red
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(state.message, color = Color.White, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.regenerate() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is ShowcaseCardUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Card preview (scaled down)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Image(
                            bitmap = state.cardBitmap.asImageBitmap(),
                            contentDescription = "Showcase card for ${state.birdName}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Customization Controls
                    val config by viewModel.config.collectAsState()
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2a2a4a))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Theme", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            
                            // Theme Selector (Horizontal Scroll)
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                com.rio.rostry.domain.showcase.ShowcaseTheme.entries.forEach { theme ->
                                    FilterChip(
                                        selected = config.theme == theme,
                                        onClick = { viewModel.updateConfig(config.copy(theme = theme)) },
                                        label = { Text(theme.label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFFFD700),
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0xFF16213e),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }
                            
                            Spacer(Modifier.height(16.dp))
                            
                            // Toggles
                            Text("Show Details", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    ConfigToggle("Wins/Stats", config.showWins) { viewModel.updateConfig(config.copy(showWins = it)) }
                                    ConfigToggle("Age", config.showAge) { viewModel.updateConfig(config.copy(showAge = it)) }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    ConfigToggle("Pedigree Badge", config.showPedigreeBadge) { viewModel.updateConfig(config.copy(showPedigreeBadge = it)) }
                                    ConfigToggle("Vaccine Badge", config.showVaccinationBadge) { viewModel.updateConfig(config.copy(showVaccinationBadge = it)) }
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Share buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Share to WhatsApp
                        Button(
                            onClick = { viewModel.shareToWhatsApp(context) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366)
                            )
                        ) {
                            Icon(
                                Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("WhatsApp")
                        }
                        
                        // Share to Instagram
                        Button(
                            onClick = { viewModel.shareToInstagram(context) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE4405F)
                            )
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Instagram")
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // General share
                    OutlinedButton(
                        onClick = { viewModel.share(context) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFFD700)
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Share to Other Apps")
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // Save to gallery
                    TextButton(
                        onClick = { viewModel.saveToGallery(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Save to Gallery", color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFD700),
                checkedTrackColor = Color(0xFF3E2723),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
            ),
            modifier = Modifier.scale(0.8f)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, color = Color.White, fontSize = 14.sp)
    }
}

/**
 * Inline Showcase Card Preview composable for embedding in other screens.
 * Shows a miniature preview of what the showcase card will look like.
 */
@Composable
fun ShowcaseCardMiniPreview(
    bird: ProductEntity,
    completeness: PedigreeCompleteness,
    vaccinationCount: Int,
    modifier: Modifier = Modifier,
    onGenerateCard: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 16f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1a1a2e),
                            Color(0xFF16213e),
                            Color(0xFF0f3460)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Bird photo
                if (bird.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = bird.imageUrls.first(),
                        contentDescription = bird.name,
                        modifier = Modifier
                            .size(120.dp) // Increased size
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(120.dp), // Increased size
                        shape = CircleShape,
                        color = Color(0xFF2a2a4a)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🐔", fontSize = 32.sp)
                        }
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                // Name
                Text(
                    bird.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                // Breed
                bird.breed?.let {
                    Text(
                        it,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                // Badges row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniPreviewBadge(
                        icon = "🌳",
                        text = completeness.label.split(" ").first(),
                        color = Color(completeness.badgeColor)
                    )
                    MiniPreviewBadge(
                        icon = "💉",
                        text = if (vaccinationCount > 0) "$vaccinationCount" else "—",
                        color = if (vaccinationCount > 0) Color(0xFF4CAF50) else Color.Gray
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Generate button
                Button(
                    onClick = onGenerateCard,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF3E2723)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Generate Card",
                        color = Color(0xFF3E2723),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // ROSTRY branding
            Text(
                "ROSTRY",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                color = Color(0xFFFFD700).copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MiniPreviewBadge(icon: String, text: String, color: Color) {
    Surface(
        shape = CircleShape, // Pill shape
        color = color.copy(alpha = 0.15f) // More subtle background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
