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
import com.housemo.monisto.data.local.entity.Inspection
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.Teal40
import com.housemo.monisto.util.DateUtils

@Composable
fun InspectionsScreen(
    navController: NavController,
    viewModel: InspectionViewModel = koinViewModel()
) {
    val inspections by viewModel.inspections.collectAsState()
    var deleteInspection by remember { mutableStateOf<Inspection?>(null) }

    val buildingIdArg = remember {
        navController.currentBackStackEntry?.arguments?.getLong("buildingId") ?: -1L
    }

    Box(Modifier.fillMaxSize()) {
        if (inspections.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.AssignmentTurnedIn, "No inspections yet", "Record your first building inspection")
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("${inspections.size} inspection${if (inspections.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(inspections, key = { it.id }) { inspection ->
                    InspectionCard(
                        inspection = inspection,
                        onClick = { navController.navigate(Screen.Issues.createRoute(inspection.id)) },
                        onDelete = { deleteInspection = inspection }
                    )
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { navController.navigate(Screen.AddInspection.createRoute(buildingIdArg)) },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Add Inspection") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    deleteInspection?.let { i ->
        DeleteConfirmDialog("Delete Inspection", "Delete inspection by ${i.inspector}?",
            onConfirm = { viewModel.deleteInspection(i); deleteInspection = null },
            onDismiss = { deleteInspection = null })
    }
}

@Composable
fun InspectionCard(inspection: Inspection, onClick: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Teal40.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, null, tint = Teal40, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(inspection.inspector, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(DateUtils.formatDate(inspection.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ConditionBadge(inspection.overallCondition)
                    Box {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, null) }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                        }
                    }
                }
            }
            if (inspection.notes.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(inspection.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
        }
    }
}
