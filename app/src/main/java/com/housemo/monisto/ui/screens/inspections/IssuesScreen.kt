package com.housemo.monisto.ui.screens.inspections

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
import androidx.navigation.NavController
import com.housemo.monisto.data.local.entity.Issue
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.SeverityCritical

@Composable
fun IssuesScreen(
    navController: NavController,
    viewModel: InspectionViewModel = koinViewModel()
) {
    val issues by viewModel.issues.collectAsState()
    val currentInspection by viewModel.currentInspection.collectAsState()
    var deleteIssue by remember { mutableStateOf<Issue?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (issues.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.CheckCircle, "No issues found", "Great! No issues recorded for this inspection")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    val openCount = issues.count { !it.isResolved }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(50), color = SeverityCritical.copy(alpha = 0.12f)) {
                            Text("$openCount Open", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = SeverityCritical, fontWeight = FontWeight.Bold)
                        }
                        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceVariant) {
                            Text("${issues.size - openCount} Resolved", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                items(issues, key = { it.id }) { issue ->
                    IssueCard(
                        issue = issue,
                        onClick = { navController.navigate(Screen.IssueDetails.createRoute(issue.id)) },
                        onResolve = { viewModel.resolveIssue(issue) },
                        onDelete = { deleteIssue = issue }
                    )
                }
            }
        }
        currentInspection?.id?.let { inspId ->
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.AddIssue.createRoute(inspId)) },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Add Issue") },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
        }
    }

    deleteIssue?.let { i ->
        DeleteConfirmDialog("Delete Issue", "Delete ${i.type} issue?",
            onConfirm = { viewModel.deleteIssue(i); deleteIssue = null },
            onDismiss = { deleteIssue = null })
    }
}

@Composable
fun IssueCard(issue: Issue, onClick: () -> Unit, onResolve: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val severityColor = when (issue.severity) {
        "Critical" -> com.housemo.monisto.ui.theme.SeverityCritical
        "High" -> com.housemo.monisto.ui.theme.SeverityHigh
        "Medium" -> com.housemo.monisto.ui.theme.SeverityMedium
        else -> com.housemo.monisto.ui.theme.SeverityLow
    }
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = if (issue.isResolved) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(severityColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ReportProblem, null, tint = severityColor, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(issue.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(issue.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SeverityBadge(issue.severity)
                    Box {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, null) }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            if (!issue.isResolved) {
                                DropdownMenuItem(text = { Text("Mark Resolved") }, onClick = { showMenu = false; onResolve() }, leadingIcon = { Icon(Icons.Default.CheckCircle, null) })
                            }
                            DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                        }
                    }
                }
            }
            if (issue.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(issue.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
            if (issue.isResolved) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = com.housemo.monisto.ui.theme.ConditionGood, modifier = Modifier.size(14.dp))
                    Text("Resolved", style = MaterialTheme.typography.labelSmall, color = com.housemo.monisto.ui.theme.ConditionGood)
                }
            }
        }
    }
}
