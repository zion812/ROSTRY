package com.rio.rostry.ui.enthusiast.creation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.enthusiast.components.RoosterWweCard
import com.rio.rostry.domain.showcase.ShowcaseCard
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseCardGeneratorScreen(
    birdId: String, // Kept for navigation consistency, though VM uses SavedStateHandle
    onNavigateBack: () -> Unit,
    viewModel: ShowcaseCardGeneratorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Stats state (Still local as they are potentially user-customizable for the card)
    var aggression by remember { mutableFloatStateOf(0.8f) }
    var stamina by remember { mutableFloatStateOf(0.7f) }
    var power by remember { mutableFloatStateOf(0.9f) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rooster Card Studio") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.generateCard { card ->
                            shareCardFile(context, card)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Preview
                    Text("Preview", style = MaterialTheme.typography.titleMedium)
                    
                    val product = uiState.product
                    val name = product?.name ?: "Loading..."
                    val breed = product?.breed ?: "Unknown Breed"
                    val imageUrl = product?.imageUrls?.firstOrNull()

                    // The Card to Capture
                    RoosterWweCard(
                        name = name,
                        breed = breed,
                        imageUrl = imageUrl, 
                        wins = 12, draws = 1, losses = 0, // Placeholder stats until we fetch real ones
                        aggression = aggression,
                        stamina = stamina,
                        power = power,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Controls
                    Text("Adjust Stats", style = MaterialTheme.typography.titleMedium)
                    
                    StatSlider("Aggression", aggression) { aggression = it }
                    StatSlider("Stamina", stamina) { stamina = it }
                    StatSlider("Power", power) { power = it }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { 
                           viewModel.generateCard { card ->
                               shareCardFile(context, card)
                           }
                        },
                        enabled = !uiState.isGenerating,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp).height(20.dp), 
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Generating...")
                        } else {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Text("Share Card", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    
                    if (uiState.error != null) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

private fun shareCardFile(context: Context, card: ShowcaseCard) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        card.file
    )
    
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, """
            🐔 Meet ${card.bird.name}!
            ${card.bird.breed ?: ""}
            
            Registered on ROSTRY
            #ROSTRY #Poultry #${card.bird.breed?.replace(" ", "") ?: "Birds"}
        """.trimIndent())
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    
    context.startActivity(Intent.createChooser(shareIntent, "Share Showcase Card"))
}

@Composable
fun StatSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text("${(value * 100).toInt()}")
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}
