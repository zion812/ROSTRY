package com.rio.rostry.ui.showcase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.ui.showcase.ComponentGalleryViewModel.GalleryRole
import com.rio.rostry.ui.showcase.components.*
import com.rio.rostry.ui.theme.Dimens
import com.rio.rostry.ui.theme.ROSTRYTheme

@Composable
fun ComponentGalleryScreen(vm: ComponentGalleryViewModel = viewModel()) {
    val role by vm.selectedRole.collectAsState()
    val dark by vm.darkMode.collectAsState()
    val query by vm.query.collectAsState()
    val sideBySide by vm.sideBySide.collectAsState()

    val mappedUserType = when (role) {
        GalleryRole.FARMER -> UserType.FARMER
        GalleryRole.ENTHUSIAST -> UserType.ENTHUSIAST
        GalleryRole.GENERAL -> UserType.GENERAL
    }

    ROSTRYTheme(userRole = if (sideBySide) null else mappedUserType, darkTheme = dark, dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(Dimens.screen_padding_horizontal)) {
                TopControls(role, dark, query, sideBySide, onRole = vm::setRole, onDark = vm::toggleDarkMode, onQuery = vm::setQuery, onSideBySide = vm::setSideBySide)
                Spacer(Modifier.height(Dimens.section_spacing))
                if (sideBySide) SideBySideThemes(dark, query) else GalleryList(query)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TopControls(
    role: GalleryRole,
    dark: Boolean,
    query: String,
    sideBySide: Boolean,
    onRole: (GalleryRole) -> Unit,
    onDark: (Boolean) -> Unit,
    onQuery: (String) -> Unit,
    onSideBySide: (Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_medium), modifier = Modifier.fillMaxWidth()) {
        // Wrap chips and toggles across lines on small widths
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.space_large),
            verticalArrangement = Arrangement.spacedBy(Dimens.space_medium)
        ) {
            // Role selector chips
            SegmentedButtonRow(role, onRole)

            // Dark toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark")
                Switch(checked = dark, onCheckedChange = onDark)
            }

            // Compare toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Compare")
                Switch(checked = sideBySide, onCheckedChange = onSideBySide)
            }
        }

        // Keep search on its own row so it can expand to full width on phones
        OutlinedTextField(
            value = query,
            onValueChange = onQuery,
            label = { Text("Search components") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SegmentedButtonRow(role: GalleryRole, onRole: (GalleryRole) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = role == GalleryRole.GENERAL, onClick = { onRole(GalleryRole.GENERAL) }, label = { Text("General") })
        FilterChip(selected = role == GalleryRole.FARMER, onClick = { onRole(GalleryRole.FARMER) }, label = { Text("Farmer") })
        FilterChip(selected = role == GalleryRole.ENTHUSIAST, onClick = { onRole(GalleryRole.ENTHUSIAST) }, label = { Text("Enthusiast") })
    }
}

@Composable
private fun GalleryList(query: String) {
    val sections = listOf(
        "Typography" to @Composable { TypographyShowcase() },
        "Colors" to @Composable { ColorPaletteShowcase() },
        "Buttons" to @Composable { ButtonShowcase() },
        "Cards" to @Composable { CardShowcase() },
        "Text Fields" to @Composable { TextFieldShowcase() },
        "Navigation" to @Composable { NavigationShowcase() },
        "Chips" to @Composable { ChipsShowcase() },
        "Controls" to @Composable { ControlsShowcase() },
        "Dialogs" to @Composable { DialogsShowcase() },
        "Snackbars" to @Composable { SnackbarsShowcase() },
    )
    val q = query.trim().lowercase()
    val filtered = if (q.isBlank()) sections else sections.filter { (title, _) -> title.lowercase().contains(q) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.section_spacing)) {
        items(filtered.size) { idx ->
            val content = filtered[idx].second
            content()
        }
    }
}

@Composable
private fun SideBySideThemes(dark: Boolean, query: String) {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        ThemeColumn("General", modifier = Modifier.weight(1f)) { ROSTRYTheme(UserType.GENERAL, darkTheme = dark, dynamicColor = false) { GalleryList(query) } }
        ThemeColumn("Farmer", modifier = Modifier.weight(1f)) { ROSTRYTheme(UserType.FARMER, darkTheme = dark, dynamicColor = false) { GalleryList(query) } }
        ThemeColumn("Enthusiast", modifier = Modifier.weight(1f)) { ROSTRYTheme(UserType.ENTHUSIAST, darkTheme = dark, dynamicColor = false) { GalleryList(query) } }
    }
}

@Composable
private fun ThemeColumn(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(Dimens.space_medium))
        content()
    }
}
