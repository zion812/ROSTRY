package com.rio.rostry.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    viewModel: UserManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentVerificationFilter = uiState.verificationFilter,
            currentSuspendedOnly = uiState.showSuspendedOnly,
            onApply = { verification, suspendedOnly ->
                viewModel.onVerificationFilterChanged(verification)
                viewModel.onSuspensionFilterChanged(suspendedOnly)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
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
            // Search and Filter Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search by name, email, phone...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Role Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.roleFilter == null,
                        onClick = { viewModel.onRoleFilterChanged(null) },
                        label = { Text("All Roles") }
                    )
                    UserType.values().slice(0..2).forEach { type -> // General, Farmer, Enthusiast
                        FilterChip(
                            selected = uiState.roleFilter == type,
                            onClick = { viewModel.onRoleFilterChanged(if (uiState.roleFilter == type) null else type) },
                            label = { Text(type.displayName) }
                        )
                    }
                }
                
                // Active Filters Display
                if (uiState.verificationFilter != null || uiState.showSuspendedOnly) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (uiState.verificationFilter != null) {
                            AssistChip(
                                onClick = { viewModel.onVerificationFilterChanged(null) },
                                label = { Text("Status: ${uiState.verificationFilter?.name}") },
                                trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp)) }
                            )
                        }
                        if (uiState.showSuspendedOnly) {
                            AssistChip(
                                onClick = { viewModel.onSuspensionFilterChanged(false) },
                                label = { Text("Suspended Only") },
                                trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp)) },
                                colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.error)
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.filteredUsers, key = { it.userId }) { user ->
                        UserListItem(
                            user = user,
                            onUpdateRole = { newRole -> viewModel.updateUserRole(user.userId, newRole) },
                            onDeleteUser = { viewModel.deleteUser(user.userId) },
                            onSuspendUser = { reason -> viewModel.suspendUser(user.userId, reason) },
                            onUnsuspendUser = { viewModel.unsuspendUser(user.userId) },
                            onImpersonateUser = { viewModel.impersonateUser(user.userId) }
                        )
                        HorizontalDivider()
                    }
                    if (uiState.filteredUsers.isEmpty() && !uiState.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No users found matching filters", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentVerificationFilter: com.rio.rostry.domain.model.VerificationStatus?,
    currentSuspendedOnly: Boolean,
    onApply: (com.rio.rostry.domain.model.VerificationStatus?, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentVerificationFilter) }
    var suspendedOnly by remember { mutableStateOf(currentSuspendedOnly) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Filter Users", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Verification Status", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    label = { Text("Any") }
                )
                com.rio.rostry.domain.model.VerificationStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        label = { Text(status.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show Suspended Only", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = suspendedOnly,
                    onCheckedChange = { suspendedOnly = it }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { onApply(selectedStatus, suspendedOnly) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Filters")
            }
        }
    }
}

@Composable
fun UserListItem(
    user: UserEntity,
    onUpdateRole: (UserType) -> Unit,
    onDeleteUser: () -> Unit,
    onSuspendUser: (String) -> Unit,
    onUnsuspendUser: () -> Unit,
    onImpersonateUser: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showSuspendDialog by remember { mutableStateOf(false) }
    var suspendReason by remember { mutableStateOf("") }

    ListItem(
        headlineContent = { 
            Text(
                user.fullName ?: user.phoneNumber ?: "Unknown User",
                fontWeight = FontWeight.Bold
            ) 
        },
        supportingContent = {
            Column {
                if (!user.email.isNullOrBlank()) {
                    Text(user.email, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "Role: ${user.userType}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (user.isSuspended) {
                    Text(
                        "SUSPENDED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        leadingContent = {
            if (!user.profilePictureUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (user.fullName?.take(1) ?: user.phoneNumber?.take(1) ?: "?").uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Change Role") },
                        onClick = {
                            showMenu = false
                            showRoleDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete User", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                    Divider()
                    if (user.isSuspended) {
                        DropdownMenuItem(
                            text = { Text("Unsuspend") },
                            onClick = {
                                showMenu = false
                                onUnsuspendUser()
                            },
                             leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text("Suspend User", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                showSuspendDialog = true
                            },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Impersonate") },
                        onClick = {
                             showMenu = false
                             onImpersonateUser()
                        },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                }
            }
        }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete User?") },
            text = { Text("Are you sure you want to delete ${user.fullName ?: "this user"}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Change User Role") },
            text = {
                Column {
                    UserType.values().forEach { type ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateRole(type)
                                    showRoleDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = user.role == type,
                                onClick = null 
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(type.displayName)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
