package com.housemo.monisto.ui.screens.buildings

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
import com.housemo.monisto.data.local.entity.Building
import com.housemo.monisto.ui.components.*
import com.housemo.monisto.ui.navigation.Screen
import com.housemo.monisto.ui.theme.Blue40

@Composable
fun BuildingsScreen(
    navController: NavController,
    viewModel: BuildingViewModel = koinViewModel()
) {
    val buildings by viewModel.buildings.collectAsState()
    var deleteBuilding by remember { mutableStateOf<Building?>(null) }

    Box(Modifier.fillMaxSize()) {
        if (buildings.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Default.Business,
                    title = "No buildings yet",
                    subtitle = "Tap the button below to add your first building"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${buildings.size} building${if (buildings.size != 1) "s" else ""} monitored",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(buildings, key = { it.id }) { building ->
                    BuildingCard(
                        building = building,
                        onClick = { navController.navigate(Screen.BuildingOverview.createRoute(building.id)) },
                        onDelete = { deleteBuilding = building }
                    )
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { navController.navigate(Screen.AddBuilding.route) },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Add Building") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    deleteBuilding?.let { b ->
        DeleteConfirmDialog(
            title = "Delete Building",
            message = "Delete \"${b.name}\"? This action cannot be undone.",
            onConfirm = {
                viewModel.deleteBuilding(b)
                deleteBuilding = null
            },
            onDismiss = { deleteBuilding = null }
        )
    }
}

@Composable
fun BuildingCard(
    building: Building,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Blue40.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Home, null, tint = Blue40, modifier = Modifier.size(30.dp))
                    }
                    Column {
                        Text(building.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (building.address.isNotEmpty()) {
                            Text(building.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() }, leadingIcon = { Icon(Icons.Default.Delete, null) })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${building.floorsCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Blue40)
                    Text("Floors", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (building.yearBuilt > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${building.yearBuilt}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text("Year Built", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                    ConditionScoreIndicator(building.conditionScore)
                }
            }
        }
    }
}
