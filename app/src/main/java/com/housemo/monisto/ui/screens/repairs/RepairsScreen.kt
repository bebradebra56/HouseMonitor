package com.housemo.monisto.ui.screens.repairs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.navigation.NavController
import com.housemo.monisto.data.local.entity.Repair
import com.housemo.monisto.data.prefs.PreferencesManager
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.*
import com.housemo.monisto.util.DateUtils

@Composable
fun RepairsScreen(
    navController: NavController,
    viewModel: RepairViewModel = koinViewModel()
) {
    val prefs: PreferencesManager = koinInject()
    val currency by prefs.defaultCurrency.collectAsState(initial = "USD")
    val repairs by viewModel.allRepairs.collectAsState()
    var deleteRepair by remember { mutableStateOf<Repair?>(null) }

    val issueId = remember {
        navController.currentBackStackEntry?.arguments?.getLong("issueId") ?: -1L
    }

    Box(Modifier.fillMaxSize()) {
        if (repairs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Build, "No repairs yet", "Plan and track your building repairs")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val pending = repairs.filter { it.status != "Done" }
                val done = repairs.filter { it.status == "Done" }
                if (pending.isNotEmpty()) {
                    item { SectionHeader("Active (${pending.size})") }
                    items(pending, key = { it.id }) { repair ->
                        RepairCard(
                            repair = repair,
                            currency = currency,
                            onClick = { navController.navigate(Screen.Tasks.createRoute(repair.id)) },
                            onComplete = { viewModel.completeRepair(repair) },
                            onDelete = { deleteRepair = repair }
                        )
                    }
                }
                if (done.isNotEmpty()) {
                    item { SectionHeader("Completed (${done.size})") }
                    items(done, key = { it.id }) { repair ->
                        RepairCard(
                            repair = repair,
                            currency = currency,
                            onClick = { navController.navigate(Screen.Tasks.createRoute(repair.id)) },
                            onComplete = {},
                            onDelete = { deleteRepair = repair }
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { navController.navigate(Screen.AddRepair.createRoute(issueId)) },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Add Repair") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    deleteRepair?.let { r ->
        DeleteConfirmDialog("Delete Repair", "Delete \"${r.type}\" repair?",
            onConfirm = { viewModel.deleteRepair(r); deleteRepair = null },
            onDismiss = { deleteRepair = null })
    }
}

@Composable
fun RepairCard(repair: Repair, currency: String = "USD", onClick: () -> Unit, onComplete: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val statusColor = when (repair.status) {
        "Done" -> StatusDone
        "InProgress" -> StatusInProgress
        else -> StatusPending
    }
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(statusColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Build, null, tint = statusColor, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(repair.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        if (repair.contractor.isNotEmpty()) Text(repair.contractor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Box {
                    IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, null) }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        if (repair.status != "Done") {
                            DropdownMenuItem(text = { Text("Mark Complete") }, onClick = { showMenu = false; onComplete() }, leadingIcon = { Icon(Icons.Default.CheckCircle, null) })
                        }
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(repair.status)
                PriorityBadge(repair.priority)
                if (repair.cost > 0) {
                    Text("$currency ${String.format("%.0f", repair.cost)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (repair.scheduledDate != null) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val overdue = DateUtils.isOverdue(repair.scheduledDate) && repair.status != "Done"
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(14.dp), tint = if (overdue) SeverityCritical else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        DateUtils.formatDate(repair.scheduledDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (overdue) SeverityCritical else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (overdue) Text("(Overdue)", style = MaterialTheme.typography.labelSmall, color = SeverityCritical, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
