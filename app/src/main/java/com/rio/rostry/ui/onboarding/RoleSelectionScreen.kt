package com.rio.rostry.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
fun RoleSelectionScreen(
    onContinue: (UserType) -> Unit
) {
    val vm: OnboardingViewModel = hiltViewModel()
    val ui by vm.ui.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Choose your role", style = MaterialTheme.typography.headlineSmall)
        Text("You can upgrade later. Each role unlocks a different set of features.")

        RoleSelectionCard(
            userType = UserType.GENERAL,
            selected = ui.selectedUserType == UserType.GENERAL,
            onClick = { vm.selectUserType(UserType.GENERAL) }
        )
        RoleSelectionCard(
            userType = UserType.FARMER,
            selected = ui.selectedUserType == UserType.FARMER,
            onClick = { vm.selectUserType(UserType.FARMER) }
        )
        RoleSelectionCard(
            userType = UserType.ENTHUSIAST,
            selected = ui.selectedUserType == UserType.ENTHUSIAST,
            onClick = { vm.selectUserType(UserType.ENTHUSIAST) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { ui.selectedUserType?.let { onContinue(it) } },
            enabled = ui.selectedUserType != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            val roleName = ui.selectedUserType?.displayName ?: "Role"
            Text("Continue with $roleName")
        }
    }
}
