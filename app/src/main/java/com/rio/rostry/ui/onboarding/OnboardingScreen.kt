package com.rio.rostry.ui.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.domain.model.UserType

@Composable
fun OnboardingScreen(
    role: UserType?,
    onComplete: () -> Unit
) {
    val vm: OnboardingViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    Crossfade(state.step, label = "onboarding") { step ->
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Welcome to ROSTRY", style = MaterialTheme.typography.headlineSmall)
            when (step) {
                0 -> Text("Personalized setup for ${role?.name ?: "User"}")
                1 -> Text("Tell us your goals")
                2 -> Text("Your experience level")
                3 -> Text("Pick your interests")
                4 -> Text("All set! Let's go")
                else -> Text("Loading...")
            }
            if (state.error != null) {
                Text("Error: ${state.error}")
            }
            if (state.completed) {
                Button(onClick = onComplete) { Text("Continue") }
            } else {
                Button(onClick = { vm.nextStep() }) { Text("Next") }
                if (state.canSkip) Button(onClick = { vm.skip() }) { Text("Skip") }
            }
        }
    }
}
