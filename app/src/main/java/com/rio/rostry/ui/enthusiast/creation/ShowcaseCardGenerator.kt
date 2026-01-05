package com.rio.rostry.ui.enthusiast.creation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rio.rostry.ui.enthusiast.components.RoosterWweCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseCardGeneratorScreen(
    birdId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Mock Data (In real app, fetch from ViewModel based on birdId)
    val name = "King Slayer" // Placeholder
    val breed = "Kelso"
    var aggression by remember { mutableFloatStateOf(0.8f) }
    var stamina by remember { mutableFloatStateOf(0.7f) }
    var power by remember { mutableFloatStateOf(0.9f) }
    
    // Capture state
    // Note: Reliable Composable capture usually requires a library like 'capturable' 
    // or wrapping the content in a View to draw it. 
    // For this MVP, we will use a simplified approach: The user will take a screenshot 
    // or we simulate the share intent sharing text/link until the bitmap logic is perfect,
    // OR we use the View-based capture tick if feasible.
    
    // Strategy: We will render the card inside a ComposeView that we can reference?
    // Actually, let's keep it simple for MVP Phase 3:
    // Just show the card and allow editing stats. The 'Share' button will currently just
    // toast/log "Image generation requires 'capturable' lib or View hook".
    // ... WAIT, I want to deliver value. I can use the View.drawToBitmap approach on the compose root view?
    // No, that captures whole screen.
    
    // Let's implement the UI first so the user can interact.
    
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
                        // Placeholder Share
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview
            Text("Preview", style = MaterialTheme.typography.titleMedium)
            
            // The Card to Capture
            RoosterWweCard(
                name = name,
                breed = breed,
                imageUrl = null, // TODO: Pass real image
                wins = 12, draws = 1, losses = 0,
                aggression = aggression,
                stamina = stamina,
                power = power,
                modifier = Modifier.fillMaxWidth() // Let it fill width, aspect ratio handled internally
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
                    // MVP: Just share text description until bitmap capture is rigorous
                    val shareText = "Check out $name ($breed) on ROSTRY! 🏆 Wins: 12 | Power: ${(power*100).toInt()}"
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Text("Share Card (Text for Phase 3 MVP)", modifier = Modifier.padding(start = 8.dp))
            }
            Text(
                "Note: Image generation coming in next update.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
