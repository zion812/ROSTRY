package com.rio.rostry.ui.admin.roles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleManagementScreen(
    viewModel: RoleManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showRoleDialog by remember { mutableStateOf(false) }
    var showSuspendDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }
    var selectedNewRole by remember { mutableStateOf<UserType?>(null) }
    var suspendReason by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Role Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search users...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Role Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedRole == null,
                    onClick = { viewModel.onRoleFilterChanged(null) },
                    label = { Text("All") }
                )
                listOf(UserType.GENERAL, UserType.FARMER, UserType.ENTHUSIAST).forEach { role ->
                    FilterChip(
                        selected = state.selectedRole == role,
                        onClick = { viewModel.onRoleFilterChanged(role) },
                        label = { Text(role.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${state.filteredUsers.size} users",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Suspended: ${state.users.count { it.isSuspended }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User List
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                    }
                }
                state.filteredUsers.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PersonOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No users found")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.filteredUsers) { user ->
                            UserRoleCard(
                                user = user,
                                isProcessing = state.isProcessing,
                                onChangeRole = {
                                    selectedUser = user
                                    selectedNewRole = user.role
                                    showRoleDialog = true
                                },
                                onToggleSuspend = { 
                                    selectedUser = user
                                    if (user.isSuspended) {
                                        viewModel.unsuspendUser(user.userId)
                                    } else {
                                        showSuspendDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Role Change Dialog
    if (showRoleDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Change Role") },
            text = {
                Column {
                    Text("Select new role for ${selectedUser?.fullName ?: "User"}:")
                    Spacer(modifier = Modifier.height(16.dp))
                    listOf(UserType.GENERAL, UserType.FARMER, UserType.ENTHUSIAST).forEach { role ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedNewRole == role,
                                onClick = { selectedNewRole = role }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(role.displayName)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedNewRole?.let { role ->
                            selectedUser?.let { user ->
                                viewModel.updateUserRole(user.userId, role)
                            }
                        }
                        showRoleDialog = false
                        selectedNewRole = null
                    },
                    enabled = selectedNewRole != null
                ) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showRoleDialog = false
                    selectedNewRole = null
                }) { Text("Cancel") }
            }
        )
    }

    // Suspend Dialog
    if (showSuspendDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showSuspendDialog = false },
            title = { Text("Suspend User") },
            text = {
                Column {
                    Text("Suspend ${selectedUser?.fullName ?: "User"}? This will prevent them from accessing the app.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = suspendReason,
                        onValueChange = { suspendReason = it },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUser?.let { user ->
                            viewModel.suspendUser(user.userId, suspendReason)
                        }
                        showSuspendDialog = false
                        suspendReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    enabled = suspendReason.isNotBlank()
                ) { Text("Suspend") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showSuspendDialog = false
                    suspendReason = ""
                }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun UserRoleCard(
    user: UserEntity,
    isProcessing: Boolean,
    onChangeRole: () -> Unit,
    onToggleSuspend: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (user.isSuspended) 
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
        else CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        user.fullName ?: "User",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (user.isSuspended) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.error,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                "SUSPENDED",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }
                
                Text(
                    user.email ?: user.phoneNumber ?: user.userId.take(12) + "...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                RoleBadge(role = user.role)
            }

            // Actions
            Column(horizontalAlignment = Alignment.End) {
                TextButton(
                    onClick = onChangeRole,
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Change Role")
                }
                
                TextButton(
                    onClick = onToggleSuspend,
                    enabled = !isProcessing,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (user.isSuspended) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        if (user.isSuspended) Icons.Default.CheckCircle else Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (user.isSuspended) "Unsuspend" else "Suspend")
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: UserType) {
    val (color, icon) = when (role) {
        UserType.GENERAL -> Color(0xFF9E9E9E) to Icons.Default.Person
        UserType.FARMER -> Color(0xFF4A8C2A) to Icons.Default.Agriculture
        UserType.ENTHUSIAST -> Color(0xFF673AB7) to Icons.Default.Star
        else -> Color(0xFF9E9E9E) to Icons.Default.Person
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
            Spacer(modifier = Modifier.width(4.dp))
            Text(role.displayName, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}
