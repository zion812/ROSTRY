package com.rio.rostry.ui.enthusiast.cards

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.enthusiast.components.RoosterCardView
import com.rio.rostry.ui.components.ConfettiEffect
import com.rio.rostry.ui.components.GradientButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoosterCardScreen(
    productId: String,
    onNavigateBack: () -> Unit
) {
    val vm: RoosterCardViewModel = hiltViewModel()
    val product by vm.product.collectAsState()
    val loading by vm.loading.collectAsState()
    val selectedTemplate by vm.selectedTemplate.collectAsState()
    val context = LocalContext.current
    var captureView by remember { mutableStateOf<View?>(null) }
    var enableTilt by remember { mutableStateOf(true) }
    
    // Celebration state (Comment 3)
    var showConfetti by remember { mutableStateOf(false) }
    var shareSuccess by remember { mutableStateOf(false) }
    
    // Hide confetti after animation
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(3000)
            showConfetti = false
        }
    }

    LaunchedEffect(productId) {
        vm.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rooster Card") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Randomize template button (Comment 3)
                    IconButton(onClick = { vm.randomTemplate() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Random Style")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Confetti overlay (Comment 3)
            ConfettiEffect(visible = showConfetti)
            
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                product?.let { currentProduct ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Create your shareable card!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        // Template Selector (Comment 3)
                        CardTemplateSelector(
                            selectedTemplate = selectedTemplate,
                            onTemplateSelected = { vm.selectTemplate(it) }
                        )
                        
                        // Tilt toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("3D Tilt Effect", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = enableTilt,
                                onCheckedChange = { enableTilt = it }
                            )
                        }
                        
                        // Card preview with selected template (Comment 3)
                        Box(Modifier.fillMaxWidth()) {
                            RoosterCardView(
                                product = currentProduct,
                                template = selectedTemplate,
                                enableTilt = enableTilt,
                                onReadyToCapture = { view -> captureView = view }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Share button with celebration (Comment 3)
                        GradientButton(
                            text = if (shareSuccess) "✓ Shared!" else "Share Card",
                            onClick = {
                                captureView?.let { view ->
                                    vm.captureAndShare(view, context)
                                    showConfetti = true
                                    shareSuccess = true
                                }
                            },
                            enabled = captureView != null && !shareSuccess,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Reset share state after success
                        LaunchedEffect(shareSuccess) {
                            if (shareSuccess) {
                                delay(3000)
                                shareSuccess = false
                            }
                        }
                    }
                } ?: run {
                    Text("Product not found", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

