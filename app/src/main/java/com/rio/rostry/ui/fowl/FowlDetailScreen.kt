package com.rio.rostry.ui.fowl

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rio.rostry.R
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.data.models.FowlRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FowlDetailScreen(
    fowlId: String,
    viewModel: FowlViewModel = hiltViewModel(),
    onNavigateToRecordCreation: (String) -> Unit,
    onNavigateToFowlDetail: (String) -> Unit,
    onNavigateToTransfer: (String) -> Unit,
    onNavigateToHistory: (String) -> Unit
) {
    val fowl by viewModel.selectedFowl.collectAsState()
    val records by viewModel.fowlRecords.collectAsState()
    val sire by viewModel.sire.collectAsState()
    val dam by viewModel.dam.collectAsState()
    val offspring by viewModel.offspring.collectAsState()

    LaunchedEffect(fowlId) {
        viewModel.getFowlById(fowlId)
        viewModel.getFowlRecords(fowlId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(fowl?.name ?: "Fowl Details") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToRecordCreation(fowlId) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Record")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (fowl == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                FowlDetailContent(fowl!!, records, sire, dam, offspring, onNavigateToFowlDetail, onNavigateToTransfer, onNavigateToHistory)
            }
        }
    }
}

@Composable
fun FowlDetailContent(
    fowl: Fowl,
    records: List<FowlRecord>,
    sire: Fowl?,
    dam: Fowl?,
    offspring: List<Fowl>,
    onNavigateToFowlDetail: (String) -> Unit,
    onNavigateToTransfer: (String) -> Unit,
    onNavigateToHistory: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fowl.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_placeholder_image),
                error = painterResource(R.drawable.ic_placeholder_image),
                contentDescription = fowl.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = fowl.name, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            Text(text = "Status: ${fowl.status}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(text = "Group: ${fowl.group ?: "N/A"}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
            Text(text = "Birth Date: ${fowl.birthDate}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToTransfer(fowl.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Transfer Ownership")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onNavigateToHistory(fowl.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Transfer History")
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { FamilySection(sire = sire, dam = dam, offspring = offspring, onNavigateToFowlDetail = onNavigateToFowlDetail) }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "QR Code", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fowl.qrCodeUrl)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_placeholder_image),
                        error = painterResource(R.drawable.ic_placeholder_image),
                        contentDescription = "QR Code for ${fowl.name}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { Text(text = "Records", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall) }
        
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(records) { record ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = record.type, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Text(text = record.date.toString(), style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = record.details, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    record.notes?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Notes: $it", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun FamilySection(
    sire: Fowl?,
    dam: Fowl?,
    offspring: List<Fowl>,
    onNavigateToFowlDetail: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Family", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Parents
            Text(
                text = "Sire: ${sire?.name ?: "Unknown"}",
                modifier = Modifier.clickable(enabled = sire != null) { sire?.id?.let { onNavigateToFowlDetail(it) } }
            )
            Text(
                text = "Dam: ${dam?.name ?: "Unknown"}",
                modifier = Modifier.clickable(enabled = dam != null) { dam?.id?.let { onNavigateToFowlDetail(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Offspring
            Text(text = "Offspring", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
            if (offspring.isEmpty()) {
                Text(text = "None")
            } else {
                offspring.forEach { child ->
                    Text(
                        text = child.name,
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToFowlDetail(child.id) }.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
