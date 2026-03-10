package com.rio.rostry.ui.farmer.calendar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.rio.rostry.data.database.entity.FarmEventType

@Composable
fun getEventTypeIcon(type: FarmEventType): ImageVector {
    return when (type) {
        FarmEventType.VACCINATION -> Icons.Default.MedicalServices
        FarmEventType.DEWORMING -> Icons.Default.Medication
        FarmEventType.BIOSECURITY -> Icons.Default.Security
        FarmEventType.FEEDING -> Icons.Default.Restaurant
        FarmEventType.WEIGHING -> Icons.Default.Scale // Or MonitorWeight if available, fallback Scale
        FarmEventType.CLEANING -> Icons.Default.CleaningServices
        FarmEventType.OTHER -> Icons.Default.Event
    }
}
