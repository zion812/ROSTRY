package com.rio.rostry.ui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoToolsScreen(vm: DemoToolsViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Demo & Testing Tools", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Enable Demo Mode")
            Switch(checked = ui.enabled, onCheckedChange = { vm.setEnabled(it) })
        }

        Divider()
        Text("Feature Flags", style = MaterialTheme.typography.titleMedium)
        FlagRow("Payments", ui.paymentsFlag) { vm.updateFlag("payments", it) }
        FlagRow("Social", ui.socialFlag) { vm.updateFlag("social", it) }
        FlagRow("Marketplace", ui.marketplaceFlag) { vm.updateFlag("marketplace", it) }
        FlagRow("Transfers", ui.transfersFlag) { vm.updateFlag("transfers", it) }
        FlagRow("Monitoring", ui.monitoringFlag) { vm.updateFlag("monitoring", it) }
        FlagRow("Analytics", ui.analyticsFlag) { vm.updateFlag("analytics", it) }
        FlagRow("Location", ui.locationFlag) { vm.updateFlag("location", it) }
        FlagRow("Offline", ui.offlineFlag) { vm.updateFlag("offline", it) }

        Divider()
        Text("Data Seeding", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.seedAll() }) { Text("Seed All Profiles") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { vm.seedRole(UserType.GENERAL) }) { Text("Seed General") }
            TextButton(onClick = { vm.seedRole(UserType.FARMER) }) { Text("Seed Farmer") }
            TextButton(onClick = { vm.seedRole(UserType.ENTHUSIAST) }) { Text("Seed Enthusiast") }
        }

        Divider()
        Text("Network Simulation", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { vm.goOffline() }) { Text("Go Offline") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { vm.simulate2G() }) { Text("2G") }
            TextButton(onClick = { vm.simulate3G() }) { Text("3G") }
            TextButton(onClick = { vm.simulate4G() }) { Text("4G") }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { vm.clearNetwork() }) { Text("Clear") }
            }
        }

        Divider()
        Text("Location Preview (AP Route)", style = MaterialTheme.typography.titleMedium)
        APRouteMap(vm)

        Divider()
        Text("Guided Tour", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!ui.tourActive) Button(onClick = { vm.startTour() }) { Text("Start Tour") }
            if (ui.tourActive) {
                TextButton(onClick = { vm.tourPrev() }) { Text("Prev") }
                TextButton(onClick = { vm.tourNext() }) { Text("Next") }
                TextButton(onClick = { vm.stopTour() }) { Text("Stop") }
            }
        }

        Divider()
        Text("Offline Testing", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.runSyncNow() }) { Text("Run Sync Now") }
            TextButton(onClick = { vm.runOutgoingNow(false) }) { Text("Run Outgoing (no net)") }
            TextButton(onClick = { vm.runOutgoingNow(true) }) { Text("Run Outgoing (net)") }
        }

        Divider()
        Text("Feedback", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { vm.sendFeedback("BUG", "Demo bug report") }) { Text("Report Bug") }
            TextButton(onClick = { vm.sendFeedback("SUGGESTION", "Demo suggestion") }) { Text("Suggest") }
            TextButton(onClick = { vm.sendFeedback("PERFORMANCE", "Demo perf note") }) { Text("Perf Note") }
        }
        ui.lastFeedbackMessage?.let { Text("Last: $it") }

        Spacer(Modifier.height(24.dp))
        Button(onClick = { vm.resetSession() }) { Text("Reset Demo Session") }
    }
}

@Composable
private fun FlagRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Switch(checked = value, onCheckedChange = onChange)
    }
}

@Composable
private fun APRouteMap(vm: DemoToolsViewModel) {
    // Collect the route as it emits; keep a growing list of visited points
    val current by vm.routeFlow.collectAsState(initial = com.rio.rostry.demo.LocationSimulation.Coordinate(16.5062, 80.6480))
    var points by remember { mutableStateOf(listOf<LatLng>()) }
    LaunchedEffect(current) {
        points = points + LatLng(current.lat, current.lng)
    }
    val start = points.firstOrNull() ?: LatLng(current.lat, current.lng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 7f)
    }
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        GoogleMap(cameraPositionState = cameraPositionState) {
            if (points.isNotEmpty()) {
                Polyline(points = points)
                Marker(state = rememberMarkerState(position = points.last()))
            }
        }
    }
}
