package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rio.rostry.ui.theme.Dimens

@Composable
fun TypographyShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_small)) {
        Text("Typography", style = MaterialTheme.typography.titleLarge)
        Text("Display Large - Farm Insights", style = MaterialTheme.typography.displayLarge)
        Text("Display Medium - Market Deals", style = MaterialTheme.typography.displayMedium)
        Text("Display Small - Today", style = MaterialTheme.typography.displaySmall)
        Text("Headline Large - Premium Broilers", style = MaterialTheme.typography.headlineLarge)
        Text("Headline Medium - Price Trends", style = MaterialTheme.typography.headlineMedium)
        Text("Headline Small - Vaccination Alerts", style = MaterialTheme.typography.headlineSmall)
        Text("Title Large - Product Title", style = MaterialTheme.typography.titleLarge)
        Text("Title Medium - Section Title", style = MaterialTheme.typography.titleMedium)
        Text("Title Small - Caption Title", style = MaterialTheme.typography.titleSmall)
        Text("Body Large - Description text for items and posts.", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium - Supporting information.", style = MaterialTheme.typography.bodyMedium)
        Text("Body Small - Metadata and notes.", style = MaterialTheme.typography.bodySmall)
        Text("Label Large - BUTTON", style = MaterialTheme.typography.labelLarge)
        Text("Label Medium - Chip", style = MaterialTheme.typography.labelMedium)
        Text("Label Small - Caption", style = MaterialTheme.typography.labelSmall)
    }
}
