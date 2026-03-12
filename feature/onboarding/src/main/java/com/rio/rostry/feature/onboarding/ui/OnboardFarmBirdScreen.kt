package com.rio.rostry.feature.onboarding.ui
import com.rio.rostry.domain.monitoring.repository.ShowRecordRepository
import com.rio.rostry.domain.error.ErrorHandler

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Temporary stub to keep the module compiling during modularization.
 * TODO(modularization): restore full onboarding UI and wire dependencies via domain contracts.
 */
@Composable
fun OnboardFarmBirdScreen(
    onNavigateRoute: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    Text("OnboardFarmBirdScreen (stub)")
}
