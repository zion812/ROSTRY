package com.rio.rostry.ui.admin.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.admin.theme.AdminColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardLayout(
    title: String,
    onMenuClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = AdminColors.OnSurface) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu, 
                            contentDescription = "Menu",
                            tint = AdminColors.OnSurface
                        )
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminColors.TopBarBackground
                )
            )
        },
        containerColor = AdminColors.Background
    ) { padding ->
        content(padding)
    }
}
