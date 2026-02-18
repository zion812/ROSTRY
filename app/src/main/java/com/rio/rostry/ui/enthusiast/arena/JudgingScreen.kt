package com.rio.rostry.ui.enthusiast.arena

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.enthusiast.digitalfarm.studio.StudioViewport
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.rio.rostry.ui.components.LoadingScreen

import com.rio.rostry.domain.model.parseAppearanceFromJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JudgingScreen(
    competitionId: String,
    onNavigateBack: () -> Unit,
    viewModel: JudgingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(competitionId) {
        viewModel.loadQueue(competitionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Judging Arena") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else if (uiState.isFinished) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Judging Complete!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Return to Arena")
                    }
                }
            } else {
                uiState.currentPair?.let { (participant, product) ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Progress Bar
                        LinearProgressIndicator(
                            progress = uiState.progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // 3D Viewport
                        Box(modifier = Modifier.weight(1f)) {
                            // Convert product back to appearance for rendering
                            val appearance = remember(product) {
                                parseAppearanceFromJson(product.metadataJson) ?: com.rio.rostry.domain.model.BirdAppearance()
                            }
                            
                            StudioViewport(
                                appearance = appearance,
                                selectedTab = 0, // General view
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        
                        // Voting Controls
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Judging: ${participant.birdName} (${participant.breed})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Score: ${uiState.voteScore.toInt()}/10")
                                Slider(
                                    value = uiState.voteScore,
                                    onValueChange = { viewModel.updateScore(it) },
                                    valueRange = 0f..10f,
                                    steps = 9
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.submitVote(uiState.voteScore) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !uiState.isSubmitting
                                ) {
                                    Text(if (uiState.isSubmitting) "Submitting..." else "Submit Vote")
                                }
                            }
                        }
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No participants to judge.")
                    }
                }
            }
        }
    }
}
