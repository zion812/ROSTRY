package com.rio.rostry.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.LocalSpacing

@Composable
fun HomeEnthusiastScreen(onProfile: () -> Unit) {
    val sp = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, Enthusiast", style = MaterialTheme.typography.titleLarge)
        Button(onClick = onProfile, modifier = Modifier) {
            Text("Profile")
        }
    }
}
