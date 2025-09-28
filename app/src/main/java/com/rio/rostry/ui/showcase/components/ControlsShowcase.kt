package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens

@Composable
fun ControlsShowcase() {
    var checked by remember { mutableStateOf(true) }
    var radio by remember { mutableStateOf(0) }
    var slider by remember { mutableStateOf(0.5f) }
    var switchOn by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Controls", style = MaterialTheme.typography.titleLarge)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Checkbox")
            Checkbox(checked = checked, onCheckedChange = { checked = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Radio")
            RadioButton(selected = radio == 0, onClick = { radio = 0 })
            RadioButton(selected = radio == 1, onClick = { radio = 1 })
        }

        Column {
            Text("Slider: %")
            Slider(value = slider, onValueChange = { slider = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Switch")
            Switch(checked = switchOn, onCheckedChange = { switchOn = it })
        }
    }
}
