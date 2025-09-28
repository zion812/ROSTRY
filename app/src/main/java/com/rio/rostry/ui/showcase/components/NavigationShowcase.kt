package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationShowcase() {
    var selected by remember { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Navigation", style = MaterialTheme.typography.titleLarge)
        NavigationBar {
            NavigationBarItem(selected = selected == 0, onClick = { selected = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Market") })
            NavigationBarItem(selected = selected == 1, onClick = { selected = 1 }, icon = { Icon(Icons.Default.ShoppingCart, null) }, label = { Text("Cart") })
            NavigationBarItem(selected = selected == 2, onClick = { selected = 2 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
        }
        TopAppBar(title = { Text("Top App Bar") })
    }
}
