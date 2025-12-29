package com.rio.rostry.ui.enthusiast.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

/**
 * ClaimTransferScreen - Where recipients enter a transfer code to claim a bird.
 * 
 * Flow:
 * 1. Enter 6-digit code
 * 2. View bird details preview
 * 3. Confirm claim
 * 4. Bird is transferred to recipient
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimTransferScreen(
    onNavigateBack: () -> Unit,
    onClaimSuccess: () -> Unit,
    viewModel: ClaimTransferViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val codeInput by viewModel.codeInput.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Claim Transfer", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is ClaimTransferUiState.EnterCode -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(32.dp))
                    
                    // Header icon
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        "Enter Transfer Code",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        "Enter the 6-character code from the bird's current owner",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(40.dp))
                    
                    // Code input boxes
                    CodeInputField(
                        code = codeInput,
                        onCodeChange = { viewModel.updateCode(it) },
                        focusRequester = focusRequester,
                        onDone = {
                            focusManager.clearFocus()
                            if (codeInput.length == 6) {
                                viewModel.claimTransfer()
                            }
                        }
                    )
                    
                    // Error message
                    if (state.errorMessage != null) {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    state.errorMessage,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Claim button
                    Button(
                        onClick = { viewModel.claimTransfer() },
                        enabled = codeInput.length == 6 && !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Claim Bird")
                        }
                    }
                }
            }
            
            is ClaimTransferUiState.Preview -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Success indicator
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        "Code Verified!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Bird preview card
                    ClaimPreviewCard(
                        birdName = state.birdName,
                        birdBreed = state.birdBreed,
                        birdImageUrl = state.birdImageUrl
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Confirmation notice
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Once you confirm, this bird will be added to your flock with its full pedigree and history.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Confirm button
                    Button(
                        onClick = { viewModel.confirmClaim() },
                        enabled = !state.isConfirming,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        if (state.isConfirming) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Pets, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add to My Flock")
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // Cancel button
                    TextButton(onClick = onNavigateBack) {
                        Text("Cancel")
                    }
                }
            }
            
            is ClaimTransferUiState.Success -> {
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
                        // Celebration animation placeholder
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Celebration,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp),
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Text(
                            "🎉 Transfer Complete!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Text(
                            "${state.birdName} is now part of your flock",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(Modifier.height(32.dp))
                        
                        Button(
                            onClick = onClaimSuccess,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View My Flock")
                        }
                    }
                }
            }
            
            is ClaimTransferUiState.Error -> {
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
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(state.message, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.reset() }) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeInputField(
    code: String,
    onCodeChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onDone: () -> Unit
) {
    // Hidden text field for input
    BasicTextField(
        value = code,
        onValueChange = { newValue ->
            if (newValue.length <= 6 && newValue.all { it.isLetterOrDigit() }) {
                onCodeChange(newValue.uppercase())
            }
        },
        modifier = Modifier.focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    val char = code.getOrNull(index)?.toString() ?: ""
                    val isFocused = index == code.length
                    
                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .border(
                                width = 2.dp,
                                color = if (isFocused) 
                                    MaterialTheme.colorScheme.primary 
                                else if (char.isNotEmpty())
                                    MaterialTheme.colorScheme.outline
                                else 
                                    MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        color = if (char.isNotEmpty()) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else
                            MaterialTheme.colorScheme.surface
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ClaimPreviewCard(
    birdName: String,
    birdBreed: String?,
    birdImageUrl: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF8BC34A)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (birdImageUrl != null) {
                    AsyncImage(
                        model = birdImageUrl,
                        contentDescription = birdName,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Pets,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
                
                Spacer(Modifier.width(20.dp))
                
                Column {
                    Text(
                        "You're receiving",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        birdName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    birdBreed?.let {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
