package com.housemo.monisto.ui.screens.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.housemo.monisto.data.local.entity.Task
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.screens.repairs.RepairViewModel
import com.housemo.monisto.ui.theme.StatusDone
import com.housemo.monisto.util.DateUtils
import androidx.compose.material3.ExperimentalMaterial3Api
import org.koin.compose.koinInject

@Composable
fun TasksScreen(
    navController: NavController,
    viewModel: RepairViewModel = koinViewModel()
) {
    val prefs: PreferencesManager = koinInject()
    val currency by prefs.defaultCurrency.collectAsState(initial = "USD")
    val tasks by viewModel.tasks.collectAsState()
    val currentRepair by viewModel.currentRepair.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTask by remember { mutableStateOf<Task?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            currentRepair?.let { repair ->
                ElevatedCard(Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(repair.type, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (repair.contractor.isNotEmpty()) Text(repair.contractor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusBadge(repair.status)
                            PriorityBadge(repair.priority)
                            if (repair.cost > 0) Text("$currency ${String.format("%.0f", repair.cost)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            if (tasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(Icons.Default.CheckBox, "No tasks yet", "Break down the repair into manageable tasks")
                }
            } else {
                val done = tasks.count { it.status == "Done" }
                LinearProgressIndicator(
                    progress = { if (tasks.isEmpty()) 0f else done.toFloat() / tasks.size },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(6.dp),
                    color = StatusDone
                )
                Text("$done / ${tasks.size} tasks completed", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                    LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { deleteTask = task }
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Add Task") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddTaskDialog(
            onAdd = { desc, dueDate -> viewModel.addTask(desc, dueDate) },
            onDismiss = { showAddDialog = false }
        )
    }

    deleteTask?.let { t ->
        DeleteConfirmDialog("Delete Task", "Delete this task?",
            onConfirm = { viewModel.deleteTask(t); deleteTask = null },
            onDismiss = { deleteTask = null })
    }
}

@Composable
fun TaskCard(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    val isDone = task.status == "Done"
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Checkbox(checked = isDone, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isDone) FontWeight.Normal else FontWeight.Medium,
                    color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (task.dueDate != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val overdue = DateUtils.isOverdue(task.dueDate) && !isDone
                        Icon(Icons.Default.Schedule, null, modifier = Modifier.size(12.dp), tint = if (overdue) com.housemo.monisto.ui.theme.SeverityCritical else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(DateUtils.formatDate(task.dueDate), style = MaterialTheme.typography.labelSmall, color = if (overdue) com.housemo.monisto.ui.theme.SeverityCritical else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onAdd: (String, Long?) -> Unit, onDismiss: () -> Unit) {
    var description by remember { mutableStateOf("") }
    var dueDateMillis by remember { mutableStateOf<Long?>(null) }
    var descError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; descError = false },
                    label = { Text("Task Description *") },
                    isError = descError,
                    supportingText = if (descError) {{ Text("Required") }} else null,
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                DatePickerField(
                    label = "Due Date (optional)",
                    selectedDateMillis = dueDateMillis,
                    onDateSelected = { dueDateMillis = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (description.isBlank()) { descError = true; return@Button }
                onAdd(description.trim(), dueDateMillis)
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
