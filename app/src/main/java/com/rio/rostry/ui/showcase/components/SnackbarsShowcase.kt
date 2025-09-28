package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun SnackbarsShowcase() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { _ ->
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
            Text("Snackbars", style = MaterialTheme.typography.titleLarge)
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { scope.launch { snackbarHostState.showSnackbar("Hello from snackbar!") } }
            ) { Text("Show snackbar") }
        }
    }
}
