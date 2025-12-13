package com.rio.rostry.ui.general.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.EducationalContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalContentDetailScreen(
    contentId: String,
    viewModel: GeneralExploreViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onBack: () -> Unit
) {
    // In a real app, we'd fetch the specific content by ID. 
    // For this demo, we'll find it in the existing list.
    val uiState = viewModel.uiState.collectAsState()
    val contentItem = uiState.value.educationalContent.find { it.id == contentId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(contentItem?.type?.name ?: "Learn") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (contentItem == null) {
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text("Content not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (contentItem.imageUrl != null) {
                    AsyncImage(
                        model = contentItem.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = contentItem.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = contentItem.description,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    // Placeholder for rich content (Markdown or HTML)
                    Text(
                        text = getDummyContent(contentItem.id),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

private fun getDummyContent(id: String): String {
    return when (id) {
        "1" -> """
            Native chicken breeds like the Asil and Kadaknath are renowned not just for their cultural heritage but for their superior nutritional profile.
            
            **Health Benefits:**
            - **Higher Protein:** Native breeds often have a higher protein-to-fat ratio compared to broilers.
            - **Rich in Iron:** Especially breeds like Kadaknath are known for high iron content.
            - **Low Cholesterol:** Studies suggest lower cholesterol levels in native meat.
            
            **Taste Profile:**
            The meat is firmer and has a distinct, gamey flavor that absorbs spices well, making it perfect for traditional curries.
        """.trimIndent()
        "2" -> """
            Selecting the right bird is crucial for your dish.
            
            **6-10 Months (Tender):**
            - Best for: Fry, Roast, Grill.
            - Texture: Soft, juicy.
            
            **12+ Months (Mature):**
            - Best for: Slow-cooked curries, Soups.
            - Texture: Firm, rich flavor.
            
            **Tip:** Look for bright eyes and smooth feathers when selecting a live bird.
        """.trimIndent()
        "3" -> """
            Starting a backyard farm is easier than you think!
            
            **1. Space:** You need about 2-3 sq ft per bird in the coop and 8-10 sq ft in the run.
            **2. Shelter:** Ensure it's predator-proof and well-ventilated.
            **3. Feed:** A mix of kitchen scraps, grains, and greens works wonders.
            
            **Recommended Breeds:**
            - **Vanaraja:** Dual purpose (eggs & meat), hardy.
            - **Gramapriya:** Excellent for backyard eggs.
        """.trimIndent()
        else -> "Content coming soon..."
    }
}
