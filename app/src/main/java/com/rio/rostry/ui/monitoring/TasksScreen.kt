package com.rio.rostry.ui.monitoring

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.components.SyncStatusBadge
import com.rio.rostry.ui.components.SyncState
import com.rio.rostry.ui.monitoring.vm.TaskTab
import com.rio.rostry.ui.monitoring.vm.TasksViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    val vm: TasksViewModel = hiltViewModel()
    val state by vm.uiState.collectAsState()
    val selectedIndex = when (state.selectedTab) {
        TaskTab.DUE -> 0
        TaskTab.OVERDUE -> 1
        TaskTab.COMPLETED -> 2
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabRow(selectedTabIndex = selectedIndex) {
                Tab(selected = selectedIndex == 0, onClick = { vm.selectTab(TaskTab.DUE) }, text = { Text("Due") })
                Tab(selected = selectedIndex == 1, onClick = { vm.selectTab(TaskTab.OVERDUE) }, text = { Text("Overdue") })
                Tab(selected = selectedIndex == 2, onClick = { vm.selectTab(TaskTab.COMPLETED) }, text = { Text("Completed") })
            }
            val list = when (state.selectedTab) {
                TaskTab.DUE -> state.dueTasks
                TaskTab.OVERDUE -> state.overdueTasks
                TaskTab.COMPLETED -> state.completedTasks
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list) { task ->
                    AnimatedVisibility(visible = true, exit = fadeOut()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .let { base ->
                                    val pid = task.productId
                                    if (!pid.isNullOrBlank()) base.then(Modifier.clickable { onNavigateToProduct(pid) }) else base
                                }
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(task.title)
                                task.description?.let { Text(it) }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (state.selectedTab != TaskTab.COMPLETED) {
                                    Checkbox(checked = false, onCheckedChange = { vm.markComplete(task.taskId) })
                                } else {
                                    Text("Done")
                                }
                                val syncState = state.taskSyncStates[task.taskId] ?: SyncState.SYNCED
                                SyncStatusBadge(syncState = syncState, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
