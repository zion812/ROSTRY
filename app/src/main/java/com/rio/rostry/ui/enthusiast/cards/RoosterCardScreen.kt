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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.enthusiast.components.RoosterCardView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoosterCardScreen(
    productId: String,
    onNavigateBack: () -> Unit
) {
    val vm: RoosterCardViewModel = hiltViewModel()
    val product by vm.product.collectAsState()
    val loading by vm.loading.collectAsState()
    val context = LocalContext.current
    var captureView by remember { mutableStateOf<View?>(null) }

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
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                product?.let { currentProduct ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Share this card to WhatsApp status!")
                        Spacer(Modifier.height(16.dp))
                        
                        // Card container with slight shadow/elevation visually handled by the card itself
                        Box(Modifier.fillMaxWidth()) {
                            RoosterCardView(
                                product = currentProduct,
                                onReadyToCapture = { view -> captureView = view }
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        Button(
                            onClick = {
                                captureView?.let { view ->
                                    vm.captureAndShare(view, context)
                                }
                            },
                            enabled = captureView != null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share Card")
                        }
                    }
                } ?: run {
                    Text("Product not found", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
